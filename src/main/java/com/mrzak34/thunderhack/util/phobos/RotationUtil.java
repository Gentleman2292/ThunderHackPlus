package com.mrzak34.thunderhack.util.phobos;

import com.mrzak34.thunderhack.Thunderhack;
import com.mrzak34.thunderhack.util.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockAccess;

import java.util.function.BiPredicate;

import static com.mrzak34.thunderhack.util.Util.mc;
import static com.mrzak34.thunderhack.util.RotationHelper.interpolatedEyePos;
import static com.mrzak34.thunderhack.util.RotationHelper.interpolatedEyeVec;

public class RotationUtil
{



    public static float[] getRotations(BlockPos pos, EnumFacing facing)
    {
        return getRotations(pos, facing, mc.player);
    }

    public static float[] getRotations(BlockPos pos, EnumFacing facing, Entity from)
    {
        return getRotations(pos, facing, from, mc.world, mc.world.getBlockState(pos));
    }

    public static float[] getRotations(BlockPos pos,
                                       EnumFacing facing,
                                       Entity from,
                                       IBlockAccess world,
                                       IBlockState state)
    {
        AxisAlignedBB bb = state.getBoundingBox(world, pos);

        double x = pos.getX() + (bb.minX + bb.maxX) / 2.0;
        double y = pos.getY() + (bb.minY + bb.maxY) / 2.0;
        double z = pos.getZ() + (bb.minZ + bb.maxZ) / 2.0;

        if (facing != null)
        {
            x += facing.getDirectionVec().getX() * ((bb.minX + bb.maxX) / 2.0);
            y += facing.getDirectionVec().getY() * ((bb.minY + bb.maxY) / 2.0);
            z += facing.getDirectionVec().getZ() * ((bb.minZ + bb.maxZ) / 2.0);
        }

        return getRotations(x, y, z, from);
    }

    /**
     * Convenience function calling
     * {@link RotationUtil#getRotations(double, double, double)}.
     */
    public static float[] getRotationsToTopMiddle(BlockPos pos)
    {
        return getRotations(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }

    // TODO: check why getRotationsToTopMiddle doesnt do that and if thats ok!
    public static float[] getRotationsToTopMiddleUp(BlockPos pos)
    {
        return getRotations(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
    }

    public static float[] getRotationsMaxYaw(BlockPos pos, float max, float current)
    {
        return new float[]
                {
                        updateRotation(current, getRotationsToTopMiddle(pos)[0], max),
                        getRotationsToTopMiddle(pos)[1]
                };
    }

    public static float[] getRotations(Entity entity, double height)
    {
        return getRotations(entity.posX,
                entity.posY + entity.getEyeHeight() * height,
                entity.posZ);
    }

    /**
     * Convenience function calling
     * {@link RotationUtil#getRotations(double, double, double)},
     * for the entities posX, posY + entity.getEyeHeight(), entity.posZ.
     */
    public static float[] getRotations(Entity entity)
    {
        return getRotations(entity.posX,
                entity.posY + entity.getEyeHeight(),
                entity.posZ);
    }

    public static float[] getRotationsMaxYaw(Entity entity,
                                             float max,
                                             float current)
    {
        return new float[]{MathHelper.wrapDegrees(
                updateRotation(current, getRotations(entity)[0], max)),
                getRotations(entity)[1]
        };
    }

    /**
     * Convenience function calling
     * {@link RotationUtil#getRotations(double, double, double)}.
     * for the entities x, y, z
     */
    public static float[] getRotations(Vec3d vec3d)
    {
        return getRotations(vec3d.x, vec3d.y, vec3d.z);
    }

    public static float[] getRotations(double x, double y, double z)
    {
        return getRotations(x, y, z, mc.player);
    }

    /**
     * Returns a float array of length 2, containing
     * yaw at index 0, and pitch at index 1 looking from
     * the given entity towards the given coordinates.
     *
     * @param x the x coordinate of the point you want to look at.
     * @param y the y coordinate of the point you want to look at.
     * @param z the z coordinate of the point you want to look at.
     * @param f the Entity supposed to rotate to the coordinates
     * @return yaw and pitch in direction of the coordinate.
     */
    public static float[] getRotations(double x, double y, double z, Entity f)
    {
        return getRotations(x, y, z, f.posX, f.posY, f.posZ, f.getEyeHeight());
    }

    public static float[] getRotations(double x,
                                       double y,
                                       double z,
                                       double fromX,
                                       double fromY,
                                       double fromZ,
                                       float fromHeight)
    {
        double xDiff = x - fromX;
        double yDiff = y - (fromY + fromHeight);
        double zDiff = z - fromZ;
        double dist = MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);

        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-(Math.atan2(yDiff, dist) * 180.0 / Math.PI));
        // Is there a better way than to use the previous yaw?
        float prevYaw = Thunderhack.rotationManager.getServerYaw();
        float diff = yaw - prevYaw;

        if (diff < -180.0f || diff > 180.0f)
        {
            float round = Math.round(Math.abs(diff / 360.0f));
            diff = diff < 0.0f ? diff + 360.0f * round : diff - (360.0f * round);
        }

        return new float[]{ prevYaw + diff, pitch };
    }

    /**
     * Convenience function calling
     * {@link RotationUtil#inFov(double, double, double)}
     */
    public static boolean inFov(Entity entity)
    {
        return inFov(entity.posX,
                entity.posY + entity.getEyeHeight() / 2,
                entity.posZ);
    }

    /**
     * Convenience function calling
     * {@link RotationUtil#inFov(double, double, double)}
     */
    public static boolean inFov(BlockPos pos)
    {
        return inFov(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }
    /**
     * Returns if the given coordinate lies within the players fov,
     * more accurately: if the angle from the vector, from player to
     * point, to the look vector of the player is smaller than the
     * fov / 2.
     *
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @param z the z coordinate.
     * @return <tt>true</tt> if x,y,z lies within players fov.
     */
    public static boolean inFov(double x, double y, double z)
    {
        /*
        // TODO: When checking this in a RenderEvent just use one Frustum...
        Entity renderEntity = RenderUtil.getEntity();
        if (renderEntity == null)
        {
            return false;
        }
        Frustum frustum = Interpolation.createFrustum(renderEntity);
        return frustum.isBoundingBoxInFrustum(
                new AxisAlignedBB(x-1,y-1,x-1,x+1,y+1,z+1));
         */

        // TODO: use RenderEntity arghhhhhhhhhh all of this is so messed up???
        return getAngle(x, y, z) < mc.gameSettings.fovSetting / 2.0f;
    }

    /**
     * Returns the Angle between the players lookVec
     * and the entities position (y + yOffset).
     *
     * @param entity  the entity to get the angle to.
     * @param yOffset the offset to the entities yPosition.
     * @return the angle to the entity.
     */
    public static double getAngle(Entity entity, double yOffset)
    {
        Vec3d vec3d = MathUtil.fromTo(interpolatedEyePos(),
                entity.posX,
                entity.posY + yOffset,
                entity.posZ);

        return MathUtil.angle(vec3d, interpolatedEyeVec());
    }

    public static double getAngle(double x, double y, double z)
    {
        Vec3d vec3d = MathUtil.fromTo(interpolatedEyePos(),
                x, y, z);

        return MathUtil.angle(vec3d, interpolatedEyeVec());
    }

    /**
     * Transforms given yaw and pitch into a Vec3d.
     *
     * @param yaw   the yaw.
     * @param pitch the pitch.
     * @return the look vector for yaw and pitch.
     */
    public static Vec3d getVec3d(float yaw, float pitch)
    {
        float vx = -MathHelper.sin(MathUtil.rad(yaw)) * MathHelper.cos(MathUtil.rad(pitch));
        float vz = MathHelper.cos(MathUtil.rad(yaw)) * MathHelper.cos(MathUtil.rad(pitch));
        float vy = -MathHelper.sin(MathUtil.rad(pitch));
        return new Vec3d(vx, vy, vz);
    }



    public static boolean isLegit(BlockPos pos)
    {
        return isLegit(pos, null);
    }

    public static boolean isLegit(BlockPos pos, EnumFacing facing)
    {
        return isLegit(pos, facing, mc.world);
    }

    public static boolean isLegit(BlockPos pos,
                                  EnumFacing facing,
                                  IBlockAccess world)
    {
        RayTraceResult ray = rayTraceTo(pos, world);
        //noinspection ConstantConditions
        return ray != null
                && ray.getBlockPos() != null
                && ray.getBlockPos().equals(pos)
                && (facing == null
                || ray.sideHit == facing);
    }

    public static RayTraceResult rayTraceTo(BlockPos pos, IBlockAccess world)
    {
        return rayTraceTo(pos, world, (b, p) -> p.equals(pos));
    }

    public static RayTraceResult rayTraceTo(BlockPos pos,
                                            IBlockAccess world,
                                            BiPredicate<Block, BlockPos> check)
    {
        return rayTraceWithYP(pos, world,
                Thunderhack.rotationManager.getServerYaw(),
                Thunderhack.rotationManager.getServerPitch(), check);
    }

    public static RayTraceResult rayTraceWithYP(BlockPos pos, IBlockAccess world, float yaw, float pitch, BiPredicate<Block, BlockPos> check)
    {
        Entity from = mc.player;
        Vec3d start = Thunderhack.positionManager.getVec().add(0, from.getEyeHeight(), 0);
        Vec3d look = RotationUtil.getVec3d(yaw, pitch);
        double d = from.getDistance(pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5) + 1;

        Vec3d end = start.add(look.x * d, look.y * d, look.z * d);

        return RayTracer.trace(mc.world,
                world,
                start,
                end,
                true,
                false,
                true,
                check);
    }


    public static float[] faceSmoothly(double curYaw,
                                       double curPitch,
                                       double intendedYaw,
                                       double intendedPitch,
                                       double yawSpeed,
                                       double pitchSpeed)
    {
        float yaw = updateRotation((float) curYaw,
                (float) intendedYaw,
                (float) yawSpeed);

        float pitch = updateRotation((float) curPitch,
                (float) intendedPitch,
                (float) pitchSpeed);

        return new float[]{yaw, pitch};
    }

    public static double angle(float[] rotation1, float[] rotation2)
    {
        Vec3d r1Vec = getVec3d(rotation1[0], rotation1[1]);
        Vec3d r2Vec = getVec3d(rotation2[0], rotation2[1]);
        return MathUtil.angle(r1Vec, r2Vec);
    }

    /**
     * Function to update a single rotation value
     *
     * @param current  current value
     * @param intended target value
     * @param factor   max rate
     * @return target rotation
     */
    public static float updateRotation(float current,
                                       float intended,
                                       float factor)
    {
        float updated = MathHelper.wrapDegrees(intended - current);

        if (updated > factor)
        {
            updated = factor;
        }

        if (updated < -factor)
        {
            updated = -factor;
        }

        return current + updated;
    }

    public static int getDirection4D()
    {
        return MathHelper.floor(
                (mc.player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
    }



}