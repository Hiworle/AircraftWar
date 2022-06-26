package com.example.aircraftwar.online;

//import static com.hit.aircraftwar_base.stand_alone_pack.GameActivity.musicService;

import static com.example.aircraftwar.LoginActivity.name_;
import static com.example.aircraftwar.online.MatchingRoomActivity.in;
import static com.example.aircraftwar.online.MatchingRoomActivity.out;

import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.aircraftwar.GameView;
import com.example.aircraftwar.R;
import com.example.aircraftwar.aircraft.AbstractAircraft;
import com.example.aircraftwar.aircraft.HeroAircraft;
import com.example.aircraftwar.application.ImageManager;
import com.example.aircraftwar.basic.AbstractFlyingObject;
import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.data.online_rank.OnlineRankPage;
import com.example.aircraftwar.data.rank.RecordDaoImpl;
import com.example.aircraftwar.factory.AbstractEnemyFactory;
import com.example.aircraftwar.factory.BossEnemyFactory;
import com.example.aircraftwar.factory.EliteEnemyFactory;
import com.example.aircraftwar.factory.MobEnemyFactory;
import com.example.aircraftwar.prop.AbstractProp;
import com.example.aircraftwar.prop.BombProp;
import com.example.aircraftwar.tool.IOUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class OnlineGameView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = GameView.class.getSimpleName();
    private Context context ;
    /**内部类*/
    private Communication communication = new Communication();

    private int screenWidth = OnlineGameActivity.screenWidth;
    private int screenHeight = OnlineGameActivity.screenHeight;
    private OnlineGameActivity gameActivity;

    private Canvas canvas;
    private Paint mPaint;
    private SurfaceHolder mSurfaceHolder;

    public static boolean ifMusicOn = true;

    /**打印信息*/
    static int score = 0;
    int otherScore = 0;
    int otherHp = 100;
    final String yourName = name_;
    String otherName = "";
    private String message = "";

    /**弹窗*/
    LoadingDialog loadingDialog;

    private int backGroundTop = 0;

    /**
     * 记录下一次触发boss战的阈值
     */
    private int nextBossScore = 50;
    private int nextBossDuration = 50;
    private boolean bossBattle = false;
    private int bossHp = 400;

    /**
     * Scheduled 线程池，用于任务调度
     */
    private final ScheduledExecutorService executorService;

    /**
     * 时间间隔(ms)，控制刷新频率
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
    private int time = 0;
    /**
     * 敌机产生周期（ms)
     * 指示敌机的产生频率
     */
    protected int addEnemyCycleDuration = 600;
    protected int addEnemyCycleTime = 0;
    /**
     * 敌机射击周期（ms)
     * 指示射击频率
     */
    protected int enemyShootActionCycleDuration = 600;
    protected int enemyShootActionCycleTime = 0;
    /**
     * 敌机射击周期（ms)
     * 指示射击频率
     */
    protected int heroShootActionCycleDuration = 600;
    protected int heroShootActionCycleTime = 0;
    /**
     * 周期（ms)
     * 指示子弹的发射、敌机的产生频率
     */
    private int cycleDuration = 600;
    private int cycleTime = 0;

    /**用来和UI线程传递消息*/
    @SuppressLint("HandlerLeak")
    private Handler handler1 = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            //发送队友阵亡
            if (msg.what == 1) {
                Toast.makeText(context,"队友已经阵亡", Toast.LENGTH_SHORT).show();
            }
            //加载弹窗
            if(msg.what == 2){
                loadingDialog = new LoadingDialog(context, "等待队友完成游戏。。。");
//                loadingDialog.setImg(R.id.img2);
                loadingDialog.show();
            }

            if(msg.what == 3){
                loadingDialog.dismiss();
            }
        }
    };


    public OnlineGameView(Context context) {
        super(context);
        this.context = context;
        this.gameActivity = (OnlineGameActivity) context;

        this.context = context;
        this.gameActivity = (OnlineGameActivity) context;

        mPaint = new Paint();
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        loadingImg();

        // 获取英雄机
        heroAircraft = HeroAircraft.getInstance();

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();

        //Scheduled 线程池，用于定时任务调度
        ThreadFactory gameThread = r -> {
            Thread t = new Thread(r);
            t.setName("game thread");
            return t;
        };
        executorService = new ScheduledThreadPoolExecutor(1, gameThread);

        this.setFocusable(true);
    }

    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {


        //进入游戏，开始播放bgm
//        MusicService.MyBinder binder = gameActivity.myBinder;
//        binder.playBgm();


        // 定时任务：绘制、对象产生、碰撞判定、击毁及结束判定
        Runnable task = () -> {

            time += timeInterval;
            adjustDifficulty();

            // 周期性执行（控制频率）
            if (addEnemyCycleJudge()) {
                Log.i(TAG,time+"");
                // 新敌机产生
                addEnemy();
            }

            if(enemyShootActionCycleJudge()) {
                // 敌机射出子弹
                enemyShootAction();
            }

            if(heroShootActionCycleJudge()) {
                // 英雄机射出子弹
                heroShootAction();
            }

            // 子弹移动
            bulletsMoveAction();
            // 飞机移动
            aircraftsMoveAction();
            // 道具移动
            propsMoveAction();

            // 撞击检测
            crashCheckAction();

            // 后处理
            postProcessAction();

            //每个时刻重绘界面
            draw();

            // 游戏结束检查
            if (heroAircraft.getHp() <= 0) {
                // 游戏结束
                executorService.shutdown();
                gameOverFlag = true;
                Log.i(TAG, "Game over!");

                // 游戏结束音效
                gameActivity.myBinder.playGameOver();

                // 结束循环bgm
//                gameActivity.stopBgm();
                startRankPage();
                // 输入姓名
//                String name = JOptionPane.showInputDialog(this, "输入姓名或放弃成绩","游戏结束",
//                        JOptionPane.QUESTION_MESSAGE);
//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setMessage(R.string.di)

//                if (name != null) {
//                    // 保存得分信息
//                    Scoring scoring = new Scoring(name,score, LocalDateTime.now());
//                    ScoringDaoImpl scoringDao = new ScoringDaoImpl(mode);
//                    scoringDao.doAdd(scoring);
//
//                    // 打印得分信息
//                    System.out.println("#############################################");
//                    System.out.println(scoringDao.toString());
//                    System.out.println("#############################################");
//                }

//                synchronized (GameActivity.MAINWINDOW_LOCK) {
//                    // 通知主线程显示得分榜
//                    GameActivity.MAINWINDOW_LOCK.notify();
//                }
            }

        };

        /*
         * 以固定延迟时间进行执行
         * 本次任务执行完成后，需要延迟设定的延迟时间，才会执行新的任务
         */
        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);

    }


    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        communicateToServer();
    }

    /**和服务器交互*/
    private void communicateToServer(){
        sendMesToYourself(yourName);
        otherName = readMesFromMyself();
        communication.start();

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int format, int width, int height) {
        screenWidth= width;
        screenHeight = height;
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    /**与自己的服务器交互消息*/
    private void sendMesToYourself(String mes){
        IOUtil.writeString(out, mes);
    }
    private String readMesFromMyself(){
        return IOUtil.readString(in);
    }
    /**给UI线程发消息*/
    private void sendMesToUI(int mes){
        //和UI线程传递消息
        Message msg;
        msg = new Message();
        msg.what = mes;
        handler1.sendMessage(msg);
    }

    /**开启多线程来交互数据*/
    class Communication extends Thread{
        boolean stop = false;
        int i=0;
        @Override
        public void run(){
            while (!this.stop){
                synchronized (this){
                    Log.i(gameOverFlag+"", "!!!!!!!!!!!!!!!!!!!!");//ToDO

                    if(gameOverFlag && i==0){
                        Log.i(TAG, "you dead");
                        sendMesToYourself("DIE");
                        sendMesToUI(2);
                        i++;
                        Log.i(TAG, "DIE msg sent");

                    }else {
                        sendMesToYourself(String.valueOf(score)+"!"+String.valueOf(heroAircraft.getHp()));
                    }

                    message = readMesFromMyself();

                    //队友阵亡
                    if(message != null){
                        if(message.equals("DIE")){
                            //Toast(给自己发结束消息"队友已阵亡,加油!!")
                            sendMesToUI(1);
                        }else if(message.equals("OQUIT")){

                        }
                        else if(message.equals("OVER")){
                            sendMesToUI(3);
                            startRankPage();
                            this.stop = true;
                        }
                        else{
                            otherScore = Integer.parseInt(message.split("!")[0]);
                            otherHp = Integer.parseInt(message.split("!")[1]);
                        }
                    }else{
                        Log.i(TAG, "收到空消息！");
                        this.stop = true;
                        gameOverFlag = true;
                    }
                }
            }
        }
    }
    /**
     * 根据以下规则产生敌机：
     *  boss战中，不产生敌机；
     *  非boss战中，若分数达到要求，触发boss战；
     *  若未达到要求，添加一架敌机，保证精英机和普通敌机的比例是eliteRate，并且数量不超过enemyMaxNumber
     */
    protected void addEnemy() {
        AbstractEnemyFactory enemyFactory;

        // boss战中
        if(bossBattle) {
            // boss机被击败
            if(enemyAircrafts.isEmpty()){
                bossBattle = false;

                // boss被击败，bgm回来
                gameActivity.myBinder.playBgm();
            }

        } else {
            if(score >= nextBossScore){
                bossBattle = true;
                nextBossScore += nextBossDuration;

                enemyFactory = new BossEnemyFactory();
                enemyAircrafts.add(enemyFactory.createEnemy());
                // boss机出现，bgm改变
                gameActivity.myBinder.playBossBgm();

            } else if(enemyAircrafts.size() < enemyMaxNumber){
                if(Math.random() < eliteRate){
                    // 召唤精英敌机
                    enemyFactory = new EliteEnemyFactory();
                } else {
                    // 召唤普通敌机
                    enemyFactory = new MobEnemyFactory();
                }
                enemyAircrafts.add(enemyFactory.createEnemy());
            }
        }
    }

    /**
     * 随时间调整难度
     */
    private void adjustDifficulty(){
        if(!harderWithTime) {
            return;
        }
        if(time >= nextDifficultyDuration) {
            // 下次增加难度的时间将是指数增长的，更新难度的时间是游戏开始后10, 20, 40, 80...秒
            nextDifficultyDuration *= 2;
            System.out.println("********************** 难度提升 **********************");
            enemyMaxNumber += 0.5;
            System.out.println("| 敌机数量最大值: " + enemyMaxNumber);
            eliteRate += 0.05;
            System.out.println("| 精英机概率: " + eliteRate);
            addEnemyCycleDuration -= 80;
            System.out.println("| 敌机产生周期: " + addEnemyCycleDuration);
            enemyShootActionCycleDuration -= 40;
            System.out.println("| 敌机射击周期: " + enemyShootActionCycleDuration);
            nextBossDuration -= 10;
            System.out.println("| Boss敌机产生的得分阈值: " + nextBossDuration);
            dropRate -= 0.01;
            System.out.println("| 装备掉落率: " + dropRate);
            System.out.println("*****************************************************");
        }

    }

    //***********************
    //      Action 各部分
    //***********************

    private boolean addEnemyCycleJudge() {
        addEnemyCycleTime += timeInterval;
        if (addEnemyCycleTime >= addEnemyCycleDuration && addEnemyCycleTime - timeInterval < addEnemyCycleTime) {
            // 跨越到新的周期
            addEnemyCycleTime %= addEnemyCycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private boolean enemyShootActionCycleJudge() {
        enemyShootActionCycleTime += timeInterval;
        if (enemyShootActionCycleTime >= enemyShootActionCycleDuration && enemyShootActionCycleTime - timeInterval < enemyShootActionCycleTime) {
            // 跨越到新的周期
            enemyShootActionCycleTime %= enemyShootActionCycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private boolean heroShootActionCycleJudge() {
        heroShootActionCycleTime += timeInterval;
        if (heroShootActionCycleTime >= heroShootActionCycleDuration && heroShootActionCycleTime - timeInterval < heroShootActionCycleTime) {
            // 跨越到新的周期
            heroShootActionCycleTime %= heroShootActionCycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private void enemyShootAction() {
        // 敌机射击
        for(AbstractAircraft enemyAircraft : enemyAircrafts ){
            enemyBullets.addAll(enemyAircraft.shoot());
        }
    }

    private void heroShootAction(){
        // 英雄射击
        heroBullets.addAll(heroAircraft.shoot());

        // 播放发射子弹音效
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
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() {
        // 敌机子弹攻击英雄
        for(BaseBullet bullet : enemyBullets){
            if(bullet.notValid()) {
                continue;
            }
            if(heroAircraft.crash(bullet)){
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }


        // 英雄子弹攻击敌机，敌机与英雄机的碰撞检测
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());

                    // 播放击中敌机声音
                    gameActivity.myBinder.playBulletHit();

                    bullet.vanish();
                    if (enemyAircraft.notValid()) {
                        // 获得分数，产生道具补给

                        if(Math.random() < dropRate){
                            props.add(enemyAircraft.dropProp());
                        }

                        // 击败一架敌机+10分
                        score += 10;
                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }

            }
        }

        // 我方获得道具，道具生效
        for(AbstractProp prop : props){
            if(prop.crash(heroAircraft) || heroAircraft.crash(prop)){
                // 获得道具音效
                gameActivity.myBinder.playGetSupply();

                // 清屏道具需要将炸毁物体添加到订阅
                if(prop instanceof BombProp) {
                    //炸弹音效
                    gameActivity.myBinder.playBombExplosion();
                    BombProp bombProp = (BombProp) prop;

                    // 添加敌机子弹
                    for (BaseBullet bullet : enemyBullets) {
                        bombProp.addObject(bullet);
                    }

                    // 添加敌机
                    for (AbstractAircraft enemy : enemyAircrafts) {
                        bombProp.addObject(enemy);
                    }
                }

                // 道具生效有可能获得得分
                score += prop.work(heroAircraft);
                prop.vanish();
            }
        }
    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * 3. 检查英雄机生存
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }

    //***********************
    //      Draw 各部分
    //***********************

    /**
     * 加载图片
     */
    public void loadingImg() {
        // 默认背景是简单图
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
     * 根据提供的文件名更改背景
     * @param bg 背景文件名，如 bg3
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

        // 清除上一次绘制内容
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        // 绘制背景，图片滚动
        canvas.drawBitmap(ImageManager.BACKGROUND_IMAGE, 0, backGroundTop - ImageManager.BACKGROUND_IMAGE.getHeight(), mPaint);
        canvas.drawBitmap(ImageManager.BACKGROUND_IMAGE, 0, backGroundTop, mPaint);
        backGroundTop += 1;
        if(backGroundTop == ImageManager.BACKGROUND_IMAGE.getHeight()) {
            backGroundTop = 0;
            Log.i(TAG, "BackgroundTop = 0, screenHeight = " + screenHeight);
        }

        // 先绘制子弹，后绘制飞机，再绘制道具掉落
        // 这样子弹显示在飞机的下层
        drawImageWithPositionRevised(canvas, enemyBullets);
        drawImageWithPositionRevised(canvas, heroBullets);

        drawImageWithPositionRevised(canvas, enemyAircrafts);

        drawImageWithPositionRevised(canvas, props);

        canvas.drawBitmap(ImageManager.HERO_IMAGE,  heroAircraft.getLocationX() - (float)ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - (float)ImageManager.HERO_IMAGE.getHeight() / 2, mPaint);

        //绘制得分和生命值
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
        //打印自己的信息
        int x = 15;
        int y = 50;
        mPaint.setColor(Color.RED);
        mPaint.setTypeface(Typeface.SANS_SERIF);
        mPaint.setTextSize(50);
        canvas.drawText("我:  " + yourName, x, y,mPaint);
        y += 55;
        canvas.drawText("SCORE:" + this.score, x, y,mPaint);
        y = y + 55;
        canvas.drawText("LIFE:" + heroAircraft.getHp(), x, y,mPaint);


        //打印别人的信息
        int x_ = screenWidth-300;
        int y_ = 50;
        mPaint.setColor(Color.RED);
        mPaint.setTypeface(Typeface.SANS_SERIF);
        mPaint.setTextSize(50);
        canvas.drawText("队友:  " + otherName, x_, y_,mPaint);
        y_ += 55;
        canvas.drawText("SCORE:" + this.otherScore, x_, y_,mPaint);
        y_ = y_ + 55;
        canvas.drawText("LIFE:" + otherHp, x_, y_,mPaint);
    }

    /**开始排行榜界面*/
    public void startRankPage(){
        RecordDaoImpl.rankDataFile = name_ + "RankData_Online"+".txt";
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        intent.setClass(context, OnlineRankPage.class);
        bundle.putString("this", yourName+"!"+otherName+"!"+score+"!"+otherScore);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

}