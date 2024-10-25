package com.example.btl.Client;

import com.example.btl.Match;
import com.example.btl.Server.ServerConnection;
import com.example.btl.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameController {
    private ServerConnection serverConnection;
    private int countdownTime = 10;  // Thời gian đếm ngược ban đầu là 10 giây
    private Timeline timeline;
    @FXML
    private Label timeStep;
    @FXML
    private Button paper;

    @FXML
    private Label player1;

    @FXML
    private Label player2;

    @FXML
    private Button rock;
    @FXML
    private AnchorPane resultanchorpane;

    @FXML
    private Label resultlabel;

    @FXML
    private Button scissors;
    private User dataUser;
    private Match dataMatch;
    private Integer idRoom;
    private Stage myStage;
    public void setUser(User data) {
        this.dataUser = data; // Lưu dữ liệu nhận được
    }

    public void setMatch(Match data) {
        this.dataMatch = data; // Lưu dữ liệu nhận được
    }
    public void setInforRoom(Integer idRoom){
        this.idRoom=idRoom;
    }
    public void setServerConnection(ServerConnection serverConnection, Stage myStage) {
        this.serverConnection = serverConnection;
        this.myStage=myStage;
    }
    public void startCountdown() {
        setup();
        countdownTime=15;
        // Cập nhật label với thời gian ban đầu
        timeStep.setText(String.valueOf(countdownTime));

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                countdownTime--;  // Giảm thời gian còn lại mỗi giây
                timeStep.setText(String.valueOf(countdownTime));  // Hiển thị thời gian lên Label

                if (countdownTime <= 0) {
                    timeline.stop();  // Dừng đếm ngược khi hết giờ
                    getResult();
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);  // Lặp vô hạn cho đến khi đếm ngược về 0
        timeline.play();  // Bắt đầu đếm ngược
    }
    private void setup(){
        if(dataUser.getUsername().equals(dataMatch.getPlayer1())){
            player1.setText(dataUser.getUsername());
            player2.setText(dataMatch.getPlayer2());
        }
        else{
            player2.setText(dataMatch.getPlayer1());
            player1.setText(dataMatch.getPlayer2());
        }
    }
    public void clickRock(){
        paper.setVisible(false);
        scissors.setVisible(false);
        serverConnection.sendMessage("playselection");
        serverConnection.sendMessage(String.valueOf(dataMatch.getIdMatch()));
        serverConnection.sendMessage("rock");
        serverConnection.sendMessage(dataUser.getUsername());
    }
    public void clickPaper(){
        rock.setVisible(false);
        scissors.setVisible(false);
        serverConnection.sendMessage("playselection");
        serverConnection.sendMessage(String.valueOf(dataMatch.getIdMatch()));
        serverConnection.sendMessage("paper");
        serverConnection.sendMessage(dataUser.getUsername());
    }
    public void clickScissors(){
        paper.setVisible(false);
        rock.setVisible(false);
        serverConnection.sendMessage("playselection");
        serverConnection.sendMessage(String.valueOf(dataMatch.getIdMatch()));
        serverConnection.sendMessage("scissors");
        serverConnection.sendMessage(dataUser.getUsername());
    }
    private void getResult(){
        serverConnection.sendMessage("getresultmatch");
        serverConnection.sendMessage(String.valueOf(dataMatch.getIdMatch()));
        try {
            String result;
            int idMatch = Integer.parseInt(serverConnection.receiveMessage());
            String player1 = serverConnection.receiveMessage();
            String player2 = serverConnection.receiveMessage();
            String player1choice=serverConnection.receiveMessage();
            String player2choice=serverConnection.receiveMessage();
            String mychoice ;
            String myname;
            String yourchoice;
            String yourname;
            System.out.println(player1);
            System.out.println(dataUser.getUsername());
            if(player1.equals(dataUser.getUsername())){
                 mychoice = player1choice;
                 myname =player1;
                 yourchoice=player2choice;
                 yourname=player2;
            }
            else {
                 mychoice = player2choice;
                 myname =player2;
                 yourchoice=player1choice;
                 yourname=player1;
            }
            if (mychoice == null) {
                result="loss";
            }
            else {
                if (mychoice.equals(yourchoice)) {
                    result = "draw";
                } else if ((mychoice.equals("rock") && yourchoice.equals("scissors")) ||
                        (mychoice.equals("scissors") && yourchoice.equals("paper")) ||
                        (mychoice.equals("paper") && yourchoice.equals("rock")) ||
                        (mychoice!=null && yourchoice==null)) {
                    result="win";
                } else {
                    result="loss";
                }
            }
            startCountdown1("you "+ result);
            if(result.equals("draw")){
                dataUser.setDraw(dataUser.getDraw()+1);
                dataUser.setTotalPoints(dataUser.getTotalPoints()+1);
            }
            if(result.equals("win")){
                dataUser.setWin(dataUser.getWin()+1);
                dataUser.setTotalPoints(dataUser.getTotalPoints()+2);
            }
            if(result.equals("loss")){
                dataUser.setLoss(dataUser.getLoss()+1);
                dataUser.setTotalPoints(dataUser.getTotalPoints()+0);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void startCountdown1(String s) {
        countdownTime=5;
        resultanchorpane.setVisible(true);
        // Cập nhật label với thời gian ban đầu
        resultlabel.setText(String.valueOf(s));

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                countdownTime--;  // Giảm thời gian còn lại mỗi giây
                resultanchorpane.setVisible(true);
                // Cập nhật label với thời gian ban đầu
                resultlabel.setText(String.valueOf(s));  // Hiển thị thời gian lên Label

                if (countdownTime <= 0) {
                    timeline.stop();  // Dừng đếm ngược khi hết giờ
                    resultanchorpane.setVisible(false);
                    // Cập nhật label với thời gian ban đầu
                    resultlabel.setText(String.valueOf(s));
                   try {
                       FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/btl/MainMenu.fxml"));
                       Scene scene = new Scene(loader.load());

                       // Lấy GameController và truyền đối tượng User
                       MainMenuController gameController = loader.getController();
                       serverConnection.sendMessage("updateScore");
                       serverConnection.sendMessage(dataUser.getUsername());
                       serverConnection.sendMessage(dataUser.getPassword());
                       serverConnection.sendMessage(dataUser.getGmail());
                       serverConnection.sendMessage(String.valueOf(dataUser.getWin()));
                       serverConnection.sendMessage(String.valueOf(dataUser.getDraw()));
                       serverConnection.sendMessage(String.valueOf(dataUser.getLoss()));
                       serverConnection.sendMessage(String.valueOf(dataUser.getTotalPoints()));
                       serverConnection.sendMessage(dataUser.getStatus());
                       serverConnection.sendMessage(String.valueOf(dataUser.getRole()));
                       // Lấy stage hiện tại từ nút đăng nhập
                       Stage stage = myStage;
                       gameController.setServerConnection(serverConnection, stage);
                       stage.setScene(scene);
                       stage.setTitle("Giao diện Game");
                       gameController.setUser(dataUser);
                       gameController.setThread();
                       gameController.setup();
                    }
                   catch (Exception e){
                       e.printStackTrace();
                   }
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);  // Lặp vô hạn cho đến khi đếm ngược về 0
        timeline.play();  // Bắt đầu đếm ngược
    }
}
