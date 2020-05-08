package com.example.cluedo_seii.activities.playerGameInteraction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.cluedo_seii.Game;
import com.example.cluedo_seii.R;
import com.example.cluedo_seii.UserInput;
import com.example.cluedo_seii.activities.GameboardScreen;
import com.example.cluedo_seii.activities.MainActivity;

public class SuspectOrAccuse extends AppCompatDialogFragment {
    Game game;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.player_game_interaction_layout, null);
        game = (Game) getArguments().getSerializable("game");


        DialogInterface.OnClickListener listenerSuspect = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // game.gameControl(UserInput.SUSPECT);
                ((GameboardScreen)getActivity()).makeSuspicion();
               // ((GameboardScreen)getActivity()).updateGame(game);

            }
        };
        DialogInterface.OnClickListener  listenerAccuse = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               //game.gameControl(UserInput.ACCUSE);
                ((GameboardScreen)getActivity()).accuseSomeone();
            }
        };

        return new AlertDialog.Builder(getActivity())
                .setTitle("What do You Wanna Do")
                .setView(view)
                .setPositiveButton("Verdächtigen", listenerSuspect)
                .setNegativeButton("Anklagen", listenerAccuse)
                .create();
    }

}
