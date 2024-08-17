package me.msicraft.towerRpg.Shop;

import me.msicraft.towerRpg.Shop.Data.ShopItem;
import me.msicraft.towerRpg.Shop.File.ShopDataFile;
import me.msicraft.towerRpg.Shop.Task.ShopTimerTask;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShopManager {

    private final TowerRpg plugin;
    private final ShopInventory shopInventory;

    private final Map<String, ShopItem> shopItemMap = new LinkedHashMap<>();

    private int updateTicks = 72000;
    private double maxPricePercent = 0.25;
    private double minPricePercent = 0.25;
    private int buyPriceChangeQuantity = 64;
    private int sellPriceChangeQuantity = 64;
    private double buyPriceChangePercent = 0.1;
    private double sellPriceChangePercent = 0.1;

    private boolean isShopMaintenance = false;
    private ShopTimerTask shopTimerTask = null;

    public ShopManager(TowerRpg plugin) {
        this.plugin = plugin;
        shopInventory = new ShopInventory();

        reloadVariables();
    }

    public void closeShopInventory() {
        if (shopInventory == null) {
            return;
        }
        List<HumanEntity> viewers = shopInventory.getInventory().getViewers();
        for (int i = viewers.size() - 1; i >= 0; i--) {
            HumanEntity humanEntity = viewers.get(i);
            humanEntity.closeInventory();
            humanEntity.sendMessage(ChatColor.RED + "현재 상점 가격 조정 중입니다. 잠시 후 이용해 주시기를 바랍니다.");
        }
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

        if (shopTimerTask != null) {
            shopTimerTask.cancel();
            shopTimerTask = null;
            setShopMaintenance(false);
        }
        shopTimerTask = new ShopTimerTask(plugin, this, updateTicks);
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
        if (type == 0) {
            player.openInventory(shopInventory.getInventory());
            shopInventory.setShopBuyInv(player);
        } else if (type == 1) {
            player.openInventory(shopInventory.getInventory());
            shopInventory.setShopSellInv(player);
        }
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
    public ShopInventory getShopInventory() {
        return shopInventory;
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
