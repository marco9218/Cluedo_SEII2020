package com.example.cluedo_seii.Network.kryonet;

import android.os.AsyncTask;
import android.util.Log;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.example.cluedo_seii.Network.Callback;
import com.example.cluedo_seii.Network.NetworkClient;
import com.example.cluedo_seii.Network.dto.RequestDTO;

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

    private NetworkClientKryo() {
        client = new Client();
    }

    public static NetworkClientKryo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NetworkClientKryo();
        }

        return INSTANCE;
    }

    @Override
    public void connect(final String host) throws IOException {
        client.start();



        new Thread("Connection") {
            @Override
            public void run() {
                try {
                    client.connect(5000,host,NetworkConstants.TCP_PORT,NetworkConstants.UDP_PORT);
                } catch (IOException e) {
                    Log.e(TAG, "run: ", e);
                }
            }
        }.start();

        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (callback != null && object instanceof RequestDTO) {
                    Log.i(TAG, "received: " + object.toString());
                    callback.callback((RequestDTO) object );
                }
            }
        });
    }

    @Override
    public void registerCallback(Callback<RequestDTO> callback) {
        this.callback = callback;
    }

    @Override
    public void sendMessage(final RequestDTO message) {
        new Thread("send") {
            @Override
            public void run() {
                client.sendTCP(message);
            }
        }.start();

    }

    @Override
    public void registerClass(Class c) {
        client.getKryo().register(c);
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
}
