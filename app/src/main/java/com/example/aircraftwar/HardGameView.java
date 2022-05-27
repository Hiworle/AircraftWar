package com.example.aircraftwar;

import android.content.Context;

public class HardGameView extends GameView{
    public HardGameView(Context context) {
        super(context, GameView.HARD);
        enemyMaxNumber = 7;
        heroShootActionCycleDuration = 400;
        enemyShootActionCycleDuration = 480;
        eliteRate = 0.5;
        addEnemyCycleDuration = 400;
        harderWithTime = true;
        dropRate = 0.3;
    }
}
