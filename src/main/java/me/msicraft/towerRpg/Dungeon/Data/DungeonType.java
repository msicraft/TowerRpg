package me.msicraft.towerRpg.Dungeon.Data;

public enum DungeonType {

    BEGINNING_TOWER("beginning_tower", "시작의 탑",1, 1);

    private final String key;
    private String displayName;
    private int totalFloor;
    private int maxPlayer;

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

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getTotalFloor() {
        return totalFloor;
    }

    public void setTotalFloor(int totalFloor) {
        this.totalFloor = totalFloor;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }
}
