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
        reloadVariables();
    }

    public void reloadVariables() {
        DungeonType[] dungeonTypes = DungeonType.values();
        for (DungeonType dungeonType : dungeonTypes) {
            String path = "Setting.Dungeon." + dungeonType.getKey();
            if (plugin.getConfig().contains(path)) {
                dungeonType.setDisplayName(plugin.getConfig().contains(path + ".DisplayName") ? plugin.getConfig().getString(path + ".DisplayName") : dungeonType.getDisplayName());
                dungeonType.setTotalFloor(plugin.getConfig().contains(path + ".TotalFloor") ? plugin.getConfig().getInt(path + ".TotalFloor") : 0);
                dungeonType.setMaxPlayer(plugin.getConfig().contains(path + ".MaxPlayer") ? plugin.getConfig().getInt(path + ".MaxPlayer") : 1);
            }
        }
    }

    public void openDungeonInventory(DungeonType dungeonType, Player player) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        DungeonGui dungeonGui = (DungeonGui) playerData.getCustomGui(GuiType.DUNGEON);
        player.openInventory(dungeonGui.getInventory());
        dungeonGui.setFloorSelectMenu(dungeonType, player);
    }

}
