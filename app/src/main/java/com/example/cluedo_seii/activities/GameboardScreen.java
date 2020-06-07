package com.example.cluedo_seii.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.cluedo_seii.Card;
import com.example.cluedo_seii.DeckOfCards;
import com.example.cluedo_seii.Game;
import com.example.cluedo_seii.GameCharacter;
import com.example.cluedo_seii.GameState;
import com.example.cluedo_seii.Notepad;
import com.example.cluedo_seii.Player;
import com.example.cluedo_seii.R;
import com.example.cluedo_seii.activities.playerGameInteraction.AccuseSomeone;
import com.example.cluedo_seii.activities.playerGameInteraction.MakeSuspicion;
import com.example.cluedo_seii.activities.playerGameInteraction.PlayerTurnNotification;
import com.example.cluedo_seii.activities.playerGameInteraction.SuspectOrAccuse;
import com.example.cluedo_seii.activities.playerGameInteraction.ThrowDice;
import com.example.cluedo_seii.activities.playerGameInteraction.ThrowDiceOrUseSecretPassage;
import com.example.cluedo_seii.network.connectionType;
import com.example.cluedo_seii.network.dto.GameDTO;
import com.example.cluedo_seii.network.dto.RequestDTO;
import com.example.cluedo_seii.network.kryonet.NetworkClientKryo;
import com.example.cluedo_seii.network.kryonet.NetworkServerKryo;
import com.example.cluedo_seii.spielbrett.Gameboard;
import com.example.cluedo_seii.spielbrett.StartingPoint;
import com.example.cluedo_seii.spielbrett.RoomElement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GameboardScreen extends AppCompatActivity  {

    private float x1, x2, y1, y2;
    private Gameboard gameboard;
    private Game game;
    private LinkedList<Player> players;
    private DeckOfCards deckOfCards;
    private FragmentManager manager;
    private String mesaggeDialogTag;
    private Bundle bundle;
    private Intent intent;
    private List<StartingPoint> startingPoints;
    private List<Player> playerMove;
    private connectionType conType;
    private NetworkServerKryo server;
    private NetworkClientKryo client;

    private Player currentPlayerInDoor;// TODO: Aufräumen und vielleicht nur mehr das Player Objekt anstatt id und Player Objekt
    private int playerCurrentlyPlayingId;

    static final int MIN_SWIPE_DISTANCE = 150;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spielbrett_screen);
        game = Game.getInstance();
        bundle = new Bundle();
        mesaggeDialogTag = "MessageDialog";
        manager = getSupportFragmentManager();

        //Netzwerkinitialisierung für die Klasse GameboardScreen und
        // Zuweisung der SPielerkarten an localPlayer
        conType = StartGameScreen.conType;
        if (conType == connectionType.CLIENT) {
            client = NetworkClientKryo.getInstance();

            for(Player player: game.getPlayers()){
            if(game.getLocalPlayer().getId()==player.getId()){
                for(Card card: player.getPlayerCards()){
                    game.getLocalPlayer().addCard(card);
                    //zuTestzwekcen
                    game.getLocalPlayer().setPosition(new Point(0,0));
                }
            }
            }
        } else if (conType == connectionType.HOST) {
            server = NetworkServerKryo.getInstance();

            //zuTestzwekcen
                game.getLocalPlayer().setPosition(new Point(0,0));
        }



        /*
            0 = GameField
            1 = NoneWalkableElement
            2 = StartingPoint
            3 = Room
            4 = Entrace1
            5 = Entrace2
            6 = Entrace3
            7 = Entrace4
            8 = Entrace5
            9 = Entrace6
            a = Entrace7
            b = Entrace9
            c = WorkingRoom1
            d = WorkingRoom2
            e = WorkingRoom3
            f = WorkingRoom4
            g = WorkingRoom5
            h = WorkingRoom6
            i = WorkingRoom7
            j = WorkingRoom8
            A = Geheimgang
            k = Workingroom9
            l = Bib1
            m = Bib2
            n = Bib3
            o = Bib4
            p = Bib5
            q = Bib6
            r = Bib7
            s = Bib8
            t = Bib9
            u = Bib10
            v = Billard1
            w = Billard2
            x = Billard3
            y = Billard4
            z = Billard5
            B = Billard6
            C = Billard7
            D = Billard8
            E = Billard9
            F = Billard10
            G = Billard11
            H = Billard13
            I = Billard12
            J = Musikzimmer1
            K = Musikzimmer3
            L = Musikzimmer5
            M = Musikzimmer6
            N = Musikzimmer7
            O = Musikzimmer8
            P = Musikzimmer9
            Q = Musikzimmer10
            R = Musikzimmer11
            T = Musikzimmer12
            S = Salon1
            U = Salon2
            V = Salon3
            W = Salon4
            X = Salon5
            Y = Salon6
            Z = Kueche1
            ! = Kueche2
            ä = Kueche3
            ö = Kueche4
            ü = Kueche5
            $ = Kueche6
            % = Kueche7
            & = Kueche8
            / = Kueche9
            ( = Kueche10
            ) = Kueche11
            + = Kueche12
            - = Kueche13
            * = Speisezimmer2
            . = Speisezimmer3
            , = Speisezimmer4
            # = Speisezimmer5
            : = Speisezimmer6
            ; = Speisezimmer7
            < = Speisezimmer8
            > = Speisezimmer9
            = = Speisezimmer10 --frei!
            @ = Speisezimmer11
            [ = Speisezimmer12
            ] = Speisezimmer13
            ^ = Speisezimmer 14
            = = Veranda1
            _ = Veranda2
            { = Veranda3
            } = Veranda4
            ~ = Veranda5
            € = Veranda6
            Ü = Veranda7
            Ö = Veranda8
            Ä = Veranda9




         */

        String gameBoard =
                "cdef04562=_{}" +
                "ghij07890~€ÜÖ" +
                "Akk30a3b03ÄÄA" +
                "2000000000000" +
                "lmt0000000000" +
                "opq3000000002" +
                "r3u0000000000" +
                "0000000000002" +
                "000000003*.,#" +
                "v3wxy0003:;<>" +
                "zBCD30000@[]^" +
                "EFGHI00000000" +
                "0000000000000" +
                "JAK30000Z3!äö" +
                "LMNO0000ü$%&-" +
                "PQRT0000A/()+" +
                "0000000000000" +
                "00003SU300000" +
                "0000VWXY00000" +
                "0020000002000";

        // Init Starting Points
        startingPoints = new ArrayList<>();
        startingPoints.add(new StartingPoint(0, 0));
        startingPoints.add(new StartingPoint(2, 1));

        // Init Player Ids and PlayerMove-Array
        int countPlayerIds = 0;
        playerMove = new ArrayList<>();

        gameboard = new Gameboard(this,13,20, gameBoard);
        setContentView(gameboard.getLayout());


        gameboard.spawnPlayer(startingPoints, this);

        for(StartingPoint startingPoint: startingPoints) {
            Log.i("Test",
                    "StartingPoint Position: " + startingPoint.getPlayerPosition().x + ":"
                            + startingPoint.getPlayerPosition().y);
            GameCharacter gameCharacter = new GameCharacter("Player_"+countPlayerIds, startingPoint.getPlayerPosition());
            playerMove.add(
                    new Player(countPlayerIds++, gameCharacter)
            );
        }
        // Wenn sich die Id ändert, dann danach updateGameboardScreen machen so wie hier!
        playerCurrentlyPlayingId = 0;
        gameboard.updateGameboardScreen(this);

        gameSetOnChangeListener();
    }

    public Gameboard getGameboard() {
        return gameboard;
    }

    public void setGameboard(Gameboard gameboard) {
        this.gameboard = gameboard;
    }

    public List<StartingPoint> getStartingPoints() {
        return startingPoints;
    }

    public void setStartingPoints(List<StartingPoint> startingPoints) {
        this.startingPoints = startingPoints;
    }

    public List<Player> getPlayerMove() {
        return playerMove;
    }

    public void setPlayerMove(List<Player> playerMove) {
        this.playerMove = playerMove;
    }

    public int getPlayerCurrentlyPlayingId() {
        return playerCurrentlyPlayingId;
    }


    public void setPlayerCurrentlyPlayingId(int playerCurrentlyPlayingId) {
        this.playerCurrentlyPlayingId = playerCurrentlyPlayingId;
    }

    public Player getCurrentPlayerInDoor() {
        return currentPlayerInDoor;
    }

    public void setCurrentPlayerInDoor(Player currentPlayerInDoor) {
        this.currentPlayerInDoor = currentPlayerInDoor;
    }



    private void startGame(){
            //TODO initialize Game according to GameLobby Settings
            //Instanz eines Game-objektes Zu Demonstrationszwecken
            deckOfCards = new DeckOfCards();
            players = new LinkedList<>();
            GameCharacter gameCharacter = new GameCharacter("Prof. Bloom", new Point(0,0));
            GameCharacter gameCharacterAlt = new GameCharacter("Fräulein Weiss", new Point(0,0));
            Player player1 = new Player(1, gameCharacterAlt);
            Player player2 = new Player(2,  gameCharacter);
            Player player3 = new Player(3,  gameCharacterAlt);
            players.add(player1);
            players.add(player2);
            players.add(player3);
            game = Game.getInstance();
            game.setGameboard(gameboard);
            game.setPlayers(players);
            game.setLocalPlayer(player1);
            game.distributeCards(); //um Notepad cheatFunction zu demonstrieren
          }

    private void gameSetOnChangeListener(){
        //Ausführung erfolgt wenn Methode changeGameState der Instanz game aufgerufen wird
        game.setListener(new Game.ChangeListener() {
            @Override
            //Wird ausgeführt wenn Methode changeGameState aufgerufen wird
            public void onChange() {
                //Ausgeführt bei GameState.PLAYERTURNBEGIN)
                if(game.getGameState().equals(GameState.PLAYERTURNBEGIN)){
                    if(game.getCurrentPlayer().getId()==game.getLocalPlayer().getId()){
                            turnBegin();
                    }
                    else{//Spieler localPlayer ist nicht am Zug
                        String text = "Spieler " + game.getCurrentPlayer().getId() + " ist am Zug" ;
                        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }

                //Ausgefürt bei GameState.PLAVERMOVEMENT
              else if(game.getGameState().equals(GameState.PLAVERMOVEMENT)){
                    if(game.getCurrentPlayer().getId()==game.getLocalPlayer().getId()){
                        if (//Prüfe ob Spieler sich in einen Raum befindet
                                game.getCurrentPlayer().getPosition().x == 3 && game.getCurrentPlayer().getPosition().y==2  ||
                                game.getCurrentPlayer().getPosition().x == 6 && game.getCurrentPlayer().getPosition().y==2  ||
                                game.getCurrentPlayer().getPosition().x == 9 && game.getCurrentPlayer().getPosition().y==2  ||
                                game.getCurrentPlayer().getPosition().x == 3 && game.getCurrentPlayer().getPosition().y==5  ||
                                game.getCurrentPlayer().getPosition().x == 1 && game.getCurrentPlayer().getPosition().y==6  ||
                                game.getCurrentPlayer().getPosition().x == 3 && game.getCurrentPlayer().getPosition().y==13 ||
                                game.getCurrentPlayer().getPosition().x == 4 && game.getCurrentPlayer().getPosition().y==17 ||
                                game.getCurrentPlayer().getPosition().x == 7 && game.getCurrentPlayer().getPosition().y==17 ||
                                game.getCurrentPlayer().getPosition().x == 9 && game.getCurrentPlayer().getPosition().y==13 ||
                                game.getCurrentPlayer().getPosition().x == 8 && game.getCurrentPlayer().getPosition().y==9  ||
                                game.getCurrentPlayer().getPosition().x == 8 && game.getCurrentPlayer().getPosition().y==8){
                            throwDiceOrUseSecretPassage();
                        }
                        else{
                            throwDice();
                        }
                    }
                    else{//Spieler localPlayer ist nicht am Zug
                    }
                }

                //Ausgeführt bei GameState.PLAYERACCUSATION
                else if(game.getGameState().equals(GameState.PLAYERACCUSATION)){
                   if(game.getCurrentPlayer().getId()==game.getLocalPlayer().getId()){

                     if(//Prüfe ob Spieler sich in einen Raum befindet
                             game.getCurrentPlayer().getPosition().x == 3 && game.getCurrentPlayer().getPosition().y==2  ||
                             game.getCurrentPlayer().getPosition().x == 6 && game.getCurrentPlayer().getPosition().y==2  ||
                             game.getCurrentPlayer().getPosition().x == 9 && game.getCurrentPlayer().getPosition().y==2  ||
                             game.getCurrentPlayer().getPosition().x == 3 && game.getCurrentPlayer().getPosition().y==5  ||
                             game.getCurrentPlayer().getPosition().x == 1 && game.getCurrentPlayer().getPosition().y==6  ||
                             game.getCurrentPlayer().getPosition().x == 3 && game.getCurrentPlayer().getPosition().y==13 ||
                             game.getCurrentPlayer().getPosition().x == 4 && game.getCurrentPlayer().getPosition().y==17 ||
                             game.getCurrentPlayer().getPosition().x == 7 && game.getCurrentPlayer().getPosition().y==17 ||
                             game.getCurrentPlayer().getPosition().x == 9 && game.getCurrentPlayer().getPosition().y==13 ||
                             game.getCurrentPlayer().getPosition().x == 8 && game.getCurrentPlayer().getPosition().y==9  ||
                             game.getCurrentPlayer().getPosition().x == 8 && game.getCurrentPlayer().getPosition().y==8){
                       suspectOrAccuse();}
                     else{ //Wenn der sich am Zug befindende sich Spieler nicht in einen Raum befindet
                     game.changeGameState(GameState.PLAYERTURNEND);
                     game.nextPlayer();}
                   }
                }

                //Ausgeführt bei GameState.PLAYERTURNEND
              else if(game.getGameState().equals(GameState.PLAYERTURNEND)) {

                   if (game.getCurrentPlayer().getId() == game.getLocalPlayer().getId()) {
                        int wrongAccusers = 0;

                        //prüfe Spielbeendigungsbedingungen
                        for (Player player : game.getPlayers()) {
                            if (player.getMadeFalseAccusation() == true) {
                                wrongAccusers++;
                            }
                        }
                        if (wrongAccusers == game.getPlayers().size()) {
                            game.setGameOver(true);
                            game.changeGameState(GameState.END);
                        } else if (game.getGameOver() == true) {
                            game.changeGameState(GameState.END);
                        }

                        //wenn Abbruchbedingungen nicht zutreffen
                        else {//nächster Spieler
                            game.nextPlayer();
                            game.changeGameState(GameState.PLAYERTURNBEGIN);
                        }
                    }
                }
                //Ausgeführt bei GameState.END
                else if(game.getGameState().equals(GameState.END)){
                    finish();
                }
            }
        });
        game.changeGameState(GameState.PLAYERTURNBEGIN);
    }

    //Aufruf von DialogOptionen

    //Dialog für SpielerBenachrichtigung bei Rundenbeginn
    public void turnBegin() {
        PlayerTurnNotification dialog = new PlayerTurnNotification();
        dialog.show(manager, mesaggeDialogTag);
    }


    //Dialog Würfel werfen
    public void throwDice(){
        ThrowDice dialog = new ThrowDice();
        dialog.show(manager, mesaggeDialogTag);
    }

    //Dialog Würfel werfen oder Geheimgang verwenden
    public void throwDiceOrUseSecretPassage(){
        ThrowDiceOrUseSecretPassage dialog = new ThrowDiceOrUseSecretPassage();
        dialog.show(manager, mesaggeDialogTag);
    }

    //Dialog Anklagen oder Verdächtigen
    public void suspectOrAccuse(){
        SuspectOrAccuse dialog = new SuspectOrAccuse();
        dialog.show(manager, mesaggeDialogTag);
    }

    //UI Aufruf von Würfeln, Verdächtigung und Anklage
    //Aufruf von Würfeln
    public void rollDice(){
        startActivity(new Intent(this, RollDiceScreen.class));
    }

    //Aufruf von Verdächtigung
    public void makeSuspicion(){
        startActivity(new Intent(this, MakeSuspicion.class));
    }

    //Aufruf Detektivnotizblock
    public void startNotepad(){
        intent = new Intent(this, NotepadScreen.class);
        //intent.putExtra("game",game);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }

    //Aufruf Anklage
    public void accuseSomeone(){
        startActivity(new Intent(this, AccuseSomeone.class));
    }

    //Zeigt Karten auf Spielerhand
    public void showCards(){
        startActivity(new Intent(this, ShowCards.class));
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
    }
    

    //EventListener für Swipe-Event
    @Override
    public boolean onTouchEvent (MotionEvent touchEvent){
        switch(touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;

            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                float swipeRight = x2-x1,
                        swipeLeft = x1-x2,
                        swipeDown = y2-y1,
                        swipeUp = y1-y2;


                if(swipeDown > MIN_SWIPE_DISTANCE){
                    startActivity(new Intent(GameboardScreen.this, RollDiceScreen.class));
                    overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
                }else if(swipeRight > MIN_SWIPE_DISTANCE){
                    startNotepad();
                } else if(swipeLeft > MIN_SWIPE_DISTANCE){
                    showCards();
                }
                break;
        }
        return false;
    }
}