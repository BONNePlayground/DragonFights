package lv.id.bonne.dragonfights.v1_16_R1.entity.controllers;


import lv.id.bonne.dragonfights.v1_16_R1.entity.BentoBoxEnderDragon;
import net.minecraft.server.v1_16_R1.Vec3D;


/**
 * Initial dragon phase.
 */
public class CustomControllerHover extends AbstractCustomController
{
    public CustomControllerHover(BentoBoxEnderDragon enderDragon)
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
            this.targetLocation = this.enderDragon.getPositionVector();
        }
    }


    @Override
    public boolean isLanded()
    {
        return true;
    }


    @Override
    public float getConstant()
    {
        return 1.0F;
    }


    @Override
    public Vec3D getTargetLocation()
    {
        return this.targetLocation;
    }


    @Override
    public CustomControllerPhase<CustomControllerHover> getControllerPhase()
    {
        return CustomControllerPhase.HOVER;
    }


	/**
	 * Target location for phase.
     */
    private Vec3D targetLocation;
}