package com.Lino.lootMeteor.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import java.util.HashMap;
import java.util.Map;

public class TerrainSnapshot {

    private final Location center;
    private final int radius;
    private final Map<Location, BlockData> blocks;

    public TerrainSnapshot(Location center, int radius) {
        this.center = center.clone();
        this.radius = radius;
        this.blocks = new HashMap<>();
        saveBlocks();
    }

    private void saveBlocks() {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location loc = center.clone().add(x, y, z);
                    Block block = loc.getBlock();
                    if (block.getType() != Material.AIR) {
                        blocks.put(loc, block.getBlockData().clone());
                    }
                }
            }
        }
    }

    public void restore() {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location loc = center.clone().add(x, y, z);
                    Block block = loc.getBlock();

                    BlockData originalData = blocks.get(loc);
                    if (originalData != null) {
                        block.setBlockData(originalData);
                    } else {
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }
}