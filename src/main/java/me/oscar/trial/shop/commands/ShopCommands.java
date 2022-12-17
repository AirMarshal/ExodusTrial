package me.oscar.trial.shop.commands;

import com.mojang.authlib.properties.Property;
import me.oscar.trial.ShopPlugin;
import me.oscar.trial.entity.ShopEntity;
import me.oscar.trial.entity.SkinTexture;
import me.oscar.trial.entity.location.EntityLocation;
import me.oscar.trial.shop.PlayerShop;
import me.oscar.trial.shop.item.PlayerShopItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class ShopCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player player) {
            if (args.length <= 0) {
                player.sendMessage(ChatColor.GOLD + "Shop Commands: ");
                player.sendMessage(" ");
                player.sendMessage(ChatColor.YELLOW + "/shop create");
                player.sendMessage(ChatColor.YELLOW + "/shop additem <price>");
                player.sendMessage(ChatColor.YELLOW + "/shop delete");
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "create" -> {

                    if (ShopPlugin.getInstance().getShopHandler().getShopByID(player.getUniqueId()) != null) {
                        player.sendMessage(ChatColor.RED + "You already have a shop!");
                        return true;
                    }

                    CraftPlayer craftPlayer = (CraftPlayer) player;
                    Property property = craftPlayer.getProfile().getProperties().get("textures").stream().findFirst().orElse(null);

                    ShopEntity shopEntity = new ShopEntity(new EntityLocation(player.getLocation()), player.getName(), player.getUniqueId(), new SkinTexture(property.getValue(), property.getSignature()));
                    shopEntity.spawnNPC();

                    ShopPlugin.getInstance().getEntityHandler().getRegisteredShops().put(shopEntity.getId(), shopEntity);

                    PlayerShop playerShop = new PlayerShop(player.getUniqueId(), shopEntity);
                    ShopPlugin.getInstance().getShopHandler().getIdToShop().put(player.getUniqueId(), playerShop);
                    Bukkit.getScheduler().runTaskAsynchronously(ShopPlugin.getInstance(), () -> ShopPlugin.getInstance().getDatastore().save(playerShop));

                    player.sendMessage(ChatColor.GREEN + "You have created a shop!");
                }
                case "delete" -> {

                    if (ShopPlugin.getInstance().getShopHandler().getShopByID(player.getUniqueId()) == null) {
                        player.sendMessage(ChatColor.RED + "You do not have a shop");
                        return true;
                    }

                    PlayerShop playerShop = ShopPlugin.getInstance().getShopHandler().getShopByID(player.getUniqueId());
                    ShopEntity shopEntity = playerShop.getShopEntity();
                    shopEntity.destroyNPC();

                    ShopPlugin.getInstance().getEntityHandler().getRegisteredShops().remove(shopEntity.getId(), shopEntity);
                    ShopPlugin.getInstance().getShopHandler().getIdToShop().remove(player.getUniqueId());
                    Bukkit.getScheduler().runTaskAsynchronously(ShopPlugin.getInstance(), () -> ShopPlugin.getInstance().getDatastore().delete(playerShop));

                    player.sendMessage(ChatColor.GREEN + "You have deleted your shop!");
                }
                case "additem" -> {
                    PlayerShop playerShop = ShopPlugin.getInstance().getShopHandler().getShopByID(player.getUniqueId());
                    ItemStack itemInHand = player.getItemInHand();

                    if (playerShop == null) {
                        player.sendMessage(ChatColor.RED + "You must have a shop if you wish to sell items!");
                        return true;
                    }

                    if (itemInHand.getType() == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You must have the item you wish to sell in your hands!");
                        return true;
                    }

                    if (args.length < 2) {
                        player.sendMessage(ChatColor.RED + "You must enter a price you wish to sell the price for.");
                        return true;
                    }

                    double price = 0;
                    try {
                        price = Double.parseDouble(args[1]);
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }

                    if (price == 0 || price < 0) {
                        player.sendMessage(ChatColor.RED + "You must list your item for more than 0");
                        return true;
                    }

                    player.setItemInHand(new ItemStack(Material.AIR));
                    playerShop.addItem(new PlayerShopItem(itemInHand, price));
                    Bukkit.getScheduler().runTaskAsynchronously(ShopPlugin.getInstance(), () -> ShopPlugin.getInstance().getDatastore().save(playerShop));
                    player.sendMessage(ChatColor.YELLOW + "You have listed your item for " + ChatColor.AQUA + "$" + ChatColor.YELLOW + price + ".");
                }
            }

        }

        return true;
    }
}
