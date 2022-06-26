package com.example.aircraftwar.online;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import com.example.aircraftwar.R;
import com.example.aircraftwar.aircraft.HeroAircraft;
import com.example.aircraftwar.application.MusicService;

public class OnlineGameActivity extends Activity {
    private static final String TAG = OnlineGameActivity.class.getSimpleName();
    public static String playerName;
    public static int screenWidth;
    public static int screenHeight;

    public MusicService.MyBinder myBinder;
    private OnlineGameActivity.Connect conn;
    private Intent intent;

    private OnlineGameView gameView;

    // 子按钮列表
//    private List<ImageButton> buttonItems = new ArrayList<ImageButton>(3);

    //    private ImageButton skill_btn,btn_hp,btn_bullet,btn_bomb,btn_shield;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getScreenHW();

        //绑定
        Log.i("music demo","===bind service===");
        conn = new Connect();
        intent = new Intent(this,MusicService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);


        //GameView.ifMusicOn = getIntent().getBooleanExtra("music", true);

        startService(intent);

        setContentView(R.layout.activity_game);

        gameView = new OnlineGameView(this);
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