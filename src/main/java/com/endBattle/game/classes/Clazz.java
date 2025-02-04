package com.endBattle.game.classes;

import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.endBattle.game.shop.item.ShopItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 职业类
 */
public abstract class Clazz implements Listener {
    protected Player player;
    protected int killCount;
    protected int totalKills;
    private String id;
    private String name;
    private Boolean select = false;
    
    public Clazz(String id, String name, Player player) {
        this.tasks = new ArrayList<>();
        this.id = id;
        this.name = name;
        this.player = player;
        this.killCount = 0;
        this.totalKills = 0;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public Boolean getSelect() {
        return select;
    }
    public void setSelect(Boolean select) {
        this.select = select;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public abstract void passiveSkill();        // 被动技能
    public abstract void primarySkill();        // 技能一
    public abstract void secondarySkill();      // 技能二
    public abstract void tertiarySkill(); 

    public abstract List<ShopItem> shopItems(); // 商店物品
    protected final List<BukkitTask> tasks;
    public Clazz(Player player) {
        this.player = player;
        this.tasks = new ArrayList<>();
    }
    
    /**
     * 取消所有与该职业相关的任务
     */
    public void cancelTasks() {
        for (BukkitTask task : tasks) {
            task.cancel();
        }
        tasks.clear();
    }

    /**
     * 添加一个任务到任务列表中
     * @param task 要添加的任务
     */
    protected void addTask(BukkitTask task) {
        tasks.add(task);
    }

    /**
     * 获取该职业的玩家
     * @return 玩家实例
     */
    public Player getPlayer() {
        return player;
    }
}
