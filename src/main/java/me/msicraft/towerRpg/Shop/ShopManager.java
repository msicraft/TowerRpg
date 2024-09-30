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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ShopManager extends CustomGuiManager {

    private final TowerRpg plugin;
    private final ShopDataFile shopDataFile;

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
        this.shopDataFile = new ShopDataFile(plugin);
        reloadVariables();
    }

    public void closeShopInventory() {
        List<UUID> viewers = getViewers();
        for (UUID uuid : viewers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.closeInventory(InventoryCloseEvent.Reason.CANT_USE);
                player.sendMessage(ChatColor.RED + "현재 상점 가격 조정 중입니다. 잠시 후 이용해 주시기를 바랍니다.");
            }
        }
        removeAll();
    }

    public void reloadVariables() {
        //shopDataFile = new ShopDataFile(plugin);
        setShopMaintenance(true);
        closeShopInventory();

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

    public void openShopInventory(Player player, ShopGui.Type type) { // 0 = 기본, 1 = 판매
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
        FileConfiguration config = shopDataFile.getConfig();
        Set<String> keySets = shopItemMap.keySet();
        for (String key : keySets) {
            ShopItem shopItem = shopItemMap.get(key);
            String path = "Items." + key;
            config.set(path + ".ItemStack", shopItem.getItemStack());
            config.set(path + ".Stock", shopItem.getStock());
            config.set(path + ".BasePrice", shopItem.getBasePrice());
            config.set(path + ".Price", shopItem.getPrice(false));
            config.set(path + ".BuyQuantity", shopItem.getBuyQuantity());
            config.set(path + ".SellQuantity", shopItem.getSellQuantity());
            config.set(path + ".UseStaticPrice", shopItem.useStaticPrice());
        }
        shopDataFile.saveConfig();
    }

    public void loadShopData() {
        FileConfiguration config = shopDataFile.getConfig();

        ConfigurationSection itemSection = config.getConfigurationSection("Items");
        if (itemSection != null) {
            Set<String> internalNames = itemSection.getKeys(false);
            for (String key : internalNames) {
                String path = "Items." + key;
                ItemStack itemStack = config.getItemStack(path + ".ItemStack");
                int stock = config.getInt(path + ".Stock", 0);
                double basePrice = config.getDouble(path + ".BasePrice", -1);
                double price = config.getDouble(path + ".Price", 0);
                int buyQuantity = config.getInt(path + ".BuyQuantity", 0);
                int sellQuantity = config.getInt(path + ".SellQuantity", 0);
                boolean useStaticPrice = config.getBoolean(path + ".UseStaticPrice", false);
                ShopItem shopItem;
                if (shopItemMap.containsKey(key)) {
                    shopItem = shopItemMap.get(key);
                    shopItem.setStock(stock);
                    shopItem.setBasePrice(basePrice);
                    shopItem.setPrice(price);
                    shopItem.setBuyQuantity(buyQuantity);
                    shopItem.setSellQuantity(sellQuantity);
                    shopItem.setUseStaticPrice(useStaticPrice);
                } else {
                    shopItem  = new ShopItem(key, itemStack, stock, basePrice, price, buyQuantity, sellQuantity);
                    shopItem.setUseStaticPrice(useStaticPrice);
                }
                shopItemMap.put(key, shopItem);
            }
        } else {
            shopItemMap.clear();
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
            openShopInventory(player, ShopGui.Type.SELL);
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

    public ShopDataFile getShopDataFile() {
        return shopDataFile;
    }

}

