package com.endBattle.game.Gaming;

import com.endBattle.EndBattle;
import com.endBattle.game.Teams.GetTeamGUI;
import com.endBattle.game.Teams.TeamManager;
import com.endBattle.game.classes.GetClassGUI;
import com.endBattle.game.mainshop.Shop;
import com.endBattle.game.shop.ShopGui;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.boss.BarStyle;
import org.bukkit.scoreboard.*;

import java.util.*;

public class Gaming implements Listener, CommandExecutor {

    //声明允许破坏方块白名单
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

    // 储存大厅方块数据
    private Map<Location, BlockData> HallBlocks = new HashMap<>();
    // 存储每个玩家的Boss Bar
    private Map<Player, BossBar> dragonBossBars = new HashMap<>();
    // 声明一个 HashMap 来存储每个玩家点击前的 finishTeam 和 finishClass 数值
    private Map<String, Integer> playerFinishTeam = new HashMap<>();
    private Map<String, Integer> playerFinishClass = new HashMap<>();
    //储存玩家队伍
    public Map<String, TeamManager> teams;
    //储存玩家等级
    private Map<String, Integer> playerLevels = new HashMap<>();
    //储存玩家职业
    Map<String, String> playerClasses = new HashMap<>();
    //储存初始方块数据
    private Map<Location, BlockData> initialBlocks = new HashMap<>();
    // 储存玩家装备
    private Map<String, ItemStack[]> playerEquipment = new HashMap<>();
    //判定加入队伍时最大人数
    private final int maxMembersPerTeam = 4;
    //判定开始游戏前玩家选择队伍人数
    private int finishTeam = 0;
    //判定开始游戏前玩家选择职业人数
    private int finishClass = 0;
    //每个队伍人数
    public int redplayer = 0;
    public int yellowplayer = 0;
    public int blueplayer = 0;
    public int greenplayer = 0;
    //储存每个队伍家的位置
    private Location redTeamHome;
    private Location yellowTeamHome;
    private Location blueTeamHome;
    private Location greenTeamHome;
    //储存每个队伍复活锚点位置
    private Location redFlag;
    private Location yellowFlag;
    private Location blueFlag;
    private Location greenFlag;
    //开始游戏判定数据，当finishTeam与finishClass都为16时变成true
    public boolean TeamIsFinish = false;
    public boolean ClassIsFinish = false;
    //判定每个队伍复活锚点是否被破坏
    private boolean redFlagBroken = false;
    private boolean yellowFlagBroken = false;
    private boolean blueFlagBroken = false;
    private boolean greenFlagBroken = false;
    //选择队伍界面打开
    private boolean opengui = true;
    private Plugin plugin;

    // 声明一个 Scoreboard 对象
    private Scoreboard scoreboard;
    // 声明一个 Objective 对象
    private Objective objective;
    // 声明并初始化 playerKills
    private Map<String, Integer> playerKills = new HashMap<>();
    
    //结束游戏判断
    private boolean gameEnded = false;



    //给定一个框选区域方法
    private void hall(Location corner1, Location corner2){
        World world = corner1.getWorld();
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        for(int x = minX; x <= maxX; x++){
            for(int y = minY; y <= maxY; y++){
                for(int z = minZ; z <= maxZ; z++){
                    Location loc = new Location(world, x, y, z);
                    Block block = loc.getBlock();
                    HallBlocks.put(loc, block.getBlockData());
                    block.setType(Material.AIR);
                }
            }
        }
    }

    public Gaming(){

        //创建队伍
        teams = new HashMap<>();
        teams.put("red", new TeamManager("Red Team", ChatColor.RED));
        teams.put("yellow", new TeamManager("Yellow Team", ChatColor.YELLOW));
        teams.put("blue", new TeamManager("Blue Team", ChatColor.BLUE));
        teams.put("green", new TeamManager("Green Team", ChatColor.GREEN));

        //创建每个队伍的家的位置
        redTeamHome = new Location(Bukkit.getWorld("world_the_end"), -6, 125, 110);
        yellowTeamHome = new Location(Bukkit.getWorld("world_the_end"), -108, 125, -3);
        blueTeamHome = new Location(Bukkit.getWorld("world_the_end"), 4, 125, -110);
        greenTeamHome = new Location(Bukkit.getWorld("world_the_end"), 108, 125, 3);

        //创建每个队伍的复活锚点位置
        redFlag = new Location(Bukkit.getWorld("world_the_end"), 0, 124, 94);
        yellowFlag = new Location(Bukkit.getWorld("world_the_end"), -94, 124, 0);
        blueFlag = new Location(Bukkit.getWorld("world_the_end"), 0, 124, -94);
        greenFlag = new Location(Bukkit.getWorld("world_the_end"), 94, 124, 0);

        plugin = Bukkit.getPluginManager().getPlugin("EndBattle");
        playerLevels = new HashMap<>();

        // 注册事件监听器
        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    //创建管理员开始游戏、结束游戏方式
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //游戏开始指令处理
        if (command.getName().equalsIgnoreCase("start")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.hasPermission("endbattle.start")) {
                    player.sendMessage(ChatColor.RED + "你没有权限使用此指令！");
                    return false;
                }
            }
            //强行改true开始游戏
            TeamIsFinish = true;
            ClassIsFinish = true;
            opengui = false;
            startGame();
            Bukkit.broadcastMessage(ChatColor.GOLD + "管理员已手动启动游戏！");
            return true;
        }

        //游戏结束指令处理
        if(command.getName().equalsIgnoreCase("end")){
            if(sender instanceof Player){
                Player player = (Player) sender;
                if(!player.hasPermission("endbattle.end")){
                    player.sendMessage(ChatColor.RED + "你没有权限使用此指令！");
                    return false;
                }
            }
            gameEnded = true;
            endGame();
            Bukkit.broadcastMessage(ChatColor.GOLD + "管理员已手动结束游戏！");
            return true;
        }
        return false;
    }

    //玩家进入末地后打开队伍选择界面
    @EventHandler
    public void onTeamGUI(PlayerChangedWorldEvent event){
        Player player = event.getPlayer();
        World newWorld = player.getWorld();

        if (newWorld.getName().equals("world_the_end") && opengui) {
            GetTeamGUI.TeamGUI(player);
        }
    }

    //判定玩家选择队伍
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
                // 保存玩家点击前的 finishTeam 数值
                playerFinishTeam.put(player.getName(), finishTeam);

                TeamManager redTeam = teams.get("red");
                if (redTeam.isFull(maxMembersPerTeam)) {
                    player.sendMessage(ChatColor.RED + "红队已满，无法加入！");
                } else {
                    redTeam.addMember(player.getName());
                    player.sendMessage(ChatColor.GREEN + "你已加入红队！");
                    //玩家成功选择队伍后判定人数+1并传送、给予盔甲，跳转到职业选择界面
                    finishTeam = finishTeam + 1;
                    redplayer = redplayer + 1;
                    player.teleport(redTeamHome);
                    // 创建红色皮革头盔
                    ItemStack Rhelmet = new ItemStack(Material.LEATHER_HELMET);
                    LeatherArmorMeta helmetMeta = (LeatherArmorMeta) Rhelmet.getItemMeta();
                    helmetMeta.setColor(Color.RED);
                    Rhelmet.setItemMeta(helmetMeta);

                    // 创建红色皮革胸甲
                    ItemStack Rchestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
                    LeatherArmorMeta chestMeta = (LeatherArmorMeta) Rchestplate.getItemMeta();
                    chestMeta.setColor(Color.RED);
                    Rchestplate.setItemMeta(chestMeta);

                    // 创建红色皮革护腿
                    ItemStack Rleggings = new ItemStack(Material.LEATHER_LEGGINGS);
                    LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) Rleggings.getItemMeta();
                    leggingsMeta.setColor(Color.RED);
                    Rleggings.setItemMeta(leggingsMeta);

                    // 创建红色皮革靴子
                    ItemStack Rboots = new ItemStack(Material.LEATHER_BOOTS);
                    LeatherArmorMeta bootsMeta = (LeatherArmorMeta) Rboots.getItemMeta();
                    bootsMeta.setColor(Color.RED);
                    Rboots.setItemMeta(bootsMeta);

                    // 装备盔甲到玩家
                    PlayerInventory inventory = player.getInventory();
                    inventory.setHelmet(Rhelmet);
                    inventory.setChestplate(Rchestplate);
                    inventory.setLeggings(Rleggings);
                    inventory.setBoots(Rboots);

                    player.updateInventory(); // 确保客户端更新
                    GetClassGUI.ClassGUI(player);
                }
                return;
            }
            if(event.getRawSlot() == 3){
                // 保存玩家点击前的 finishTeam 数值
                playerFinishTeam.put(player.getName(), finishTeam);

                TeamManager yellowTeam = teams.get("yellow");
                if (yellowTeam.isFull(maxMembersPerTeam)) {
                    player.sendMessage(ChatColor.RED + "黄队已满，无法加入！");
                } else {
                    yellowTeam.addMember(player.getName());
                    player.sendMessage(ChatColor.GREEN + "你已加入黄队！");
                    //玩家成功选择队伍后判定人数+1并传送、给予盔甲，跳转到职业选择界面
                    finishTeam = finishTeam + 1;
                    yellowplayer = yellowplayer + 1;
                    player.teleport(yellowTeamHome);
                    // 创建黄色皮革头盔
                    ItemStack Yhelmet = new ItemStack(Material.LEATHER_HELMET);
                    LeatherArmorMeta helmetMeta = (LeatherArmorMeta) Yhelmet.getItemMeta();
                    helmetMeta.setColor(Color.YELLOW);
                    Yhelmet.setItemMeta(helmetMeta);

                    // 创建黄色皮革胸甲
                    ItemStack Ychestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
                    LeatherArmorMeta chestMeta = (LeatherArmorMeta) Ychestplate.getItemMeta();
                    chestMeta.setColor(Color.YELLOW);
                    Ychestplate.setItemMeta(chestMeta);

                    // 创建黄色皮革护腿
                    ItemStack Yleggings = new ItemStack(Material.LEATHER_LEGGINGS);
                    LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) Yleggings.getItemMeta();
                    leggingsMeta.setColor(Color.YELLOW);
                    Yleggings.setItemMeta(leggingsMeta);

                    // 创建黄色皮革靴子
                    ItemStack Yboots = new ItemStack(Material.LEATHER_BOOTS);
                    LeatherArmorMeta bootsMeta = (LeatherArmorMeta) Yboots.getItemMeta();
                    bootsMeta.setColor(Color.YELLOW);
                    Yboots.setItemMeta(bootsMeta);

                    // 装备盔甲到玩家
                    PlayerInventory inventory = player.getInventory();
                    inventory.setHelmet(Yhelmet);
                    inventory.setChestplate(Ychestplate);
                    inventory.setLeggings(Yleggings);
                    inventory.setBoots(Yboots);

                    player.updateInventory(); // 确保客户端更新
                    GetClassGUI.ClassGUI(player);
                }
            }
            if(event.getRawSlot() == 5){
                // 保存玩家点击前的 finishTeam 数值
                playerFinishTeam.put(player.getName(), finishTeam);

                TeamManager blueTeam = teams.get("blue");
                if (blueTeam.isFull(maxMembersPerTeam)) {
                    player.sendMessage(ChatColor.RED + "蓝队已满，无法加入！");
                } else {
                    blueTeam.addMember(player.getName());
                    player.sendMessage(ChatColor.GREEN + "你已加入蓝队！");
                    //玩家成功选择队伍后判定人数+1并传送、给予盔甲，跳转到职业选择界面
                    finishTeam = finishTeam + 1;
                    blueplayer = blueplayer + 1;
                    player.teleport(blueTeamHome);
                    // 创建蓝色皮革头盔
                    ItemStack Bhelmet = new ItemStack(Material.LEATHER_HELMET);
                    LeatherArmorMeta helmetMeta = (LeatherArmorMeta) Bhelmet.getItemMeta();
                    helmetMeta.setColor(Color.BLUE);
                    Bhelmet.setItemMeta(helmetMeta);

                    // 创建蓝色皮革胸甲
                    ItemStack Bchestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
                    LeatherArmorMeta chestMeta = (LeatherArmorMeta) Bchestplate.getItemMeta();
                    chestMeta.setColor(Color.BLUE);
                    Bchestplate.setItemMeta(chestMeta);

                    // 创建蓝色皮革护腿
                    ItemStack Bleggings = new ItemStack(Material.LEATHER_LEGGINGS);
                    LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) Bleggings.getItemMeta();
                    leggingsMeta.setColor(Color.BLUE);
                    Bleggings.setItemMeta(leggingsMeta);

                    // 创建蓝色皮革靴子
                    ItemStack Bboots = new ItemStack(Material.LEATHER_BOOTS);
                    LeatherArmorMeta bootsMeta = (LeatherArmorMeta) Bboots.getItemMeta();
                    bootsMeta.setColor(Color.BLUE);
                    Bboots.setItemMeta(bootsMeta);

                    // 装备盔甲到玩家
                    PlayerInventory inventory = player.getInventory();
                    inventory.setHelmet(Bhelmet);
                    inventory.setChestplate(Bchestplate);
                    inventory.setLeggings(Bleggings);
                    inventory.setBoots(Bboots);

                    player.updateInventory(); // 确保客户端更新
                    GetClassGUI.ClassGUI(player);
                }
            }
            if(event.getRawSlot() == 7){
                // 保存玩家点击前的 finishTeam 数值
                playerFinishTeam.put(player.getName(), finishTeam);

                TeamManager greenTeam = teams.get("green");
                if (greenTeam.isFull(maxMembersPerTeam)) {
                    player.sendMessage(ChatColor.RED + "绿队已满，无法加入！");
                } else {
                    greenTeam.addMember(player.getName());
                    player.sendMessage(ChatColor.GREEN + "你已加入绿队！");
                    //玩家成功选择队伍后判定人数+1并传送、给予盔甲，跳转到职业选择界面
                    finishTeam = finishTeam + 1;
                    greenplayer = greenplayer + 1;
                    player.teleport(greenTeamHome);
                    // 创建绿色皮革头盔
                    ItemStack Ghelmet = new ItemStack(Material.LEATHER_HELMET);
                    LeatherArmorMeta helmetMeta = (LeatherArmorMeta) Ghelmet.getItemMeta();
                    helmetMeta.setColor(Color.GREEN);
                    Ghelmet.setItemMeta(helmetMeta);

                    // 创建绿色皮革胸甲
                    ItemStack Gchestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
                    LeatherArmorMeta chestMeta = (LeatherArmorMeta) Gchestplate.getItemMeta();
                    chestMeta.setColor(Color.GREEN);
                    Gchestplate.setItemMeta(chestMeta);

                    // 创建绿色皮革护腿
                    ItemStack Gleggings = new ItemStack(Material.LEATHER_LEGGINGS);
                    LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) Gleggings.getItemMeta();
                    leggingsMeta.setColor(Color.GREEN);
                    Gleggings.setItemMeta(leggingsMeta);

                    // 创建绿色皮革靴子
                    ItemStack Gboots = new ItemStack(Material.LEATHER_BOOTS);
                    LeatherArmorMeta bootsMeta = (LeatherArmorMeta) Gboots.getItemMeta();
                    bootsMeta.setColor(Color.GREEN);
                    Gboots.setItemMeta(bootsMeta);

                    // 装备盔甲到玩家
                    PlayerInventory inventory = player.getInventory();
                    inventory.setHelmet(Ghelmet);
                    inventory.setChestplate(Gchestplate);
                    inventory.setLeggings(Gleggings);
                    inventory.setBoots(Gboots);

                    player.updateInventory(); // 确保客户端更新
                    GetClassGUI.ClassGUI(player);
                }
            }
        }
        //判定是否可以开始游戏
        if (finishTeam == 16 && finishClass == 16) {
            TeamIsFinish = true;
            ClassIsFinish = true;
            opengui = false;
            startGame();
        }
    }

    //玩家选择职业界面点击事件处理
    @EventHandler
    public void clickclass(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equalsIgnoreCase(ChatColor.BLACK.WHITE+"职业选择")) {
            event.setCancelled(true);
            if (event.getRawSlot() > event.getInventory().getSize()) {
                return;
            }
            if (event.getRawSlot() < 0) {
                return;
            }
            // 保存玩家点击前的 finishClass 数值
            playerFinishClass.put(player.getName(), finishClass);

            String className = "";
            switch (event.getRawSlot()) {
                case 0:
                    className = "zombie";
                    player.sendMessage(ChatColor.GREEN + "已选择职业：僵尸");
                    break;
                case 1:
                    className = "spider";
                    player.sendMessage(ChatColor.GREEN + "已选择职业：蜘蛛");
                    break;
                case 2:
                    className = "creeper";
                    player.sendMessage(ChatColor.GREEN + "已选择职业：苦力怕");
                    break;
                case 3:
                    className = "ghast";
                    player.sendMessage(ChatColor.GREEN + "已选择职业：恶魂");
                    break;
                case 4:
                    className = "witch";
                    player.sendMessage(ChatColor.GREEN + "已选择职业：女巫");
                    break;
                case 5:
                    className = "iron_golem";
                    player.sendMessage(ChatColor.GREEN + "已选择职业：铁傀儡");
                    break;
                case 6:
                    className = "slime";
                    player.sendMessage(ChatColor.GREEN + "已选择职业：史莱姆");
                    break;
                case 7:
                    className = "skeleton";
                    player.sendMessage(ChatColor.GREEN + "已选择职业：骷髅");
                    break;
                case 17:
                    GetTeamGUI.TeamGUI(player);
                    finishTeam = finishTeam - 1;
                    return;
                default:
                    return;
            }
            finishClass = finishClass + 1;
            player.closeInventory();
            // 更新 playerClasses 映射
            playerClasses.put(player.getName(), className);
            // 调用 switchPlayerClass 方法
            EndBattle.getInstance().switchPlayerClass(player, className);
            // 更新计分板
            updateScoreboard(player);
        }
        if (finishTeam == 16 && finishClass == 16) {
            TeamIsFinish = true;
            ClassIsFinish = true;
            opengui = false;
            startGame();
        }
    }

    //商店购买点击处理
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
                new ShopGui(player).open();
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
            if (event.getRawSlot() == 23) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 20)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 20));
                    p.getInventory().addItem(new ItemStack(Material.CROSSBOW));
                }
            }
            if (event.getRawSlot() == 28) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 40)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 40));
                    p.getInventory().setChestplate(null);
                    p.getInventory().setLeggings(null);
                    ItemStack chainmailChestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
                    ItemStack chainmailLeggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
                    ItemStack chainmailBoots = new ItemStack(Material.CHAINMAIL_BOOTS);
                    p.getInventory().setChestplate(chainmailChestplate);
                    p.getInventory().setLeggings(chainmailLeggings);
                    p.getInventory().setBoots(chainmailBoots);
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
                    ItemStack ironBoots = new ItemStack(Material.IRON_BOOTS);
                    p.getInventory().setChestplate(ironChestplate);
                    p.getInventory().setLeggings(ironLeggings);
                    p.getInventory().setBoots(ironBoots);
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
                    ItemStack diamondBoots = new ItemStack(Material.DIAMOND_BOOTS);
                    p.getInventory().setChestplate(diamondChestplate);
                    p.getInventory().setLeggings(diamondLeggings);
                    p.getInventory().setBoots(diamondBoots);
                    p.updateInventory();
                } else {

                }
            }
            if (event.getRawSlot() == 31) {
                if (p.getInventory().containsAtLeast(new ItemStack(Material.IRON_INGOT), 40)) {
                    p.getInventory().removeItem(new ItemStack(Material.IRON_INGOT, 40));
                    p.getInventory().addItem(new ItemStack(Material.SHIELD));
                }
            }
            if (event.getRawSlot() == 44) {
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
            if (event.getRawSlot() == 35) {
                Shop.ShopGUI(p);
            }
        }
    }

    @EventHandler
    public void setScoreboard(Player player) {
        // 初始化计分板
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("endbattle", "dummy", "End Battle", RenderType.INTEGER);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // 设置玩家计分板
        player.setScoreboard(scoreboard);
    }

    //游戏开始相关
    private void startGame() {
        if (TeamIsFinish && ClassIsFinish) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                //向所有玩家发送游戏开始
                player.sendMessage("游戏开始！");

                // 重置所有玩家药水、血量、饥饿度
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);

                // 添加夜视效果
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, true, false, false));

                // 添加生命恢复效果
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0, true, false, false));

                // 检查玩家的职业
                String className = playerClasses.get(player.getName());
                if ("ghast".equalsIgnoreCase(className)) {
                    // 给予缓降效果
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, Integer.MAX_VALUE, 1, false, false));
                }

                // 储存并清除指定区域内的方块
                Location corner1 = new Location(Bukkit.getWorld("world_the_end"), -4, 198, -4);
                Location corner2 = new Location(Bukkit.getWorld("world_the_end"), 4, 203, 4);
                hall(corner1, corner2);
            }
            // 记录旗帜位置上方块
            initialBlocks.put(redFlag, redFlag.getBlock().getRelative(BlockFace.UP).getBlockData());
            initialBlocks.put(yellowFlag, yellowFlag.getBlock().getRelative(BlockFace.UP).getBlockData());
            initialBlocks.put(blueFlag, blueFlag.getBlock().getRelative(BlockFace.UP).getBlockData());
            initialBlocks.put(greenFlag, greenFlag.getBlock().getRelative(BlockFace.UP).getBlockData());

            //物品刷新与末影龙重生计时器
            startItemIronTask();
            startItemDiamondTask();
            startItemGoldTask();
            startItemLapis_LazuliTask();
            Plugin plugin = Bukkit.getPluginManager().getPlugin("EndBattle");
            if (plugin != null) {
                Bukkit.getPluginManager().registerEvents(this, plugin);
                Bukkit.getScheduler().runTaskLater(plugin, this::spawnEnderDragon, 20L * 60L * 10L);
            }

            //设置世界生成点为末地
            World endWorld = Bukkit.getWorld("world_the_end");
            if (endWorld != null) {
                Location spawnLocation = new Location(endWorld, 0, 0, 128);
                endWorld.setSpawnLocation(spawnLocation.getBlockX(), spawnLocation.getBlockY(), spawnLocation.getBlockZ());
            }
        }
    }

    //注册钻石刷新计时器，设置刷新间隔为30秒
    private void startItemDiamondTask() {
        Bukkit.getScheduler().runTaskTimer(Bukkit.getPluginManager().getPlugin("EndBattle"), this::DiamondItems,0L,20L*30L);
    }

    //注册青金石刷新计时器，设置刷新间隔为60秒
    private void startItemLapis_LazuliTask() {
        Bukkit.getScheduler().runTaskTimer(Bukkit.getPluginManager().getPlugin("EndBattle"), this::Lapis_LazuliItems, 0L, 20L * 60L);
    }

    //注册金锭刷新计时器，设置刷新间隔为10秒
    private void startItemGoldTask(){
        Bukkit.getScheduler().runTaskTimer(Bukkit.getPluginManager().getPlugin("EndBattle"), this::GoldItems,0L,20L*10L);
    }

    //注册铁锭刷新计时器，设置刷新间隔为3秒
    private void startItemIronTask() {
        Bukkit.getScheduler().runTaskTimer(Bukkit.getPluginManager().getPlugin("EndBattle"), this::IronItems,0L,20L*3L);
    }

    //铁锭刷新位置与随机数
    private void IronItems() {
        //设置刷新位置
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

        //每个队伍一个随机数，一个随机数控制队伍三个刷新位置
        ItemStack itemTOiron1 = getRandomItem(Math.random());
        ItemStack itemTOiron2 = getRandomItem(Math.random());
        ItemStack itemTOiron3 = getRandomItem(Math.random());
        ItemStack itemTOiron4 = getRandomItem(Math.random());

        //刷新物品位置与物品
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

    //钻石刷新位置与随机数
    private void DiamondItems() {
        //设置刷新位置
        Location DiamondLocation1 = new Location(Bukkit.getWorld("world_the_end"), -73,130,-73);
        Location DiamondLocation2 = new Location(Bukkit.getWorld("world_the_end"), 74,130,-73);
        Location DiamondLocation3 = new Location(Bukkit.getWorld("world_the_end"), 74,130,74);
        Location DiamondLocation4 = new Location(Bukkit.getWorld("world_the_end"), -73,130,74);

        //为四个刷新位置分别设置一个随机数
        ItemStack itemTODiamond1 = getRandomDiamondItem(Math.random());
        ItemStack itemTODiamond2 = getRandomDiamondItem(Math.random());
        ItemStack itemTODiamond3 = getRandomDiamondItem(Math.random());
        ItemStack itemTODiamond4 = getRandomDiamondItem(Math.random());

        //刷新物品位置与物品
        DiamondLocation1.getWorld().dropItemNaturally(DiamondLocation1, itemTODiamond1);
        DiamondLocation2.getWorld().dropItemNaturally(DiamondLocation2, itemTODiamond2);
        DiamondLocation3.getWorld().dropItemNaturally(DiamondLocation3, itemTODiamond3);
        DiamondLocation4.getWorld().dropItemNaturally(DiamondLocation4, itemTODiamond4);
    }

    //金锭刷新位置与随机数
    private void GoldItems() {
        //设置刷新位置
        Location GoldLocation1 = new Location(Bukkit.getWorld("world_the_end"), -55,124,0);
        Location GoldLocation2 = new Location(Bukkit.getWorld("world_the_end"), 55,124,0);
        Location GoldLocation3 = new Location(Bukkit.getWorld("world_the_end"), 0,124,55);
        Location GoldLocation4 = new Location(Bukkit.getWorld("world_the_end"), 0,124,-55);

        //为四个刷新位置分别设置一个随机数
        ItemStack itemTOGold1 = getRandomGoldItem(Math.random());
        ItemStack itemToGold2 = getRandomGoldItem(Math.random());
        ItemStack itemTOGold3 = getRandomGoldItem(Math.random());
        ItemStack itemToGold4 = getRandomGoldItem(Math.random());

        //刷新物品位置与物品
        GoldLocation1.getWorld().dropItemNaturally(GoldLocation1, itemTOGold1);
        GoldLocation2.getWorld().dropItemNaturally(GoldLocation2, itemToGold2);
        GoldLocation3.getWorld().dropItemNaturally(GoldLocation3, itemTOGold3);
        GoldLocation4.getWorld().dropItemNaturally(GoldLocation4, itemToGold4);
    }

    //青金石刷新位置与随机数，并判断刷新物品
    private void Lapis_LazuliItems() {
        //设置刷新位置
        Location Lapis_LazuliLocation = new Location(Bukkit.getWorld("world_the_end"), 0, 125, 0);
        //设置随机数
        double lapis_lazuli = Math.random();

        //声明一个变量，用于存储刷新物品
        ItemStack itemToLapis_Lazu;

        //判断随机数，并设置刷新物品
        if (lapis_lazuli < 0.7) {
            itemToLapis_Lazu = new ItemStack(Material.LAPIS_LAZULI, 1);
        } else if (lapis_lazuli < 0.9) {
            itemToLapis_Lazu = new ItemStack(Material.DIAMOND, 1);
        } else {
            itemToLapis_Lazu = new ItemStack(Material.EXPERIENCE_BOTTLE, 1);
        }

        //刷新物品
        Lapis_LazuliLocation.getWorld().dropItemNaturally(Lapis_LazuliLocation, itemToLapis_Lazu);
    }

    //添加铁锭刷新位置随机数判定
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

    //添加钻石刷新位置随机数判定
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

    //添加金锭刷新位置随机数判定
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

    //末影龙10分钟刷新判定
    private void spawnEnderDragon() {
        World endWorld = Bukkit.getWorld("world_the_end");
        //添加四个末影水晶位置
        if (endWorld != null) {
            Location[] crystalLocations = {
                    new Location(endWorld, 14, 139, -13),
                    new Location(endWorld, 14, 139, 13),
                    new Location(endWorld, -13, 139, 14),
                    new Location(endWorld, -13, 139, -14)
            };

            //放置末影水晶
            for (Location loc : crystalLocations) {
                Location baseLocation = loc.clone().subtract(0, 1, 0);
                baseLocation.getBlock().setType(Material.OBSIDIAN);
                EnderCrystal crystal = (EnderCrystal) endWorld.spawnEntity(loc, EntityType.END_CRYSTAL);
                crystal.setShowingBottom(true);
                crystal.setInvulnerable(false);
            }

            //放置末影龙
            Location dragonSpawnLocation = new Location(endWorld, 0, 128, 0);
            EnderDragon dragon = (EnderDragon) endWorld.spawnEntity(dragonSpawnLocation, EntityType.ENDER_DRAGON);

            //设置末影龙属性
            dragon.setPhase(EnderDragon.Phase.CIRCLING);
            dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(200.0);
            dragon.setHealth(200.0);

            // 创建Boss Bar
            BossBar bossBar = Bukkit.createBossBar("末影龙", BarColor.RED, BarStyle.SOLID);
            bossBar.setProgress(dragon.getHealth() / dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());

            // 为每个在线玩家添加Boss Bar
            for (Player player : Bukkit.getOnlinePlayers()) {
                bossBar.addPlayer(player);
                dragonBossBars.put(player, bossBar);
            }
            //播放特效和声音
            endWorld.playEffect(dragonSpawnLocation, Effect.ENDER_SIGNAL, 0);
            endWorld.playSound(dragonSpawnLocation, Sound.ENTITY_ENDER_DRAGON_GROWL, 5.0F, 1.0F);

            Bukkit.broadcastMessage(ChatColor.DARK_RED + "末影龙已复活！");
        }
    }

    //末影龙死亡事件
    @EventHandler
    public void onEnderDragonDeath(EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.ENDER_DRAGON) {
            // 设置掉落的经验值为 1000
            event.setDroppedExp(1000);

            // 移除Boss Bar
            for (Player player : dragonBossBars.keySet()) {
                BossBar bossBar = dragonBossBars.get(player);
                bossBar.removePlayer(player);
            }
            dragonBossBars.clear();

            // 重新生成末影龙
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("EndBattle"), this::spawnEnderDragon, 20L * 60L * 5L);
        }
    }

    //玩家死亡事件
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String teamName = getTeamName(player);

        // 保存玩家装备
        ItemStack[] equipment = new ItemStack[4];
        equipment[0] = player.getInventory().getHelmet();
        equipment[1] = player.getInventory().getChestplate();
        equipment[2] = player.getInventory().getLeggings();
        equipment[3] = player.getInventory().getBoots();
        playerEquipment.put(player.getName(), equipment);

        // 如果玩家死亡先转换模式为旁观者模式
        if (teamName == null) {
            return;
        }
        player.setGameMode(GameMode.SPECTATOR);

        // 如果死亡后队伍旗帜被破坏，则将玩家传送回队伍出生点并保持旁观者模式
        if (isTeamFlagBroken(teamName)) {
            TeamManager team = teams.get(teamName);
            team.removeMember(player.getName());
            updateTeamPlayerCount(teamName);
            player.sendMessage(ChatColor.RED + "你的队伍旗帜已被破坏，你将无法传送回出生点！");
            Location teamHome = getTeamHome(teamName);
            if (teamHome != null) {
                player.teleport(teamHome);
            }
        } else {
            // 如果死亡后队伍旗帜没有被破坏，则将玩家传送回队伍出生点并设置游戏模式为生存模式
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Location teamHome = getTeamHome(teamName);
                if (teamHome != null) {
                    player.teleport(teamHome);
                    player.setGameMode(GameMode.SURVIVAL);

                    // 重新添加夜视效果
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, true, false, false));

                    // 重新添加生命恢复效果
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0, true, false, false));

                    // 检查玩家的职业
                    String className = playerClasses.get(player.getName());
                    if ("ghast".equalsIgnoreCase(className)) {
                        // 给予缓降效果
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, Integer.MAX_VALUE, 1, false, false));
                    }

                    // 恢复玩家装备
                    ItemStack[] savedEquipment = playerEquipment.get(player.getName());
                    if (savedEquipment != null) {
                        player.getInventory().setHelmet(savedEquipment[0]);
                        player.getInventory().setChestplate(savedEquipment[1]);
                        player.getInventory().setLeggings(savedEquipment[2]);
                        player.getInventory().setBoots(savedEquipment[3]);
                    }
                }
                // 复活倒计时5s
            }, 20L * 5L);
        }
        // 记录击杀信息
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            String killerName = killer.getName();
            playerKills.put(killerName, playerKills.getOrDefault(killerName, 0) + 1);
        }
    }

    //获取队伍名称
    public String getTeamName(Player player) {
        for (Map.Entry<String, TeamManager> entry : teams.entrySet()) {
            if (entry.getValue().getMembers().contains(player.getName())) {
                return entry.getKey();
            }
        }
        return null;
    }

    //获取队伍旗帜是否被破坏
    private boolean isTeamFlagBroken(String teamName) {
        switch (teamName) {
            case "red":
                return redFlagBroken;
            case "yellow":
                return yellowFlagBroken;
            case "blue":
                return blueFlagBroken;
            case "green":
                return greenFlagBroken;
            default:
                return false;
        }
    }

    //获取队伍出生点
    private Location getTeamHome(String teamName) {
        switch (teamName) {
            case "red":
                return redTeamHome;
            case "yellow":
                return yellowTeamHome;
            case "blue":
                return blueTeamHome;
            case "green":
                return greenTeamHome;
            default:
                return null;
        }
    }

    //更新player数，用于作为游戏结束判定
    private void updateTeamPlayerCount(String teamName) {
        switch (teamName) {
            case "red":
                redplayer--;
                break;
            case "yellow":
                yellowplayer--;
                break;
            case "blue":
                blueplayer--;
                break;
            case "green":
                greenplayer--;
                break;
        }
    }

    //旗帜被破坏事件
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (block.getLocation().equals(redFlag)) {
                redFlagBroken = true;
                announceFlagBroken("红队");
            } else if (block.getLocation().equals(yellowFlag)) {
                yellowFlagBroken = true;
                announceFlagBroken("黄队");
            } else if (block.getLocation().equals(blueFlag)) {
                blueFlagBroken = true;
                announceFlagBroken("蓝队");
            } else if (block.getLocation().equals(greenFlag)) {
                greenFlagBroken = true;
                announceFlagBroken("绿队");
            }
        }
    }

    //旗帜被破坏事件
    @EventHandler
    public void onFlagBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getLocation().equals(redFlag)) {
            redFlagBroken = true;
            announceFlagBroken("红队");
        } else if (block.getLocation().equals(yellowFlag)) {
            yellowFlagBroken = true;
            announceFlagBroken("黄队");
        } else if (block.getLocation().equals(blueFlag)) {
            blueFlagBroken = true;
            announceFlagBroken("蓝队");
        } else if (block.getLocation().equals(greenFlag)) {
            greenFlagBroken = true;
            announceFlagBroken("绿队");
        }
    }

    //宣布某队旗帜被破坏
    private void announceFlagBroken(String teamName) {
        Bukkit.broadcastMessage(ChatColor.RED + teamName + "的旗帜已被破坏！");
        World endWorld = Bukkit.getWorld("world_the_end");
        if (endWorld != null) {
            endWorld.playSound(endWorld.getSpawnLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 5.0F, 1.0F);
        }
    }

    //结束游戏相关
    @EventHandler
    public void endGame() {
        // 判断胜利队伍
        String winningTeam = null;
        if (redplayer > 0 && yellowplayer == 0 && blueplayer == 0 && greenplayer == 0) {
            winningTeam = "red";
        } else if (yellowplayer > 0 && redplayer == 0 && blueplayer == 0 && greenplayer == 0) {
            winningTeam = "yellow";
        } else if (blueplayer > 0 && redplayer == 0 && yellowplayer == 0 && greenplayer == 0) {
            winningTeam = "blue";
        } else if (greenplayer > 0 && redplayer == 0 && yellowplayer == 0 && blueplayer == 0) {
            winningTeam = "green";
        } else {
            // 如果没有队伍胜利，直接返回
            return;
        }
        gameEnded = true;

        if(gameEnded){
            // 停止所有物品刷新
            stopItemSpawning();

            // 杀死末影龙
            killEnderDragon();

            // 清空ALLOWED_BLOCKS
            clearAllowedBlocks();

            // 清空掉落物
            clearDroppedItems();

            // 清空玩家背包内物品
            clearPlayerInventories();

            // 清空箱子内物品
            clearChestContents();

            // 清空玩家存放在末影箱中的物品
            clearEnderChestContents();

            // 将所有玩家传送到主世界（0，0，0）坐标
            teleportPlayersToSpawn();

            // 设置主世界的（0，0，0）坐标为世界出生点
            setWorldSpawnPoint(0, 0, 0);

            // 转换所有玩家模式为生存模式
            setPlayerGameModeToSurvival();

            // 广播胜利消息
            Bukkit.broadcastMessage(ChatColor.GREEN + winningTeam + "队胜利！");

            // 重置所有声明值，清除玩家队伍与职业
            resetGame();

            // 清空玩家经验值
            clearPlayerExperience();

            // 还原旗帜位置上方块
            restoreInitialBlocks();
        }

    }

    // 重置所有声明值，清除玩家队伍与职业
    private void clearPlayerExperience() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setTotalExperience(0);
            player.setLevel(0);
            player.setExp(0);
        }
    }

    private void stopItemSpawning() {
        // 停止所有物品刷新任务
        Bukkit.getScheduler().cancelTasks(plugin);
    }

    private void killEnderDragon() {
        // 杀死末影龙
        World endWorld = Bukkit.getWorld("world_the_end");
        if (endWorld != null) {
            for (EnderDragon dragon : endWorld.getEntitiesByClass(EnderDragon.class)) {
                dragon.remove();
            }
        }
    }

    private void clearAllowedBlocks() {
        // 清空ALLOWED_BLOCKS
        World endWorld = Bukkit.getWorld("world_the_end");
        if (endWorld != null) {
            for (Chunk chunk : endWorld.getLoadedChunks()) {
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 256; y++) {
                        for (int z = 0; z < 16; z++) {
                            Block b = chunk.getBlock(x, y, z);
                            if (ALLOWED_BLOCKS.contains(b.getType())) {
                                b.setType(Material.AIR);
                            }
                        }
                    }
                }
            }
        }
    }

    private void clearDroppedItems() {
        // 清空掉落物
        World endWorld = Bukkit.getWorld("world_the_end");
        if (endWorld != null) {
            for (Item item : endWorld.getEntitiesByClass(Item.class)) {
                item.remove();
            }
        }
    }

    private void clearPlayerInventories() {
        // 清空玩家背包内物品
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
        }
    }

    private void clearChestContents() {
        // 清空箱子内物品
        World endWorld = Bukkit.getWorld("world_the_end");
        if (endWorld != null) {
            for (Entity entity : endWorld.getEntities()) {
                if (entity instanceof Chest) {
                    ((Chest) entity).getInventory().clear();
                }
            }
            // 或者遍历所有块来查找箱子
            for (Chunk chunk : endWorld.getLoadedChunks()) {
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 256; y++) {
                        for (int z = 0; z < 16; z++) {
                            Block block = chunk.getBlock(x, y, z);
                            if (block.getState() instanceof Chest) {
                                ((Chest) block.getState()).getInventory().clear();
                            }
                        }
                    }
                }
            }
        }
    }

    private void clearEnderChestContents() {
        // 清空玩家存放在末影箱中的物品
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getEnderChest().clear();
        }
    }

    private void teleportPlayersToSpawn() {
        // 将所有玩家传送到主世界（0，0，0）坐标
        World mainWorld = Bukkit.getWorld("world");
        if (mainWorld != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.teleport(new Location(mainWorld, 0, 0, 0));
            }
        }
    }

    private void setWorldSpawnPoint(int x, int y, int z) {
        // 设置主世界的（0，0，0）坐标为世界出生点
        World mainWorld = Bukkit.getWorld("world");
        if (mainWorld != null) {
            mainWorld.setSpawnLocation(x, y, z);
        }
    }

    private void setPlayerGameModeToSurvival() {
        // 转换所有玩家模式为生存模式
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

    private void resetGame() {
        // 重置所有声明值，清除玩家队伍与职业
        teams.clear();
        playerLevels.clear();
        finishTeam = 0;
        finishClass = 0;
        redplayer = 0;
        yellowplayer = 0;
        blueplayer = 0;
        greenplayer = 0;
        redFlagBroken = false;
        yellowFlagBroken = false;
        blueFlagBroken = false;
        greenFlagBroken = false;
        TeamIsFinish = false;
        ClassIsFinish = false;
        gameEnded = false;

        // 重新初始化队伍
        teams.put("red", new TeamManager("Red Team", ChatColor.RED));
        teams.put("yellow", new TeamManager("Yellow Team", ChatColor.YELLOW));
        teams.put("blue", new TeamManager("Blue Team", ChatColor.BLUE));
        teams.put("green", new TeamManager("Green Team", ChatColor.GREEN));

        // 重置计分板
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("endbattle", "dummy", "End Battle", RenderType.INTEGER);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(scoreboard);
        }
    }

    //判定玩家经验值并增加生命值上限
    @EventHandler
    public void onPlayerLevelChange(PlayerLevelChangeEvent event) {
        Player player = event.getPlayer();
        String className = playerClasses.get(player.getName());

        if (className == null) {
            return;
        }

        int oldLevel = event.getOldLevel();
        int newLevel = event.getNewLevel();

        // 只有在等级小于或等于15时才增加生命值上限
        if (newLevel <= 15) {
            double healthIncrease = "irongolem".equalsIgnoreCase(className) ? 2.0 : 1.0;

            double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

            if (newLevel > oldLevel) {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth + healthIncrease);
            } else if (newLevel < oldLevel) {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth - healthIncrease);
            }
        }
    }

    // 恢复初始方块
    private void restoreInitialBlocks() {
        for (Map.Entry<Location, BlockData> entry : initialBlocks.entrySet()) {
            Location location = entry.getKey().clone().add(0, 1, 0);
            BlockData blockData = entry.getValue();
            location.getBlock().setBlockData(blockData);
        }
        initialBlocks.clear(); // 清空记录
    }

    // 恢复 finishTeam 和 finishClass 数值
    public void restoreFinishCounts(String playerName) {
        if (playerFinishTeam.containsKey(playerName)) {
            finishTeam = playerFinishTeam.get(playerName);
            playerFinishTeam.remove(playerName);
        }
        if (playerFinishClass.containsKey(playerName)) {
            finishClass = playerFinishClass.get(playerName);
            playerFinishClass.remove(playerName);
        }
    }

    //防止同队玩家互相攻击
    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Player defender = (Player) event.getEntity();

            ItemStack attackerHelmet = attacker.getInventory().getHelmet();
            ItemStack defenderHelmet = defender.getInventory().getHelmet();

            if (attackerHelmet != null && defenderHelmet != null &&
                    attackerHelmet.getType() == Material.LEATHER_HELMET &&
                    defenderHelmet.getType() == Material.LEATHER_HELMET) {

                LeatherArmorMeta attackerMeta = (LeatherArmorMeta) attackerHelmet.getItemMeta();
                LeatherArmorMeta defenderMeta = (LeatherArmorMeta) defenderHelmet.getItemMeta();

                if (attackerMeta != null && defenderMeta != null) {
                    Color attackerColor = attackerMeta.getColor();
                    Color defenderColor = defenderMeta.getColor();

                    if (attackerColor.equals(defenderColor)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    // 在玩家击杀实体时更新击杀数
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player) {
            Player killer = (Player) event.getEntity().getKiller();
            String killerName = killer.getName();

            // 更新击杀数
            playerKills.put(killerName, playerKills.getOrDefault(killerName, 0) + 1);

            // 更新计分板
            updateScoreboard(killer);

            // 更新计分板上的击杀数排名
            updateKillCountRanking();
        }
    }

    // 更新玩家计分板
    private void updateScoreboard(Player player) {
        String teamName = getTeamName(player);
        String className = playerClasses.get(player.getName());
        int kills = playerKills.getOrDefault(player.getName(), 0);

        Score score = objective.getScore(player.getName());
        score.setScore(kills);

        // 设置玩家计分板显示
        player.setScoreboard(scoreboard);

        // 设置玩家计分板显示内容
        Team team = scoreboard.getTeam(player.getName());
        if (team == null) {
            team = scoreboard.registerNewTeam(player.getName());
        }
        team.addEntry(player.getName());
        team.setPrefix(teams.get(teamName).getColor().toString() + "[" + teamName + "] " + className + " ");
    }

    // 更新击杀数排名
    private void updateKillCountRanking() {
        // 清空现有计分板
        for (String entry : objective.getScoreboard().getEntries()) {
            objective.getScore(entry).setScore(0);
        }

        // 获取所有玩家的击杀数并排序
        List<Map.Entry<String, Integer>> sortedKills = new ArrayList<>(playerKills.entrySet());
        sortedKills.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        // 更新计分板上的击杀数排名
        int rank = 1;
        for (Map.Entry<String, Integer> entry : sortedKills) {
            String playerName = entry.getKey();
            int kills = entry.getValue();

            Score score = objective.getScore(playerName);
            score.setScore(kills);

            // 设置玩家计分板显示内容
            Team team = scoreboard.getTeam(playerName);
            if (team == null) {
                team = scoreboard.registerNewTeam(playerName);
            }
            String teamName = getTeamName(Bukkit.getPlayer(playerName));
            String className = playerClasses.get(playerName);
            team.setPrefix(teams.get(teamName).getColor().toString() + "[" + teamName + "] " + className + " ");
            team.setSuffix(" Kills: " + kills);

            rank++;
        }
    }
}
