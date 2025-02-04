package com.endBattle.game.Teams;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GetTeamGUI {

    //创建玩家选择队伍界面
    public static void TeamGUI(Player player) {

        Inventory teamgui = Bukkit.createInventory(null,1*9, ChatColor.RED.YELLOW.BLUE.GREEN+"队伍选择");

        ItemStack red_wool = new ItemStack(Material.RED_WOOL);
        ItemMeta r = red_wool.getItemMeta();
        r.setDisplayName("§c红队");
        red_wool.setItemMeta(r);

        ItemStack yellow_wool = new ItemStack(Material.YELLOW_WOOL);
        ItemMeta y = yellow_wool.getItemMeta();
        y.setDisplayName("§e黄队");
        yellow_wool.setItemMeta(y);

        ItemStack blue_wool = new ItemStack(Material.BLUE_WOOL);
        ItemMeta b = blue_wool.getItemMeta();
        b.setDisplayName("§9蓝队");
        blue_wool.setItemMeta(b);

        ItemStack green_wool = new ItemStack(Material.GREEN_WOOL);
        ItemMeta g = green_wool.getItemMeta();
        g.setDisplayName("§a绿队");
        green_wool.setItemMeta(g);

        teamgui.setItem(1, red_wool);
        teamgui.setItem(3, yellow_wool);
        teamgui.setItem(5, blue_wool);
        teamgui.setItem(7, green_wool);

        player.openInventory(teamgui);

    }
}
