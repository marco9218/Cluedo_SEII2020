package com.example.cluedo_seii.activities.playerGameInteraction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cluedo_seii.Game;
import com.example.cluedo_seii.GameState;
import com.example.cluedo_seii.R;
import com.example.cluedo_seii.activities.GameboardScreen;

public class PlayerTurnNotification extends DialogFragment {
    Game game;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.player_game_interaction_layout, null);
        game = Game.getInstance();
        setCancelable(false);

        DialogInterface.OnClickListener startTurn = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                game.changeGameState(GameState.PLAVERMOVEMENT);

            }
        };

        return new AlertDialog.Builder(getActivity())
                .setTitle("Du bist am Zug")
                .setView(view)
                .setPositiveButton("Ok", startTurn)
                .create();
    }

}
