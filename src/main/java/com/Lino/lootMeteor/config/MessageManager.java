package com.Lino.lootMeteor.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.Lino.lootMeteor.LootMeteor;
import java.io.File;
import java.io.IOException;

public class MessageManager {

    private final LootMeteor plugin;
    private File messagesFile;
    private FileConfiguration messagesConfig;

    public MessageManager(LootMeteor plugin) {
        this.plugin = plugin;
        createMessagesFile();
    }

    private void createMessagesFile() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            try {
                messagesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        messagesConfig.addDefault("prefix", "&6[LootMeteor] &r");
        messagesConfig.addDefault("meteor-spawned", "&aA meteor has fallen at coordinates: &e%x%, %y%, %z%");
        messagesConfig.addDefault("meteor-spawning", "&cA meteor is approaching!");
        messagesConfig.addDefault("no-permission", "&cYou don't have permission to use this command!");
        messagesConfig.addDefault("reload-success", "&aConfiguration reloaded successfully!");
        messagesConfig.addDefault("update-available", "&aA new update is available! Version: &e%version%");
        messagesConfig.addDefault("gui-title", "&6LootMeteor Editor");
        messagesConfig.addDefault("gui-save", "&aSave and Exit");
        messagesConfig.addDefault("gui-cancel", "&cCancel");
        messagesConfig.addDefault("loot-saved", "&aLoot configuration saved!");
        messagesConfig.addDefault("invalid-command", "&cInvalid command usage!");
        messagesConfig.addDefault("chest-protected", "&cThis chest is protected!");

        messagesConfig.options().copyDefaults(true);
        save();
    }

    public void reload() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private void save() {
        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&',
                messagesConfig.getString("prefix") + messagesConfig.getString(path));
    }

    public String getMessage(String path, String... replacements) {
        String message = getMessage(path);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }
        return message;
    }
}