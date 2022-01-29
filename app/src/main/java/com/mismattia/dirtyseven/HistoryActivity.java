package com.mismattia.dirtyseven;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.mismattia.dirtyseven.adapter.GameAdapter;
import com.mismattia.dirtyseven.helper.GameRecyclerViewTouchHelper;
import com.mismattia.dirtyseven.model.Game;
import com.mismattia.dirtyseven.singleton.GameState;
import com.mismattia.dirtyseven.utility.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView gameRecyclerView;
    private DatabaseHelper myDB;
    private List<Game> games;
    private GameAdapter adapter;
    private SearchView searchViewGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        gameRecyclerView = findViewById(R.id.recyclerView);
        searchViewGame = findViewById(R.id.searchViewGame);

        myDB = new DatabaseHelper(HistoryActivity.this);
        games = new ArrayList<>();
        adapter = new GameAdapter(myDB, HistoryActivity.this, new GameAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Game item) {
                showGameResult(item);
            }
        });

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

    }

    private void showGameResult(Game game) {
        GameState.getInstance().changeState(game);
        startActivity(new Intent(HistoryActivity.this, ResultActivity.class ));
    }
}