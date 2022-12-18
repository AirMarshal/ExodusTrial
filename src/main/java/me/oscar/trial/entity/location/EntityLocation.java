package me.oscar.trial.entity.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class EntityLocation {
    private double x;
    private double y;
    private double z;

    private float yaw, pitch;
    private String worldName;

    public EntityLocation(double x, double y, double z, float yaw, float pitch, String worldName) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.worldName = worldName;
    }

    public EntityLocation(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.worldName = location.getWorld().getName();
    }

    public EntityLocation() {

    }

    public Location toBukkitLocation() {
        return new Location(this.toBukkitWorld(), this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public World toBukkitWorld() {
        return Bukkit.getWorld(this.worldName);
    }
}
