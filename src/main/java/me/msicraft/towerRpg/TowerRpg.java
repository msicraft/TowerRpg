package me.msicraft.towerRpg;

import me.msicraft.towerRpg.Command.MainCommand;
import me.msicraft.towerRpg.Command.MainTabCompleter;
import me.msicraft.towerRpg.Dungeon.DungeonManager;
import me.msicraft.towerRpg.Dungeon.Menu.Event.DungeonMenuEvent;
import me.msicraft.towerRpg.Event.EntityRelatedEvent;
import me.msicraft.towerRpg.Menu.Event.MenuGuiEvent;
import me.msicraft.towerRpg.Party.Menu.Event.PartyMenuEvent;
import me.msicraft.towerRpg.Party.PartyManager;
import me.msicraft.towerRpg.PlayerData.Data.PlayerData;
import me.msicraft.towerRpg.PlayerData.Event.PlayerDataRelatedEvent;
import me.msicraft.towerRpg.PlayerData.PlayerDataManager;
import me.msicraft.towerRpg.Prefix.Data.Prefix;
import me.msicraft.towerRpg.Prefix.File.PrefixDataFile;
import me.msicraft.towerRpg.Prefix.PrefixManager;
import me.msicraft.towerRpg.Shop.File.ShopDataFile;
import me.msicraft.towerRpg.Shop.Menu.Event.ShopMenuEvent;
import me.msicraft.towerRpg.Shop.ShopManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class TowerRpg extends JavaPlugin {

    private static TowerRpg plugin;

    public static TowerRpg getPlugin() {
        return plugin;
    }

    public static final String PREFIX = ChatColor.GREEN + "[TowerRpg] ";

    private Economy economy;

    private PlayerDataManager playerDataManager;
    private PrefixManager prefixManager;
    private ShopManager shopManager;
    private DungeonManager dungeonManager;
    private PartyManager partyManager;

    private PrefixDataFile prefixDataFile;
    private ShopDataFile shopDataFile;

    @Override
    public void onEnable() {
        plugin = this;
        createConfigFile();

        this.prefixDataFile = new PrefixDataFile(this);
        this.shopDataFile = new ShopDataFile(this);

        this.playerDataManager = new PlayerDataManager(this);
        this.prefixManager = new PrefixManager(this);
        this.shopManager = new ShopManager(this);
        this.dungeonManager = new DungeonManager(this);
        this.partyManager = new PartyManager(this);

        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registeredEvents();
        registeredCommands();

        reloadVariables();

        getServer().getConsoleSender().sendMessage(PREFIX + ChatColor.GREEN + "플러그인이 활성화 되었습니다");
    }

    @Override
    public void onDisable() {
        shopManager.saveShopData();

        getServer().getConsoleSender().sendMessage(PREFIX + ChatColor.RED + "플러그인이 비 활성화 되었습니다");
    }

    public void registeredEvents() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerDataRelatedEvent(this), this);
        pluginManager.registerEvents(new MenuGuiEvent( this), this);
        pluginManager.registerEvents(new ShopMenuEvent(this), this);
        pluginManager.registerEvents(EntityRelatedEvent.getInstance(), this);
        pluginManager.registerEvents(new DungeonMenuEvent(this), this);
        pluginManager.registerEvents(new PartyMenuEvent(this), this);
    }

    public void registeredCommands() {
        PluginCommand pluginCommand = Bukkit.getPluginCommand("towerrpg");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(new MainCommand(this));
            pluginCommand.setTabCompleter(new MainTabCompleter(this));
        }
    }

    public void reloadVariables() {
        reloadConfig();
        prefixDataFile.reloadConfig();

        prefixManager.update();
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData playerData = playerDataManager.getPlayerData(player);
            Prefix prefix = playerData.getPlayerPrefix().getPrefix();

            prefixManager.applyPrefix(player, prefix);
        }

        shopDataFile.reloadConfig();
        shopManager.reloadVariables();
        EntityRelatedEvent.getInstance().reloadVariables();
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

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public Economy getEconomy() {
        return economy;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public PrefixManager getPrefixManager() {
        return prefixManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public DungeonManager getDungeonManager() {
        return dungeonManager;
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }

    public PrefixDataFile getPrefixDataFile() {
        return prefixDataFile;
    }

    public ShopDataFile getShopDataFile() {
        return shopDataFile;
    }

}

