package com.example.cluedo_seii.activities;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cluedo_seii.Game;
import com.example.cluedo_seii.GameCharacter;
import com.example.cluedo_seii.Player;
import com.example.cluedo_seii.network.Callback;
import com.example.cluedo_seii.network.connectionType;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cluedo_seii.R;
import com.example.cluedo_seii.network.dto.GameCharacterDTO;
import com.example.cluedo_seii.network.dto.GameDTO;
import com.example.cluedo_seii.network.kryonet.NetworkClientKryo;
import com.example.cluedo_seii.network.kryonet.NetworkServerKryo;

import java.util.HashMap;
import java.util.LinkedList;

public class ChoosePlayerScreen extends AppCompatActivity {
    private connectionType conType;
    private NetworkServerKryo server;
    private NetworkClientKryo client;
    private ListView characterList;
    private TextView waitingForHost;

    private Game game;

    private HashMap<String,GameCharacter> availableCharacters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_player);

        characterList = findViewById(R.id.chooseCharacterListView);
        waitingForHost = findViewById(R.id.waitingForHostText);

        game = Game.getInstance();

        this.conType = StartGameScreen.conType;

        if (conType == connectionType.CLIENT) {
            client = NetworkClientKryo.getInstance();
            clientChooseCharacter();

        } else if (conType == connectionType.HOST) {
            server = NetworkServerKryo.getInstance();

            //TODO implement the startingPoint from the Characters
            GameCharacter character1 = new GameCharacter("Character 1", null);
            GameCharacter character2 = new GameCharacter("Character 2", null);
            GameCharacter character3 = new GameCharacter("Character 3", null);
            GameCharacter character4 = new GameCharacter("Character 4", null);
            GameCharacter character5 = new GameCharacter("Character 5", null);
            GameCharacter character6 = new GameCharacter("Character 6", null);

            availableCharacters = new HashMap<>();
            availableCharacters.put(character1.getName(),character1);
            availableCharacters.put(character2.getName(),character2);
            availableCharacters.put(character3.getName(),character3);
            availableCharacters.put(character4.getName(),character4);
            availableCharacters.put(character5.getName(),character5);
            availableCharacters.put(character6.getName(),character6);

            GameCharacterDTO gameCharacterDTO = new GameCharacterDTO();
            gameCharacterDTO.setAvailablePlayers(availableCharacters);
            server.broadcastMessage(gameCharacterDTO);

            hostChooseCharacter();
        } else {
            Log.d("ERROR", "Neither Client nor Server at Choose Player Screen");
        }
    }

    private void hostChooseCharacter() {
        waitingForHost.setVisibility(View.INVISIBLE);

        updateCharacterList(availableCharacters);

        // UPDATE Current Players
        server.registerCharacterDTOCallback(new Callback<GameCharacterDTO>() {
            @Override
            public void callback(GameCharacterDTO argument) {
                // TODO implement
                availableCharacters = argument.getAvailablePlayers();
                updateCharacterList(availableCharacters);

                if (everyOneHasChosenACharacter()) {
                    //TODO implement next step
                    Log.d("test", "Finished Choosing Characters");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Button proceedToGame = findViewById(R.id.proceedToGameButton);
                            proceedToGame.setVisibility(View.VISIBLE);
                            proceedToGame.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v){
                                    prepareGame();
                                    startActivity(new Intent(ChoosePlayerScreen.this, GameboardScreen.class));
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void clientChooseCharacter() {
        client.registerCharacterCallback(new Callback<GameCharacterDTO>() {
            @Override
            public void callback(GameCharacterDTO argument) {
                // TODO implement
                waitingForHost.setVisibility(View.INVISIBLE);
                updateCharacterList(argument.getAvailablePlayers());
            }
        });

        client.registerGameCallback(new Callback<GameDTO>() {
            @Override
            public void callback(GameDTO argument) {
                //Todo set distributed Cards
                Game tempGame = argument.getGame();
                if (tempGame.getPlayers().contains(Game.getInstance().getLocalPlayer())) {
                    Log.d("Callback", "callback: nice");
                }
                startActivity(new Intent(ChoosePlayerScreen.this, GameboardScreen.class));
            }
        });

    }

    private void updateCharacterList(final HashMap<String,GameCharacter> gameCharacters) {
        final LinkedList<String> characterNameList = new LinkedList<>(gameCharacters.keySet());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayAdapter<String> characterListAdapter = new ArrayAdapter<>(ChoosePlayerScreen.this, android.R.layout.simple_list_item_1, characterNameList);
                characterList.setAdapter(characterListAdapter);

                //remove on Click Listener if the character is already chosen
                if (game.getLocalPlayer() == null) {
                    characterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selectedItem = (String) parent.getItemAtPosition(position);
                            Log.d("Selected shit:", selectedItem);

                            GameCharacter selectedCharacter = gameCharacters.get(selectedItem);
                            Log.d("Selected Character", selectedCharacter.toString());


                            if (conType == connectionType.HOST) {
                                //remove Character from List and forward it to all clients
                                gameCharacters.remove(selectedItem);

                                GameCharacterDTO gameCharacterDTO = new GameCharacterDTO();
                                gameCharacterDTO.setAvailablePlayers(gameCharacters);

                                server.broadcastMessage(gameCharacterDTO);

                                //set local Player and add Player to the Players List
                                Player player = new Player(1, selectedCharacter);
                                game.setLocalPlayer(player);

                                LinkedList<Player> playerLinkedList = game.getPlayers();
                                if (playerLinkedList == null) playerLinkedList = new LinkedList<>();
                                playerLinkedList.add(player);
                                game.setPlayers(playerLinkedList);

                                updateCharacterList(gameCharacters);
                            } else if (conType == connectionType.CLIENT) {
                                GameCharacterDTO gameCharacterDTO = new GameCharacterDTO();
                                gameCharacterDTO.setChoosenPlayer(selectedCharacter);
                                gameCharacterDTO.setAvailablePlayers(gameCharacters);

                                client.sendMessage(gameCharacterDTO);
                            }
                        }
                    });
                } else {
                    characterList.setOnItemClickListener(null);
                }
            }
        });
    }

    private boolean everyOneHasChosenACharacter() {
        return game.getPlayers().size() == server.getClientList().size() + 1;
    }

    private void prepareGame() {
        //TODO implement
        game.distributeCards();
        GameDTO gameDTO = new GameDTO();
        gameDTO.setGame(game);
        server.broadcastMessage(gameDTO);


    }



}
