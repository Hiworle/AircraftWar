package com.example.aircraftwar.data.online_rank;




import static com.example.aircraftwar.LoginActivity.currentUser;
import static com.example.aircraftwar.LoginActivity.userManager;
import static com.example.aircraftwar.data.online_rank.PlayerDaoImpl.rankDataFile;

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

import com.example.aircraftwar.ModeSelActivity;
import com.example.aircraftwar.R;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class OnlineRankPage extends AppCompatActivity {
    /**创建玩家对象，用于初始化信息*/
    PlayerDaoImpl playerDao = new PlayerDaoImpl();
    /**当前新纪录*/
    Player player ;
    /**排行榜适配器*/
    OnlineRecordsAdapter adapter;
    /**listView实现*/
    ListView rankList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_rank_page);

        init();

        addRecordPopUp();

    }

    /**创建最新记录内容*/
    public String createRecord(){
        Intent intent = getIntent();
        if (intent.getExtras() != null){
            String records = intent.getExtras().getString("this");
            player = new Player(records.split("!")[0],
                    records.split("!")[1],
                    Integer.parseInt(records.split("!")[2]),
                    Integer.parseInt(records.split("!")[3]));
            currentUser.addScore(Integer.parseInt(records.split("!")[2]));
            userManager.updateList(currentUser);

        }
        //增加积分
        return player.toString();
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
                playerDao.addPlayer(OnlineRankPage.this.player);
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
                PlayerDaoImpl.players.remove(num);
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

        rankList = findViewById(R.id.onlineRankList);
        //添加排行榜表头
        View view = View.inflate(this, R.layout.online_page_title,null);
        rankList.addHeaderView(view, null, false);

        loadDataFromDataBase();
        //排行榜显示
        adapter = new OnlineRecordsAdapter(OnlineRankPage.this, R.layout.online_records_item, PlayerDaoImpl.players);
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
                Player player = (Player) adapterView.getAdapter().getItem(i);
                Toast.makeText(OnlineRankPage.this, "已选中 "+ player +" ,长按删除该记录", Toast.LENGTH_LONG).show();
            }
        });

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
        PlayerDaoImpl.players.clear();
        try {
            fis = openFileInput(rankDataFile);
            Log.i("file", rankDataFile);
            ois = new ObjectInputStream(fis);
            PlayerDaoImpl.players =(List<Player>) ois.readObject();
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
                fos = openFileOutput(rankDataFile, MODE_PRIVATE);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(PlayerDaoImpl.players);
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

    @Override
    protected void onPause() {
        closePage();
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            Intent intent = new Intent();
            intent.setClass(OnlineRankPage.this, ModeSelActivity.class);
            startActivity(intent);

            Log.i("程序", "发生回退");
        }
        return super.onKeyDown(keyCode, event);
    }


}