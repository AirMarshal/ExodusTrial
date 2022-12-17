package me.oscar.trial.shop.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.mongodb.morphia.annotations.Entity;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Entity(noClassnameStored = true)
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
