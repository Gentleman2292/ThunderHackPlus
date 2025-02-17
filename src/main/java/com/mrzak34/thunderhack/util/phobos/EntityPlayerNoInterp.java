package com.mrzak34.thunderhack.util.phobos;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.world.World;

import static com.mrzak34.thunderhack.util.ItemUtil.mc;

public class EntityPlayerNoInterp extends EntityOtherPlayerMP implements IEntityNoInterp
{
    public EntityPlayerNoInterp(World worldIn)
    {
        this(worldIn, mc.player.getGameProfile());
    }

    public EntityPlayerNoInterp(World worldIn, GameProfile gameProfileIn)
    {
        super(worldIn, gameProfileIn);
    }

    @Override
    public double getNoInterpX() { return posX; }

    @Override
    public double getNoInterpY() { return posY; }

    @Override
    public double getNoInterpZ() { return posZ; }

    @Override
    public void setNoInterpX(double x) { }

    @Override
    public void setNoInterpY(double y) { }

    @Override
    public void setNoInterpZ(double z) { }

    @Override
    public int getPosIncrements() { return 0; }

    @Override
    public void setPosIncrements(int posIncrements) { }

    @Override
    public float getNoInterpSwingAmount() { return 0; }

    @Override
    public float getNoInterpSwing() { return 0; }

    @Override
    public float getNoInterpPrevSwing() { return 0; }

    @Override
    public void setNoInterpSwingAmount(float noInterpSwingAmount) { }

    @Override
    public void setNoInterpSwing(float noInterpSwing) { }

    @Override
    public void setNoInterpPrevSwing(float noInterpPrevSwing) { }

    @Override
    public boolean isNoInterping() { return false; }

    @Override
    public void setNoInterping(boolean noInterping) { }
}