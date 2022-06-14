package com.example.aircraftwar.application;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.aircraftwar.GameView;
import com.example.aircraftwar.R;

import java.util.HashMap;

public class MusicService extends Service {
    private static final String TAG = "MusicService";
    private HashMap<Integer, Integer> soundID = new HashMap<Integer, Integer>();
    private SoundPool mSoundPool;
    private MediaPlayer bossBgmPlayer;
    private MediaPlayer bgmPlayer;

    public MusicService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "==== MusicService onCreate ===");
        mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        soundID.put(1, mSoundPool.load(this, R.raw.bomb_explosion, 1));
        soundID.put(2, mSoundPool.load(this, R.raw.bullet, 1));
        soundID.put(3, mSoundPool.load(this, R.raw.bullet_hit, 1));
        soundID.put(4, mSoundPool.load(this, R.raw.get_supply, 1));
        soundID.put(5, mSoundPool.load(this, R.raw.game_over, 1));
        bgmPlayer = MediaPlayer.create(this, R.raw.bgm);
        bossBgmPlayer = MediaPlayer.create(this, R.raw.bgm_boss);
        bgmPlayer.setLooping(true);
        bossBgmPlayer.setLooping(true);
        if (GameView.ifMusicOn) {
            bgmPlayer.start();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
//        public void playBGM() {
//            mSoundPool.play(soundID.get(1), 1, 1, 0, 1, 1);
//        }
//
//        public void playBgmBoss() {
//            mSoundPool.play(soundID.get(2), 1, 1, 0, 1, 1);
//        }
//        public void playBgm(String bgm) {
//            playMusic(bgm);
//        }
//
        public void playBombExplosion() {
            if (GameView.ifMusicOn) {
                mSoundPool.play(soundID.get(1), 1, 1, 0, 0, 1);
            }
        }

        public void playBullet() {
            if (GameView.ifMusicOn) {
                mSoundPool.play(soundID.get(2), 1, 1, 0, 0, 1);
            }
        }

        public void playBulletHit() {
            if (GameView.ifMusicOn) {
                mSoundPool.play(soundID.get(3), 1, 1, 0, 0, 1);
            }
        }

        public void playGetSupply() {
            if (GameView.ifMusicOn) {
                mSoundPool.play(soundID.get(4), 1, 1, 0, 0, 1);
            }
        }

        public void playGameOver() {
            if (GameView.ifMusicOn) {
                mSoundPool.play(soundID.get(5), 1, 1, 0, 0, 1);
                stopMusic();
            }
        }

        public void playBossBgm() {
            if (GameView.ifMusicOn) {
                bgmPlayer.pause();
                bossBgmPlayer.start();
            }
        }

        public void playBgm() {
            if (GameView.ifMusicOn) {
                bossBgmPlayer.pause();
                bgmPlayer.start();
            }
        }
//        public void play(int id) {
//            if (GameView.ifMUsicOn) {
//                mSoundPool.play(soundID.get(id), 1, 1, 0, 0, 1);
//            }
//        }
    }

//
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
//    }

    /**
     * 播放BGM，循环播放
     */


    /**
     * 停止播放BGM
     */
    private void stopMusic() {
        if (bgmPlayer != null) {
            bgmPlayer.stop();
            bgmPlayer.reset();
            bgmPlayer.release();
            bgmPlayer = null;
        }
        if (bossBgmPlayer != null) {
            bossBgmPlayer.stop();
            this.bossBgmPlayer.reset();
            this.bossBgmPlayer.release();
            this.bossBgmPlayer = null;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMusic();
    }


}
