package me.msicraft.towerRpg.API.CustomEvent;

import me.msicraft.towerRpg.PlayerData.Data.PlayerData;

public class PlayerDataLoadEvent extends TowerRpgEvent{

    private final PlayerData playerData;

    public PlayerDataLoadEvent(PlayerData playerData) {
        this.playerData = playerData;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

}
