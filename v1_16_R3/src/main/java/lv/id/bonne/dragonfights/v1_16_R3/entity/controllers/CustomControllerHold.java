package lv.id.bonne.dragonfights.v1_16_R3.entity.controllers;


import lv.id.bonne.dragonfights.v1_16_R3.entity.BentoBoxEnderDragon;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.HeightMap;
import net.minecraft.server.v1_16_R3.MathHelper;
import net.minecraft.server.v1_16_R3.PathEntity;
import net.minecraft.server.v1_16_R3.PathfinderTargetCondition;
import net.minecraft.server.v1_16_R3.Vec3D;


/**
 * Phase for continuing current path
 */
public class CustomControllerHold extends AbstractCustomController
{
    public CustomControllerHold(BentoBoxEnderDragon enderDragon)
    {
        super(enderDragon);
    }


    @Override
    public void init()
    {
        this.path = null;
        this.targetLocation = null;
    }


    @Override
    public void movementTick()
    {
        double distance = this.targetLocation == null ? 0.0D :
            this.targetLocation.c(this.enderDragon.locX(), this.enderDragon.locY(), this.enderDragon.locZ());

        if (distance < 100.0D || distance > 22500.0D || this.enderDragon.positionChanged || this.enderDragon.v)
        {
            this.findPath();
        }
    }


    @Override
    public CustomControllerPhase<CustomControllerHold> getControllerPhase()
    {
        return CustomControllerPhase.HOLDING_PATTERN;
    }


    @Override
    public Vec3D getTargetLocation()
    {
        return this.targetLocation;
    }


    /**
     * This method finds closest path node for dragon.
     */
    private void findPath()
    {
        int randomIndex;

        if (this.path != null && this.path.c())
        {
            BlockPosition blockPosition =
                this.enderDragon.world.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES,
                    this.enderDragon.getPortalLocation());

            randomIndex = 5;

            if (this.enderDragon.getRandom().nextInt(randomIndex + 3) == 0)
            {
                this.enderDragon.getCustomControllerManager().setControllerPhase(CustomControllerPhase.LANDING_APPROACH);
                return;
            }

            double distance = 64.0D;
            EntityHuman entityHuman = this.enderDragon.world.a(CustomControllerHold.TARGET_FINDER,
                blockPosition.getX(),
                blockPosition.getY(),
                blockPosition.getZ());

            if (entityHuman != null)
            {
                distance = blockPosition.a(entityHuman.getPositionVector(), true) / 512.0D;
            }

            if (entityHuman != null && !entityHuman.abilities.isInvulnerable &&
                (this.enderDragon.getRandom().nextInt(MathHelper.a((int) distance) + 2) == 0 ||
                    this.enderDragon.getRandom().nextInt(randomIndex + 2) == 0))
            {
                this.attack(entityHuman);
                return;
            }
        }

        if (this.path == null || this.path.c())
        {
            int closestPathNode = this.enderDragon.searchClosestPathPoint();

            randomIndex = closestPathNode;

            if (this.enderDragon.getRandom().nextInt(this.enderDragon.getPathPointCount()) == 0)
            {
                this.increase = !this.increase;
            }

            if (this.increase)
            {
                ++randomIndex;
            }
            else
            {
                --randomIndex;
            }

            if (randomIndex < 0)
            {
                randomIndex += this.enderDragon.getPathPointCount();
            }
            else if (randomIndex >= this.enderDragon.getPathPointCount())
            {
                randomIndex = randomIndex - this.enderDragon.getPathPointCount();
            }

            this.path = this.enderDragon.searchPath(closestPathNode, randomIndex, null);

            if (this.path != null)
            {
                this.path.a();
            }
        }

        this.assignTargetLocation();
    }


    /**
     * This method change faze to strafe and set given entity as target.
     * @param entityHuman target entity.
     */
    private void attack(EntityHuman entityHuman)
    {
        this.enderDragon.getCustomControllerManager().setControllerPhase(CustomControllerPhase.STRAFE_PLAYER);
        this.enderDragon.getCustomControllerManager().getPhase(CustomControllerPhase.STRAFE_PLAYER).setTarget(entityHuman);
    }


    /**
     * This method assigns target location if there exists path.
     */
    private void assignTargetLocation()
    {
        if (this.path != null && !this.path.c())
        {
            BlockPosition targetLocation = this.path.g();

            this.path.a();
            double y;

            do
            {
                y = ((float) targetLocation.getY() + this.enderDragon.getRandom().nextFloat() * 20.0F);
            }
            while (y < (double) targetLocation.getY());

            this.targetLocation = new Vec3D(
                targetLocation.getX(),
                Math.max(y, this.enderDragon.getMaxAltitude()),
                targetLocation.getZ());
        }
    }


    /**
     * Path for entity.
     */
    private PathEntity path;

    /**
     * Target location.
     */
    private Vec3D targetLocation;

    /**
     * Increase or decrease direction.
     */
    private boolean increase;

    /**
     * Target finder method.
     */
    private static final PathfinderTargetCondition TARGET_FINDER = new PathfinderTargetCondition().a(64.0D);
}
