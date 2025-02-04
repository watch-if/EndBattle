package com.endBattle.game.classes;

import com.endBattle.EndBattle;
import com.endBattle.game.shop.item.Money;
import com.endBattle.game.shop.item.ShopItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

/**
 * 恶魂类
 */
public class Ghast extends Clazz {
    private Player player;
    private int killCount;
    private int totalKills;
    private static final int FIREBALL_SLOT = 1;    // 烈焰弹槽位
    private static final int JUMP_SLOT = 7;        // 跳跃药水槽位
    private static final int NIGHT_SLOT = 8;       // 夜视药水槽位

    public Ghast(Player player) {
        super("ghast", "恶魂", player);
        this.player = player;

        // 从配置文件中读取killCount和totalKills的值
        this.killCount = EndBattle.getInstance().getConfig().getInt("ghast." + player.getUniqueId() + ".killCount");
        this.totalKills = EndBattle.getInstance().getConfig().getInt("ghast." + player.getUniqueId() + ".totalKills");
    }

    @EventHandler
    public void handleDeath(PlayerDeathEvent event) {
        if (!getSelect()) return;

        if (event.getEntity().getKiller() == player) {
            killCount++;
            totalKills++;
            //保存到配置文件
            EndBattle.getInstance().getConfig().set("ghast." + player.getUniqueId() + ".killCount", killCount);
            EndBattle.getInstance().getConfig().set("ghast." + player.getUniqueId() + ".totalKills", totalKills);
            EndBattle.getInstance().saveConfig();
            
            // 技能一：每击杀两名玩家获得烈焰弹
            if (killCount >= 2) {
                killCount = 0;
                //保存到配置文件
                EndBattle.getInstance().getConfig().set("ghast." + player.getUniqueId() + ".killCount", killCount);
                EndBattle.getInstance().saveConfig();
                primarySkill();
            }
            
            // 技能三：累计击杀6名玩家触发
            if (totalKills >= 6) {
                tertiarySkill();
                totalKills = 0;
                // 保存到配置文件
                EndBattle.getInstance().getConfig().set("ghast." + player.getUniqueId() + ".totalKills", totalKills);
                EndBattle.getInstance().saveConfig();
            }
        }
    }

    @EventHandler
    public void handleRespawn(PlayerRespawnEvent event) {
        if (!getSelect()) return;
        
        if (event.getPlayer() == player) {
            killCount = 0;
            totalKills = 0;
            //保存到配置文件
            EndBattle.getInstance().getConfig().set("ghast." + player.getUniqueId() + ".killCount", killCount);
            EndBattle.getInstance().getConfig().set("ghast." + player.getUniqueId() + ".totalKills", totalKills);
            EndBattle.getInstance().saveConfig();

            // 重生时给予烈焰弹
            giveFireball();
            // 给予缓降效果
            passiveSkill();
        }
    }

    private void giveFireball() {
        ItemStack fireball = new ItemStack(Material.FIRE_CHARGE);
        player.getInventory().setItem(FIREBALL_SLOT, fireball);
    }

    private void giveJumpPotion() {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 20, 1), true); // 1秒 = 20tick
        potion.setItemMeta(meta);
        player.getInventory().setItem(JUMP_SLOT, potion);
    }

    private void giveNightVisionPotion() {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 0), true); // 5秒
        potion.setItemMeta(meta);
        player.getInventory().setItem(NIGHT_SLOT, potion);
        
        // 给予漂浮效果
        player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 0));
        
        // 每秒刷新技能一
        BukkitTask task = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (count >= 5) { // 5秒内执行5次
                    this.cancel();
                    return;
                }
                primarySkill();
                count++;
            }
        }.runTaskTimer(com.endBattle.EndBattle.getInstance(), 0L, 20L); // 20tick = 1秒
        addTask(task);
    }

    @Override
    public void passiveSkill() {
        // 给予缓降效果
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, Integer.MAX_VALUE, 1, false, false));
    }

    @Override
    public void primarySkill() {
        // 检查烈焰弹数量上限
        ItemStack currentFireball = player.getInventory().getItem(FIREBALL_SLOT);
        if (currentFireball == null) {
            giveFireball();
        } else if (currentFireball.getAmount() < 2) {
            currentFireball.setAmount(currentFireball.getAmount() + 1);
        }
    }

    @Override
    public void secondarySkill() {
        // 给予跳跃药水
        giveJumpPotion();
    }

    @Override
    public void tertiarySkill() {
        // 给予夜视药水并启动定时任务
        giveNightVisionPotion();
    }

    @Override
    public List<ShopItem> shopItems() {
        List<ShopItem> items = new ArrayList<>();
        
        // 烈焰弹商店物品
        ShopItem fireball = new ShopItem();
        fireball.setMaterial(Material.FIRE_CHARGE);
        fireball.setAmount(1);
        fireball.setMoney(Money.GOLD);
        fireball.setPrice(4);
        fireball.setLimit(1);
        
        // 跳跃药水商店物品
        ShopItem jumpPotion = new ShopItem();
        jumpPotion.setMaterial(Material.POTION);
        jumpPotion.setAmount(1);
        jumpPotion.setMoney(Money.IRON);
        jumpPotion.setPrice(20);
        jumpPotion.setLimit(1);
        
        items.add(fireball);
        items.add(jumpPotion);
        return items;
    }
}