package com.mrzak34.thunderhack.modules.funnygame;

import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.Setting;
import com.mrzak34.thunderhack.util.ItemUtil;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;

public class Offhand extends Module {

    public Offhand() {
        super("StormOffhand", "AutoTotem", Category.COMBAT, true, false, false);
    }

    public Setting<Boolean> rightClickGapple = this.register(new Setting<>("rightClickGapple", true));
    public Setting<Boolean> crapple = this.register(new Setting<>("crap", true));
    public Setting<Float> health = this.register(new Setting<>("health", 10f, 1f, 36f));
    public Setting<Float> faldistance = this.register(new Setting<>("faldistance", 10f, 1f, 36f));

    public Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.TOTEM));

    public enum Mode {
        TOTEM,
        GAPPLE,
        CRYSTAL,
        SHIELD
    }

    @Override
    public void onTick() {
        if (nullCheck() || mc.currentScreen instanceof GuiInventory) return;
        float hp = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        if(ItemUtil.getItemSlot(Items.TOTEM_OF_UNDYING) != -1) {
            if (hp > health.getValue() && !(mc.player.fallDistance >= faldistance.getValue())) {
                Item heldItem = mc.player.getHeldItemMainhand().getItem();
                if (rightClickGapple.getValue() && mc.gameSettings.keyBindUseItem.isKeyDown() && (heldItem instanceof ItemSword || heldItem instanceof ItemAxe) && mc.currentScreen == null) {
                    if (mode.getValue() != Mode.GAPPLE)
                        ItemUtil.swapToOffhandSlot(getSlot(Mode.GAPPLE));
                } else {
                    ItemUtil.swapToOffhandSlot(getSlot(mode.getValue()));
                }
            } else {
                ItemUtil.swapToOffhandSlot(ItemUtil.getItemSlot(Items.TOTEM_OF_UNDYING));
            }
        } else if (ItemUtil.getGappleSlot(crapple.getValue()) != -1){
            ItemUtil.swapToOffhandSlot(ItemUtil.getGappleSlot(crapple.getValue()));
        }

    }

    private int getSlot(Mode mode) {
        switch (mode) {
            case CRYSTAL:
                return ItemUtil.getItemSlot(Items.END_CRYSTAL);
            case GAPPLE:
                return ItemUtil.getGappleSlot(crapple.getValue());
            case SHIELD:
                return ItemUtil.getItemSlot(Items.SHIELD);
            default:
                return ItemUtil.getItemSlot(Items.TOTEM_OF_UNDYING);
        }
    }


}
