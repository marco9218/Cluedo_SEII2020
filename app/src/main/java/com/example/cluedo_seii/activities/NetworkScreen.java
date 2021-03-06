//TODO delete NetworkScreen

package com.example.cluedo_seii.activities;

import android.annotation.SuppressLint;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cluedo_seii.network.Callback;
import com.example.cluedo_seii.network.NetworkGlobalHost;
import com.example.cluedo_seii.network.connectionType;
import com.example.cluedo_seii.network.dto.ConnectedDTO;
import com.example.cluedo_seii.network.dto.QuitGameDTO;
import com.example.cluedo_seii.network.dto.RequestDTO;
import com.example.cluedo_seii.network.dto.TextMessage;
import com.example.cluedo_seii.network.kryonet.GlobalNetworkHostKryo;
import com.example.cluedo_seii.network.kryonet.KryoHelper;
import com.example.cluedo_seii.network.kryonet.KryoNetComponent;
import com.example.cluedo_seii.network.kryonet.NetworkClientKryo;
import com.example.cluedo_seii.network.kryonet.NetworkServerKryo;

import com.example.cluedo_seii.R;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;

public class NetworkScreen extends AppCompatActivity {
    private connectionType conType;
    //private NetworkServerKryo server;
    private NetworkClientKryo client;
    private GlobalNetworkHostKryo globalHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

    }

    @SuppressLint("SetTextI18n")
    public void selectHost(View view) {
        this.conType = connectionType.GLOBALHOST;

        TextView txtType = findViewById(R.id.typeText);
        txtType.setText("GLOBALHOST");

        globalHost = GlobalNetworkHostKryo.getInstance();
        KryoHelper.registerClasses(globalHost);

        try {
            //TODO change to server IP address
            globalHost.connect("192.168.178.20");
        } catch (IOException e) {
            e.printStackTrace();
        }

        globalHost.registerTextMessageCallback(new Callback<TextMessage>() {
            @Override
            public void callback(TextMessage argument) {
                System.out.println("Received:" + argument.toString());
                updateServerResponseMessage(argument.toString());
            }
        });

    }

    public void sendMessage(View view) {
        if (conType.equals(connectionType.GLOBALHOST)) {
            EditText msgInput = findViewById(R.id.editMessage);
            String message = msgInput.getText().toString();

            globalHost.sendMessage(new TextMessage(message));

            //globalHost.sendMessage(new RequestDTO());
            //globalHost.broadcastMessage(new TextMessage(message));
        } else if (conType.equals(connectionType.CLIENT)) {
            EditText msgInput = findViewById(R.id.editMessage);
            String message = msgInput.getText().toString();

            client.sendMessage(new TextMessage(message));
        }
    }

    @SuppressLint("SetTextI18n")
    public void selectClient(View view) {
        try {
            this.conType = connectionType.CLIENT;

            TextView txtType = findViewById(R.id.typeText);
            txtType.setText("CLIENT");

            client = NetworkClientKryo.getInstance();


            client.registerClass(RequestDTO.class);
            client.registerClass(TextMessage.class);
            client.registerClass(QuitGameDTO.class);
            client.registerClass(ConnectedDTO.class);

            client.registerCallback(new Callback<RequestDTO>() {
                @Override
                public void callback(RequestDTO argument) {
                    System.out.println("Received:" + argument.toString());
                    updateServerResponseMessage(argument.toString());
                }
            });

            //client.connect("localhost");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectToHost(View view) {
        if (conType.equals(connectionType.CLIENT)) {
            EditText ipInput = findViewById(R.id.ipAddressInput);
            String ip = ipInput.getText().toString();

            if (ip.equals("ip")) {
                //TODO delete hardcoded ip
                ip = "192.168.178.47";
            }



            try {
                client.connect(ip);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void updateServerResponseMessage(final String message) {
        runOnUiThread(new Runnable() {
            public void run() {
                TextView serverResponse = findViewById(R.id.serverResponse);
                serverResponse.setText(message);
            }
        });

    }
}



