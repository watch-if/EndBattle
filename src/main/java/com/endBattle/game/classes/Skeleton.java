package com.endBattle.game.classes;

import com.endBattle.EndBattle;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.endBattle.game.shop.item.Money;
import com.endBattle.game.shop.item.ShopItem;

import java.util.ArrayList;
import java.util.List;

public class Skeleton extends Clazz {
    private Player player;
    private int killCount;
    private int totalKills;
    private static final int BOW_SLOT = 0;  // 弓箭槽位
    private static final int FIREWORK_ROCKET = 1;  // 弩槽位
    private static final int HARM_ARROW_SLOT = 7;  // 瞬间伤害箭槽位
    private static final int SONIC_BOOM_SLOT = 8;  // 风弹槽位

    public Skeleton(Player player) {
        super("skeleton", "骷髅", player);
        this.player = player;
        //从配置文件中读取killCount和totalkills的值
        this.killCount = EndBattle.getInstance().getConfig().getInt("skeleton." + player.getUniqueId() + ".killCount", 0);
        this.totalKills = EndBattle.getInstance().getConfig().getInt("skeleton." + player.getUniqueId() + ".totalKills", 0);
    }

    @EventHandler
    public void handleRespawn(PlayerRespawnEvent event) {

        if (!getSelect()) {
            return;
        }
        // 检查是否是当前职业
        if (event.getPlayer() == player) {
            killCount = 0;
            totalKills = 0;
            //保存到配置文件
            EndBattle.getInstance().getConfig().set("skeleton." + player.getUniqueId() + ".killCount", 0);
            EndBattle.getInstance().getConfig().set("skeleton." + player.getUniqueId() + ".totalKills", 0);
            EndBattle.getInstance().saveConfig();
            giveInfinityBow();
            giveFireworkRocket();
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
            //保存到配置文件
            EndBattle.getInstance().getConfig().set("skeleton." + player.getUniqueId() + ".killCount", 0);
            EndBattle.getInstance().getConfig().set("skeleton." + player.getUniqueId() + ".totalKills", 0);
            EndBattle.getInstance().saveConfig();
            
            // 技能一：每击杀两名玩家获得烟花弩
            if (killCount >= 2) {
                killCount = 0;
                //保存到配置文件
                EndBattle.getInstance().getConfig().set("skeleton." + player.getUniqueId() + ".killCount", killCount);
                EndBattle.getInstance().saveConfig();
                primarySkill();
            }
            
            // 技能三：累计击杀5名玩家获得风弹
            if (totalKills >= 5) {
                tertiarySkill();
                totalKills = 0;
                // 保存到配置文件
                EndBattle.getInstance().getConfig().set("skeleton." + player.getUniqueId() + ".totalKills", 0);
                EndBattle.getInstance().saveConfig();
            }
        }
    }

    private void giveInfinityBow() {
        // 创建无限附魔弓
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        if (bowMeta != null) {
            bowMeta.addEnchant(Enchantment.INFINITY, 1, true);
            bow.setItemMeta(bowMeta);
        }
        player.getInventory().setItem(BOW_SLOT, bow);
        
        // 给予一支箭
        player.getInventory().addItem(new ItemStack(Material.ARROW));
    }

    private void giveFireworkRocket() {
        // 创建烟花火箭
        ItemStack fireworkRocket = new ItemStack(Material.FIREWORK_ROCKET);
        FireworkMeta fireworkMeta = (FireworkMeta) fireworkRocket.getItemMeta();

        if (fireworkMeta != null) {
            // 创建烟花效果
            FireworkEffect effect = FireworkEffect.builder()
                    .with(FireworkEffect.Type.CREEPER)  // 设置爆炸效果类型为苦力怕状
                    .withTrail()  // 添加拖曳痕迹
                    .build();

            // 将烟花效果添加到烟花火箭
            fireworkMeta.addEffect(effect);
            fireworkMeta.setPower(1);  // 设置烟花火箭的威力

            fireworkRocket.setItemMeta(fireworkMeta);
        }

        // 将烟花火箭添加到玩家的物品栏中
        player.getInventory().addItem(fireworkRocket);
    }

    @Override
    public void passiveSkill() {
        // 被动技能在重生时已实现
    }

    @Override
    public void primarySkill() {
        // 检查烟花弩数量上限
        ItemStack currentCrossbow = player.getInventory().getItem(FIREWORK_ROCKET);
        if (currentCrossbow == null) {
            giveFireworkRocket();
        } else if (currentCrossbow.getAmount() < 2) {
            currentCrossbow.setAmount(currentCrossbow.getAmount() + 1);
        }
    }

    @Override
    public void secondarySkill() {
        // 瞬间伤害箭（通过商店购买获得）
        ItemStack harmArrow = new ItemStack(Material.TIPPED_ARROW);
        // TODO: 设置箭的药水效果为瞬间伤害
        player.getInventory().setItem(HARM_ARROW_SLOT, harmArrow);
    }

    @Override
    public void tertiarySkill() {
        // 给予风弹（声波）
        ItemStack sonicBoom = new ItemStack(Material.DRAGON_BREATH);
        ItemMeta meta = sonicBoom.getItemMeta();
        if (meta != null) {
            // 使用 Component 替代已弃用的 setDisplayName 方法
            meta.displayName(net.kyori.adventure.text.Component.text("风弹").color(net.kyori.adventure.text.format.NamedTextColor.AQUA));
            sonicBoom.setItemMeta(meta);
        }
        player.getInventory().setItem(SONIC_BOOM_SLOT, sonicBoom);
    }

    @Override
    public List<ShopItem> shopItems() {
        List<ShopItem> items = new ArrayList<>();
        
        // 烟花弩商店物品
        ShopItem crossbow = new ShopItem();
        crossbow.setMaterial(Material.FIREWORK_ROCKET);
        crossbow.setAmount(1);
        crossbow.setMoney(Money.DIAMOND);
        crossbow.setPrice(1);
        crossbow.setLimit(1);
        
        // 瞬间伤害箭商店物品
        ShopItem harmArrow = new ShopItem();
        harmArrow.setMaterial(Material.TIPPED_ARROW);
        harmArrow.setAmount(1);
        harmArrow.setMoney(Money.IRON);
        harmArrow.setPrice(40);
        harmArrow.setLimit(2);
        
        items.add(crossbow);
        items.add(harmArrow);
        return items;
    }
}