package com.example.btl.Server;


import java.io.*;
import java.net.*;

public class ServerConnection {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Kết nối đến server thành công!");
    }

    public void disconnect() throws IOException {
        in.close();
        out.close();
        socket.close();
        System.out.println("Ngắt kết nối!");
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String receiveMessage() throws IOException {
        return in.readLine();
    }
}
