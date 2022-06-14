package com.example.aircraftwar;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aircraftwar.aircraft.HeroAircraft;
import com.example.aircraftwar.application.MusicService;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = GameActivity.class.getSimpleName();
    private String mode;
    public static int screenWidth;
    public static int screenHeight;

    public MusicService.MyBinder myBinder;
    private Connect conn;
    private Intent intent;

    private GameView gameView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getScreenHW();

        //绑定
        Log.i("music demo","===bind service===");
        conn = new Connect();
        intent = new Intent(this,MusicService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

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

        //GameView.ifMusicOn = getIntent().getBooleanExtra("music", true);

        startService(intent);

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

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i("music demo","stop");
        unbindService(conn);
    }

//    public void playBgm(String bgm) {
//        myBinder.playBgm(bgm);
//    }
//
//    public void playBombExplosion() {
//        myBinder.playBombExplosion();
//    }
//
//    public void playBullet() {
//        myBinder.playBullet();
//    }
//
//    public void playBulletHit() {
//        myBinder.playBulletHit();
//    }
//
//    public void playGetSupply() {
//        myBinder.playGetSupply();
//    }
//
//    public void playGameOver() {
//        myBinder.playGameOver();
//    }

    class Connect implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            Log.i("music service","===Service Connnected===");
            myBinder = (MusicService.MyBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }
}
