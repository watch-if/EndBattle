package com.endBattle;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Zombie {

    public void placeBlockUnderPlayer(Player player) {
        ItemStack itemInHand = player.getInventory().getItem(0);
        if (itemInHand != null && itemInHand.getType().isBlock()) {
            Location playerLocation = player.getLocation();
            Location targetLocation = new Location(player.getWorld(), playerLocation.getX(), 122, playerLocation.getZ());
            if (targetLocation.getBlock().getType() == Material.AIR) {
                targetLocation.getBlock().setType(itemInHand.getType());
                itemInHand.setAmount(itemInHand.getAmount() - 1);
                if (itemInHand.getAmount() <= 0) {
                    player.getInventory().setItem(0, null);
                }
            }
        }
    }

}