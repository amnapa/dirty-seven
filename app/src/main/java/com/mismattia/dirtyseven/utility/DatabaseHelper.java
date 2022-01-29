package com.mismattia.dirtyseven.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.mismattia.dirtyseven.model.Game;
import com.mismattia.dirtyseven.model.Player;
import com.mismattia.dirtyseven.singleton.GameState;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SEVEN_DATABASE";
    private static final int DATABASE_VERSION = 3;

    private static final String GAME_TABLE_NAME = "GAME_TABLE";
    private static final String GAME_ID_COL = "ID";
    private static final String GAME_DURATION_COL = "DURATION";
    private static final String GAME_DATE_COL = "DATE";
    private static final String GAME_NAME_COL = "NAME";
    private static final String GAME_MAX_SCORE_COL = "MAX_SCORE";

    private static final String PLAYER_TABLE_NAME = "PLAYER_TABLE";
    private static final String PLAYER_ID_COL = "ID";
    private static final String PLAYER_NAME_COL = "NAME";
    private static final String PLAYER_GAME_ID_COL = "GAME_ID";
    private static final String PLAYER_LAST_SCORE_COL = "LAST_SCORE";

    private static final String GAME_ROUNDS_TABLE_NAME = "GAME_ROUNDS_TABLE";
    private static final String GAME_ROUNDS_GAME_ID_COL = "GAME_ID";
    private static final String GAME_ROUNDS_PLAYER_ID_COL = "PLAYER_ID";
    private static final String GAME_ROUNDS_SCORE = "SCORE";

    private SQLiteDatabase db;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GAME_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, MAX_SCORE INTEGER DEFAULT 0, DURATION DEFAULT '', DATE DATETIME DEFAULT CURRENT_TIMESTAMP)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + PLAYER_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, GAME_ID INTEGER, LAST_SCORE INTEGER DEFAULT 0)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GAME_ROUNDS_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, GAME_ID INTEGER, PLAYER_ID INTEGER, SCORE INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + GAME_TABLE_NAME + " ADD COLUMN " + GAME_MAX_SCORE_COL + " INTEGER DEFAULT 0;");
        }

        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + GAME_TABLE_NAME + " ADD COLUMN " + GAME_DURATION_COL + " TEXT DEFAULT '';");
        }
    }

    public void insertPlayer(Player model) {
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PLAYER_NAME_COL, model.getName());
        values.put(PLAYER_GAME_ID_COL, GameState.getInstance().gameId);
        values.put(PLAYER_LAST_SCORE_COL, 0);

        db.insert(PLAYER_TABLE_NAME, null, values);
    }

    public long insertGame(Game model) {
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GAME_NAME_COL, model.getName());
        values.put(GAME_MAX_SCORE_COL, model.getMaxScore());

        return db.insert(GAME_TABLE_NAME, null, values);
    }

    private int toInteger(Boolean value) {
        if (value) {
            return 1;
        }

        return 0;
    }


    public void updatePlayer(int id, String name) {
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PLAYER_NAME_COL, name);

        db.update(PLAYER_TABLE_NAME, values, "ID=?", new String[]{String.valueOf(id)});
    }

    public Game getGame(long id) {
        db = this.getReadableDatabase();
        Cursor cursor = null;
        Game game = new Game();

        db.beginTransaction();
        try {
            cursor = db.query(GAME_TABLE_NAME, null, "ID=?", new String[] { String.valueOf(id) }, null, null, null);
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            DateConverter converter = new DateConverter();

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                        game.setId(cursor.getInt(cursor.getColumnIndexOrThrow(GAME_ID_COL)));
                        game.setName(cursor.getString(cursor.getColumnIndexOrThrow(GAME_NAME_COL)));
                        game.setDuration(cursor.getString(cursor.getColumnIndexOrThrow(GAME_DURATION_COL)));

                        try {
                            Date date = format.parse(cursor.getString(cursor.getColumnIndexOrThrow(GAME_DATE_COL)));
                            cal.setTime(date);
                            converter.gregorianToPersian(cal.get(Calendar.YEAR), (cal.get(Calendar.MONTH)+1), cal.get(Calendar.DAY_OF_MONTH) + 1);
                            game.setDate(converter.getYear()+ "/" + converter.getMonth() + "/" + converter.getDay());
                        } catch (ParseException e) {
                            game.setDate(cursor.getString(cursor.getColumnIndexOrThrow(GAME_DATE_COL)));
                        }
                }
            }
        } finally {
            db.endTransaction();
            cursor.close();
        }

        return game;
    }

    public void updateGame(long id, String duration) {
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GAME_DURATION_COL, duration);

        db.update(GAME_TABLE_NAME, values, "ID=?", new String[]{String.valueOf(id)});
    }

    public void insertGameRound(int player_id, int score) {
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GAME_ROUNDS_GAME_ID_COL, GameState.getInstance().gameId);
        values.put(GAME_ROUNDS_PLAYER_ID_COL, player_id);
        values.put(GAME_ROUNDS_SCORE, score);

        db.insert(GAME_ROUNDS_TABLE_NAME, null, values);
    }

    public void deletePlayer(int id) {
        db = this.getWritableDatabase();
        db.delete(PLAYER_TABLE_NAME, "ID=?", new String[]{String.valueOf(id)});
    }

    public ArrayList<Player> getAllPlayers() {
        db = this.getReadableDatabase();
        Cursor cursor = null;
        ArrayList<Player> modelList = new ArrayList<>();

        db.beginTransaction();
        try {
            cursor = db.query(PLAYER_TABLE_NAME, null, PLAYER_GAME_ID_COL + "='" + GameState.getInstance().gameId + "'", null, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        Player player = new Player();
                        player.setId(cursor.getInt(cursor.getColumnIndexOrThrow(PLAYER_ID_COL)));
                        player.setName(cursor.getString(cursor.getColumnIndexOrThrow(PLAYER_NAME_COL)));
                        player.setGameId(cursor.getInt(cursor.getColumnIndexOrThrow(PLAYER_GAME_ID_COL)));
                        player.setLastScore(cursor.getInt(cursor.getColumnIndexOrThrow(PLAYER_LAST_SCORE_COL)));

                        modelList.add(player);
                    } while (cursor.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            cursor.close();
        }

        return modelList;
    }

    public List<Game> getAllGames() {
        db = this.getReadableDatabase();
        Cursor cursor = null;
        List<Game> modelList = new ArrayList<>();

        db.beginTransaction();
        try {
            cursor = db.query(GAME_TABLE_NAME, null, null, null, null, null, null);
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            DateConverter converter = new DateConverter();

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        Game game = new Game();
                        game.setId(cursor.getInt(cursor.getColumnIndexOrThrow(GAME_ID_COL)));
                        game.setName(cursor.getString(cursor.getColumnIndexOrThrow(GAME_NAME_COL)));

                        try {
                            Date date = format.parse(cursor.getString(cursor.getColumnIndexOrThrow(GAME_DATE_COL)));
                            cal.setTime(date);
                            converter.gregorianToPersian(cal.get(Calendar.YEAR), (cal.get(Calendar.MONTH)+1), cal.get(Calendar.DAY_OF_MONTH) + 1);
                            game.setDate(converter.getYear()+ "/" + converter.getMonth() + "/" + converter.getDay());
                        } catch (ParseException e) {
                            game.setDate(cursor.getString(cursor.getColumnIndexOrThrow(GAME_DATE_COL)));
                        }

                        modelList.add(game);
                    } while (cursor.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            cursor.close();
        }

        return modelList;
    }

    public LinkedHashMap<String, Integer> getFinalResult() {
        db = this.getReadableDatabase();
        Cursor cursor = null;
        LinkedHashMap<String, Integer> modelList = new LinkedHashMap<>();

        db.beginTransaction();
        try {
            cursor = db.rawQuery("SELECT PLAYER_TABLE.NAME, SUM(SCORE) AS TOTAL_SCORE FROM PLAYER_TABLE \n" +
                    " JOIN GAME_ROUNDS_TABLE ON PLAYER_TABLE.ID = GAME_ROUNDS_TABLE.PLAYER_ID \n" +
                    " WHERE GAME_ROUNDS_TABLE.GAME_ID = ? \n" +
                    " GROUP BY PLAYER_TABLE.ID\n" +
                    " ORDER BY TOTAL_SCORE ASC;", new String[]{String.valueOf(GameState.getInstance().gameId)});

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String playerName = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
                        int totalScore = cursor.getInt(cursor.getColumnIndexOrThrow("TOTAL_SCORE"));

                        modelList.put(playerName, totalScore);
                    } while (cursor.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            cursor.close();
        }

        return modelList;
    }

    public boolean maxScoresIsReached() {
        boolean maxScoreReached = false;

        if (GameState.getInstance().maxScore == 0) {
            return false;
        }

        db = this.getReadableDatabase();
        Cursor cursor = null;


        cursor = db.rawQuery("SELECT SUM(SCORE) AS TOTAL_SCORE FROM GAME_ROUNDS_TABLE \n" +
                " WHERE GAME_ID = " + GameState.getInstance().gameId + "\n" +
                " GROUP BY PLAYER_ID\n" +
                " HAVING TOTAL_SCORE >= " + GameState.getInstance().maxScore, null);

        if (cursor != null && cursor.getCount() > 0) {
            maxScoreReached = true;
        }

        cursor.close();

        return maxScoreReached;
    }

    public int getAllPlayerTotalScores(int playerId) {
        int playerTotalScores = 0;

        db = this.getReadableDatabase();
        Cursor cursor = null;

        cursor = db.rawQuery("SELECT SUM(SCORE) AS TOTAL_SCORE FROM GAME_ROUNDS_TABLE \n" +
                " WHERE PLAYER_ID = " + playerId, null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                playerTotalScores = cursor.getInt(cursor.getColumnIndexOrThrow("TOTAL_SCORE"));
            }
        }

        cursor.close();

        return playerTotalScores;
    }

    public void deleteGame(int gameId) {
        db = this.getWritableDatabase();
        db.delete(GAME_TABLE_NAME, "ID=?", new String[]{String.valueOf(gameId)});
        db.delete(GAME_ROUNDS_TABLE_NAME, "GAME_ID=?", new String[]{String.valueOf(gameId)});
        db.delete(PLAYER_TABLE_NAME, "GAME_ID=?", new String[]{String.valueOf(gameId)});
    }
}
