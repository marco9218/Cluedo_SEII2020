package com.example.cluedo_seii.network.kryonet;

import android.util.Log;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.example.cluedo_seii.Game;
import com.example.cluedo_seii.network.Callback;
import com.example.cluedo_seii.network.NetworkGlobalHost;
import com.example.cluedo_seii.network.dto.ConnectedDTO;
import com.example.cluedo_seii.network.dto.GameCharacterDTO;
import com.example.cluedo_seii.network.dto.GameDTO;
import com.example.cluedo_seii.network.dto.PlayerDTO;
import com.example.cluedo_seii.network.dto.RequestDTO;
import com.example.cluedo_seii.network.dto.TextMessage;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class GlobalNetworkHostKryo implements NetworkGlobalHost, KryoNetComponent {

    //INSTANCE
    private static GlobalNetworkHostKryo INSTANCE = null;

    private Client client;
    private Callback<RequestDTO> callback;
    private Callback<ConnectedDTO> connectionCallback;
    private Callback<GameCharacterDTO> characterCallback;
    private Callback<PlayerDTO> playerCallback;
    private Callback<GameDTO> gameCallback;

    private boolean isConnected;

    private GlobalNetworkHostKryo() {
        client = new Client();
    }


    public static GlobalNetworkHostKryo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GlobalNetworkHostKryo();
        }

        return INSTANCE;
    }

    public static void deleteInstance() {
        INSTANCE = null;
    }


    @Override
    public void connect(final String host) throws IOException {
        client.start();



        new Thread("Connection") {
            @Override
            public void run() {
                try {
                    client.connect(5000,host,NetworkConstants.TCP_PORT,NetworkConstants.UDP_PORT);
                    /*
                    ConnectedDTO connectedDTO = new ConnectedDTO();
                    connectedDTO.setConnected(true);
                    sendMessage(connectedDTO);
                     */

                    isConnected = true;
                } catch (IOException e) {
                    Log.e(TAG, "run: ", e);
                }
            }
        }.start();

        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof RequestDTO) {
                    Log.i(TAG, "received: " + object.toString());
                    handleRequest(connection,object);
                    //callback.callback((RequestDTO) object );
                }
            }
        });
    }

    private void handleRequest(Connection connection, Object object) {
        if (object instanceof TextMessage) {
            //TODO delete
            callback.callback((RequestDTO) object );
        }
    }

    @Override
    public void registerCallback(Callback<RequestDTO> callback) {
        //TODO implement
    }

    @Override
    public void registerClass(Class c) {
        client.getKryo().register(c);
    }

    @Override
    public void sendGame(Game game) {
        //TODO implement
    }
}
