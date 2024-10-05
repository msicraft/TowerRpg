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

    public void register(String mmoItemsId, double price) {
        priceMap.put(mmoItemsId, price);
        String path = "Data." + mmoItemsId;
        mmoItemsPriceData.getConfig().set(path, price);
        mmoItemsPriceData.saveConfig();
    }

    public void unregister(String mmoItemsId) {
        priceMap.remove(mmoItemsId);
        String path = "Data." + mmoItemsId;
        if (mmoItemsPriceData.getConfig().contains(path)) {
            mmoItemsPriceData.getConfig().set(path, null);
            mmoItemsPriceData.saveConfig();
        }
    }

    public boolean hasPrice(String mmoItemsId) {
        return priceMap.containsKey(mmoItemsId);
    }

    public void setPrice(String mmoItemsId, double price) {
        if (hasPrice(mmoItemsId)) {
            priceMap.put(mmoItemsId, price);
            String path = "Data." + mmoItemsId;
            mmoItemsPriceData.getConfig().set(path, price);
            mmoItemsPriceData.saveConfig();
        }
    }

    public double getPrice(String mmoItemsId) {
        if (hasPrice(mmoItemsId)) {
            return priceMap.get(mmoItemsId);
        }
        return 0;
    }

    public Set<String> getMMOItemsIds() {
        return priceMap.keySet();
    }

    public MMOItemsPriceData getMMOItemsPriceData() {
        return mmoItemsPriceData;
    }

}
