package com.mismattia.dirtyseven;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.mismattia.dirtyseven.adapter.PlayerAdapter;
import com.mismattia.dirtyseven.model.Player;
import com.mismattia.dirtyseven.singleton.GameState;
import com.mismattia.dirtyseven.utility.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView playerRecyclerView;
    private DatabaseHelper myDB;
    private ArrayList<Player> players;
    private PlayerAdapter adapter;
    private TextView txtViewGameName;
    private TextView txtViewRoundNumber;
    private ExtendedFloatingActionButton fabEndRound;
    private ExtendedFloatingActionButton fabEndPlay;
    private Chronometer simpleChronometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerRecyclerView = findViewById(R.id.recyclerView);
        txtViewGameName = findViewById(R.id.txtViewGameName);
        txtViewRoundNumber = findViewById(R.id.txtViewRoundNumber);
        simpleChronometer = findViewById(R.id.simpleChronometer);
        fabEndRound = findViewById(R.id.fabEndRound);
        fabEndPlay = findViewById(R.id.fabEndPlay);

        simpleChronometer.start();

        myDB = new DatabaseHelper(MainActivity.this);
        players = new ArrayList<>();
        adapter = new PlayerAdapter(myDB, MainActivity.this);

        players = myDB.getAllPlayers();
        adapter.setPlayers(players);

        playerRecyclerView.setHasFixedSize(true);
        playerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        playerRecyclerView.setAdapter(adapter);

        txtViewGameName.setText(GameState.getInstance().gameName);

        // Store round points
        fabEndRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(MainActivity.this, view);

                List<Player> players = adapter.getPlayers();

                if(playersHaveUnsetScore(players)) {
                    return;
                }


                for (Player player : players) {
                    myDB.insertGameRound(player.getId(), player.getLastScore());

                    player.setLastScore(-1);
                }

                adapter.clearScores();
                Toast.makeText(MainActivity.this, "امتیازهای این دور ذخیره شد", Toast.LENGTH_SHORT).show();

                if(myDB.maxScoresIsReached()) {
                    playCompletionSound();

                    String gameDuration = simpleChronometer.getText().toString();
                    myDB.updateGame(GameState.getInstance().gameId, gameDuration);

                    startActivity(new Intent(MainActivity.this, ResultActivity.class));
                }

                updateRoundNumber();
            }
        });

        // End game and show results
        fabEndPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());

                // alert dialog title align center
                SpannableString title = new SpannableString("پایان بازی");
                title.setSpan(
                        new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                        0,
                        title.length(),
                        0
                );

                builder.setTitle(title);
                builder.setMessage("پایان بازی و مشاهده نتیجه؟");
                builder.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String gameDuration = simpleChronometer.getText().toString();
                        myDB.updateGame(GameState.getInstance().gameId, gameDuration);

                        startActivity(new Intent(MainActivity.this, ResultActivity.class));
                    }
                });
                builder.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });
    }

    private void playCompletionSound() {
        final MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.complete);
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(700, VibrationEffect.EFFECT_DOUBLE_CLICK));
        } else {
            //deprecated in API 26
            v.vibrate(700);
        }

        mp.start();
    }

    // Keep track of number of played rounds
    private void updateRoundNumber() {
        int roundNumber = ++GameState.getInstance().roundNumber;
        txtViewRoundNumber.setText("دور " + String.valueOf(roundNumber));
    }

    // Hide keyboard after storing every round points
    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    private boolean playersHaveUnsetScore(List<Player> players) {
        boolean haveUnsetScore = false;

        for (Player player : players) {
            if(player.getLastScore() == -1) {
                haveUnsetScore = true;

                break;
            }
        }

        return haveUnsetScore;
    }
}