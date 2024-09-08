package me.msicraft.towerRpg.API.CustomEvent;

import me.msicraft.towerRpg.PlayerData.Data.PlayerData;

public class PlayerDataUnloadEvent extends TowerRpgEvent{

    private final PlayerData playerData;

    public PlayerDataUnloadEvent(PlayerData playerData) {
        this.playerData = playerData;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

}
