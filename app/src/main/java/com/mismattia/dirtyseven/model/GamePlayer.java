package com.mismattia.dirtyseven.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GamePlayer implements Parcelable {
    private int id, playerId, gameId, lastScore;
    private String name;

    public int getLastScore() {
        return lastScore;
    }

    public void setLastScore(int lastScore) {
        this.lastScore = lastScore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public GamePlayer(){

    }

    public GamePlayer(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.id = Integer.parseInt(data[0]);
        this.playerId = Integer.parseInt(data[1]);
        this.gameId = Integer.parseInt(data[2]);
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{String.valueOf(this.id),
                String.valueOf(this.playerId),
                String.valueOf(this.gameId)});
    }
}
