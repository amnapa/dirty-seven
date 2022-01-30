package com.mismattia.dirtyseven;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.mismattia.dirtyseven.adapter.GameResultAdapter;
import com.mismattia.dirtyseven.model.Game;
import com.mismattia.dirtyseven.model.GameResult;
import com.mismattia.dirtyseven.singleton.GameState;
import com.mismattia.dirtyseven.utility.DatabaseHelper;

import java.util.List;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        RecyclerView gaeResultRecyclerView = findViewById(R.id.recyclerView);
        TextView txtViewResultGameName = findViewById(R.id.txtViewResultGameName);
        TextView txtViewGameDuration = findViewById(R.id.txtViewGameDuration);
        TextView txtViewGameRounds = findViewById(R.id.txtViewGameRounds);

        DatabaseHelper myDB = new DatabaseHelper(ResultActivity.this);
        Game game = myDB.getGame(GameState.getInstance().gameId);

        txtViewResultGameName.setText("امتیازهای پایانی " + GameState.getInstance().gameName );
        txtViewGameRounds.setText("تعداد دور: " + game.getRounds());
        txtViewGameDuration.setText("مدت زمان: " + game.getDuration());

        myDB = new DatabaseHelper(ResultActivity.this);
        List<GameResult> gameResults;
        GameResultAdapter adapter = new GameResultAdapter(myDB, ResultActivity.this, this::showPlayerResult);

        gameResults = myDB.getFinalResult();
        adapter.setResults(gameResults);

        gaeResultRecyclerView.setHasFixedSize(true);
        gaeResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        gaeResultRecyclerView.setAdapter(adapter);
    }

    private void showPlayerResult(GameResult gameResult) {
        Intent intent = new Intent(ResultActivity.this, PlayerResultActivity.class );
        intent.putExtra("PLAYER_ID", gameResult.getPlayerId());
        startActivity(intent);
    }
}