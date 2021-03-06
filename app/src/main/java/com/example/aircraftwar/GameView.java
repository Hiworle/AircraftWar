package com.example.aircraftwar;

import static com.example.aircraftwar.LoginActivity.name_;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.aircraftwar.aircraft.AbstractAircraft;
import com.example.aircraftwar.aircraft.HeroAircraft;
import com.example.aircraftwar.application.ImageManager;
import com.example.aircraftwar.basic.AbstractFlyingObject;
import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.data.rank.RecordDaoImpl;
import com.example.aircraftwar.factory.AbstractEnemyFactory;
import com.example.aircraftwar.factory.BossEnemyFactory;
import com.example.aircraftwar.factory.EliteEnemyFactory;
import com.example.aircraftwar.factory.MobEnemyFactory;
import com.example.aircraftwar.prop.AbstractProp;
import com.example.aircraftwar.prop.BombProp;
import com.example.aircraftwar.data.rank.RankPage;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class GameView extends SurfaceView{

    private static final String TAG = GameView.class.getSimpleName();
    private Context context ;

    private int screenWidth = GameActivity.screenWidth;
    private int screenHeight = GameActivity.screenHeight;
    private GameActivity gameActivity;

    private Canvas canvas;
    private Paint mPaint;
    private SurfaceHolder mSurfaceHolder;

    public static boolean ifMusicOn = true;

    public static final String HARD = "HARD";
    public static final String NORMAL = "NORMAL";
    public static final String EASY = "EASY";
    public static String mode;

    private int backGroundTop = 0;

    /**
     * ?????????????????????boss????????????
     */
    private int nextBossScore = 50;
    private int nextBossDuration = 50;
    private boolean bossBattle = false;
    private int bossHp = 400;

    /**
     * Scheduled ??????????????????????????????
     */
    private final ScheduledExecutorService executorService;

    /**
     * ????????????(ms)?????????????????????
     */
    private final int timeInterval = 40;

    protected final HeroAircraft heroAircraft;
    protected final List<AbstractAircraft> enemyAircrafts;
    protected final List<BaseBullet> heroBullets;
    protected final List<BaseBullet> enemyBullets;
    protected final List<AbstractProp> props;

    protected double enemyMaxNumber = 5;
    protected double eliteRate = 0.3;
    protected double dropRate = 0.3;
    protected boolean harderWithTime = true;
    protected int nextDifficultyDuration = 10_000;

    public boolean gameOverFlag = false;
    public static int score = 0;
    private int time = 0;
    /**
     * ?????????????????????ms)
     * ???????????????????????????
     */
    protected int addEnemyCycleDuration = 600;
    protected int addEnemyCycleTime = 0;
    /**
     * ?????????????????????ms)
     * ??????????????????
     */
    protected int enemyShootActionCycleDuration = 600;
    protected int enemyShootActionCycleTime = 0;
    /**
     * ?????????????????????ms)
     * ??????????????????
     */
    protected int heroShootActionCycleDuration = 600;
    protected int heroShootActionCycleTime = 0;
    /**
     * ?????????ms)
     * ?????????????????????????????????????????????
     */
    private int cycleDuration = 600;
    private int cycleTime = 0;

    public GameView(Context context, String mode){
        super(context);
        this.context = context;
        this.gameActivity = (GameActivity) context;

        this.context = context;
        this.gameActivity = (GameActivity) context;

        mPaint = new Paint();
        mSurfaceHolder = getHolder();
        loadingImg();

        // ????????????
        GameView.mode = mode;
        switch (mode) {
            case EASY:
                this.setBackgroundImage("bg");
                break;
            case NORMAL:
                this.setBackgroundImage("bg2");
                break;
            case HARD:
                this.setBackgroundImage("bg3");
                break;
            default:
        }

        // ???????????????
        heroAircraft = HeroAircraft.getInstance();

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();

        //Scheduled ????????????????????????????????????
        ThreadFactory gameThread = r -> {
            Thread t = new Thread(r);
            t.setName("game thread");
            return t;
        };
        executorService = new ScheduledThreadPoolExecutor(1, gameThread);
    }

    public GameView(Context context){
        this(context, NORMAL);
    }

    /**
     * ???????????????????????????????????????
     */
    public void action() {


        //???????????????????????????bgm
//        MusicService.MyBinder binder = gameActivity.myBinder;
//        binder.playBgm();


        // ???????????????????????????????????????????????????????????????????????????
        Runnable task = () -> {

            time += timeInterval;
            adjustDifficulty();

            // ?????????????????????????????????
            if (addEnemyCycleJudge()) {
                Log.i(TAG,time+"");
                // ???????????????
                addEnemy();
            }

            if(enemyShootActionCycleJudge()) {
                // ??????????????????
                enemyShootAction();
            }

            if(heroShootActionCycleJudge()) {
                // ?????????????????????
                heroShootAction();
            }

            // ????????????
            bulletsMoveAction();
            // ????????????
            aircraftsMoveAction();
            // ????????????
            propsMoveAction();

            // ????????????
            crashCheckAction();

            // ?????????
            postProcessAction();

            //????????????????????????
            draw();

            // ??????????????????
            if (heroAircraft.getHp() <= 0) {
                // ????????????
                executorService.shutdown();
                gameOverFlag = true;
                Log.i(TAG, "Game over!");

                // ??????????????????
                gameActivity.myBinder.playGameOver();

                // ????????????bgm
//                gameActivity.stopBgm();
                startRankPage();
                // ????????????
//                String name = JOptionPane.showInputDialog(this, "???????????????????????????","????????????",
//                        JOptionPane.QUESTION_MESSAGE);
//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setMessage(R.string.di)

//                if (name != null) {
//                    // ??????????????????
//                    Scoring scoring = new Scoring(name,score, LocalDateTime.now());
//                    ScoringDaoImpl scoringDao = new ScoringDaoImpl(mode);
//                    scoringDao.doAdd(scoring);
//
//                    // ??????????????????
//                    System.out.println("#############################################");
//                    System.out.println(scoringDao.toString());
//                    System.out.println("#############################################");
//                }

//                synchronized (GameActivity.MAINWINDOW_LOCK) {
//                    // ??????????????????????????????
//                    GameActivity.MAINWINDOW_LOCK.notify();
//                }
            }

        };

        /*
         * ?????????????????????????????????
         * ??????????????????????????????????????????????????????????????????????????????????????????
         */
        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);

    }

    /**
     * ?????????????????????????????????
     *  boss???????????????????????????
     *  ???boss???????????????????????????????????????boss??????
     *  ????????????????????????????????????????????????????????????????????????????????????eliteRate????????????????????????enemyMaxNumber
     */
    protected void addEnemy() {
        AbstractEnemyFactory enemyFactory;

        // boss??????
        if(bossBattle) {
            // boss????????????
            if(enemyAircrafts.isEmpty()){
                bossBattle = false;

                // boss????????????bgm??????
                gameActivity.myBinder.playBgm();
            }

        } else {
            if(score >= nextBossScore){
                bossBattle = true;
                nextBossScore += nextBossDuration;

                // ????????????boss???????????????
                if(mode.equals(HARD)) {
                    bossHp += 250;
                    enemyFactory = new BossEnemyFactory(bossHp);
                } else {
                    enemyFactory = new BossEnemyFactory();
                }

                enemyAircrafts.add(enemyFactory.createEnemy());
                // boss????????????bgm??????
                gameActivity.myBinder.playBossBgm();

            } else if(enemyAircrafts.size() < enemyMaxNumber){
                if(Math.random() < eliteRate){
                    // ??????????????????
                    enemyFactory = new EliteEnemyFactory();
                } else {
                    // ??????????????????
                    enemyFactory = new MobEnemyFactory();
                }
                enemyAircrafts.add(enemyFactory.createEnemy());
            }
        }
    }

    /**
     * ?????????????????????
     */
    private void adjustDifficulty(){
        if(!harderWithTime) {
            return;
        }
        if(time >= nextDifficultyDuration) {
            // ??????????????????????????????????????????????????????????????????????????????????????????10, 20, 40, 80...???
            nextDifficultyDuration *= 2;
            System.out.println("********************** ???????????? **********************");
            enemyMaxNumber += 0.5;
            System.out.println("| ?????????????????????: " + enemyMaxNumber);
            eliteRate += 0.05;
            System.out.println("| ???????????????: " + eliteRate);
            addEnemyCycleDuration -= 80;
            System.out.println("| ??????????????????: " + addEnemyCycleDuration);
            enemyShootActionCycleDuration -= 40;
            System.out.println("| ??????????????????: " + enemyShootActionCycleDuration);
            nextBossDuration -= 10;
            System.out.println("| Boss???????????????????????????: " + nextBossDuration);
            dropRate -= 0.01;
            System.out.println("| ???????????????: " + dropRate);
            System.out.println("*****************************************************");
        }

    }

    //***********************
    //      Action ?????????
    //***********************

    private boolean addEnemyCycleJudge() {
        addEnemyCycleTime += timeInterval;
        if (addEnemyCycleTime >= addEnemyCycleDuration && addEnemyCycleTime - timeInterval < addEnemyCycleTime) {
            // ?????????????????????
            addEnemyCycleTime %= addEnemyCycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private boolean enemyShootActionCycleJudge() {
        enemyShootActionCycleTime += timeInterval;
        if (enemyShootActionCycleTime >= enemyShootActionCycleDuration && enemyShootActionCycleTime - timeInterval < enemyShootActionCycleTime) {
            // ?????????????????????
            enemyShootActionCycleTime %= enemyShootActionCycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private boolean heroShootActionCycleJudge() {
        heroShootActionCycleTime += timeInterval;
        if (heroShootActionCycleTime >= heroShootActionCycleDuration && heroShootActionCycleTime - timeInterval < heroShootActionCycleTime) {
            // ?????????????????????
            heroShootActionCycleTime %= heroShootActionCycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private void enemyShootAction() {
        // ????????????
        for(AbstractAircraft enemyAircraft : enemyAircrafts ){
            enemyBullets.addAll(enemyAircraft.shoot());
        }
    }

    private void heroShootAction(){
        // ????????????
        heroBullets.addAll(heroAircraft.shoot());

        // ????????????????????????
        gameActivity.myBinder.playBullet();
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    private void propsMoveAction() {
        for(AbstractProp prop : props) {
            prop.forward();
        }
    }

    /**
     * ???????????????
     * 1. ??????????????????
     * 2. ????????????/????????????
     * 3. ??????????????????
     */
    private void crashCheckAction() {
        // ????????????????????????
        for(BaseBullet bullet : enemyBullets){
            if(bullet.notValid()) {
                continue;
            }
            if(heroAircraft.crash(bullet)){
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }


        // ????????????????????????????????????????????????????????????
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // ????????????????????????????????????????????????
                    // ???????????????????????????????????????????????????
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // ??????????????????????????????
                    // ???????????????????????????
                    enemyAircraft.decreaseHp(bullet.getPower());

                    // ????????????????????????
                    gameActivity.myBinder.playBulletHit();

                    bullet.vanish();
                    if (enemyAircraft.notValid()) {
                        // ?????????????????????????????????

                        if(Math.random() < dropRate){
                            props.add(enemyAircraft.dropProp());
                        }

                        // ??????????????????+10???
                        score += 10;
                    }
                }
                // ????????? ??? ?????? ??????????????????
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }

            }
        }

        // ?????????????????????????????????
        for(AbstractProp prop : props){
            if(prop.crash(heroAircraft) || heroAircraft.crash(prop)){
                // ??????????????????
                gameActivity.myBinder.playGetSupply();

                // ????????????????????????????????????????????????
                if(prop instanceof BombProp) {
                    //????????????
                    gameActivity.myBinder.playBombExplosion();
                    BombProp bombProp = (BombProp) prop;

                    // ??????????????????
                    for (BaseBullet bullet : enemyBullets) {
                        bombProp.addObject(bullet);
                    }

                    // ????????????
                    for (AbstractAircraft enemy : enemyAircrafts) {
                        bombProp.addObject(enemy);
                    }
                }

                // ?????????????????????????????????
                score += prop.work(heroAircraft);
                prop.vanish();
            }
        }
    }

    /**
     * ????????????
     * 1. ?????????????????????
     * 2. ?????????????????????
     * 3. ?????????????????????
     * <p>
     * ????????????????????????????????????????????????
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }

    //***********************
    //      Draw ?????????
    //***********************

    /**
     * ????????????
     */
    public void loadingImg() {
        // ????????????????????????
        ImageManager.BACKGROUND_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.bg);

        ImageManager.HERO_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.hero);
        ImageManager.MOB_ENEMY_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.mob);
        ImageManager.ELITE_ENEMY_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.elite);
        ImageManager.BOSS_ENEMY_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.boss);

        ImageManager.HERO_BULLET_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.bullet_hero);
        ImageManager.ENEMY_BULLET_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.bullet_enemy);

        ImageManager.BOMB_PROP_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.prop_bomb);
        ImageManager.FIRE_PROP_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.prop_bullet);
        ImageManager.BLOOD_PROP_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.prop_blood);

        ImageManager.flushMap();
    }

    /**
     * ????????????????????????????????????
     * @param bg ????????????????????? bg3
     */
    public void setBackgroundImage(String bg){
        int bgId = getResources().getIdentifier(bg, "drawable", "com.example.aircraftwar");
        ImageManager.BACKGROUND_IMAGE = BitmapFactory.decodeResource(getResources(), bgId);
    }

    public void draw() {
        canvas = mSurfaceHolder.lockCanvas();
        if(mSurfaceHolder == null || canvas == null) {
            return;
        }

        // ???????????????????????????
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        // ???????????????????????????
        canvas.drawBitmap(ImageManager.BACKGROUND_IMAGE, 0, backGroundTop - ImageManager.BACKGROUND_IMAGE.getHeight(), mPaint);
        canvas.drawBitmap(ImageManager.BACKGROUND_IMAGE, 0, backGroundTop, mPaint);
        backGroundTop += 1;
        if(backGroundTop == ImageManager.BACKGROUND_IMAGE.getHeight()) {
            backGroundTop = 0;
            Log.i(TAG, "BackgroundTop = 0, screenHeight = " + screenHeight);
        }

        // ?????????????????????????????????????????????????????????
        // ????????????????????????????????????
        drawImageWithPositionRevised(canvas, enemyBullets);
        drawImageWithPositionRevised(canvas, heroBullets);

        drawImageWithPositionRevised(canvas, enemyAircrafts);

        drawImageWithPositionRevised(canvas, props);

        canvas.drawBitmap(ImageManager.HERO_IMAGE,  heroAircraft.getLocationX() - (float)ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - (float)ImageManager.HERO_IMAGE.getHeight() / 2, mPaint);

        //????????????????????????
        drawScoreAndLife(canvas);

        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    private void drawImageWithPositionRevised(Canvas c, List<? extends AbstractFlyingObject> objects) {
        if (objects.size() == 0) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            Bitmap image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            c.drawBitmap(image, object.getLocationX() - (float)image.getWidth() / 2,
                    object.getLocationY() - (float)image.getHeight() / 2, mPaint);
        }
    }

    private void drawScoreAndLife(Canvas canvas) {
        int x = 30;
        int y = 75;
        mPaint.setColor(Color.RED);
        Typeface font = Typeface.create(Typeface.SANS_SERIF,Typeface.BOLD);
        mPaint.setTypeface(font);
        mPaint.setTextSize(66);

        canvas.drawText("SCORE:" + this.score, x, y, mPaint);
        y = y + 70;
        canvas.drawText("LIFE:" + this.heroAircraft.getHp(), x, y, mPaint);
    }

    /**?????????????????????*/
    public void startRankPage(){
        RecordDaoImpl.rankDataFile = name_ + "RankData_"+mode+".txt";
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        intent.setClass(context, RankPage.class);
        bundle.putInt("score", score);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

}
