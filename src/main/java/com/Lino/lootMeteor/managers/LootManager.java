package com.Lino.lootMeteor.managers;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import com.Lino.lootMeteor.LootMeteor;
import com.Lino.lootMeteor.utils.LootItem;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class LootManager {

    private final LootMeteor plugin;
    private File lootFile;
    private FileConfiguration lootConfig;
    private List<LootItem> lootItems;
    private Random random;

    public LootManager(LootMeteor plugin) {
        this.plugin = plugin;
        this.random = new Random();
        this.lootItems = new ArrayList<>();
        createLootFile();
        loadLoot();
    }

    private void createLootFile() {
        lootFile = new File(plugin.getDataFolder(), "loot.yml");
        if (!lootFile.exists()) {
            lootFile.getParentFile().mkdirs();
            try {
                lootFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        lootConfig = YamlConfiguration.loadConfiguration(lootFile);

        // Create default items only if the file is empty
        if (!lootConfig.contains("loot")) {
            // Use the old format for default items for backward compatibility
            lootConfig.set("loot.diamond.material", "DIAMOND");
            lootConfig.set("loot.diamond.amount", 3);
            lootConfig.set("loot.diamond.chance", 15.0);

            lootConfig.set("loot.iron_ingot.material", "IRON_INGOT");
            lootConfig.set("loot.iron_ingot.amount", 10);
            lootConfig.set("loot.iron_ingot.chance", 40.0);

            lootConfig.set("loot.gold_ingot.material", "GOLD_INGOT");
            lootConfig.set("loot.gold_ingot.amount", 5);
            lootConfig.set("loot.gold_ingot.chance", 25.0);

            lootConfig.set("loot.emerald.material", "EMERALD");
            lootConfig.set("loot.emerald.amount", 2);
            lootConfig.set("loot.emerald.chance", 10.0);

            lootConfig.set("loot.netherite_scrap.material", "NETHERITE_SCRAP");
            lootConfig.set("loot.netherite_scrap.amount", 1);
            lootConfig.set("loot.netherite_scrap.chance", 5.0);

            saveLootConfig();
        }
    }

    public void loadLoot() {
        lootItems.clear();

        if (lootConfig.contains("loot")) {
            for (String key : lootConfig.getConfigurationSection("loot").getKeys(false)) {
                String path = "loot." + key;

                // Check if it's the new format (serialized item)
                if (lootConfig.contains(path + ".item")) {
                    // New format with full item serialization
                    ItemStack item = lootConfig.getItemStack(path + ".item");
                    double chance = lootConfig.getDouble(path + ".chance", 10.0);

                    if (item != null) {
                        lootItems.add(new LootItem(item, chance));
                    }
                } else if (lootConfig.contains(path + ".material")) {
                    // Old format for backward compatibility
                    String materialName = lootConfig.getString(path + ".material");
                    int amount = lootConfig.getInt(path + ".amount", 1);
                    double chance = lootConfig.getDouble(path + ".chance", 10.0);

                    try {
                        Material material = Material.valueOf(materialName);
                        ItemStack item = new ItemStack(material, amount);
                        lootItems.add(new LootItem(item, chance));
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid material: " + materialName);
                    }
                }
            }
        }
    }

    public void saveLootFromInventory(List<ItemStack> items) {
        // Clear existing loot
        lootConfig.set("loot", null);

        int index = 0;
        for (ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                String key = "item_" + index;
                // Save the entire ItemStack with all its metadata
                lootConfig.set("loot." + key + ".item", item);
                lootConfig.set("loot." + key + ".chance", 20.0);
                index++;
            }
        }

        saveLootConfig();
        loadLoot();
    }

    public List<ItemStack> generateLoot() {
        List<ItemStack> generatedLoot = new ArrayList<>();

        for (LootItem lootItem : lootItems) {
            if (random.nextDouble() * 100 <= lootItem.getChance()) {
                generatedLoot.add(lootItem.getItem().clone());
            }
        }

        // Ensure at least one item is generated
        if (generatedLoot.isEmpty() && !lootItems.isEmpty()) {
            LootItem randomItem = lootItems.get(random.nextInt(lootItems.size()));
            generatedLoot.add(randomItem.getItem().clone());
        }

        return generatedLoot;
    }

    public List<ItemStack> getAllLootItems() {
        List<ItemStack> items = new ArrayList<>();
        for (LootItem lootItem : lootItems) {
            items.add(lootItem.getItem().clone());
        }
        return items;
    }

    private void saveLootConfig() {
        try {
            lootConfig.save(lootFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}