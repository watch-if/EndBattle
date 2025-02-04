package com.endBattle.utils.chat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.TextComponent;

public class CC {
    public static void sendMessage(String message, Player target) {
        target.sendMessage(trans(message));
    }
    public static TextComponent trans(String message) {
        return net.kyori.adventure.text.Component.text(message.replace("&","§"));
    }

    public static void brocastDebug(String message) {
        Bukkit.getServer().broadcast(trans("&e[SB Debug] " + message));
    }
    public static void debugLog(String message) {
        Bukkit.getLogger().info(trans("&e[SB Debug] ") + message);
    }
    public static void consoleInfo(String message) {
        Bukkit.getLogger().info(message);
    }
}
