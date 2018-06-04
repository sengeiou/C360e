package com.alfredwaiter.javabean;

/**
 * Created by Alex on 2017/4/8.
 */

public class ModifierCPVariance {
    private boolean isTitle;
    private int modifierId;
    private String modifierName;
    private String tag;
    public boolean isTitle() {
        return isTitle;
    }

    public void setTitle(boolean title) {
        isTitle = title;
    }

    public int getModifierId() {
        return modifierId;
    }

    public void setModifierId(int modifierId) {
        this.modifierId = modifierId;
    }

    public String getModifierName() {
        return modifierName;
    }

    public void setModifierName(String modifierName) {
        this.modifierName = modifierName;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
