package com.example.cluedo_seii.network;

import com.example.cluedo_seii.network.dto.RequestDTO;

import java.io.IOException;

public interface NetworkServer {

    void start() throws IOException;

    void registerCallback(Callback<RequestDTO> callback);

    void broadcastMessage(RequestDTO message);
}
