package com.genassembly.dotdashdot.sqllab;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mauve3 on 7/7/16.
 */
public class SimpleAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final ArrayList<String> teams;

    public SimpleAdapter(Context context, ArrayList<String> teams) {
        //super();
        inflater = LayoutInflater.from(context);
        this.teams = teams;
    }

    @Override
    public int getCount() {
        return teams.size();
    }

    @Override
    public Object getItem(int position) {
        return teams.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d("Postion: " , "" + position);

        View v = convertView;
        TextView name;
        EditText score;

        if (v == null) {

            v = inflater.inflate(R.layout.layout_team, parent, false);
        }

        name = (TextView) v.findViewById(R.id.teamName);
        score = (EditText) v.findViewById(R.id.editScore);

        name.setText(String.valueOf(teams.get(position)));

        return v;
    }
}
