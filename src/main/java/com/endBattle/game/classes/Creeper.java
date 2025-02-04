package com.endBattle.game.classes;

import com.endBattle.EndBattle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.endBattle.game.shop.item.Money;
import com.endBattle.game.shop.item.ShopItem;

import java.util.ArrayList;
import java.util.List;

public class Creeper extends Clazz {
    private Player player;
    private int killCount;
    private int totalKills;
    private static final int TNT_SLOT = 1;      // TNT槽位
    private static final int SNOWBALL_SLOT = 7;  // 雪球槽位
    private static final int POTION_SLOT = 8;    // 药水槽位

    public Creeper(Player player) {
        super("creeper", "苦力怕", player);
        this.player = player;

        // 从配置文件中读取killCount和totalKills的值
        this.killCount = EndBattle.getInstance().getConfig().getInt("creeper." + player.getUniqueId() + ".killCount", 0);
        this.totalKills = EndBattle.getInstance().getConfig().getInt("creeper." + player.getUniqueId() + ".totalKills", 0);
    }

    @EventHandler
    public void handleDeath(PlayerDeathEvent event) {
        if (!getSelect()) {
            return;
        }
        // 检查是否是当前职业
        if (event.getEntity() == player) {
            // 被动：死亡位置生成TNT
            Location deathLoc = player.getLocation();
            TNTPrimed tnt = deathLoc.getWorld().spawn(deathLoc, TNTPrimed.class);
            tnt.setFuseTicks(40); // 2秒后爆炸
        }
        
        // 击杀相关
        if (event.getEntity().getKiller() == player) {
            killCount++;
            totalKills++;
            // 保存到配置文件
            EndBattle.getInstance().getConfig().set("creeper." + player.getUniqueId() + ".killCount", killCount);
            EndBattle.getInstance().getConfig().set("creeper." + player.getUniqueId() + ".totalKills", totalKills);
            EndBattle.getInstance().saveConfig();
            
            // 技能一：每击杀两名玩家获得TNT
            if (killCount >= 2) {
                killCount = 0;
                // 保存到配置文件
                EndBattle.getInstance().getConfig().set("creeper." + player.getUniqueId() + ".killCount", killCount);
                EndBattle.getInstance().saveConfig();
                primarySkill();
            }
            
            // 技能三：累计击杀6名玩家触发
            if (totalKills >= 6) {
                tertiarySkill();
                totalKills = 0;
                // 保存到配置文件
                EndBattle.getInstance().getConfig().set("creeper." + player.getUniqueId() + ".totalKills", totalKills);
                EndBattle.getInstance().saveConfig();
            }
        }
    }

    @EventHandler
    public void handleRespawn(PlayerRespawnEvent event) {

        if (!getSelect()) {
            return;
        }
        // 检查是否是当前职业
        if (event.getPlayer() == player) {
            // 重置击杀计数并给予TNT
            killCount = 0;
            totalKills = 0;
            //保存到配置文件
            EndBattle.getInstance().getConfig().set("creeper." + player.getUniqueId() + ".killCount", 0);
            EndBattle.getInstance().getConfig().set("creeper." + player.getUniqueId() + ".totalKills", 0);
            ItemStack tnt = new ItemStack(Material.TNT);
            player.getInventory().setItem(TNT_SLOT, tnt);
        }
    }

    @EventHandler
    public void handleSnowballHit(ProjectileHitEvent event) {

        if (!getSelect()) {
            return;
        }
        if (event.getEntity() instanceof Snowball && event.getEntity().getShooter() == player) {
            Location hitLoc = event.getEntity().getLocation();
            TNTPrimed tnt = hitLoc.getWorld().spawn(hitLoc, TNTPrimed.class);
            tnt.setFuseTicks(20); // 1秒后爆炸
        }
    }

    @Override
    public void passiveSkill() {
        // 被动技能在死亡事件中实现
    }

    @Override
    public void primarySkill() {
        // 检查TNT数量上限
        ItemStack currentTNT = player.getInventory().getItem(TNT_SLOT);
        if (currentTNT == null) {
            currentTNT = new ItemStack(Material.TNT);
            player.getInventory().setItem(TNT_SLOT, currentTNT);
        } else if (currentTNT.getAmount() < 2) {
            currentTNT.setAmount(currentTNT.getAmount() + 1);
        }
    }

    @Override
    public void secondarySkill() {
        // 雪球（通过商店购买获得）
        ItemStack snowball = new ItemStack(Material.SNOWBALL);
        player.getInventory().setItem(SNOWBALL_SLOT, snowball);
    }

    @Override
    public void tertiarySkill() {
        // 力量药水效果
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 80, 0));
        
        // 每秒刷新一个TNT
        BukkitTask task = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (count >= 4) {
                    this.cancel();
                    return;
                }
                primarySkill();
                count++;
            }
        }.runTaskTimer(com.endBattle.EndBattle.getInstance(), 0L, 20L);
        addTask(task);
    }

    @Override
    public List<ShopItem> shopItems() {
        List<ShopItem> items = new ArrayList<>();
        
        // TNT商店物品
        ShopItem tnt = new ShopItem();
        tnt.setMaterial(Material.TNT);
        tnt.setAmount(1);
        tnt.setMoney(Money.GOLD);
        tnt.setPrice(5);
        tnt.setLimit(1);
        
        // 雪球商店物品
        ShopItem snowball = new ShopItem();
        snowball.setMaterial(Material.SNOWBALL);
        snowball.setAmount(1);
        snowball.setMoney(Money.GOLD);
        snowball.setPrice(5);
        snowball.setLimit(1);
        
        items.add(tnt);
        items.add(snowball);
        return items;
    }
}