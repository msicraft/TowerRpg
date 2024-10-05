package me.msicraft.towerRpg;

import me.msicraft.towerRpg.API.MMOItems.MMOItemsPriceManager;
import me.msicraft.towerRpg.API.MythicMobs.MythicMobsRegisterEvent;
import me.msicraft.towerRpg.Command.MainCommand;
import me.msicraft.towerRpg.Command.MainTabCompleter;
import me.msicraft.towerRpg.DisableItem.DisableItemManger;
import me.msicraft.towerRpg.DisableItem.Event.DisableItemEvent;
import me.msicraft.towerRpg.Dungeon.DungeonManager;
import me.msicraft.towerRpg.Dungeon.Event.DungeonRelatedEvent;
import me.msicraft.towerRpg.Dungeon.Menu.Event.DungeonMenuEvent;
import me.msicraft.towerRpg.Event.EntityRelatedEvent;
import me.msicraft.towerRpg.Event.PlayerRelatedEvent;
import me.msicraft.towerRpg.Menu.Event.MenuGuiEvent;
import me.msicraft.towerRpg.Party.Menu.Event.PartyMenuEvent;
import me.msicraft.towerRpg.Party.PartyManager;
import me.msicraft.towerRpg.PlayerData.Data.PlayerData;
import me.msicraft.towerRpg.PlayerData.Event.PlayerDataRelatedEvent;
import me.msicraft.towerRpg.PlayerData.PlayerDataManager;
import me.msicraft.towerRpg.Prefix.Data.Prefix;
import me.msicraft.towerRpg.Prefix.PrefixManager;
import me.msicraft.towerRpg.Shop.Menu.Event.ShopMenuEvent;
import me.msicraft.towerRpg.Shop.ShopManager;
import me.msicraft.towerRpg.SkillBook.Event.SkillBookRelatedEvent;
import me.msicraft.towerRpg.SkillBook.SkillBookManager;
import net.milkbowl.vault.economy.Economy;
import net.playavalon.mythicdungeons.api.MythicDungeonsService;
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
    private SkillBookManager skillBookManager;
    private DisableItemManger disableItemManger;
    private MMOItemsPriceManager mmoItemsPriceManager;

    @Override
    public void onEnable() {
        plugin = this;
        createConfigFile();

        this.playerDataManager = new PlayerDataManager(this);
        this.prefixManager = new PrefixManager(this);
        this.shopManager = new ShopManager(this);
        this.dungeonManager = new DungeonManager(this);
        this.partyManager = new PartyManager(this);
        this.skillBookManager = new SkillBookManager(this);
        this.disableItemManger = new DisableItemManger(this);
        this.mmoItemsPriceManager = new MMOItemsPriceManager(this);

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
    }

    public void registeredEvents() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerDataRelatedEvent(this), this);
        pluginManager.registerEvents(new MenuGuiEvent( this), this);
        pluginManager.registerEvents(new ShopMenuEvent(this), this);
        pluginManager.registerEvents(EntityRelatedEvent.getInstance(), this);
        pluginManager.registerEvents(new DungeonMenuEvent(this), this);
        pluginManager.registerEvents(new PartyMenuEvent(this), this);
        pluginManager.registerEvents(new DungeonRelatedEvent(this), this);
        pluginManager.registerEvents(new SkillBookRelatedEvent(this), this);
        pluginManager.registerEvents(new MythicMobsRegisterEvent(this), this);
        pluginManager.registerEvents(new DisableItemEvent(this), this);
        pluginManager.registerEvents(PlayerRelatedEvent.getInstance(), this);
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
        partyManager.reloadVariables();
        dungeonManager.reloadVariables();
        shopManager.reloadVariables();

        prefixManager.getPrefixDataFile().reloadConfig();
        prefixManager.update();
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData playerData = playerDataManager.getPlayerData(player);
            Prefix prefix = playerData.getPlayerPrefix().getPrefix();

            prefixManager.applyPrefix(player, prefix);
        }


        EntityRelatedEvent.getInstance().reloadVariables();
        PlayerRelatedEvent.getInstance().reloadVariables();
        skillBookManager.reloadVariables();
        disableItemManger.reloadVariables();
        mmoItemsPriceManager.reloadVariables();
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

    public static MythicDungeonsService mythicDungeonsAPI() {
        return Bukkit.getServer().getServicesManager().load(MythicDungeonsService.class);
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

    public SkillBookManager getSkillBookManager() {
        return skillBookManager;
    }

    public DisableItemManger getDisableItemManger() {
        return disableItemManger;
    }

    public MMOItemsPriceManager getMMOItemsPriceManager() {
        return mmoItemsPriceManager;
    }

}

