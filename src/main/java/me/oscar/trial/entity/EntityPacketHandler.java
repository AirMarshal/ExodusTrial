package me.oscar.trial.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.oscar.trial.ShopPlugin;
import me.oscar.trial.menu.impl.ShopMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EntityPacketHandler {

    private final ShopPlugin shopPlugin;

    public EntityPacketHandler(final ShopPlugin shopPlugin) {
        this.shopPlugin = shopPlugin;

        final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(this.shopPlugin, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(final PacketEvent event) {
                if (event.isCancelled()) {
                    return;
                }

                final Player targetPlayer = event.getPlayer();
                final PacketContainer packet = event.getPacket();

                final int id = packet.getIntegers().read(0);

                if (shopPlugin.getEntityHandler().getRegisteredShops().containsKey(id)) {
                    final ShopEntity entity = shopPlugin.getEntityHandler().getRegisteredShops().get(id);
                    Bukkit.getScheduler().runTask(shopPlugin, () -> targetPlayer.openInventory(new ShopMenu(entity.getOwnerName(), entity.getOwnerID(), targetPlayer.getUniqueId(), shopPlugin).getInventory()));
                }
            }
        });
    }
}
