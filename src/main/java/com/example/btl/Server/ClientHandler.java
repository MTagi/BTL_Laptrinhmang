package com.example.btl.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.example.btl.CustomRoom;
import com.example.btl.Match;
import com.example.btl.RandomRoom;
import com.example.btl.User;
import com.example.btl.Server.GameHandler;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private Connection connection;
    private String username;
    private BufferedReader input;
    private PrintWriter output;
    private static Map<String, ClientHandler> onlineUsers;

    public ClientHandler(Socket socket, Connection connection, Map<String, ClientHandler> onlineUsers) {
        this.clientSocket = socket;
        this.connection = connection;
        this.onlineUsers = onlineUsers;
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);
            String command;


            while ((command = input.readLine()) != null) {
                System.out.println(command);
                switch (command) {
                    case "login":
                        LoginHandler loginHandler = new LoginHandler(connection);
                        String username = input.readLine();
                        String password = input.readLine();
                        User user = loginHandler.authenticate(username, password);

                        if (user != null) {
                            this.username = user.getUsername();
                            if (onlineUsers.containsKey(this.username)) {
                                onlineUsers.remove(this.username); // Xóa phần tử cũ
                            }
                            onlineUsers.put(this.username, this); // Thêm phần tử mới // Lưu client handler vào danh sách online
                            output.println("success");
                            output.println(user.getUsername());
                            output.println(user.getPassword());
                            output.println(user.getGmail());
                            output.println(user.getWin());
                            output.println(user.getDraw());
                            output.println(user.getLoss());
                            output.println(user.getTotalPoints());
                            output.println(user.getStatus());
                            output.println(user.getRole());
                            System.out.println("Online Users:");
                            for (String user1 : onlineUsers.keySet()) {
                                System.out.println(user1);
                            }
                        } else {
                            output.println("fail");
                        }
                        break;
                    case "dangki":
                        String tkdki=input.readLine();
                        String mkdki=input.readLine();
                        String gmdki=input.readLine();
                        LoginHandler dkiHandler = new LoginHandler(connection);
                        boolean ok1=dkiHandler.dki(tkdki, mkdki, gmdki);
                        if(ok1){
                            output.println("dkithanhcong");
                        }
                        else{
                            output.println("dkithatbai");
                        }
                        break;

                    // Thêm các lệnh khác ở đây
                    case "setOffline":
                        // Cập nhật trạng thái người dùng thành 'offline'
                        String userToSetOffline = input.readLine();
                        LoginHandler handler = new LoginHandler(connection);
                        handler.updateStatus(userToSetOffline, "offline");
                        onlineUsers.remove(userToSetOffline);
                        break;
                    case "setWaiting":
                        String userToSetWaiting = input.readLine();
                        LoginHandler Whandler = new LoginHandler(connection);
                        Whandler.updateStatus(userToSetWaiting, "waiting");
                        break;
                    case "setIngame":
                        String userToSetIngame = input.readLine();
                        LoginHandler Ihandler = new LoginHandler(connection);
                        Ihandler.updateStatus(userToSetIngame, "ingame");
                        break;
                    case "checkRandomRoom":
                        String userToSetRandomRoom = input.readLine();
                        MainMenuHandler RRoomHandler = new MainMenuHandler(connection);
                        RandomRoom rroom = RRoomHandler.createRandomRoom(userToSetRandomRoom);
                        if (rroom != null) {
                            if (rroom.getPlayer2() == null) {
                                output.println("null");
                                output.println("waitingplayer");
                                output.println(rroom.getIdRoom());
                                output.println(rroom.getPlayer1());
                                output.println(rroom.getPlayer2());
                            } else {
                                output.println("null");
                                output.println("playnow");
                                GameHandler gHandler =new GameHandler(connection);
                                Match newMatch = gHandler.createMatch(rroom.getPlayer1(), rroom.getPlayer2());
                                output.println(String.valueOf(rroom.getIdRoom()));
                                output.println(newMatch.getIdMatch());
                                output.println(newMatch.getPlayer1());
                                output.println(newMatch.getPlayer2());
                                output.println(newMatch.getTimeBegin());
                                ClientHandler friend = onlineUsers.get(rroom.getPlayer1());
                                if (friend != null) {
                                    // Gửi lời mời đến người bạn
                                    friend.output.println("playgamenow");
                                    friend.output.println(String.valueOf(rroom.getIdRoom()));
                                    friend.output.println(newMatch.getIdMatch());
                                    friend.output.println(newMatch.getPlayer1());
                                    friend.output.println(newMatch.getPlayer2());
                                    friend.output.println(newMatch.getTimeBegin());
                                } else {
                                    output.println("User " + " is not online.");
                                }
                            }
                        }
                        break;
                    case "createCustomRoom":
                        String userToSetCutsomRoom = input.readLine();
                        MainMenuHandler CRoomHandler = new MainMenuHandler(connection);
                        CustomRoom customRoom = CRoomHandler.createCustomRoom(userToSetCutsomRoom);
                        if (customRoom != null) {
                            output.println("null");
                            output.println("roomCreated");
                            output.println(customRoom.getIdRoom());
                            output.println(customRoom.getPlayer1());
                            output.println(customRoom.getPlayer2());
                        } else {
                            output.println("fail");
                        }
                        break;
                    case "playselection":
                        int idMatch = Integer.parseInt(input.readLine());
                        String selection= input.readLine();
                        String namePlayer=input.readLine();
                        GameHandler selectionHandler = new GameHandler(connection);
                        selectionHandler.setSelection(idMatch,selection,namePlayer);
                        break;
                    case "getresultmatch":
                        int idMatch1 = Integer.parseInt(input.readLine());
                        GameHandler resultHandler = new GameHandler(connection);
                        Match resultMatch= resultHandler.getMatchInfo(idMatch1);
                        output.println(resultMatch.getIdMatch());
                        output.println(resultMatch.getPlayer1());
                        output.println(resultMatch.getPlayer2());
                        output.println(resultMatch.getPlayer1Choice());
                        output.println(resultMatch.getPlayer2Choice());
                        break;
                    case "sendInvite":
                        String nameFriend= input.readLine();
                        String userInvite= input.readLine();
                        String idRoom=input.readLine();
                        ClientHandler friend = onlineUsers.get(nameFriend);
                        if (friend != null) {
                            // Gửi lời mời đến người bạn
                            friend.output.println("ReceiveInvite");
                            friend.output.println(userInvite);
                            friend.output.println(idRoom);
                        } else {
                            output.println("User " + " is not online.");
                        }
                        break;
                    case "getlistfriend":
                        String usergetlistfriend=input.readLine();
                        CustomRoomHandler glfHandler=new CustomRoomHandler(connection);
                        String res= glfHandler.getListFriend(usergetlistfriend);
                        output.println("null");
                        output.println(res);
                        break;
                    case "getStart":
                        String usergetstart=input.readLine();
                        CustomRoomHandler startHandler=new CustomRoomHandler(connection);
                        break;
                    case "acceptInvite":
                         String nameFriend1=input.readLine();
                         String userAccept=input.readLine();
                         String idRoom1= input.readLine();
                         ClientHandler friendacpt = onlineUsers.get(nameFriend1);
                         CustomRoomHandler adplayerHandler=new CustomRoomHandler(connection);
                         adplayerHandler.addfriend(idRoom1, userAccept);
                        if (friendacpt != null) {
                            // Gửi lời mời đến người bạn
                            friendacpt.output.println("friendAcceptInvite");
                            friendacpt.output.println(userAccept);
                        } else {
                            output.println("User " + " is not online.");
                        }
                        break;
                    case "clickready":
                        String namePlayerReady=input.readLine();
                        String namePlayer2=input.readLine();
                        String idRoom2=input.readLine();
                        System.out.println(idRoom2+"aaaaaaaaaa");
                        CustomRoomHandler readyHandler=new CustomRoomHandler(connection);
                        boolean check=readyHandler.isPlayer1ChoiceNotNull(Integer.parseInt(idRoom2));
                        ClientHandler friend3 = onlineUsers.get(namePlayerReady);
                        if(check){
                            friend3.output.println("aaaaaa");
                            friend3.output.println("playnowcustomroom");
//                            friend3.output.println("123");
                            GameHandler gHandler1 =new GameHandler(connection);
                            Match newMatch1 = gHandler1.createMatch(namePlayerReady, namePlayer2);
                            friend3.output.println(idRoom2);
                            friend3.output.println(newMatch1.getIdMatch());
                            friend3.output.println(newMatch1.getPlayer1());
                            friend3.output.println(newMatch1.getPlayer2());
                            friend3.output.println(newMatch1.getTimeBegin());
                            ClientHandler friend2 = onlineUsers.get(namePlayer2);
                            if (friend2 != null) {
                                // Gửi lời mời đến người bạn
                                friend2.output.println("playgamenowcustomroom");
                                friend2.output.println(idRoom2);
                                friend2.output.println(newMatch1.getIdMatch());
                                friend2.output.println(newMatch1.getPlayer1());
                                friend2.output.println(newMatch1.getPlayer2());
                                friend2.output.println(newMatch1.getTimeBegin());
                            } else {
                                output.println("User " + " is not online.");
                            }
                        }
                        else{
                            friend3.output.println("bulll");
                            friend3.output.println("waitingcustomroom");
                        }
                    break;
                    case "test":
                        output.println("test");
                        break;
                    case "getRank":
                        handleGetAllRanks();
                        break;
                    case "userwithfind":
                        String res1=input.readLine();
                        getUserwithname(res1);
                        break;
                    case "ketban":
                        String us1=input.readLine();
                        String us2=input.readLine();
                        addrequestfriend(us1,us2);
                        break;
                    case "chapnhanyeucau":
                        String us00=input.readLine();
                        String us01=input.readLine();
                        addfriend(us00,us01);
                        deleterequestfriend(us00,us01);
                    break;
                    case "yeucauketban":
                        String as=input.readLine();
                        output.println("aaaa");
                        output.println(getYeuCau(as));
                        break;
                    case "clickTimPhong":
                        String tsset=input.readLine();
                        String pla2=input.readLine();
                        String check1=checkCustomRoom(Integer.parseInt(tsset));
                        if(check1!=null){
                            ClientHandler friendacpt1 = onlineUsers.get(check1);
                            CustomRoomHandler adplayerHandler1=new CustomRoomHandler(connection);
                            adplayerHandler1.addfriend(tsset, pla2);
                            if (friendacpt1 != null) {
                                // Gửi lời mời đến người bạn
                                friendacpt1.output.println("friendAcceptInvite");
                                friendacpt1.output.println(pla2);
                            } else {
                                output.println("User " + " is not online.");
                            }
                            System.out.println("checckkkkkk" +check1);
                            output.println("     ");
                            output.println("haveroom");
                            output.println(check1);
                        }
                        else{
                            System.out.println("thashahtalh");
                            output.println("         ");
                            output.println("noroom");
                        }
                        break;
                    case "updateScore":
                        updateUser(input.readLine(), input.readLine(), input.readLine(), Integer.parseInt(input.readLine()),
                                Integer.parseInt(input.readLine()), Integer.parseInt(input.readLine()), Integer.parseInt(input.readLine()),
                                input.readLine(), input.readLine());
                    default:
                        output.println("Lệnh không hợp lệ.");
                        break;
                }
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void updateUser(String username, String password, String gmail, int win, int draw, int loss, int totalPoints, String status, String role) {
        // Chuỗi kết nối đến cơ sở dữ liệu
        // Câu lệnh SQL để cập nhật thông tin người dùng
        String sql = "UPDATE users SET password = ?, gmail = ?, win = ?, draw = ?, loss = ?, totalPoints = ?, status = ?, role = ? WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Thiết lập các giá trị cho tham số ?
            pstmt.setString(1, password);
            pstmt.setString(2, gmail);
            pstmt.setInt(3, win);
            pstmt.setInt(4, draw);
            pstmt.setInt(5, loss);
            pstmt.setInt(6, totalPoints);
            pstmt.setString(7, status);
            pstmt.setString(8, role);
            pstmt.setString(9, username);

            // Thực thi câu lệnh update
            int rowsAffected = pstmt.executeUpdate();

            // Kiểm tra kết quả
            if (rowsAffected > 0) {
                System.out.println("Dữ liệu của người dùng đã được cập nhật thành công.");
            } else {
                System.out.println("Không tìm thấy người dùng với username: " + username);
            }

        } catch (SQLException e) {
            System.out.println("Lỗi khi cập nhật dữ liệu người dùng: " + e.getMessage());
        }
    }
    private String checkCustomRoom(int roomId){
        String sql = "SELECT player1, player2 FROM customroom WHERE idroom = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // Thiết lập giá trị cho tham số id của phòng
            pstmt.setInt(1, roomId);

            // Thực thi câu lệnh và xử lý kết quả
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Lấy giá trị player1 và player2
                    String player1 = rs.getString("player1");
                    String player2 = rs.getString("player2");

                    // Kiểm tra player2 có trống hay không
                    if (player2 == null) {
                        return player1;
                    } else {
                        return "null";
                    }
                } else {
                    return "null";
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi khi kiểm tra phòng: " + e.getMessage());
        }
        return null;
    }
    private void addrequestfriend(String us1, String us2){
        String sql = "INSERT INTO friendrequest (user1, user2) VALUES (?, ?)";
        // Kết nối và chèn dữ liệu
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Thiết lập giá trị cho các tham số ?
            pstmt.setString(1, us1);
            pstmt.setString(2, us2);
            // Thực thi câu lệnh insert
            int rowsAffected = pstmt.executeUpdate();

            // Kiểm tra kết quả
            if (rowsAffected > 0) {
                System.out.println("Yêu cầu kết bạn đã được thêm thành công.");
            }

        } catch (SQLException e) {
            System.out.println("Lỗi khi thêm yêu cầu kết bạn: " + e.getMessage());
        }
    }
    private void deleterequestfriend(String us1, String us2) {
        // Câu lệnh SQL để xóa yêu cầu kết bạn giữa us1 và us2, bất kể ai là người gửi và ai là người nhận
        String sql = "DELETE FROM friendrequest WHERE (user1 = ? AND user2 = ?) OR (user1 = ? AND user2 = ?)";

        // Kết nối và xóa dữ liệu
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Thiết lập giá trị cho các tham số ? (cả hai cặp hoán đổi)
            pstmt.setString(1, us1);
            pstmt.setString(2, us2);
            pstmt.setString(3, us2);  // Đảo chiều
            pstmt.setString(4, us1);  // Đảo chiều

            // Thực thi câu lệnh delete
            int rowsAffected = pstmt.executeUpdate();

            // Kiểm tra kết quả
            if (rowsAffected > 0) {
                System.out.println("Yêu cầu kết bạn giữa " + us1 + " và " + us2 + " đã được xóa thành công.");
            } else {
                System.out.println("Không tìm thấy yêu cầu kết bạn giữa " + us1 + " và " + us2 + ".");
            }

        } catch (SQLException e) {
            System.out.println("Lỗi khi xóa yêu cầu kết bạn: " + e.getMessage());
        }
    }
    private void addfriend(String us1, String us2){
        String sql = "INSERT INTO friend(user1, user2) VALUES (?, ?)";
        // Kết nối và chèn dữ liệu
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Thiết lập giá trị cho các tham số ?
            pstmt.setString(1, us1);
            pstmt.setString(2, us2);
            // Thực thi câu lệnh insert
            int rowsAffected = pstmt.executeUpdate();

            // Kiểm tra kết quả
            if (rowsAffected > 0) {
                System.out.println("Yêu cầu kết bạn đã được thêm thành công.");
            }

        } catch (SQLException e) {
            System.out.println("Lỗi khi thêm yêu cầu kết bạn: " + e.getMessage());
        }
    }

    private void handleGetAllRanks() throws IOException {
        String query = "SELECT username, totalPoints FROM users where role=0 ORDER BY totalPoints DESC"; // Lấy xếp hạng và sắp xếp theo totalPoints
        StringBuilder ranks = new StringBuilder();

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                int totalPoints = resultSet.getInt("totalPoints");
                ranks.append(username).append(": ").append(totalPoints).append("\n");
            }
            ranks.append("END");
        } catch (SQLException e) {
            e.printStackTrace();
            output.println("Lỗi khi lấy xếp hạng từ cơ sở dữ liệu.");
            return;
        }

        if (ranks.length() > 0) {
            output.println("    ");
            output.println(ranks.toString());
        } else {
            output.println("Không có người dùng nào trong cơ sở dữ liệu.");
        }
    }
    private String getYeuCau(String a){
        StringBuilder result = new StringBuilder();
        String sql = "SELECT user1 FROM friendrequest WHERE user2 = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Thực thi truy vấn
            // Thiết lập giá trị cho tham số ?
            pstmt.setString(1, a);

            // Thực thi truy vấn
            try (ResultSet rs = pstmt.executeQuery()) {
                // Duyệt kết quả và nối user1 vào chuỗi
                while (rs.next()) {
                    String user1 = rs.getString("user1");

                    // Nếu không phải là giá trị đầu tiên, thêm dấu ; trước
                    if (result.length() > 0) {
                        result.append(";");
                    }

                    // Thêm user1 vào chuỗi kết quả
                    result.append(user1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi khi truy vấn danh sách người gửi yêu cầu: " + e.getMessage());
        }

        return result.toString();
    }
    private void getUserwithname(String se) throws IOException {
        String query = "SELECT username, totalPoints FROM users where role=0 and username like ?"; // Lấy xếp hạng và sắp xếp theo totalPoints
        StringBuilder ranks = new StringBuilder();

        try (PreparedStatement statement = connection.prepareStatement(query);){
             statement.setString(1, "%" + se + "%");
             ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                int totalPoints = resultSet.getInt("totalPoints");
                ranks.append(username).append(": ").append(totalPoints).append("\n");
            }
            ranks.append("END");
        } catch (SQLException e) {
            e.printStackTrace();
            output.println("Lỗi khi lấy xếp hạng từ cơ sở dữ liệu.");
            return;
        }
        output.println("  ;  ");
        output.println(ranks.toString());

    }
}

