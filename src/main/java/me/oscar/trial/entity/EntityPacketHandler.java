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


    public EntityPacketHandler() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(ShopPlugin.getInstance(), PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.isCancelled()) {
                    return;
                }

                Player targetPlayer = event.getPlayer();
                PacketContainer packet = event.getPacket();

                int id = packet.getIntegers().read(0);

                if (ShopPlugin.getInstance().getEntityHandler().getRegisteredShops().containsKey(id)) {
                    ShopEntity entity = ShopPlugin.getInstance().getEntityHandler().getRegisteredShops().get(id);
                    Bukkit.getScheduler().runTask(ShopPlugin.getInstance(), () -> targetPlayer.openInventory(new ShopMenu(entity.getOwnerName(), entity.getOwnerID(),targetPlayer.getUniqueId()).getInventory()));
                }
            }
        });
    }
}
