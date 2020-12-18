package lv.id.bonne.dragonfights.v1_15_R1.entity.controllers;

import lv.id.bonne.dragonfights.v1_15_R1.entity.BentoBoxEnderDragon;
import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.HeightMap;
import net.minecraft.server.v1_15_R1.Vec3D;


/**
 * Process ender dragon dying.
 */
public class CustomControllerDying extends AbstractCustomController
{
    public CustomControllerDying(BentoBoxEnderDragon enderDragon)
    {
        super(enderDragon);
    }


    @Override
    public void init()
    {
        this.dyingLocation = null;
    }


    @Override
    public void movementTick()
    {
        if (this.dyingLocation == null)
        {
            BlockPosition blockPosition = this.enderDragon.world.getHighestBlockYAt(
                HeightMap.Type.MOTION_BLOCKING,
                this.enderDragon.getPortalLocation());
            this.dyingLocation = new Vec3D(
                blockPosition.getX() + 0.5D,
                blockPosition.getY(),
                blockPosition.getZ() + 0.5D);
        }

        double distance = this.dyingLocation.c(this.enderDragon.locX(), this.enderDragon.locY(), this.enderDragon.locZ());

        if (distance >= 100.0D && distance <= 22500.0D && !this.enderDragon.positionChanged && !this.enderDragon.v)
        {
            this.enderDragon.setHealth(1.0F);
        }
        else
        {
            this.enderDragon.setHealth(0.0F);
        }
    }


    @Override
    public float getConstant()
    {
        return 3.0F;
    }


    @Override
    public Vec3D getTargetLocation()
    {
        return this.dyingLocation;
    }


    @Override
    public CustomControllerPhase<CustomControllerDying> getControllerPhase()
    {
        return CustomControllerPhase.DYING;
    }


    /**
     * Dying location.
     */
    private Vec3D dyingLocation;
}
