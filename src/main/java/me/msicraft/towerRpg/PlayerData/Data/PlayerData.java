package me.msicraft.towerRpg.PlayerData.Data;

import me.msicraft.towerRpg.API.Data.CustomGui;
import me.msicraft.towerRpg.Menu.GuiType;
import me.msicraft.towerRpg.Menu.MenuGui;
import me.msicraft.towerRpg.PlayerData.File.PlayerDataFile;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerData {

    private final Player player;
    private final PlayerDataFile playerDataFile;

    private final Map<GuiType, CustomGui> customGuiMap = new HashMap<>();

    private final Map<String, Object> tempDataMap = new HashMap<>();
    private final Map<String, Object> dataMap = new HashMap<>();
    private final List<String> tagList = new ArrayList<>();

    private final PlayerPrefix playerPrefix;

    public PlayerData(Player player) {
        this.player = player;
        this.playerDataFile = new PlayerDataFile(player);
        this.playerPrefix = new PlayerPrefix(this);
    }

    public void loadData() {
        playerPrefix.loadData();

        FileConfiguration playerDataConfig = playerDataFile.getConfig();

        List<String> tags = playerDataConfig.getStringList("Tags");
        tagList.addAll(tags);

        ConfigurationSection dataSection = playerDataConfig.getConfigurationSection("Data");
        if (dataSection!= null) {
            Set<String> keys = dataSection.getKeys(false);
            for (String key : keys) {
                Object object = playerDataConfig.get("Data." + key);
                dataMap.put(key, object);
            }
        }
    }

    public void saveData() {
        playerPrefix.saveData();
        FileConfiguration playerDataConfig = playerDataFile.getConfig();

        playerDataConfig.set("Tags", tagList);

        Set<String> dataKeys = dataMap.keySet();
        for (String key : dataKeys) {
            Object value = dataMap.get(key);
            playerDataConfig.set("Data." + key, value);
        }

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
        if (!hasTempData(key) || object == null) {
            return def;
        }
        return object;
    }

    public boolean hasTempData(String key) {
        return tempDataMap.containsKey(key);
    }

    public void removeTempData(String key) {
        tempDataMap.remove(key);
    }

    public void setData(String key, Object object) {
        dataMap.put(key, object);
    }

    public Object getData(String key) {
        return dataMap.getOrDefault(key, null);
    }

    public Object getData(String key, Object def) {
        Object object = getData(key);
        if (!hasData(key) || object == null) {
            return def;
        }
        return object;
    }

    public boolean hasData(String key) {
        return dataMap.containsKey(key);
    }

    public void removeData(String key) {
        dataMap.remove(key);
    }

    public PlayerPrefix getPlayerPrefix() {
        return playerPrefix;
    }

    public boolean hasTag(String key) {
        return tagList.contains(key);
    }

    public void addTag(String key) {
        tagList.add(key);
    }

    public void removeTag(String key) {
        tagList.remove(key);
    }

}
