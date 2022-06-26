package com.example.aircraftwar.online;

import static com.example.aircraftwar.LoginActivity.name_;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aircraftwar.ModeSelActivity;
import com.example.aircraftwar.R;
import com.example.aircraftwar.tool.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MatchingRoomActivity extends AppCompatActivity {
    public static Socket socket;
    public static InputStream in;
    public static OutputStream out;
    private LoadingDialog loadingDialog;

    /**提醒*/
    private static Toast toast = null;
    /**组件*/
    private Button set_ready;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
//    private Switch switchV;
    /**标志*/
    private int timeNum = 3;
    private int clicked = 0;
    private boolean startGameFlag = false;
    private boolean myBreak = false;
    /**从服务器读取到的数据*/
    private String toast_conn ;
    /**端口号*/
    public static final  int PORT = 1128;
    private static final int COMPLETED = 0;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                set_ready.setVisibility(View.VISIBLE);
            }
            else if(msg.what == 1){
                handlerCountDown();
            }else if(msg.what == 2){
                myBreak = true;
            }else if(msg.what == 3){
                loadingDialog = new LoadingDialog(MatchingRoomActivity.this, "正在匹配玩家...");
//                loadingDialog.setImg(R.id.img1);
                loadingDialog.show();

            }
            else if(msg.what == 4){
                loadingDialog.dismiss();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maching_room);
        //初始化各种控件
        init();
        //创建当前客户端套接字
        socket = new Socket();
        //开始连接网络
        new Thread(new NetConn()).start();
        loadingDialog = new LoadingDialog(this, "正在连接到服务器...");
//        loadingDialog.setImg(R.id.img1);
        loadingDialog.show();

    }

//    class DialogThread extends Thread{
//        @Override
//        public void run(){
//            loadingDialog = new LoadingDialog(this, "正在连接到服务器...");
//            loadingDialog.setImg(R.id.img1);
//            loadingDialog.show();
//        }
//    }

    class NetConn extends Thread{

        @Override
        public void run(){
            try {
                socket.connect(new InetSocketAddress("192.168.137.1",PORT), 10000);
                //建立输入输出流
                try {
                    in = socket.getInputStream();
                    out = socket.getOutputStream();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //向服务器发送自己的数据
                IOUtil.writeString(out, name_);


                //等待所有玩家点击准备 接受来自服务器端的确认
                while(!startGameFlag){
                    synchronized (this){

                        toast_conn= IOUtil.readString(in);

                        if(socket.isClosed()){
                            return;
                        }

                        if(toast_conn != null){

                            if(toast_conn.equals("READY")){
                                //倒计时开始游戏动画
                                sendMesToUI(1);
                            }else if(toast_conn.equals("ENTER")){
                                //连接成功进入房间提示
                                sendMesToUI(4);
                                showToast(MatchingRoomActivity.this, "您已进入房间");
                                sendMesToUI(3);
                            }
                            else if(toast_conn.equals("BREAK")){
                                //中断倒计时
                                sendMesToUI(2);

                                showToast(MatchingRoomActivity.this, "点击准备,重新开始游戏");
                            }else if(toast_conn.equals("WAIT1")){
                                //中断倒计时
                                sendMesToUI(2);
                                showToast(MatchingRoomActivity.this, "等待队友点击准备");

                            }
                            else if(toast_conn.equals("START")){

                                startGameFlag = true;
                            }
                            else if(toast_conn.equals("WAIT")){
                                sendMesToUI(4);
                                showToast(MatchingRoomActivity.this, "队友已加入游戏");
                                //将按钮显现
                                showToast(MatchingRoomActivity.this, "准备开始游戏！");
                                sendMesToUI(0);
                            }else if(toast_conn.equals("OQUIT")){
                                IOUtil.writeString(out, "OQUIT");
                                sendMesToUI(3);
                            }
                        }else {
                            Log.i("MatchingRoom", "读出null");
                        }
                    }
                }

                // 开始正式游戏
                Intent intent = new Intent(MatchingRoomActivity.this, OnlineGameActivity.class);
                startActivity(intent);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void showToast(Activity activity, String text) {

        // 子线程
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();

            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void handlerCountDown(){
        //重置中断标签
        myBreak = false;
        timeNum = 3;
        Handler mHandler = new Handler();


        mHandler.postDelayed(new Runnable() {

            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                if(myBreak){
                    return;
                }
                else if(timeNum != 0 && !myBreak){
                    timeNum--;
                    mHandler.postDelayed(this, 1000);
                }else if(!myBreak){
                    //(倒计时结束开始游戏,,向服务器发送-1)
                    Log.i("MatchingRoom", "开始战斗");//TODO

                    IOUtil.writeString(out, "-1");
                }
            }
        }, 1000);
    }

    private void sendMesToUI(int mes){
        //和UI线程传递消息
        Message msg;
        msg = new Message();
        msg.what = mes;
        handler.sendMessage(msg);
    }

    private void init() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            set_ready = findViewById(R.id.set_ready);
            set_ready.setVisibility(View.INVISIBLE);
            set_ready.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clicked++;
                    //点击一次
                    if (clicked % 2 == 1) {
                        set_ready.setText("取消准备");
                        //向服务端发送就绪
                        IOUtil.writeString(out, "YES");
                    } else {
                        set_ready.setText("准备");
                        //向服务端发送中断就绪
                        IOUtil.writeString(out, "NOT");
                    }
                }
            });

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        //TODO 处理程序运行时退回桌面，程序继续运行
        if(keyCode == KeyEvent.KEYCODE_HOME && event.getRepeatCount() == 0){
            //moveTaskToBack(true);
        }
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("您已经匹配成功,是否退出当前房间?");
            //确认按钮

            builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //TODO 当一玩家中途退出,关闭套接字,告诉服务器他阵亡
                    IOUtil.writeString(out,"QUIT");
                    Intent intent = new Intent();
                    intent.setClass(MatchingRoomActivity.this, ModeSelActivity.class);
                    startActivity(intent);
                    try {
                        socket.shutdownInput();
                        socket.shutdownOutput();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    onStop();
                }
            });
            //取消按钮
            builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            //弹出窗口时,按返回键,继续游戏
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                }
            });
            builder.create();
            builder.show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}