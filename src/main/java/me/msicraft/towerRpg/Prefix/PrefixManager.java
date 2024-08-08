package me.msicraft.towerRpg.Prefix;

import me.msicraft.towerRpg.Prefix.File.PrefixDataFile;
import me.msicraft.towerRpg.TowerRpg;

import java.util.HashMap;
import java.util.Map;

public class PrefixManager {

    private final TowerRpg plugin;

    public PrefixManager(TowerRpg plugin) {
        this.plugin = plugin;
    }

    private final Map<String, Prefix> prefixMap = new HashMap<>();

    public void updatePrefixData() {
        PrefixDataFile prefixDataFile = plugin.getPrefixDataFile();
    }

}
