package com.endBattle.game.classes;

import javax.annotation.Nullable;
// 导入Player类
import org.bukkit.entity.Player;
import java.util.HashMap;

/**
 * 职业管理器
 */
public class ClassesManager {

    private HashMap<String, Class<? extends Clazz>> classes = new HashMap<>();  // 初始化 HashMap
    // 新增：存储玩家和职业实例的映射
    private HashMap<String, Clazz> playerClasses = new HashMap<>();
    
    /**
     * 注册职业
     *
     * @param clazz 职业类
     * @see Clazz
     */
    public void register(String name, Class<? extends Clazz> clazz) {
        classes.put(name, clazz);
    }

    @Nullable
    public Class<? extends Clazz> getClassType(String name) {
        return classes.get(name);
    }
    /**
     * 获取玩家当前的职业实例
     * @param player 目标玩家
     * @return 职业实例，如果没有则返回null
     */
    @Nullable
    public Clazz getPlayerClass(Player player) {
        return playerClasses.get(player.getUniqueId().toString());
    }

    /**
     * 设置玩家的职业实例
     * @param player 目标玩家
     * @param clazz 职业实例
     */
    public void setPlayerClass(Player player, Clazz clazz) {
        playerClasses.put(player.getUniqueId().toString(), clazz);
    }

    /**
     * 移除玩家的职业实例
     * @param player 目标玩家
     */
    public void removePlayerClass(Player player) {
        playerClasses.remove(player.getUniqueId().toString());
    }
}
