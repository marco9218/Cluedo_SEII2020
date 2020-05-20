package com.example.cluedo_seii.network.kryonet;

import android.os.Handler;
import android.util.Log;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.example.cluedo_seii.network.Callback;
import com.example.cluedo_seii.network.ClientData;
import com.example.cluedo_seii.network.NetworkServer;
import com.example.cluedo_seii.network.dto.ConnectedDTO;
import com.example.cluedo_seii.network.dto.QuitGameDTO;
import com.example.cluedo_seii.network.dto.RequestDTO;
import com.example.cluedo_seii.network.dto.TextMessage;
import com.example.cluedo_seii.network.dto.UserNameRequestDTO;

import java.io.IOException;
import java.util.LinkedHashMap;

import static android.content.ContentValues.TAG;

public class NetworkServerKryo implements KryoNetComponent, NetworkServer {
    //SINGLETON_INSTANCE
    private static NetworkServerKryo INSTANCE = null;

    private Server server;
    private Callback<RequestDTO> messageCallback;
    private Callback<LinkedHashMap<Integer, ClientData>> newClientCallback;

    private LinkedHashMap<Integer, ClientData> clientList;

    private NetworkServerKryo() {
        server = new Server();
        clientList = new LinkedHashMap<>();
    }

    public static NetworkServerKryo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NetworkServerKryo();
        }

        return INSTANCE;
    }

    public void start() throws IOException {
        server.start();
        server.bind(NetworkConstants.TCP_PORT,NetworkConstants.UDP_PORT);

        server.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof RequestDTO)
                    handleRequest(connection, object);
                    //messageCallback.callback((RequestDTO) object);
            }
        });
    }

    private void handleRequest(Connection connection, Object object) {
        Log.d("Received Object:",object.getClass().toString());
        if (object instanceof TextMessage) {
            messageCallback.callback((TextMessage) object);
        } else if (object instanceof ConnectedDTO) {
            Log.d("test", "ConnectedDTO");
            handleConnectedRequest(connection, (ConnectedDTO) object);
        } else if (object instanceof UserNameRequestDTO) {
            Log.d("test", "UserNameRequestDTO");
            handleUsernameRequest(connection, (UserNameRequestDTO) object);
        }
    }

    private void handleConnectedRequest(Connection connection, ConnectedDTO connectedDTO) {
        Log.d("network-Server:", "Received Connected Request");
        sendMessageToClient(connectedDTO, connection);
    }

    private void handleUsernameRequest(Connection connection, UserNameRequestDTO userNameRequestDTO) {
        // TODO delete hardcoded IDs and add Player to ClientData
        Log.d("UserNameRequest", userNameRequestDTO.getUsername());
        ClientData client = new ClientData();
        client.setId();
        client.setConnection(connection);
        client.setUsername(userNameRequestDTO.getUsername());

        clientList.put(client.getId(), client);

        newClientCallback.callback(clientList);
    }


    @Override
    public void registerCallback(Callback<RequestDTO> callback) {
        this.messageCallback = callback;
    }

    public void registerNewClientCallback(Callback<LinkedHashMap<Integer, ClientData>> callback) {
        this.newClientCallback = callback;
    }

    @Override
    public void broadcastMessage(RequestDTO message) {
        for (Connection connection: server.getConnections()) {
            sendMessageToClient(message, connection);
        }
    }

    private void sendMessageToClient(final RequestDTO message, final Connection connection) {
        Log.d(TAG, "sendMessageToClient: " + message.toString());

        new Thread("send") {
            @Override
            public void run() {
                connection.sendTCP(message);
            }
        }.start();
    }

    @Override
    public void registerClass(Class c) {
        server.getKryo().register(c);
    }

    public String getAddress() {
        return null;
    }

    //TODO delete Users from List
    public void resetNetwork() {
        sendQuitGame();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                server.close();
                server.stop();

                INSTANCE = new NetworkServerKryo();
            }
        });

        clientList.clear();
    }

    private void sendQuitGame() {
        QuitGameDTO quitGame = new QuitGameDTO();
        broadcastMessage(quitGame);
    }
}