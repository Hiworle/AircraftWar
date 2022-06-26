package com.example.aircraftwar.data.rank;


import static com.example.aircraftwar.LoginActivity.currentUser;
import static com.example.aircraftwar.LoginActivity.name_;
import static com.example.aircraftwar.LoginActivity.userManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aircraftwar.R;
import com.example.aircraftwar.SingleModeSelActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RankPage extends AppCompatActivity {
    /**创建玩家对象，用于初始化信息*/
    RecordDaoImpl playerDao = new RecordDaoImpl();
    Record record = new Record();
    private int score = 9;
    /**排行榜适配器*/
    RecordsAdapter adapter;
    /**listView实现*/
    ListView rankList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_page);
        Intent intent = getIntent();
        //获得游戏得分
        if (intent.getExtras() != null){
            this.score = intent.getExtras().getInt("score");

        }
        //rankList绑定
        rankList = findViewById(R.id.rankList);
        //排行榜表头
        View view = View.inflate(this, R.layout.page_title,null);
        rankList.addHeaderView(view, null, false);
        //初始化排行榜数据
        init();

        //排行榜显示
        adapter = new RecordsAdapter(RankPage.this, R.layout.records_item, RecordDaoImpl.records);
        rankList.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        // 添加长按记录删除事件
        rankList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                confirmDeletePopUp(i-rankList.getHeaderViewsCount());
                return true;
            }
        });
        rankList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Record record = (Record) adapterView.getAdapter().getItem(i);
                Toast.makeText(RankPage.this, "已选中 "+ record +" ,长按删除该记录", Toast.LENGTH_LONG).show();
            }
        });

        //弹出  添加记录对话框
        addRecordPopUp();
    }

    @Override
    protected void onPause() {
        closePage();
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            Intent intent = new Intent();
            intent.setClass(RankPage.this, SingleModeSelActivity.class);
            startActivity(intent);

            Log.i("程序", "发生回退");
        }
        return super.onKeyDown(keyCode, event);
    }


    /**创建最新记录内容*/
    public String createRecord(){
        //添加日期
        Date now = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        record.setDate(dateFormat.format( now ));
        //添加得分
        record.setScore(score);
        // 添加姓名(获取从登录界面保存的,用户姓名static变量)
        record.setName(name_);
        currentUser.addScore(score);
        userManager.updateList(currentUser);

        return record.toString();
    }



    /**确认添加记录弹窗*/
    public void addRecordPopUp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否添加这条记录？");
        String record = createRecord();
        builder.setMessage(record);
        //确认按钮
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //增加数据
                RecordDaoImpl.records.add(RankPage.this.record);
                update();
                //更新排行榜
                adapter.notifyDataSetChanged();
            }
        });
        //取消按钮
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create();
        builder.show();
    }



    /**确认删除记录弹窗*/
    public void confirmDeletePopUp(int num){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否删除这条记录？");
        //确认按钮
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //删除数据
                RecordDaoImpl.records.remove(num);
                update();
                //更新排行榜
                adapter.notifyDataSetChanged();
            }
        });
        //取消按钮
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create();
        builder.show();
    }



    /**将新数据加载进adapter*/
    private void update(){
        playerDao.sortAllPlayers();
    }



    /**用于界面一开始初始化数据*/
    private void init(){
        loadDataFromDataBase();
    }



    /**关闭当前界面或跳转时执行(将数据存进数据库等操作)*/
    private void closePage(){
        //保存数据进数据库
        saveDataToDataBase();
    }



    /**加载数据操作 每次进都从database读入数据*/
    public void loadDataFromDataBase(){
        ObjectInputStream ois = null;
        FileInputStream fis = null;
        RecordDaoImpl.records.clear();
        try {
            fis = openFileInput(RecordDaoImpl.rankDataFile);
            Log.i("file", RecordDaoImpl.rankDataFile);
            ois = new ObjectInputStream(fis);
            RecordDaoImpl.records =(List<Record>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(ois != null){
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**存储数据操作 每次进都对database进行覆盖式写入。*/
    public void saveDataToDataBase(){
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        {
            try {
                fos = openFileOutput(RecordDaoImpl.rankDataFile, MODE_PRIVATE);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(RecordDaoImpl.records);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(oos != null){
                    try {
                        oos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(fos != null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}