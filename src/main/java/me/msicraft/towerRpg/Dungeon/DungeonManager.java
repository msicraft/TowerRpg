package me.msicraft.towerRpg.Dungeon;

import me.msicraft.towerRpg.Dungeon.Data.DungeonType;
import me.msicraft.towerRpg.Dungeon.Menu.DungeonGui;
import me.msicraft.towerRpg.Menu.GuiType;
import me.msicraft.towerRpg.PlayerData.Data.PlayerData;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DungeonManager {

    private final TowerRpg plugin;

    public DungeonManager(TowerRpg plugin) {
        this.plugin = plugin;
        reloadVariables();
    }

    public void reloadVariables() {
        DungeonType[] dungeonTypes = DungeonType.values();
        List<String> dungeonKeys = new ArrayList<>(dungeonTypes.length);
        for (DungeonType dungeonType : dungeonTypes) {
            dungeonKeys.add(dungeonType.getKey());
        }

        boolean isChange = false;
        for (String key : dungeonKeys) {
            String path = "Setting.Dungeon." + key;
            try {
                DungeonType dungeonType = DungeonType.valueOf(key.toUpperCase());
                if (plugin.getConfig().contains(path)) {
                    dungeonType.setDisplayName(plugin.getConfig().contains(path + ".DisplayName") ? plugin.getConfig().getString(path + ".DisplayName") : dungeonType.getDisplayName());
                    dungeonType.setTotalFloor(plugin.getConfig().contains(path + ".TotalFloor") ? plugin.getConfig().getInt(path + ".TotalFloor") : 0);
                    dungeonType.setMaxPlayer(plugin.getConfig().contains(path + ".MaxPlayer") ? plugin.getConfig().getInt(path + ".MaxPlayer") : 1);
                } else {
                    if (!isChange) {
                        isChange = true;
                    }
                    plugin.getConfig().set(path + ".DisplayName", dungeonType.getDisplayName());
                    plugin.getConfig().set(path + ".TotalFloor", dungeonType.getTotalFloor());
                    plugin.getConfig().set(path + ".MaxPlayer", dungeonType.getMaxPlayer());
                }
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (isChange) {
            plugin.saveConfig();
        }
    }

    public void openDungeonInventory(DungeonType dungeonType, Player player) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        DungeonGui dungeonGui = (DungeonGui) playerData.getCustomGui(GuiType.DUNGEON);
        player.openInventory(dungeonGui.getInventory());
        dungeonGui.setFloorSelectMenu(dungeonType, player);
    }

}
