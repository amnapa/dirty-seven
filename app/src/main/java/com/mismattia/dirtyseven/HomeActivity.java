package com.mismattia.dirtyseven;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btnNewGame = findViewById(R.id.btnNewGame);
        Button btnGameHistory = findViewById(R.id.btnGameHistory);

        btnNewGame.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, GameActivity.class )));

        btnGameHistory.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, HistoryActivity.class )));

    }
}