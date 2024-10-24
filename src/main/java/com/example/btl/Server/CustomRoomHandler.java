package com.example.btl.Server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomRoomHandler {
    private Connection connection;

    public CustomRoomHandler(Connection connection) {
        this.connection = connection;
    }
    public String getListFriend(String user1){
        List<String> friends = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            // SQL query to find all friends of user1
            String query = "SELECT DISTINCT u.username AS friend FROM ( " +
                    "   SELECT user2 AS friend FROM friend WHERE user1 = ? " +
                    "   UNION " +
                    "   SELECT user1 AS friend FROM friend WHERE user2 = ? " +
                    ") AS all_friends " +
                    "JOIN users u ON u.username = all_friends.friend " +
                    "WHERE u.status = 'online';";

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, user1);
            preparedStatement.setString(2, user1);

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, user1);
            preparedStatement.setString(2, user1);

            // Execute query
            resultSet = preparedStatement.executeQuery();

            // Add each friend to the list
            while (resultSet.next()) {
                friends.add(resultSet.getString("friend"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        String result="";
        for(int i=0; i<friends.size(); i++){
            result=result+friends.get(i)+";";
        }
        try {
            if (friends.size() >= 1) result = result.trim().substring(0,result.length()-1);
            else result = "";
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public void addfriend(String idRoom, String name){
        String updateQuery = "UPDATE customroom SET player2 = ? WHERE idroom = ?";

        try (
             PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            // Thiết lập các tham số cho câu lệnh SQL
            pstmt.setString(1, name);     // Cập nhật player1choice với giá trị name
            pstmt.setString(2, idRoom);   // Điều kiện WHERE idroom = idRoom

            // Thực thi câu lệnh cập nhật
            int affectedRows = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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

