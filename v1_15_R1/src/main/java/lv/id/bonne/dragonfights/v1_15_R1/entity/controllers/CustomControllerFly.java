package lv.id.bonne.dragonfights.v1_15_R1.entity.controllers;

import lv.id.bonne.dragonfights.v1_15_R1.entity.BentoBoxEnderDragon;
import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.HeightMap;
import net.minecraft.server.v1_15_R1.PathEntity;
import net.minecraft.server.v1_15_R1.Vec3D;


/**
 * Process dragon flying.
 */
public class CustomControllerFly extends AbstractCustomController
{
    public CustomControllerFly(BentoBoxEnderDragon enderDragon)
    {
        super(enderDragon);
    }


    @Override
    public void init()
    {
        this.hasPath = true;
        this.path = null;
        this.targetLocation = null;
    }


    @Override
    public void movementTick()
    {
        if (!this.hasPath && this.path != null)
        {
            BlockPosition highestBlock = this.enderDragon.world.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES,
                this.enderDragon.getPortalLocation());

            if (!highestBlock.a(this.enderDragon.getPositionVector(), 10.0D))
            {
                this.enderDragon.getCustomControllerManager().setControllerPhase(CustomControllerPhase.HOLDING_PATTERN);
            }
        }
        else
        {
            this.hasPath = false;
            this.findPath();
        }
    }


    @Override
    public Vec3D getTargetLocation()
    {
        return this.targetLocation;
    }


    @Override
    public CustomControllerPhase<CustomControllerFly> getControllerPhase()
    {
        return CustomControllerPhase.TAKEOFF;
    }


    /**
     * This method tries to find next path and node where dragon should fly.
     */
    private void findPath()
    {
        int closestNode = this.enderDragon.searchClosestPathPoint();
        Vec3D vec3d = this.enderDragon.getMovementVector(1.0F);
        int targetNode = this.enderDragon.searchClosestPathPoint(
            -vec3d.x * this.enderDragon.getPathPointDistance() + this.enderDragon.xOffset(),
            Math.min(this.enderDragon.getMinAltitude() + 40.0D, this.enderDragon.getMaxAltitude()),
            -vec3d.z * this.enderDragon.getPathPointDistance() + this.enderDragon.zOffset());

        this.path = this.enderDragon.searchPath(closestNode, targetNode, null);
        this.assignTargetLocation();
    }


    /**
     * This method gets target node location.
     */
    private void assignTargetLocation()
    {
        if (this.path != null)
        {
            this.path.a();

            if (!this.path.b())
            {
                Vec3D targetLocation = this.path.g();

                this.path.a();
                double y;

                do
                {
                    y = (float) targetLocation.getY() + this.enderDragon.getRandom().nextFloat() * 20.0F;
                }
                while (y < targetLocation.getY());

                this.targetLocation = new Vec3D(
                    targetLocation.getX(),
                    Math.max(y, this.enderDragon.getMaxAltitude()),
                    targetLocation.getZ());
            }
        }
    }


    /**
     * Indicate that path exists.
     */
    private boolean hasPath;

    /**
     * Path for dragon.
     */
    private PathEntity path;

    /**
     * Target location.
     */
    private Vec3D targetLocation;
}
