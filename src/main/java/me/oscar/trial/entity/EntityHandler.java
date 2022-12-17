package me.oscar.trial.entity;

import java.util.HashMap;
import java.util.Map;

public class EntityHandler {
    private final Map<Integer, ShopEntity> registeredShops = new HashMap<>();

    public Map<Integer, ShopEntity> getRegisteredShops() {
        return this.registeredShops;
    }
}
