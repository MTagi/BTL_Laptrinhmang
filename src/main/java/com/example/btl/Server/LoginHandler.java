package com.example.btl.Server;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.example.btl.User;
public class LoginHandler {
    private Connection connection;

    public LoginHandler(Connection connection) {
        this.connection = connection;
    }

    public User authenticate(String username, String password) {
        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Cập nhật trạng thái user thành online
                String updateStatusQuery = "UPDATE users SET status = 'online' WHERE username = ?";
                PreparedStatement updateStatusStmt = connection.prepareStatement(updateStatusQuery);
                updateStatusStmt.setString(1, username);
                updateStatusStmt.executeUpdate();

                // Tạo đối tượng User từ kết quả query
                return new User(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("gmail"),
                        resultSet.getInt("win"),
                        resultSet.getInt("draw"),
                        resultSet.getInt("loss"),
                        resultSet.getInt("totalPoints"),
                        "online",  // Trạng thái mới cập nhật
                        resultSet.getInt("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean dki(String tkdki, String mkdki, String gmdki){
        PreparedStatement checkStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;

        try {
            // Kiểm tra xem tài khoản đã tồn tại chưa
            String checkQuery = "SELECT * FROM Users WHERE username = ? ";
            checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setString(1, tkdki);
            rs = checkStmt.executeQuery();

            if (rs.next()) {
                return false;
            } else {
                // Thêm tài khoản mới nếu không tồn tại
                String insertQuery = "INSERT INTO Users (username, password, gmail, win, draw, loss, totalPoints, status, role) "
                        + "VALUES (?, ?, ?, 0, 0, 0, 0, 'offline', 0)";
                insertStmt = connection.prepareStatement(insertQuery);
                insertStmt.setString(1, tkdki);
                insertStmt.setString(2, mkdki);
                insertStmt.setString(3, gmdki);

                int rowsInserted = insertStmt.executeUpdate();
                if (rowsInserted > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void updateStatus(String username, String status) {
        try {
            String updateQuery = "UPDATE users SET status = ? WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(updateQuery);
            statement.setString(1, status);
            statement.setString(2, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
