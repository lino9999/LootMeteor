package com.Lino.lootMeteor.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import com.Lino.lootMeteor.LootMeteor;

public class ChestProtectionListener implements Listener {

    private final LootMeteor plugin;

    public ChestProtectionListener(LootMeteor plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.CHEST) {
            if (plugin.getMeteorManager().isMeteorChest(event.getBlock().getLocation())) {
                if (!event.getPlayer().hasPermission("lootmeteor.bypass")) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(plugin.getMessageManager().getMessage("chest-protected"));
                }
            }
        }
    }
}