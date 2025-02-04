package com.endBattle.utils.gui.button;

import com.endBattle.game.mainshop.Shop;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractButton {
    private ItemStack itemStack;
    private boolean prohibitedMove;
    
    public AbstractButton() {}
    
    public static ButtonBuilder builder() {
        return new ButtonBuilder();
    }

    public static class ButtonBuilder {
        private ItemStack icon;
        private boolean prohibitedMove = true;
        private ClickHandler clickHandler;

        public ButtonBuilder icon(ItemStack icon) {
            this.icon = icon;
            return this;
        }

        public ButtonBuilder prohibitedMove(boolean prohibitedMove) {
            this.prohibitedMove = prohibitedMove;
            return this;
        }

        public ButtonBuilder clickHandler(ClickHandler clickHandler) {
            this.clickHandler = clickHandler;
            return this;
        }

        public AbstractButton build() {
            return new AbstractButton(icon, prohibitedMove) {
                @Override
                public void onClick(Player player) {
                    if (clickHandler != null) {
                        clickHandler.onClick(player);
                    }
                }
            };
        }
    }

    @FunctionalInterface
    public interface ClickHandler {
        void onClick(Player player);
    }
    
    /**
     * 构造方法重载，默认禁止移动
     * @param itemStack 按钮显示的物品
     */
    public AbstractButton(ItemStack itemStack) {
        this(itemStack, true);
    }
    
    public AbstractButton(ItemStack itemStack, boolean prohibitedMove) {
        this.itemStack = itemStack;
        this.prohibitedMove = prohibitedMove;
    }
    
    public ItemStack getItemStack() {
        return itemStack;
    }
    
    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
    
    public boolean isProhibitedMove() {
        return prohibitedMove;
    }
    
    public void setProhibitedMove(boolean prohibitedMove) {
        this.prohibitedMove = prohibitedMove;
    }

    public abstract void onClick(Player player);

    public static AbstractButton buildButton(ItemStack itemStack, boolean prohibitedMove) {
        return new AbstractButton(itemStack, prohibitedMove) {
            @Override
            public void onClick(Player player) {
                // 默认实现为空，由具体按钮类重写
            }
        };
    }
}
