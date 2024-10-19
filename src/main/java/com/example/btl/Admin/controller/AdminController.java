package com.example.btl.Admin.controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

public class AdminController {
    private Socket adminSocket;
    private String serverHost = "localhost";
    private int serverPort = 54321;

    public AdminController() {
    }

    public Socket openConnection(){
        try {

            adminSocket = new Socket(serverHost, serverPort);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        return adminSocket;
    }

    public boolean sendData(Map<String, Object> data) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(adminSocket.getOutputStream());
            oos.writeObject(data);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public Map<String, Object> receiveData() {
        try {
            ObjectInputStream ois = new ObjectInputStream(adminSocket.getInputStream());
            Object o = ois.readObject();
            return (Map<String, Object>) o;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public boolean closeConnection() {
        try {
            adminSocket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
