package com.example.aircraftwar.prop;

import com.example.aircraftwar.aircraft.BossEnemy;
import com.example.aircraftwar.aircraft.EliteEnemy;
import com.example.aircraftwar.aircraft.HeroAircraft;
import com.example.aircraftwar.aircraft.MobEnemy;
import com.example.aircraftwar.basic.AbstractFlyingObject;
import com.example.aircraftwar.bullet.EnemyBullet;

import java.util.ArrayList;
import java.util.List;

/**
 * 清屏道具
 * @author yanghaopeng
 */
public class BombProp extends AbstractProp{

    private final List<AbstractFlyingObject> flyingObjects;

    public BombProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
        flyingObjects = new ArrayList<>();
    }

    /**
     * 添加订阅者（待摧毁的物品）
     * @param obj 订阅者
     */
    public void addObject(AbstractFlyingObject obj) {
        flyingObjects.add(obj);
    }

    /**
     * 按照以下规则炸毁订阅者
     *  对于敌机子弹：直接炸毁
     *  对于普通敌机和精英敌机：炸毁
     *  对于boss机：减少100血量
     *  ！不摧毁英雄机及其子弹！
     * @return score 增加的分数
     */
    private int bombAll(){
        int score = 0;

        for(AbstractFlyingObject object : flyingObjects) {
            if(object instanceof EnemyBullet) {
                object.vanish();
            } else if(object instanceof EliteEnemy || object instanceof MobEnemy) {
                object.vanish();
                score += 10;
            } else if(object instanceof BossEnemy) {
                BossEnemy enemy = (BossEnemy) object;
                enemy.decreaseHp(100);
                if(enemy.notValid()){
                    score += 10;
                }
            }
        }
        return score;
    }

    @Override
    public int work(HeroAircraft heroAircraft) {
        System.out.println("BombSupply Active!");

        // 炸毁一切订阅者，获得炸毁后的得分
        int score = bombAll();

        // 清屏道具音效
//        MusicManager.playSoundEffect("src/videos/bomb_explosion.wav");

        return score;
    }

}
