package me.oscar.trial.shop;

import me.oscar.trial.entity.ShopEntity;
import me.oscar.trial.shop.item.PlayerShopItem;
import org.mongodb.morphia.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(value = "PlayerShops", noClassnameStored = true)
public class PlayerShop {

    @Id
    @Indexed(options = @IndexOptions(unique = true))
    private UUID ownerID;

    @Embedded("items")
    private List<PlayerShopItem> items = new ArrayList<>();
    private ShopEntity shopEntity;

    public PlayerShop(final UUID uuid, final ShopEntity shopEntity) {
        this.ownerID = uuid;
        this.shopEntity = shopEntity;
    }

    public PlayerShop() {

    }

    public void addItem(final PlayerShopItem item) {
        this.items.add(item);
    }

    public List<PlayerShopItem> getItems() {
        return this.items;
    }

    public UUID getOwnerID() {
        return this.ownerID;
    }

    public ShopEntity getShopEntity() {
        return this.shopEntity;
    }
}
