package com.endBattle.game.classes;

import com.endBattle.EndBattle;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.endBattle.game.shop.item.Money;
import com.endBattle.game.shop.item.ShopItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 女巫类
 */
public class Witch extends Clazz {
    private Player player;
    private int killCount;
    private int totalKills;
    private static final int HARM_POTION_SLOT = 1;     // 瞬间伤害药水槽位
    private static final int POISON_POTION_SLOT = 7;   // 剧毒药水槽位
    private static final int HEAL_POTION_SLOT = 8;     // 治疗药水槽位

    public Witch(Player player) {
        super("witch", "女巫", player);
        this.player = player;
        // 从配置文件中读取killCount和totalKills的值
        this.killCount = EndBattle.getInstance().getConfig().getInt("witch." + player.getUniqueId() + ".killCount", 0);
        this.totalKills = EndBattle.getInstance().getConfig().getInt("witch." + player.getUniqueId() + ".totalKills", 0);
    }

    @EventHandler
    public void handleDeath(PlayerDeathEvent event) {
        if (!getSelect()) return;

        if (event.getEntity().getKiller() == player) {
            killCount++;
            totalKills++;
            //保存到配置文件
            EndBattle.getInstance().getConfig().set("witch." + player.getUniqueId() + ".killCount", killCount);
            EndBattle.getInstance().getConfig().set("witch." + player.getUniqueId() + ".totalKills", totalKills);
            EndBattle.getInstance().saveConfig();
            
            // 技能一：每击杀两名玩家获得瞬间伤害药水
            if (killCount >= 2) {
                killCount = 0;
                // 保存到配置文件
                EndBattle.getInstance().getConfig().set("witch." + player.getUniqueId() + ".killCount", killCount);
                EndBattle.getInstance().saveConfig();
                primarySkill();
            }
            
            // 技能三：累计击杀7名玩家触发
            if (totalKills >= 7) {
                tertiarySkill();
                totalKills = 0;
                //保存到配置文件
                EndBattle.getInstance().getConfig().set("witch." + player.getUniqueId() + ".totalKills", totalKills);
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
            EndBattle.getInstance().getConfig().set("witch." + player.getUniqueId() + ".killCount", killCount);
            EndBattle.getInstance().getConfig().set("witch." + player.getUniqueId() + ".totalKills", totalKills);
            EndBattle.getInstance().saveConfig();
            // 重生时给予瞬间伤害药水
            giveHarmingPotion();
        }
    }

    private void giveHarmingPotion() {
        ItemStack potion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setBasePotionType(PotionType.HARMING);
        potion.setItemMeta(meta);
        player.getInventory().setItem(HARM_POTION_SLOT, potion);
    }

    private void givePoisonPotion() {
        ItemStack potion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setBasePotionType(PotionType.POISON);
        potion.setItemMeta(meta);
        player.getInventory().setItem(POISON_POTION_SLOT, potion);
    }

    private void giveHealingPotion() {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setBasePotionType(PotionType.HEALING);
        potion.setItemMeta(meta);
        player.getInventory().setItem(HEAL_POTION_SLOT, potion);
    }

    @Override
    public void passiveSkill() {
        // 被动技能：开放炼药系统（通过商店实现）
    }

    @Override
    public void primarySkill() {
        // 检查瞬间伤害药水数量上限
        ItemStack currentPotion = player.getInventory().getItem(HARM_POTION_SLOT);
        if (currentPotion == null) {
            giveHarmingPotion();
        } else if (currentPotion.getAmount() < 2) {
            currentPotion.setAmount(currentPotion.getAmount() + 1);
        }
    }

    @Override
    public void secondarySkill() {
        // 给予剧毒药水
        givePoisonPotion();
    }

    @Override
    public void tertiarySkill() {
        // 给予治疗药水并启动定时任务
        giveHealingPotion();
        
        // 每2秒刷新技能一和技能二
        BukkitTask task = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (count >= 2) { // 4秒内执行2次
                    this.cancel();
                    return;
                }
                primarySkill();
                secondarySkill();
                count++;
            }
        }.runTaskTimer(com.endBattle.EndBattle.getInstance(), 0L, 40L); // 40tick = 2秒
        addTask(task);
    }

    @Override
    public List<ShopItem> shopItems() {
        List<ShopItem> items = new ArrayList<>();
        
        // 添加所有商店物品
        addShopItem(items, Material.GLASS_BOTTLE, Money.IRON, 20);
        addShopItem(items, Material.POTION, Money.IRON, 30);
        addShopItem(items, Material.GUNPOWDER, Money.GOLD, 10);
        addShopItem(items, Material.REDSTONE, Money.IRON, 30);
        addShopItem(items, Material.GLOWSTONE_DUST, Money.IRON, 30);
        addShopItem(items, Material.SPIDER_EYE, Money.IRON, 30);
        addShopItem(items, Material.SUGAR, Money.IRON, 40);
        addShopItem(items, Material.RABBIT_FOOT, Money.IRON, 40);
        addShopItem(items, Material.BLAZE_POWDER, Money.IRON, 40);
        addShopItem(items, Material.GLISTERING_MELON_SLICE, Money.IRON, 40);
        addShopItem(items, Material.FERMENTED_SPIDER_EYE, Money.IRON, 40);
        addShopItem(items, Material.GHAST_TEAR, Money.IRON, 40);
        addShopItem(items, Material.MAGMA_CREAM, Money.IRON, 40);
        addShopItem(items, Material.PUFFERFISH, Money.IRON, 40);
        addShopItem(items, Material.GOLDEN_CARROT, Money.IRON, 40);
        addShopItem(items, Material.TURTLE_HELMET, Money.GOLD, 10);
        addShopItem(items, Material.PHANTOM_MEMBRANE, Money.IRON, 40);
        
        return items;
    }

    private void addShopItem(List<ShopItem> items, Material material, Money money, int price) {
        ShopItem item = new ShopItem();
        item.setMaterial(material);
        item.setAmount(1);
        item.setMoney(money);
        item.setPrice(price);
        item.setLimit(1);
        items.add(item);
    }
}