package me.msicraft.towerRpg.Shop.Task;

import me.msicraft.towerRpg.Shop.Data.ShopItem;
import me.msicraft.towerRpg.Shop.ShopManager;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public class ShopTimerTask extends BukkitRunnable {

    private final TowerRpg plugin;
    private final ShopManager shopManager;
    private final int totalTicks;
    private int maintenanceTicks = -1;

    private int ticks = 0;

    public ShopTimerTask(TowerRpg plugin, ShopManager shopManager, int totalTicks) {
        this.plugin = plugin;
        this.shopManager = shopManager;
        this.totalTicks = totalTicks;

        this.maintenanceTicks = totalTicks - (20 * 60);

        this.runTaskTimerAsynchronously(plugin, 0, 20);
    }

    @Override
    public void run() {
        ticks++;
        if (maintenanceTicks != -1 && ticks >= maintenanceTicks) {
            maintenanceTicks = -1;
            shopManager.setShopMaintenance(true);
            Bukkit.getScheduler().runTask(plugin, shopManager::closeShopInventory);
            return;
        }

        if (ticks >= totalTicks) {
            shopManager.setShopMaintenance(false);

            Set<String> internalNames = shopManager.getInternalNameSet();
            Bukkit.getScheduler().runTask(plugin, () -> {
                for (String s : internalNames) {
                    ShopItem shopItem = shopManager.getShopItem(s);
                    if (shopItem != null) {
                        shopItem.updatePrice();
                    }
                }
            });
            ticks = 0;
        }
    }

}
