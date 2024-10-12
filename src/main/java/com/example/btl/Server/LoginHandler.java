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
