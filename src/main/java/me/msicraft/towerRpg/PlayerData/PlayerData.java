package me.msicraft.towerRpg.PlayerData;

import me.msicraft.towerRpg.PlayerData.Data.CustomGui;
import me.msicraft.towerRpg.PlayerData.Data.GuiType;
import me.msicraft.towerRpg.PlayerData.File.PlayerDataFile;
import me.msicraft.towerRpg.PlayerData.Menu.MenuGui;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerData {

    private final Player player;
    private final PlayerDataFile playerDataFile;

    private final Map<GuiType, CustomGui> customGuiMap = new HashMap<>();

    public PlayerData(Player player) {
        this.player = player;
        this.playerDataFile = new PlayerDataFile(player);
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerDataFile getPlayerDataFile() {
        return playerDataFile;
    }

    public CustomGui getCustomGui(GuiType guiType) {
        CustomGui customGui = customGuiMap.getOrDefault(guiType, null);
        if (customGui == null) {
            switch (guiType) {
                case MAIN -> {
                    customGui = new MenuGui();
                    customGuiMap.put(guiType, customGui);
                }
                default -> {
                    customGui = new MenuGui();
                    Bukkit.getConsoleSender().sendMessage(TowerRpg.PREFIX + ChatColor.YELLOW + "플레이어: " + player.getName(),
                            ChatColor.YELLOW + "메뉴 생성중 기본값 사용이 발생하였습니다.");
                }
            }
        }
        return customGui;
    }

}
