package me.msicraft.towerRpg;

import me.msicraft.towerRpg.PlayerData.Event.PlayerDataRelatedEvent;
import me.msicraft.towerRpg.PlayerData.Menu.MenuGuiEvent;
import me.msicraft.towerRpg.PlayerData.PlayerDataManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class TowerRpg extends JavaPlugin {

    private static TowerRpg plugin;

    public static TowerRpg getPlugin() {
        return plugin;
    }

    public static final String PREFIX = ChatColor.GREEN + "[TowerRpg] ";

    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        plugin = this;
        createConfigFile();

        this.playerDataManager = new PlayerDataManager(this);

        registeredEvents();
        registeredCommands();

        reloadVariables();

        getServer().getConsoleSender().sendMessage(PREFIX + ChatColor.GREEN + "플러그인이 활성화 되었습니다");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(PREFIX + ChatColor.RED + "플러그인이 비 활성화 되었습니다");
    }

    public void registeredEvents() {
        getServer().getPluginManager().registerEvents(new PlayerDataRelatedEvent(this), this);
        getServer().getPluginManager().registerEvents(new MenuGuiEvent( this), this);
    }

    public void registeredCommands() {
    }

    public void reloadVariables() {
        reloadConfig();
    }

    private void createConfigFile() {
        File configf = new File(getDataFolder(), "config.yml");
        if (!configf.exists()) {
            configf.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(configf);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
}
