package com.mismattia.dirtyseven;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.mismattia.dirtyseven.adapter.GameAdapter;
import com.mismattia.dirtyseven.helper.GameRecyclerViewTouchHelper;
import com.mismattia.dirtyseven.model.Game;
import com.mismattia.dirtyseven.singleton.GameState;
import com.mismattia.dirtyseven.utility.DatabaseHelper;

import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private GameAdapter adapter;
    private TextView textViewNoGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        TextView txtViewBack = findViewById(R.id.txtViewBack);
        textViewNoGame = findViewById(R.id.textViewNoGame);
        RecyclerView gameRecyclerView = findViewById(R.id.recyclerView);
        SearchView searchViewGame = findViewById(R.id.searchViewGame);

        DatabaseHelper myDB = new DatabaseHelper(HistoryActivity.this);
        List<Game> games;
        adapter = new GameAdapter(myDB, HistoryActivity.this, this::showGameResult);

        games = myDB.getAllGames();
        Collections.reverse(games);

        adapter.setGames(games);

        gameRecyclerView.setHasFixedSize(true);
        gameRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        gameRecyclerView.setAdapter(adapter);


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new GameRecyclerViewTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(gameRecyclerView);

        searchViewGame.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });

        // Back to game result
        txtViewBack.setOnClickListener(view -> {
            startActivity(new Intent(HistoryActivity.this, HomeActivity.class));
        });

        if (adapter.getItemCount() == 0) {
            textViewNoGame.setVisibility(View.VISIBLE);
        } else {
            textViewNoGame.setVisibility(View.INVISIBLE);
        }
    }

    private void showGameResult(Game game) {
        GameState.getInstance().changeState(game);
        startActivity(new Intent(HistoryActivity.this, ResultActivity.class ));
    }
}