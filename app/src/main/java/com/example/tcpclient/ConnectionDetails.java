package com.example.tcpclient;

public class ConnectionDetails {
    private int port;
    private String ip;

    public ConnectionDetails(String ip, int port) {
        this.port = port;
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }
}
