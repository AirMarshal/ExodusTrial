package me.oscar.trial.shop.item;

import org.bukkit.inventory.ItemStack;

public class PlayerShopItem {
    private ItemStack stack;
    private double price;

    public PlayerShopItem(final ItemStack stack, final double price) {
        this.stack = stack;
        this.price = price;
    }

    public PlayerShopItem() {

    }

    public ItemStack getStack() {
        return this.stack;
    }

    public double getPrice() {
        return this.price;
    }
}
