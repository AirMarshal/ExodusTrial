package me.oscar.trial.entity;

import me.oscar.trial.ShopPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ShopEntityListener implements Listener {

    private final ShopPlugin shopPlugin;

    public ShopEntityListener(ShopPlugin shopPlugin) {
        this.shopPlugin = shopPlugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.shopPlugin.getServer().getScheduler().runTaskLaterAsynchronously(this.shopPlugin, () ->
                this.shopPlugin.getEntityHandler().getRegisteredShops().values().forEach(shopEntity -> this.shopPlugin.getEntityHandler().spawnNPC(shopEntity)), 10L);

    }
}
