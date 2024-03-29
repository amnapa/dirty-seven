package com.mismattia.dirtyseven.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Player implements Parcelable {
    private String name;
    private int id;
    private int gameId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Player(){

    }

    public Player(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.id = Integer.parseInt(data[0]);
        this.name = data[1];
        this.gameId = Integer.parseInt(data[2]);
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{String.valueOf(this.id),
                this.name,
                String.valueOf(this.gameId)});
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
