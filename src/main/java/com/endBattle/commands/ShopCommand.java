package com.endBattle.commands;

import com.endBattle.game.shop.ShopGui;
import com.endBattle.utils.chat.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 商店命令处理器
 */
public class ShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 检查是否为玩家执行
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ShopGui shopGui = new ShopGui(player);
            shopGui.open();
        } else {
            sender.sendMessage("Only players can use this command.");
        }
        return true;
    }
}