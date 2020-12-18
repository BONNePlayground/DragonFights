package lv.id.bonne.dragonfights.v1_16_R2.entity.controllers;

import lv.id.bonne.dragonfights.v1_16_R2.entity.BentoBoxEnderDragon;
import net.minecraft.server.v1_16_R2.Vec3D;


/**
 * Some sort of charge controller.
 */
public class CustomControllerCharge extends AbstractCustomController
{
    public CustomControllerCharge(BentoBoxEnderDragon enderDragon)
    {
        super(enderDragon);
    }


    @Override
    public void init()
    {
        this.targetLocation = null;
        this.counter = 0;
    }


    @Override
    public void movementTick()
    {
        if (this.targetLocation == null)
        {
            this.enderDragon.getCustomControllerManager().setControllerPhase(CustomControllerPhase.HOLDING_PATTERN);
        }
        else if (this.counter > 0 && this.counter++ >= 10)
        {
            this.enderDragon.getCustomControllerManager().setControllerPhase(CustomControllerPhase.HOLDING_PATTERN);
        }
        else
        {
            double distance = this.targetLocation.c(this.enderDragon.locX(), this.enderDragon.locY(), this.enderDragon.locZ());

            if (distance < 100.0D || distance > 22500.0D || this.enderDragon.positionChanged || this.enderDragon.v)
            {
                ++this.counter;
            }
        }
    }


    @Override
    public float getConstant()
    {
        return 3.0F;
    }


    public void setTargetLocation(Vec3D targetLocation)
    {
        this.targetLocation = targetLocation;
    }


    @Override
    public Vec3D getTargetLocation()
    {
        return this.targetLocation;
    }


    @Override
    public CustomControllerPhase<CustomControllerCharge> getControllerPhase()
    {
        return CustomControllerPhase.CHARGING_PLAYER;
    }


    /**
     * Target location.
     */
    private Vec3D targetLocation;

    /**
     * Some kind of counter.
     */
    private int counter;
}
