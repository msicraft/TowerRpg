package me.msicraft.towerRpg.Event;

import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerRelatedEvent implements Listener {

    private static PlayerRelatedEvent instance = null;

    public static PlayerRelatedEvent getInstance() {
        if (instance == null) {
            instance = new PlayerRelatedEvent();
        }
        return instance;
    }

    private final TowerRpg plugin;

    private PlayerRelatedEvent() {
        this.plugin = TowerRpg.getPlugin();
    }

    private boolean disableBoneMeal = false;

    public void reloadVariables() {
        FileConfiguration config = plugin.getConfig();
        this.disableBoneMeal = config.contains("Setting.DisableBoneMeal") && config.getBoolean("Setting.DisableBoneMeal");
    }

    @EventHandler
    public void disableBoneMeal(PlayerInteractEvent e) {
        if (disableBoneMeal) {
            if (e.getPlayer().isOp()) {
                return;
            }
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                ItemStack itemStack = e.getItem();
                if (itemStack != null && itemStack.getType() == Material.BONE_MEAL) {
                    e.setCancelled(true);
                }
            }
        }
    }

}
