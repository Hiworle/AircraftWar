package com.example.aircraftwar.data.rank;

import java.util.List;

/**用于操纵多条记录的链表*/
public interface RecordDao {
    public List<Record> getAllPlayers();
    public void addPlayer(Record record);
    public void deletePlayer(Record record);
    public void sortAllPlayers();
}
