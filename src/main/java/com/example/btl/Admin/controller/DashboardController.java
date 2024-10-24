package com.example.btl.Admin.controller;

import com.example.btl.User;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashboardController extends AdminController {
    public DashboardController() {
        super();
    }

    public List<User> searchUserByName(String name) {
        //openConnection();
        sendData(Map.of("request_type", "SEARCH_USER_BY_NAME", "name", name));
        Map<String, Object> receivedData = receiveData();
        //closeConnection();
        if(receivedData != null) {
            String result = (String) receivedData.get("result");
            if(result.equals("true")) {
                return (List<User>) receivedData.get("students");
            } else {
                return new ArrayList<User>();
            }
        } else {
            return new ArrayList<User>();
        }
    }

    public int getPlayerRank(String username) {
        //openConnection();
        sendData(Map.of("request_type", "GET_PLAYER_RANK", "username", username));
        Map<String, Object> receivedData = receiveData();
        //closeConnection();
        if(receivedData != null) {
            String result = (String) receivedData.get("result");
            if(result.equals("true")) {
                return (int) receivedData.get("rank");
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public int getNumOfPlayers() {
        //openConnection();
        sendData(Map.of("request_type", "GET_NUM_OF_PLAYERS"));
        Map<String, Object> receivedData = receiveData();
        //closeConnection();
        if(receivedData != null) {
            String result = (String) receivedData.get("result");
            if(result.equals("true")) {
                return (int) receivedData.get("numOfPlayers");
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public int getNumOfOnlinePlayers() {
        //openConnection();
        sendData(Map.of("request_type", "GET_NUM_OF_ONLINE_PLAYERS"));
        Map<String, Object> receivedData = receiveData();
        //closeConnection();
        if(receivedData != null) {
            String result = (String) receivedData.get("result");
            if(result.equals("true")) {
                return (int) receivedData.get("numOfOnlinePlayers");
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public boolean logout(String username) {
        //openConnection();
        sendData(Map.of("request_type", "LOGOUT", "username", username));
        Map<String, Object> receivedData = receiveData();
        //closeConnection();
        if(receivedData != null) {
            String result = (String) receivedData.get("result");
            return result.equals("true");
        } else {
            return false;
        }
    }

    public List<User> getPlayersInRank(int min, int max) {
        //openConnection();
        sendData(Map.of("request_type", "GET_PLAYERS_IN_RANK", "min", min, "max", max));
        Map<String, Object> receivedData = receiveData();
        //closeConnection();
        if(receivedData != null) {
            String result = (String) receivedData.get("result");
            if(result.equals("true")) {
                return (List<User>) receivedData.get("players");
            } else {
                return new ArrayList<User>();
            }
        } else {
            return new ArrayList<User>();
        }
    }

    public boolean deleteUser(String username) {
        //openConnection();
        sendData(Map.of("request_type", "DELETE_USER", "username", username));
        Map<String, Object> receivedData = receiveData();
        //closeConnection();
        if(receivedData != null) {
            String result = (String) receivedData.get("result");
            return result.equals("true");
        } else {
            return false;
        }
    }
}
