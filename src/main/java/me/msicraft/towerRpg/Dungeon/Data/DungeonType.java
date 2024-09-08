package me.msicraft.towerRpg.Dungeon.Data;

public enum DungeonType {

    BEGINNING_TOWER("beginning_tower", "시작의 탑",5, 4);

    private final String key;
    private final String displayName;
    private final int totalFloor;
    private final int maxPlayer;

    DungeonType(String key, String displayName, int totalFloor, int maxPlayer) {
        this.key = key;
        this.displayName = displayName;
        this.totalFloor = totalFloor;
        this.maxPlayer = maxPlayer;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getTotalFloor() {
        return totalFloor;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

}
