package com.mrzak34.thunderhack.gui.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.thundergui.fontstuff.FontRender;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.PositionSetting;
import com.mrzak34.thunderhack.setting.Setting;

import com.mrzak34.thunderhack.util.DrawHelper;
import com.mrzak34.thunderhack.util.PaletteHelper;
import com.mrzak34.thunderhack.util.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class ArrayList extends Module {
    private static ArrayList INSTANCE = new ArrayList();
    public ArrayList() {
        super("ArrayList", "Autopot", Module.Category.HUD,true,false,false);
        setInstance();
    }
    public final Setting<ColorSetting> color = register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    private   Setting<Boolean> glow = register(new Setting<>("glow", true));
    private   Setting<Boolean> shadoiw = register(new Setting<>("shadow", true));


    private enum bMode {
        Flat, Shader
    }
    public static ArrayList getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ArrayList();
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;

    }





    public Setting<Float> rainbowSpeed = register(new Setting("Speed", 10.0f, 1.0f, 20.0f));
    public Setting<Float> saturation = register(new Setting("Saturation", 0.5f, 0.1f, 1.0f));
    public Setting<Integer> gste = register(new Setting("GS", 30, 10, 50));



    private final Setting<PositionSetting> pos = register(new Setting<>("Position", new PositionSetting(0.5f,0.5f)));

    private Setting<cMode> cmode = register(new Setting<>("ColorMode", cMode.Rainbow));
    private enum cMode {
        Rainbow, Custom
    }

    float x1 =0;
    float y1= 0;

    boolean reverse;
    public int calc(int value) {
        ScaledResolution rs = new ScaledResolution(Minecraft.getMinecraft());
        return value * rs.getScaleFactor() / 2;
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e){
        int stringWidth;
        ScaledResolution rs = new ScaledResolution(mc);
        int width = calc(rs.getScaledWidth());
        int height = calc(rs.getScaledHeight());

        y1 = rs.getScaledHeight() * pos.getValue().getY();
        x1 = rs.getScaledWidth() * pos.getValue().getX();

        reverse = x1 > (float)(width / 2);
        boolean reverseY = y1 > (float)(height / 2);
        int offset = 0;
        int yTotal = 0;
        for (int i = 0; i < Thunderhack.moduleManager.sortedModules.size(); ++i) {
            yTotal += FontRender.getFontHeight6() + 3;
        }


            for (int k = 0; k < Thunderhack.moduleManager.sortedModules.size(); k++) {
                Module module = Thunderhack.moduleManager.sortedModules.get(k);
                if(!module.isDrawn()){
                    continue;
                }

                if (!reverse) {
                    stringWidth = FontRender.getStringWidth6(module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "")) + 3;
                    if (glow.getValue()) {
                        RenderHelper.drawBlurredShadow(x1 - 3, y1 + (float)offset - 1, (float)stringWidth + 4.0f, (float)9.0f,gste.getValue(),  (cmode.getValue() == cMode.Rainbow) ? PaletteHelper.astolfo(offset, yTotal, saturation.getValue(), rainbowSpeed.getValue()) : new Color(color.getValue().getColor()).darker());
                    }
                    drawRect(x1, y1 + (float)offset, x1 + (float)stringWidth + 1.0f, y1 + (float)offset + 8.0f, (cmode.getValue() == cMode.Rainbow) ? PaletteHelper.astolfo(offset, yTotal, saturation.getValue(), rainbowSpeed.getValue()).getRGB() : new Color(color.getValue().getColor()).darker().getRGB());


                    drawRect(x1 - 2.0f, y1 + (float)offset, x1 + 1.0f, y1 + (float)offset + 8.0f, -1);
                    FontRender.drawString6(module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : ""), x1 + 3.0f, y1 + 2.0f + (float)offset, -1,shadoiw.getValue());
                }
                if (reverse) {
                    stringWidth = FontRender.getStringWidth6(module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "")) + 3;
                    if (glow.getValue()) {
                        RenderHelper.drawBlurredShadow(x1 - (float)stringWidth - 3, y1 + (float)offset - 1, stringWidth + 4, 9f, gste.getValue(),(cmode.getValue() == cMode.Rainbow) ? PaletteHelper.astolfo(offset, yTotal, saturation.getValue(), rainbowSpeed.getValue()) : new Color(color.getValue().getColor()).darker());
                    }
                    drawRect(x1 - (float)stringWidth, y1 + (float)offset, x1  + 1.0f, y1 + (float)offset + 8.0f, (cmode.getValue() == cMode.Rainbow) ? PaletteHelper.astolfo(offset, yTotal, saturation.getValue(), rainbowSpeed.getValue()).getRGB() : new Color(color.getValue().getColor()).darker().getRGB());


                    drawRect(x1 + 1f , y1 + (float)offset, x1 + 4.0f, y1 + (float)offset + 8.0f, -1);
                    FontRender.drawString6(module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : ""), x1 - stringWidth + 2.0f , y1 + 2.0f + (float)offset, -1,shadoiw.getValue());

                }
                offset += 8;
            }


        if(mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof HudEditorGui){
            if(isHovering()){
                if(Mouse.isButtonDown(0) && mousestate){
                    pos.getValue().setX( (float) (normaliseX() - dragX) /  rs.getScaledWidth());
                    pos.getValue().setY( (float) (normaliseY() - dragY) / rs.getScaledHeight());
                }

            }
        }

        if(Mouse.isButtonDown(0) && isHovering()){
            if(!mousestate){
                dragX = (int) (normaliseX() - (pos.getValue().getX() * rs.getScaledWidth()));
                dragY = (int) (normaliseY() - (pos.getValue().getY() * rs.getScaledHeight()));
            }
            mousestate = true;
        } else {
            mousestate = false;
        }
    }


    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.color((float)f, (float)f1, (float)f2, (float)f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0).endVertex();
        bufferbuilder.pos(right, bottom, 0.0).endVertex();
        bufferbuilder.pos(right, top, 0.0).endVertex();
        bufferbuilder.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    int dragX, dragY = 0;
    boolean mousestate = false;

    public int normaliseX(){
        return (int) ((Mouse.getX()/2f));
    }
    public int normaliseY(){
        ScaledResolution sr = new ScaledResolution(mc);
        return (((-Mouse.getY() + sr.getScaledHeight()) + sr.getScaledHeight())/2);
    }

    public boolean isHovering(){
        if(reverse){
            return normaliseX() > x1 - 50 && normaliseX() < x1 && normaliseY() > y1 && normaliseY() < y1 + 150;
        } else {
            return normaliseX() > x1 - 10 && normaliseX() < x1 + 50 && normaliseY() > y1 && normaliseY() < y1 + 150;
        }
    }

}
