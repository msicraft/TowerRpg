package me.msicraft.towerRpg.Prefix;

import me.msicraft.towerRpg.Prefix.File.PrefixDataFile;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PrefixManager {

    private final TowerRpg plugin;

    public PrefixManager(TowerRpg plugin) {
        this.plugin = plugin;
    }

    private final Map<String, Prefix> prefixMap = new HashMap<>();

    public void updatePrefixData() {
        PrefixDataFile prefixDataFile = plugin.getPrefixDataFile();
        FileConfiguration config = prefixDataFile.getConfig();

        ConfigurationSection section = config.getConfigurationSection("Prefix");
        if (section != null) {
            Set<String> sets = section.getKeys(false);
            for (String id : sets) {
                String path = "Prefix." + id;
                String displayName = config.getString(path + ".DisplayName");
                Prefix prefix = new Prefix(id, displayName, config.getStringList(path + ".Stat"));
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

}
