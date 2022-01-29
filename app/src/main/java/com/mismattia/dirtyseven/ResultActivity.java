package com.mismattia.dirtyseven;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mismattia.dirtyseven.model.Game;
import com.mismattia.dirtyseven.singleton.GameState;
import com.mismattia.dirtyseven.utility.DatabaseHelper;

import org.w3c.dom.Text;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private DatabaseHelper myDB;
    private TextView txtViewResultGameName;
    private TextView txtViewGameDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        txtViewResultGameName = findViewById(R.id.txtViewResultGameName);
        txtViewGameDuration = findViewById(R.id.txtViewGameDuration);
        myDB = new DatabaseHelper(ResultActivity.this);

        LinkedHashMap<String, Integer> players = myDB.getFinalResult();
        Game game = myDB.getGame(GameState.getInstance().gameId);

        txtViewResultGameName.setText("امتیازهای پایانی - " + game.getName() );
        String gameDuration = game.getDuration().equals("") ? "-" : game.getDuration();
        txtViewGameDuration.setText("مدت زمان بازی: " + gameDuration);

        TableLayout stk = findViewById(R.id.tableResult);
        TableRow.LayoutParams tb = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f);

        int counter = 0;

        for (Map.Entry<String, Integer> set :
                players.entrySet()) {
            counter++;

            TableRow tbrow = new TableRow(this);
            tbrow.setTextDirection(View.TEXT_DIRECTION_RTL);

            TextView t1v = new TextView(this);
            t1v.setTextDirection(View.TEXT_DIRECTION_RTL);
            String name = String.valueOf(counter) + ". " + set.getKey();
            t1v.setText(name);
            t1v.setTextColor(Color.BLACK);
            t1v.setTextSize(20);
            t1v.setPadding(20, 20,20,20);
            t1v.setGravity(Gravity.CENTER);
            t1v.setLayoutParams(tb);
            tbrow.addView(t1v);

            TextView t2v = new TextView(this);
            t2v.setTextDirection(View.TEXT_DIRECTION_RTL);
            t2v.setText(set.getValue().toString());
            t2v.setTextColor(Color.BLACK);
            t2v.setTextSize(25);
            t2v.setPadding(20, 20,20,20);
            t2v.setGravity(Gravity.CENTER);
            t2v.setLayoutParams(tb);
            tbrow.addView(t2v);

            stk.addView(tbrow);

        }



    }
}