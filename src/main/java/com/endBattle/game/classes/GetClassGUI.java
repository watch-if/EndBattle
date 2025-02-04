package com.endBattle.game.classes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GetClassGUI {

    //创建职业选择界面
    public static void ClassGUI(Player player) {

        Inventory classgui = Bukkit.createInventory(null,2*9, ChatColor.BLACK.WHITE+"职业选择");

        ItemStack diamond_sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta d = diamond_sword.getItemMeta();
        d.setDisplayName("僵尸");
        diamond_sword.setItemMeta(d);

        ItemStack cobweb = new ItemStack(Material.COBWEB);
        ItemMeta c = cobweb.getItemMeta();
        c.setDisplayName("蜘蛛");
        cobweb.setItemMeta(c);

        ItemStack tnt = new ItemStack(Material.TNT);
        ItemMeta t = tnt.getItemMeta();
        t.setDisplayName("苦力怕");
        tnt.setItemMeta(t);

        ItemStack fire_charge = new ItemStack(Material.FIRE_CHARGE);
        ItemMeta f = fire_charge.getItemMeta();
        f.setDisplayName("恶魂");
        fire_charge.setItemMeta(f);

        ItemStack glass_bottle = new ItemStack(Material.GLASS_BOTTLE);
        ItemMeta gl = glass_bottle.getItemMeta();
        gl.setDisplayName("女巫");
        glass_bottle.setItemMeta(gl);

        ItemStack anvil = new ItemStack(Material.ANVIL);
        ItemMeta a = anvil.getItemMeta();
        a.setDisplayName("铁傀儡");
        anvil.setItemMeta(a);

        ItemStack slime_ball = new ItemStack(Material.SLIME_BALL);
        ItemMeta s = slime_ball.getItemMeta();
        s.setDisplayName("史莱姆");
        slime_ball.setItemMeta(s);

        ItemStack bone = new ItemStack(Material.BONE);
        ItemMeta be = bone.getItemMeta();
        be.setDisplayName("骷髅");
        bone.setItemMeta(be);

        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta ba = barrier.getItemMeta();
        ba.setDisplayName("返回上一级");
        barrier.setItemMeta(ba);

        classgui.setItem(0, diamond_sword);
        classgui.setItem(1, cobweb);
        classgui.setItem(2, tnt);
        classgui.setItem(3, fire_charge);
        classgui.setItem(4, glass_bottle);
        classgui.setItem(5, anvil);
        classgui.setItem(6, slime_ball);
        classgui.setItem(7, bone);
        classgui.setItem(17, barrier);

        player.openInventory(classgui);
    }
}
