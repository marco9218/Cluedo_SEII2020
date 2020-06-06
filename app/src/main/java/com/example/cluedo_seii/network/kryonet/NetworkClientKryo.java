package com.example.cluedo_seii.network.kryonet;

import android.util.Log;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.example.cluedo_seii.Game;
import com.example.cluedo_seii.network.Callback;
import com.example.cluedo_seii.network.NetworkClient;
import com.example.cluedo_seii.network.dto.ConnectedDTO;
import com.example.cluedo_seii.network.dto.GameCharacterDTO;
import com.example.cluedo_seii.network.dto.GameDTO;
import com.example.cluedo_seii.network.dto.PlayerDTO;
import com.example.cluedo_seii.network.dto.RequestDTO;
import com.example.cluedo_seii.network.dto.TextMessage;
import com.example.cluedo_seii.network.dto.UserNameRequestDTO;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class NetworkClientKryo implements NetworkClient, KryoNetComponent {
    //INSTANCE
    private static NetworkClientKryo INSTANCE = null;

    private Client client;
    private Callback<RequestDTO> callback;
    private Callback<ConnectedDTO> connectionCallback;
    private Callback<GameCharacterDTO> characterCallback;
    private Callback<PlayerDTO> playerCallback;
    private Callback<GameDTO> gameCallback;

    private boolean isConnected;

    private NetworkClientKryo() {
        client = new Client();
    }


    public static NetworkClientKryo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NetworkClientKryo();
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

                    ConnectedDTO connectedDTO = new ConnectedDTO();
                    connectedDTO.setConnected(true);
                    sendMessage(connectedDTO);

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
        } else if (object instanceof ConnectedDTO) {
            handleConnectionResponse(connection, (ConnectedDTO) object);
        } else if (object instanceof GameCharacterDTO) {
            handleGameCharacterResponse(connection, (GameCharacterDTO) object);
        } else if (object instanceof  PlayerDTO) {
            handlePlayerResponse(connection, (PlayerDTO) object);
        } else if (object instanceof GameDTO) {
            handleGameResponse(connection, (GameDTO) object);
        }
    }

    private void handleConnectionResponse(Connection connection, ConnectedDTO connectedDTO) {
        if (connectionCallback != null) {
            connectionCallback.callback(connectedDTO);
            // reset connection Callback
            connectionCallback = null;
        }
    }

    private void handleGameCharacterResponse(Connection connection,  GameCharacterDTO gameCharacterDTO) {
        //TODO implement
        if (characterCallback != null) {
            characterCallback.callback(gameCharacterDTO);
        }
    }

    private void handlePlayerResponse(Connection connection, PlayerDTO playerDTO) {
        // delete character Callback, because the client already choose his character
        // characterCallback = null;
        // TODO implement

        //set Player as LocalPlayer
        Game.getInstance().setLocalPlayer(playerDTO.getPlayer());
        Log.d("Player Response:", playerDTO.toString());
    }

    private void handleGameResponse(Connection connection, GameDTO gameDTO) {
        //TODO delete
        gameCallback.callback(gameDTO);
        Game inGame = gameDTO.getGame();

        Game game = Game.getInstance();

        game.setPlayers(inGame.getPlayers());
        game.setCurrentPlayer(inGame.getCurrentPlayer());
        game.setRound(inGame.getRound());
        game.setGameOver(inGame.getGameOver());

        game.setGameState(inGame.getGameState());
        // TODO set game attributes

    }

    @Override
    public void registerCallback(Callback<RequestDTO> callback) {
        this.callback = callback;
    }

    @Override
    public void registerConnectionCallback(Callback<ConnectedDTO> callback) {
        this.connectionCallback = callback;
    }

    @Override
    public void registerCharacterCallback(Callback<GameCharacterDTO> callback) {
        this.characterCallback = null;
        this.characterCallback = callback;
    }

    public void registerGameCallback(Callback<GameDTO> callback) {
        this.gameCallback = callback;
    }

    public void sendGame(Game game) {
        GameDTO gameDTO = new GameDTO();
        gameDTO.setGame(game);
        sendMessage(gameDTO);
    }

    @Override
    public void sendMessage(final RequestDTO message) {
        new Thread("send") {
            @Override
            public void run() {
                Log.d("Sending Object:", message.getClass().toString());
                client.sendTCP(message);
            }
        }.start();

    }

    @Override
    public void registerClass(Class c) {
        client.getKryo().register(c);
    }

    public void sendUsernameRequest(final UserNameRequestDTO userNameRequestDTO) {
        Log.d("UserNameRequest", userNameRequestDTO.getUsername());
        sendMessage(userNameRequestDTO);
    }

    //TODO delete
    public void getNetworks() {
        client.start();
        List<InetAddress> hosts = client.discoverHosts(NetworkConstants.UDP_PORT,10000);

        Log.i(TAG, "getNetworks: length:" + hosts.size());

        List<String> hostsOut = new LinkedList<>();
        for (InetAddress host: hosts) {
            Log.i(TAG, "getNetworks: " + host.toString());
            hostsOut.add( host.getHostAddress());
        }


        client.stop();
        //return hostsOut;
    }

    private void quitGameHandler() {
        client.close();
        client.stop();
    }


    public boolean isConnected() {
        return isConnected;
    }
}
