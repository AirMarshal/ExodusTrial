package me.oscar.trial.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.oscar.trial.ShopPlugin;
import me.oscar.trial.entity.location.EntityLocation;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ShopEntity {
    private final EntityLocation location;
    private final String ownerName;
    private final UUID ownerID;
    private final SkinTexture texture;

    private GameProfile gameProfile;
    private EntityPlayer entityPlayer;
    private int id;

    public ShopEntity(EntityLocation location, String ownerName, UUID ownerID, SkinTexture texture) {
        this.location = location;
        this.ownerName = ownerName;
        this.ownerID = ownerID;
        this.texture = texture;

        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();

        this.gameProfile = new GameProfile(UUID.randomUUID(), ChatColor.GOLD + "Player Shop");
        this.gameProfile.getProperties().clear();
        this.gameProfile.getProperties().put("textures", new Property("textures", this.texture.getValue(), this.texture.getSignature()));

        Location bukkitLocation = this.location.toBukkitLocation();
        this.entityPlayer = new EntityPlayer(minecraftServer, ((CraftWorld) bukkitLocation.getWorld()).getHandle(), this.gameProfile, null);
        this.entityPlayer.teleportTo(((CraftWorld) bukkitLocation.getWorld()).getHandle(), new Position(bukkitLocation.getX(), bukkitLocation.getY(), bukkitLocation.getZ()));

        this.id = this.entityPlayer.getBukkitEntity().getEntityId();
    }

    public void spawnNPC(Player receiver) {
        PlayerConnection playerConnection = ((CraftPlayer) receiver).getHandle().b;
        this.destroyNPC();
        playerConnection.a(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, this.entityPlayer));
        playerConnection.a(new PacketPlayOutNamedEntitySpawn(this.entityPlayer));

        //Let skin apply
        Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(ShopPlugin.getInstance(), () -> {
           playerConnection.a(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, this.entityPlayer));
        }, 5L);
    }

    public void spawnNPC() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.spawnNPC(player);
        }
    }

    public void destroyNPC(Player receiver) {
        PlayerConnection playerConnection = ((CraftPlayer) receiver).getHandle().b;
        playerConnection.a(new PacketPlayOutEntityDestroy(this.id));
    }

    public void destroyNPC() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.destroyNPC(player);
        }
    }

    public EntityLocation getLocation() {
        return this.location;
    }

    public String getOwnerName() {
        return this.ownerName;
    }

    public UUID getOwnerID() {
        return this.ownerID;
    }

    public SkinTexture getTexture() {
        return this.texture;
    }

    public int getId() {
        return this.id;
    }
}
