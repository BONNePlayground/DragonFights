package lv.id.bonne.dragonfights.v1_16_R3.entity.controllers;

import lv.id.bonne.dragonfights.v1_16_R3.entity.BentoBoxEnderDragon;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.EntityAreaEffectCloud;
import net.minecraft.server.v1_16_R3.MathHelper;
import net.minecraft.server.v1_16_R3.MobEffect;
import net.minecraft.server.v1_16_R3.MobEffects;
import net.minecraft.server.v1_16_R3.Particles;
import net.minecraft.server.v1_16_R3.Vec3D;


/**
 * This controller process flame attack?
 */
public class CustomControllerLandedFlame extends AbstractCustomController
{
    public CustomControllerLandedFlame(BentoBoxEnderDragon enderDragon)
    {
        super(enderDragon);
    }


    @Override
    public void init()
    {
        this.counter = 0;
        ++this.attackCounter;
    }


    @Override
    public void movementTick()
    {
        ++this.counter;
        if (this.counter >= 200)
        {
            if (this.attackCounter >= 4)
            {
                this.enderDragon.getCustomControllerManager().setControllerPhase(CustomControllerPhase.TAKEOFF);
            }
            else
            {
                this.enderDragon.getCustomControllerManager().setControllerPhase(CustomControllerPhase.SITTING_SCANNING);
            }
        }
        else if (this.counter == 10)
        {
            Vec3D vec3d = new Vec3D(this.enderDragon.bo.locX() - this.enderDragon.locX(),
                0.0D,
                this.enderDragon.bo.locZ() - this.enderDragon.locZ()).d();

            double x = this.enderDragon.bo.locX() + vec3d.x * 5.0D / 2.0D;
            double z = this.enderDragon.bo.locZ() + vec3d.z * 5.0D / 2.0D;
            double y = this.enderDragon.bo.e(0.5D);
            double offset = y;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition =
                new BlockPosition.MutableBlockPosition(x, y, z);

            while (this.enderDragon.world.isEmpty(blockposition_mutableblockposition))
            {
                --offset;
                if (offset < 0.0D)
                {
                    offset = y;
                    break;
                }

                blockposition_mutableblockposition.c(x, offset, z);
            }

            offset = MathHelper.floor(offset) + 1;
            this.effect = new EntityAreaEffectCloud(this.enderDragon.world, x, offset, z);
            this.effect.setSource(this.enderDragon);
            this.effect.setRadius(5.0F);
            this.effect.setDuration(200);
            this.effect.setParticle(Particles.DRAGON_BREATH);
            this.effect.addEffect(new MobEffect(MobEffects.HARM));
            this.enderDragon.world.addEntity(this.effect);
        }
    }


    @Override
    public void stop()
    {
        if (this.effect != null)
        {
            this.effect.die();
            this.effect = null;
        }
    }


    @Override
    public boolean isLanded()
    {
        return true;
    }


    @Override
    public CustomControllerPhase<CustomControllerLandedFlame> getControllerPhase()
    {
        return CustomControllerPhase.SITTING_FLAMING;
    }


    public void resetAttackCounter()
    {
        this.attackCounter = 0;
    }


    /**
     * Some kind of counter.
     */
    private int counter;

    /**
     * Number of performed attacks.
     */
    private int attackCounter;

    /**
     * Effect that is done in each attack.
     */
    private EntityAreaEffectCloud effect;
}
