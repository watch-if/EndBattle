package com.endBattle.game.classes;

import com.endBattle.EndBattle;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.endBattle.game.shop.item.Money;
import com.endBattle.game.shop.item.ShopItem;

import org.bukkit.attribute.Attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * 史莱姆类
 */
public class Slime extends Clazz {
    private Player player;
    private int killCount;
    private int totalKills;
    private boolean totemUsed;
    private BukkitTask healingPotionTask;
    private static final int HEALING_POTION_SLOT = 1;    // 治疗药水槽位
    private static final int SPLASH_POTION_SLOT = 7;     // 喷溅药水槽位
    private static final int HEAD_SLOT = 8;              // 头颅槽位

    public Slime(Player player) {
        super("slime", "史莱姆", player);
        this.player = player;
        // 从配置文件中读取killCount和totalKills的值
        this.killCount = EndBattle.getInstance().getConfig().getInt("slime." + player.getUniqueId() + ".killCount");
        this.totalKills = EndBattle.getInstance().getConfig().getInt("slime." + player.getUniqueId() + ".totalKills");
        this.totemUsed = false;
    }

    @EventHandler
    public void handleDeath(PlayerDeathEvent event) {
        if (!getSelect()) return;

        if (event.getEntity().getKiller() == player) {
            totalKills++;
            // 保存到配置文件
            EndBattle.getInstance().getConfig().set("slime." + player.getUniqueId() + ".totalKills", totalKills);
            EndBattle.getInstance().saveConfig();
            
            // 技能三：累计击杀6名玩家触发
            if (totalKills >= 6) {
                tertiarySkill();
                totalKills = 0;
                //保存到配置文件
                EndBattle.getInstance().getConfig().set("slime." + player.getUniqueId() + ".totalKills", totalKills);
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
            EndBattle.getInstance().getConfig().set("slime." + player.getUniqueId() + ".killCount", killCount);
            EndBattle.getInstance().getConfig().set("slime." + player.getUniqueId() + ".totalKills", totalKills);
            EndBattle.getInstance().saveConfig();
            totemUsed = false;
            
            // 重生时给予不死图腾和治疗药水
            giveTotem();
            giveHealingPotion();
        }
    }

    @EventHandler
    public void handleTotemUse(EntityResurrectEvent event) {
        if (!getSelect()) return;
        
        if (event.getEntity() == player && !totemUsed) {
            totemUsed = true;
            // 触发不死图腾后生命上限减半
            double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth / 2);
        }
    }

    @EventHandler
    public void handlePotionUse(PlayerInteractEvent event) {
        if (!getSelect() || event.getPlayer() != player) return;
        
        ItemStack item = event.getItem();
        if (item != null && item.getType() == Material.POTION) {
            // 为周围非队友玩家施加减速效果
            for (Player nearby : player.getWorld().getPlayers()) {
                if (nearby != player && !isTeammate(nearby) && 
                    nearby.getLocation().distance(player.getLocation()) <= 10) {
                    nearby.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 0));
                }
            }
            
            // 40秒后刷新药水
            healingPotionTask = new BukkitRunnable() {
                @Override
                public void run() {
                    giveHealingPotion();
                }
            }.runTaskLater(com.endBattle.EndBattle.getInstance(), 800L); // 40秒 = 800tick
            addTask(healingPotionTask);
        }
    }

    @EventHandler
    public void handleHeadPlace(BlockPlaceEvent event) {
        if (!getSelect()) return;
        
        if (event.getPlayer() == player && event.getItemInHand().getType() == Material.PLAYER_HEAD) {
            event.setCancelled(true);
            ItemStack head = event.getItemInHand();
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null && meta.getOwningPlayer() != null) {
                Player target = meta.getOwningPlayer().getPlayer();
                if (target != null && isTeammate(target)) {
                    target.teleport(player.getLocation());
                }
            }
        }
    }

    private void giveTotem() {
        player.getInventory().addItem(new ItemStack(Material.TOTEM_OF_UNDYING));
    }

    private void giveHealingPotion() {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 80, 0), true);
        potion.setItemMeta(meta);
        player.getInventory().setItem(HEALING_POTION_SLOT, potion);
    }

    private void giveSplashPotion() {
        ItemStack potion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 1, 0), true);
        potion.setItemMeta(meta);
        player.getInventory().setItem(SPLASH_POTION_SLOT, potion);
    }

    private void giveTeammateHeads() {
        for (Player p : player.getWorld().getPlayers()) {
            if (p != player && isTeammate(p)) {
                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) head.getItemMeta();
                meta.setOwningPlayer(p);
                head.setItemMeta(meta);
                player.getInventory().addItem(head);
            }
        }
    }

    @Override
    public void passiveSkill() {
        // 被动技能在重生和图腾使用时处理
    }

    @Override
    public void primarySkill() {
        // 给予治疗药水
        giveHealingPotion();
    }

    @Override
    public void secondarySkill() {
        // 给予瞬间治疗喷溅药水
        giveSplashPotion();
    }

    @Override
    public void tertiarySkill() {
        // 给予队友头颅
        giveTeammateHeads();
    }

    @Override
    public List<ShopItem> shopItems() {
        List<ShopItem> items = new ArrayList<>();
        
        // 瞬间治疗喷溅药水商店物品
        ShopItem splashPotion = new ShopItem();
        splashPotion.setMaterial(Material.SPLASH_POTION);
        splashPotion.setAmount(1);
        splashPotion.setMoney(Money.IRON);
        splashPotion.setPrice(25);
        splashPotion.setLimit(1);
        
        items.add(splashPotion);
        return items;
    }

    private boolean isTeammate(Player other) {
        // TODO: 实现队友检查逻辑
        return false;
    }
}