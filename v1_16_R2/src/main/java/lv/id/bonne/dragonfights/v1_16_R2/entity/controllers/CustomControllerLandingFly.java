package lv.id.bonne.dragonfights.v1_16_R2.entity.controllers;


import lv.id.bonne.dragonfights.v1_16_R2.entity.BentoBoxEnderDragon;
import net.minecraft.server.v1_16_R2.BlockPosition;
import net.minecraft.server.v1_16_R2.EntityHuman;
import net.minecraft.server.v1_16_R2.HeightMap;
import net.minecraft.server.v1_16_R2.PathEntity;
import net.minecraft.server.v1_16_R2.PathPoint;
import net.minecraft.server.v1_16_R2.PathfinderTargetCondition;
import net.minecraft.server.v1_16_R2.Vec3D;


/**
 * This controller process landing search phase.
 */
public class CustomControllerLandingFly extends AbstractCustomController
{
    public CustomControllerLandingFly(BentoBoxEnderDragon enderDragon)
    {
        super(enderDragon);
    }


    @Override
    public CustomControllerPhase<CustomControllerLandingFly> getControllerPhase()
    {
        return CustomControllerPhase.LANDING_APPROACH;
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
    public Vec3D getTargetLocation()
    {
        return this.targetLocation;
    }


    /**
     * This method finds closest path from current position till portal for dragon.
     */
    private void findPath()
    {
        if (this.path == null || this.path.c())
        {
            int closestPathNode = this.enderDragon.searchClosestPathPoint();
            BlockPosition targetPosition = this.enderDragon.world.getHighestBlockYAt(
                HeightMap.Type.MOTION_BLOCKING_NO_LEAVES,
                this.enderDragon.getPortalLocation());
            EntityHuman entityHuman = this.enderDragon.world.a(CustomControllerLandingFly.TARGET_FINDER,
                targetPosition.getX(),
                targetPosition.getY(),
                targetPosition.getZ());

            int targetPathNode;

            if (entityHuman != null)
            {
                Vec3D vec3d = new Vec3D(entityHuman.locX(), 0.0D, entityHuman.locZ()).d();

                targetPathNode = this.enderDragon.searchClosestPathPoint(
                    -vec3d.x * this.enderDragon.getPathPointDistance() + this.enderDragon.xOffset(),
                    Math.min(this.enderDragon.getMinAltitude() + 40.0D, this.enderDragon.getMaxAltitude()),
                    -vec3d.z * this.enderDragon.getPathPointDistance() + this.enderDragon.zOffset());
            }
            else
            {
                targetPathNode = this.enderDragon.searchClosestPathPoint(
                    this.enderDragon.getPathPointDistance() + this.enderDragon.xOffset(),
                    targetPosition.getY(),
                    this.enderDragon.zOffset());
            }

            PathPoint flyThroughNode = new PathPoint(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ());

            this.path = this.enderDragon.searchPath(closestPathNode, targetPathNode, flyThroughNode);

            if (this.path != null)
            {
                this.path.a();
            }
        }

        this.assignTargetLocation();

        if (this.path != null && this.path.c())
        {
            this.enderDragon.getCustomControllerManager().setControllerPhase(CustomControllerPhase.LANDING);
        }
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
     * Target finder condition.
     */
    private static final PathfinderTargetCondition TARGET_FINDER = new PathfinderTargetCondition().a(128.0D);
}
