package com.endBattle;

import org.bukkit.plugin.java.JavaPlugin;

public final class EndBattle extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("终末之战已启用");
        getServer().getPluginManager().registerEvents(new Gaming(), this);
        getServer().getPluginManager().registerEvents(new Enter(), this);
        getServer().getPluginManager().registerEvents(new Rules(), this);
        getServer().getPluginManager().registerEvents(new ShopVillager(), this);
        getCommand("enter").setExecutor(new Enter());
        getCommand("start").setExecutor(new Gaming());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
