package me.msicraft.towerRpg.Menu.Event;

import me.msicraft.towerRpg.API.Data.CustomGui;
import me.msicraft.towerRpg.Dungeon.Data.DungeonType;
import me.msicraft.towerRpg.Menu.GuiType;
import me.msicraft.towerRpg.Menu.MenuGui;
import me.msicraft.towerRpg.Party.Menu.PartyGui;
import me.msicraft.towerRpg.Party.PartyManager;
import me.msicraft.towerRpg.PlayerData.Data.PlayerData;
import me.msicraft.towerRpg.PlayerData.PlayerDataManager;
import me.msicraft.towerRpg.Shop.Menu.ShopGui;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class MenuGuiEvent implements Listener {

    private final TowerRpg plugin;

    public MenuGuiEvent(TowerRpg plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void menuGuiOpenEvent(PlayerSwapHandItemsEvent e) {
        Player player = e.getPlayer();
        if (player.isSneaking()) {
            PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
            PlayerData playerData = playerDataManager.getPlayerData(player);
            CustomGui customGui = playerData.getCustomGui(GuiType.MAIN);
            if (customGui instanceof MenuGui menuGui) {
                player.openInventory(menuGui.getInventory());
            }
        }
    }

    @EventHandler
    public void mainMenuGuiClickEvent(InventoryClickEvent e) {
        Inventory topInventory = e.getView().getTopInventory();
        if (topInventory.getHolder(false) instanceof MenuGui menuGui) {
            ClickType type = e.getClick();
            if (type == ClickType.NUMBER_KEY || type == ClickType.SWAP_OFFHAND
                    || type == ClickType.SHIFT_LEFT || type == ClickType.SHIFT_RIGHT) {
                e.setCancelled(true);
                return;
            }
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
            PlayerData playerData = playerDataManager.getPlayerData(player);

            ItemStack itemStack = e.getCurrentItem();
            if (itemStack != null) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta != null) {
                    PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
                    if (dataContainer.has(new NamespacedKey(plugin, "MenuGui_Main"))) {
                        String data = dataContainer.get(new NamespacedKey(plugin, "MenuGui_Main"), PersistentDataType.STRING);
                        if (data != null) {
                            switch (data) {
                                case "RpgInventory" -> {
                                    Bukkit.dispatchCommand(player, "rpginventory");
                                }
                                case "RpgStat" -> {
                                    Bukkit.dispatchCommand(player, "stats");
                                }
                                case "RpgSkill" -> {
                                    Bukkit.dispatchCommand(player,"skills");
                                }
                                case "ShopInventory" -> {
                                    plugin.getShopManager().openShopInventory(player, ShopGui.Type.BUY);
                                }
                                case "Party" -> {
                                    PartyManager partyManager = plugin.getPartyManager();
                                    if (e.isLeftClick()) {
                                        partyManager.openPartyInventory(player, PartyGui.Type.SEARCH);
                                    } else if (e.isRightClick()) {
                                        if (playerData.hasParty()) {
                                            partyManager.openPartyInventory(player, PartyGui.Type.INFO);
                                        } else {
                                            player.sendMessage(ChatColor.RED + "파티가 없습니다.");
                                        }
                                    }
                                }
                                case "Dungeon" -> {
                                    plugin.getDungeonManager().openDugeonInventory(DungeonType.BEGINNING_TOWER, player);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
