package com.example.cluedo_seii.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cluedo_seii.GameCharacter;
import com.example.cluedo_seii.network.Callback;
import com.example.cluedo_seii.network.ClientData;
import com.example.cluedo_seii.network.connectionType;
import com.example.cluedo_seii.network.dto.ConnectedDTO;
import com.example.cluedo_seii.network.dto.GameCharacterDTO;
import com.example.cluedo_seii.network.dto.PlayerDTO;
import com.example.cluedo_seii.network.dto.QuitGameDTO;
import com.example.cluedo_seii.network.dto.RequestDTO;
import com.example.cluedo_seii.network.dto.TextMessage;
import com.example.cluedo_seii.network.dto.UserNameRequestDTO;
import com.example.cluedo_seii.network.kryonet.NetworkClientKryo;
import com.example.cluedo_seii.network.kryonet.NetworkServerKryo;
import com.example.cluedo_seii.R;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class startGameScreen extends AppCompatActivity {
    public static connectionType conType;
    private NetworkServerKryo server;
    private NetworkClientKryo client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game_screen);

        //TODO add Logic if the game is ready to start
        final Button chooseCharacter = findViewById(R.id.chooseCharacter);
        chooseCharacter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(startGameScreen.this, ChoosePlayerScreen.class));
            }
        });
    }

    public void selectHost(View view) {
        final ListView clientList = findViewById(R.id.clientList);
        conType = connectionType.HOST;

        server = NetworkServerKryo.getInstance();
        server.registerClass(RequestDTO.class);
        server.registerClass(TextMessage.class);
        server.registerClass(QuitGameDTO.class);
        server.registerClass(ConnectedDTO.class);
        server.registerClass(UserNameRequestDTO.class);
        server.registerClass(GameCharacterDTO.class);
        server.registerClass(PlayerDTO.class);
        server.registerClass(LinkedList.class);
        server.registerClass(GameCharacter.class);

        server.registerNewClientCallback(new Callback<LinkedHashMap<Integer, ClientData>>() {
            @Override
            public void callback(LinkedHashMap<Integer, ClientData> argument) {
                final LinkedList<String> usernameList = new LinkedList<>();
                for (ClientData clientData : argument.values()) {
                    usernameList.add(clientData.getUsername());
                }

                // UPDATE Current Players
                runOnUiThread(new Runnable() {
                    public void run() {
                        TextView userNameInput = findViewById(R.id.username_input);
                        ArrayAdapter<String> clientListAdapter = new ArrayAdapter<>(startGameScreen.this, android.R.layout.simple_list_item_1,usernameList);
                        clientList.setAdapter(clientListAdapter);
                        userNameInput.setText(usernameList.toString());
                    }
                });
            }
        });

        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        TextView ipAddressField = findViewById(R.id.ipAddress);
        ipAddressField.setText(ip);

        // hide Username Input Field && show UserList
        findViewById(R.id.username_input).setVisibility(View.INVISIBLE);

        clientList.setVisibility(View.VISIBLE);




    }

    public void selectClient(View view) {
        try {
            conType = connectionType.CLIENT;

            client = NetworkClientKryo.getInstance();


            client.registerClass(RequestDTO.class);
            client.registerClass(TextMessage.class);
            client.registerClass(QuitGameDTO.class);
            client.registerClass(ConnectedDTO.class);
            client.registerClass(UserNameRequestDTO.class);
            client.registerClass(GameCharacterDTO.class);
            client.registerClass(PlayerDTO.class);
            client.registerClass(LinkedList.class);
            client.registerClass(GameCharacter.class);

            //client.connect("localhost");

            EditText ipInput = findViewById(R.id.ipAddress);
            String ip = ipInput.getText().toString();

            if (ip.equals("ip")) {
                //TODO delete hardcoded ip
                ip = "192.168.178.47";
            }

            EditText username_input = findViewById(R.id.username_input);
            final UserNameRequestDTO userNameRequestDTO = new UserNameRequestDTO();
            userNameRequestDTO.setUsername(username_input.getText().toString());

            client.registerConnectionCallback(new Callback<ConnectedDTO>() {
                @Override
                public void callback(ConnectedDTO argument) {
                    // ausführung nach erflogreichem verbinden
                    Log.d("Connection Callback", "callback: ");
                    client.sendUsernameRequest(userNameRequestDTO);
                    // after a succesfull connection the client gets forwarded to the choose Player Activity
                    startActivity(new Intent(startGameScreen.this, ChoosePlayerScreen.class));
                }
            });


            try {
                client.connect(ip);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
