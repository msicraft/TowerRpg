package me.msicraft.towerRpg.DisableItem.Event;

import io.lumine.mythic.lib.api.item.NBTItem;
import me.msicraft.towerRpg.DisableItem.DisableItemManger;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class DisableItemEvent implements Listener {

    private final TowerRpg plugin;
    private final DisableItemManger disableItemManger;

    public DisableItemEvent(TowerRpg plugin) {
        this.plugin = plugin;
        this.disableItemManger = plugin.getDisableItemManger();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void disableCrafting(PrepareItemCraftEvent e) {
        if (disableItemManger.isEnabled()) {
            if (e.getView().getPlayer().isOp()) {
                return;
            }
            ItemStack result = e.getInventory().getResult();
            if (disableItemManger.isDisableMaterial(result)) {
                if (NBTItem.get(result).hasType()) {
                    return;
                }
                e.getInventory().setResult(null);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void disableItemPickup(EntityPickupItemEvent e) {
        if (disableItemManger.isEnabled()) {
            if (e.getEntity() instanceof Player player) {
                if (player.isOp()) {
                    return;
                }
                Item item = e.getItem();
                ItemStack itemStack = item.getItemStack();
                if (disableItemManger.isDisableMaterial(itemStack)) {
                    if (NBTItem.get(itemStack).hasType()) {
                        return;
                    }
                    e.setCancelled(true);
                    item.remove();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void disableItemInteraction(PlayerInteractEvent e) {
        if (disableItemManger.isEnabled()) {
            if (e.getPlayer().isOp()) {
                return;
            }
            ItemStack itemStack = e.getItem();
            if (disableItemManger.isDisableMaterial(itemStack)) {
                if (NBTItem.get(itemStack).hasType()) {
                    return;
                }
                e.setCancelled(true);
                itemStack.setAmount(0);
            }
        }
    }

}
