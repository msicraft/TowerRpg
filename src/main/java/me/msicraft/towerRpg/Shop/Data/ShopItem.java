package me.msicraft.towerRpg.Shop.Data;

import me.msicraft.towerRpg.Shop.ShopManager;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.inventory.ItemStack;

public class ShopItem {

    private boolean useStaticPrice = false;

    private final ItemStack itemStack;
    private final String id;

    private int stock = 0; //재고
    private double basePrice = -1; //기본 가격
    private double price = -1;

    private int buyQuantity = 0; //구매 수량
    private int sellQuantity = 0; //판매 수량

    public ShopItem(String id, ItemStack itemStack, double basePrice) {
        this.id = id;
        this.itemStack = itemStack;
        this.basePrice = basePrice;
        this.price = basePrice;
    }

    public ShopItem(String id, ItemStack itemStack, int stock, double basePrice, double price, int buyQuantity, int sellQuantity) {
        this.id = id;
        this.itemStack = itemStack;
        this.stock = stock;
        this.basePrice = basePrice;
        this.price = price;
        this.buyQuantity = buyQuantity;
        this.sellQuantity = sellQuantity;
    }

    public void updatePrice() {
        if (useStaticPrice) {
            price = basePrice;
            return;
        }
        ShopManager shopManager = TowerRpg.getPlugin().getShopManager();

        double maxPrice = basePrice + (basePrice * shopManager.getMaxPricePercent());
        double minPrice = basePrice + (basePrice * shopManager.getMinPricePercent());
        if (minPrice < 0) {
            minPrice = 1;
        }
        double changedPrice = price;

        int buyQuantityCal = buyQuantity / shopManager.getBuyPriceChangeQuantity();
        if (buyQuantityCal >= 1) {
            buyQuantity = buyQuantity - shopManager.getBuyPriceChangeQuantity() * buyQuantityCal;

            double changePercent = shopManager.getBuyPriceChangePercent() * buyQuantityCal;
            changedPrice = changedPrice + (changedPrice * changePercent);
        }
        int sellQuantityCal = sellQuantity / shopManager.getSellPriceChangeQuantity();
        if (sellQuantityCal >= 1) {
            sellQuantity = sellQuantity - shopManager.getSellPriceChangeQuantity() * sellQuantityCal;

            double changePercent = shopManager.getSellPriceChangePercent() * sellQuantityCal;
            changedPrice = changedPrice - (changedPrice * changePercent);
        }

        this.price = Math.min(Math.max(changedPrice, minPrice), maxPrice);
    }

    public boolean useStaticPrice() {
        return useStaticPrice;
    }

    public void setUseStaticPrice(boolean useStaticPrice) {
        this.useStaticPrice = useStaticPrice;
    }

    public String getId() {
        return id;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean hasEnoughStock(int amount) {
        return stock >= amount;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void addStock(int amount) {
        this.stock = this.stock + amount;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getPrice(boolean isPerPrice) {
        if (isPerPrice) {
            return price / itemStack.getAmount();
        }
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getBuyQuantity() {
        return buyQuantity;
    }

    public void setBuyQuantity(int buyQuantity) {
        this.buyQuantity = buyQuantity;
    }

    public void addBuyQuantity(int quantity) {
        this.buyQuantity += this.buyQuantity + quantity;
    }

    public int getSellQuantity() {
        return sellQuantity;
    }

    public void setSellQuantity(int sellQuantity) {
        this.sellQuantity = sellQuantity;
    }

    public void addSellQuantity(int quantity) {
        this.sellQuantity = this.sellQuantity + quantity;
    }

}
