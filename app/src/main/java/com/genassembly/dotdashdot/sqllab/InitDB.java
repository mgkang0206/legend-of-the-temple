package com.genassembly.dotdashdot.sqllab;

import android.content.Context;

/**
 * Created by Mauve3 on 7/18/16.
 */
public class InitDB {

    private SQLDB db;

    public SQLDB getDB(){
        return db;
    }

    InitDB(Context self) {
        db = new SQLDB(self);

        if (db.getTeams().size() == 0) {
            db.insertTeam("Red Jaguars");
            db.insertTeam("Blue Barracudas");
            db.insertTeam("Green Monkeys");
            db.insertTeam("Orange Iguanas");
            db.insertTeam("Purple Parrots");
            db.insertTeam("Silver Snakes");
        }
    }
}
