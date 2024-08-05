package me.msicraft.towerRpg.PlayerData;

import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerDataManager{

    private final TowerRpg plugin;

    public PlayerDataManager(TowerRpg plugin) {
        this.plugin = plugin;
    }

    private final Map<UUID, PlayerData> registeredPlayerDataMap = new HashMap<>();

    public void registerPlayerData(Player player) {
        PlayerData playerData = new PlayerData(player);
        registeredPlayerDataMap.put(player.getUniqueId(), playerData);
    }

    public void unregisterPlayerData(Player player) {
        registeredPlayerDataMap.remove(player.getUniqueId());
    }

    public PlayerData getPlayerData(Player player) {
        return registeredPlayerDataMap.getOrDefault(player.getUniqueId(), new PlayerData(player));
    }

    public Set<UUID> getUUIDSets() {
        return registeredPlayerDataMap.keySet();
    }
}
