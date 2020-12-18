package lv.id.bonne.dragonfights.v1_16_R2.entity.controllers;


import lv.id.bonne.dragonfights.v1_16_R2.entity.BentoBoxEnderDragon;
import net.minecraft.server.v1_16_R2.BlockPosition;
import net.minecraft.server.v1_16_R2.EntityDragonFireball;
import net.minecraft.server.v1_16_R2.EntityLiving;
import net.minecraft.server.v1_16_R2.MathHelper;
import net.minecraft.server.v1_16_R2.PathEntity;
import net.minecraft.server.v1_16_R2.PathPoint;
import net.minecraft.server.v1_16_R2.Vec3D;


public class CustomControllerStrafe extends AbstractCustomController
{
    public CustomControllerStrafe(BentoBoxEnderDragon enderDragon)
    {
        super(enderDragon);
    }


    @Override
    public void init()
    {
        this.counter = 0;
        this.targetLocation = null;
        this.path = null;
        this.target = null;
    }


    @Override
    public void movementTick()
    {
        if (this.target == null)
        {
            this.enderDragon.getCustomControllerManager().setControllerPhase(CustomControllerPhase.HOLDING_PATTERN);
        }
        else
        {
            double x;
            double z;

            if (this.path != null && this.path.c())
            {
                x = this.target.locX();
                z = this.target.locZ();
                double xMovement = x - this.enderDragon.locX();
                double zMovement = z - this.enderDragon.locZ();

                double movementSquare = MathHelper.sqrt(xMovement * xMovement + zMovement * zMovement);
                double y = Math.min(0.4000000059604645D + movementSquare / 80.0D - 1.0D, 10.0D);

                this.targetLocation = new Vec3D(x, this.target.locY() + y, z);
            }

            x = this.targetLocation == null ? 0.0D :
                this.targetLocation.c(this.enderDragon.locX(), this.enderDragon.locY(), this.enderDragon.locZ());

            if (x < 100.0D || x > 22500.0D)
            {
                this.findPath();
            }

            if (this.target.h(this.enderDragon) < 4096.0D)
            {
                if (this.enderDragon.hasLineOfSight(this.target))
                {
                    ++this.counter;
                    Vec3D vec3d = new Vec3D(this.target.locX() - this.enderDragon.locX(),
                        0.0D,
                        this.target.locZ() - this.enderDragon.locZ()).d();
                    Vec3D vec3d1 = new Vec3D(
                        MathHelper.sin(this.enderDragon.yaw * 0.017453292F),
                        0.0D,
                        -MathHelper.cos(this.enderDragon.yaw * 0.017453292F)).d();
                    float f = (float) vec3d1.b(vec3d);
                    float f1 = (float) (Math.acos(f) * 57.2957763671875D);

                    f1 += 0.5F;
                    if (this.counter >= 5 && f1 >= 0.0F && f1 < 10.0F)
                    {
                        Vec3D vec3d2 = this.enderDragon.f(1.0F);
                        double d6 = this.enderDragon.bo.locX() - vec3d2.x * 1.0D;
                        double d7 = this.enderDragon.bo.e(0.5D) + 0.5D;
                        double d8 = this.enderDragon.bo.locZ() - vec3d2.z * 1.0D;
                        double xPosition = this.target.locX() - d6;
                        double yPosition = this.target.e(0.5D) - d7;
                        double zPosition = this.target.locZ() - d8;

                        if (!this.enderDragon.isSilent())
                        {
                            this.enderDragon.world.a(null, 1017, this.enderDragon.getChunkCoordinates(), 0);
                        }

                        EntityDragonFireball fireBall =
                            new EntityDragonFireball(this.enderDragon.world, this.enderDragon, xPosition, yPosition, zPosition);
                        fireBall.setPositionRotation(d6, d7, d8, 0.0F, 0.0F);

                        this.enderDragon.world.addEntity(fireBall);
                        this.counter = 0;
                        if (this.path != null)
                        {
                            while (!this.path.c())
                            {
                                this.path.a();
                            }
                        }

                        this.enderDragon.getCustomControllerManager().setControllerPhase(CustomControllerPhase.HOLDING_PATTERN);
                    }
                }
                else if (this.counter > 0)
                {
                    --this.counter;
                }
            }
            else if (this.counter > 0)
            {
                --this.counter;
            }
        }
    }


    /**
     * This method finds next path for entity.
     */
    private void findPath()
    {
        if (this.path == null || this.path.c())
        {
            int closestPath = this.enderDragon.searchClosestPathPoint();
            int targetPath = closestPath;

            if (this.enderDragon.getRandom().nextInt(this.enderDragon.getPathPointCount()) == 0)
            {
                this.increase = !this.increase;
            }

            if (this.increase)
            {
                ++targetPath;
            }
            else
            {
                --targetPath;
            }

            if (targetPath < 0)
            {
                targetPath += this.enderDragon.getPathPointCount();
            }
            else if (targetPath >= this.enderDragon.getPathPointCount())
            {
                targetPath = targetPath - this.enderDragon.getPathPointCount();
            }

            this.path = this.enderDragon.searchPath(closestPath, targetPath, null);

            if (this.path != null)
            {
                this.path.a();
            }
        }

        this.assignTargetLocation();
    }


    /**
	 * This method assigns target for controller.
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
	 * This method sets living entity as target.
     * @param entityLiving new target entity.
     */
    public void setTarget(EntityLiving entityLiving)
    {
        this.target = entityLiving;
        int startNode = this.enderDragon.searchClosestPathPoint();
        int endNode = this.enderDragon.searchClosestPathPoint(this.target.locX(), this.target.locY(), this.target.locZ());
        int k = MathHelper.floor(this.target.locX());
        int l = MathHelper.floor(this.target.locZ());
        double d0 = (double) k - this.enderDragon.locX();
        double d1 = (double) l - this.enderDragon.locZ();
        double d2 = MathHelper.sqrt(d0 * d0 + d1 * d1);
        double d3 = Math.min(0.4000000059604645D + d2 / 80.0D - 1.0D, 10.0D);
        int i1 = MathHelper.floor(this.target.locY() + d3);
        PathPoint pathpoint = new PathPoint(k, i1, l);

        this.path = this.enderDragon.searchPath(startNode, endNode, pathpoint);
        if (this.path != null)
        {
            this.path.a();
            this.assignTargetLocation();
        }
    }


    @Override
    public Vec3D getTargetLocation()
    {
        return this.targetLocation;
    }


    @Override
    public CustomControllerPhase<CustomControllerStrafe> getControllerPhase()
    {
        return CustomControllerPhase.STRAFE_PLAYER;
    }


    private int counter;

    private PathEntity path;

    private Vec3D targetLocation;

    private EntityLiving target;

    private boolean increase;
}
