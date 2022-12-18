package me.oscar.trial.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.oscar.trial.entity.location.EntityLocation;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.UUID;

public class ShopEntityConverter extends TypeConverter implements SimpleValueConverter {

    private final Gson gson;

    public ShopEntityConverter() {
        super(ShopEntity.class);
        this.gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    @Override
    public Object encode(Object value, MappedField optionalExtraInfo) {
        ShopEntity shopEntity = (ShopEntity) value;
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("location", this.gson.toJsonTree(shopEntity.getLocation()));
        jsonObject.addProperty("ownerName", shopEntity.getOwnerName());
        jsonObject.addProperty("ownerID", shopEntity.getOwnerID().toString());
        jsonObject.add("skintexture", this.gson.toJsonTree(shopEntity.getTexture()));
        jsonObject.addProperty("id", shopEntity.getId());
        return this.gson.toJson(jsonObject);
    }

    @Override
    public Object decode(Class<?> aClass, Object o, MappedField mappedField) {
        JsonObject jsonObject = this.gson.fromJson((String)o, JsonObject.class);
        EntityLocation entityLocation = this.gson.fromJson(jsonObject.get("location"), EntityLocation.class);
        String ownerName = jsonObject.get("ownerName").getAsString();
        UUID uuid = UUID.fromString(jsonObject.get("ownerID").getAsString());
        SkinTexture skinTexture = this.gson.fromJson(jsonObject.get("skintexture"), SkinTexture.class);

        ShopEntity shopEntity = new ShopEntity(entityLocation,ownerName , uuid, skinTexture);
        shopEntity.setId(jsonObject.get("id").getAsInt());
        return shopEntity;
    }
}
