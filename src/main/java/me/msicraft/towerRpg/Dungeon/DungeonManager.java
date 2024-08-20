package me.msicraft.towerRpg.Dungeon;

import me.msicraft.towerRpg.Dungeon.Menu.DungeonGui;
import me.msicraft.towerRpg.TowerRpg;

public class DungeonManager {

    private final TowerRpg plugin;
    private final DungeonGui dungeonGui;

    public DungeonManager(TowerRpg plugin) {
        this.plugin = plugin;
        this.dungeonGui = new DungeonGui(plugin);
    }

}
