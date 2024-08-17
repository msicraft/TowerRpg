package me.msicraft.towerRpg.PlayerData.Data;

import me.msicraft.towerRpg.Menu.MenuGui;
import me.msicraft.towerRpg.PlayerData.File.PlayerDataFile;
import me.msicraft.towerRpg.Prefix.Data.Prefix;
import me.msicraft.towerRpg.Prefix.PrefixManager;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerData {

    private final Player player;
    private final PlayerDataFile playerDataFile;

    private final Map<GuiType, CustomGui> customGuiMap = new HashMap<>();

    private final Map<String, Object> tempDataMap = new HashMap<>();
    private final List<String> tagList = new ArrayList<>();

    private final PlayerPrefix playerPrefix;

    public PlayerData(Player player) {
        this.player = player;
        this.playerDataFile = new PlayerDataFile(player);
        this.playerPrefix = new PlayerPrefix();
    }

    public void loadData() {
        FileConfiguration config = playerDataFile.getConfig();
        PrefixManager prefixManager = TowerRpg.getPlugin().getPrefixManager();

        //
        String applyPrefixId = config.getString("Prefix.ApplyPrefix");
        Prefix prefix = prefixManager.getPrefix(applyPrefixId);
        prefixManager.applyPrefix(player, prefix);

        List<String> prefixList = config.getStringList("Prefix.List");
        for (String id : prefixList) {
            Prefix prefix1 = prefixManager.getPrefix(id);
            if (prefix1 != null) {
                playerPrefix.addPrefix(prefix1);
            }
        }
        //

        List<String> tags = config.getStringList("Tags");
        tagList.addAll(tags);
    }

    public void saveData() {
        FileConfiguration config = playerDataFile.getConfig();

        //
        Prefix applyPrefix = playerPrefix.getPrefix();
        if (applyPrefix != null) {
            config.set("Prefix.ApplyPrefix", applyPrefix.getId());
        } else {
            config.set("Prefix.ApplyPrefix", null);
        }

        List<String> prefixIdList = new ArrayList<>();
        for (Prefix prefix : playerPrefix.getPrefixList()) {
            prefixIdList.add(prefix.getId());
        }
        config.set("Prefix.List", prefixIdList);

        //
        config.set("Tags", tagList);

        //

        playerDataFile.saveConfig();
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

    public void setTempData(String key, Object object) {
        tempDataMap.put(key, object);
    }

    public Object getTempData(String key) {
        return tempDataMap.getOrDefault(key, null);
    }

    public Object getTempData(String key, Object def) {
        Object object = getTempData(key);
        if (object == null) {
            return def;
        }
        return object;
    }

    public PlayerPrefix getPlayerPrefix() {
        return playerPrefix;
    }

    public boolean hasTag(String tag) {
        return tagList.contains(tag);
    }

    public void addTag(String tag) {
        tagList.add(tag);
    }

    public void removeTag(String tag) {
        tagList.remove(tag);
    }

}
