package com.mismattia.dirtyseven.model;

public class Game {
    private int id;

    public int getMaxScore() {
        return maxScore;
    }

    private int maxScore;

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    private int rounds;
    private String name;
    private String date;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    private String duration;

    public Game() {
        super();
    }

    public Game(String name, int maxScore, String date, int rounds) {
        this.name = name;
        this.maxScore = maxScore;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
