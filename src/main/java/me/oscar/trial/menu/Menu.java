package me.oscar.trial.menu;

import me.oscar.trial.shop.item.PlayerShopItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Menu implements InventoryHolder {
    public abstract Consumer<InventoryClickEvent> consumer();

    public final String title;
    private Inventory inventory;

    private int currentPage;

    private final int itemsPerPage;
    private final List<PlayerShopItem> pageItems = new ArrayList<>();

    public Menu(final String title, final int itemsPerPage) {
        this.itemsPerPage = (int) (Math.ceil(itemsPerPage / 9D) * 9D);

        if (this.itemsPerPage > 36) {
            throw new IllegalArgumentException("It must be 36 or below");
        }

        this.title = title;
        this.inventory = null;
    }

    public Inventory createInventory() {
        return Bukkit.createInventory(this, this.itemsPerPage + 9, this.title);
    }

    public Inventory getInventory() {
        if (this.inventory == null) {
            this.inventory = this.createInventory();
        }

        return this.inventory;
    }

    public ItemStack getNextButton() {
        final ItemStack stack = new ItemStack(Material.GREEN_WOOL);
        final ItemMeta itemMeta = stack.getItemMeta();

        final int offsetPage = this.currentPage + 1;
        final int maxPage = (this.getMaxPages() + 1);
        itemMeta.setDisplayName(ChatColor.YELLOW + "Current Page: " + ChatColor.GOLD + offsetPage + ChatColor.GRAY + "/" + ChatColor.GOLD + maxPage);
        itemMeta.setLore(offsetPage == maxPage ? List.of(ChatColor.RED + "This is the final page.") : List.of(ChatColor.GRAY + "Click me to move to page " + (offsetPage + 1)));

        stack.setItemMeta(itemMeta);

        return stack;
    }

    public ItemStack getBackButton() {
        final ItemStack stack = new ItemStack(Material.RED_WOOL);
        final ItemMeta itemMeta = stack.getItemMeta();

        final int offsetPage = this.currentPage + 1;
        final int maxPage = (this.getMaxPages() + 1);
        itemMeta.setDisplayName(ChatColor.YELLOW + "Current Page: " + ChatColor.GOLD + offsetPage + ChatColor.GRAY + "/" + ChatColor.GOLD + maxPage);
        itemMeta.setLore(offsetPage == 1 ? List.of(ChatColor.RED + "This is the first page.") : List.of(ChatColor.GRAY + "Click me to move to page " + (offsetPage - 1)));

        stack.setItemMeta(itemMeta);

        return stack;
    }

    public int getMaxPages() {
        return (Math.floorDiv(this.pageItems.size() + this.itemsPerPage, this.itemsPerPage) - 1);
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void incrementPage() {
        this.currentPage++;
    }

    public void decrementPage(){
        this.currentPage--;
    }

    public int getItemsPerPage() {
        return this.itemsPerPage;
    }

    public List<PlayerShopItem> getPageItems() {
        return this.pageItems;
    }

}
