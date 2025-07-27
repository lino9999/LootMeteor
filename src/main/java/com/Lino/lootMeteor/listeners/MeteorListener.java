package com.Lino.lootMeteor.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;
import com.Lino.lootMeteor.LootMeteor;

public class MeteorListener implements Listener {

    private final LootMeteor plugin;

    public MeteorListener(LootMeteor plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Fireball) {
            Fireball fireball = (Fireball) entity;
            if (fireball.getCustomName() != null && fireball.getCustomName().equals("LootMeteor")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Fireball) {
            Fireball fireball = (Fireball) entity;
            if (fireball.getCustomName() != null && fireball.getCustomName().equals("LootMeteor")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Fireball) {
            Fireball fireball = (Fireball) event.getEntity();
            if (fireball.getCustomName() != null && fireball.getCustomName().equals("LootMeteor")) {
                event.setCancelled(true);
            }
        }
    }
}