package me.oscar.trial.entity.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class EntityLocation {
    private double x, y, z;
    private float yaw, pitch;
    private UUID worldUID;

    public EntityLocation(double x, double y, double z, float yaw, float pitch, UUID worldUID) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.worldUID = worldUID;
    }

    public EntityLocation(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.worldUID = location.getWorld().getUID();
    }

    public EntityLocation() {

    }

    public Location toBukkitLocation() {
        return new Location(this.toBukkitWorld(), this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public World toBukkitWorld() {
        return Bukkit.getWorld(this.worldUID);
    }
}
