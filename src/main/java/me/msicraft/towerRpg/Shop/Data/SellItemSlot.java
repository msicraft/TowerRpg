package me.msicraft.towerRpg.Shop.Data;

import org.bukkit.inventory.ItemStack;

public class SellItemSlot {

    private final String id;
    private final ItemStack itemStack;
    private final double totalPrice;

    public SellItemSlot(String id, ItemStack itemStack, double totalPrice) {
        this.id = id;
        this.itemStack = itemStack;
        this.totalPrice = totalPrice;
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
