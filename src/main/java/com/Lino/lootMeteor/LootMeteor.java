package com.Lino.lootMeteor;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import com.Lino.lootMeteor.commands.LootMeteorCommand;
import com.Lino.lootMeteor.config.ConfigManager;
import com.Lino.lootMeteor.config.MessageManager;
import com.Lino.lootMeteor.listeners.ChestProtectionListener;
import com.Lino.lootMeteor.listeners.MeteorListener;
import com.Lino.lootMeteor.managers.LootManager;
import com.Lino.lootMeteor.managers.MeteorManager;
import com.Lino.lootMeteor.managers.UpdateChecker;
import com.Lino.lootMeteor.tasks.MeteorSpawnTask;

public class LootMeteor extends JavaPlugin {

    private static LootMeteor instance;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private LootManager lootManager;
    private MeteorManager meteorManager;
    private UpdateChecker updateChecker;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);
        lootManager = new LootManager(this);
        meteorManager = new MeteorManager(this);
        updateChecker = new UpdateChecker(this, 123456);

        registerCommands();
        registerListeners();

        new MeteorSpawnTask(this).runTaskTimer(this, 20L, configManager.getMeteorInterval() * 60L * 20L);

        updateChecker.checkForUpdates();

        getLogger().info("LootMeteor has been enabled!");
    }

    @Override
    public void onDisable() {
        meteorManager.cleanup();
        getLogger().info("LootMeteor has been disabled!");
    }

    private void registerCommands() {
        getCommand("lootmeteor").setExecutor(new LootMeteorCommand(this));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new ChestProtectionListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MeteorListener(this), this);
    }

    public static LootMeteor getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public LootManager getLootManager() {
        return lootManager;
    }

    public MeteorManager getMeteorManager() {
        return meteorManager;
    }
}