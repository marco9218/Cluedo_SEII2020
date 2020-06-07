//TODO delete NetworkScreen

package com.example.cluedo_seii.activities;

import android.annotation.SuppressLint;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cluedo_seii.network.Callback;
import com.example.cluedo_seii.network.connectionType;
import com.example.cluedo_seii.network.dto.ConnectedDTO;
import com.example.cluedo_seii.network.dto.QuitGameDTO;
import com.example.cluedo_seii.network.dto.RequestDTO;
import com.example.cluedo_seii.network.dto.TextMessage;
import com.example.cluedo_seii.network.kryonet.NetworkClientKryo;
import com.example.cluedo_seii.network.kryonet.NetworkServerKryo;

import com.example.cluedo_seii.R;

import java.io.IOException;

public class NetworkScreen extends AppCompatActivity {
    private connectionType conType;
    private NetworkServerKryo server;
    private NetworkClientKryo client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

    }

    @SuppressLint("SetTextI18n")
    public void selectHost(View view) {
        this.conType = connectionType.HOST;

        TextView txtType = findViewById(R.id.typeText);
        txtType.setText("HOST");

        server = NetworkServerKryo.getInstance();
        server.registerClass(RequestDTO.class);
        server.registerClass(TextMessage.class);
        server.registerClass(QuitGameDTO.class);
        server.registerClass(ConnectedDTO.class);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        TextView serverResponse = findViewById(R.id.serverResponse);
        serverResponse.setText(ip);

        server.registerCallback(new Callback<RequestDTO>() {
            @Override
            public void callback(RequestDTO argument) {
                System.out.println("Received:" + argument.toString());
                updateServerResponseMessage(argument.toString());
            }
        });
    }

    public void sendMessage(View view) {
        if (conType.equals(connectionType.HOST)) {
            EditText msgInput = findViewById(R.id.editMessage);
            String message = msgInput.getText().toString();

            server.broadcastMessage(new TextMessage(message));
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
