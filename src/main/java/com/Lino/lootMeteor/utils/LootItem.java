package com.Lino.lootMeteor.utils;

import org.bukkit.inventory.ItemStack;

public class LootItem {

    private final ItemStack item;
    private final double chance;

    public LootItem(ItemStack item, double chance) {
        this.item = item;
        this.chance = chance;
    }

    public ItemStack getItem() {
        return item;
    }

    public double getChance() {
        return chance;
    }
}