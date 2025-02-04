package com.endBattle.game.Gaming;

import com.endBattle.EndBattle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Rules implements Listener {

    private static final Set<Material> ALLOWED_BLOCKS = new HashSet<>();
    private static final Set<Material> ALLOWED_ITEMS = new HashSet<>();
    private final Map<Player, Boolean> isAttacked = new HashMap<>();

    static {
        // 可破坏方块白名单
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
        ALLOWED_BLOCKS.add(Material.RED_BANNER);
        ALLOWED_BLOCKS.add(Material.YELLOW_BANNER);
        ALLOWED_BLOCKS.add(Material.BLUE_BANNER);
        ALLOWED_BLOCKS.add(Material.GREEN_BANNER);
        ALLOWED_BLOCKS.add(Material.OBSIDIAN);
        ALLOWED_BLOCKS.add(Material.FIRE);
        ALLOWED_BLOCKS.add(Material.TNT);
        ALLOWED_BLOCKS.add(Material.COBWEB);
        ALLOWED_BLOCKS.add(Material.IRON_BARS);
        ALLOWED_BLOCKS.add(Material.GLASS);

        // 可丢弃方块白名单
        ALLOWED_ITEMS.add(Material.RED_WOOL);
        ALLOWED_ITEMS.add(Material.YELLOW_WOOL);
        ALLOWED_ITEMS.add(Material.BLUE_WOOL);
        ALLOWED_ITEMS.add(Material.GREEN_WOOL);
        ALLOWED_ITEMS.add(Material.OAK_PLANKS);
        ALLOWED_ITEMS.add(Material.END_STONE);
        ALLOWED_ITEMS.add(Material.OBSIDIAN);
        ALLOWED_ITEMS.add(Material.WOODEN_SWORD);
        ALLOWED_ITEMS.add(Material.STONE_SWORD);
        ALLOWED_ITEMS.add(Material.DIAMOND_SWORD);
        ALLOWED_ITEMS.add(Material.WOODEN_PICKAXE);
        ALLOWED_ITEMS.add(Material.STONE_PICKAXE);
        ALLOWED_ITEMS.add(Material.IRON_PICKAXE);
        ALLOWED_ITEMS.add(Material.DIAMOND_PICKAXE);
        ALLOWED_ITEMS.add(Material.DIAMOND_AXE);
        ALLOWED_ITEMS.add(Material.BOW);
        ALLOWED_ITEMS.add(Material.ARROW);
        ALLOWED_ITEMS.add(Material.SHEARS);
        ALLOWED_ITEMS.add(Material.IRON_INGOT);
        ALLOWED_ITEMS.add(Material.GOLD_INGOT);
        ALLOWED_ITEMS.add(Material.DIAMOND);
        ALLOWED_ITEMS.add(Material.LAPIS_LAZULI);
    }

    // 取消玩家攻击CD
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(100.0);
        addNightVision(player);
        addRegeneration(player);
    }

    // 添加夜视效果
    private void addNightVision(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, true, false));
    }

    // 添加生命回复效果
    private void addRegeneration(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0, true, false));
    }

    // 取消玩家破坏方块事件
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material blockType = event.getBlock().getType();

        if (!ALLOWED_BLOCKS.contains(blockType)) {
            event.setCancelled(true);
        }
    }

    // TNT放置方法
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block placedBlock = event.getBlock();
        Material blockType = placedBlock.getType();

        if (blockType == Material.TNT) {
            placedBlock.setType(Material.AIR);
            placedBlock.getWorld().spawn(placedBlock.getLocation(), org.bukkit.entity.TNTPrimed.class);
        }
    }

    // 防止爆炸破坏方块
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> {
            Material blockType = block.getType();
            // 临时移除玻璃，确保爆炸不会破坏它
            return !ALLOWED_BLOCKS.contains(blockType) || blockType == Material.GLASS;
        });
    }

    // 防止末地龙破坏方块
    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.EnderDragon) {
            Material blockType = event.getBlock().getType();
            if (!ALLOWED_BLOCKS.contains(blockType)) {
                event.setCancelled(true);
            }
        }
    }

    // 火焰弹释放方法
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getType() == Material.FIRE_CHARGE) {
            Fireball fireball = player.launchProjectile(Fireball.class);
            Vector direction = player.getLocation().getDirection().multiply(2);
            fireball.setVelocity(direction);
            itemInHand.setAmount(itemInHand.getAmount() - 1);
        }
    }

    // 防止丢弃方块
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.CHEST || event.getInventory().getType() == InventoryType.ENDER_CHEST) {
            ItemStack item = event.getCurrentItem();

            if (item != null && !ALLOWED_ITEMS.contains(item.getType())) {
                event.setCancelled(true);
            }
        }
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();

            if (item != null && isArmor(item)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();

        if (!ALLOWED_ITEMS.contains(item.getType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            ItemStack item = event.getItem().getItemStack();
            if (!ALLOWED_ITEMS.contains(item.getType()) && item.getAmount() > 2) {
                item.setAmount(2);
            }
        }
    }

    // 雪球释放方法
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Snowball) {
            Location location = event.getEntity().getLocation();
            if (location.getWorld() != null) {
                Block block = location.getBlock();
                block.setType(Material.TNT);
                TNTPrimed tnt = (TNTPrimed) location.getWorld().spawnEntity(location, EntityType.TNT);
                tnt.setFuseTicks(0);
            }
        }
    }

    // 防止村民被伤害
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.Villager) {
            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK &&
                    event.getEntity().getLastDamageCause() != null &&
                    event.getEntity().getLastDamageCause().getEntity() instanceof org.bukkit.entity.EnderDragon) {
                event.setCancelled(true);
            } else if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION &&
                    event instanceof EntityDamageByBlockEvent) {
                EntityDamageByBlockEvent blockEvent = (EntityDamageByBlockEvent) event;
                if (blockEvent.getDamager() instanceof TNTPrimed) {
                    event.setCancelled(true);
                }
            }
            if (event.getEntity() instanceof Villager) {
                event.setCancelled(true);
            }
        }

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            isAttacked.put(player, true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    isAttacked.put(player, false);
                }
            }.runTaskLater(EndBattle.getInstance(), 20L * 5); // 5秒后重置
        }
    }

    // 玩家死亡掉落物
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        List<ItemStack> drops = new ArrayList<>();

        for (ItemStack item : event.getDrops()) {
            Material type = item.getType();
            switch (type) {
                case IRON_INGOT:
                    int ironAmount = item.getAmount();
                    int ironToKeep = ironAmount / 2;
                    int ironToExpBottles = ironAmount - ironToKeep;
                    if (ironToExpBottles > 0) {
                        drops.add(new ItemStack(Material.EXPERIENCE_BOTTLE, ironToExpBottles));
                    }
                    if (ironToKeep > 0) {
                        drops.add(new ItemStack(Material.IRON_INGOT, ironToKeep));
                    }
                    break;
                case GOLD_INGOT:
                    int goldAmount = item.getAmount();
                    int goldToKeep = goldAmount / 2;
                    int goldToExpBottles = goldAmount - goldToKeep;
                    if (goldToExpBottles > 0) {
                        drops.add(new ItemStack(Material.EXPERIENCE_BOTTLE, goldToExpBottles * 2));
                    }
                    if (goldToKeep > 0) {
                        drops.add(new ItemStack(Material.GOLD_INGOT, goldToKeep));
                    }
                    break;
                case DIAMOND:
                case LAPIS_LAZULI:
                case ENCHANTED_BOOK:
                    drops.add(item);
                    break;
                default:
                    break;
            }
        }

        event.getDrops().clear();
        event.getDrops().addAll(drops);
    }

    // 防止玩家脱去盔甲
    private boolean isArmor(ItemStack item) {
        if (item == null) {
            return false;
        }
        Material type = item.getType();
        return type == Material.LEATHER_HELMET ||
                type == Material.LEATHER_CHESTPLATE ||
                type == Material.LEATHER_LEGGINGS ||
                type == Material.LEATHER_BOOTS ||
                type == Material.CHAINMAIL_HELMET ||
                type == Material.CHAINMAIL_CHESTPLATE ||
                type == Material.CHAINMAIL_LEGGINGS ||
                type == Material.CHAINMAIL_BOOTS ||
                type == Material.IRON_HELMET ||
                type == Material.IRON_CHESTPLATE ||
                type == Material.IRON_LEGGINGS ||
                type == Material.IRON_BOOTS ||
                type == Material.GOLDEN_HELMET ||
                type == Material.GOLDEN_CHESTPLATE ||
                type == Material.GOLDEN_LEGGINGS ||
                type == Material.GOLDEN_BOOTS ||
                type == Material.DIAMOND_HELMET ||
                type == Material.DIAMOND_CHESTPLATE ||
                type == Material.DIAMOND_LEGGINGS ||
                type == Material.DIAMOND_BOOTS;
    }

    // 防止玩家饥饿值减少
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
            ((Player) event.getEntity()).setFoodLevel(20);
        }
    }

    // 生命回复只在玩家不被攻击时生效
    @EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (isAttacked.getOrDefault(player, false)) {
                event.setCancelled(true);
            }
        }
    }

    //防止特定物品耐久值减少
    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        Material type = item.getType();

        if (type == Material.WOODEN_SWORD || type == Material.STONE_SWORD || type == Material.IRON_SWORD ||
                type == Material.GOLDEN_SWORD || type == Material.DIAMOND_SWORD || type == Material.NETHERITE_SWORD ||
                type == Material.WOODEN_AXE || type == Material.STONE_AXE || type == Material.IRON_AXE ||
                type == Material.GOLDEN_AXE || type == Material.DIAMOND_AXE || type == Material.NETHERITE_AXE ||
                type == Material.WOODEN_PICKAXE || type == Material.STONE_PICKAXE || type == Material.IRON_PICKAXE ||
                type == Material.GOLDEN_PICKAXE || type == Material.DIAMOND_PICKAXE || type == Material.NETHERITE_PICKAXE ||
                type == Material.BOW || type == Material.CROSSBOW) {
            event.setCancelled(true);
        }
    }
}
