package com.endBattle.game.classes;

import com.endBattle.EndBattle;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import com.endBattle.game.shop.item.Money;
import com.endBattle.game.shop.item.ShopItem;
// 导入类 CC
import com.endBattle.utils.chat.CC;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;

/**
 * 铁傀儡类
 */
public class IronGolem extends Clazz {
    private Player player;
    private int killCount;
    private int totalKills;
    private static final int APPLE_SLOT = 1;      // 金苹果槽位
    private static final int STICK_SLOT = 7;      // 木棒槽位
    private static final int POTION_SLOT = 8;     // 药水槽位

    public IronGolem(Player player) {
        super("iron_golem", "铁傀儡", player);
        this.player = player;

        //从配置文件中读取killCount和totalkills的值
        this.killCount = EndBattle.getInstance().getConfig().getInt("iron_golem." + player.getUniqueId() + ".killCount", 0);
        this.totalKills = EndBattle.getInstance().getConfig().getInt("iron_golem." + player.getUniqueId() + ".totalKills", 0);
    }

    @EventHandler
    public void handleDeath(PlayerDeathEvent event) {
        if (!getSelect()) return;

        if (event.getEntity().getKiller() == player) {
            killCount++;
            totalKills++;
            //保存到配置文件
            EndBattle.getInstance().getConfig().set("iron_golem." + player.getUniqueId() + ".killCount", killCount);
            EndBattle.getInstance().getConfig().set("iron_golem." + player.getUniqueId() + ".totalKills", totalKills);
            EndBattle.getInstance().saveConfig();
            
            // 技能一：每击杀两名玩家获得金苹果
            if (killCount >= 2) {
                killCount = 0;
                //保存到配置文件
                EndBattle.getInstance().getConfig().set("iron_golem." + player.getUniqueId() + ".killCount", killCount);
                EndBattle.getInstance().saveConfig();
                primarySkill();
            }
            
            // 技能三：累计击杀6名玩家触发
            if (totalKills >= 6) {
                tertiarySkill();
                totalKills = 0;
                // 保存到配置文件
                EndBattle.getInstance().getConfig().set("iron_golem." + player.getUniqueId() + ".totalKills", totalKills);
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
            EndBattle.getInstance().getConfig().set("iron_golem." + player.getUniqueId() + ".killCount", killCount);
            EndBattle.getInstance().getConfig().set("iron_golem." + player.getUniqueId() + ".totalKills", totalKills);
            EndBattle.getInstance().saveConfig();
            // 重生时给予金苹果
            giveGoldenApple();
        }
    }

    @EventHandler
    public void handleInteract(PlayerInteractEntityEvent event) {
        if (!getSelect()) return;
        
        // 检查是否是用铁锭右键点击玩家
        if (event.getRightClicked() instanceof Player target) {
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            if (item.getType() == Material.IRON_INGOT) {
                // 检查是否是队友或自己
                if (target == player || isTeammate(target)) {
                    // 恢复一点血量并消耗一个铁锭
                    double health = target.getHealth();
                    if (health < target.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue()) {
                        target.setHealth(Math.min(health + 1, target.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue()));
                        item.setAmount(item.getAmount() - 1);
                    }
                }
            }
        }
    }

    private void giveGoldenApple() {
        ItemStack apple = new ItemStack(Material.GOLDEN_APPLE);
        player.getInventory().setItem(APPLE_SLOT, apple);
    }

    private void giveKnockbackStick() {
        ItemStack stick = new ItemStack(Material.STICK);
        ItemMeta meta = stick.getItemMeta();
        meta.displayName(CC.trans("&r击退木棒"));
        meta.addEnchant(Enchantment.KNOCKBACK, 3, true);
        stick.setItemMeta(meta);
        player.getInventory().setItem(STICK_SLOT, stick);
    }

    private void giveHealingAndStrengthPotion() {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 1, 0), true);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.STRENGTH, 80, 0), true);
        potion.setItemMeta(meta);
        player.getInventory().setItem(POTION_SLOT, potion);
    }

    @Override
    public void passiveSkill() {
        // 被动技能通过事件处理
    }

    @Override
    public void primarySkill() {
        // 给予金苹果（检查上限为1）
        ItemStack currentApple = player.getInventory().getItem(APPLE_SLOT);
        if (currentApple == null || currentApple.getAmount() < 1) {
            giveGoldenApple();
        }
    }

    @Override
    public void secondarySkill() {
        // 给予击退木棒
        giveKnockbackStick();
    }

    @Override
    public void tertiarySkill() {
        // 给予治疗与力量药水
        giveHealingAndStrengthPotion();
    }

    @Override
    public List<ShopItem> shopItems() {
        List<ShopItem> items = new ArrayList<>();
        
        // 击退木棒商店物品
        ShopItem stick = new ShopItem();
        stick.setMaterial(Material.STICK);
        stick.setAmount(1);
        stick.setMoney(Money.GOLD);
        stick.setPrice(10);
        stick.setLimit(1);
        
        items.add(stick);
        return items;
    }

    private boolean isTeammate(Player other) {
        // TODO: 实现队友检查逻辑
        return false;
    }
}