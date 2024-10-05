package me.msicraft.towerRpg.Shop.Task;

import me.msicraft.towerRpg.Shop.Data.ShopItem;
import me.msicraft.towerRpg.Shop.ShopManager;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ShopTask extends BukkitRunnable {

    private final TowerRpg plugin;
    private final ShopManager shopManager;
    private final int updateSeconds;
    private int maintenanceSeconds;

    private int seconds;

    public ShopTask(TowerRpg plugin, ShopManager shopManager, int updateSeconds) {
        this.plugin = plugin;
        this.shopManager = shopManager;
        this.updateSeconds = updateSeconds;
        this.seconds = updateSeconds;

        this.maintenanceSeconds = updateSeconds - 60;
        if (maintenanceSeconds <= 0) {
            this.maintenanceSeconds = (int) (updateSeconds - (updateSeconds * 0.1));
        }

        this.runTaskTimerAsynchronously(plugin, 0, 20);
    }

    @Override
    public void run() {
        seconds--;
        if (maintenanceSeconds != -1 && seconds <= maintenanceSeconds) {
            maintenanceSeconds = -1;
            Bukkit.getScheduler().runTask(plugin, () -> {
                shopManager.setShopMaintenance(true);
                shopManager.closeShopInventory();
            });
            return;
        }

        if (seconds <= 0) {
            shopManager.getInternalNameList().forEach(s -> {
                ShopItem shopItem = shopManager.getShopItem(s);
                if (shopItem != null) {
                    shopItem.updatePrice(shopManager);
                }
            });

            shopManager.setShopMaintenance(false);
            seconds = updateSeconds;
        }
    }

}
