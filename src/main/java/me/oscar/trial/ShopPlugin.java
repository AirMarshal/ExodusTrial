package me.oscar.trial;

import com.mongodb.MongoClient;
import me.oscar.trial.entity.*;
import me.oscar.trial.menu.MenuListener;
import me.oscar.trial.shop.ShopHandler;
import me.oscar.trial.shop.commands.ShopCommands;
import me.oscar.trial.shop.item.ItemStackConverter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.DefaultCreator;

public class ShopPlugin extends JavaPlugin {

    public static ShopPlugin instance;

    private EntityHandler entityHandler;
    private ShopHandler shopHandler;

    private Datastore datastore;
    private MongoClient mongoClient;

    @Override
    public void onEnable() {
        instance = this;

        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        this.getLogger().info("Beginning to load Morphia");
        this.registerMorphia();
        this.getLogger().info("Successfully loaded Morphia");

        this.getLogger().info("Beginning to load managers");
        this.registerManagers();
        this.getLogger().info("Successfully loaded managers");

        this.getLogger().info("Beginning to load listeners");
        this.registerListeners();
        this.getLogger().info("Successfully loaded listeners");

        this.getCommand("shop").setExecutor(new ShopCommands());
    }

    @Override
    public void onDisable() {
        this.mongoClient.close();
    }

    private void registerMorphia() {
        this.mongoClient = new MongoClient("localhost", 27017);

        Morphia morphia = new Morphia();
        morphia.getMapper().getConverters().addConverter(new ItemStackConverter());
        morphia.getMapper().getConverters().addConverter(new ShopEntityConverter());

        morphia.getMapper().getOptions().setObjectFactory(new DefaultCreator() {
            @Override
            protected ClassLoader getClassLoaderForClass() {
                return ShopPlugin.getInstance().getClassLoader();
            }
        });

        this.datastore = morphia.createDatastore(this.mongoClient, "shop");
        this.datastore.ensureIndexes();
    }

    private void registerManagers() {
        this.entityHandler = new EntityHandler();
        new EntityPacketHandler();
        this.shopHandler = new ShopHandler();
    }

    private void registerListeners() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new MenuListener(), this);
        pluginManager.registerEvents(new EntityListener(), this);
    }

    public static ShopPlugin getInstance() {
        return instance;
    }

    public EntityHandler getEntityHandler() {
        return this.entityHandler;
    }

    public ShopHandler getShopHandler() {
        return this.shopHandler;
    }

    public Datastore getDatastore() {
        return this.datastore;
    }
}
