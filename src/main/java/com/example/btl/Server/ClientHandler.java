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
                            friend3.output.println("nulll");
                            friend3.output.println("playnowcustomroom");
                            friend3.output.println("1234");
                            GameHandler gHandler1 =new GameHandler(connection);
                            Match newMatch = gHandler1.createMatch(namePlayerReady, namePlayer2);
                            friend3.output.println(idRoom2);
                            friend3.output.println(newMatch.getIdMatch());
                            friend3.output.println(newMatch.getPlayer1());
                            friend3.output.println(newMatch.getPlayer2());
                            friend3.output.println(newMatch.getTimeBegin());
                            ClientHandler friend2 = onlineUsers.get(namePlayer2);
                            if (friend2 != null) {
                                friend2.output.println("nulll");
                                // Gửi lời mời đến người bạn
                                friend2.output.println("playgamenowcustomroom");
                                friend2.output.println(idRoom2);
                                friend2.output.println(newMatch.getIdMatch());
                                friend2.output.println(newMatch.getPlayer1());
                                friend2.output.println(newMatch.getPlayer2());
                                friend2.output.println(newMatch.getTimeBegin());
                            } else {
                                output.println("User " + " is not online.");
                            }
                        }
                        else{
                            System.out.println("aaaaaaa");
                            System.out.println(namePlayerReady);
                            friend3.output.println("bulll");
                            friend3.output.println("waitingcustomroom");
                        }
                    break;

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

