package me.msicraft.towerRpg.Shop.Event;

import me.msicraft.towerRpg.PlayerData.Data.PlayerData;
import me.msicraft.towerRpg.Shop.Data.SellItemSlot;
import me.msicraft.towerRpg.Shop.Data.ShopItem;
import me.msicraft.towerRpg.Shop.ShopInventory;
import me.msicraft.towerRpg.Shop.ShopManager;
import me.msicraft.towerRpg.TowerRpg;
import me.msicraft.towerRpg.Utils.GuiUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ShopInventoryEvent implements Listener {

    private final TowerRpg plugin;

    public ShopInventoryEvent(TowerRpg plugin) {
        this.plugin = plugin;
    }

    private final NamespacedKey buyKey = new NamespacedKey(TowerRpg.getPlugin(), "ShopInventory_Buy");
    private final NamespacedKey sellKey = new NamespacedKey(TowerRpg.getPlugin(), "ShopInventory_Sell");

    @EventHandler
    public void shopInventoryClose(InventoryCloseEvent e) {
        if (e.getReason() == InventoryCloseEvent.Reason.OPEN_NEW) {
            return;
        }
        Inventory topInventory = e.getView().getTopInventory();
        if (topInventory.getHolder(false) instanceof ShopInventory shopInventory) {
            Player player = (Player) e.getPlayer();
            PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
            SellItemSlot[] sellItemSlots = (SellItemSlot[]) playerData.getTempData("ShopInventory_Sell_Stacks", null);
            if (sellItemSlots != null) {
                for (SellItemSlot sellItemSlot : sellItemSlots) {
                    if (sellItemSlot != null) {
                        player.getInventory().addItem(sellItemSlot.getItemStack());
                    }
                }
            }
            playerData.getTempData("ShopInventory_Sell_Stacks", null);
        }
    }

    @EventHandler
    public void shopInventoryClick(InventoryClickEvent e) {
        Inventory topInventory = e.getView().getTopInventory();
        if (topInventory.getHolder(false) instanceof ShopInventory shopInventory) {
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
            PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
            ShopManager shopManager = plugin.getShopManager();
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            if (dataContainer.has(buyKey)) {
                String data = dataContainer.get(buyKey, PersistentDataType.STRING);
                if (data != null) {
                    int maxPage = shopManager.getInternalNameSet().size() / 45;
                    int current = (int) playerData.getTempData("ShopInventory_Page", 1);
                    switch (data) {
                        case "Next" -> {
                            int next = current + 1;
                            if (next > maxPage) {
                                next = 0;
                            }
                            playerData.setTempData("ShopInventory_Page", next);
                            shopManager.openShopInventory(player, 0);
                        }
                        case "Previous" -> {
                            int previous = current - 1;
                            if (previous < 0) {
                                previous = maxPage;
                            }
                            playerData.setTempData("ShopInventory_Page", previous);
                            shopManager.openShopInventory(player, 0);
                        }
                        case "Sell" -> {
                            shopManager.openShopInventory(player, 1);
                        }
                        default -> {
                            if (e.isLeftClick()) {
                                ShopItem shopItem = shopManager.getShopItem(data);
                                if (shopItem != null) {
                                    int amount = (int) playerData.getTempData("ShopInventory_" + data + "_SelectCount", 1);
                                    shopManager.buyShopItem(player, data, amount);
                                }
                            }
                        }
                    }
                    return;
                }
            } else if (dataContainer.has(sellKey)) {
                String data = dataContainer.get(sellKey, PersistentDataType.STRING);
                if (data != null) {
                    switch (data) {
                        case "Back" -> {
                            shopManager.openShopInventory(player, 0);
                        }
                        case "SellConfirm" -> {
                            SellItemSlot[] sellItemSlots = (SellItemSlot[]) playerData.getTempData("ShopInventory_Sell_Stacks", null);
                            if (sellItemSlots != null) {
                                double totalPrice = 0;
                                for (SellItemSlot sellItemSlot : sellItemSlots) {
                                    if (sellItemSlot != null) {
                                        totalPrice = totalPrice + sellItemSlot.getTotalPrice();
                                    }
                                }
                                plugin.getEconomy().depositPlayer(player, totalPrice);
                                playerData.setTempData("ShopInventory_Sell_Stacks", null);
                                player.sendMessage(ChatColor.GREEN + "모든 아이템이 판매되었습니다.");
                                shopManager.openShopInventory(player, 1);
                            } else {
                                player.sendMessage(ChatColor.RED + "판매할 아이템이 없습니다.");
                            }
                        }
                    }
                    return;
                }
            }
            ItemStack sellInvCheckStack = shopInventory.getInventory().getItem(49);
            if (sellInvCheckStack != null && sellInvCheckStack.getType() == Material.CHEST) {
                InventoryType inventoryType = e.getClickedInventory().getType();
                ItemStack selectItemStack = e.getCurrentItem();
                if (selectItemStack != null && selectItemStack.getType() != Material.AIR) {
                    SellItemSlot[] sellItemSlots = (SellItemSlot[]) playerData.getTempData("ShopInventory_Sell_Stacks", null);
                    if (sellItemSlots == null) {
                        sellItemSlots = new SellItemSlot[45];
                    }
                    int clickSlot = e.getSlot();
                    switch (inventoryType) {
                        case PLAYER -> {
                            int emptySlot = -1;
                            for (int i = 0; i<sellItemSlots.length; i++) {
                                SellItemSlot sellItemSlot = sellItemSlots[i];
                                if (sellItemSlot == null || sellItemSlot.getItemStack().getType() == Material.AIR) {
                                    emptySlot = i;
                                    break;
                                }
                            }
                            if (emptySlot != -1) {
                                ShopItem shopItem = shopManager.searchShopItem(selectItemStack);
                                if (shopItem != null) {
                                    SellItemSlot sellItemSlot = new SellItemSlot(selectItemStack, (shopItem.getPrice(true) * selectItemStack.getAmount()));
                                    sellItemSlots[emptySlot] = sellItemSlot;

                                    player.getInventory().setItem(clickSlot, GuiUtil.AIR_STACK);
                                } else {
                                    player.sendMessage(ChatColor.RED + "해당 아이템의 판매가격이 0 이하 입니다.");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "빈 슬롯이 없습니다");
                            }
                        }
                        case CHEST -> {
                            int emptySlot = player.getInventory().firstEmpty();
                            if (emptySlot != -1) {
                                SellItemSlot sellItemSlot = sellItemSlots[clickSlot];
                                if (sellItemSlot != null) {
                                    player.getInventory().addItem(sellItemSlot.getItemStack());
                                    sellItemSlots[clickSlot] = null;
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "인벤토리에 빈 공간이 없습니다.");
                            }
                        }
                    }
                    playerData.setTempData("ShopInventory_Sell_Stacks", sellItemSlots);
                    shopManager.openShopInventory(player, 1);
                }
            }
        }
    }

}
