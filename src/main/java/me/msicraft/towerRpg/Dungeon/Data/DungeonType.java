package me.msicraft.towerRpg.Dungeon.Data;

public enum DungeonType {

    BEGINNING_TOWER(5, 3);

    private final int totalFloor;
    private final int maxPlayer;

    DungeonType(int totalFloor, int maxPlayer) {
        this.totalFloor = totalFloor;
        this.maxPlayer = maxPlayer;
    }

    public int getTotalFloor() {
        return totalFloor;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

}
