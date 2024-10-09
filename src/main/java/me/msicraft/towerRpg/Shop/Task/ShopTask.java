package me.msicraft.towerRpg.Shop.Task;

import me.msicraft.towerRpg.Shop.Data.ShopItem;
import me.msicraft.towerRpg.Shop.ShopManager;
import me.msicraft.towerRpg.TowerRpg;
import me.msicraft.towerRpg.Utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ShopTask extends BukkitRunnable {

    private final TowerRpg plugin;
    private final ShopManager shopManager;
    private final int updateSeconds;
    private int maintenanceSeconds;
    private boolean isMaintenance = false;

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

        if (plugin.useDebug()) {
            MessageUtil.sendDebugMessage("ShopTask-Init-Debug",
                    "Seconds: " + seconds, "UpdateSeconds: " + updateSeconds, "MaintenanceSeconds: " + maintenanceSeconds);
        }

        this.runTaskTimerAsynchronously(plugin, 0, 20);
    }

    @Override
    public void run() {
        seconds--;
        if (!isMaintenance && seconds <= maintenanceSeconds) {
            if (plugin.useDebug()) {
                MessageUtil.sendDebugMessage("ShopTask-Debug", "Start-Maintenance");
            }
            Bukkit.getScheduler().runTask(plugin, shopManager::closeShopInventory);
            shopManager.setShopMaintenance(true);
            isMaintenance = true;
            return;
        }

        if (seconds <= 0) {
            if (plugin.useDebug()) {
                MessageUtil.sendDebugMessage("ShopTask-Debug", "End-Maintenance");
            }
            shopManager.getInternalNameList().forEach(s -> {
                ShopItem shopItem = shopManager.getShopItem(s);
                if (shopItem != null) {
                    shopItem.updatePrice(shopManager);
                }
            });

            shopManager.setShopMaintenance(false);
            seconds = updateSeconds;
            isMaintenance = false;
        }
    }

}
