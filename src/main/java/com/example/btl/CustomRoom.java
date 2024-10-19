package com.example.btl;

public class CustomRoom {
    private int idRoom;  // ID của phòng
    private String player1;  // Người chơi 1
    private String player2;  // Người chơi 2

    // Constructor
    public CustomRoom(int idRoom, String player1, String player2) {
        this.idRoom = idRoom;
        this.player1 = player1;
        this.player2 = player2;
    }

    // Getter cho idRoom
    public int getIdRoom() {
        return idRoom;
    }

    // Setter cho idRoom
    public void setIdRoom(int idRoom) {
        this.idRoom = idRoom;
    }

    // Getter cho player1
    public String getPlayer1() {
        return player1;
    }

    // Setter cho player1
    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    // Getter cho player2
    public String getPlayer2() {
        return player2;
    }

    // Setter cho player2
    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

}
