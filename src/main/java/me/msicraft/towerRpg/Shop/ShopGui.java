package me.msicraft.towerRpg.Shop;

import me.msicraft.towerRpg.PlayerData.Data.CustomGui;
import me.msicraft.towerRpg.PlayerData.Data.PlayerData;
import me.msicraft.towerRpg.Shop.Data.SellItemSlot;
import me.msicraft.towerRpg.Shop.Data.ShopItem;
import me.msicraft.towerRpg.TowerRpg;
import me.msicraft.towerRpg.Utils.GuiUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ShopGui extends CustomGui {

    private final Inventory gui;

    public ShopGui() {
        this.gui = Bukkit.createInventory(this, 54, Component.text("상점"));
    }

    public void setShopBuyInv(Player player) {
        gui.clear();

        String dataTag = "ShopInventory_Buy";
        ItemStack itemStack;
        itemStack = GuiUtil.createItemStack(Material.ARROW, "다음 페이지", GuiUtil.EMPTY_LORE, -1, dataTag, "Next");
        gui.setItem(48, itemStack);
        itemStack = GuiUtil.createItemStack(Material.ARROW, "이전 페이지", GuiUtil.EMPTY_LORE, -1, dataTag, "Previous");
        gui.setItem(50, itemStack);

        itemStack = GuiUtil.createItemStack(Material.CHEST, "아이템 판매", GuiUtil.EMPTY_LORE, -1, dataTag, "Sell");
        gui.setItem(45, itemStack);

        PlayerData playerData = TowerRpg.getPlugin().getPlayerDataManager().getPlayerData(player);
        ShopManager shopManager = TowerRpg.getPlugin().getShopManager();

        List<String> internalNames = shopManager.getInternalNameList();
        int maxSize = internalNames.size();
        int page = 0;
        Object pageObject = playerData.getTempData("ShopInventory_Page");
        if (pageObject instanceof Integer) {
            page = (int) pageObject;
        }
        int guiCount = 0;
        int lastCount = page * 45;

        String pageS = "페이지: " + (page + 1) + "/" + ((maxSize / 45) + 1);
        itemStack = GuiUtil.createItemStack(Material.BOOK, pageS, GuiUtil.EMPTY_LORE, -1, dataTag, "Page");
        gui.setItem(49, itemStack);

        for (int a = lastCount; a < maxSize; a++) {
            String internalName = internalNames.get(a);
            ShopItem shopItem = shopManager.getShopItem(internalName);
            if (shopItem != null) {
                int selectCount = 1;
                Object selectCountObject = playerData.getTempData("ShopInventory_" + internalName + "_SelectCount");
                if (selectCountObject instanceof Integer) {
                    selectCount = (int) selectCountObject;
                }
                ItemStack cloneStack = new ItemStack(shopItem.getItemStack());
                ItemMeta itemMeta = cloneStack.getItemMeta();
                PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text(ChatColor.WHITE + "현재 가격: " + shopItem.getPrice(false)));
                lore.add(Component.text(ChatColor.WHITE + "남은 재고: " + shopItem.getStock()));
                lore.add(Component.text(""));
                lore.add(Component.text(ChatColor.YELLOW + "선택된 개수: " + selectCount));
                lore.add(Component.text(""));
                lore.add(Component.text(ChatColor.YELLOW + "좌 클릭:" + ChatColor.GREEN + " 구매"));
                lore.add(Component.text(ChatColor.YELLOW + "우 클릭:" + ChatColor.GREEN + " 개수 입력"));

                dataContainer.set(new NamespacedKey(TowerRpg.getPlugin(), dataTag), PersistentDataType.STRING, internalName);

                itemMeta.lore(lore);
                cloneStack.setItemMeta(itemMeta);
                gui.setItem(guiCount, cloneStack);
                guiCount++;
                if (guiCount >= 45) {
                    break;
                }
            }
        }
    }

    public void setShopSellInv(Player player) {
        gui.clear();

        String dataTag = "ShopInventory_Sell";
        ItemStack itemStack;
        itemStack = GuiUtil.createItemStack(Material.BARRIER, "뒤로", GuiUtil.EMPTY_LORE, -1, dataTag, "Back");
        gui.setItem(45, itemStack);

        PlayerData playerData = TowerRpg.getPlugin().getPlayerDataManager().getPlayerData(player);
        ShopManager shopManager = TowerRpg.getPlugin().getShopManager();
        double totalPrice = 0;
        SellItemSlot[] sellItemSlots = (SellItemSlot[]) playerData.getTempData("ShopInventory_Sell_Stacks", null);
        if (sellItemSlots != null) {
            int size = sellItemSlots.length;
            for (int i = 0; i < size; i++) {
                SellItemSlot sellItemSlot = sellItemSlots[i];
                if (sellItemSlot != null) {
                    ItemStack sellStack = sellItemSlot.getItemStack();
                    if (sellStack == null || sellStack.getType() == Material.AIR) {
                        continue;
                    }
                    ItemStack cloneStack = new ItemStack(sellStack);
                    ItemMeta itemMeta = cloneStack.getItemMeta();
                    PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
                    //dataContainer.set(new NamespacedKey(TowerRpg.getPlugin(), dataTag), PersistentDataType.STRING, "Sell_ItemStack");

                    double price = 0;
                    ShopItem shopItem = shopManager.searchShopItem(cloneStack);
                    if (shopItem != null) {
                        price = shopItem.getPrice(true) * cloneStack.getAmount();
                        price = Math.round(price * 100.0) / 100.0;
                    }
                    List<Component> lore = new ArrayList<>();
                    lore.add(Component.text(ChatColor.GREEN + "판매 가격: " + price));
                    itemMeta.lore(lore);
                    cloneStack.setItemMeta(itemMeta);

                    gui.setItem(i, cloneStack);
                    totalPrice = totalPrice + price;
                }
            }
        }
        List<String> sellConfirmLore = new ArrayList<>();
        sellConfirmLore.add(ChatColor.GREEN + "총 판매 가격: " + (Math.round(totalPrice * 100.0) / 100.0));
        itemStack = GuiUtil.createItemStack(Material.CHEST, "판매 확인", sellConfirmLore, -1, dataTag, "SellConfirm");
        gui.setItem(49, itemStack);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.gui;
    }

}
