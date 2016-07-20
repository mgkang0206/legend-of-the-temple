package com.genassembly.dotdashdot.sqllab;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Mauve3 on 7/18/16.
 */
public class SQLDB extends SQLiteOpenHelper {

    private Context mContext;

    public void setContext(Context newContext) {
        mContext = newContext;
    }

    private interface  dbs {

        public String getTblName();
        public String getCreateString();
        public Cursor getData(SQLiteDatabase db);

        class teams implements dbs {
            public String getTblName(){
                return "teams";
            }
            public String getCreateString(){
                return "create table "
                        + getTblName() + "( _id integer primary key autoincrement, name text not null);";
            }
            public boolean insertRow (SQLiteDatabase db, String name)
            {
                ContentValues contentValues = new ContentValues();
                contentValues.put("name", name);
                db.insert(getTblName(), null, contentValues);
                return true;
            }
            public Cursor getData(SQLiteDatabase db){
                Cursor res =  db.rawQuery( "select * from " + getTblName(), null );
                return res;
            }
            public String getRow(Cursor row){
                return "_id: " + row.getInt(0) + " name: " + row.getString(1);
            }
        }
        class games implements dbs {
            public String getTblName(){
                return "games";
            }
            public String getCreateString(){
                return "create table "
                        + getTblName() + "( _id integer primary key autoincrement, team integer not null,"
                        + " points integer  not null, foundIdol integer not null, game integer not null );";
            }
            public boolean insertRow (SQLiteDatabase db, int team, int points, boolean foundIdol, int game)
            {
                ContentValues contentValues = new ContentValues();
                contentValues.put("team", team);
                contentValues.put("points", points);
                contentValues.put("foundIdol", foundIdol);
                contentValues.put("game", game);

                db.insert(getTblName(), null, contentValues);
                return true;
            }
            public Cursor getData(SQLiteDatabase db){
                Cursor res =  db.rawQuery( "select * from " + getTblName(), null );
                return res;
            }
            public Cursor getMostPoints(SQLiteDatabase db){
                dbs.teams teams = new dbs.teams();
                String query = "select " + teams.getTblName() + ".name, sum(" + getTblName() + ".points) as sum from " + getTblName() + ", "
                        + teams.getTblName() + " where (" + teams.getTblName() + "._id) - 1 = " + getTblName() + ".team group by " + teams.getTblName() + ".name order by sum desc";
                Log.i("SQL", query);

                Cursor res =  db.rawQuery( query, null );
                return res;
            }
            public String getRow(Cursor row){
                return "_id: " + row.getInt(0) + " team: " + row.getInt(1) + " points: " + row.getInt(2)
                        + " foundIdol: " + row.getInt(3) + " game: " + row.getInt(4);
            }
        }
    }

    public static final String DBNAME = "legends.db";

    public static final dbs.teams teams = new dbs.teams();
    public static final dbs.games games = new dbs.games();

    private static final int DATABASE_VERSION = 5;

    private static SQLDB instance;

    public static SQLDB getInstance(Context context) {
        if (instance == null) {
            instance = new SQLDB(context);
        }
        return instance;
    }

    //This creates the database
    private SQLDB(Context context) {
        super(context, DBNAME, null, DATABASE_VERSION);
    }

    //This makes the tables
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(teams.getCreateString());
        database.execSQL(games.getCreateString());
    }

    @Override
    public void
    onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLDB.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + teams.getTblName());
        db.execSQL("DROP TABLE IF EXISTS " + games.getTblName());
        onCreate(db);
    }

    public boolean insertTeam(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        teams.insertRow(db, name);
        return true;
    }

    public void printTeams(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = teams.getData(db);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Log.i("SQLDB: city",teams.getRow(cursor));
            cursor.moveToNext();
        }

        // make sure to close the cursor!!!
        cursor.close();
    }

    public ArrayList<String> getTeams(){

        ArrayList<String> teamList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = teams.getData(db);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Log.i("SQLDB: teams",teams.getRow(cursor));
            teamList.add(cursor.getString(1));
            cursor.moveToNext();
        }

        // make sure to close the cursor!!!
        cursor.close();
        return teamList;
    }

    public boolean insertGame(int team, int points, boolean foundIdol, int game) {
            SQLiteDatabase db = this.getWritableDatabase();
            games.insertRow(db, team, points, foundIdol, game);
            return true;
    }

    public boolean doesGameAlreadyExist(int game) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + games.getTblName() + " WHERE game = '" + game + "';", null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }

    public void printGames(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = games.getData(db);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Log.i("SQLDB: games",games.getRow(cursor));
            cursor.moveToNext();
        }

        // make sure to close the cursor!!!
        cursor.close();
    }

    public ArrayList<String> getSumPoints(){
        ArrayList<String> pointList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = games.getMostPoints(db);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            pointList.add(cursor.getString(0) + ":" + cursor.getInt(1));
            cursor.moveToNext();
        }

        // make sure to close the cursor!!!
        cursor.close();

        return pointList;
    }

    public void dropGames() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + teams.getTblName());
        db.execSQL("DROP TABLE IF EXISTS " + games.getTblName());
        onCreate(db);
        Toast.makeText(mContext, "Database Flushed!", Toast.LENGTH_LONG).show();
    }
// return int[]
    public String getMostWins() {
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<Integer> gamesPlayed = getGamesPlayed();

//        ArrayList<Integer> winners = new ArrayList<>();
//        for (int i : gamesPlayed) {
//            Cursor newCursor;
//            newCursor = db.rawQuery("SELECT team FROM games "
//                    + "WHERE game = '" + i + "' ORDER BY points DESC;", null);
//            newCursor.moveToFirst();
//            winners.add(newCursor.getInt(newCursor.getColumnIndexOrThrow("team")));
//            newCursor.close();
//        }

        ArrayList<Integer> winners = findWinners(gamesPlayed, db);

        int winInt = getWinningist(winners);
        String winString = getTeamName(winInt);
        return winString;

    }

    public ArrayList<Integer> findWinners(ArrayList<Integer> gamesPlayed, SQLiteDatabase db) {
        ArrayList<Integer> winners = new ArrayList<>();
        for (int i : gamesPlayed) {
            Cursor newCursor;
            newCursor = db.rawQuery("SELECT team FROM games "
                    + "WHERE game = '" + i + "' ORDER BY points DESC;", null);
            newCursor.moveToFirst();
            winners.add(newCursor.getInt(newCursor.getColumnIndexOrThrow("team")));
            newCursor.close();
        }
        Log.i("SEVTEST: ", "winners: " + winners);
        return winners;
    }

    public int getWinningist (ArrayList<Integer> arrayList) {
        HashMap<Integer, Integer> winningist = new HashMap<>();

        for (int i : arrayList) {
            winningist.put(i, 0);
        }

        for (int i : arrayList) {
            int tempint = winningist.get(i);
            winningist.put(i, tempint+1);
        }

        int mostWinningist = arrayList.get(0);
        int wins = 0;
        for (int i : arrayList) {
            int temptInt = winningist.get(i);
            if (temptInt > wins) {
                wins = temptInt;
                mostWinningist = i;
            }
        }
        return mostWinningist;
    }

    public String getTeamName(int myint) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor mycursor = db.rawQuery("SELECT name FROM teams WHERE _id = '" + (myint+1) + "';", null);
        mycursor.moveToFirst();
        String winner = mycursor.getString(mycursor.getColumnIndexOrThrow("name"));
        mycursor.close();
        return winner;
    }

    public ArrayList<Integer> getGamesPlayed() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor gameNumber = db.rawQuery("SELECT game FROM games GROUP BY game;", null);
        ArrayList<Integer> gamesPlayed = new ArrayList<>();

        gameNumber.moveToFirst();
        while (!gameNumber.isAfterLast()){
            int nextGame = gameNumber.getInt(gameNumber.getColumnIndexOrThrow("game"));
            gamesPlayed.add(nextGame);
            gameNumber.moveToNext();
        }
        gameNumber.close();
        Collections.sort(gamesPlayed);
        return gamesPlayed;
    }

    public String getLosingStreak() {
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<Integer> gamesPlayed = getGamesPlayed();
        ArrayList<Integer> winners = findWinners(gamesPlayed, db);

        int shitTeam = 0;
        int shitRecord = -1;
        HashMap<Integer, Integer> currentStreaks = new HashMap<>();

        for (int i = 0; i < 6; i++) {
            // streak starts at 0
            currentStreaks.put(i, 0);
        }

        for (int j : gamesPlayed){
            for (int i = 0; i < 6; i++) {
                int losses = currentStreaks.get(i);
                currentStreaks.put(i, (losses+1));
            }
//            int currentShit = ((currentStreaks.get(winners.get(j))) - 1);
//            int currentShit = ((currentStreaks.get(winners.get(gamesPlayed.indexOf(j)))));

            int currentGame = gamesPlayed.indexOf(j);
            int winnerOfCurrent = winners.get(currentGame);
//            int currentShit = currentStreaks.get(winnerOfCurrent);

            currentStreaks.put(winnerOfCurrent, 0);
            for (int k = 0; k < 6; k++) {
                if (currentStreaks.get(k) > shitRecord) {
                    shitRecord = currentStreaks.get(k);
                    shitTeam = k;
                }
            }


//            if (currentShit > shitRecord) {
//                shitRecord = currentShit;
//                shitTeam = winners.get(currentGame);
//            }
        }

        for (int i = 0; i < 6; i++) {
            int currentShit = (currentStreaks.get(i));
            if (currentShit > shitRecord) {
                shitRecord = currentShit;
                shitTeam = i;
            }
        }

        Log.i("SEVTEST: ", "shittest team: " + shitTeam);

        String shitTeamName = getTeamName(shitTeam);
        return shitTeamName;

    }


}
