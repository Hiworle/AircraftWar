package com.example.aircraftwar.data.rank;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.aircraftwar.R;

import java.util.List;

public class RecordsAdapter extends ArrayAdapter<Record> {
    public RecordsAdapter(@NonNull Context context, int resource, @NonNull List<Record> objects) {
        super(context, resource, objects);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertVIew, @NonNull ViewGroup parent){
        Log.i("getView", "action");
        Record record = getItem(position);
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(R.layout.records_item, parent, false);
        TextView playerRank = view.findViewById(R.id.rank);
        TextView playerName = view.findViewById(R.id.name);
        TextView playerScore = view.findViewById(R.id.score);
        TextView playerDate = view.findViewById(R.id.date);

        playerRank.setText(record.getRanking()+"");
        playerName.setText(record.getName()+"");
        playerScore.setText(record.getScore()+"");
        playerDate.setText(record.getDate()+"");

        return view;
    }
}
