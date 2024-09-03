package me.msicraft.towerRpg.Shop;

import me.msicraft.towerRpg.API.Data.CustomGuiManager;
import me.msicraft.towerRpg.Menu.GuiType;
import me.msicraft.towerRpg.PlayerData.Data.PlayerData;
import me.msicraft.towerRpg.Shop.Data.SellItemSlot;
import me.msicraft.towerRpg.Shop.Data.ShopItem;
import me.msicraft.towerRpg.Shop.File.ShopDataFile;
import me.msicraft.towerRpg.Shop.Menu.ShopGui;
import me.msicraft.towerRpg.Shop.Task.ShopTask;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ShopManager extends CustomGuiManager {

    private final TowerRpg plugin;

    private final Map<String, ShopItem> shopItemMap = new LinkedHashMap<>();

    private int updateTicks = 72000;
    private double maxPricePercent = 0.25;
    private double minPricePercent = 0.25;
    private int buyPriceChangeQuantity = 64;
    private int sellPriceChangeQuantity = 64;
    private double buyPriceChangePercent = 0.1;
    private double sellPriceChangePercent = 0.1;

    private boolean isShopMaintenance = false;
    private ShopTask shopTask = null;

    public ShopManager(TowerRpg plugin) {
        this.plugin = plugin;
        reloadVariables();
    }

    public void closeShopInventory() {
        for (UUID uuid : getViewers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "현재 상점 가격 조정 중입니다. 잠시 후 이용해 주시기를 바랍니다.");
            }
        }
        removeAll();
    }

    public void reloadVariables() {
        setShopMaintenance(true);

        closeShopInventory();

        saveShopData();

        FileConfiguration config = plugin.getConfig();

        this.maxPricePercent = config.getDouble("Setting.Shop.MaxPricePercent");
        this.minPricePercent = config.getDouble("Setting.Shop.MinPricePercent");
        this.buyPriceChangeQuantity = config.getInt("Setting.Shop.BuyPriceChangeQuantity");
        this.sellPriceChangeQuantity = config.getInt("Setting.Shop.SellPriceChangeQuantity");
        this.buyPriceChangePercent = config.getDouble("Setting.Shop.BuyPriceChangePercent");
        this.sellPriceChangePercent = config.getDouble("Setting.Shop.SellPriceChangePercent");
        this.updateTicks = config.getInt("Setting.Shop.UpdateTicks");

        loadShopData();

        if (shopTask != null) {
            shopTask.cancel();
            shopTask = null;
            setShopMaintenance(false);
        }
        shopTask = new ShopTask(plugin, this, updateTicks);
    }

    public void sendMaintenanceMessage(Player player) {
        player.sendMessage(ChatColor.RED + "현재 상점 가격 조정 중입니다. 잠시 후 이용해 주시기를 바랍니다.");
    }

    public void openShopInventory(Player player, int type) { // 0 = 기본, 1 = 판매
        if (isShopMaintenance) {
            player.closeInventory();
            sendMaintenanceMessage(player);
            return;
        }
        addViewer(player);

        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        ShopGui shopGui = (ShopGui) playerData.getCustomGui(GuiType.SHOP);
        player.openInventory(shopGui.getInventory());
        shopGui.setGui(player, type);
    }

    public void saveShopData() {
        ShopDataFile shopDataFile = plugin.getShopDataFile();
        FileConfiguration config = shopDataFile.getConfig();

        for (String key : shopItemMap.keySet()) {
            ShopItem shopItem = shopItemMap.get(key);
            String path = "Items." + key;
            config.set(path + ".ItemStack", shopItem.getItemStack());
            config.set(path + ".Stock", shopItem.getStock());
            config.set(path + ".BasePrice", shopItem.getBasePrice());
            config.set(path + ".Price", shopItem.getPrice(false));
            config.set(path + ".BuyQuantity", shopItem.getBuyQuantity());
            config.set(path + ".SellQuantity", shopItem.getSellQuantity());
        }

        shopDataFile.saveConfig();
    }

    public void loadShopData() {
        ShopDataFile shopDataFile = plugin.getShopDataFile();
        FileConfiguration config = shopDataFile.getConfig();

        ConfigurationSection itemSection = config.getConfigurationSection("Items");
        if (itemSection != null) {
            shopItemMap.clear();

            Set<String> internalNames = itemSection.getKeys(false);
            for (String key : internalNames) {
                String path = "Items." + key;
                ItemStack itemStack = config.getItemStack(path + ".ItemStack");
                int stock = config.getInt(path + ".Stock");
                double basePrice = config.getDouble(path + ".BasePrice");
                double price = config.getDouble(path + ".Price");
                int buyQuantity = config.getInt(path + ".BuyQuantity");
                int sellQuantity = config.getInt(path + ".SellQuantity");
                ShopItem shopItem = new ShopItem(key, itemStack, stock, basePrice, price, buyQuantity, sellQuantity);

                shopItemMap.put(key, shopItem);
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(TowerRpg.PREFIX + ChatColor.RED + "상점 데이터가 존재하지 않습니다");
        }
    }

    public void buyShopItem(Player player, String id, int amount) {
        ShopItem shopItem = getShopItem(id);
        if (shopItem != null) {
            if (!shopItem.hasEnoughStock(amount)) {
                player.sendMessage(ChatColor.RED + "아이템의 재고가 부족합니다.");
                return;
            }
            double totalPrice = shopItem.getPrice(true) * amount;
            double playerBalance = plugin.getEconomy().getBalance(player);
            if (playerBalance < totalPrice) {
                player.sendMessage(ChatColor.RED + "충분한 돈이 없습니다.");
                return;
            }
            plugin.getEconomy().withdrawPlayer(player, totalPrice);
            shopItem.addStock(-amount);
            shopItem.addBuyQuantity(amount);

            ItemStack itemStack = shopItem.getItemStack();
            for (int i = 0; i<amount; i++) {
                player.getInventory().addItem(itemStack);
            }
            player.sendMessage(ChatColor.GREEN + "아이템을 구매하였습니다.");
        } else {
            player.sendMessage(ChatColor.RED + "해당 아이템이 존재하지 않습니다");
        }
    }

    public void sellShopItem(Player player) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        SellItemSlot[] sellItemSlots = (SellItemSlot[]) playerData.getTempData("ShopInventory_Sell_Stacks", null);
        if (sellItemSlots != null) {
            double totalPrice = 0;
            for (SellItemSlot sellItemSlot : sellItemSlots) {
                if (sellItemSlot != null) {
                    ShopItem shopItem = getShopItem(sellItemSlot.getId());
                    totalPrice = totalPrice + sellItemSlot.getTotalPrice();

                    int amount = sellItemSlot.getItemStack().getAmount();
                    shopItem.addStock(amount);
                    shopItem.addSellQuantity(amount);
                }
            }
            plugin.getEconomy().depositPlayer(player, totalPrice);
            playerData.setTempData("ShopInventory_Sell_Stacks", null);

            player.sendMessage(ChatColor.GREEN + "모든 아이템이 판매되었습니다.");
            openShopInventory(player, 1);
        } else {
            player.sendMessage(ChatColor.RED + "판매할 아이템이 없습니다.");
        }
    }

    public ShopItem searchShopItem(ItemStack itemStack) {
        for (ShopItem shopItem : shopItemMap.values()) {
            if (shopItem.getItemStack().isSimilar(itemStack)) {
                return shopItem;
            }
        }
        return null;
    }

    public boolean hasInternalName(String internalName) {
        return shopItemMap.containsKey(internalName);
    }

    public ShopItem getShopItem(String internalName) {
        return shopItemMap.getOrDefault(internalName, null);
    }

    public void addShopItem(String internalName, ShopItem shopItem) {
        shopItemMap.put(internalName, shopItem);
    }

    public void removeShopItem(String internalName) {
        shopItemMap.remove(internalName);
    }

    public Set<String> getInternalNameSet() {
        return shopItemMap.keySet();
    }

    public List<String> getInternalNameList() {
        return List.copyOf(shopItemMap.keySet());
    }

    public double getMaxPricePercent() {
        return maxPricePercent;
    }

    public double getMinPricePercent() {
        return minPricePercent;
    }

    public int getBuyPriceChangeQuantity() {
        return buyPriceChangeQuantity;
    }

    public int getSellPriceChangeQuantity() {
        return sellPriceChangeQuantity;
    }

    public double getBuyPriceChangePercent() {
        return buyPriceChangePercent;
    }

    public double getSellPriceChangePercent() {
        return sellPriceChangePercent;
    }

    public boolean isShopMaintenance() {
        return isShopMaintenance;
    }

    public void setShopMaintenance(boolean shopMaintenance) {
        isShopMaintenance = shopMaintenance;
    }
}
