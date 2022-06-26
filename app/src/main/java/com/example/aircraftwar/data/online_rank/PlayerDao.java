package com.example.aircraftwar.data.online_rank;

import java.util.List;

public interface PlayerDao {
    public void addPlayer(Player player);
    public void deletePlayer(Player player);
    public void sortAllPlayers();
    public List<Player> getAllPlayers();

}
