package me.oscar.trial.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.oscar.trial.entity.location.EntityLocation;
import org.bukkit.ChatColor;

import java.util.UUID;

public class ShopEntity {
    private final EntityLocation location;
    private final String ownerName;
    private final UUID ownerID;
    private final SkinTexture texture;

    private final GameProfile gameProfile;
    private Integer id = null;

    public ShopEntity(EntityLocation location, String ownerName, UUID ownerID, SkinTexture texture) {
        this.location = location;
        this.ownerName = ownerName;
        this.ownerID = ownerID;
        this.texture = texture;

        this.gameProfile = new GameProfile(UUID.randomUUID(), ChatColor.GOLD + "Player Shop");
        this.gameProfile.getProperties().clear();
        this.gameProfile.getProperties().put("textures", new Property("textures", this.texture.getValue(), this.texture.getSignature()));
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

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
