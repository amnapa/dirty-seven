package com.mismattia.dirtyseven.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.mismattia.dirtyseven.model.Game;
import com.mismattia.dirtyseven.model.GamePlayer;
import com.mismattia.dirtyseven.model.GameResult;
import com.mismattia.dirtyseven.model.Player;
import com.mismattia.dirtyseven.singleton.GameState;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SEVEN_DATABASE";
    private static final int DATABASE_VERSION = 1;

    private static final String GAME_TABLE_NAME = "GAME_TABLE";
    private static final String GAME_ID_COL = "ID";
    private static final String GAME_DURATION_COL = "DURATION";
    private static final String GAME_ROUNDS_COL = "ROUNDS";
    private static final String GAME_DATE_COL = "DATE";
    private static final String GAME_NAME_COL = "NAME";
    private static final String GAME_MAX_SCORE_COL = "MAX_SCORE";

    private static final String PLAYER_TABLE_NAME = "PLAYER_TABLE";
    private static final String PLAYER_ID_COL = "ID";
    private static final String PLAYER_NAME_COL = "NAME";

    private static final String GAME_PLAYER_TABLE_NAME = "GAME_PLAYER_TABLE";
    private static final String GAME_PLAYER_ID_COL = "ID";
    private static final String GAME_PLAYER_PLAYER_ID_COL = "PLAYER_ID";
    private static final String GAME_PLAYER_GAME_ID_COL = "GAME_ID";

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
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GAME_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, MAX_SCORE INTEGER DEFAULT 0, ROUNDS INTEGER DEFAULT 0, DURATION DEFAULT '', DATE DATETIME DEFAULT CURRENT_TIMESTAMP)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + PLAYER_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GAME_PLAYER_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, GAME_ID INTEGER, PLAYER_ID INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GAME_ROUNDS_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, GAME_ID INTEGER, PLAYER_ID INTEGER, SCORE INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insertPlayer(Player player) {
        db = this.getWritableDatabase();

        ContentValues playerValues = new ContentValues();
        playerValues.put(PLAYER_NAME_COL, player.getName());

        long playerId = db.insert(PLAYER_TABLE_NAME, null, playerValues);

        player.setId((int) playerId);
        insertGamePlayer(player);
    }

    public void insertGamePlayer(Player player) {
        ContentValues gamePlayerValues = new ContentValues();
        gamePlayerValues.put(GAME_PLAYER_GAME_ID_COL, GameState.getInstance().gameId);
        gamePlayerValues.put(GAME_PLAYER_PLAYER_ID_COL, player.getId());

        db.insert(GAME_PLAYER_TABLE_NAME, null, gamePlayerValues);
    }


    public long insertGame(Game model) {
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GAME_NAME_COL, model.getName());
        values.put(GAME_MAX_SCORE_COL, model.getMaxScore());

        return db.insert(GAME_TABLE_NAME, null, values);
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
                        game.setRounds(cursor.getInt(cursor.getColumnIndexOrThrow(GAME_ROUNDS_COL)));

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

    public void updateGame(long id, String duration, int rounds) {
        db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GAME_DURATION_COL, duration);
        values.put(GAME_DURATION_COL, duration);
        values.put(GAME_ROUNDS_COL, rounds);

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
        db.delete(GAME_PLAYER_TABLE_NAME, "ID=?", new String[]{String.valueOf(id)});
    }

    public ArrayList<GamePlayer> getAllGamePlayers() {
        db = this.getReadableDatabase();
        Cursor cursor = null;
        ArrayList<GamePlayer> modelList = new ArrayList<>();

        db.beginTransaction();
        try {
            final String MY_QUERY = "SELECT GP.ID ID, GP.PLAYER_ID AS PLAYER_ID, P.NAME NAME FROM PLAYER_TABLE P INNER JOIN GAME_PLAYER_TABLE GP ON P.ID = GP.PLAYER_ID WHERE GP.GAME_ID=?";
            cursor = db.rawQuery(MY_QUERY, new String[]{String.valueOf(GameState.getInstance().gameId)});

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        GamePlayer player = new GamePlayer();
                        player.setId(cursor.getInt(cursor.getColumnIndexOrThrow(GAME_PLAYER_ID_COL)));
                        player.setPlayerId(cursor.getInt(cursor.getColumnIndexOrThrow(GAME_PLAYER_PLAYER_ID_COL)));
                        player.setName(cursor.getString(cursor.getColumnIndexOrThrow(PLAYER_NAME_COL)));

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

    public ArrayList<Player> getAllPlayers() {
        db = this.getReadableDatabase();
        Cursor cursor = null;
        ArrayList<Player> modelList = new ArrayList<>();

        db.beginTransaction();
        try {
            cursor = db.query(PLAYER_TABLE_NAME, null, null, null, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        Player player = new Player();
                        player.setId(cursor.getInt(cursor.getColumnIndexOrThrow(PLAYER_ID_COL)));
                        player.setName(cursor.getString(cursor.getColumnIndexOrThrow(PLAYER_NAME_COL)));

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

    public List<GameResult> getFinalResult() {
        db = this.getReadableDatabase();
        Cursor cursor = null;
        List<GameResult> modelList = new ArrayList<>();

        db.beginTransaction();
        try {
            cursor = db.rawQuery("SELECT PLAYER_ID, NAME,  SUM(SCORE) AS TOTAL_SCORE FROM PLAYER_TABLE \n" +
                    " JOIN GAME_ROUNDS_TABLE ON PLAYER_TABLE.ID = GAME_ROUNDS_TABLE.PLAYER_ID \n" +
                    " WHERE GAME_ROUNDS_TABLE.GAME_ID = ? \n" +
                    " GROUP BY PLAYER_TABLE.ID\n" +
                    " ORDER BY TOTAL_SCORE ASC;", new String[]{String.valueOf(GameState.getInstance().gameId)});

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        GameResult gameResult = new GameResult();
                        gameResult.setPlayerId(cursor.getInt(cursor.getColumnIndexOrThrow("PLAYER_ID")));
                        gameResult.setPlayerName(cursor.getString(cursor.getColumnIndexOrThrow("NAME")));
                        gameResult.setScore(cursor.getInt(cursor.getColumnIndexOrThrow("TOTAL_SCORE")));

                        modelList.add(gameResult);
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
        Cursor cursor;


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
                " WHERE PLAYER_ID = " + playerId + " AND GAME_ID = " + GameState.getInstance().gameId, null);

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
        db.delete(GAME_PLAYER_TABLE_NAME, "GAME_ID=?", new String[]{String.valueOf(gameId)});
    }

    public List<Integer> getPlayerScores(int playerId) {
        db = this.getWritableDatabase();

        Cursor cursor = null;
        List<Integer> scores = new ArrayList<>();
        int score;

        db.beginTransaction();
        try {
            cursor = db.rawQuery("SELECT SCORE FROM GAME_ROUNDS_TABLE \n" +
                    " WHERE GAME_ID = ? \n" +
                    " AND PLAYER_ID = ? \n", new String[]{String.valueOf(GameState.getInstance().gameId), String.valueOf(playerId)});

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        score = cursor.getInt(cursor.getColumnIndexOrThrow("SCORE"));

                        scores.add(score);
                    } while (cursor.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            cursor.close();
        }

        return scores;
    }

    public Player getPlayer(int playerId) {
        db = this.getReadableDatabase();
        Cursor cursor = null;
        Player player = new Player();

        db.beginTransaction();
        try {
            cursor = db.query(PLAYER_TABLE_NAME, null, "ID=?", new String[] { String.valueOf(playerId) }, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    player.setName(cursor.getString(cursor.getColumnIndexOrThrow(PLAYER_NAME_COL)));
                }
            }
        } finally {
            db.endTransaction();
            cursor.close();
        }

        return player;
    }


}
