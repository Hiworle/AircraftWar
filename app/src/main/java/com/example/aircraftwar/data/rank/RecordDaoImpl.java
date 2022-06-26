package com.example.aircraftwar.data.rank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**操作*/

public class RecordDaoImpl implements RecordDao {
    /**存储排行榜数据的文件*/
    public static String rankDataFile = "rankData.txt";
    /**排行榜的对象用链表存储*/
    public static List<Record> records = new ArrayList<>();
    /**数据访问实现类初始化时，同时从datafile加载数据到players*/
    public RecordDaoImpl(){

    }
    /**获取排行榜对象链表*/
    @Override
    public List<Record> getAllPlayers() {
        return records;
    }
    /**添加对象*/
    @Override
    public void addPlayer(Record record) { records.add(record); }
    /**删除对象*/
    @Override
    public void deletePlayer(Record record) {
        records.remove(record);
    }
    /**对排行榜对象排序*/
    @Override
    public void sortAllPlayers() {
        Collections.sort(records,new Comparator<Object>(){
            @Override
            public int compare(Object o1, Object o2) {
                return ((Record) o2).getScore() - ((Record) o1).getScore();
            }
        });
        int ranking = 1;
        for(Record record : records){
            record.setRanking(ranking++);
        }
    }
}
