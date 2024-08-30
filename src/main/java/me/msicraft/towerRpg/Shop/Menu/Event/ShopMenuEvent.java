package me.msicraft.towerRpg.Shop.Menu.Event;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.msicraft.towerRpg.PlayerData.Data.PlayerData;
import me.msicraft.towerRpg.Shop.Data.SellItemSlot;
import me.msicraft.towerRpg.Shop.Data.ShopItem;
import me.msicraft.towerRpg.Shop.Menu.ShopGui;
import me.msicraft.towerRpg.Shop.ShopManager;
import me.msicraft.towerRpg.TowerRpg;
import me.msicraft.towerRpg.Utils.GuiUtil;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
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

public class ShopMenuEvent implements Listener {

    private final TowerRpg plugin;

    public ShopMenuEvent(TowerRpg plugin) {
        this.plugin = plugin;
    }

    private final NamespacedKey buyKey = new NamespacedKey(TowerRpg.getPlugin(), "ShopInventory_Buy");
    private final NamespacedKey sellKey = new NamespacedKey(TowerRpg.getPlugin(), "ShopInventory_Sell");

    @EventHandler
    public void shopInventoryChatEdit(AsyncChatEvent e) {
        Player player = e.getPlayer();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        String itemInternalName = (String) playerData.getTempData("ShopInventory_Edit_Amount", null);
        if (itemInternalName != null) {
            if (plugin.getShopManager().hasInternalName(itemInternalName)) {
                e.setCancelled(true);
                String message = PlainTextComponentSerializer.plainText().serialize(e.message());
                if (message.equalsIgnoreCase("cancel")) {
                    return;
                }
                int amount = Integer.parseInt(message.replaceAll("[^0-9]", ""));
                playerData.setTempData("ShopInventory_" + itemInternalName + "_SelectCount", amount);
            } else {
                player.sendMessage(ChatColor.RED + "잘못된 내부이름 데이터입니다. 관리자에게 문의해주시기바랍니다.");
            }
            playerData.removeTempData("ShopInventory_Edit_Amount");
            Bukkit.getScheduler().runTask(plugin, ()-> {
                plugin.getShopManager().openShopInventory(player, 0);
            });
        }
    }

    @EventHandler
    public void shopInventoryClose(InventoryCloseEvent e) {
        if (e.getReason() == InventoryCloseEvent.Reason.OPEN_NEW) {
            return;
        }
        Inventory topInventory = e.getView().getTopInventory();
        if (topInventory.getHolder(false) instanceof ShopGui shopGui) {
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

            ShopManager shopManager = plugin.getShopManager();
            shopManager.removeViewer(player);
        }
    }

    @EventHandler
    public void shopInventoryClick(InventoryClickEvent e) {
        Inventory topInventory = e.getView().getTopInventory();
        if (topInventory.getHolder(false) instanceof ShopGui shopGui) {
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
                            } else if (e.isRightClick()) {
                                player.sendMessage(ChatColor.GRAY + "========================================");
                                player.sendMessage(ChatColor.GRAY + "개수를 입력해주세요.");
                                player.sendMessage(ChatColor.GRAY + "'cancel' 입력시 취소");
                                player.sendMessage(ChatColor.GRAY + "========================================");

                                playerData.setTempData("ShopInventory_Edit_Amount", data);
                                player.closeInventory();
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
                            shopManager.sellShopItem(player);
                        }
                    }
                    return;
                }
            }
            ItemStack sellInvCheckStack = shopGui.getInventory().getItem(49);
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
                                    SellItemSlot sellItemSlot = new SellItemSlot(shopItem.getId(), selectItemStack, (shopItem.getPrice(true) * selectItemStack.getAmount()));
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
