package com.mrzak34.thunderhack.gui.hud;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.event.events.Render2DEvent;
import com.mrzak34.thunderhack.gui.clickui.ColorUtil;
import com.mrzak34.thunderhack.util.Drawable;
import com.mrzak34.thunderhack.modules.Module;
import com.mrzak34.thunderhack.setting.ColorSetting;
import com.mrzak34.thunderhack.setting.PositionSetting;
import com.mrzak34.thunderhack.setting.Setting;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import com.mrzak34.thunderhack.util.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;


public class RadarRewrite extends Module {

    public RadarRewrite() {
        super("AkrienRadar", "стрелочки", Category.RENDER, true, false, false);
    }

    private Setting<Float> width = register( new Setting<>("TracerHeight", 2.28f, 0.1f, 5f));
    private Setting<Float> rad22ius = register(new Setting<>("TracerDown", 3.63f, 0.1F, 20.0F)); //радиус круга компасса ежжи
    private Setting<Float> tracerA = register(new Setting<>("TracerWidth", 0.44F, 0.0F, 8.0F));
    private Setting<Integer> xOffset = register(new Setting<>("TracerRadius", 68, 20, 100));
    private Setting<Integer> maxup2 = register(new Setting<>("PitchLock", 42, -90, 90));
    public Setting<Boolean> glow = register(new Setting("TracerGlow", false));
    private Setting<Integer> glowe = register(new Setting<>("GlowRadius", 10, 1, 20));
    private Setting<Integer> glowa = register(new Setting<>("GlowAlpha", 170, 0, 255));

    private Setting<triangleModeEn> triangleMode = register(new Setting<>("TracerCMode", triangleModeEn.Astolfo));
    private Setting<mode2> Mode2 = register(new Setting<>("CircleCMode", mode2.Astolfo));
    private Setting<PositionSetting> pos = this.register(new Setting<>("Position", new PositionSetting(0.5f, 0.78f)));
    private Setting<Float> CRadius = register(new Setting<>("CompasRadius", 47F, 0.1F, 70.0F)); //радиус круга компасса ежжи
    private Setting<Integer> fsef = register(new Setting<>("Correct", 12, -90, 90));
    private  Setting<ColorSetting> cColor = this.register(new Setting<>("CompassColor", new ColorSetting(0x2250b4b4)));
    private  Setting<ColorSetting> ciColor = this.register(new Setting<>("CircleColor", new ColorSetting(0x2250b4b4)));
    private  Setting<ColorSetting> colorf = this.register(new Setting<>("FriendColor", new ColorSetting(0x2250b4b4)));
    private  Setting<ColorSetting> colors = this.register(new Setting<>("TracerColor", new ColorSetting(0x2250b4b4)));


    public enum mode2 {
        Custom, Rainbow, Astolfo;
    }

    public enum triangleModeEn {
        Custom, Astolfo, Rainbow;
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
        return normaliseX() > xOffset2 - 50 && normaliseX()< xOffset2 + 50 && normaliseY() > yOffset2 - 50 &&  normaliseY() < yOffset2 + 50;
    }

    float xOffset2 = 0;
    float yOffset2 = 0;


    @Override
    public void onUpdate(){
        astolfo.update();
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {

        ScaledResolution sr = new ScaledResolution(mc);

        if(mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof HudEditorGui){
            if(isHovering()){
                if(Mouse.isButtonDown(0) && mousestate){
                    pos.getValue().setX( (float) (normaliseX() - dragX) /  sr.getScaledWidth());
                    pos.getValue().setY( (float) (normaliseY() - dragY) / sr.getScaledHeight());
                }

            }
        }

        if(Mouse.isButtonDown(0) && isHovering()){
            if(!mousestate){
                dragX = (int) (normaliseX() - (pos.getValue().getX() * sr.getScaledWidth()));
                dragY = (int) (normaliseY() - (pos.getValue().getY() * sr.getScaledHeight()));
            }
            mousestate = true;
        } else {
            mousestate = false;
        }

        GlStateManager.pushMatrix();
        rendercompass();
        GlStateManager.popMatrix();

        xOffset2 = (sr.getScaledWidth() * pos.getValue().getX());
        yOffset2 = (sr.getScaledHeight() * pos.getValue().getY());

        int color = 0;
        switch (triangleMode.getValue()) {
            case Custom:
                color = colors.getValue().getColor();
                break;
            case Astolfo:
                color = DrawHelper.astolfo(false, 1).getRGB();
                break;
            case Rainbow:
                color = DrawHelper.rainbow(300, 1, 1).getRGB();
                break;
        }
        float xOffset = sr.getScaledWidth() * pos.getValue().getX() ;
        float yOffset = sr.getScaledHeight() * pos.getValue().getY() ;

        GlStateManager.pushMatrix();
        GlStateManager.translate(xOffset2, yOffset2, 0);
        GL11.glRotatef(90f / Math.abs(90f / clamp2(mc.player.rotationPitch ,maxup2.getValue(),90f)) - 90 -fsef.getValue(), 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(-xOffset2, -yOffset2, 0);


        for (EntityPlayer e : mc.world.playerEntities) {
            if (e != mc.player) {
                GL11.glPushMatrix();
                float yaw = getRotations(e) - mc.player.rotationYaw;
                GL11.glTranslatef(xOffset, yOffset, 0.0F);
                GL11.glRotatef(yaw, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(-xOffset, -yOffset, 0.0F);
                if(Thunderhack.friendManager.isFriend(e)){
                    drawTracerPointer(xOffset, yOffset - this.xOffset.getValue(), width.getValue() * 5F, colorf.getValue().getColor());
                } else {
                    drawTracerPointer(xOffset, yOffset - this.xOffset.getValue(), width.getValue() * 5F, color);
                }
                GL11.glTranslatef(xOffset, yOffset, 0.0F);
                GL11.glRotatef(-yaw, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(-xOffset, -yOffset, 0.0F);
                GL11.glColor4f(1F, 1F, 1F, 1F);
                GL11.glPopMatrix();
            }
        }
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GlStateManager.popMatrix();
    }


    public void rendercompass() {
            ScaledResolution sr = new ScaledResolution(mc);
            float x = sr.getScaledWidth() * pos.getValue().getX();
            float y = sr.getScaledHeight() * pos.getValue().getY();

            float nigga = Math.abs(90f / clamp2(mc.player.rotationPitch,maxup2.getValue(),90f));

            if (Mode2.getValue() == mode2.Custom) {
                RenderHelper.drawEllipsCompas(-(int) mc.player.rotationYaw, x, y,nigga,1f, CRadius.getValue() -2, ciColor.getValue().getColorObject(),false);
                RenderHelper.drawEllipsCompas(-(int) mc.player.rotationYaw, x, y,nigga,1f, CRadius.getValue()-2.5f, ciColor.getValue().getColorObject(),false);
            }
            if (Mode2.getValue() == mode2.Rainbow) {
                RenderHelper.drawEllipsCompas(-(int) mc.player.rotationYaw, x, y,nigga,1f, CRadius.getValue() -2, PaletteHelper.rainbow(300, 1, 1),false);
                RenderHelper.drawEllipsCompas(-(int) mc.player.rotationYaw, x, y,nigga,1f, CRadius.getValue()-2.5f, PaletteHelper.rainbow(300, 1, 1),false);
            }
            if (Mode2.getValue() == mode2.Astolfo) {
                RenderHelper.drawEllipsCompas(-(int) mc.player.rotationYaw, x, y,nigga,1f, CRadius.getValue() -2, null,false);
                RenderHelper.drawEllipsCompas(-(int) mc.player.rotationYaw, x, y,nigga,1f, CRadius.getValue() -2.5f, null,false);
            }
            RenderHelper.drawEllipsCompas(-(int) mc.player.rotationYaw, x, y,nigga,1f, CRadius.getValue(), cColor.getValue().getColorObject(),true);
    }

    public static float clamp2(float num, float min, float max) {
        if (num < min) {
            return min;
        } else {
            return Math.min(num, max);
        }
    }

    public static AstolfoAnimation astolfo = new AstolfoAnimation();

    public  void drawTracerPointer(float x, float y, float size, int color) {
        boolean blend = GL11.glIsEnabled(GL_BLEND);
        GL11.glEnable(GL_BLEND);
        boolean depth = GL11.glIsEnabled(GL_DEPTH_TEST);
        glDisable(GL_DEPTH_TEST);

        GL11.glDisable(GL_TEXTURE_2D);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL_LINE_SMOOTH);
        GL11.glPushMatrix();

        hexColor(color);
        GL11.glBegin(7);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d((x - size * tracerA.getValue() ), (y + size));
        GL11.glVertex2d(x, (y + size- rad22ius.getValue()));
        GL11.glVertex2d(x, y);
        GL11.glEnd();

        hexColor(ColorUtil.darker(new Color(color),0.8f).getRGB());
        GL11.glBegin(7);
        GL11.glVertex2d(x, y); //top
        GL11.glVertex2d(x, (y + size- rad22ius.getValue())); //midle
        GL11.glVertex2d((x + size * tracerA.getValue()), (y + size)); // left right
        GL11.glVertex2d(x, y); //top
        GL11.glEnd();


        hexColor(ColorUtil.darker(new Color(color),0.6f).getRGB());
        GL11.glBegin(7);
        GL11.glVertex2d((x - size * tracerA.getValue() ), (y + size ));
        GL11.glVertex2d((x + size * tracerA.getValue()), (y + size )); // left right
        GL11.glVertex2d(x, (y + size - rad22ius.getValue())); //midle
        GL11.glVertex2d((x - size * tracerA.getValue() ), (y + size ));
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glEnable(GL_TEXTURE_2D);
        if (!blend)
            GL11.glDisable(GL_BLEND);
        GL11.glDisable(GL_LINE_SMOOTH);

        if(glow.getValue())
            Drawable.drawBlurredShadow(x- size * tracerA.getValue(),y,(x + size * tracerA.getValue()) -(x - size * tracerA.getValue() ),size,glowe.getValue(),DrawHelper.injectAlpha(new Color(color),glowa.getValue()) );

        if(depth)
            glEnable(GL_DEPTH_TEST);


    }
    public static void hexColor(int hexColor) {
        float red = (float) (hexColor >> 16 & 0xFF) / 255.0f;
        float green = (float) (hexColor >> 8 & 0xFF) / 255.0f;
        float blue = (float) (hexColor & 0xFF) / 255.0f;
        float alpha = (float) (hexColor >> 24 & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
    }

    private float getRotations(Entity entity) {
        double x = interp(entity.posX,entity.lastTickPosX) - interp(mc.player.posX,mc.player.lastTickPosX);
        double z = interp(entity.posZ,entity.lastTickPosZ) - interp(mc.player.posZ, mc.player.lastTickPosZ);
        return (float)-(Math.atan2(x, z) * (180 / Math.PI));
    }
    public double interp(double d, double d2) {
        return d2 + (d - d2) * (double)mc.getRenderPartialTicks();
    }
}