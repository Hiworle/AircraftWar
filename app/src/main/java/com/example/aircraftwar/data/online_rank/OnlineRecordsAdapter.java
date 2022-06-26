package com.example.aircraftwar.data.online_rank;

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

public class OnlineRecordsAdapter extends ArrayAdapter<Player> {
    public OnlineRecordsAdapter(@NonNull Context context, int resource, @NonNull List<Player> objects) {
        super(context, resource, objects);
    }
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertVIew, @NonNull ViewGroup parent){
        Log.i("getView", "action");
        Player player = getItem(position);
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(R.layout.online_records_item, parent, false);
        TextView ranking = view.findViewById(R.id.ranking);
        TextView yourName = view.findViewById(R.id.your_name);
        TextView otherName = view.findViewById(R.id.other_name);
        TextView yourScore = view.findViewById(R.id.your_score);
        TextView otherScore = view.findViewById(R.id.other_score);
        TextView totalScore = view.findViewById(R.id.total_score);

        ranking.setText(player.getRanking()+"");
        yourName.setText(player.getYourName());
        otherName.setText(player.getOtherName());
        yourScore.setText(player.getYourScore()+"");
        otherScore.setText(player.getOtherScore()+"");
        totalScore.setText(player.getScore()+"");

        return view;
    }
}
