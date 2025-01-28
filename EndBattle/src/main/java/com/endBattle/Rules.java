package com.endBattle;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.bukkit.entity.EntityType.*;

public class Rules implements Listener {
    private static final Set<Material> ALLOWED_BLOCKS = new HashSet<>();
    private static final Set<Material> ALLOWED_ITEMS = new HashSet<>();

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
        ALLOWED_BLOCKS.add(Material.RED_BANNER);
        ALLOWED_BLOCKS.add(Material.YELLOW_BANNER);
        ALLOWED_BLOCKS.add(Material.BLUE_BANNER);
        ALLOWED_BLOCKS.add(Material.GREEN_BANNER);
        ALLOWED_BLOCKS.add(Material.OBSIDIAN);
        ALLOWED_BLOCKS.add(Material.FIRE);
        ALLOWED_BLOCKS.add(Material.TNT);
        ALLOWED_BLOCKS.add(Material.COBWEB);
        ALLOWED_BLOCKS.add(Material.IRON_BARS);

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

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material blockType = event.getBlock().getType();

        if (!ALLOWED_BLOCKS.contains(blockType)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block placedBlock = event.getBlock();
        Material blockType = placedBlock.getType();

        if (blockType == Material.TNT) {
            placedBlock.setType(Material.AIR);
            placedBlock.getWorld().spawn(placedBlock.getLocation(), org.bukkit.entity.TNTPrimed.class);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> {
            Material blockType = block.getType();
            return !ALLOWED_BLOCKS.contains(blockType);
        });
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.EnderDragon) {
            Material blockType = event.getBlock().getType();
            if (!ALLOWED_BLOCKS.contains(blockType)) {
                event.setCancelled(true);
            }
        }
    }

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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.CHEST || event.getInventory().getType() == InventoryType.ENDER_CHEST) {
            ItemStack item = event.getCurrentItem();

            if (item != null && !ALLOWED_ITEMS.contains(item.getType())) {
                event.setCancelled(true);
            }
            //取消玩家点击装备栏与第八第九物品栏事件, 将技能二的物品上限设置为2
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
        }
    }

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
}
