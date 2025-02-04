package com.endBattle.game.mainshop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Shop {

    //主商店界面
    public static void ShopGUI(Player player){

        Inventory shop = Bukkit.createInventory(null, 1 * 9, ChatColor.RED + "商店");

        ItemStack diamond_sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta d = diamond_sword.getItemMeta();
        d.setDisplayName(ChatColor.RED + "武器装备");
        diamond_sword.setItemMeta(d);

        ItemStack white_wool = new ItemStack(Material.WHITE_WOOL);
        ItemMeta w = white_wool.getItemMeta();
        w.setDisplayName(ChatColor.YELLOW + "建筑方块");
        white_wool.setItemMeta(w);

        ItemStack ender_eye = new ItemStack(Material.ENDER_EYE);
        ItemMeta e = ender_eye.getItemMeta();
        e.setDisplayName(ChatColor.AQUA + "技能");
        ender_eye.setItemMeta(e);

        shop.setItem(2, diamond_sword);
        shop.setItem(4, white_wool);
        shop.setItem(6, ender_eye);

        player.openInventory(shop);
    }

    //武器装备界面
    public static void weaponry(Player player) {

        Inventory weaponry_gui = Bukkit.createInventory(null,5*9,ChatColor.RED + "武器装备");

        ItemStack stone_sword = new ItemStack(Material.STONE_SWORD);
        ItemMeta sw = stone_sword.getItemMeta();
        ArrayList<String> STONE_SWORD_lore = new ArrayList<>();
        STONE_SWORD_lore.add("10铁/把");
        sw.setLore(STONE_SWORD_lore);
        stone_sword.setItemMeta(sw);

        ItemStack iron_sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta iron = iron_sword.getItemMeta();
        ArrayList<String> IRON_SWORD_lore = new ArrayList<>();
        IRON_SWORD_lore.add("7金/把");
        iron.setLore(IRON_SWORD_lore);
        iron_sword.setItemMeta(iron);

        ItemStack diamond_sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta ds = diamond_sword.getItemMeta();
        ArrayList<String> DIAMOND_SWORD_lore = new ArrayList<>();
        DIAMOND_SWORD_lore.add("4钻石/把");
        ds.setLore(DIAMOND_SWORD_lore);
        diamond_sword.setItemMeta(ds);

        ItemStack wooden_pickaxe = new ItemStack(Material.WOODEN_PICKAXE);
        ItemMeta wp = wooden_pickaxe.getItemMeta();
        ArrayList<String> WOODEN_PICKAXE_lore = new ArrayList<>();
        WOODEN_PICKAXE_lore.add("10铁/把");
        wp.setLore(WOODEN_PICKAXE_lore);
        wooden_pickaxe.setItemMeta(wp);

        ItemStack stone_pickaxe = new ItemStack(Material.STONE_PICKAXE);
        ItemMeta sp = stone_pickaxe.getItemMeta();
        ArrayList<String> STONE_PICKAXE_lore = new ArrayList<>();
        STONE_PICKAXE_lore.add("20铁/把");
        sp.setLore(STONE_PICKAXE_lore);
        stone_pickaxe.setItemMeta(sp);

        ItemStack iron_pickaxe = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta ip = iron_pickaxe.getItemMeta();
        ArrayList<String> IRON_PICKAXE_lore = new ArrayList<>();
        IRON_PICKAXE_lore.add("8金/把");
        ip.setLore(IRON_PICKAXE_lore);
        iron_pickaxe.setItemMeta(ip);

        ItemStack diamond_pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta dp = diamond_pickaxe.getItemMeta();
        ArrayList<String> DIAMOND_PICKAXE_lore = new ArrayList<>();
        DIAMOND_PICKAXE_lore.add("12金/把");
        dp.setLore(DIAMOND_PICKAXE_lore);
        diamond_pickaxe.setItemMeta(dp);

        ItemStack diamond_axe = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta da = diamond_axe.getItemMeta();
        ArrayList<String> DIAMOND_AXE_lore = new ArrayList<>();
        DIAMOND_AXE_lore.add("12金/把");
        da.setLore(DIAMOND_AXE_lore);
        diamond_axe.setItemMeta(da);

        ItemStack shears = new ItemStack(Material.SHEARS);
        ItemMeta sh = shears.getItemMeta();
        ArrayList<String> SHEARS_lore = new ArrayList<>();
        SHEARS_lore.add("30铁/个");
        sh.setLore(SHEARS_lore);
        shears.setItemMeta(sh);

        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta b = bow.getItemMeta();
        ArrayList<String> BOW_lore = new ArrayList<>();
        BOW_lore.add("10铁/把");
        b.setLore(BOW_lore);
        bow.setItemMeta(b);

        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta ar = arrow.getItemMeta();
        ArrayList<String> ARROW_lore = new ArrayList<>();
        ARROW_lore.add("2金/8根");
        ar.setLore(ARROW_lore);
        arrow.setItemMeta(ar);

        ItemStack crossbow = new ItemStack(Material.CROSSBOW);
        ItemMeta cb = crossbow.getItemMeta();
        ArrayList<String> CROSSBOW_lore = new ArrayList<>();
        CROSSBOW_lore.add("20铁/把");
        cb.setLore(CROSSBOW_lore);
        crossbow.setItemMeta(cb);

        ItemStack chainmail_chestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        ItemMeta ch = chainmail_chestplate.getItemMeta();
        ArrayList<String> CHAINMAIL_CHESTPLATE_lore = new ArrayList<>();
        CHAINMAIL_CHESTPLATE_lore.add("40铁/套");
        ch.setLore(CHAINMAIL_CHESTPLATE_lore);
        chainmail_chestplate.setItemMeta(ch);

        ItemStack iron_chestplate = new ItemStack(Material.IRON_CHESTPLATE);
        ItemMeta ir = iron_chestplate.getItemMeta();
        ArrayList<String> IRON_CHESTPLATE_lore = new ArrayList<>();
        IRON_CHESTPLATE_lore.add("12金/套");
        ir.setLore(IRON_CHESTPLATE_lore);
        iron_chestplate.setItemMeta(ir);

        ItemStack diamond_chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta dc = diamond_chestplate.getItemMeta();
        ArrayList<String> DIAMOND_CHESTPLATE_lore = new ArrayList<>();
        DIAMOND_CHESTPLATE_lore.add("6钻石/套");
        dc.setLore(DIAMOND_CHESTPLATE_lore);
        diamond_chestplate.setItemMeta(dc);

        ItemStack shield = new ItemStack(Material.SHIELD);
        ItemMeta sd = shield.getItemMeta();
        ArrayList<String> SHIELD_lore = new ArrayList<>();
        SHIELD_lore.add("40铁/个");
        sd.setLore(SHIELD_lore);
        shield.setItemMeta(sd);

        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta ba = barrier.getItemMeta();
        ba.setDisplayName("返回上一级");
        barrier.setItemMeta(ba);

        weaponry_gui.setItem(1,stone_sword);
        weaponry_gui.setItem(2,iron_sword);
        weaponry_gui.setItem(3,diamond_sword);
        weaponry_gui.setItem(10,wooden_pickaxe);
        weaponry_gui.setItem(11,stone_pickaxe);
        weaponry_gui.setItem(12,iron_pickaxe);
        weaponry_gui.setItem(13,diamond_pickaxe);
        weaponry_gui.setItem(19,diamond_axe);
        weaponry_gui.setItem(20,shears);
        weaponry_gui.setItem(21,bow);
        weaponry_gui.setItem(22,arrow);
        weaponry_gui.setItem(23,crossbow);
        weaponry_gui.setItem(28,chainmail_chestplate);
        weaponry_gui.setItem(29,iron_chestplate);
        weaponry_gui.setItem(30,diamond_chestplate);
        weaponry_gui.setItem(31,shield);
        weaponry_gui.setItem(44,barrier);

        player.openInventory(weaponry_gui);

    }

    //建筑类方块界面
    public static void Structure_block(Player player) {

        Inventory structure_block_gui = Bukkit.createInventory(null,4*9,ChatColor.YELLOW + "建筑方块");

        ItemStack white_wool = new ItemStack(Material.WHITE_WOOL);
        ItemMeta w = white_wool.getItemMeta();
        ArrayList<String> WHITE_WOOL_lore = new ArrayList<>();
        WHITE_WOOL_lore.add("4铁/16个");
        w.setLore(WHITE_WOOL_lore);
        white_wool.setItemMeta(w);

        ItemStack oak_wood_planks = new ItemStack(Material.OAK_PLANKS);
        ItemMeta owp = oak_wood_planks.getItemMeta();
        ArrayList<String> OAK_PLANKS_lore = new ArrayList<>();
        OAK_PLANKS_lore.add("4金/个");
        owp.setLore(OAK_PLANKS_lore);
        oak_wood_planks.setItemMeta(owp);

        ItemStack end_stone = new ItemStack(Material.END_STONE);
        ItemMeta es = end_stone.getItemMeta();
        ArrayList<String> END_STONE_lore = new ArrayList<>();
        END_STONE_lore.add("24铁/个");
        es.setLore(END_STONE_lore);
        end_stone.setItemMeta(es);

        ItemStack obsidian = new ItemStack(Material.OBSIDIAN);
        ItemMeta obs = obsidian.getItemMeta();
        ArrayList<String> OBSIDIAN_lore = new ArrayList<>();
        OBSIDIAN_lore.add("4钻石/个");
        obs.setLore(OBSIDIAN_lore);
        obsidian.setItemMeta(obs);

        ItemStack glass = new ItemStack(Material.GLASS);
        ItemMeta gl = glass.getItemMeta();
        ArrayList<String> GLASS_lore = new ArrayList<>();
        GLASS_lore.add("4金锭/个");
        gl.setLore(GLASS_lore);
        glass.setItemMeta(gl);

        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta ba = barrier.getItemMeta();
        ba.setDisplayName("返回上一级");
        barrier.setItemMeta(ba);

        structure_block_gui.setItem(10,white_wool);
        structure_block_gui.setItem(11,oak_wood_planks);
        structure_block_gui.setItem(12,end_stone);
        structure_block_gui.setItem(13,obsidian);
        structure_block_gui.setItem(14,glass);
        structure_block_gui.setItem(35,barrier);

        player.openInventory(structure_block_gui);

    }
}
