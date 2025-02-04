package com.endBattle.game.classes;

import com.endBattle.EndBattle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.endBattle.game.shop.item.Money;
import com.endBattle.game.shop.item.ShopItem;

import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.List;

public class Zombie extends Clazz {

    private Player player;
    private int killCount;
    private int totalKills;
    private static final int PEARL_SLOT = 1;  // 末影珍珠槽位
    private static final int SPEED_POTION_SLOT = 7;  // 速度药水槽位
    private static final int STRENGTH_POTION_SLOT = 8;  // 力量药水槽位

    public Zombie(Player player) {
        super("zombie", "僵尸", player);
        this.player = player;

        // 从配置文件中读取killCount和totalKills的值
        this.killCount = EndBattle.getInstance().getConfig().getInt("zombie." + player.getUniqueId() + ".killCount", 0);
        this.totalKills = EndBattle.getInstance().getConfig().getInt("zombie." + player.getUniqueId() + ".totalKills", 0);

    }

    @EventHandler
    public void handleMove(PlayerMoveEvent event) {

        if (!getSelect()) {
            return;
        }
        if (event.getPlayer() == player) {
            passiveSkill();
        }
    }

    @EventHandler
    public void handleRespawn(PlayerRespawnEvent event) {

        if (!getSelect()) {
            return;
        }
        if (event.getPlayer() == player) {
            // 重生时重置击杀计数并给予末影珍珠
            killCount = 0;
            totalKills = 0;

            // 保存到配置文件
            EndBattle.getInstance().getConfig().set("zombie." + player.getUniqueId() + ".killCount", killCount);
            EndBattle.getInstance().getConfig().set("zombie." + player.getUniqueId() + ".totalKills", totalKills);
            EndBattle.getInstance().saveConfig();

            ItemStack pearl = new ItemStack(Material.ENDER_PEARL);
            player.getInventory().setItem(PEARL_SLOT, pearl);
        }
    }

    @EventHandler
    public void handleKill(PlayerDeathEvent event) {

        if (!getSelect()) {
            return;
        }
        if (event.getEntity().getKiller() == player) {
            killCount++;
            totalKills++;

            // 保存到配置文件
            EndBattle.getInstance().getConfig().set("zombie." + player.getUniqueId() + ".killCount", killCount);
            EndBattle.getInstance().getConfig().set("zombie." + player.getUniqueId() + ".totalKills", totalKills);
            EndBattle.getInstance().saveConfig();

            if (killCount == 2) {
                killCount = 0;
                // 保存到配置文件
                EndBattle.getInstance().getConfig().set("zombie." + player.getUniqueId() + ".killCount", killCount);
                EndBattle.getInstance().saveConfig();
                primarySkill();
            }

            if (totalKills >= 6) {
                tertiarySkill();
                totalKills = 0;
                // 保存到配置文件
                EndBattle.getInstance().getConfig().set("zombie." + player.getUniqueId() + ".totalKills", totalKills);
                EndBattle.getInstance().saveConfig();
            }
        }
    }
    
    @Override
    public void passiveSkill() {
        // 检查第一格是否有羊毛
        ItemStack firstSlot = player.getInventory().getItem(0);
        if (firstSlot != null && firstSlot.getType().name().endsWith("_WOOL")) {
            Location playerLoc = player.getLocation();
            Location targetLoc = new Location(player.getWorld(), playerLoc.getX(), 122, playerLoc.getZ());
            if (targetLoc.getBlock().getType() == Material.AIR) {
                targetLoc.getBlock().setType(firstSlot.getType());
                firstSlot.setAmount(firstSlot.getAmount() - 1);
            }
        }
    }

    @Override
    public void primarySkill() {
        // 检查末影珍珠数量上限
        ItemStack currentPearls = player.getInventory().getItem(PEARL_SLOT);
        if (currentPearls == null) {
            currentPearls = new ItemStack(Material.ENDER_PEARL);
            player.getInventory().setItem(PEARL_SLOT, currentPearls);
        } else if (currentPearls.getAmount() < 2) {
            currentPearls.setAmount(currentPearls.getAmount() + 1);
        }
    }

    @Override
    public void secondarySkill() {
        // 速度二效果药水（通过商店购买获得）
        ItemStack speedPotion = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) speedPotion.getItemMeta();
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1), true);
        speedPotion.setItemMeta(potionMeta);
        player.getInventory().setItem(SPEED_POTION_SLOT, speedPotion);
    }

    @Override
    public void tertiarySkill() {
        // 力量三效果药水
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 80, 2));
        
        // 添加定时任务管理
        BukkitTask task = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (count >= 4) { // 4秒后停止
                    this.cancel();
                    return;
                }
                // 给周围敌人施加黑暗效果
                player.getNearbyEntities(10, 10, 10).stream()
                        .filter(entity -> entity instanceof Player)
                        .map(entity -> (Player) entity)
                        .filter(nearbyPlayer -> !nearbyPlayer.getScoreboardTags().contains(player.getScoreboardTags().iterator().next()))
                        .forEach(enemy -> enemy.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 80, 0)));
                count++;
            }
        }.runTaskTimer(com.endBattle.EndBattle.getInstance(), 0L, 20L);
        addTask(task);
    }
    @Override
    public List<ShopItem> shopItems() {
        List<ShopItem> items = new ArrayList<>();
        
        // 末影珍珠商店物品
        ShopItem pearl = new ShopItem();
        pearl.setMaterial(Material.ENDER_PEARL);
        pearl.setAmount(1);
        pearl.setMoney(Money.GOLD);
        pearl.setPrice(10);
        pearl.setLimit(1);
        
        // 速度药水商店物品
        ShopItem speedPotion = new ShopItem();
        speedPotion.setMaterial(Material.POTION);
        speedPotion.setAmount(1);
        speedPotion.setMoney(Money.IRON);
        speedPotion.setPrice(20);
        speedPotion.setLimit(1);
        
        items.add(pearl);
        items.add(speedPotion);
        return items;
    }
}

