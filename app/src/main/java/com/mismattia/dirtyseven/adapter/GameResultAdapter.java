package com.mismattia.dirtyseven.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.mismattia.dirtyseven.R;
import com.mismattia.dirtyseven.model.Game;
import com.mismattia.dirtyseven.model.GameResult;
import com.mismattia.dirtyseven.utility.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class GameResultAdapter extends RecyclerView.Adapter<GameResultAdapter.MyViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(GameResult item);
    }

    private List<GameResult> results = new ArrayList<>();
    private AppCompatActivity activity;
    private DatabaseHelper myDB;
    final private OnItemClickListener listener;


    public GameResultAdapter(DatabaseHelper myDB, AppCompatActivity activity, OnItemClickListener listener) {
        this.activity = activity;
        this.myDB = myDB;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_result_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final GameResult result = results.get(position);
        holder.playerName.setText(result.getPlayerName());
        holder.playerScore.setText(String.valueOf(result.getScore()));
        holder.bind(results.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public Context getContext() {
        return activity;
    }

    public void setResults(List<GameResult> results) {
        this.results = results;
    }

    public List<GameResult> getGameResults() {
        return this.results;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView playerName;
        public TextView playerScore;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            playerName = itemView.findViewById(R.id.txtViewPlayerName);
            playerScore = itemView.findViewById(R.id.txtViewPlayerScore);
        }

        public void bind(final GameResult item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}

