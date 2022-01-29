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
import com.mismattia.dirtyseven.utility.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.MyViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Game item);
    }

    private static final String TAG = "GameAdapter";
    private List<Game> gameList = new ArrayList<>();
    private List<Game> gameListCopy = new ArrayList<>();
    private AppCompatActivity activity;
    private DatabaseHelper myDB;
    final private OnItemClickListener listener;


    public GameAdapter(DatabaseHelper myDB, AppCompatActivity activity, OnItemClickListener listener) {
        this.activity = activity;
        this.myDB = myDB;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Game game = gameList.get(position);
        holder.gameName.setText(game.getName());
        holder.gameDate.setText(game.getDate());
        holder.bind(gameList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public Context getContext() {
        return activity;
    }

    public void setGames(List<Game> gameList) {
        this.gameList = gameList;
        this.gameListCopy = gameList;
    }

    public List<Game> getGames() {
        return this.gameList;
    }

    public void deleteGame(int position) {
        Game item = gameList.get(position);
        myDB.deleteGame(item.getId());
        gameList.remove(position);
        notifyItemRemoved(position);
    }

    public void filter(String text) {
        gameList.clear();
        if(text.isEmpty()){
            List<Game> games = myDB.getAllGames();
            Collections.reverse(games);
            gameList.addAll(games);
        } else{
            for(Game item: myDB.getAllGames()){
                if(item.getName().contains(text)){
                    gameList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView gameName;
        public TextView gameDate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            gameName = itemView.findViewById(R.id.txtViewGameName);
            gameDate = itemView.findViewById(R.id.txtViewGameDate);
        }

        public void bind(final Game item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
