package me.oscar.trial.shop.item;

import org.bukkit.inventory.ItemStack;

public class PlayerShopItem {
    private ItemStack stack;
    private double price;

    public PlayerShopItem(ItemStack stack, double price) {
        this.stack = stack;
        this.price = price;
    }

    public PlayerShopItem() {

    }

    public ItemStack getStack() {
        return stack;
    }

    public double getPrice() {
        return price;
    }
}
