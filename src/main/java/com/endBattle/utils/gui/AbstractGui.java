package com.endBattle.utils.gui;

import com.endBattle.EndBattle;
import com.endBattle.utils.gui.button.AbstractButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**
 * GUI抽象类，用于创建和管理插件中的物品栏界面
 * 实现了基本的物品栏事件处理和生命周期管理
 */
public abstract class AbstractGui implements Listener {
    /**
     * 构造一个新的GUI实例
     * @param displayTitle GUI显示的标题
     * @param owner GUI的所有者UUID
     */
    public AbstractGui(String displayTitle,UUID owner) {
        this.displayTitle = displayTitle;
        this.owner = owner;
        Bukkit.getPluginManager().registerEvents(this, EndBattle.getInstance());
    }

    private String displayTitle;
    private Inventory currentInv;
    private UUID owner;

    /**
     * 获取GUI的大小（格子数量）
     * @return 物品栏大小
     */
    public abstract int getSize();

    /**
     * 获取GUI中所有的按钮设置
     * @return 按钮设置列表
     */
    public abstract ArrayList<ButtonSet> getButtons();

    /**
     * GUI打开前的准备工作
     */
    public abstract void beforeOpen();

    /**
     * GUI打开时的操作
     */
    public abstract void onOpen();

    /**
     * GUI打开后的操作
     */
    public abstract void afterOpen();

    /**
     * 处理物品栏点击事件
     * @param e 物品栏点击事件
     */
    @EventHandler
    public void handleClick(InventoryClickEvent e) {
        // 判断是否为此菜单的拥有者点击
        if (!e.getWhoClicked().getUniqueId().equals(this.owner) ||
                !e.getClickedInventory().equals(this.currentInv) ||
                e.getCurrentItem() == null ||
                e.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        // 获取点击的物品
        ItemStack clickedItem = e.getCurrentItem();
        ItemMeta clickedItemMeta = clickedItem.getItemMeta();

        // 调试信息
        System.out.println("Clicked item: " + clickedItemMeta.getDisplayName());


        // 遍历所有按钮，找到匹配的按钮并调用其 onClick 方法
        for (ButtonSet buttonSet : getButtons()) {
            AbstractButton button = buttonSet.getButton();
            ItemStack buttonItem = button.getItemStack();
            ItemMeta buttonItemMeta = buttonItem.getItemMeta();

            // 使用 Adventure API 比较显示名称
            if (buttonItemMeta.displayName().equals(clickedItemMeta.displayName())) {
                e.setCancelled(true);
                button.onClick((Player) e.getWhoClicked());
                break;
            }
        }
    }
    /**
     * 处理物品栏关闭事件
     * @param e 物品栏关闭事件
     */
    @EventHandler
    public void handleCloseInventory(InventoryCloseEvent e) {
        if (e.getPlayer().getUniqueId().equals(this.owner)) {
            HandlerList.unregisterAll(this);
        }
    }

    /**
     * 打开GUI给玩家
     * 这个方法会依次调用beforeOpen、创建物品栏、onOpen和afterOpen
     */
    public void open() {
        beforeOpen();
    
        // 使用新的创建物品栏API

        this.currentInv = Bukkit.createInventory(null, getSize(), net.kyori.adventure.text.Component.text(this.displayTitle));

        // 添加按钮到物品栏
        for (ButtonSet buttonSet : getButtons()) {
            this.currentInv.setItem(buttonSet.getIndex(), buttonSet.getButton().getItemStack());
        }

        // 获取并打开物品栏给玩家
        Player player = Bukkit.getPlayer(this.owner);
        if (player != null) {
            player.openInventory(this.currentInv);
        }
    
        onOpen();
        afterOpen();
    }
}

