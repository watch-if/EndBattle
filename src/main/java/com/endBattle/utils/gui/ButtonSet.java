
package com.endBattle.utils.gui;

import com.endBattle.utils.gui.button.AbstractButton;

public class ButtonSet {
    private AbstractButton button;
    private int index;
    
    public ButtonSet() {}
    
    public ButtonSet(AbstractButton button, int index) {
        this.button = button;
        this.index = index;
    }
    
    public AbstractButton getButton() {
        return button;
    }
    
    public void setButton(AbstractButton button) {
        this.button = button;
    }
    
    public int getIndex() {
        return index;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public static ButtonSet toButtonSet(int index, AbstractButton button) {
        return new ButtonSet(button, index);
    }

    /**
     * 构造方法重载，支持直接传入位置和按钮
     * @param index 按钮在GUI中的位置
     * @param button 按钮实例
     */
    public ButtonSet(int index, AbstractButton button) {
        this.index = index;
        this.button = button;
    }

    /**
     * 创建一个新的ButtonSet实例
     * @param index 按钮在GUI中的位置
     * @param button 按钮实例
     * @return 新的ButtonSet实例
     */
    public static ButtonSet create(int index, AbstractButton button) {
        return new ButtonSet(button, index);
    }
}