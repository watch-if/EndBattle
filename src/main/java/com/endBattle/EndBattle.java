package com.endBattle;

import com.endBattle.game.Gaming.Enter;
import com.endBattle.game.Gaming.Gaming;
import com.endBattle.game.Gaming.Rules;
import com.endBattle.game.classes.ClassesManager;
import com.endBattle.game.classes.Clazz;
import com.endBattle.game.classes.*;
import com.endBattle.game.mainshop.OpenThisShop;
import com.endBattle.game.shop.PurchaseManager;
import com.endBattle.commands.ShopCommand;
import com.endBattle.utils.chat.CC;
import com.endBattle.utils.gui.AbstractGui;
import com.endBattle.utils.gui.ButtonSet;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

/**
 * 终末之战插件主类
 */
public final class EndBattle extends JavaPlugin {
    private static EndBattle instance;
    private ClassesManager classesManager;
    private Gaming gaming;
    private Rules rules;
    private Enter enter;
    private OpenThisShop openThisShop;

    /**
     * 获取插件实例
     * @return 插件实例
     */
    public static EndBattle getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // 加载配置文件
        saveDefaultConfig();
        reloadConfig();
        
        // 初始化职业管理器
        classesManager = new ClassesManager();
        
        // 注册职业
        classesManager.register("zombie", Zombie.class);
        classesManager.register("witch", Witch.class);
        classesManager.register("spider", Spider.class);
        classesManager.register("slime", Slime.class);
        classesManager.register("skeleton", Skeleton.class);
        classesManager.register("creeper", Creeper.class);
        // ironGolem
        classesManager.register("iron_golem", IronGolem.class);
        // ghast
        classesManager.register("ghast", Ghast.class);

        // 初始化游戏管理器
        gaming = new Gaming();
        rules = new Rules();
        enter = new Enter();

        // 初始化商店管理器
        openThisShop = new OpenThisShop();

        // 注册商店指令
        getCommand("shop").setExecutor(new ShopCommand());
        getLogger().info("终末之战已启用");

        //注册监听器
        getServer().getPluginManager().registerEvents(new OpenThisShop(), this);
        getServer().getPluginManager().registerEvents(new Gaming(), this);
        getServer().getPluginManager().registerEvents(new Enter(), this);
        getServer().getPluginManager().registerEvents(new Rules(), this);

        //注册指令
        getCommand("start").setExecutor(new Gaming());
        getCommand("enter").setExecutor(new Enter());
        getCommand("end").setExecutor(new Gaming());

    }

    @Override
    public void onDisable() {
        // 清理所有玩家的职业任务
        // 重置所有购买记录
        PurchaseManager.getInstance().resetAllPurchases();
    }
    
    /**
     * 获取职业管理器
     * @return 职业管理器实例
     */
    public ClassesManager getClassesManager() {
        return classesManager;
    }

    public Gaming getGaming() {
        return gaming;
    }

    /**
     * 切换玩家职业
     * @param player 目标玩家
     * @param className 职业名称
     */
    public void switchPlayerClass(Player player, String className) {
        Class<? extends Clazz> clazzType = classesManager.getClassType(className);
        if (clazzType != null) {
            try {
                // 取消旧职业的任务
                Clazz oldClass = classesManager.getPlayerClass(player);
                if (oldClass != null) {
                    oldClass.cancelTasks();
                    oldClass.setSelect(false);
                }
                // 创建新职业实例并设置
                Clazz newClass = clazzType.getConstructor(Player.class).newInstance(player);
                // 注册事件监听器
                getServer().getPluginManager().registerEvents(newClass, this);
                newClass.setSelect(true);
                player.sendMessage(CC.trans("&a成功切换到 " + className + " 职业！"));
                // 存储新职业实例到 ClassesManager
                classesManager.setPlayerClass(player, newClass);
            } catch (Exception e) {
                e.printStackTrace();
                player.sendMessage(CC.trans("&c切换职业失败！"));
            }
        } else {
            player.sendMessage(CC.trans("&c未找到该职业！"));
        }
    }
}
