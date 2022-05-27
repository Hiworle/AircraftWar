package com.example.aircraftwar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnEasy = (Button) findViewById(R.id.easyButton);
        Button btnNormal = (Button) findViewById(R.id.normalButton);
        Button btnHard = (Button) findViewById(R.id.hardButton);
        btnEasy.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("mode", GameView.EASY);
            startActivity(intent);
        });

        btnNormal.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("mode", GameView.NORMAL);
            startActivity(intent);
        });

        btnHard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("mode", GameView.HARD);
            startActivity(intent);
        });
    }


}
