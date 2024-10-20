package com.example.btl.Server;


import com.example.btl.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminHandler extends Thread {
    private Socket adminSocket;
    private Connection connection;

    public AdminHandler(Socket socket, Connection connection) {
        this.adminSocket = socket;
        this.connection = connection;
    }

    @Override
    public void run() {
        listening();
    }

    private void listening(){
        try {
            while (true) { // Vòng lặp để duy trì kết nối
                Map<String, Object> receivedData = receiveData();
                if (receivedData != null) {
                    String request_type = (String) receivedData.get("request_type");
                    if (request_type.equals("SEARCH_USER_BY_NAME")) {
                        String name = (String) receivedData.get("name");
                        String result = "false";
                        ArrayList<User> students = searchByName(name);
                        if (students != null) {
                            result = "true";
                            sendData(Map.of("result", result, "students", students));
                        } else {
                            sendData(Map.of("result", result));
                        }
                    } else if (request_type.equals("GET_PLAYER_RANK")) {
                        String username = (String) receivedData.get("username");
                        String result = "false";
                        int rank = getPlayerRank(username);
                        if (rank != -1) {
                            result = "true";
                            sendData(Map.of("result", result, "rank", rank));
                        } else {
                            sendData(Map.of("result", result));
                        }
                    } else if (request_type.equals("GET_NUM_OF_PLAYERS")) {
                        String result = "false";
                        int numOfPlayers = getNumOfPlayers();
                        if (numOfPlayers != -1) {
                            result = "true";
                            sendData(Map.of("result", result, "numOfPlayers", numOfPlayers));
                        } else {
                            sendData(Map.of("result", result));
                        }
                    } else if (request_type.equals("GET_NUM_OF_ONLINE_PLAYERS")) {
                        String result = "false";
                        int numOfOnlinePlayers = getNumOfOnlinePlayers();
                        if (numOfOnlinePlayers != -1) {
                            result = "true";
                            sendData(Map.of("result", result, "numOfOnlinePlayers", numOfOnlinePlayers));
                        } else {
                            sendData(Map.of("result", result));
                        }
                    } else if (request_type.equals("LOGOUT")) {
                        String username = (String) receivedData.get("username");
                        String result = "false";
                        if (logout(username)) {
                            result = "true";
                            sendData(Map.of("result", result));
                        } else {
                            sendData(Map.of("result", result));
                        }
                        break; // Kết thúc phiên làm việc sau khi admin logout
                    } else if (request_type.equals("GET_PLAYERS_IN_RANK")) {
                        int min = (int) receivedData.get("min");
                        int max = (int) receivedData.get("max");
                        String result = "false";
                        List<User> players = getPlayersInRank(min, max);
                        if (players != null) {
                            result = "true";
                            sendData(Map.of("result", result, "players", players));
                        } else {
                            sendData(Map.of("result", result));
                        }
                    } else if (request_type.equals("DELETE_USER")) {
                        String username = (String) receivedData.get("username");
                        String result = "false";
                        if (deleteUser(username)) {
                            result = "true";
                            sendData(Map.of("result", result));
                        } else {
                            sendData(Map.of("result", result));
                        }
                    } else {
                        // Invalid request
                        sendData(Map.of("result", "false"));
                    }
                }
            }

            adminSocket.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getNumOfPlayers() {
        try {
            String query = "SELECT COUNT(*) FROM users WHERE role = 0";
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private int getNumOfOnlinePlayers() {
        try {
            String query = "SELECT COUNT(*) FROM users WHERE role = 0 AND status = 'online'";
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private int getPlayerRank(String username) {
        try {
            String query = "SELECT username, totalPoints FROM users WHERE role = 0 ORDER BY totalPoints DESC";
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();

            int rank = 1;
            while (resultSet.next()) {
                String user = resultSet.getString("username");
                if(user.equals(username)){
                    return rank;
                }
                rank++;
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private ArrayList<User> searchByName(String name) {
        try {
            String query = "SELECT * FROM users WHERE username LIKE ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "%" + name + "%");

            ResultSet resultSet = statement.executeQuery();

            ArrayList<User> students = new ArrayList<>();
            while (resultSet.next()) {
                User student = new User(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("gmail"),
                        resultSet.getInt("win"),
                        resultSet.getInt("draw"),
                        resultSet.getInt("loss"),
                        resultSet.getInt("totalPoints"),
                        resultSet.getString("status"),
                        resultSet.getInt("role")
                );
                students.add(student);
            }
            return students;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<User> getPlayersInRank(int min, int max) {
        try {
            int limit = max - min + 1;
            String query = "SELECT * FROM users WHERE role = 0 ORDER BY totalPoints DESC LIMIT ? OFFSET ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, limit);
            statement.setInt(2, min - 1);

            ResultSet resultSet = statement.executeQuery();

            List<User> players = new ArrayList<>();
            while (resultSet.next()) {
                User player = new User(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("gmail"),
                        resultSet.getInt("win"),
                        resultSet.getInt("draw"),
                        resultSet.getInt("loss"),
                        resultSet.getInt("totalPoints"),
                        resultSet.getString("status"),
                        resultSet.getInt("role")
                );
                players.add(player);
            }
            return players;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean deleteUser(String username) {
        try {
            String query = "DELETE FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);

            int rows = statement.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean logout(String username) {
        try {
            String query = "UPDATE users SET status = 'offline' WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);

            int rows = statement.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void sendData(Map<String, Object> data) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(adminSocket.getOutputStream());
            oos.writeObject(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Map<String, Object> receiveData() {
        try {
            ObjectInputStream ois = new ObjectInputStream(adminSocket.getInputStream());
            Object o = ois.readObject();
            return (Map<String, Object>) o;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
