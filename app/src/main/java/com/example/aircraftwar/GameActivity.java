package com.example.aircraftwar;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aircraftwar.aircraft.HeroAircraft;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = GameActivity.class.getSimpleName();
    private String mode;
    public static int screenWidth;
    public static int screenHeight;

    private GameView gameView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getScreenHW();

        mode = getIntent().getStringExtra("mode");
        switch (mode) {
            case GameView.EASY:
                gameView = new EasyGameView(this);
                break;
            case GameView.NORMAL:
                gameView = new NormalGameView(this);
                break;
            case GameView.HARD:
                gameView = new HardGameView(this);
                break;
            default:
        }

        setContentView(R.layout.activity_game);

        setContentView(gameView);

        gameView.action();
    }

    /**
     * 获得窗口的宽度和高度
     */
    private void getScreenHW(){
        // 定义 DisplayMetrics 对象
        DisplayMetrics dm = new DisplayMetrics();
        // 获得窗口属性
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        // 窗口的宽度
        screenWidth = dm.widthPixels;
        Log.i(TAG, "screenWidth: " + screenWidth);

        // 窗口的高度
        screenHeight = dm.heightPixels;
        Log.i(TAG, "getScreenHW: " + screenHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_MOVE) {
            double x = event.getX();
            double y = event.getY();
            HeroAircraft.getInstance().setLocation(x,y);
        }
        return false;
    }
}
