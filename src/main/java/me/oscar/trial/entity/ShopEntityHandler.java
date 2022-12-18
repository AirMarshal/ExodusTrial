package me.oscar.trial.entity;

import io.netty.buffer.Unpooled;
import me.oscar.trial.ShopPlugin;
import me.oscar.trial.util.Callback;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.EnumGamemode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ShopEntityHandler {

    private final Map<Integer, ShopEntity> registeredShops = new HashMap<>();
    private final AtomicInteger atomicInteger = new AtomicInteger();
    private final ShopPlugin shopPlugin;

    public ShopEntityHandler(ShopPlugin shopPlugin) {
        this.shopPlugin = shopPlugin;
    }

    public void spawnNPC(Player receiver, ShopEntity shopEntity) {
        PlayerConnection playerConnection = ((CraftPlayer) receiver).getHandle().b;

        if (shopEntity.getId() == null) {
            shopEntity.setId(this.atomicInteger.incrementAndGet());
            this.shopPlugin.getServer().getScheduler().runTaskAsynchronously(this.shopPlugin, () -> this.shopPlugin.getDatastore().save(shopEntity));
        }

        this.destroyNPC(shopEntity);

        playerConnection.a(this.createInfoPacket(shopEntity, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a));
        playerConnection.a(this.createEntitySpawn(shopEntity));

        //Let skin apply
        this.shopPlugin.getServer().getScheduler().scheduleAsyncDelayedTask(this.shopPlugin, () ->
                playerConnection.a(this.createInfoPacket(shopEntity, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e)), 5L);
    }

    public void spawnNPC(ShopEntity shopEntity) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.spawnNPC(player,shopEntity);
        }
    }

    public void destroyNPC(Player receiver, ShopEntity shopEntity) {
        PlayerConnection playerConnection = ((CraftPlayer) receiver).getHandle().b;
        playerConnection.a(this.createInfoPacket(shopEntity, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e));
    }

    public void destroyNPC(ShopEntity shopEntity) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.destroyNPC(player, shopEntity);
        }
    }

    public Map<Integer, ShopEntity> getRegisteredShops() {
        return this.registeredShops;
    }

    private PacketPlayOutNamedEntitySpawn createEntitySpawn(ShopEntity shopEntity) {
        return this.createPacketData(packetDataSerializer ->  {
            packetDataSerializer.d(shopEntity.getId());
            packetDataSerializer.a(shopEntity.getGameProfile().getId());

            Location entityLocation = shopEntity.getLocation().toBukkitLocation();
            packetDataSerializer.writeDouble(entityLocation.getX());
            packetDataSerializer.writeDouble(entityLocation.getY());
            packetDataSerializer.writeDouble(entityLocation.getZ());

            packetDataSerializer.writeByte( (byte) ((int) (entityLocation.getYaw() * 256.0F / 360.0F)));
            packetDataSerializer.writeByte( (byte) ((int) (entityLocation.getPitch() * 256.0F / 360.0F)));
            return new PacketPlayOutNamedEntitySpawn(packetDataSerializer);
        });
    }

    private PacketPlayOutPlayerInfo createInfoPacket(ShopEntity shopEntity, PacketPlayOutPlayerInfo.EnumPlayerInfoAction infoAction) {
        return this.createPacketData(packetDataSerializer -> {
            ProfilePublicKey.a profilePublicKey = null;
            packetDataSerializer.a(infoAction);

            PacketPlayOutPlayerInfo.PlayerInfoData playerInfoData = new PacketPlayOutPlayerInfo.PlayerInfoData(shopEntity.getGameProfile(), 149, EnumGamemode.b, CraftChatMessage.fromString(shopEntity.getGameProfile().getName())[0], profilePublicKey);
            List<PacketPlayOutPlayerInfo.PlayerInfoData> list = List.of(playerInfoData);

            Method method = infoAction.getDeclaringClass().getDeclaredMethod("a", PacketDataSerializer.class, PacketPlayOutPlayerInfo.PlayerInfoData.class);
            method.setAccessible(true);

            packetDataSerializer.a(list, (PacketDataSerializer.b<PacketPlayOutPlayerInfo.PlayerInfoData>) (a,b) -> new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        method.invoke(infoAction, a,b);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }.run());
            return new PacketPlayOutPlayerInfo(packetDataSerializer);
        });
    }

    private <T> T createPacketData(Callback<PacketDataSerializer, T> callback) {
        PacketDataSerializer data = new PacketDataSerializer(Unpooled.buffer());
        T result = null;
        try {
            result = callback.apply(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            data.release();
        }

        return result;
    }

}
