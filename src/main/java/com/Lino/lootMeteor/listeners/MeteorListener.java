package com.Lino.lootMeteor.listeners;

import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import com.Lino.lootMeteor.LootMeteor;

public class MeteorListener implements Listener {

    private final LootMeteor plugin;

    public MeteorListener(LootMeteor plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof ArmorStand) {
            ArmorStand armorStand = (ArmorStand) event.getEntity();
            if (armorStand.getCustomName() != null && armorStand.getCustomName().equals("LootMeteor")) {
                event.setCancelled(true);
            }
        }
    }
}