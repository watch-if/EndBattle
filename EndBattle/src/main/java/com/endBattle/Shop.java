package com.endBattle;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Shop {

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
        weaponry_gui.setItem(28,chainmail_chestplate);
        weaponry_gui.setItem(29,iron_chestplate);
        weaponry_gui.setItem(30,diamond_chestplate);
        weaponry_gui.setItem(31,shield);
        weaponry_gui.setItem(44,barrier);

        player.openInventory(weaponry_gui);

    }

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

        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta ba = barrier.getItemMeta();
        ba.setDisplayName("返回上一级");
        barrier.setItemMeta(ba);

        structure_block_gui.setItem(10,white_wool);
        structure_block_gui.setItem(11,oak_wood_planks);
        structure_block_gui.setItem(12,end_stone);
        structure_block_gui.setItem(13,obsidian);
        structure_block_gui.setItem(35,barrier);

        player.openInventory(structure_block_gui);

    }

    public static void zombie(Player player) {

        Inventory zombie_gui = Bukkit.createInventory(null, 2*9, ChatColor.RED + "僵尸");

        ItemStack ender_pearl = new ItemStack(Material.ENDER_PEARL);
        ItemMeta e = ender_pearl.getItemMeta();
        ArrayList<String> ENDER_PEARL_lore = new ArrayList<>();
        ENDER_PEARL_lore.add("10金/个");
        e.setLore(ENDER_PEARL_lore);
        ender_pearl.setItemMeta(e);

        ItemStack potion_of_swift = new ItemStack(Material.POTION);
        ItemMeta pos = potion_of_swift.getItemMeta();
        pos.setDisplayName(ChatColor.RED+"僵尸速度药水");
        ArrayList<String> potion_of_swift_lore = new ArrayList<>();
        potion_of_swift_lore.add("20铁/瓶");
        pos.setLore(potion_of_swift_lore);
        potion_of_swift.setItemMeta(pos);

        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta ba = barrier.getItemMeta();
        ba.setDisplayName("返回上一级");
        barrier.setItemMeta(ba);

        zombie_gui.setItem(2,ender_pearl);
        zombie_gui.setItem(6,potion_of_swift);
        zombie_gui.setItem(17,barrier);

        player.openInventory(zombie_gui);
    }

    public static void spider(Player player) {

        Inventory spider_gui = Bukkit.createInventory(null,2*9,ChatColor.BLACK+"蜘蛛");

        ItemStack cobweb = new ItemStack(Material.COBWEB);
        ItemMeta cob = cobweb.getItemMeta();
        ArrayList<String> cob_lore = new ArrayList<>();
        cob_lore.add("40铁/个");
        cob.setLore(cob_lore);
        cobweb.setItemMeta(cob);

        ItemStack stick = new ItemStack(Material.STICK);
        ItemMeta sticks = stick.getItemMeta();
        sticks.setDisplayName(ChatColor.GREEN+"剧毒木棒");
        ArrayList<String> stick_lore = new ArrayList<>();
        stick_lore.add("3钻石/个");
        sticks.setLore(stick_lore);
        stick.setItemMeta(sticks);

        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta ba = barrier.getItemMeta();
        ba.setDisplayName("返回上一级");
        barrier.setItemMeta(ba);

        spider_gui.setItem(2,cobweb);
        spider_gui.setItem(6,stick);
        spider_gui.setItem(17,barrier);

        player.openInventory(spider_gui);
    }

    public static void crepper(Player player) {

        Inventory crepper_gui = Bukkit.createInventory(null,2*9,ChatColor.GREEN+"苦力怕");;

        ItemStack tnt = new ItemStack(Material.TNT);
        ItemMeta tn = tnt.getItemMeta();
        ArrayList<String> tnlore = new ArrayList<>();
        tnlore.add("5金/个");
        tn.setLore(tnlore);
        tnt.setItemMeta(tn);

        ItemStack snowball = new ItemStack(Material.SNOWBALL);
        ItemMeta snow = snowball.getItemMeta();
        snow.setDisplayName(ChatColor.WHITE.RED + "爆炸雪球");
        ArrayList<String> snowlore = new ArrayList<>();
        snowlore.add("5金/个");
        snow.setLore(snowlore);
        snowball.setItemMeta(snow);

        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta ba = barrier.getItemMeta();
        ba.setDisplayName("返回上一级");
        barrier.setItemMeta(ba);

        crepper_gui.setItem(2,tnt);
        crepper_gui.setItem(6,snowball);
        crepper_gui.setItem(17,barrier);

        player.openInventory(crepper_gui);
    }

    public static void ghast(Player player) {

        Inventory ghast_gui = Bukkit.createInventory(null,2*9,ChatColor.WHITE.LIGHT_PURPLE+"恶魂");

        ItemStack fire_charge = new ItemStack(Material.FIRE_CHARGE);
        ItemMeta fc = fire_charge.getItemMeta();
        ArrayList<String> fclore = new ArrayList<>();
        fclore.add("4金/个");
        fc.setLore(fclore);
        fire_charge.setItemMeta(fc);

        ItemStack potion_of_leaping = new ItemStack(Material.POTION);
        ItemMeta pol = potion_of_leaping.getItemMeta();
        pol.setDisplayName(ChatColor.WHITE+"恶魂跳跃药水");
        ArrayList<String> potion_of_leaping_lore = new ArrayList<>();
        potion_of_leaping_lore.add("20铁/个");
        pol.setLore(potion_of_leaping_lore);
        potion_of_leaping.setItemMeta(pol);

        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta ba = barrier.getItemMeta();
        ba.setDisplayName("返回上一级");
        barrier.setItemMeta(ba);


        ghast_gui.setItem(2,fire_charge);
        ghast_gui.setItem(6,potion_of_leaping);
        ghast_gui.setItem(17,barrier);

        player.openInventory(ghast_gui);
    }

    public static void witch(Player player) {

        Inventory witch_gui = Bukkit.createInventory(null,27,ChatColor.AQUA+"女巫");

        ItemStack glass_bottle = new ItemStack(Material.GLASS_BOTTLE);
        ItemMeta gb = glass_bottle.getItemMeta();
        ArrayList<String> glass_bottle_lore = new ArrayList<>();
        glass_bottle_lore.add("20铁/瓶");
        gb.setLore(glass_bottle_lore);
        glass_bottle.setItemMeta(gb);

        ItemStack potion = new ItemStack(Material.POTION);
        ItemMeta po = potion.getItemMeta();
        ArrayList<String> potion_lore = new ArrayList<>();
        potion_lore.add("30铁/瓶");
        po.setLore(potion_lore);
        potion.setItemMeta(po);

        ItemStack fermented_spider_eye = new ItemStack(Material.FERMENTED_SPIDER_EYE);
        ItemMeta ferm = fermented_spider_eye.getItemMeta();
        ArrayList<String> ferm_lore = new ArrayList<>();
        ferm_lore.add("40铁/个");
        ferm.setLore(ferm_lore);
        fermented_spider_eye.setItemMeta(ferm);

        ItemStack nether_wart = new ItemStack(Material.NETHER_WART);
        ItemMeta net = nether_wart.getItemMeta();
        ArrayList<String> net_lore = new ArrayList<>();
        net_lore.add("30铁/个");
        net.setLore(net_lore);
        nether_wart.setItemMeta(net);

        ItemStack gunpowder = new ItemStack(Material.GUNPOWDER);
        ItemMeta gp = gunpowder.getItemMeta();
        ArrayList<String> gp_lore = new ArrayList<>();
        gp_lore.add("10金/个");
        gp.setLore(gp_lore);
        gunpowder.setItemMeta(gp);

        ItemStack spider_eye =  new ItemStack(Material.SPIDER_EYE);
        ItemMeta sp = spider_eye.getItemMeta();
        ArrayList<String> sp_lore = new ArrayList<>();
        sp_lore.add("30铁/个");
        sp.setLore(sp_lore);
        spider_eye.setItemMeta(sp);

        ItemStack ghast_tear = new ItemStack(Material.GHAST_TEAR);
        ItemMeta gh = ghast_tear.getItemMeta();
        ArrayList<String> gh_lore = new ArrayList<>();
        gh_lore.add("40铁/个");
        gh.setLore(gh_lore);
        ghast_tear.setItemMeta(gh);

        ItemStack rabbit_foot = new ItemStack(Material.RABBIT_FOOT);
        ItemMeta rf = rabbit_foot.getItemMeta();
        ArrayList<String> rf_lore = new ArrayList<>();
        rf_lore.add("40铁/个");
        rf.setLore(rf_lore);
        rabbit_foot.setItemMeta(rf);

        ItemStack blaze_powder = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta bl = blaze_powder.getItemMeta();
        ArrayList<String> bl_lore = new ArrayList<>();
        bl_lore.add("40铁/个");
        bl.setLore(bl_lore);
        blaze_powder.setItemMeta(bl);

        ItemStack glistering_melon_slice = new ItemStack(Material.GLISTERING_MELON_SLICE);
        ItemMeta gl = glistering_melon_slice.getItemMeta();
        ArrayList<String> gl_lore = new ArrayList<>();
        gl_lore.add("40铁/个");
        gl.setLore(gl_lore);
        glistering_melon_slice.setItemMeta(gl);

        ItemStack sugar = new ItemStack(Material.SUGAR);
        ItemMeta sug = sugar.getItemMeta();
        ArrayList<String> sug_lore = new ArrayList<>();
        sug_lore.add("40铁/个");
        sug.setLore(sug_lore);
        sugar.setItemMeta(sug);

        ItemStack magma_cream = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta mc = magma_cream.getItemMeta();
        ArrayList<String> mc_lore = new ArrayList<>();
        mc_lore.add("40铁/个");
        mc.setLore(mc_lore);
        magma_cream.setItemMeta(mc);

        ItemStack redstone = new ItemStack(Material.REDSTONE);
        ItemMeta rw = redstone.getItemMeta();
        ArrayList<String> rw_lore = new ArrayList<>();
        rw_lore.add("30铁/个");
        rw.setLore(rw_lore);
        redstone.setItemMeta(rw);

        ItemStack glowstone_dust = new ItemStack(Material.GLOWSTONE_DUST);
        ItemMeta gd = glowstone_dust.getItemMeta();
        ArrayList<String> gd_lore = new ArrayList<>();
        gd_lore.add("30铁/个");
        gd.setLore(gd_lore);
        glowstone_dust.setItemMeta(gd);

        ItemStack pufferfish = new ItemStack(Material.PUFFERFISH);
        ItemMeta pu = pufferfish.getItemMeta();
        ArrayList<String> pu_lore = new ArrayList<>();
        pu_lore.add("40铁/个");
        pu.setLore(pu_lore);
        pufferfish.setItemMeta(pu);

        ItemStack golden_carrot = new ItemStack(Material.GOLDEN_CARROT);
        ItemMeta gcd = golden_carrot.getItemMeta();
        ArrayList<String> gcd_lore = new ArrayList<>();
        gcd_lore.add("40铁/个");
        gcd.setLore(gcd_lore);
        golden_carrot.setItemMeta(gcd);

        ItemStack turtle_shell = new ItemStack(Material.TURTLE_HELMET);
        ItemMeta ts = turtle_shell.getItemMeta();
        ArrayList<String> ts_lore = new ArrayList<>();
        ts_lore.add("10金/个");
        ts.setLore(ts_lore);
        turtle_shell.setItemMeta(ts);

        ItemStack phantom_membrane = new ItemStack(Material.PHANTOM_MEMBRANE);
        ItemMeta pm = phantom_membrane.getItemMeta();
        ArrayList<String> pm_lore = new ArrayList<>();
        pm_lore.add("40铁/个");
        pm.setLore(pm_lore);
        phantom_membrane.setItemMeta(pm);

        ItemStack dragon_breath = new ItemStack(Material.DRAGON_BREATH);
        ItemMeta db = dragon_breath.getItemMeta();
        ArrayList<String> db_lore = new ArrayList<>();
        bl_lore.add("30铁/个");
        db.setLore(db_lore);
        dragon_breath.setItemMeta(db);

        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta ba = barrier.getItemMeta();
        ba.setDisplayName("返回上一级");
        barrier.setItemMeta(ba);

        witch_gui.setItem(0,glass_bottle);
        witch_gui.setItem(1,potion);
        witch_gui.setItem(2,fermented_spider_eye);
        witch_gui.setItem(3,nether_wart);
        witch_gui.setItem(4,gunpowder);
        witch_gui.setItem(5,spider_eye);
        witch_gui.setItem(6,ghast_tear);
        witch_gui.setItem(7,rabbit_foot);
        witch_gui.setItem(8,blaze_powder);
        witch_gui.setItem(9,glistering_melon_slice);
        witch_gui.setItem(10,sugar);
        witch_gui.setItem(11,magma_cream);
        witch_gui.setItem(12,redstone);
        witch_gui.setItem(13,glowstone_dust);
        witch_gui.setItem(14,pufferfish);
        witch_gui.setItem(15,golden_carrot);
        witch_gui.setItem(16,turtle_shell);
        witch_gui.setItem(26,phantom_membrane);

        player.openInventory(witch_gui);

    }

    public static void skeleton(Player player){

        Inventory skeleton_gui = Bukkit.createInventory(null, 2*9, ChatColor.WHITE+"骷髅");

        ItemStack firework_rocket = new ItemStack(Material.FIREWORK_ROCKET);
        ItemMeta fro = firework_rocket.getItemMeta();
        ArrayList<String> fro_lore = new ArrayList<>();
        fro_lore.add("1钻石/个");
        fro.setLore(fro_lore);
        firework_rocket.setItemMeta(fro);

        ItemStack harming_arrow = new ItemStack(Material.ARROW);
        ItemMeta hm = harming_arrow.getItemMeta();
        hm.setDisplayName(ChatColor.RED+"伤害之箭");
        ArrayList<String> hm_lore = new ArrayList<>();
        hm_lore.add("40铁/把");
        hm.setLore(hm_lore);
        harming_arrow.setItemMeta(hm);

        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta ba = barrier.getItemMeta();
        ba.setDisplayName("返回上一级");
        barrier.setItemMeta(ba);

        skeleton_gui.setItem(2,firework_rocket);
        skeleton_gui.setItem(6,harming_arrow);
        skeleton_gui.setItem(17,barrier);

        player.openInventory(skeleton_gui);
    }

    public static void iron_golem(Player player){

        Inventory iron_golem_gui = Bukkit.createInventory(null,9,ChatColor.WHITE.GREEN+"铁傀儡");

        ItemStack stick = new ItemStack(Material.STICK);
        ItemMeta st = stick.getItemMeta();
        st.setDisplayName(ChatColor.WHITE+"击退木棒");
        ArrayList<String> st_lore = new ArrayList<>();
        st_lore.add("10金/个");
        st.setLore(st_lore);
        stick.setItemMeta(st);

        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta ba = barrier.getItemMeta();
        ba.setDisplayName("返回上一级");
        barrier.setItemMeta(ba);

        iron_golem_gui.setItem(4,stick);
        iron_golem_gui.setItem(17,barrier);

        player.openInventory(iron_golem_gui);
    }

    public static void sliem (Player player){

        Inventory sliem_gui = Bukkit.createInventory(null,2*9,ChatColor.GREEN.RED+"史莱姆");

        ItemStack potion = new ItemStack(Material.POTION);
        ItemMeta pm = potion.getItemMeta();
        pm.setDisplayName(ChatColor.GREEN+"史莱姆瞬间治疗药水");
        ArrayList<String> pm_lore = new ArrayList<>();
        pm_lore.add("25铁/瓶");
        pm.setLore(pm_lore);
        potion.setItemMeta(pm);

        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta ba = barrier.getItemMeta();
        ba.setDisplayName("返回上一级");
        barrier.setItemMeta(ba);

        sliem_gui.setItem(4,potion);
        sliem_gui.setItem(17,barrier);

        player.openInventory(sliem_gui);
    }
}
