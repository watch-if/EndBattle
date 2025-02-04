package com.endBattle.game.Teams;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

//创建队伍信息，存储队伍信息
public class TeamManager {
    private String name;
    private ChatColor color;
    private List<String> members;

    public TeamManager(String name, ChatColor color) {
        this.name = name;
        this.color = color;
        this.members = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public List<String> getMembers() {
        return members;
    }

    public void addMember(String playerName) {
        if (!members.contains(playerName)) {
            members.add(playerName);
        }
    }

    public void removeMember(String playerName) {
        members.remove(playerName);
    }

    public boolean isFull(int maxMembers) {
        return members.size() >= maxMembers;
    }
}
