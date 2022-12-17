package me.oscar.trial.entity;

import me.oscar.trial.ShopPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EntityListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ShopPlugin.getInstance().getEntityHandler().getRegisteredShops().values().forEach(shopEntity -> shopEntity.spawnNPC(event.getPlayer()));
    }
}
