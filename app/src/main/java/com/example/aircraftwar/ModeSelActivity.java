package com.example.aircraftwar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aircraftwar.online.MatchingRoomActivity;


public class ModeSelActivity extends AppCompatActivity {

    private Button bt_SAM;
    private Button bt_OM;
    /**当前游戏用户名*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_sel);

        bt_SAM = findViewById(R.id.bt_SAM);
        bt_OM = findViewById(R.id.bt_OM);


        bt_SAM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                /**开始单机模式*/
                intent.setClass(ModeSelActivity.this, SingleModeSelActivity.class);
                startActivity(intent);
            }
        });

        bt_OM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                /**开始联机模式*/

                intent.setClass(ModeSelActivity.this, MatchingRoomActivity.class);
                startActivity(intent);
            }
        });
    }
}