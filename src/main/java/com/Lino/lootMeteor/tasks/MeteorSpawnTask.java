package com.Lino.lootMeteor.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import com.Lino.lootMeteor.LootMeteor;

public class MeteorSpawnTask extends BukkitRunnable {

    private final LootMeteor plugin;

    public MeteorSpawnTask(LootMeteor plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getMeteorManager().spawnMeteor();
    }
}