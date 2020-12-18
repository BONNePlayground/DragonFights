package lv.id.bonne.dragonfights.v1_16_R1.entity.controllers;

import lv.id.bonne.dragonfights.v1_16_R1.entity.BentoBoxEnderDragon;
import net.minecraft.server.v1_16_R1.EntityHuman;
import net.minecraft.server.v1_16_R1.MathHelper;
import net.minecraft.server.v1_16_R1.PathfinderTargetCondition;
import net.minecraft.server.v1_16_R1.Vec3D;


/**
 * Some sort of searching for next entity to attack.
 */
public class CustomControllerLandedSearch extends AbstractCustomController
{
    public CustomControllerLandedSearch(BentoBoxEnderDragon enderDragon)
    {
        super(enderDragon);
        // Target that is closer than 10 blocks?
        this.target = new PathfinderTargetCondition().a(20.0D).a((livingEntity) ->
            Math.abs(livingEntity.locY() - enderDragon.locY()) <= 10.0D);
    }


    @Override
    public void init()
    {
        this.counter = 0;
    }


    @Override
    public void movementTick()
    {
        ++this.counter;
        EntityHuman entityHuman = this.enderDragon.world.a(this.target,
            this.enderDragon, this.enderDragon.locX(), this.enderDragon.locY(), this.enderDragon.locZ());

        if (entityHuman != null)
        {
            if (this.counter > 25)
            {
                this.enderDragon.getCustomControllerManager().setControllerPhase(CustomControllerPhase.SITTING_ATTACKING);
            }
            else
            {
                Vec3D vec3d = new Vec3D(entityHuman.locX() - this.enderDragon.locX(),
                    0.0D,
                    entityHuman.locZ() - this.enderDragon.locZ()).d();
                Vec3D vec3d1 = new Vec3D(
                    MathHelper.sin(this.enderDragon.yaw * 0.017453292F),
                    0.0D,
                    -MathHelper.cos(this.enderDragon.yaw * 0.017453292F)).d();
                float f = (float) vec3d1.b(vec3d);
                float f1 = (float) (Math.acos(f) * 57.2957763671875D) + 0.5F;

                if (f1 < 0.0F || f1 > 10.0F)
                {
                    double d0 = entityHuman.locX() - this.enderDragon.bv.locX();
                    double d1 = entityHuman.locZ() - this.enderDragon.bv.locZ();
                    double d2 = MathHelper.a(MathHelper.g(180.0D - MathHelper.d(d0, d1) * 57.2957763671875D - (double) this.enderDragon.yaw),
                        -100.0D,
                        100.0D);

                    this.enderDragon.bA *= 0.8F;
                    float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1) + 1.0F;
                    float f3 = f2;

                    if (f2 > 40.0F)
                    {
                        f2 = 40.0F;
                    }

                    this.enderDragon.bA = (float) ((double) this.enderDragon.bA + d2 * (double) (0.7F / f2 / f3));
                    this.enderDragon.yaw += this.enderDragon.bA;
                }
            }
        }
        else if (this.counter >= 100)
        {
            entityHuman = this.enderDragon.world.a(CustomControllerLandedSearch.targetFinder,
                this.enderDragon,
                this.enderDragon.locX(),
                this.enderDragon.locY(),
                this.enderDragon.locZ());
            this.enderDragon.getCustomControllerManager().setControllerPhase(CustomControllerPhase.TAKEOFF);

            if (entityHuman != null)
            {
                this.enderDragon.getCustomControllerManager().setControllerPhase(CustomControllerPhase.CHARGING_PLAYER);
                this.enderDragon.getCustomControllerManager().getPhase(CustomControllerPhase.CHARGING_PLAYER).
                    setTargetLocation(new Vec3D(entityHuman.locX(), entityHuman.locY(), entityHuman.locZ()));
            }
        }
    }


    @Override
    public boolean isLanded()
    {
        return true;
    }


    @Override
    public CustomControllerPhase<CustomControllerLandedSearch> getControllerPhase()
    {
        return CustomControllerPhase.SITTING_SCANNING;
    }


    /**
     * Target for next attack.
     */
    private final PathfinderTargetCondition target;

    /**
     * Some kind of counter.
     */
    private int counter;

    /**
     * Target finding method.
     */
    private static final PathfinderTargetCondition targetFinder = new PathfinderTargetCondition().a(150.0D);
}
