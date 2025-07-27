package com.Lino.lootMeteor.managers;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Fireball;
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
    private final Set<Fireball> activeFireballs;
    private final Random random;

    public MeteorManager(LootMeteor plugin) {
        this.plugin = plugin;
        this.activeMeteors = new HashMap<>();
        this.activeFireballs = new HashSet<>();
        this.random = new Random();
    }

    public void spawnMeteor() {
        World world = Bukkit.getWorld(plugin.getConfigManager().getWorld());
        if (world == null) return;

        List<Player> onlinePlayers = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(world)) {
                onlinePlayers.add(player);
            }
        }

        if (onlinePlayers.isEmpty()) {
            plugin.getLogger().info("No players online in world " + world.getName() + ", skipping meteor spawn");
            return;
        }

        Player targetPlayer = onlinePlayers.get(random.nextInt(onlinePlayers.size()));

        Location spawnLocation = getRandomLocationNearPlayer(targetPlayer);
        if (spawnLocation == null) return;

        spawnLocation.setY(plugin.getConfigManager().getHeight());

        Fireball meteor = world.spawn(spawnLocation, Fireball.class);
        meteor.setIsIncendiary(false);
        meteor.setYield(0);
        meteor.setGravity(false);
        meteor.setVelocity(new Vector(0, -0.1, 0));
        meteor.setDirection(new Vector(0, -1, 0));
        meteor.setCustomName("LootMeteor");
        meteor.setCustomNameVisible(false);
        meteor.setBounce(false);

        activeFireballs.add(meteor);

        animateMeteor(meteor, spawnLocation);

        for (Player player : world.getPlayers()) {
            if (player.getLocation().distance(spawnLocation) <= plugin.getConfigManager().getNotificationRadius()) {
                player.sendMessage(plugin.getMessageManager().getMessage("meteor-spawning"));
            }
        }

        plugin.getLogger().info("Meteor spawning near player: " + targetPlayer.getName() +
                " at distance: " + targetPlayer.getLocation().distance(spawnLocation));
    }

    private void animateMeteor(Fireball meteor, Location start) {
        Location target = new Location(start.getWorld(), start.getX(),
                start.getWorld().getHighestBlockYAt(start) + 1, start.getZ());

        new BukkitRunnable() {
            double t = 0;
            List<Location> trail = new ArrayList<>();

            @Override
            public void run() {
                if (meteor.isDead()) {
                    activeFireballs.remove(meteor);
                    cancel();
                    return;
                }

                t += plugin.getConfigManager().getSpeed();

                double y = start.getY() - (t * t * 2);
                Location currentLoc = new Location(start.getWorld(), start.getX(), y, start.getZ());
                meteor.teleport(currentLoc);
                meteor.setVelocity(new Vector(0, -0.1, 0));
                meteor.setDirection(new Vector(0, -1, 0));

                trail.add(currentLoc.clone());
                if (trail.size() > 10) {
                    trail.remove(0);
                }

                for (int i = 0; i < trail.size(); i++) {
                    Location trailLoc = trail.get(i);
                    double intensity = (double)(i + 1) / trail.size();
                    trailLoc.getWorld().spawnParticle(Particle.FLAME, trailLoc,
                            (int)(20 * intensity), 0.5, 0.5, 0.5, 0.05);
                    trailLoc.getWorld().spawnParticle(Particle.LAVA, trailLoc,
                            (int)(10 * intensity), 0.3, 0.3, 0.3, 0);
                }

                spawnMeteorEffects(meteor.getLocation());
                playMeteorSound(meteor.getLocation());

                if (y <= target.getY()) {
                    createImpact(target);
                    activeFireballs.remove(meteor);
                    meteor.remove();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnMeteorEffects(Location loc) {
        World world = loc.getWorld();
        world.spawnParticle(Particle.FLAME, loc, 50, 1.0, 1.0, 1.0, 0.2);
        world.spawnParticle(Particle.LAVA, loc, 30, 0.5, 0.5, 0.5, 0);
        world.spawnParticle(Particle.SMOKE, loc, 60, 1.2, 1.2, 1.2, 0.15);
        world.spawnParticle(Particle.FALLING_LAVA, loc, 40, 0.8, 0.8, 0.8, 0);
        world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, loc, 20, 0.5, 0.5, 0.5, 0.1);
        world.spawnParticle(Particle.END_ROD, loc, 10, 0.3, 0.3, 0.3, 0.05);
    }

    private void playMeteorSound(Location loc) {
        loc.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 2.0f, 0.3f);
        loc.getWorld().playSound(loc, Sound.ENTITY_GHAST_SHOOT, 1.5f, 0.2f);
        loc.getWorld().playSound(loc, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.0f, 0.5f);
    }

    private void createImpact(Location center) {
        World world = center.getWorld();
        int radius = plugin.getConfigManager().getCraterRadius();
        int sphereRadius = plugin.getConfigManager().getObsidianSphereRadius();

        TerrainSnapshot snapshot = new TerrainSnapshot(center, radius + sphereRadius);

        world.createExplosion(center, plugin.getConfigManager().getExplosionPower(), false, false);
        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 3.0f, 0.3f);
        world.playSound(center, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 2.0f, 0.5f);
        world.playSound(center, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.5f, 0.7f);

        world.spawnParticle(Particle.EXPLOSION, center, 5, 2, 2, 2, 0);
        world.spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, center, 100, 3, 3, 3, 0.1);
        world.spawnParticle(Particle.LAVA, center, 200, radius, 2, radius, 0);
        world.spawnParticle(Particle.FIREWORK, center, 50, 2, 2, 2, 0.2);

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

        for (Player player : world.getPlayers()) {
            if (player.getLocation().distance(center) <= plugin.getConfigManager().getNotificationRadius()) {
                player.sendMessage(plugin.getMessageManager().getMessage("meteor-spawned",
                        "%x%", String.valueOf(center.getBlockX()),
                        "%y%", String.valueOf(center.getBlockY()),
                        "%z%", String.valueOf(center.getBlockZ())));
            }
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

    private Location getRandomLocationNearPlayer(Player player) {
        int minRadius = plugin.getConfigManager().getMinPlayerDistance();
        int maxRadius = plugin.getConfigManager().getMaxPlayerDistance();
        Location playerLoc = player.getLocation();

        int attempts = 0;
        while (attempts < 100) {
            int radius = random.nextInt(maxRadius - minRadius) + minRadius;
            double angle = random.nextDouble() * 2 * Math.PI;

            int x = (int) (playerLoc.getX() + radius * Math.cos(angle));
            int z = (int) (playerLoc.getZ() + radius * Math.sin(angle));

            Location loc = new Location(playerLoc.getWorld(), x, playerLoc.getWorld().getHighestBlockYAt(x, z), z);

            boolean tooClose = false;
            for (Player p : playerLoc.getWorld().getPlayers()) {
                if (p.getLocation().distance(loc) < minRadius) {
                    tooClose = true;
                    break;
                }
            }

            if (!tooClose && loc.getBlock().getType() != Material.WATER &&
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

        for (Fireball fireball : activeFireballs) {
            fireball.remove();
        }
        activeFireballs.clear();
    }
}