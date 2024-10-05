package me.msicraft.towerRpg.Shop.Data;

import org.bukkit.inventory.ItemStack;

public class SellItemSlot {

    public enum ItemType {
        TOWER_RPG, MMOITEMS
    }

    private final ItemType itemType;
    private final String id;
    private final ItemStack itemStack;
    private final double totalPrice;

    public SellItemSlot(ItemType itemType, String id, ItemStack itemStack, double totalPrice) {
        this.itemType = itemType;
        this.id = id;
        this.itemStack = itemStack;
        this.totalPrice = totalPrice;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public String getId() {
        return id;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

}
