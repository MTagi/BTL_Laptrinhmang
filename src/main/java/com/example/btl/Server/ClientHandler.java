package com.example.btl.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
                switch (command) {
                    case "login":
                        LoginHandler loginHandler = new LoginHandler(connection);
                        String username = input.readLine();
                        String password = input.readLine();
                        User user = loginHandler.authenticate(username, password);

                        if (user != null) {
                            this.username = user.getUsername();
                            onlineUsers.put(this.username, this);  // Lưu client handler vào danh sách online
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
                                output.println("waitingplayer");
                                output.println(rroom.getIdRoom());
                                output.println(rroom.getPlayer1());
                                output.println(rroom.getPlayer2());
                            } else {
                                output.println("playnow");
                                GameHandler gHandler =new GameHandler(connection);
                                Match newMatch = gHandler.createMatch(rroom.getPlayer1(), rroom.getPlayer2());
                                output.println(newMatch.getIdMatch());
                                output.println(newMatch.getPlayer1());
                                output.println(newMatch.getPlayer2());
                                output.println(newMatch.getTimeBegin());
                                ClientHandler friend = onlineUsers.get(rroom.getPlayer1());
                                if (friend != null) {
                                    // Gửi lời mời đến người bạn
                                    friend.output.println("playgamenow");
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

}

