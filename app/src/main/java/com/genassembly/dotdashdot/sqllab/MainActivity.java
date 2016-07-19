package com.genassembly.dotdashdot.sqllab;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/* TODO : Reset DB when button is clicked*/
public class MainActivity extends AppCompatActivity {

    private Context self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        self = this;

        final InitDB db = new InitDB(this);
        final ListView listy = (ListView) findViewById(R.id.mainList);
        SimpleAdapter adapty = new SimpleAdapter(this, db.getDB().getTeams());
        listy.setAdapter(adapty);

        final EditText gameEdit = (EditText) findViewById(R.id.gameNum);
        Button buttony = (Button) findViewById(R.id.submit);
        if (buttony != null) {
            buttony.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!gameEdit.getText().toString().isEmpty()) {
                        for (int i = 0; i < listy.getChildCount(); i++) {
                            View team = listy.getChildAt(i);

                            final EditText gameScore = (EditText) team.findViewById(R.id.editScore);
                            db.getDB().insertGame(i, Integer.valueOf(gameScore.getText().toString()), false, Integer.valueOf(gameEdit.getText().toString()));
                            gameScore.setText("");
                        }
                        Log.i("Main", "Printing...");
                        gameEdit.setText("");
                        db.getDB().printGames();
                    } else {
                        Toast.makeText(self, "Game number not inserted!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        Button stats = (Button) findViewById(R.id.stats);
        if (stats != null) {
            stats.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(self, StatsActivity.class);
                    startActivity(i);
                }
            });
        }
    }
}
