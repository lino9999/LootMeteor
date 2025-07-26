package com.Lino.lootMeteor.config;

import org.bukkit.configuration.file.FileConfiguration;
import com.Lino.lootMeteor.LootMeteor;

public class ConfigManager {

    private final LootMeteor plugin;
    private FileConfiguration config;

    public ConfigManager(LootMeteor plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();

        config.addDefault("meteor.spawn-interval", 10);
        config.addDefault("meteor.world", "world");
        config.addDefault("meteor.min-radius", 100);
        config.addDefault("meteor.max-radius", 500);
        config.addDefault("meteor.height", 150);
        config.addDefault("meteor.speed", 0.5);
        config.addDefault("meteor.explosion-power", 4.0);
        config.addDefault("meteor.crater-radius", 5);
        config.addDefault("meteor.regeneration-time", 10);
        config.addDefault("meteor.fire-spread", true);
        config.addDefault("meteor.obsidian-sphere-radius", 3);

        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    public int getMeteorInterval() {
        return config.getInt("meteor.spawn-interval");
    }

    public String getWorld() {
        return config.getString("meteor.world");
    }

    public int getMinRadius() {
        return config.getInt("meteor.min-radius");
    }

    public int getMaxRadius() {
        return config.getInt("meteor.max-radius");
    }

    public int getHeight() {
        return config.getInt("meteor.height");
    }

    public double getSpeed() {
        return config.getDouble("meteor.speed");
    }

    public float getExplosionPower() {
        return (float) config.getDouble("meteor.explosion-power");
    }

    public int getCraterRadius() {
        return config.getInt("meteor.crater-radius");
    }

    public int getRegenerationTime() {
        return config.getInt("meteor.regeneration-time");
    }

    public boolean isFireSpread() {
        return config.getBoolean("meteor.fire-spread");
    }

    public int getObsidianSphereRadius() {
        return config.getInt("meteor.obsidian-sphere-radius");
    }
}