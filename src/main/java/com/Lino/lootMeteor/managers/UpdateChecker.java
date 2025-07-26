package com.Lino.lootMeteor.managers;

import org.bukkit.Bukkit;
import com.Lino.lootMeteor.LootMeteor;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URI;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

    private final LootMeteor plugin;
    private final int resourceId;

    public UpdateChecker(LootMeteor plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (InputStream inputStream = URI.create("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId)
                    .toURL().openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                plugin.getLogger().info("Unable to check for updates: " + exception.getMessage());
            }
        });
    }

    public void checkForUpdates() {
        getVersion(version -> {
            if (!plugin.getDescription().getVersion().equalsIgnoreCase(version)) {
                plugin.getLogger().info("There is a new update available! Version: " + version);
                plugin.getLogger().info("Download it at: https://www.spigotmc.org/resources/" + resourceId);

                Bukkit.getScheduler().runTask(plugin, () -> {
                    Bukkit.getOnlinePlayers().stream()
                            .filter(player -> player.hasPermission("lootmeteor.admin"))
                            .forEach(player -> player.sendMessage(
                                    plugin.getMessageManager().getMessage("update-available", "%version%", version)
                            ));
                });
            }
        });
    }
}