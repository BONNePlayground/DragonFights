package lv.id.bonne.dragonfights.v1_16_R2.entity.controllers;

import lv.id.bonne.dragonfights.v1_16_R2.entity.BentoBoxEnderDragon;
import net.minecraft.server.v1_16_R2.MathHelper;
import net.minecraft.server.v1_16_R2.Vec3D;


/**
 * Abstract controller for processing non-landed phases.
 */
public abstract class AbstractCustomController implements ICustomController
{
    /**
     * Instantiates a new Abstract custom controller.
     *
     * @param entity the entity
     */
    public AbstractCustomController(BentoBoxEnderDragon entity)
    {
        this.enderDragon = entity;
    }


    @Override
    public void movementTick()
    {
    }


    @Override
    public float getConstant()
    {
        return 0.6F;
    }


    @Override
    public Vec3D getTargetLocation()
    {
        return null;
    }


    @Override
    public float getRotation()
    {
        float f = MathHelper.sqrt(BentoBoxEnderDragon.area(this.enderDragon.getMot())) + 1.0F;
        float f1 = Math.min(f, 40.0F);

        return 0.7F / f1 / f;
    }


    /**
	 * Instance of the ender dragon.
     */
    protected final BentoBoxEnderDragon enderDragon;
}
