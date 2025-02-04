package com.endBattle.game.shop;

import com.endBattle.game.classes.GetClassGUI;
import com.endBattle.game.mainshop.Shop;
import com.endBattle.utils.chat.CC;
import com.endBattle.utils.gui.AbstractGui;
import com.endBattle.utils.gui.ButtonSet;
import com.endBattle.utils.gui.button.AbstractButton;

import net.kyori.adventure.text.TextComponent;

import com.endBattle.EndBattle;
import com.endBattle.game.classes.Clazz;
import com.endBattle.game.shop.item.ShopItem;

import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
// 导入Player
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShopGui extends AbstractGui {
    private final MoneyManager moneyManager;
    private final Player player;
    private final PurchaseManager purchaseManager;

    public ShopGui(Player owner) {
        super("商店", owner.getUniqueId());
        this.player = owner;
        this.moneyManager = MoneyManager.getInstance();
        this.purchaseManager = PurchaseManager.getInstance();

        // 检查玩家是否有职业实例
        Clazz playerClass = EndBattle.getInstance().getClassesManager().getPlayerClass(player);
        if (playerClass == null) {
            player.sendMessage(CC.trans("&c你还没有选择职业！请先选择一个职业。"));
            // 关闭当前 GUI 或打开职业选择 GUI
            return;
        }

    }

    private void getclassgui(){
        GetClassGUI getClassGUI = new GetClassGUI();
        getClassGUI.ClassGUI(player);
    }

    @Override
    public int getSize() {
        return 54; // 6行*9列
    }

    @Override
    public ArrayList<ButtonSet> getButtons() {
        ArrayList<ButtonSet> buttons = new ArrayList<>();
        Clazz playerClass = EndBattle.getInstance().getClassesManager().getPlayerClass(player);
        if (playerClass == null) {
            // 如果玩家没有职业实例，返回空按钮列表或提示信息
            return buttons;
        }
        List<ShopItem> items = EndBattle.getInstance().getClassesManager().getPlayerClass(
            player
        ).shopItems();

        for (int i = 0; i < items.size(); i++) {
            ShopItem item = items.get(i);
            ItemStack itemStack = new ItemStack(item.getMaterial());
            ItemMeta meta = itemStack.getItemMeta();
            
            List<TextComponent> lore = new ArrayList<>();
            lore.add(CC.trans("&7价格: &f" + item.getPrice() + " " + item.getMoney().name()));
            if (item.getLimit() > 0) {
                lore.add(CC.trans("&7限购: &f" + item.getLimit() + "个"));
            }
            meta.lore(lore);
            itemStack.setItemMeta(meta);

            // 创建按钮并设置点击事件
            AbstractButton button = AbstractButton.builder()
                .icon(itemStack)
                .clickHandler(player -> {
                    // 检查购买限制
                    if (item.getLimit() > 0 && 
                        purchaseManager.getPurchaseCount(player, item.getMaterial()) >= item.getLimit()) {
                        player.sendMessage(CC.trans("&c你已达到该物品的购买上限！"));
                        return;
                    }

                    // 检查是否有足够的货币
                    if (!moneyManager.hasMoney(player, item.getMoney(), item.getPrice())) {
                        player.sendMessage(CC.trans("&c你没有足够的" + item.getMoney().name() + "！"));
                        return;
                    }

                    // 扣除货币并给予物品
                    moneyManager.removeMoney(player, item.getMoney(), item.getPrice());
                    ItemStack boughtItem = new ItemStack(item.getMaterial(), item.getAmount());
                    player.getInventory().addItem(boughtItem);
                    purchaseManager.updatePurchaseCount(player, item.getMaterial());
                    player.sendMessage(CC.trans("&a购买成功！"));
                })
                .build();

            // 添加按钮到指定位置
            buttons.add(ButtonSet.create(i, button));
        }

        // 添加返回按钮
        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta barrierMeta = barrier.getItemMeta();
        barrierMeta.displayName(CC.trans("&c返回"));
        barrier.setItemMeta(barrierMeta);
        
        buttons.add(new ButtonSet(53, new AbstractButton(barrier) {
            @Override
            public void onClick(Player player) {

                player.closeInventory();
                Shop.ShopGUI(player);
            }
        }));

        return buttons;
    }

    @Override
    public void beforeOpen() {
        // 打开界面前的准备工作
    }

    @Override
    public void onOpen() {
        // 打开界面时的操作
    }

    @Override
    public void afterOpen() {
        // 打开界面后的操作
    }
}