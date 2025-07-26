package com.Lino.lootMeteor.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.Lino.lootMeteor.LootMeteor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LootEditorGUI implements Listener {

    private final LootMeteor plugin;
    private final Player player;
    private final Inventory inventory;
    private boolean saving = false;

    public LootEditorGUI(LootMeteor plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 54,
                plugin.getMessageManager().getMessage("gui-title").replace(plugin.getMessageManager().getMessage("prefix"), ""));

        loadCurrentLoot();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open() {
        player.openInventory(inventory);
    }

    private void loadCurrentLoot() {
        List<ItemStack> lootItems = plugin.getLootManager().getAllLootItems();
        for (int i = 0; i < Math.min(lootItems.size(), 45); i++) {
            inventory.setItem(i, lootItems.get(i));
        }

        ItemStack saveButton = createButton(Material.EMERALD_BLOCK,
                plugin.getMessageManager().getMessage("gui-save").replace(plugin.getMessageManager().getMessage("prefix"), ""),
                Arrays.asList("§7Click to save changes"));

        ItemStack cancelButton = createButton(Material.REDSTONE_BLOCK,
                plugin.getMessageManager().getMessage("gui-cancel").replace(plugin.getMessageManager().getMessage("prefix"), ""),
                Arrays.asList("§7Click to discard changes"));

        inventory.setItem(49, saveButton);
        inventory.setItem(50, cancelButton);

        for (int i = 45; i < 54; i++) {
            if (i != 49 && i != 50) {
                inventory.setItem(i, createButton(Material.GRAY_STAINED_GLASS_PANE, " ", new ArrayList<>()));
            }
        }
    }

    private ItemStack createButton(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory)) return;

        if (event.getSlot() >= 45) {
            event.setCancelled(true);

            if (event.getSlot() == 49) {
                saveLoot();
            } else if (event.getSlot() == 50) {
                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory) && !saving) {
            HandlerList.unregisterAll(this);
        }
    }

    private void saveLoot() {
        saving = true;
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < 45; i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                items.add(item);
            }
        }

        plugin.getLootManager().saveLootFromInventory(items);
        player.sendMessage(plugin.getMessageManager().getMessage("loot-saved"));
        player.closeInventory();
        HandlerList.unregisterAll(this);
    }
}