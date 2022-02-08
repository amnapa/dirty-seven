package com.mismattia.dirtyseven;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mismattia.dirtyseven.adapter.PlayerAdapter;
import com.mismattia.dirtyseven.helper.PlayerRecyclerViewTouchHelper;
import com.mismattia.dirtyseven.model.GamePlayer;
import com.mismattia.dirtyseven.model.Player;
import com.mismattia.dirtyseven.utility.DatabaseHelper;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity implements OnDialogCloseListener {

    private DatabaseHelper myDB;
    private ArrayList<GamePlayer> gamePlayers;
    private ArrayList<Player> allPlayers;
    private PlayerAdapter adapter;
    private AutoCompleteTextView autoCompleteTxtViewPlayerName;
    private TextView textViewNoPLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        autoCompleteTxtViewPlayerName = findViewById(R.id.autoCompleteTxtViewPlayerName);
        textViewNoPLayer = findViewById(R.id.textViewNoPLayer);
        FloatingActionButton fabPlay = findViewById(R.id.fabPlay);
        RecyclerView playerRecyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton addPlayerButton = findViewById(R.id.fab);
        myDB = new DatabaseHelper(PlayerActivity.this);
        gamePlayers = new ArrayList<>();
        adapter = new PlayerAdapter(myDB, PlayerActivity.this);

        adapter.setPlayers(gamePlayers);
        noPlayerMessage();

        playerRecyclerView.setHasFixedSize(true);
        playerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        playerRecyclerView.setAdapter(adapter);

        allPlayers = myDB.getAllPlayers();

        addPlayerButton.setOnClickListener((v) -> {
            Bundle bundle = new Bundle();
            bundle.putBoolean("is_update", false);
            bundle.putParcelableArrayList("players", allPlayers);

            AddNewPlayer player = new AddNewPlayer();
            player.setArguments(bundle);

            player.show(this.getSupportFragmentManager(), player.getTag());
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new PlayerRecyclerViewTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(playerRecyclerView);

        fabPlay.setOnClickListener(view -> {
            //Todo: Check for at least 5 players
            if (adapter.getItemCount() < 3) {
                Toast.makeText(PlayerActivity.this, "حداقل سه بازیکن را انتخاب یا اضافه کنید.", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(PlayerActivity.this, MainActivity.class));
            }
        });

        ArrayAdapter<Player> playerAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allPlayers);
        autoCompleteTxtViewPlayerName.setAdapter(playerAdapter);

        autoCompleteTxtViewPlayerName.setOnItemClickListener((parent, view, position, id) -> {
            // clear the text view and allow adding more players
            autoCompleteTxtViewPlayerName.setText("");

            Player player = (Player) parent.getItemAtPosition(position);

            for (GamePlayer gamePlayer : gamePlayers) {
                if (gamePlayer.getPlayerId() == player.getId()) {
                    Toast.makeText(PlayerActivity.this, "این بازیکن قبلا به لیست اضافه شده است.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Add existing player to the game
            myDB.insertGamePlayer(player);

            // Refresh the Recycler view and show the new player
            gamePlayers = myDB.getAllGamePlayers();
            adapter.setPlayers(gamePlayers);
            adapter.notifyDataSetChanged();
            noPlayerMessage();
        });
    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        gamePlayers = myDB.getAllGamePlayers();
        adapter.setPlayers(gamePlayers);
        adapter.notifyDataSetChanged();
        noPlayerMessage();
    }

    private void noPlayerMessage() {
        if (adapter.getItemCount() == 0) {
            textViewNoPLayer.setVisibility(View.VISIBLE);
        } else {
            textViewNoPLayer.setVisibility(View.INVISIBLE);
        }
    }
}