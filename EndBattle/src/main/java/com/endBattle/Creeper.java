package com.endBattle;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.TNTPrimed;

public class Creeper {
    public static void spawnTNT(Location location) {
        World world = location.getWorld();
        if (world != null) {
            world.spawn(location, TNTPrimed.class);
        }
    }
}
