package com.Lino.lootMeteor.utils;

import org.bukkit.Location;

public class MeteorData {

    private final Location impactLocation;
    private final Location chestLocation;
    private final TerrainSnapshot snapshot;

    public MeteorData(Location impactLocation, Location chestLocation, TerrainSnapshot snapshot) {
        this.impactLocation = impactLocation;
        this.chestLocation = chestLocation;
        this.snapshot = snapshot;
    }

    public Location getImpactLocation() {
        return impactLocation;
    }

    public Location getChestLocation() {
        return chestLocation;
    }

    public TerrainSnapshot getSnapshot() {
        return snapshot;
    }
}