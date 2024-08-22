package me.msicraft.towerRpg.Dungeon.Data;

public enum DungeonType {

    BEGINNING_TOWER("beginning_tower",5, 3);

    private final String key;
    private final int totalFloor;
    private final int maxPlayer;

    DungeonType(String key, int totalFloor, int maxPlayer) {
        this.key = key;
        this.totalFloor = totalFloor;
        this.maxPlayer = maxPlayer;
    }

    public String getKey() {
        return key;
    }

    public int getTotalFloor() {
        return totalFloor;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

}
