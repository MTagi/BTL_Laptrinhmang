package com.example.btl.Server;

import com.example.btl.CustomRoom;
import com.example.btl.RandomRoom;
import com.example.btl.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class MainMenuHandler {
    private Connection connection;

    public MainMenuHandler(Connection connection) {
        this.connection = connection;
    }

    public RandomRoom createRandomRoom(String username) {
        try {
            String query = "SELECT * FROM randomroom WHERE player2 IS NULL LIMIT 1";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    int idRoom = rs.getInt("idRoom");
                    String player1 = rs.getString("player1");
                    String player2 = username;
                    String updateQuery1 = "UPDATE randomroom SET  player2 = ? WHERE idroom = ?";
                    try {
                        PreparedStatement statement2 = connection.prepareStatement(updateQuery1);
                        statement2.setString(1, username);
                        statement2.setInt(2, idRoom);
                        statement2.executeUpdate();

                    }
                    catch (SQLException e){
                        e.printStackTrace();
                    }
                    return new RandomRoom(idRoom, player1, player2);
                } else {
                    String query1 = "SELECT COUNT(*) AS room_count FROM randomroom";
                    try (PreparedStatement statement1 = connection.prepareStatement(query1);
                         ResultSet resultSet1 = statement1.executeQuery()) {

                        int numberOfRooms = 0;
                        if (resultSet1.next()) {
                            numberOfRooms = resultSet1.getInt("room_count");
                        }

                        int idRoom = generateCustomRoomId(numberOfRooms);
                        String insertRoomQuery = "INSERT INTO randomroom (idRoom, player1, player2) VALUES (?, ?, ?)";

                        try (PreparedStatement insertStatement = connection.prepareStatement(insertRoomQuery)) {
                            insertStatement.setInt(1, idRoom);
                            insertStatement.setString(2, username);
                            insertStatement.setNull(3, java.sql.Types.VARCHAR);

                            int rowsInserted = insertStatement.executeUpdate();
                            if (rowsInserted > 0) {
                                return new RandomRoom(idRoom, username, null);
                            } else {
                                System.out.println("Lỗi khi tạo phòng chơi.");
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi tạo phòng trong createRandomRoom.");
            e.printStackTrace();
        }
        return null;
    }

    public CustomRoom createCustomRoom(String username) {
        try {
            String query = "SELECT COUNT(*) AS room_count FROM customroom";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                int numberOfRooms = 0;
                if (resultSet.next()) {
                    numberOfRooms = resultSet.getInt("room_count");
                }

                int idRoom = generateCustomRoomId(numberOfRooms);
                String insertRoomQuery = "INSERT INTO customroom (idRoom, player1, player2) VALUES (?, ?, ?)";

                try (PreparedStatement insertStatement = connection.prepareStatement(insertRoomQuery)) {
                    insertStatement.setInt(1, idRoom);
                    insertStatement.setString(2, username);
                    insertStatement.setNull(3, java.sql.Types.VARCHAR);

                    int rowsInserted = insertStatement.executeUpdate();
                    if (rowsInserted > 0) {
                        return new CustomRoom(idRoom, username, null);
                    } else {
                        System.out.println("Lỗi khi tạo phòng chơi.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi tạo phòng trong createCustomRoom.");
            e.printStackTrace();
        }
        return null;
    }

    private int generateCustomRoomId(int a) {
        Random random = new Random();
        int randomSuffix = random.nextInt(900) + 100;
        return (100 + a) * 1000 + randomSuffix;
    }
}

