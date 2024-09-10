package me.msicraft.towerRpg.Dungeon.Event;

import me.msicraft.towerRpg.PlayerData.Data.PlayerData;
import me.msicraft.towerRpg.TowerRpg;
import net.playavalon.mythicdungeons.api.events.dungeon.PlayerFinishDungeonEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DungeonRelatedEvent implements Listener {

    private final TowerRpg plugin;

    public DungeonRelatedEvent(TowerRpg plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void playerFinishDungeonEvent(PlayerFinishDungeonEvent e) {
        Player player = e.getPlayer();
        String dungeonKey = e.getDungeon().getWorldName();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        playerData.setData(dungeonKey, System.currentTimeMillis());
    }

}
