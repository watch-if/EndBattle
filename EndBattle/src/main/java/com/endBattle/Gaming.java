package com.endBattle;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rotatable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EnderDragon;
import org.bukkit.Material;

import java.util.*;

public class Gaming implements Listener, CommandExecutor {

    private static final Set<Material> ALLOWED_BLOCKS = new HashSet<>();

    static {
        ALLOWED_BLOCKS.add(Material.RED_WOOL);
        ALLOWED_BLOCKS.add(Material.YELLOW_WOOL);
        ALLOWED_BLOCKS.add(Material.BLUE_WOOL);
        ALLOWED_BLOCKS.add(Material.GREEN_WOOL);
        ALLOWED_BLOCKS.add(Material.RED_BANNER);
        ALLOWED_BLOCKS.add(Material.BLUE_BANNER);
        ALLOWED_BLOCKS.add(Material.YELLOW_BANNER);
        ALLOWED_BLOCKS.add(Material.GREEN_BANNER);
        ALLOWED_BLOCKS.add(Material.OAK_PLANKS);
        ALLOWED_BLOCKS.add(Material.END_STONE);
        ALLOWED_BLOCKS.add(Material.END_CRYSTAL);
        ALLOWED_BLOCKS.add(Material.OBSIDIAN);
        ALLOWED_BLOCKS.add(Material.FIRE);
        ALLOWED_BLOCKS.add(Material.TNT);
    }

    private Map<String, Team> teams;
    private Map<String, Class> classes;
    private Map<String, String> playerClasses;
    private Map<String, Integer> playerLevels = new HashMap<>();
    private final int maxMembersPerTeam = 4;
    private int finishTeam = 0;
    private int finishClass = 0;
    private Location redTeamHome;
    private Location yellowTeamHome;
    private Location blueTeamHome;
    private Location greenTeamHome;
    private Location redFlag;
    private Location yellowFlag;
    private Location blueFlag;
    private Location greenFlag;
    private boolean TeamIsFinish = false;
    private boolean ClassIsFinish = false;
    private boolean redFlagBroken = false;
    private boolean yellowFlagBroken = false;
    private boolean blueFlagBroken = false;
    private boolean greenFlagBroken = false;
    private Plugin plugin;
    private Spider spider = new Spider();

    public Gaming(){

        teams = new HashMap<>();
        teams.put("red", new Team("Red Team", ChatColor.RED));
        teams.put("yellow", new Team("Yellow Team", ChatColor.YELLOW));
        teams.put("blue", new Team("Blue Team", ChatColor.BLUE));
        teams.put("green", new Team("Green Team", ChatColor.GREEN));

        classes = new HashMap<>();
        playerClasses = new HashMap<>();
        classes.put("zombie", new Class("Zombie", 20));
        classes.put("spider", new Class("Spider", 20));
        classes.put("creeper", new Class("Creeper", 20));
        classes.put("ghast", new Class("Ghast", 20));
        classes.put("witch", new Class("Witch", 20));
        classes.put("iron_golem", new Class("Iron_Golem", 20));
        classes.put("slime", new Class("Slime", 20));
        classes.put("skeleton", new Class("Skeleton", 20));

        redFlag = new Location(Bukkit.getWorld("world_the_end"), 0, 124, 94);
        yellowFlag = new Location(Bukkit.getWorld("world_the_end"), -94, 124, 0);
        blueFlag = new Location(Bukkit.getWorld("world_the_end"), 0, 124, -94);
        greenFlag = new Location(Bukkit.getWorld("world_the_end"), 94, 124, 0);

        plugin = Bukkit.getPluginManager().getPlugin("EndBattle");
        playerLevels = new HashMap<>();

    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("start")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.hasPermission("endbattle.start")) {
                    player.sendMessage(ChatColor.RED + "你没有权限使用此指令！");
                    return false;
                }
            }
            TeamIsFinish = true;
            ClassIsFinish = true;
            startGame();
            Bukkit.broadcastMessage(ChatColor.GOLD + "管理员已手动启动游戏！");
            return true;
        }
        return false;
    }

    @EventHandler
    public void onTeamGUI(PlayerChangedWorldEvent event){
        Player player = event.getPlayer();
        World newWorld = player.getWorld();

        if (newWorld.getName().equals("world_the_end")) {
            Team.TeamGUI(player);
        }
    }

    @EventHandler
    public void clickteam(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if(event.getView().getTitle().equalsIgnoreCase(ChatColor.RED.YELLOW.BLUE.GREEN+"队伍选择")){
            event.setCancelled(true);
            if (event.getRawSlot() > event.getInventory().getSize()) {
                return;
            }
            if (event.getRawSlot() < 0) {
                return;
            }
            if(event.getRawSlot() == 1){
                Team redTeam = teams.get("red");
                if (redTeam.isFull(maxMembersPerTeam)) {
                    player.sendMessage(ChatColor.RED + "红队已满，无法加入！");
                } else {
                    redTeam.addMember(player.getName());
                    player.sendMessage(ChatColor.GREEN + "你已加入红队！");
                    finishTeam = finishTeam + 1;
                    Class.ClassGUI(player);
                }
                return;
            }
            if(event.getRawSlot() == 3){
                Team yellowTeam = teams.get("yellow");
                if (yellowTeam.isFull(maxMembersPerTeam)) {
                    player.sendMessage(ChatColor.RED + "黄队已满，无法加入！");
                } else {
                    yellowTeam.addMember(player.getName());
                    player.sendMessage(ChatColor.GREEN + "你已加入黄队！");
                    finishTeam = finishTeam + 1;
                    Class.ClassGUI(player);
                }
            }
            if(event.getRawSlot() == 5){
                Team blueTeam = teams.get("blue");
                if (blueTeam.isFull(maxMembersPerTeam)) {
                    player.sendMessage(ChatColor.RED + "蓝队已满，无法加入！");
                } else {
                    blueTeam.addMember(player.getName());
                    player.sendMessage(ChatColor.GREEN + "你已加入蓝队！");
                    finishTeam = finishTeam + 1;
                    Class.ClassGUI(player);
                }
            }
            if(event.getRawSlot() == 7){
                Team greenTeam = teams.get("green");
                if (greenTeam.isFull(maxMembersPerTeam)) {
                    player.sendMessage(ChatColor.RED + "绿队已满，无法加入！");
                } else {
                    greenTeam.addMember(player.getName());
                    player.sendMessage(ChatColor.GREEN + "你已加入绿队！");
                    finishTeam = finishTeam + 1;
                    Class.ClassGUI(player);
                }
            }
        }
        if (finishTeam == 16 && finishClass == 16) {
            TeamIsFinish = true;
            ClassIsFinish = true;
            startGame();
        }
    }

    @EventHandler
    public void clickclass(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if(event.getView().getTitle().equalsIgnoreCase(ChatColor.BLACK.WHITE+"职业选择")){
            event.setCancelled(true);
            if (event.getRawSlot() > event.getInventory().getSize()) {
                return;
            }
            if (event.getRawSlot() < 0) {
                return;
            }
            if(event.getRawSlot() == 0){
                playerClasses.put(player.getName(), "zombie");
                player.sendMessage(ChatColor.GREEN + "已选择职业：僵尸");
                finishClass = finishClass + 1;
                player.closeInventory();
            }
            if(event.getRawSlot() == 1){
                playerClasses.put(player.getName(), "spider");
                player.sendMessage(ChatColor.GREEN + "已选择职业：蜘蛛");
                finishClass = finishClass + 1;
                player.closeInventory();
            }
            if(event.getRawSlot() == 2){
                playerClasses.put(player.getName(), "creeper");
                player.sendMessage(ChatColor.GREEN + "已选择职业：苦力怕");
                finishClass = finishClass + 1;
                player.closeInventory();
            }
            if(event.getRawSlot() == 3){
                playerClasses.put(player.getName(), "ghast");
                player.sendMessage(ChatColor.GREEN + "已选择职业：恶魂");
                finishClass = finishClass + 1;
                player.closeInventory();
            }
            if(event.getRawSlot() == 4){
                playerClasses.put(player.getName(), "witch");
                player.sendMessage(ChatColor.GREEN + "已选择职业：女巫");
                finishClass = finishClass + 1;
                player.closeInventory();
            }
            if(event.getRawSlot() == 5){
                playerClasses.put(player.getName(), "iron_golem");
                player.sendMessage(ChatColor.GREEN + "已选择职业：铁傀儡");
                finishClass = finishClass + 1;
                player.closeInventory();
            }
            if(event.getRawSlot() == 6){
                playerClasses.put(player.getName(), "slime");
                player.sendMessage(ChatColor.GREEN + "已选择职业：史莱姆");
                finishClass = finishClass + 1;
                player.closeInventory();
            }
            if(event.getRawSlot() == 7){
                playerClasses.put(player.getName(), "skeleton");
                player.sendMessage(ChatColor.GREEN + "已选择职业：骷髅");
                finishClass = finishClass + 1;
                player.closeInventory();
            }
            if(event.getRawSlot() == 17){
                Team.TeamGUI(player);
                finishTeam = finishTeam - 1;
            }
        }
        if (finishTeam == 16 && finishClass == 16) {
            TeamIsFinish = true;
            ClassIsFinish = true;
            startGame();
        }
    }

    @EventHandler
    public void clickshop(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equalsIgnoreCase(ChatColor.RED + "商店")) {
            event.setCancelled(true);
            if (event.getRawSlot() > event.getInventory().getSize()) {
                return;
            }
            if (event.getRawSlot() < 0) {
                return;
            }
            if (event.getRawSlot() == 2) {
                Player player = (Player) event.getWhoClicked();
                Shop.weaponry(player);
            }
            if (event.getRawSlot() == 4) {
                Player player = (Player) event.getWhoClicked();
                Shop.Structure_block(player);
            }
            if (event.getRawSlot() == 6) {
                Player player = (Player) event.getWhoClicked();
                String profession = playerClasses.get(player.getName());
                if("zombie".equalsIgnoreCase(profession)){
                    Shop.zombie(player);
                }
                if("spider".equalsIgnoreCase(profession)){
                    Shop.spider(player);
                }
                if("creeper".equalsIgnoreCase(profession)){
                    Shop.crepper(player);
                }
                if("ghast".equalsIgnoreCase(profession)){
                    Shop.ghast(player);
                }
                if("witch".equalsIgnoreCase(profession)){
                    Shop.witch(player);
                }
                if("iron_golem".equalsIgnoreCase(profession)){
                    Shop.iron_golem(player);
                }
                if("slime".equalsIgnoreCase(profession)){
                    Shop.sliem(player);
                }
                if("skeleton".equalsIgnoreCase(profession)){
                    Shop.skeleton(player);
                }
            }
        } else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.RED + "武器装备")) {
            event.setCancelled(true);
            if (event.getRawSlot() > event.getInventory().getSize()) {
                return;
            }
            if (event.getRawSlot() < 0) {
                return;
            }
            if (event.getRawSlot() == 1) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 10)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 10));
                    p.getInventory().addItem(new ItemStack(Material.STONE_SWORD));

                } else {

                }
            }
            if (event.getRawSlot() == 2) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.GOLD_INGOT), 7)) {
                    p.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, 7));
                    p.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
                } else {

                }
            }
            if (event.getRawSlot() == 3) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.DIAMOND), 4)) {
                    p.getInventory().removeItem(new ItemStack(Material.DIAMOND, 4));
                    p.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
                } else {

                }
            }
            if (event.getRawSlot() == 10) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 10)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 10));
                    p.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
                } else {

                }
            }
            if (event.getRawSlot() == 11) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 20)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 20));
                    p.getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
                } else {

                }
            }
            if (event.getRawSlot() == 12) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.GOLD_INGOT), 8)) {
                    p.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, 8));
                    p.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
                } else {

                }
            }
            if (event.getRawSlot() == 13) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.GOLD_INGOT), 12)) {
                    p.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, 12));
                    p.getInventory().addItem(new ItemStack(Material.DIAMOND_PICKAXE));
                } else {

                }
            }
            if (event.getRawSlot() == 19) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.GOLD_INGOT), 12)) {
                    p.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, 12));
                    p.getInventory().addItem(new ItemStack(Material.DIAMOND_AXE));
                } else {

                }
            }
            if (event.getRawSlot() == 20) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 30)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 30));
                    p.getInventory().addItem(new ItemStack(Material.SHEARS));
                } else {

                }
            }
            if (event.getRawSlot() == 21) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 10)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 10));
                    p.getInventory().addItem(new ItemStack(Material.BOW));
                }
            }
            if (event.getRawSlot() == 22) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.GOLD_INGOT), 2)) {
                    p.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, 2));
                    p.getInventory().addItem(new ItemStack(Material.ARROW, 8));
                } else {

                }
            }
            if (event.getRawSlot() == 28) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 40)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 40));
                    p.getInventory().setChestplate(null);
                    p.getInventory().setLeggings(null);
                    ItemStack chainmailChestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
                    ItemStack chainmailLeggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
                    p.getInventory().setChestplate(chainmailChestplate);
                    p.getInventory().setLeggings(chainmailLeggings);
                    p.updateInventory();
                } else {

                }
            }
            if (event.getRawSlot() == 29) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.GOLD_INGOT), 12)) {
                    p.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, 12));
                    p.getInventory().setChestplate(null);
                    p.getInventory().setLeggings(null);
                    ItemStack ironChestplate = new ItemStack(Material.IRON_CHESTPLATE);
                    ItemStack ironLeggings = new ItemStack(Material.IRON_LEGGINGS);
                    p.getInventory().setChestplate(ironChestplate);
                    p.getInventory().setLeggings(ironLeggings);
                    p.updateInventory();
                } else {

                }
            }
            if (event.getRawSlot() == 30) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.DIAMOND), 6)) {
                    p.getInventory().removeItem(new ItemStack(Material.DIAMOND, 6));
                    p.getInventory().setChestplate(null);
                    p.getInventory().setLeggings(null);
                    ItemStack diamondChestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
                    ItemStack diamondLeggings = new ItemStack(Material.DIAMOND_LEGGINGS);
                    p.getInventory().setChestplate(diamondChestplate);
                    p.getInventory().setLeggings(diamondLeggings);
                    p.updateInventory();
                }
            }
            if(event.getRawSlot() == 31){
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 40)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 40));
                    p.getInventory().addItem(new ItemStack(Material.SHIELD));
                }
            }
            if(event.getRawSlot() == 44){
                Shop.ShopGUI(p);
            }
        } else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.YELLOW + "建筑方块")) {
            event.setCancelled(true);
            if (event.getRawSlot() > event.getInventory().getSize()) {

            }
            if (event.getRawSlot() < 0) {

            }
            if (event.getRawSlot() == 10) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 4)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 4));
                    if (teams.get("red").getMembers().contains(p.getName())) {
                        p.getInventory().addItem(new ItemStack(Material.RED_WOOL, 16));
                    }
                    if (teams.get("blue").getMembers().contains(p.getName())) {
                        p.getInventory().addItem(new ItemStack(Material.BLUE_WOOL, 16));
                    }
                    if (teams.get("green").getMembers().contains(p.getName())) {
                        p.getInventory().addItem(new ItemStack(Material.GREEN_WOOL, 16));
                    }
                    if (teams.get("yellow").getMembers().contains(p.getName())) {
                        p.getInventory().addItem(new ItemStack(Material.YELLOW_WOOL, 16));
                    }
                } else {

                }
            }
            if (event.getRawSlot() == 11) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.GOLD_INGOT), 4)) {
                    p.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, 4));
                    p.getInventory().addItem(new ItemStack(Material.OAK_PLANKS));
                } else {

                }
            }
            if (event.getRawSlot() == 12) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 24)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 24));
                    p.getInventory().addItem(new ItemStack(Material.END_STONE));
                } else {

                }
            }
            if (event.getRawSlot() == 13) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.DIAMOND), 4)) {
                    p.getInventory().removeItem(new ItemStack(Material.DIAMOND, 4));
                    p.getInventory().addItem(new ItemStack(Material.OBSIDIAN));
                } else {

                }
            }
            if(event.getRawSlot() == 35){
                Shop.ShopGUI(p);
            }
        }else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.RED + "僵尸")) {
            event.setCancelled(true);
            if (event.getRawSlot() > event.getInventory().getSize()) {
                return;
            }
            if (event.getRawSlot() < 0) {
                return;
            }
            if (event.getRawSlot() == 2) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.GOLD_INGOT), 10)) {
                    p.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, 10));
                    p.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                }
            }
            if (event.getRawSlot() == 6) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 20)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 20));
                    ItemStack potion_of_swift = new ItemStack(Material.POTION);
                    PotionMeta pos = (PotionMeta) potion_of_swift.getItemMeta();
                    pos.setDisplayName(ChatColor.RED + "僵尸速度药水");
                    pos.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1), true);
                    potion_of_swift.setItemMeta(pos);
                    p.getInventory().addItem(potion_of_swift);
                }
            }
            if (event.getRawSlot() == 17) {
                Shop.ShopGUI(p);
            }
        } else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.BLACK + "蜘蛛")) {
            event.setCancelled(true);
            if (event.getRawSlot() > event.getInventory().getSize()) {
                return;
            }
            if (event.getRawSlot() < 0) {
                return;
            }
            if (event.getRawSlot() == 2) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 40)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 40));
                    p.getInventory().addItem(new ItemStack(Material.COBWEB));
                }
            }
            if (event.getRawSlot() == 6) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.DIAMOND), 3)) {
                    p.getInventory().removeItem(new ItemStack(Material.DIAMOND, 3));
                    p.getInventory().addItem(createPoisonStick());
                }
            }
            if (event.getRawSlot() == 17) {
                Shop.ShopGUI(p);
            }
        }else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.GREEN + "苦力怕")) {
            event.setCancelled(true);
            if (event.getRawSlot() > event.getInventory().getSize()) {
                return;
            }
            if (event.getRawSlot() < 0) {
                return;
            }
            if (event.getRawSlot() == 2) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.GOLD_INGOT), 5)) {
                    p.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, 5));
                    p.getInventory().addItem(new ItemStack(Material.TNT));
                }
            }
            if (event.getRawSlot() == 6) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.GOLD_INGOT), 5)) {
                    p.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, 5));
                    ItemStack snowball = new ItemStack(Material.SNOWBALL);
                    ItemMeta snow = snowball.getItemMeta();
                    snow.setDisplayName(ChatColor.WHITE.RED + "爆炸雪球");
                    snowball.setItemMeta(snow);
                    p.getInventory().addItem(snowball);
                }
            }
            if (event.getRawSlot() == 17) {
                Shop.ShopGUI(p);
            }
        } else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE.LIGHT_PURPLE + "恶魂")) {
            event.setCancelled(true);
            if (event.getRawSlot() > event.getInventory().getSize()) {
                return;
            }
            if (event.getRawSlot() < 0) {
                return;
            }
            if (event.getRawSlot() == 2) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.GOLD_INGOT), 4)) {
                    p.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, 4));
                    p.getInventory().addItem(new ItemStack(Material.FIRE_CHARGE));
                }
            }
            if (event.getRawSlot() == 6) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 20)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 20));
                    ItemStack potion_of_leaping = new ItemStack(Material.POTION);
                    PotionMeta pol = (PotionMeta) potion_of_leaping.getItemMeta();
                    pol.setDisplayName(ChatColor.WHITE + "恶魂跳跃药水");
                    pol.addCustomEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 40, 5), true);
                    potion_of_leaping.setItemMeta(pol);
                    p.getInventory().addItem(potion_of_leaping);
                }
            }
            if (event.getRawSlot() == 17) {
                Shop.ShopGUI(p);
            }
        } else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.AQUA + "女巫")) {
            event.setCancelled(true);
            if (event.getRawSlot() > event.getInventory().getSize()) {
                return;
            }
            if (event.getRawSlot() < 0) {
                return;
            }
            if (event.getRawSlot() == 0) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 20)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 20));
                    p.getInventory().addItem(new ItemStack(Material.GLASS_BOTTLE));
                }
            }
            if (event.getRawSlot() == 1) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 30)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 30));
                    p.getInventory().addItem(new ItemStack(Material.POTION));
                }
            }
            if (event.getRawSlot() == 2) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 40)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 40));
                    p.getInventory().addItem(new ItemStack(Material.FERMENTED_SPIDER_EYE));
                }
            }
            if (event.getRawSlot() == 3) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 30)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 30));
                    p.getInventory().addItem(new ItemStack(Material.NETHER_WART));
                }
            }
            if (event.getRawSlot() == 4) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.GOLD_INGOT), 10)) {
                    p.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, 10));
                    p.getInventory().addItem(new ItemStack(Material.GUNPOWDER));
                }
            }
            if (event.getRawSlot() == 5) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 30)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 30));
                    p.getInventory().addItem(new ItemStack(Material.SPIDER_EYE));
                }
            }
            if (event.getRawSlot() == 6) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 40)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 40));
                    p.getInventory().addItem(new ItemStack(Material.GHAST_TEAR));
                }
            }
            if (event.getRawSlot() == 7) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 40)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 40));
                    p.getInventory().addItem(new ItemStack(Material.RABBIT_FOOT));
                }
            }
            if (event.getRawSlot() == 8) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 40)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 40));
                    p.getInventory().addItem(new ItemStack(Material.BLAZE_POWDER));
                }
            }
            if (event.getRawSlot() == 9) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 40)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 40));
                    p.getInventory().addItem(new ItemStack(Material.GLISTERING_MELON_SLICE));
                }
            }
            if (event.getRawSlot() == 10) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 40)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 40));
                    p.getInventory().addItem(new ItemStack(Material.SUGAR));
                }
            }
            if (event.getRawSlot() == 11) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 40)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 40));
                    p.getInventory().addItem(new ItemStack(Material.MAGMA_CREAM));
                }
            }
            if (event.getRawSlot() == 12) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 30)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 30));
                    p.getInventory().addItem(new ItemStack(Material.REDSTONE));
                }
            }
            if (event.getRawSlot() == 13) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 30)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 30));
                    p.getInventory().addItem(new ItemStack(Material.GLOWSTONE_DUST));
                }
            }
            if (event.getRawSlot() == 14) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 40)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 40));
                    p.getInventory().addItem(new ItemStack(Material.PUFFERFISH));
                }
            }
            if (event.getRawSlot() == 15) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 40)) ;
                p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 40));
                p.getInventory().addItem(new ItemStack(Material.GOLDEN_CARROT));
            }
            if (event.getRawSlot() == 16) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.GOLD_INGOT), 10)) {
                    p.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, 10));
                    p.getInventory().addItem(new ItemStack(Material.TURTLE_HELMET));
                }
            }
            if (event.getRawSlot() == 17) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 40)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 40));
                    p.getInventory().addItem(new ItemStack(Material.PHANTOM_MEMBRANE));
                }
            }
            if (event.getRawSlot() == 26) {
                Shop.ShopGUI(p);
            }
        } else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "骷髅")) {
            event.setCancelled(true);
            if (event.getRawSlot() > event.getInventory().getSize()) {
                return;
            }
            if (event.getRawSlot() < 0) {
                return;
            }
            if (event.getRawSlot() == 2) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.DIAMOND), 1)) {
                    p.getInventory().removeItem(new ItemStack(Material.DIAMOND, 1));
                    p.getInventory().addItem(new ItemStack(Material.FIREWORK_ROCKET));
                }
            }
            if (event.getRawSlot() == 6) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 40)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 40));
                    ItemStack harming_arrow = new ItemStack(Material.TIPPED_ARROW);
                    PotionMeta hm = (PotionMeta) harming_arrow.getItemMeta();
                    hm.setDisplayName(ChatColor.RED + "伤害之箭");
                    hm.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 2), true);
                    harming_arrow.setItemMeta(hm);
                    p.getInventory().addItem(harming_arrow);
                }
            }
            if (event.getRawSlot() == 17) {
                Shop.ShopGUI(p);
            }
        } else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE.GREEN + "铁傀儡")) {
            event.setCancelled(true);
            if (event.getRawSlot() > event.getInventory().getSize()) {
                return;
            }
            if (event.getRawSlot() < 0) {
                return;
            }
            if (event.getRawSlot() == 4) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.GOLD_INGOT), 10)) {
                    p.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, 10));
                    ItemStack stick = new ItemStack(Material.STICK);
                    ItemMeta st = stick.getItemMeta();
                    st.setDisplayName(ChatColor.WHITE + "击退木棒");
                    st.addEnchant(Enchantment.KNOCKBACK, 3, true);
                    stick.setItemMeta(st);
                    p.getInventory().addItem(stick);
                }
            }
            if (event.getRawSlot() == 17) {
                Shop.ShopGUI(p);
            }
        } else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.GREEN.RED + "史莱姆")) {
            event.setCancelled(true);
            if (event.getRawSlot() > event.getInventory().getSize()) {
                return;
            }
            if (event.getRawSlot() < 0) {
                return;
            }
            if (event.getRawSlot() == 4) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 25)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 25));
                    ItemStack potion = new ItemStack(Material.SPLASH_POTION);
                    PotionMeta pm = (PotionMeta) potion.getItemMeta();
                    pm.setDisplayName(ChatColor.GREEN + "史莱姆瞬间治疗药水");
                    pm.addCustomEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 1, 1), true);
                    potion.setItemMeta(pm);
                    p.getInventory().addItem(potion);
                }
            }
            if (event.getRawSlot() == 17) {
                Shop.ShopGUI(p);
            }
        }
    }

    private void equipPlayer(Player player, Color color) {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);

        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
        LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
        LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();

        helmetMeta.setColor(color);
        chestplateMeta.setColor(color);
        leggingsMeta.setColor(color);

        helmet.setItemMeta(helmetMeta);
        chestplate.setItemMeta(chestplateMeta);
        leggings.setItemMeta(leggingsMeta);

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
    }

    private void startGame() {
        if (TeamIsFinish && ClassIsFinish) {
            redTeamHome = new Location(Bukkit.getWorld("world_the_end"), -6, 125, 110);
            yellowTeamHome = new Location(Bukkit.getWorld("world_the_end"), -108, 125, -3);
            blueTeamHome = new Location(Bukkit.getWorld("world_the_end"), 4, 125, -110);
            greenTeamHome = new Location(Bukkit.getWorld("world_the_end"), 108, 125, 3);

            World endWorld = Bukkit.getWorld("world_the_end");
            if (endWorld != null) {
                endWorld.setSpawnLocation(0, 125, 0);
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage("游戏开始！");

                player.getInventory().clear();
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);

                if (teams.get("red").getMembers().contains(player.getName())) {
                    player.setGameMode(GameMode.SURVIVAL);
                    equipPlayer(player, Color.RED);
                    player.teleport(redTeamHome);
                } else if (teams.get("yellow").getMembers().contains(player.getName())) {
                    player.setGameMode(GameMode.SURVIVAL);
                    equipPlayer(player, Color.YELLOW);
                    player.teleport(yellowTeamHome);
                } else if (teams.get("blue").getMembers().contains(player.getName())) {
                    player.setGameMode(GameMode.SURVIVAL);
                    equipPlayer(player, Color.BLUE);
                    player.teleport(blueTeamHome);
                } else if (teams.get("green").getMembers().contains(player.getName())) {
                    player.setGameMode(GameMode.SURVIVAL);
                    equipPlayer(player, Color.GREEN);
                    player.teleport(greenTeamHome);
                }
                String className = playerClasses.get(player.getName()); //bug无法将玩家传送并且给予玩家盔甲
            }
            startItemIronTask();
            startItemDiamondTask();
            startItemGoldTask();
            startItemLapis_LazuliTask();
            Plugin plugin = Bukkit.getPluginManager().getPlugin("EndBattle");
            if (plugin != null) {
                Bukkit.getPluginManager().registerEvents(this, plugin);
                Bukkit.getScheduler().runTaskLater(plugin, this::spawnEnderDragon, 20L * 60L * 10L);
            }
        }
    }

    private void startItemDiamondTask() {
        Bukkit.getScheduler().runTaskTimer(Bukkit.getPluginManager().getPlugin("EndBattle"), this::DiamondItems,0L,20L*30L);
    }

    private void startItemLapis_LazuliTask() {
        Bukkit.getScheduler().runTaskTimer(Bukkit.getPluginManager().getPlugin("EndBattle"), this::Lapis_LazuliItems, 0L, 20L * 60L);
    }

    private void startItemGoldTask(){
        Bukkit.getScheduler().runTaskTimer(Bukkit.getPluginManager().getPlugin("EndBattle"), this::GoldItems,0L,20L*10L);
    }

    private void startItemIronTask() {
        Bukkit.getScheduler().runTaskTimer(Bukkit.getPluginManager().getPlugin("EndBattle"), this::IronItems,0L,20L*3L);
    }

    private void IronItems() {
        Location IronLocation1 = new Location(Bukkit.getWorld("world_the_end"), 0,125,109);
        Location IronLocation2 = new Location(Bukkit.getWorld("world_the_end"), -1,125,109);
        Location IronLocation3 = new Location(Bukkit.getWorld("world_the_end"), 1,125,109);
        Location IronLocation4 = new Location(Bukkit.getWorld("world_the_end"), 0,125,-113);
        Location IronLocation5 = new Location(Bukkit.getWorld("world_the_end"), -1,125,-113);
        Location IronLocation6 = new Location(Bukkit.getWorld("world_the_end"), 1,125,-113);
        Location IronLocation7 = new Location(Bukkit.getWorld("world_the_end"), -113,125,0);
        Location IronLocation8 = new Location(Bukkit.getWorld("world_the_end"), -113,125,1);
        Location IronLocation9 = new Location(Bukkit.getWorld("world_the_end"), -113,125,-1);
        Location IronLocation10 = new Location(Bukkit.getWorld("world_the_end"), 113,125,0);
        Location IronLocation11 = new Location(Bukkit.getWorld("world_the_end"), 113,125,1);
        Location IronLocation12 = new Location(Bukkit.getWorld("world_the_end"), 113,125,-1);

        ItemStack itemTOiron1 = getRandomItem(Math.random());
        ItemStack itemTOiron2 = getRandomItem(Math.random());
        ItemStack itemTOiron3 = getRandomItem(Math.random());
        ItemStack itemTOiron4 = getRandomItem(Math.random());

        IronLocation1.getWorld().dropItemNaturally(IronLocation1,itemTOiron1);
        IronLocation2.getWorld().dropItemNaturally(IronLocation2,itemTOiron1);
        IronLocation3.getWorld().dropItemNaturally(IronLocation3,itemTOiron1);
        IronLocation4.getWorld().dropItemNaturally(IronLocation4,itemTOiron2);
        IronLocation5.getWorld().dropItemNaturally(IronLocation5,itemTOiron2);
        IronLocation6.getWorld().dropItemNaturally(IronLocation6,itemTOiron2);
        IronLocation7.getWorld().dropItemNaturally(IronLocation7,itemTOiron3);
        IronLocation8.getWorld().dropItemNaturally(IronLocation8,itemTOiron3);
        IronLocation9.getWorld().dropItemNaturally(IronLocation9,itemTOiron3);
        IronLocation10.getWorld().dropItemNaturally(IronLocation10,itemTOiron4);
        IronLocation11.getWorld().dropItemNaturally(IronLocation11, itemTOiron4);
        IronLocation12.getWorld().dropItemNaturally(IronLocation12, itemTOiron4);
    }

    private void DiamondItems() {
        Location DiamondLocation1 = new Location(Bukkit.getWorld("world_the_end"), -73,130,-73);
        Location DiamondLocation2 = new Location(Bukkit.getWorld("world_the_end"), 74,130,-73);
        Location DiamondLocation3 = new Location(Bukkit.getWorld("world_the_end"), 74,130,74);
        Location DiamondLocation4 = new Location(Bukkit.getWorld("world_the_end"), -73,130,74);

        ItemStack itemTODiamond1 = getRandomDiamondItem(Math.random());
        ItemStack itemTODiamond2 = getRandomDiamondItem(Math.random());
        ItemStack itemTODiamond3 = getRandomDiamondItem(Math.random());
        ItemStack itemTODiamond4 = getRandomDiamondItem(Math.random());

        DiamondLocation1.getWorld().dropItemNaturally(DiamondLocation1, itemTODiamond1);
        DiamondLocation2.getWorld().dropItemNaturally(DiamondLocation2, itemTODiamond2);
        DiamondLocation3.getWorld().dropItemNaturally(DiamondLocation3, itemTODiamond3);
        DiamondLocation4.getWorld().dropItemNaturally(DiamondLocation4, itemTODiamond4);
    }

    private void GoldItems() {
        Location GoldLocation1 = new Location(Bukkit.getWorld("world_the_end"), -55,124,0);
        Location GoldLocation2 = new Location(Bukkit.getWorld("world_the_end"), 55,124,0);
        Location GoldLocation3 = new Location(Bukkit.getWorld("world_the_end"), 0,124,55);
        Location GoldLocation4 = new Location(Bukkit.getWorld("world_the_end"), 0,124,-55);

        ItemStack itemTOGold1 = getRandomGoldItem(Math.random());
        ItemStack itemToGold2 = getRandomGoldItem(Math.random());
        ItemStack itemTOGold3 = getRandomGoldItem(Math.random());
        ItemStack itemToGold4 = getRandomGoldItem(Math.random());

        GoldLocation1.getWorld().dropItemNaturally(GoldLocation1, itemTOGold1);
        GoldLocation2.getWorld().dropItemNaturally(GoldLocation2, itemToGold2);
        GoldLocation3.getWorld().dropItemNaturally(GoldLocation3, itemTOGold3);
        GoldLocation4.getWorld().dropItemNaturally(GoldLocation4, itemToGold4);
    }

    private void Lapis_LazuliItems() {
        Location Lapis_LazuliLocation = new Location(Bukkit.getWorld("world_the_end"), 0, 125, 0);
        double lapis_lazuli = Math.random();

        ItemStack itemToLapis_Lazu;

        if (lapis_lazuli < 0.7) {
            itemToLapis_Lazu = new ItemStack(Material.LAPIS_LAZULI, 1);
        } else if (lapis_lazuli < 0.9) {
            itemToLapis_Lazu = new ItemStack(Material.DIAMOND, 1);
        } else {
            itemToLapis_Lazu = new ItemStack(Material.EXPERIENCE_BOTTLE, 1);
        }

        Lapis_LazuliLocation.getWorld().dropItemNaturally(Lapis_LazuliLocation, itemToLapis_Lazu);
    }

    private ItemStack getRandomItem(double random) {
        if(random < 0.9){
            return new ItemStack(Material.IRON_INGOT,1);
        }else if(random < 0.96){
            return new ItemStack(Material.GOLD_INGOT,1);
        }else if(random < 0.99){
            return new ItemStack(Material.LAPIS_LAZULI,1);
        }else {
            return new ItemStack(Material.DIAMOND,1);
        }
    }

    private ItemStack getRandomDiamondItem(double random) {
        if(random < 0.8){
            return new ItemStack(Material.DIAMOND, 1);
        }else if(random < 0.9){
            return new ItemStack(Material.GOLD_INGOT, 1);
        }else if(random < 0.92){
            return new ItemStack(Material.IRON_INGOT, 1);
        }else if(random < 0.95){
            return new ItemStack(Material.LAPIS_LAZULI, 1);
        }else {
            return new ItemStack(Material.EXPERIENCE_BOTTLE, 1);
        }
    }

    private ItemStack getRandomGoldItem(double random) {
        if(random < 0.85){
            return new ItemStack(Material.GOLD_INGOT,1);
        }else if(random < 0.95){
            return new ItemStack(Material.IRON_INGOT,1);
        }else if(random < 0.98){
            return new ItemStack(Material.LAPIS_LAZULI,1);
        }else {
            return new ItemStack(Material.DIAMOND,1);
        }
    }

    private void spawnEnderDragon() {
        World endWorld = Bukkit.getWorld("world_the_end");
        if (endWorld != null) {
            Location[] crystalLocations = {
                    new Location(endWorld, 14, 139, -12),
                    new Location(endWorld, 14, 139, 13),
                    new Location(endWorld, -12, 139, 14),
                    new Location(endWorld, -12, 139, -13)
            };

            for (Location loc : crystalLocations) {
                Location baseLocation = loc.clone().subtract(0, 1, 0);
                baseLocation.getBlock().setType(Material.OBSIDIAN);
                EnderCrystal crystal = (EnderCrystal) endWorld.spawnEntity(loc, EntityType.END_CRYSTAL);
                crystal.setShowingBottom(true);
                crystal.setInvulnerable(false);
            }

            Location dragonSpawnLocation = new Location(endWorld, 0, 128, 0);
            EnderDragon dragon = (EnderDragon) endWorld.spawnEntity(dragonSpawnLocation, EntityType.ENDER_DRAGON);

            dragon.setPhase(EnderDragon.Phase.CIRCLING);
            dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(200.0);
            dragon.setHealth(200.0);

            endWorld.playEffect(dragonSpawnLocation, Effect.ENDER_SIGNAL, 0);
            endWorld.playSound(dragonSpawnLocation, Sound.ENTITY_ENDER_DRAGON_GROWL, 5.0F, 1.0F);

            Bukkit.broadcastMessage(ChatColor.DARK_RED + "末影龙已复活！");
        }
    }

    @EventHandler
    public void onEnderDragonDeath(EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.ENDER_DRAGON) {
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("EndBattle"), this::spawnEnderDragon, 20L * 60L * 5L);
        }
    }

    @EventHandler
    public void onRedFlagBreak(BlockBreakEvent event) {
        Location blockLocation = event.getBlock().getLocation();
        if (blockLocation.equals(redFlag) && event.getBlock().getState() instanceof Banner) {
            redFlagBroken = true;
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(ChatColor.RED + "红队旗帜被破坏！");
            }
        }
    }

    @EventHandler
    public void onYellowFlagBreak(BlockBreakEvent event) {
        Location blockLocation = event.getBlock().getLocation();
        if (blockLocation.equals(yellowFlag) && event.getBlock().getState() instanceof Banner) {
            yellowFlagBroken = true;
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(ChatColor.YELLOW + "黄队旗帜被破坏");
            }
        }
    }

    @EventHandler
    public void onBlueFlagBreak(BlockBreakEvent event) {
        Location blockLocation = event.getBlock().getLocation();
        if (blockLocation.equals(blueFlag) && event.getBlock().getState() instanceof Banner) {
            blueFlagBroken = true;
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(ChatColor.BLUE + "蓝队旗帜被破坏");
            }
        }
    }

    @EventHandler
    public void onGreenFlagBreak(BlockBreakEvent event) {
        Location blockLocation = event.getBlock().getLocation();
        if (blockLocation.equals(greenFlag) && event.getBlock().getState() instanceof Banner) {
            greenFlagBroken = true;
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(ChatColor.GREEN + "绿队旗帜被破坏");
            }
        }
    }

    private void checkGameEnd() {
        int aliveTeams = 0;
        String winningTeam = null;
        if (!teams.get("red").getMembers().isEmpty()) {
            aliveTeams++;
            winningTeam = "red";
        }
        if (!teams.get("yellow").getMembers().isEmpty()) {
            aliveTeams++;
            if (winningTeam!= null) {
                winningTeam = null;
            } else {
                winningTeam = "yellow";
            }
        }
        if (!teams.get("blue").getMembers().isEmpty()) {
            aliveTeams++;
            if (winningTeam!= null) {
                winningTeam = null;
            } else {
                winningTeam = "blue";
            }
        }
        if (!teams.get("green").getMembers().isEmpty()) {
            aliveTeams++;
            if (winningTeam!= null) {
                winningTeam = null;
            } else {
                winningTeam = "green";
            }
        }
        if (aliveTeams == 1 && winningTeam!= null) {
            endGame(winningTeam);
        }
    }

    private void cancelAllTasks() {
        if (plugin != null) {
            Bukkit.getScheduler().cancelTasks(plugin);
        }
    }

    private void endGame(String winningTeam) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(ChatColor.GOLD + "游戏结束，" + teams.get(winningTeam).getColor() + winningTeam + "队获胜！");
            player.setGameMode(GameMode.SPECTATOR);
        }
        World endWorld = Bukkit.getWorld("world_the_end");
        if (endWorld != null) {
            for (Entity entity : endWorld.getEntities()) {
                if (entity.getType() == EntityType.ENDER_DRAGON) {
                    entity.remove();
                    break;
                }
            }
        }
        if (endWorld != null) {
            for (Entity entity : endWorld.getEntities()) {
                if (entity.getType() == EntityType.ITEM) {
                    entity.remove();
                }
            }
            clearAllowedBlocks(endWorld);
            placeTeamFlags(endWorld);
        }
        cancelAllTasks();

        World mainWorld = Bukkit.getWorld("world");
        if (mainWorld != null) {
            mainWorld.setSpawnLocation(0, 0, 0);

            Location spawnLocation = new Location(mainWorld, 0, 0, 0);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.teleport(spawnLocation);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location respawnLocation = new Location(Bukkit.getWorld("world_the_end"), 0, 125, 0);
        String className = playerClasses.get(player.getName());
        if ("creeper".equalsIgnoreCase(className)) {
            Creeper.spawnTNT(event.getEntity().getLocation());
        }
        if (teams.get("red").getMembers().contains(player.getName()) && redFlagBroken) {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(respawnLocation);
        } else if (teams.get("yellow").getMembers().contains(player.getName()) && yellowFlagBroken) {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(respawnLocation);
        } else if (teams.get("blue").getMembers().contains(player.getName()) && blueFlagBroken) {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(respawnLocation);
        } else if (teams.get("green").getMembers().contains(player.getName()) && greenFlagBroken) {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(respawnLocation);
        } else {
            Location newrespawnLocation = new Location(Bukkit.getWorld("world_the_end"), 0, 125, 0);
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(newrespawnLocation);
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("EndBattle"), () -> {
                if (teams.get("red").getMembers().contains(player.getName()) && !redFlagBroken) {
                    player.teleport(redTeamHome);
                    player.setGameMode(GameMode.SURVIVAL);
                } else if (teams.get("yellow").getMembers().contains(player.getName()) && !yellowFlagBroken) {
                    player.teleport(yellowTeamHome);
                    player.setGameMode(GameMode.SURVIVAL);
                } else if (teams.get("blue").getMembers().contains(player.getName()) && !blueFlagBroken) {
                    player.teleport(blueTeamHome);
                    player.setGameMode(GameMode.SURVIVAL);
                } else if (teams.get("green").getMembers().contains(player.getName()) && !greenFlagBroken) {
                    player.teleport(greenTeamHome);
                    player.setGameMode(GameMode.SURVIVAL);
                }
            }, 100L);
        }
        checkGameEnd();
    }

    private void clearAllowedBlocks(World world) {
        if (world != null) {
            for (Chunk chunk : world.getLoadedChunks()) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < world.getMaxHeight(); y++) {
                            Block block = chunk.getBlock(x, y, z);
                            if (ALLOWED_BLOCKS.contains(block.getType())) {
                                block.setType(Material.AIR);
                            }
                        }
                    }
                }
            }
        }
    }

    private void placeTeamFlags(World world) {
        if (world != null) {
            placeFlag(world, redFlag, DyeColor.RED);
            placeFlag(world, yellowFlag, DyeColor.YELLOW);
            placeFlag(world, blueFlag, DyeColor.BLUE);
            placeFlag(world, greenFlag, DyeColor.GREEN);
        }
    }

    private void placeFlag(World world, Location location, DyeColor color) {
        if (world != null && location != null) {
            Material bannerType = BANNER_MATERIALS.get(color);
            if (bannerType != null) {
                Block block = world.getBlockAt(location);
                block.setType(bannerType);
                Banner banner = (Banner) block.getState();
                banner.setBaseColor(color);

                double dx = 0 - location.getX();
                double dz = 0 - location.getZ();
                float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));

                Rotatable rotatable = (Rotatable) block.getBlockData();
                rotatable.setRotation(getClosestBlockFace(yaw));
                block.setBlockData(rotatable);

                banner.update();
            }
        }
    }

    private BlockFace getClosestBlockFace(float yaw) {
        yaw = (yaw % 360 + 360) % 360;

        if (yaw < 22.5 || yaw >= 337.5) return BlockFace.NORTH;
        if (yaw < 67.5) return BlockFace.NORTH_EAST;
        if (yaw < 112.5) return BlockFace.EAST;
        if (yaw < 157.5) return BlockFace.SOUTH_EAST;
        if (yaw < 202.5) return BlockFace.SOUTH;
        if (yaw < 247.5) return BlockFace.SOUTH_WEST;
        if (yaw < 292.5) return BlockFace.WEST;
        if (yaw < 337.5) return BlockFace.NORTH_WEST;
        return BlockFace.NORTH;
    }

    private static final Map<DyeColor, Material> BANNER_MATERIALS = new HashMap<>();

    static {
        BANNER_MATERIALS.put(DyeColor.RED, Material.RED_BANNER);
        BANNER_MATERIALS.put(DyeColor.YELLOW, Material.YELLOW_BANNER);
        BANNER_MATERIALS.put(DyeColor.BLUE, Material.BLUE_BANNER);
        BANNER_MATERIALS.put(DyeColor.GREEN, Material.GREEN_BANNER);
    }

    private ItemStack createPoisonStick() {
        ItemStack stick = new ItemStack(Material.STICK);
        ItemMeta meta = stick.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "剧毒木棒");
        stick.setItemMeta(meta);
        return stick;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand != null && itemInHand.getType() == Material.STICK) {
                ItemMeta meta = itemInHand.getItemMeta();
                if (meta != null && meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.GREEN + "剧毒木棒")) {
                    if (event.getEntity() instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) event.getEntity();
                        if (livingEntity instanceof Villager) {
                            event.setCancelled(true);
                        } else {
                            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 5, 0));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        String className = playerClasses.get(player.getName());
        if ("zombie".equalsIgnoreCase(className)) {
            Zombie zombie = new Zombie();
            zombie.placeBlockUnderPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        String className = playerClasses.get(player.getName());

        if (className == null) {
            return;
        }

        int expAdded = event.getAmount();
        int currentLevel = player.getLevel();
        int newLevel = currentLevel + expAdded;

        int levelUpCount = newLevel - currentLevel;

        int currentHealthBoost = playerLevels.getOrDefault(player.getName(), 0);

        int maxHealthBoost = "iron_golem".equalsIgnoreCase(className) ? 30 : 15;
        int healthBoostToAdd = 0;

        for (int i = 0; i < levelUpCount; i++) {
            if (currentHealthBoost < maxHealthBoost) {
                healthBoostToAdd += "iron_golem".equalsIgnoreCase(className) ? 2 : 1;
                currentHealthBoost += "iron_golem".equalsIgnoreCase(className) ? 2 : 1;
            }
        }

        increaseHealth(player, healthBoostToAdd);

        player.setLevel(newLevel);

        playerLevels.put(player.getName(), currentHealthBoost);
    }

    private void increaseHealth(Player player, int amount) {
        AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(maxHealth.getBaseValue() + amount);
            player.setHealth(maxHealth.getBaseValue());
        }
    }


}
