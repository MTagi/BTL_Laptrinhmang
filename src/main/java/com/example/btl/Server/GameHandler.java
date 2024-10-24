package com.example.btl.Server;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import com.example.btl.Match;
import com.example.btl.User;
public class GameHandler {
    private Connection connection;

    public GameHandler(Connection connection) {
        this.connection = connection;
    }

    public Match createMatch(String player1, String player2) {
        try {
            // Bước 1: Tạo idMatch bằng cách đếm số hàng hiện tại trong bảng Match
            int idMatch;
            String countQuery = "SELECT COUNT(*) FROM `match`";

            try (PreparedStatement countStatement = connection.prepareStatement(countQuery);
                 ResultSet resultSet = countStatement.executeQuery()) {
                if (resultSet.next()) {
                    idMatch = resultSet.getInt(1) + 1; // Tăng lên 1 để tạo idMatch mới
                } else {
                    throw new SQLException("Failed to count matches.");
                }
            }

            // Bước 2: Thêm một hàng mới vào bảng Match
            String insertQuery = "INSERT INTO `match` (idMatch, player1, player2, timeBegin) VALUES (?, ?, ?, NOW())";

            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setInt(1, idMatch);
                insertStatement.setString(2, player1);
                insertStatement.setString(3, player2);
                insertStatement.executeUpdate();
            }
            return new Match(idMatch, player1, player2, LocalDateTime.now());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void setSelection(int idMatch, String selection, String username) {
        String getMatchQuery = "SELECT player1, player2 FROM `match` WHERE idMatch = ?";
        String updatePlayer1ChoiceQuery = "UPDATE `match` SET player1Choice = ? WHERE idMatch = ?";
        String updatePlayer2ChoiceQuery = "UPDATE `match` SET player2Choice = ? WHERE idMatch = ?";

        try (PreparedStatement getMatchStmt = connection.prepareStatement(getMatchQuery)) {

            // Bước 1: Lấy thông tin trận đấu từ idMatch
            getMatchStmt.setInt(1, idMatch);
            ResultSet rs = getMatchStmt.executeQuery();

            if (rs.next()) {
                String player1 = rs.getString("player1");
                String player2 = rs.getString("player2");

                // Bước 2: Kiểm tra xem username là player1 hay player2
                if (username.equals(player1)) {
                    // Cập nhật lựa chọn cho player1
                    try (PreparedStatement updateStmt = connection.prepareStatement(updatePlayer1ChoiceQuery)) {
                        updateStmt.setString(1, selection);
                        updateStmt.setInt(2, idMatch);
                        int rowsUpdated = updateStmt.executeUpdate();
                        if (rowsUpdated > 0) {
                            System.out.println("Lựa chọn của player1 đã được cập nhật thành công.");
                        }
                    }
                } else if (username.equals(player2)) {
                    // Cập nhật lựa chọn cho player2
                    try (PreparedStatement updateStmt = connection.prepareStatement(updatePlayer2ChoiceQuery)) {
                        updateStmt.setString(1, selection);
                        updateStmt.setInt(2, idMatch);
                        int rowsUpdated = updateStmt.executeUpdate();
                        if (rowsUpdated > 0) {
                            System.out.println("Lựa chọn của player2 đã được cập nhật thành công.");
                        }
                    }
                } else {
                    System.out.println("Username không thuộc player1 hoặc player2 trong trận đấu này.");
                }
            } else {
                System.out.println("Không tìm thấy trận đấu với idMatch này.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Match getMatchInfo(int idMatch) {
        String query = "SELECT idMatch, player1, player2, player1Choice, player2Choice, timeBegin FROM `match` WHERE idMatch = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idMatch);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Lấy thông tin từ ResultSet
                int matchId = rs.getInt("idMatch");
                String player1 = rs.getString("player1");
                String player2 = rs.getString("player2");
                String player1Choice = rs.getString("player1Choice");
                String player2Choice = rs.getString("player2Choice");
                LocalDateTime timeBegin = rs.getTimestamp("timeBegin").toLocalDateTime();

                // Tạo và trả về đối tượng Match
                return new Match(matchId, player1, player2, player1Choice, player2Choice, timeBegin);
            } else {
                System.out.println("Không tìm thấy trận đấu với idMatch này.");
                return null;  // Trả về null nếu không tìm thấy trận đấu
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean isPlayer1ChoiceNotNull(int roomId) {
        String query = "SELECT player1status FROM customroom WHERE idroom = ? AND player1status IS NOT NULL";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, roomId); // Truyền idroom vào truy vấn
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                String updateQuery1 = "UPDATE customroom SET player1status = NULL WHERE idroom = ?";
                PreparedStatement pstmt2 = connection.prepareStatement(updateQuery1);
                pstmt2.setString(1, String.valueOf(roomId));
                int affectedRows = pstmt2.executeUpdate();
                return true;
            }
            else {
                String updateQuery = "UPDATE customroom SET player1status = ? WHERE idroom = ?";
                PreparedStatement pstmt1 = connection.prepareStatement(updateQuery);
                // Thiết lập các tham số cho câu lệnh SQL
                pstmt1.setString(1, "ready");     // Cập nhật player1choice với giá trị name
                pstmt1.setString(2, String.valueOf(roomId));   // Điều kiện WHERE idroom = idRoom
                // Thực thi câu lệnh cập nhật
                int affectedRows = pstmt1.executeUpdate();
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

}
