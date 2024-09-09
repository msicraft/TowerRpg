package me.msicraft.towerRpg.Dungeon;

import me.msicraft.towerRpg.Dungeon.Data.DungeonType;
import me.msicraft.towerRpg.Dungeon.Menu.DungeonGui;
import me.msicraft.towerRpg.Menu.GuiType;
import me.msicraft.towerRpg.PlayerData.Data.PlayerData;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.entity.Player;

public class DungeonManager {

    private final TowerRpg plugin;

    public DungeonManager(TowerRpg plugin) {
        this.plugin = plugin;
    }

    public void openDugeonInventory(DungeonType dungeonType, Player player) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        DungeonGui dungeonGui = (DungeonGui) playerData.getCustomGui(GuiType.DUNGEON);
        player.openInventory(dungeonGui.getInventory());
        dungeonGui.setFloorSelectMenu(dungeonType, player);
    }

}
