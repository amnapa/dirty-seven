package com.mismattia.dirtyseven.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.mismattia.dirtyseven.AddNewPlayer;
import com.mismattia.dirtyseven.MainActivity;
import com.mismattia.dirtyseven.R;
import com.mismattia.dirtyseven.model.GamePlayer;
import com.mismattia.dirtyseven.model.Player;
import com.mismattia.dirtyseven.utility.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.MyViewHolder> {
    private ArrayList<GamePlayer> playerList = new ArrayList<>();
    private AppCompatActivity activity;
    private DatabaseHelper myDB;
    private Boolean clearScores = false;

    public PlayerAdapter(DatabaseHelper myDB, AppCompatActivity activity) {
        this.activity = activity;
        this.myDB = myDB;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_layout, parent, false);
        return new MyViewHolder(v, new MyCustomEditTextListener());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final GamePlayer player = playerList.get(position);
        holder.playerName.setText(player.getName());
        holder.playerTotalScore.setText(String.valueOf(myDB.getAllPlayerTotalScores(player.getPlayerId())));

        // Clear players score after each round
        if (clearScores) {
            holder.playerScore.setText("");
        }

        // If the main activity is started make the player score visible
        if(activity instanceof MainActivity) {
            holder.playerScore.setVisibility(View.VISIBLE);
            holder.playerTotalScore.setVisibility(View.VISIBLE);
        }

        holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public Context getContext() {
        return activity;
    }

    public void setPlayers(ArrayList<GamePlayer> playerList) {
        this.playerList = playerList;
    }

    public ArrayList<GamePlayer> getPlayers() {
        return this.playerList;
    }

    public void deletePlayer(int position) {
        GamePlayer item = playerList.get(position);
        myDB.deletePlayer(item.getId());
        playerList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        GamePlayer item = playerList.get(position);
        ArrayList<Player> players = myDB.getAllPlayers();

        Bundle bundle = new Bundle();
        bundle.putBoolean("is_update", true);
        bundle.putInt("id", item.getPlayerId());
        bundle.putString("name", item.getName());
        bundle.putParcelableArrayList("players", players);

        AddNewPlayer player = new AddNewPlayer();
        player.setArguments(bundle);
        player.show(activity.getSupportFragmentManager(), player.getTag());
    }

    public void clearScores() {
        this.clearScores = true;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView playerName;
        public EditText playerScore;
        public TextView playerTotalScore;
        public MyCustomEditTextListener myCustomEditTextListener;

        public MyViewHolder(@NonNull View itemView, MyCustomEditTextListener myCustomEditTextListener) {
            super(itemView);
            playerName = itemView.findViewById(R.id.txtViewPlayerName);

            this.playerScore = itemView.findViewById(R.id.txtEditRoundScore);
            this.playerTotalScore = itemView.findViewById(R.id.txtViewPlayerScore);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.playerScore.addTextChangedListener(myCustomEditTextListener);
        }
    }

    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence editable, int i, int i2, int i3) {
            GamePlayer player = playerList.get(position);
            String playerScoreText = editable.toString();

                    if (! playerScoreText.equals("")) {
                        int score = Integer.parseInt(playerScoreText);
                        player.setLastScore(score);
                    }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }
}
