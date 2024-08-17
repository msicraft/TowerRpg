package me.msicraft.towerRpg.Shop.Data;

import org.bukkit.inventory.ItemStack;

public class SellItemSlot {

    private final ItemStack itemStack;
    private final double totalPrice;

    public SellItemSlot(ItemStack itemStack, double totalPrice) {
        this.itemStack = itemStack;
        this.totalPrice = totalPrice;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

}
