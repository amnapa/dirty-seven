package com.mismattia.dirtyseven;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mismattia.dirtyseven.model.Game;
import com.mismattia.dirtyseven.singleton.GameState;
import com.mismattia.dirtyseven.utility.DatabaseHelper;

public class GameActivity extends AppCompatActivity {
    private EditText edtTxtGameName;
    private EditText edtTxtMaxScore;
    private DatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Button btnCreateGame = findViewById(R.id.btnCreateGame);
        edtTxtGameName = findViewById(R.id.edtTxtGameName);
        edtTxtMaxScore = findViewById(R.id.edtTxtMaxScore);

        btnCreateGame.setOnClickListener(view -> {
            String gameName = edtTxtGameName.getText().toString();
            String maxScoreText = edtTxtMaxScore.getText().toString();

            if(gameName.trim().length() < 3) {
                Toast.makeText(GameActivity.this, "نام بازی باید حداقل سه کاراکتر باشد", Toast.LENGTH_SHORT).show();

                return;
            }

            int maxScore = maxScoreText.equals("") ? 0 :
                    Integer.parseInt(maxScoreText);

            // Store new game data
            Game game = new Game(gameName, maxScore, null);
            myDB = new DatabaseHelper(GameActivity.this);

            // Set global properties of game id, name and max score
            GameState.getInstance().gameId = myDB.insertGame(game);
            GameState.getInstance().gameName = gameName;
            GameState.getInstance().maxScore = maxScore;

            // Switch to Player activity and get all players from user
            startActivity(new Intent(GameActivity.this, PlayerActivity.class ));
        });
    }
}