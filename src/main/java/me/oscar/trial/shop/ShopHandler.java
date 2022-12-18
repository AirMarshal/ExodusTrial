package me.oscar.trial.shop;

import me.oscar.trial.ShopPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopHandler {
    private final Map<UUID, PlayerShop> idToShop = new HashMap<>();

    private final ShopPlugin shopPlugin;

    public ShopHandler(final ShopPlugin shopPlugin) {
        this.shopPlugin = shopPlugin;

        this.load();
    }

    public void load() {
        this.shopPlugin.getDatastore().find(PlayerShop.class).iterator().forEachRemaining(playerShop -> {
            this.idToShop.put(playerShop.getOwnerID(), playerShop);
            this.shopPlugin.getEntityHandler().getRegisteredShops().put(playerShop.getShopEntity().getId(), playerShop.getShopEntity());
        });

        this.shopPlugin.getLogger().info("Loaded %s shops".formatted(this.idToShop.size()));
    }

    public Map<UUID, PlayerShop> getIdToShop() {
        return this.idToShop;
    }

    public PlayerShop getShopByID(final UUID uuid) {
        return this.idToShop.get(uuid);
    }
}
