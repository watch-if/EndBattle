package com.endBattle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Enter implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Inventory entergui = Bukkit.createInventory(null,1*9,"大厅选择");

        ItemStack dragonhead = new ItemStack(Material.DRAGON_HEAD);
        ItemMeta m = dragonhead.getItemMeta();
        m.setDisplayName("§4终末之战");
        dragonhead.setItemMeta(m);
        entergui.setItem(2, dragonhead);

        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta n = compass.getItemMeta();
        n.setDisplayName("§e主城");
        compass.setItemMeta(n);
        entergui.setItem(6, compass);
        Player p = (Player) sender;
        p.openInventory(entergui);
        return false;
    }

    @EventHandler
    public void check(InventoryClickEvent ch) {
        Player p = (Player) ch.getWhoClicked();
        String inventoryTitle = ch.getWhoClicked().getOpenInventory().getTitle();
        if (inventoryTitle.replaceAll("§.", "").trim().equals("大厅选择")) {
            ch.setCancelled(true);
            if (ch.getRawSlot() > ch.getInventory().getSize()) {
                p.sendMessage("无效操作");
                return;
            }
            if (ch.getRawSlot() < 0) {
                p.sendMessage("无效操作");
                return;
            }
            if (ch.getRawSlot() == 2) {
                Location endLocation = new Location(Bukkit.getWorld("world_the_end"), 0, 201, 0);
                p.teleport(endLocation);
                return;
            }
            if (ch.getRawSlot() == 6) {
                Location mainWorldLocation = new Location(Bukkit.getWorld("world"), 0, 0, 0);
                p.teleport(mainWorldLocation);
                return;
            }
        }
        return;
    }
}
