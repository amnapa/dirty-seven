package com.mismattia.dirtyseven;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mismattia.dirtyseven.adapter.PlayerAdapter;
import com.mismattia.dirtyseven.helper.PlayerRecyclerViewTouchHelper;
import com.mismattia.dirtyseven.model.Player;
import com.mismattia.dirtyseven.utility.DatabaseHelper;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity implements OnDialogCloseListener {

    private RecyclerView playerRecyclerView;
    private FloatingActionButton addPlayerButton;
    private DatabaseHelper myDB;
    private ArrayList<Player> players;
    private PlayerAdapter adapter;
    private FloatingActionButton fabPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        fabPlay = findViewById(R.id.fabPlay);
        playerRecyclerView = findViewById(R.id.recyclerView);
        addPlayerButton = findViewById(R.id.fab);
        myDB = new DatabaseHelper(PlayerActivity.this);
        players = new ArrayList<>();
        adapter = new PlayerAdapter(myDB, PlayerActivity.this);

        adapter.setPlayers(players);

        playerRecyclerView.setHasFixedSize(true);
        playerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        playerRecyclerView.setAdapter(adapter);

        addPlayerButton.setOnClickListener((v) -> {
            Bundle bundle = new Bundle();
            bundle.putBoolean("is_update", false);
            bundle.putParcelableArrayList("players", players);

            AddNewPlayer player = new AddNewPlayer();
            player.setArguments(bundle);

            player.show(this.getSupportFragmentManager(), player.getTag());
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new PlayerRecyclerViewTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(playerRecyclerView);

        fabPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Todo: Check for at least 5 players
                if(adapter.getItemCount() == 0) {
                    Toast.makeText(PlayerActivity.this, "تعداد بازیکن‌ها کافی نیست", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(PlayerActivity.this, MainActivity.class ));
                }
            }
        });
    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        players = myDB.getAllPlayers();
        //Collections.reverse(players);
        adapter.setPlayers(players);
        adapter.notifyDataSetChanged();
    }
}