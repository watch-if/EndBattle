package com.endBattle.game.Gaming;

import com.endBattle.EndBattle;
import com.endBattle.game.Teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Enter implements Listener, CommandExecutor {


    //大厅选择界面
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Inventory entergui = Bukkit.createInventory(null,1*9,"大厅选择");

        //创建物品按钮
        ItemStack dragonhead = new ItemStack(Material.DRAGON_HEAD);
        ItemMeta m = dragonhead.getItemMeta();
        m.setDisplayName("§4终末之战");
        dragonhead.setItemMeta(m);
        entergui.setItem(2, dragonhead);

        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta n = compass.getItemMeta();
        n.setDisplayName("§e主城");
        compass.setItemMeta(n);
        entergui.setItem(6, compass);
        Player p = (Player) sender;
        p.openInventory(entergui);
        return false;
    }

    //玩家点击大厅选择界面事件处理
    @EventHandler
    public void check(InventoryClickEvent ch) {
        Player p = (Player) ch.getWhoClicked();
        String inventoryTitle = ch.getWhoClicked().getOpenInventory().getTitle();
        if (inventoryTitle.replaceAll("§.", "").trim().equals("大厅选择")) {
            ch.setCancelled(true);
            if (ch.getRawSlot() > ch.getInventory().getSize()) {
                p.sendMessage("无效操作");
                return;
            }
            if (ch.getRawSlot() < 0) {
                p.sendMessage("无效操作");
                return;
            }

            //点击终末之战按钮传送
            if (ch.getRawSlot() == 2) {
                Location endLocation = new Location(Bukkit.getWorld("world_the_end"), 0, 201, 0);
                p.teleport(endLocation);
                return;
            }

            // 点击主城按钮传送
            if (ch.getRawSlot() == 6) {
                Location mainWorldLocation = new Location(Bukkit.getWorld("world"), 0, 0, 0);
                p.teleport(mainWorldLocation);

                // 清空玩家背包
                p.getInventory().clear();

                // 清除玩家的职业与队伍数据
                String playerName = p.getName();
                Gaming gaming = EndBattle.getInstance().getGaming();
                gaming.playerClasses.remove(playerName);
                for (TeamManager team : gaming.teams.values()) {
                    if (team.getMembers().contains(playerName)) {
                        team.removeMember(playerName);
                        break;
                    }
                }

                // 调整 finishTeam 和 finishClass 的数值
                if (!gaming.TeamIsFinish && !gaming.ClassIsFinish) {
                    // 如果游戏没有开始，恢复 finishTeam 和 finishClass 到该玩家点击前的数值
                    gaming.restoreFinishCounts(playerName);
                } else {
                    // 如果游戏已经开始，减少该玩家所在队伍的 player 数量
                    String teamName = gaming.getTeamName(p);
                    if (teamName != null) {
                        switch (teamName) {
                            case "red":
                                gaming.redplayer--;
                                break;
                            case "yellow":
                                gaming.yellowplayer--;
                                break;
                            case "blue":
                                gaming.blueplayer--;
                                break;
                            case "green":
                                gaming.greenplayer--;
                                break;
                        }
                    }
                }

                return;
            }
        }
        return;
    }
}
