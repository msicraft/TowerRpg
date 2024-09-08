package me.msicraft.towerRpg.PlayerData.Event;

import me.msicraft.towerRpg.API.CustomEvent.PlayerDataLoadEvent;
import me.msicraft.towerRpg.API.CustomEvent.PlayerDataUnloadEvent;
import me.msicraft.towerRpg.Party.Data.Party;
import me.msicraft.towerRpg.PlayerData.Data.PlayerData;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDataRelatedEvent implements Listener {

    private final TowerRpg plugin;

    public PlayerDataRelatedEvent(TowerRpg plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        Player player = e.getPlayer();

        if (e.getResult() == PlayerLoginEvent.Result.ALLOWED) {
            plugin.getPlayerDataManager().registerPlayerData(player);
        }
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);

        playerData.loadData();

        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.getPluginManager().callEvent(new PlayerDataLoadEvent(playerData));
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        playerData.saveData();

        plugin.getPlayerDataManager().unregisterPlayerData(player);

        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.getPluginManager().callEvent(new PlayerDataUnloadEvent(playerData));
        });
    }

    @EventHandler
    public void playerDataLoad(PlayerDataLoadEvent e) {
        PlayerData playerData = e.getPlayerData();
    }

    @EventHandler
    public void playerDataUnload(PlayerDataUnloadEvent e) {
        PlayerData playerData = e.getPlayerData();

        if (playerData.hasParty()) {
            Party party = playerData.getParty();
            if (party.getLeaderUUID().equals(playerData.getPlayer().getUniqueId())) {
                party.disband();
            } else {
                party.removePlayer(playerData.getPlayer());
            }
        }
    }

}
