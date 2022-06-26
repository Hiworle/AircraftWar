package com.example.aircraftwar.online;


import static com.example.aircraftwar.online.MatchingRoomActivity.out;
import static com.example.aircraftwar.online.MatchingRoomActivity.socket;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.aircraftwar.ModeSelActivity;
import com.example.aircraftwar.R;
import com.example.aircraftwar.tool.IOUtil;

import java.io.IOException;

/**
 * 加载旋转框，用于耗时操作时堵塞用户操作
 */
public class LoadingDialog {
    private Context mContext;
    private AlertDialog mAlertDialog;
    private View mView;
    @SuppressLint("HandlerLeak")
    private android.os.Handler handler2 = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                IOUtil.writeString(out,"QUIT");
                try {
                    if(!socket.isClosed()){
                        socket.shutdownInput();
                        socket.shutdownOutput();
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.setClass(mContext, ModeSelActivity.class);
                mContext.startActivity(intent);

            }else if(msg.what == 1){
                Toast.makeText(mContext, "请等待队友完成比赛", Toast.LENGTH_SHORT).show();
            }

        }
    };
    /**
     * 构造一个可以阻塞用户操作的的弹窗对象
     *
     * @param aContext 上下文对象
     */
    public LoadingDialog(Context aContext) {
        this.mContext = aContext;
        this.mView = LayoutInflater.from(aContext).inflate(R.layout.loading_dialog, null);
        this.mAlertDialog = new AlertDialog
                .Builder(this.mContext, R.style.NoDimAlertDialog)
                .setView(this.mView)
                .setCancelable(false)
                .create();

        // 将为透明的
        this.mAlertDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

    }

    /**
     * 构造一个可以阻塞用户操作的的弹窗对象，并指定提示字符
     *
     * @param aContext    上下文对象
     * @param aPromptText 提示字符
     */
    public LoadingDialog(Context aContext, @NonNull String aPromptText) {
        this.mContext = aContext;
        this.mView = LayoutInflater.from(aContext).inflate(R.layout.loading_dialog, null);
        TextView textView = this.mView.findViewById(R.id.loading_dialog_text_view);
        textView.setText(aPromptText);
        this.mAlertDialog = new AlertDialog
                .Builder(this.mContext, R.style.NoDimAlertDialog)
                .setView(this.mView)
                .setCancelable(false)
                .create();
        // 将为透明的
        this.mAlertDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

        //加载窗体返回
        this.mAlertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK){
                    if(aPromptText.equals("正在匹配玩家...")){
                        sendMesToUI(0);
                    }else if(aPromptText.equals("您已经阵亡,等待队友完成比赛...")){
                        sendMesToUI(1);
                    }else if(aPromptText.equals("正在连接到服务器...")){
                        sendMesToUI(0);
                    }
                }
                return false;
            }
        });
    }

    /**给UI线程发消息*/
    private void sendMesToUI(int mes){
        //和UI线程传递消息
        Message msg;
        msg = new Message();
        msg.what = mes;
        handler2.sendMessage(msg);
    }

    /**
     * 显示弹窗
     */
    public void show() {
        if (!this.mAlertDialog.isShowing()) {
            this.mAlertDialog.show();
        }
    }

    /**
     * 隐藏弹窗
     */
    public void dismiss() {
        if (this.mAlertDialog.isShowing()) {
            this.mAlertDialog.dismiss();
        }
    }

    /**
     * 更改提示字符
     *
     * @param aPrompt 提示字符
     */
    public void changePrompt(String aPrompt) {
        TextView textView = this.mView.findViewById(R.id.loading_dialog_text_view);
        textView.setText(aPrompt);
    }

    public void setImg(int id){
        ImageView imageView = this.mView.findViewById(id);

        imageView.setVisibility(View.VISIBLE);
    }



}
