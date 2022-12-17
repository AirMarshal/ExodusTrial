package me.oscar.trial.shop;

import me.oscar.trial.ShopPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopHandler {
    private final Map<UUID, PlayerShop> idToShop = new HashMap<>();

    public ShopHandler() {
        this.load();
    }

    public void load() {
        ShopPlugin.getInstance().getDatastore().find(PlayerShop.class).iterator().forEachRemaining(playerShop -> {
            this.idToShop.put(playerShop.getOwnerID(), playerShop);
            ShopPlugin.getInstance().getEntityHandler().getRegisteredShops().put(playerShop.getShopEntity().getId(), playerShop.getShopEntity());
        });

        ShopPlugin.getInstance().getLogger().info("Loaded %s shops".formatted(this.idToShop.size()));
    }

    public Map<UUID, PlayerShop> getIdToShop() {
        return this.idToShop;
    }

    public PlayerShop getShopByID(UUID uuid) {
        return this.idToShop.get(uuid);
    }
}
