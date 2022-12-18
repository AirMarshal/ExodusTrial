package me.oscar.trial.shop.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ItemStackConverter extends TypeConverter implements SimpleValueConverter {

    public ItemStackConverter() {
        super(ItemStack.class);
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        final ItemStack itemStack = (ItemStack) value;

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final BukkitObjectOutputStream dataOutput;

        try {
            dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(itemStack);
            dataOutput.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return Base64Coder.encodeLines(outputStream.toByteArray());
    }

    @Override
    public Object decode(final Class<?> aClass, final Object o, final MappedField mappedField) {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines((String) o));
        final BukkitObjectInputStream dataInput;
        ItemStack item = null;

        try {
            dataInput = new BukkitObjectInputStream(inputStream);
            item = (ItemStack) dataInput.readObject();
            dataInput.close();
        } catch (final IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return item;
    }
}
