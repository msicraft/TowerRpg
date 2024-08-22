package me.msicraft.towerRpg.Prefix;

import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.StatMap;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import me.msicraft.towerRpg.API.CustomEvent.PrefixChangeEvent;
import me.msicraft.towerRpg.PlayerData.Data.PlayerData;
import me.msicraft.towerRpg.Prefix.Data.Prefix;
import me.msicraft.towerRpg.Prefix.Data.StatValueType;
import me.msicraft.towerRpg.Prefix.File.PrefixDataFile;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class PrefixManager {

    private final TowerRpg plugin;
    private final String PREFIX_KEY = "TowerRpgPrefixStat";

    public PrefixManager(TowerRpg plugin) {
        this.plugin = plugin;
        update();
    }

    private final Map<String, Prefix> prefixMap = new HashMap<>();

    public void update() {
        PrefixDataFile prefixDataFile = plugin.getPrefixDataFile();
        FileConfiguration config = prefixDataFile.getConfig();

        ConfigurationSection section = config.getConfigurationSection("Prefix");
        if (section != null) {
            Set<String> sets = section.getKeys(false);
            for (String id : sets) {
                Prefix prefix;
                if (prefixMap.containsKey(id)) {
                    prefix = prefixMap.get(id);
                    prefix.update(prefixDataFile);
                } else {
                    String path = "Prefix." + id;
                    String displayName = config.getString(path + ".DisplayName");
                    List<String> statList = config.getStringList(path + ".Stat");
                    prefix = new Prefix(id, displayName, statList);
                }
                prefixMap.put(id, prefix);
            }
        }
    }

    public Prefix getPrefix(String id) {
        if (prefixMap.containsKey(id)) {
            return prefixMap.get(id);
        }
        return null;
    }

    public void applyPrefix(Player player, Prefix prefix) {
        MMOPlayerData mmoPlayerData = MMOPlayerData.get(player.getUniqueId());
        StatMap statMap = mmoPlayerData.getStatMap();

        statMap.getInstances().forEach(statInstance -> statInstance.removeIf(PREFIX_KEY::equals));

        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        if (prefix != null) {
            playerData.getPlayerPrefix().setPrefix(prefix);
            Set<PrefixStat> statSet = prefix.getStats();
            for (PrefixStat stat : statSet) {
                ModifierType modifierType = ModifierType.FLAT;
                if (stat.getStatValueType() == StatValueType.MULTIPLY) {
                    modifierType = ModifierType.RELATIVE;
                }
                StatModifier statModifier = new StatModifier(PREFIX_KEY, stat.getStatName().toUpperCase(), stat.getValue(), modifierType);
                statModifier.register(mmoPlayerData);
            }
        }

        Bukkit.getPluginManager().callEvent(new PrefixChangeEvent(player, prefix));
    }

    public Set<Prefix> getPrefixes() {
        Set<Prefix> prefixes = new HashSet<>();
        for (String id : prefixMap.keySet()) {
            Prefix prefix = prefixMap.get(id);
            prefixes.add(prefix);
        }
        return prefixes;
    }

}
