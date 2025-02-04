package com.endBattle.game.classes;

import com.endBattle.EndBattle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.endBattle.game.shop.item.Money;
import com.endBattle.game.shop.item.ShopItem;

import java.util.ArrayList;
import java.util.List;
/**
 * 蜘蛛类
 */
public class Spider extends Clazz {

    private Player player;
    private int killCount;
    private int totalKills;
    private static final int COBWEB_SLOT = 1;    // 蜘蛛网槽位
    private static final int STICK_SLOT = 7;      // 木棒槽位
    private static final int SPECIAL_WEB_SLOT = 8; // 特殊蜘蛛网槽位

    public Spider(Player player) {
        super("spider", "蜘蛛", player);
        this.player = player;
        // 从配置文件中读取killCount和totalKills的值
        this.killCount = EndBattle.getInstance().getConfig().getInt("spider." + player.getUniqueId() + ".killCount");
        this.totalKills = EndBattle.getInstance().getConfig().getInt("spider." + player.getUniqueId() + ".totalKills");
    }

    @EventHandler
    public void handleMove(PlayerMoveEvent event) {
        if (!getSelect()) return;
        
        if (event.getPlayer() == player) {
            // 检查是否在蜘蛛网中
            if (event.getTo().getBlock().getType() == Material.COBWEB) {
                passiveSkill();
            }
            
            // 检查是否在墙面前
            Block block = event.getTo().getBlock();
            if (player.isSneaking() && (
                block.getRelative(1, 0, 0).getType().isSolid() ||
                block.getRelative(-1, 0, 0).getType().isSolid() ||
                block.getRelative(0, 0, 1).getType().isSolid() ||
                block.getRelative(0, 0, -1).getType().isSolid()
            )) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 0));
            }
        }
    }

    @EventHandler
    public void handleDeath(PlayerDeathEvent event) {
        if (!getSelect()) return;

        if (event.getEntity().getKiller() == player) {
            killCount++;
            totalKills++;
            // 保存到配置文件
            EndBattle.getInstance().getConfig().set("skeleton." + player.getUniqueId() + ".killCount", killCount);
            EndBattle.getInstance().getConfig().set("skeleton." + player.getUniqueId() + ".totalKills", totalKills);
            EndBattle.getInstance().saveConfig();
            
            // 技能一：每击杀两名玩家获得蜘蛛网
            if (killCount >= 2) {
                killCount = 0;
                // 保存到配置文件
                EndBattle.getInstance().getConfig().set("spider." + player.getUniqueId() + ".killCount", killCount);
                EndBattle.getInstance().saveConfig();
                primarySkill();
            }
            
            // 技能三：累计击杀5名玩家触发
            if (totalKills >= 5) {
                tertiarySkill();
                totalKills = 0;
                // 保存到配置文件
                EndBattle.getInstance().getConfig().set("spider." + player.getUniqueId() + ".totalKills", totalKills);
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
            // 保存到配置文件
            EndBattle.getInstance().getConfig().set("spider." + player.getUniqueId() + ".killCount", 0);
            EndBattle.getInstance().getConfig().set("spider." + player.getUniqueId() + ".totalKills", 0);
            EndBattle.getInstance().saveConfig();
            // 重生时给予蜘蛛网
            ItemStack cobweb = new ItemStack(Material.COBWEB);
            player.getInventory().setItem(COBWEB_SLOT, cobweb);
        }
    }

    @EventHandler
    public void handleBlockPlace(BlockPlaceEvent event) {
        if (!getSelect()) return;
        
        if (event.getPlayer() == player && event.getItemInHand().getItemMeta() != null 
            && event.getItemInHand().getItemMeta().getDisplayName().equals("特殊蜘蛛网")) {
            Location center = event.getBlock().getLocation();
            // 在半径5的范围内填充蜘蛛网
            for (int x = -5; x <= 5; x++) {
                for (int y = -5; y <= 5; y++) {
                    for (int z = -5; z <= 5; z++) {
                        if (Math.sqrt(x*x + y*y + z*z) <= 5) {
                            Location loc = center.clone().add(x, y, z);
                            if (loc.getBlock().getType().isAir()) {
                                loc.getBlock().setType(Material.COBWEB);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void passiveSkill() {
        // 给予玩家速度效果来抵消蜘蛛网的减速
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1, false, false));
    }

    @Override
    public void primarySkill() {
        // 检查蜘蛛网数量上限
        ItemStack currentWeb = player.getInventory().getItem(COBWEB_SLOT);
        if (currentWeb == null) {
            currentWeb = new ItemStack(Material.COBWEB);
            player.getInventory().setItem(COBWEB_SLOT, currentWeb);
        } else if (currentWeb.getAmount() < 2) {
            currentWeb.setAmount(currentWeb.getAmount() + 1);
        }
    }

    @Override
    public void secondarySkill() {
        // 给予剧毒木棒
        ItemStack stick = new ItemStack(Material.STICK);
        ItemMeta meta = stick.getItemMeta();
        meta.setDisplayName("剧毒木棒");
        stick.setItemMeta(meta);
        player.getInventory().setItem(STICK_SLOT, stick);
    }

    @Override
    public void tertiarySkill() {
        // 给予特殊蜘蛛网
        ItemStack specialWeb = new ItemStack(Material.COBWEB);
        ItemMeta meta = specialWeb.getItemMeta();
        meta.setDisplayName("特殊蜘蛛网");
        specialWeb.setItemMeta(meta);
        player.getInventory().setItem(SPECIAL_WEB_SLOT, specialWeb);
    }

    @Override
    public List<ShopItem> shopItems() {
        List<ShopItem> items = new ArrayList<>();
        
        // 蜘蛛网商店物品
        ShopItem cobweb = new ShopItem();
        cobweb.setMaterial(Material.COBWEB);
        cobweb.setAmount(1);
        cobweb.setMoney(Money.IRON);
        cobweb.setPrice(40);
        cobweb.setLimit(1);
        
        // 剧毒木棒商店物品
        ShopItem poisonStick = new ShopItem();
        poisonStick.setMaterial(Material.STICK);
        poisonStick.setAmount(1);
        poisonStick.setMoney(Money.DIAMOND);
        poisonStick.setPrice(3);
        poisonStick.setLimit(1);
        
        items.add(cobweb);
        items.add(poisonStick);
        return items;
    }
}