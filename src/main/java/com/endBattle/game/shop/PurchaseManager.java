package com.endBattle.game.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 购买记录管理器
 * 用于管理玩家在商店中的购买次数记录
 */
public class PurchaseManager {
    // 单例模式实例
    private static final PurchaseManager instance = new PurchaseManager();
    
    // 外层Map: 玩家UUID -> 内层Map
    // 内层Map: 物品Material -> 购买次数
    private final Map<UUID, Map<Material, Integer>> purchaseRecords = new HashMap<>();

    /**
     * 私有构造函数，防止外部实例化
     */
    private PurchaseManager() {}

    /**
     * 获取PurchaseManager的单例实例
     * @return PurchaseManager实例
     */
    public static PurchaseManager getInstance() {
        return instance;
    }

    /**
     * 获取玩家对特定物品的购买次数
     * @param player 要查询的玩家
     * @param material 要查询的物品类型
     * @return 玩家购买该物品的次数
     */
    public int getPurchaseCount(Player player, Material material) {
        return purchaseRecords
                .computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .getOrDefault(material, 0);
    }

    /**
     * 更新玩家对特定物品的购买次数（增加1次）
     * @param player 要更新的玩家
     * @param material 购买的物品类型
     */
    public void updatePurchaseCount(Player player, Material material) {
        Map<Material, Integer> playerRecords = purchaseRecords
                .computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        
        playerRecords.put(material, 
            playerRecords.getOrDefault(material, 0) + 1);
    }

    /**
     * 重置指定玩家的所有购买记录
     * @param player 要重置的玩家
     */
    public void resetPurchases(Player player) {
        purchaseRecords.remove(player.getUniqueId());
    }

    /**
     * 重置所有玩家的购买记录
     */
    public void resetAllPurchases() {
        purchaseRecords.clear();
    }
}