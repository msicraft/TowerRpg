package me.msicraft.towerRpg.API.MMOItems;

import me.msicraft.towerRpg.API.MMOItems.File.MMOItemsPriceData;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MMOItemsPriceManager {

    private final TowerRpg plugin;
    private final MMOItemsPriceData mmoItemsPriceData;

    public MMOItemsPriceManager(TowerRpg plugin) {
        this.plugin = plugin;
        this.mmoItemsPriceData = new MMOItemsPriceData(plugin);
    }

    private final Map<String, Double> priceMap = new HashMap<>();

    public void reloadVariables() {
        mmoItemsPriceData.reloadConfig();

        ConfigurationSection section = mmoItemsPriceData.getConfig().getConfigurationSection("Data");
        priceMap.clear();
        if (section != null) {
            Set<String> sets = section.getKeys(false);
            for (String internalname : sets) {
                String path = "Data." + internalname.toUpperCase();
                double price = mmoItemsPriceData.getConfig().getDouble(path, 0.0);
                priceMap.put(internalname, price);
            }
        }
    }

    public boolean hasPrice(String internalName) {
        return priceMap.containsKey(internalName);
    }

    public double getPrice(String internalName) {
        return priceMap.getOrDefault(internalName, 0.0);
    }

}
