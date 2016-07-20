package com.genassembly.dotdashdot.sqllab;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        final InitDB db = new InitDB(this);

        TextView points = (TextView) findViewById(R.id.mostPoints);
        points.setText(db.getDB().getSumPoints().get(0));

        TextView gWon = (TextView) findViewById(R.id.mostGamesWon);
        String winner = SQLDB.getInstance(this).getMostWins();
        gWon.setText(winner);

        TextView longestLoseStreak = (TextView) findViewById(R.id.longestStreak);
        String loser = SQLDB.getInstance(this).getLosingStreak();
        longestLoseStreak.setText(loser);

        SQLDB.getInstance(this).getLosingStreak();


    }

}
