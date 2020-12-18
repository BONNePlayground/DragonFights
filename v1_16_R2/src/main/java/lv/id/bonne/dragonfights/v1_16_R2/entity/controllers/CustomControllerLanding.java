package lv.id.bonne.dragonfights.v1_16_R2.entity.controllers;


import lv.id.bonne.dragonfights.v1_16_R2.entity.BentoBoxEnderDragon;
import net.minecraft.server.v1_16_R2.HeightMap;
import net.minecraft.server.v1_16_R2.MathHelper;
import net.minecraft.server.v1_16_R2.Vec3D;


/**
 * This process dragon landing phase.
 */
public class CustomControllerLanding extends AbstractCustomController
{
    public CustomControllerLanding(BentoBoxEnderDragon enderDragon)
    {
        super(enderDragon);
    }


    @Override
    public void init()
    {
        this.targetLocation = null;
    }


    @Override
    public void movementTick()
    {
        if (this.targetLocation == null)
        {
            this.targetLocation = Vec3D.c(this.enderDragon.world.getHighestBlockYAt(
                HeightMap.Type.MOTION_BLOCKING_NO_LEAVES,
                this.enderDragon.getPortalLocation()));
        }

        if (this.targetLocation.c(this.enderDragon.locX(), this.enderDragon.locY(), this.enderDragon.locZ()) < 1.0D)
        {
            this.enderDragon.getCustomControllerManager().getPhase(CustomControllerPhase.SITTING_FLAMING).resetAttackCounter();
            this.enderDragon.getCustomControllerManager().setControllerPhase(CustomControllerPhase.SITTING_SCANNING);
        }
    }


    @Override
    public float getConstant()
    {
        return 1.5F;
    }


    @Override
    public float getRotation()
    {
        float f = MathHelper.sqrt(BentoBoxEnderDragon.area(this.enderDragon.getMot())) + 1.0F;
        float f1 = Math.min(f, 40.0F);

        return f1 / f;
    }


    @Override
    public Vec3D getTargetLocation()
    {
        return this.targetLocation;
    }


    @Override
    public CustomControllerPhase<CustomControllerLanding> getControllerPhase()
    {
        return CustomControllerPhase.LANDING;
    }


	/**
	 * Portal location.
     */
    private Vec3D targetLocation;
}
