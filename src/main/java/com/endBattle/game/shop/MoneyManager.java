package com.endBattle.game.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.endBattle.game.shop.item.Money;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MoneyManager {
    private static final MoneyManager instance = new MoneyManager();
    public final Map<UUID, Map<Money, Integer>> playerMoney = new HashMap<>();

    private MoneyManager() {}

    public static MoneyManager getInstance() {
        return instance;
    }

    /**
     * 检查玩家是否有足够的货币
     */
    public boolean hasMoney(Player player, Money type, int amount) {
        return getMoney(player, type) >= amount;
    }

    /**
     * 获取玩家某种货币的数量
     */
    public int getMoney(Player player, Money type) {
        // 检查玩家背包中对应物品的数量
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && isMoneyCurrency(item, type)) {
                count += item.getAmount();
            }
        }
        return count;
    }

    /**
     * 扣除玩家货币
     */
    public void removeMoney(Player player, Money type, int amount) {
        int remaining = amount;
        ItemStack[] contents = player.getInventory().getContents();
        
        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack item = contents[i];
            if (item != null && isMoneyCurrency(item, type)) {
                if (item.getAmount() > remaining) {
                    item.setAmount(item.getAmount() - remaining);
                    remaining = 0;
                } else {
                    remaining -= item.getAmount();
                    player.getInventory().setItem(i, null);
                }
            }
        }
    }

    /**
     * 添加货币到玩家背包
     */
    public void addMoney(Player player, Money type, int amount) {
        ItemStack moneyItem = createMoneyCurrency(type);
        moneyItem.setAmount(amount);
        player.getInventory().addItem(moneyItem);
    }

    /**
     * 检查物品是否为对应的货币类型
     */
    private boolean isMoneyCurrency(ItemStack item, Money type) {
        switch (type) {
            case IRON:
                return item.getType() == Material.IRON_INGOT;
            case GOLD:
                return item.getType() == Material.GOLD_INGOT;
            case DIAMOND:
                return item.getType() == Material.DIAMOND;
            default:
                return false;
        }
    }

    /**
     * 创建对应类型的货币物品
     */
    private ItemStack createMoneyCurrency(Money type) {
        Material material;
        switch (type) {
            case IRON:
                material = Material.IRON_INGOT;
                break;
            case GOLD:
                material = Material.GOLD_INGOT;
                break;
            case DIAMOND:
                material = Material.DIAMOND;
                break;
            default:
                throw new IllegalArgumentException("未知的货币类型");
        }
        return new ItemStack(material);
    }
}
