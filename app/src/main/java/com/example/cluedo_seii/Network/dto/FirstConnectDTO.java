package com.example.cluedo_seii.Network.dto;

public class FirstConnectDTO extends RequestDTO{
    private boolean isConnected;

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
