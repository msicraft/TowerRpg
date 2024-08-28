package me.msicraft.towerRpg.Dungeon.Menu.Event;

import me.msicraft.towerRpg.Dungeon.Menu.DungeonGui;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class DungeonMenuEvent implements Listener {

    private final TowerRpg plugin;

    public DungeonMenuEvent(TowerRpg plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void DungeonInventoryClick(InventoryClickEvent e) {
        Inventory topInventory = e.getView().getTopInventory();
        if (topInventory.getHolder(false) instanceof DungeonGui dungeonGui) {
            ClickType type = e.getClick();
            if (type == ClickType.NUMBER_KEY || type == ClickType.SWAP_OFFHAND
                    || type == ClickType.SHIFT_LEFT || type == ClickType.SHIFT_RIGHT) {
                e.setCancelled(true);
                return;
            }
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            ItemStack itemStack = e.getCurrentItem();
            if (itemStack == null) {
                return;
            }
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) {
                return;
            }
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        }
    }

}
