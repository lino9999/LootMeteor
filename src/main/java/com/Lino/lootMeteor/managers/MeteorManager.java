package com.Lino.lootMeteor.managers;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import com.Lino.lootMeteor.LootMeteor;
import com.Lino.lootMeteor.utils.MeteorData;
import com.Lino.lootMeteor.utils.TerrainSnapshot;
import java.util.*;

public class MeteorManager {

    private final LootMeteor plugin;
    private final Map<Location, MeteorData> activeMeteors;
    private final Random random;

    public MeteorManager(LootMeteor plugin) {
        this.plugin = plugin;
        this.activeMeteors = new HashMap<>();
        this.random = new Random();
    }

    public void spawnMeteor() {
        World world = Bukkit.getWorld(plugin.getConfigManager().getWorld());
        if (world == null) return;

        Location spawnLocation = getRandomLocation(world);
        if (spawnLocation == null) return;

        spawnLocation.setY(plugin.getConfigManager().getHeight());

        ArmorStand meteor = world.spawn(spawnLocation, ArmorStand.class);
        meteor.setVisible(false);
        meteor.setGravity(false);
        meteor.setInvulnerable(true);
        meteor.setCustomName("LootMeteor");
        meteor.getEquipment().setHelmet(new ItemStack(Material.MAGMA_BLOCK));

        animateMeteor(meteor, spawnLocation);

        Player nearestPlayer = getNearestPlayer(spawnLocation);
        if (nearestPlayer != null) {
            nearestPlayer.sendMessage(plugin.getMessageManager().getMessage("meteor-spawning"));
        }
    }

    private void animateMeteor(ArmorStand meteor, Location start) {
        Location target = new Location(start.getWorld(), start.getX(),
                start.getWorld().getHighestBlockYAt(start) + 1, start.getZ());

        new BukkitRunnable() {
            double t = 0;

            @Override
            public void run() {
                if (meteor.isDead()) {
                    cancel();
                    return;
                }

                t += plugin.getConfigManager().getSpeed();

                double y = start.getY() - (t * t);
                meteor.teleport(new Location(start.getWorld(), start.getX(), y, start.getZ()));

                spawnMeteorEffects(meteor.getLocation());
                playMeteorSound(meteor.getLocation());

                if (y <= target.getY()) {
                    createImpact(target);
                    meteor.remove();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnMeteorEffects(Location loc) {
        World world = loc.getWorld();
        world.spawnParticle(Particle.FLAME, loc, 20, 0.5, 0.5, 0.5, 0.1);
        world.spawnParticle(Particle.LAVA, loc, 10, 0.3, 0.3, 0.3, 0);
        world.spawnParticle(Particle.SMOKE, loc, 30, 0.6, 0.6, 0.6, 0.1);
        world.spawnParticle(Particle.FALLING_LAVA, loc, 15, 0.4, 0.4, 0.4, 0);
    }

    private void playMeteorSound(Location loc) {
        loc.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 1.0f, 0.5f);
        loc.getWorld().playSound(loc, Sound.ENTITY_GHAST_SHOOT, 0.8f, 0.3f);
    }

    private void createImpact(Location center) {
        World world = center.getWorld();
        int radius = plugin.getConfigManager().getCraterRadius();
        int sphereRadius = plugin.getConfigManager().getObsidianSphereRadius();

        TerrainSnapshot snapshot = new TerrainSnapshot(center, radius + sphereRadius);

        world.createExplosion(center, plugin.getConfigManager().getExplosionPower(), false, false);
        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f);
        world.spawnParticle(Particle.EXPLOSION, center, 1, 0, 0, 0, 0);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x*x + y*y + z*z);
                    if (distance <= radius) {
                        Block block = world.getBlockAt(center.clone().add(x, y, z));
                        if (distance <= sphereRadius) {
                            block.setType(Material.OBSIDIAN);
                        } else {
                            block.setType(Material.AIR);
                        }
                    }
                }
            }
        }

        Location chestLoc = center.clone();
        Block chestBlock = chestLoc.getBlock();
        chestBlock.setType(Material.CHEST);
        Chest chest = (Chest) chestBlock.getState();

        List<ItemStack> loot = plugin.getLootManager().generateLoot();
        for (ItemStack item : loot) {
            chest.getInventory().addItem(item);
        }

        if (plugin.getConfigManager().isFireSpread()) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (random.nextInt(3) == 0) {
                        Block block = world.getHighestBlockAt(center.clone().add(x, 0, z)).getRelative(0, 1, 0);
                        if (block.getType() == Material.AIR) {
                            block.setType(Material.FIRE);
                        }
                    }
                }
            }
        }

        MeteorData data = new MeteorData(center, chestLoc, snapshot);
        activeMeteors.put(center, data);

        scheduleRegeneration(center);

        Player nearestPlayer = getNearestPlayer(center);
        if (nearestPlayer != null) {
            nearestPlayer.sendMessage(plugin.getMessageManager().getMessage("meteor-spawned",
                    "%x%", String.valueOf(center.getBlockX()),
                    "%y%", String.valueOf(center.getBlockY()),
                    "%z%", String.valueOf(center.getBlockZ())));
        }
    }

    private void scheduleRegeneration(Location center) {
        new BukkitRunnable() {
            @Override
            public void run() {
                MeteorData data = activeMeteors.remove(center);
                if (data != null) {
                    data.getSnapshot().restore();
                }
            }
        }.runTaskLater(plugin, plugin.getConfigManager().getRegenerationTime() * 60L * 20L);
    }

    private Location getRandomLocation(World world) {
        int minRadius = plugin.getConfigManager().getMinRadius();
        int maxRadius = plugin.getConfigManager().getMaxRadius();

        int attempts = 0;
        while (attempts < 100) {
            int radius = random.nextInt(maxRadius - minRadius) + minRadius;
            double angle = random.nextDouble() * 2 * Math.PI;

            int x = (int) (world.getSpawnLocation().getX() + radius * Math.cos(angle));
            int z = (int) (world.getSpawnLocation().getZ() + radius * Math.sin(angle));

            Location loc = new Location(world, x, world.getHighestBlockYAt(x, z), z);

            if (loc.getBlock().getType() != Material.WATER &&
                    loc.getBlock().getType() != Material.LAVA) {
                return loc;
            }
            attempts++;
        }
        return null;
    }

    private Player getNearestPlayer(Location location) {
        Player nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Player player : location.getWorld().getPlayers()) {
            double distance = player.getLocation().distance(location);
            if (distance < nearestDistance) {
                nearest = player;
                nearestDistance = distance;
            }
        }

        return nearest;
    }

    public boolean isMeteorChest(Location location) {
        for (MeteorData data : activeMeteors.values()) {
            if (data.getChestLocation().equals(location)) {
                return true;
            }
        }
        return false;
    }

    public void cleanup() {
        for (MeteorData data : activeMeteors.values()) {
            data.getSnapshot().restore();
        }
        activeMeteors.clear();
    }
}