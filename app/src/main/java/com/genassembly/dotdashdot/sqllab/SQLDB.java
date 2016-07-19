package com.genassembly.dotdashdot.sqllab;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.w3c.dom.Comment;

import java.util.ArrayList;

/**
 * Created by Mauve3 on 7/18/16.
 */
public class SQLDB extends SQLiteOpenHelper {

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
                        + getTblName() + "( " + "_id integer primary key autoincrement, team integer not null,"
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
                        + teams.getTblName() + " where " + teams.getTblName() + "._id = " + getTblName() + ".team group by " + teams.getTblName() + ".name order by sum desc";
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

    //This creates the database
    public SQLDB(Context context) {
        super(context, DBNAME, null, DATABASE_VERSION);
    }

    //This makes the tables
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(teams.getCreateString());
        database.execSQL(games.getCreateString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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
        SQLiteDatabase db = this.getReadableDatabase();
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
        /* TODO : Check to make sure the game doesn't already exist in the database*/
        SQLiteDatabase db = this.getWritableDatabase();
        games.insertRow(db, team, points, foundIdol, game);
        return true;
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

}