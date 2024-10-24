package com.example.btl;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;
    private String gmail;
    private int win;
    private int draw;
    private int loss;
    private int totalPoints;
    private String status;
    private int role;
    private int stt;
    public User(String username, int stt) {
        this.username = username;
        this.stt = stt;
    }

    // Getters và Setters
    // ... các phương thức hiện có ...

    public int getStt() {
        return stt;
    }

    public void setStt(int stt) {
        this.stt = stt;
    }
    public User(String username, String password, String gmail, int win, int draw, int loss, int totalPoints, String status, int role) {
        this.username = username;
        this.password = password;
        this.gmail = gmail;
        this.win = win;
        this.draw = draw;
        this.loss = loss;
        this.totalPoints = totalPoints;
        this.status = status;
        this.role = role;
    }
    public User(String username) {
        this.username = username;
    }

    // Getters và Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getGmail() { return gmail; }
    public void setGmail(String gmail) { this.gmail = gmail; }

    public int getWin() { return win; }
    public void setWin(int win) { this.win = win; }

    public int getDraw() { return draw; }
    public void setDraw(int draw) { this.draw = draw; }

    public int getLoss() { return loss; }
    public void setLoss(int loss) { this.loss = loss; }

    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getRole() { return role; }
    public void setRole(int role) { this.role = role; }
    public int getTotalGames() {
        return win + loss + draw;
    }

    public String getWinRate() {
        int totalGames = getTotalGames();
        return totalGames == 0 ? "0%" : String.format("%.2f%%", (win * 100.0 / totalGames));
    }
}
