package me.oscar.trial.menu.impl;

import me.oscar.trial.ShopPlugin;
import me.oscar.trial.menu.Menu;
import me.oscar.trial.shop.PlayerShop;
import me.oscar.trial.shop.item.PlayerShopItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class ShopMenu extends Menu {

    private final UUID ownerID;
    private final UUID openerID;
    private final ShopPlugin shopPlugin;

    public ShopMenu(String ownerName, UUID uuid, UUID openerID, ShopPlugin shopPlugin) {
        super(ChatColor.GOLD + ownerName + "'s Shop", 9);

        this.ownerID = uuid;
        this.openerID = openerID;
        this.shopPlugin = shopPlugin;
    }

    @Override
    public Consumer<InventoryClickEvent> consumer() {
        return event -> {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();

            if (item == null || item.getType() == Material.AIR) {
                return;
            }

            if (item.equals(this.getNextButton())) {
                int currentPage = this.getCurrentPage();

                if (currentPage >= this.getMaxPages()) {
                    return;
                }

                this.incrementPage();

                this.getInventory().clear();
                this.populatePage(event.getInventory());
                player.updateInventory();
            } else if (item.equals(this.getBackButton())) {
                int currentPage = this.getCurrentPage();

                if (currentPage <= 0) {
                    return;
                }

                this.decrementPage();

                this.getInventory().clear();
                this.populatePage(event.getInventory());
                player.updateInventory();
            }

            PlayerShop playerShop = this.shopPlugin.getShopHandler().getShopByID(this.ownerID);
            if (event.getClick() == ClickType.LEFT) {

                PlayerShopItem toRemove = null;
                for (PlayerShopItem playerShopItem : playerShop.getItems()) {
                    ItemStack shopItemStack = playerShopItem.getStack();

                    if (item.equals(this.withLore(playerShopItem))) {

                        if (player.getUniqueId().equals(this.ownerID)) {
                            player.sendMessage(ChatColor.RED + "You cannot buy your own item.");
                            return;
                        }

                        player.closeInventory();

                        player.getInventory().addItem(shopItemStack);
                        player.updateInventory();
                        toRemove = playerShopItem;
                        break;
                    }
                }

                if (toRemove != null) {
                    playerShop.getItems().remove(toRemove);

                    Bukkit.getScheduler().runTaskAsynchronously(this.shopPlugin, () -> this.shopPlugin.getDatastore().save(playerShop));

                    if (this.shopPlugin.getConfig().getBoolean("MESSAGES.BUY.ENABLED")) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.shopPlugin.getConfig().getString("MESSAGES.BUY.MESSAGE")
                                .replace("%item%", toRemove.getStack().getType().name())
                                .replace("%price%", String.valueOf(toRemove.getPrice()))));
                    }

                    if (this.shopPlugin.getConfig().getBoolean("MESSAGES.SELL.ENABLED")) {
                        Player seller = Bukkit.getPlayer(this.ownerID);
                        if (seller != null) {
                            seller.sendMessage(ChatColor.translateAlternateColorCodes('&', this.shopPlugin.getConfig().getString("MESSAGES.SELL.MESSAGE")
                                    .replace("%item%", toRemove.getStack().getType().name())
                                    .replace("%price%", String.valueOf(toRemove.getPrice()))));
                        }
                    }
                }
            } else if (event.getClick() == ClickType.RIGHT) {
                if (player.getUniqueId().equals(this.ownerID)) {
                    PlayerShopItem toRemove = null;

                    for (PlayerShopItem playerShopItem : playerShop.getItems()) {
                        ItemStack shopItemStack = playerShopItem.getStack();

                        if (item.equals(this.withLore(playerShopItem))) {
                            if (player.getInventory().firstEmpty() == -1){
                                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), shopItemStack);
                            } else {
                                player.getInventory().addItem(shopItemStack);
                                player.updateInventory();
                            }

                            toRemove = playerShopItem;
                            player.closeInventory();
                            break;
                        }
                    }

                    if (toRemove != null) {
                        playerShop.getItems().remove(toRemove);
                        Bukkit.getScheduler().runTaskAsynchronously(this.shopPlugin, () -> this.shopPlugin.getDatastore().save(playerShop));

                        if (this.shopPlugin.getConfig().getBoolean("MESSAGES.REMOVE.ENABLED")) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.shopPlugin.getConfig().getString("MESSAGES.REMOVE.MESSAGE")
                                    .replace("%item%", toRemove.getStack().getType().name())
                                    .replace("%price%", String.valueOf(toRemove.getPrice()))));
                        }
                    }
                }
            }
        };
    }

    @Override
    public Inventory createInventory() {
        Inventory inventory = Bukkit.createInventory(this, this.getItemsPerPage() + 9, this.title);

        PlayerShop playerShop = this.shopPlugin.getShopHandler().getShopByID(this.ownerID);
        if (!playerShop.getItems().isEmpty()) {
            playerShop.getItems().forEach(item -> this.getPageItems().add(item));
        }

        this.populatePage(inventory);
        return inventory;
    }

    public void populatePage(Inventory inventory) {
        int startIndex = this.getItemsPerPage() * this.getCurrentPage();
        int maxElements = Math.min(this.getPageItems().size(), startIndex + this.getItemsPerPage());

        int position = 0;
        for (int i = startIndex; i < maxElements; i++) {
            if (i < 0 || i >= this.getPageItems().size()) {
                continue;
            }

            PlayerShopItem playerShopItem = this.getPageItems().get(i);
            inventory.setItem(position, this.withLore(playerShopItem));
            position++;
        }

        inventory.setItem(this.getItemsPerPage(), this.getBackButton());
        inventory.setItem(this.getItemsPerPage() + 8, this.getNextButton());
    }

    public ItemStack withLore(PlayerShopItem playerShopItem){
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "Price: " + ChatColor.GOLD + playerShopItem.getPrice());
        lore.add("");
        lore.add(ChatColor.GRAY + "Left click to purchase");

        if (this.ownerID.equals(this.openerID)) {
            lore.add(ChatColor.RED + "Right click to remove this item from the shop.");
        }

        ItemStack playerShopStack = playerShopItem.getStack().clone();
        ItemMeta playerShopMeta = playerShopStack.getItemMeta();

        if (playerShopMeta.getLore() != null) {
            for (String string : lore) {
                playerShopMeta.getLore().add(string);
            }
        } else {
            playerShopMeta.setLore(lore);
        }

        playerShopStack.setItemMeta(playerShopMeta);
        return playerShopStack;
    }
}
