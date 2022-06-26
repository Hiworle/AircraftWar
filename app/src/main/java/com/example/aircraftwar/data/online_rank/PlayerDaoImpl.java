package com.example.aircraftwar.data.online_rank;



import static com.example.aircraftwar.LoginActivity.name_;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayerDaoImpl implements PlayerDao {

    /**存储排行榜数据的文件*/
    public static String rankDataFile = name_+"_onlineRecords.txt";
    /**排行榜的对象用链表存储*/
    public static List<Player> players = new ArrayList<>();
    public PlayerDaoImpl(){

    }
    /**获取排行榜对象链表*/
    @Override
    public List<Player> getAllPlayers() {
        return players;
    }
    /**添加对象*/
    @Override
    public  void addPlayer(Player player) { players.add(player); }
    /**删除对象*/
    @Override
    public void deletePlayer(Player player) {players.remove(player);
    }
    /**对排行榜对象排序*/
    @Override
    public void sortAllPlayers() {
        Collections.sort(players,new Comparator<Object>(){
            @Override
            public int compare(Object o1, Object o2) {
                return ((Player) o2).getScore() - ((Player) o1).getScore();
            }
        });
        int ranking = 1;
        for(Player player : players){
            player.setRanking(ranking++);
        }
    }

}
