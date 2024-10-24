package com.example.btl;

import java.io.Serializable;
import java.time.LocalDateTime;

import java.time.LocalDateTime;

public class Match {
    private int idMatch;
    private String player1;
    private String player2;
    private String player1Choice; // Khởi tạo là null
    private String player2Choice; // Khởi tạo là null
    private LocalDateTime timeBegin;

    // Constructor
    public Match(int idMatch, String player1, String player2, LocalDateTime timeBegin) {
        this.idMatch = idMatch;
        this.player1 = player1;
        this.player2 = player2;
        this.player1Choice = null; // Khởi tạo là null
        this.player2Choice = null; // Khởi tạo là null
        this.timeBegin = timeBegin;
    }
    public Match(int idMatch, String player1, String player2, String player1Choice, String player2Choice, LocalDateTime timeBegin) {
        this.idMatch = idMatch;
        this.player1 = player1;
        this.player2 = player2;
        this.player1Choice = player1Choice;
        this.player2Choice = player2Choice;
        this.timeBegin = timeBegin;
    }
    // Getters and Setters
    public int getIdMatch() {
        return idMatch;
    }

    public String getPlayer1() {
        return player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public String getPlayer1Choice() {
        return player1Choice;
    }

    public void setPlayer1Choice(String player1Choice) {
        this.player1Choice = player1Choice;
    }

    public String getPlayer2Choice() {
        return player2Choice;
    }

    public void setPlayer2Choice(String player2Choice) {
        this.player2Choice = player2Choice;
    }

    public LocalDateTime getTimeBegin() {
        return timeBegin;
    }
}
