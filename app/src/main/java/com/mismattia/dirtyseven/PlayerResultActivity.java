package com.mismattia.dirtyseven;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.mismattia.dirtyseven.model.Player;
import com.mismattia.dirtyseven.utility.ChartValueFormatter;
import com.mismattia.dirtyseven.utility.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerResultActivity extends AppCompatActivity {
    private LineChart scoreChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_result);

        DatabaseHelper myDb = new DatabaseHelper(this);
        List<Integer> scores;
        ArrayList<Entry> scoreValues = new ArrayList<>();

        TextView txtViewPlayerName = findViewById(R.id.txtViewPlayerName);
        TextView txtViewTotalScores = findViewById(R.id.txtViewTotalScores);
        TextView txtViewMinScoreValue = findViewById(R.id.txtViewMinScoreValue);
        TextView txtViewMaxScoreValue = findViewById(R.id.txtViewMaxScoreValue);
        scoreChart = findViewById(R.id.scoreChart);

        // Get player Info
        int playerId = getIntent().getExtras().getInt("PLAYER_ID");
        Player player = myDb.getPlayer(playerId);

        // Get player scores
        scores = myDb.getPlayerScores(playerId);

        int index = 1;
        int totalScores = 0;

        for(Integer score: scores) {
            totalScores += score;
            scoreValues.add(new Entry(index, score));
            index++;
        }

        txtViewPlayerName.setText(player.getName());
        txtViewTotalScores.setText("امتیاز کل: " + String.valueOf(totalScores));
        txtViewMinScoreValue.setText(String.valueOf(Collections.min(scores)));
        txtViewMaxScoreValue.setText(String.valueOf(Collections.max(scores)));

        makeLineChart(scoreValues);
    }

    private void makeLineChart(ArrayList<Entry> scoreValues) {
        LineDataSet set1 = new LineDataSet(scoreValues, "امتیاز");

        set1.setFillAlpha(110);
        set1.setLineWidth(3f);
        set1.setValueTextSize(14f);
        set1.setCircleRadius(8f);
        set1.setColor(Color.RED);
        set1.setCircleColor(Color.RED);
        set1.setFillColor(Color.RED);
        set1.setCircleHoleColor(Color.RED);

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
        set1.setFillDrawable(drawable);
        set1.setDrawFilled(true);

        scoreChart.getXAxis().setGranularity(1f);
        scoreChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        scoreChart.getAxisLeft().setAxisMinValue(0);
        scoreChart.getAxisRight().setAxisMinValue(0);

        scoreChart.getAxisLeft().setDrawGridLines(false);
        scoreChart.getXAxis().setDrawGridLines(false);

        scoreChart.getLegend().setEnabled(false);
        scoreChart.getDescription().setEnabled(false);

        scoreChart.getXAxis().setLabelCount(scoreValues.size(),true);
        scoreChart.getXAxis().setAxisMinimum(1);
        scoreChart.getXAxis().setAxisMaximum(scoreValues.size());


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);
        data.setValueFormatter(new ChartValueFormatter());

        scoreChart.setData(data);
    }
}