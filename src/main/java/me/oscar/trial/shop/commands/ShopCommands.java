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

public class ShopCommands implements CommandExecutor {

    private final ShopPlugin shopPlugin;
    public ShopCommands(final ShopPlugin shopPlugin) {
        this.shopPlugin = shopPlugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (sender instanceof Player player) {
            if (args.length <= 0) {
                for (final String string : this.shopPlugin.getConfig().getStringList("MESSAGES.ERROR.HELP")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', string));
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("create")) {
                if (this.shopPlugin.getShopHandler().getShopByID(player.getUniqueId()) != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.shopPlugin.getConfig().getString("MESSAGES.ERROR.ALREADY_HAVE_SHOP")));
                    return true;
                }

                final CraftPlayer craftPlayer = (CraftPlayer) player;
                final Property property = craftPlayer.getProfile().getProperties().get("textures").stream().findFirst().orElse(null);

                final ShopEntity shopEntity = new ShopEntity(new EntityLocation(player.getLocation()), player.getName(), player.getUniqueId(), new SkinTexture(property.getValue(), property.getSignature()));

                this.shopPlugin.getEntityHandler().getRegisteredShops().put(shopEntity.getId(), shopEntity);

                final PlayerShop playerShop = new PlayerShop(player.getUniqueId(), shopEntity);
                this.shopPlugin.getShopHandler().getIdToShop().put(player.getUniqueId(), playerShop);
                Bukkit.getScheduler().runTaskAsynchronously(this.shopPlugin, () -> {
                    this.shopPlugin.getEntityHandler().spawnNPC(shopEntity);
                    this.shopPlugin.getDatastore().save(playerShop);
                });

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.shopPlugin.getConfig().getString("MESSAGES.SHOP_CREATE")));
            } else if (args[0].equalsIgnoreCase("delete")) {

                final PlayerShop playerShop = this.shopPlugin.getShopHandler().getShopByID(player.getUniqueId());

                if (playerShop == null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.shopPlugin.getConfig().getString("MESSAGES.ERROR.DONT_HAVE_SHOP")));
                    return true;
                }

                final ShopEntity shopEntity = playerShop.getShopEntity();

                this.shopPlugin.getEntityHandler().getRegisteredShops().remove(shopEntity.getId(), shopEntity);
                this.shopPlugin.getShopHandler().getIdToShop().remove(player.getUniqueId());

                Bukkit.getScheduler().runTaskAsynchronously(this.shopPlugin, () -> {
                    this.shopPlugin.getEntityHandler().destroyNPC(shopEntity);
                    this.shopPlugin.getDatastore().delete(playerShop);
                });

                player.sendMessage(ChatColor.translateAlternateColorCodes('&',this.shopPlugin.getConfig().getString("MESSAGES.SHOP_DELETE")));
            } else if (args[0].equalsIgnoreCase("additem")) {
                final PlayerShop playerShop = this.shopPlugin.getShopHandler().getShopByID(player.getUniqueId());
                final ItemStack itemInHand = player.getItemInHand();

                if (playerShop == null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.shopPlugin.getConfig().getString("MESSAGES.ERROR.DONT_HAVE_SHOP")));
                    return true;
                }

                if (itemInHand.getType() == Material.AIR) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.shopPlugin.getConfig().getString("MESSAGES.ERROR.EMPTY_HANDS")));
                    return true;
                }

                if (args.length < 2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.shopPlugin.getConfig().getString("MESSAGES.ERROR.INVALID_PRICE")));
                    return true;
                }

                double price = 0;
                try {
                    price = Double.parseDouble(args[1]);
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }

                if (price == 0 || price < 0) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.shopPlugin.getConfig().getString("MESSAGES.ERROR.NEGATIVE_PRICE")));
                    return true;
                }

                player.setItemInHand(new ItemStack(Material.AIR));
                playerShop.addItem(new PlayerShopItem(itemInHand, price));
                Bukkit.getScheduler().runTaskAsynchronously(this.shopPlugin, () -> this.shopPlugin.getDatastore().save(playerShop));
                if (this.shopPlugin.getConfig().getBoolean("MESSAGES.LIST.ENABLED")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.shopPlugin.getConfig().getString("MESSAGES.LIST.MESSAGE")
                            .replace("%item%", itemInHand.getType().name())
                            .replace("%price%", String.valueOf(price))));
                }
            } else if (args[0].equalsIgnoreCase("show")) {
                this.shopPlugin.getServer().getScheduler().runTaskAsynchronously(this.shopPlugin,()
                        -> this.shopPlugin.getEntityHandler().spawnNPC(this.shopPlugin.getShopHandler().getIdToShop().get(player.getUniqueId()).getShopEntity()));

            }
        }

        return true;
    }
}
