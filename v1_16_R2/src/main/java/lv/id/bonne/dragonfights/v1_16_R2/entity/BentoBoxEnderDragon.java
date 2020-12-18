//
// Created by BONNe
// Copyright - 2020
//


package lv.id.bonne.dragonfights.v1_16_R2.entity;


import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;


import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


import lv.id.bonne.dragonfights.v1_16_R2.entity.controllers.CustomControllerManager;
import lv.id.bonne.dragonfights.v1_16_R2.entity.controllers.CustomControllerPhase;
import lv.id.bonne.dragonfights.v1_16_R2.entity.controllers.ICustomController;

import net.minecraft.server.v1_16_R2.AxisAlignedBB;
import net.minecraft.server.v1_16_R2.Block;
import net.minecraft.server.v1_16_R2.BlockPosition;
import net.minecraft.server.v1_16_R2.DamageSource;
import net.minecraft.server.v1_16_R2.EnderDragonBattle;
import net.minecraft.server.v1_16_R2.Entity;
import net.minecraft.server.v1_16_R2.EntityArrow;
import net.minecraft.server.v1_16_R2.EntityComplexPart;
import net.minecraft.server.v1_16_R2.EntityDamageSource;
import net.minecraft.server.v1_16_R2.EntityEnderCrystal;
import net.minecraft.server.v1_16_R2.EntityEnderDragon;
import net.minecraft.server.v1_16_R2.EntityHuman;
import net.minecraft.server.v1_16_R2.EntityLiving;
import net.minecraft.server.v1_16_R2.EntityTypes;
import net.minecraft.server.v1_16_R2.EnumMoveType;
import net.minecraft.server.v1_16_R2.GameRules;
import net.minecraft.server.v1_16_R2.HeightMap;
import net.minecraft.server.v1_16_R2.IBlockData;
import net.minecraft.server.v1_16_R2.IEntitySelector;
import net.minecraft.server.v1_16_R2.Material;
import net.minecraft.server.v1_16_R2.MathHelper;
import net.minecraft.server.v1_16_R2.NBTTagCompound;
import net.minecraft.server.v1_16_R2.Particles;
import net.minecraft.server.v1_16_R2.Path;
import net.minecraft.server.v1_16_R2.PathEntity;
import net.minecraft.server.v1_16_R2.PathPoint;
import net.minecraft.server.v1_16_R2.TagsBlock;
import net.minecraft.server.v1_16_R2.Vec3D;
import net.minecraft.server.v1_16_R2.World;


/**
 * This class process BentoBox Ender Dragon for Minecraft 1.16.4
 */
public class BentoBoxEnderDragon extends EntityEnderDragon
{
	/**
	 * Instantiates a new BentoBox Ender Dragon.
	 *
	 * @param entityTypes the entity types
	 * @param world the world
	 */
	public BentoBoxEnderDragon(EntityTypes<? extends EntityEnderDragon> entityTypes, World world)
	{
		super(EntityTypes.ENDER_DRAGON, world);
		this.type = entityTypes;
		this.customDragonController = new CustomControllerManager(this);
		this.setPersistent();
	}


// ---------------------------------------------------------------------
// Section: Path Finder Algorithm
// ---------------------------------------------------------------------


	/**
	 * This method finds closest node for dragon to fly.
	 * If nodes are not initialized, it does it.
	 * @return Index of closest node.
	 */
	public int searchClosestPathPoint()
	{
		if (this.nodePathPoints == null)
		{
			int pathPointCount = this.getPathPointCount();
			int halfPathPointCount = pathPointCount / 2;

			this.nodePathPoints = new PathPoint[pathPointCount + halfPathPointCount];
			this.nodeConnections = new int[pathPointCount + halfPathPointCount];

			// Portal center offset.
			int xOffset = this.getPortalLocation().getX();
			int zOffset = this.getPortalLocation().getZ();

			// Get min and max fly height
			int minAltitude = this.getMinAltitude();
			int maxAltitude = this.getMaxAltitude();

			// Get distance from center point.
			int pathPointDistance = this.getPathPointDistance();

			for (int nodeIndex = 0; nodeIndex < pathPointCount; nodeIndex++)
			{
				int xPosition = xOffset + (int) Math.floor(pathPointDistance * Math.cos(nodeIndex * Math.PI / pathPointCount));
				int zPosition = zOffset + (int) Math.floor(pathPointDistance * Math.sin(nodeIndex * Math.PI / pathPointCount));

				int blockHeight = Math.max(
					(int) Math.floor(Math.random() * (maxAltitude - minAltitude + 1) + minAltitude),
					this.world.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES,
						new BlockPosition(xPosition, this.getPortalLocation().getY(), zPosition)).getY());

				this.nodePathPoints[nodeIndex] = new PathPoint(xPosition, blockHeight, zPosition);

				// Add connections to adjacent nodes.
				int previous = nodeIndex == 0 ? pathPointCount - 1 : nodeIndex - 1;
				int next = nodeIndex == pathPointCount - 1 ? 0 : nodeIndex + 1;
				// Add connections to inner dummy nodes.
				int inner1 = nodeIndex / 2 + pathPointCount;
				int inner2 = (nodeIndex + 1) / 2 + pathPointCount;

				// First and last should be processed differently.
				this.nodeConnections[nodeIndex] = (1 << previous) | (1 << next) | (1 << inner1) | (1 << inner2);
			}

			// Reduce distance 2 times for inner points
			pathPointDistance /= 2;
			// Increase minAltitude by 10.
			minAltitude += 10;
			// Just a check to avoid issues with incorrect height assignment.
			maxAltitude = Math.max(minAltitude, maxAltitude);

			for (int nodeIndex = 0; nodeIndex < halfPathPointCount; nodeIndex++)
			{
				int xPosition = xOffset + (int) Math.floor(pathPointDistance * Math.cos(nodeIndex * Math.PI / halfPathPointCount));
				int zPosition = zOffset + (int) Math.floor(pathPointDistance * Math.sin(nodeIndex * Math.PI / halfPathPointCount));

				// Calculate path point height based on random value from min till max altitude.
				// Use highest block location, if it is smaller then calculated value.
				int blockHeight = Math.max(
					(int) Math.floor(Math.random() * (maxAltitude - minAltitude + 1) + minAltitude),
					this.world.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES,
						new BlockPosition(xPosition, this.getPortalLocation().getY(), zPosition)).getY() + 10);

				int actualIndex = nodeIndex + pathPointCount;

				this.nodePathPoints[actualIndex] = new PathPoint(xPosition, blockHeight, zPosition);

				// Add connections to all other inner path points:
				for (int inner = 0; inner < halfPathPointCount; inner++)
				{
					if (inner != nodeIndex)
					{
						this.nodeConnections[actualIndex] |= (1 << (inner + pathPointCount));
					}
				}

				// Add connections to 3 outside path points.

				this.nodeConnections[actualIndex] |= nodeIndex == 0 ? (1 << (pathPointCount - 1)) : (1 << (nodeIndex * 2 - 1));
				this.nodeConnections[actualIndex] |= (1 << (nodeIndex * 2));
				this.nodeConnections[actualIndex] |= (1 << (nodeIndex * 2 + 1));
			}
		}

		return this.searchClosestPathPoint(this.locX(), this.locY(), this.locZ());
	}


	/**
	 * This calculates closest node location based on given coordinates.
	 * @param x - coordinate.
	 * @param y - coordinate.
	 * @param z - coordinate.
	 * @return Closest node index.
	 */
	public int searchClosestPathPoint(double x, double y, double z)
	{
		float dummyValue = Float.MAX_VALUE;
		int closestIndex = 0;
		PathPoint location = new PathPoint(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));

		// If there are alive crystals, change to 0.
		byte startingIndex = 0;
		int size = this.getPathPointCount() + this.getPathPointCount() / 2;

		for (int index = startingIndex; index < size; ++index)
		{
			if (this.nodePathPoints[index] != null)
			{
				float distanceSquared = this.nodePathPoints[index].b(location);

				if (distanceSquared < dummyValue)
				{
					dummyValue = distanceSquared;
					closestIndex = index;
				}
			}
		}

		return closestIndex;
	}


	/**
	 * This method finds a path from node I to nod J.
	 * @param startIndex Starting path node index.
	 * @param endIndex Ending path node index.
	 * @param inputPathNode Backup path node.
	 * @return Return path for entity.
	 */
	public PathEntity searchPath(int startIndex, int endIndex, PathPoint inputPathNode)
	{
		// Reset all values.
		for (PathPoint pathPoint : this.nodePathPoints)
		{
			pathPoint.i = false;
			pathPoint.g = 0.0F;
			pathPoint.e = 0.0F;
			pathPoint.f = 0.0F;
			pathPoint.h = null;
			pathPoint.d = -1;
		}

		// Start searching valid path.
		PathPoint startPoint = this.nodePathPoints[startIndex];
		PathPoint endPoint = this.nodePathPoints[endIndex];
		startPoint.e = 0.0F;
		startPoint.f = startPoint.a(endPoint);
		startPoint.g = startPoint.f;
		this.openSet.a();
		this.openSet.a(startPoint);
		PathPoint currentNode = startPoint;

		while (!this.openSet.e())
		{
			PathPoint pathPoint = this.openSet.c();

			// If end is reached, then return a path.
			if (pathPoint.equals(endPoint))
			{
				if (inputPathNode != null)
				{
					inputPathNode.h = endPoint;
					endPoint = inputPathNode;
				}

				return this.createPath(startPoint, endPoint);
			}

			// Check if current node must be updated, by looking at distance from pathNode till endPoint.
			if (pathPoint.a(endPoint) < currentNode.a(endPoint))
			{
				currentNode = pathPoint;
			}

			// Something that sets path point state.
			pathPoint.i = true;

			// Find index of the current pathPoint
			int currentIndex = 0;

			for (int nodeIndex = 0; nodeIndex < this.nodePathPoints.length; nodeIndex++)
			{
				if (this.nodePathPoints[nodeIndex] == pathPoint)
				{
					currentIndex = nodeIndex;
				}
			}

			// Find closest connection point and add it to the search path.
			for (int nodeIndex = 0; nodeIndex < this.nodePathPoints.length; nodeIndex++)
			{
				if ((this.nodeConnections[currentIndex] & 1 << nodeIndex) > 0)
				{
					PathPoint adjacentNode = this.nodePathPoints[nodeIndex];

					if (!adjacentNode.i)
					{
						float weight = pathPoint.e + pathPoint.a(adjacentNode);

						// If adjacent node is not in search queue or weight is smaller, process it.
						if (!adjacentNode.c() || weight < adjacentNode.e)
						{
							// Assing new values to the adjacent node.
							adjacentNode.h = pathPoint;
							adjacentNode.e = weight;
							adjacentNode.f = adjacentNode.a(endPoint);

							if (adjacentNode.c())
							{
								// Update cost if node is in the search queue.
								this.openSet.a(adjacentNode, weight + adjacentNode.f);
							}
							else
							{
								// Add node to the search queue.
								adjacentNode.g = weight + adjacentNode.f;
								this.openSet.a(adjacentNode);
							}
						}
					}
				}
			}
		}

		if (currentNode == startPoint)
		{
			return null;
		}
		else
		{
			if (inputPathNode != null)
			{
				inputPathNode.h = currentNode;
				currentNode = inputPathNode;
			}

			return this.createPath(startPoint, currentNode);
		}
	}


	/**
	 * This method reconstructs paht from first path node to the second path node.
	 * @param sourceNode First path node.
	 * @param targetNode Second path node.
	 * @return Path for entity.
	 */
	private PathEntity createPath(PathPoint sourceNode, PathPoint targetNode)
	{
		LinkedList<PathPoint> pathList = new LinkedList<>();
		pathList.add(targetNode);

		PathPoint current = targetNode;

		while (current != sourceNode)
		{
			current = current.h;
			pathList.addFirst(current);
		}

		return new PathEntity(pathList, new BlockPosition(targetNode.a, targetNode.b, targetNode.c), true);
	}


// ---------------------------------------------------------------------
// Section: Mojang impl
// ---------------------------------------------------------------------


	/**
	 * This method performs an entity movement.
	 */
	@Override
	public void movementTick()
	{
		float f;
		float f1;

		// Old Flap time and new Flap Time
		this.bp = this.bq;

		// Is Dead Processing.
		if (this.dk())
		{
			f = (this.random.nextFloat() - 0.5F) * 8.0F;
			f1 = (this.random.nextFloat() - 0.5F) * 4.0F;
			float f2 = (this.random.nextFloat() - 0.5F) * 8.0F;

			this.world.addParticle(Particles.EXPLOSION,
				this.locX() + (double) f,
				this.locY() + 2.0D + (double) f1,
				this.locZ() + (double) f2,
				0.0D,
				0.0D,
				0.0D);
		}
		else
		{
			this.updateDragonHealth();
			Vec3D vec3d = this.getMot();

			f1 = 0.2F / (MathHelper.sqrt(area(vec3d)) * 10.0F + 1.0F);
			f1 *= (float) Math.pow(2.0D, vec3d.y);
			if (this.getCustomControllerManager().getCurrentPhase().isLanded())
			{
				this.bq += 0.1F;
			}
			else if (this.br)
			{
				this.bq += f1 * 0.5F;
			}
			else
			{
				this.bq += f1;
			}

			this.yaw = MathHelper.g(this.yaw);

			if (this.d < 0)
			{
				for (int i = 0; i < this.c.length; ++i)
				{
					this.c[i][0] = this.yaw;
					this.c[i][1] = this.locY();
				}
			}

			if (++this.d == this.c.length)
			{
				this.d = 0;
			}

			this.c[this.d][0] = this.yaw;
			this.c[this.d][1] = this.locY();
			double d0;
			double d1;
			double d2;
			float f3;
			float f4;

			ICustomController idragoncontroller = this.getCustomControllerManager().getCurrentPhase();

			idragoncontroller.movementTick();
			if (this.getCustomControllerManager().getCurrentPhase() != idragoncontroller)
			{
				idragoncontroller = this.getCustomControllerManager().getCurrentPhase();
				idragoncontroller.movementTick();
			}

			Vec3D vec3d1 = idragoncontroller.getTargetLocation();

			if (vec3d1 != null)
			{
				d0 = vec3d1.x - this.locX();
				d1 = vec3d1.y - this.locY();
				d2 = vec3d1.z - this.locZ();
				double d4 = d0 * d0 + d1 * d1 + d2 * d2;
				float f5 = idragoncontroller.getConstant();
				double d5 = MathHelper.sqrt(d0 * d0 + d2 * d2);

				if (d5 > 0.0D)
				{
					d1 = MathHelper.a(d1 / d5, (double) (-f5), (double) f5);
				}

				this.setMot(this.getMot().add(0.0D, d1 * 0.01D, 0.0D));
				this.yaw = MathHelper.g(this.yaw);
				double d6 = MathHelper.a(
					MathHelper.g(180.0D - MathHelper.d(d0, d2) * 57.2957763671875D - (double) this.yaw),
					-50.0D,
					50.0D);
				Vec3D vec3d2 = vec3d1.a(this.locX(), this.locY(), this.locZ()).d();
				Vec3D vec3d3 = new Vec3D(
					MathHelper.sin(this.yaw * 0.017453292F),
					this.getMot().y,
					-MathHelper.cos(this.yaw * 0.017453292F)).d();

				f3 = Math.max(((float) vec3d3.b(vec3d2) + 0.5F) / 1.5F, 0.0F);
				this.bt *= 0.8F;
				this.bt = (float) ((double) this.bt + d6 * (double) idragoncontroller.getRotation());
				this.yaw += this.bt * 0.1F;
				f4 = (float) (2.0D / (d4 + 1.0D));
				float f6 = 0.06F;

				this.a(0.06F * (f3 * f4 + (1.0F - f4)), new Vec3D(0.0D, 0.0D, -1.0D));
				if (this.br)
				{
					this.move(EnumMoveType.SELF, this.getMot().a(0.800000011920929D));
				}
				else
				{
					this.move(EnumMoveType.SELF, this.getMot());
				}

				Vec3D vec3d4 = this.getMot().d();
				double d7 = 0.8D + 0.15D * (vec3d4.b(vec3d3) + 1.0D) / 2.0D;

				this.setMot(this.getMot().d(d7, 0.9100000262260437D, d7));
			}

			this.aA = this.yaw;
			Vec3D[] avec3d = new Vec3D[this.children.length];

			for (int j = 0; j < this.children.length; ++j)
			{
				avec3d[j] = new Vec3D(this.children[j].locX(), this.children[j].locY(), this.children[j].locZ());
			}

			float f7 = (float) (this.a(5, 1.0F)[1] - this.a(10, 1.0F)[1]) * 10.0F * 0.017453292F;
			float f8 = MathHelper.cos(f7);
			float f9 = MathHelper.sin(f7);
			float f10 = this.yaw * 0.017453292F;
			float f11 = MathHelper.sin(f10);
			float f12 = MathHelper.cos(f10);

			this.movePart(this.children[2], (double) (f11 * 0.5F), 0.0D, (double) (-f12 * 0.5F));
			this.movePart(this.children[6], (double) (f12 * 4.5F), 2.0D, (double) (f11 * 4.5F));
			this.movePart(this.children[7], (double) (f12 * -4.5F), 2.0D, (double) (f11 * -4.5F));

			if (this.hurtTicks == 0)
			{
				this.applyKnockBack(this.world.getEntities(this,
					this.children[6].getBoundingBox().grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0.0D),
					IEntitySelector.e));
				this.applyKnockBack(this.world.getEntities(this,
					this.children[7].getBoundingBox().grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0.0D),
					IEntitySelector.e));
				this.damageEntity(this.world.getEntities(this, this.children[0].getBoundingBox().g(1.0D), IEntitySelector.e));
				this.damageEntity(this.world.getEntities(this, this.children[1].getBoundingBox().g(1.0D), IEntitySelector.e));
			}

			float f13 = MathHelper.sin(this.yaw * 0.017453292F - this.bt * 0.01F);
			float f14 = MathHelper.cos(this.yaw * 0.017453292F - this.bt * 0.01F);
			float f15 = this.getOffset();

			this.movePart(this.children[0], (f13 * 6.5F * f8), (f15 + f9 * 6.5F), (-f14 * 6.5F * f8));
			this.movePart(this.children[1], (f13 * 5.5F * f8), (f15 + f9 * 5.5F), (-f14 * 5.5F * f8));
			double[] adouble = this.a(5, 1.0F);

			int k;

			for (k = 0; k < 3; ++k)
			{
				EntityComplexPart entitycomplexpart;

				switch (k)
				{
					case 0:
						entitycomplexpart = this.children[3];
						break;
					case 1:
						entitycomplexpart = this.children[4];
						break;
					default:
						entitycomplexpart = this.children[5];
						break;
				}

				double[] adouble1 = this.a(12 + k * 2, 1.0F);
				float f16 = this.yaw * 0.017453292F + this.normalizeDegrees(adouble1[0] - adouble[0]) * 0.017453292F;
				float f17 = MathHelper.sin(f16);
				float f18 = MathHelper.cos(f16);

				f4 = (float) (k + 1) * 2.0F;
				this.movePart(entitycomplexpart,
					-(f11 * 1.5F + f17 * f4) * f8,
					adouble1[1] - adouble[1] - (double) ((f4 + 1.5F) * f9) + 1.5D,
					(f12 * 1.5F + f18 * f4) * f8);
			}

			this.br = this.intersect(this.children[0].getBoundingBox()) |
				this.intersect(this.children[1].getBoundingBox()) |
				this.intersect(this.children[2].getBoundingBox());

			for (k = 0; k < this.children.length; ++k)
			{
				this.children[k].lastX = avec3d[k].x;
				this.children[k].lastY = avec3d[k].y;
				this.children[k].lastZ = avec3d[k].z;
				this.children[k].D = avec3d[k].x;
				this.children[k].E = avec3d[k].y;
				this.children[k].F = avec3d[k].z;
			}
		}

		if (this.customDragonController.getCurrentPhase().getControllerPhase() == CustomControllerPhase.HOVER)
		{
			// Move out from initial hover phase.
			if (this.nodePathPoints == null)
			{
				this.customDragonController.setControllerPhase(CustomControllerPhase.HOLDING_PATTERN);
			}
		}
	}


	/**
	 * This method process damaging entity.
	 * @param dragonPart Part that was hit.
	 * @param damageSource Damage Source.
	 * @param amount Damage.
	 * @return {@code true} if damage was caneclled.
	 */
	@Override
	public boolean a(EntityComplexPart dragonPart, DamageSource damageSource, float amount)
	{
		if (this.getCustomControllerManager().getCurrentPhase().getControllerPhase() == CustomControllerPhase.DYING)
		{
			return false;
		}
		else
		{
			if (this.customDragonController.getCurrentPhase().isLanded())
			{
				// Apparently landed dragon cannot be harmed with arrows.
				if (damageSource.j() instanceof EntityArrow)
				{
					damageSource.j().setOnFire(1);
					amount = 0.0F;
				}
			}

			if (dragonPart != this.bo)
			{
				amount = amount / 4.0F + Math.min(amount, 1.0F);
			}

			if (amount < 0.01F)
			{
				return false;
			}
			else
			{
				if (damageSource.getEntity() instanceof EntityHuman || damageSource.isExplosion())
				{
					float f1 = this.getHealth();

					this.dealDamage(damageSource, amount);

					if (this.dk() && !this.getCustomControllerManager().getCurrentPhase().isLanded())
					{
						this.setHealth(1.0F);
						this.getCustomControllerManager().setControllerPhase(CustomControllerPhase.DYING);
					}

					if (this.getCustomControllerManager().getCurrentPhase().isLanded())
					{
						this.damageReceived = (int) ((float) this.damageReceived + (f1 - this.getHealth()));

						if ((float) this.damageReceived > 0.25F * this.getMaxHealth())
						{
							this.damageReceived = 0;
							this.getCustomControllerManager().setControllerPhase(CustomControllerPhase.TAKEOFF);
						}
					}
				}

				return true;
			}
		}
	}


	/**
	 * This method process entity damaging.
	 * @param damageSource Damage Source.
	 * @param damage Damage amount.
	 * @return {@code false} always.
	 */
	@Override
	public boolean damageEntity(DamageSource damageSource, float damage)
	{
		if (damageSource instanceof EntityDamageSource && ((EntityDamageSource) damageSource).y())
		{
			this.a(this.children[2], damageSource, damage);
		}

		return false;
	}


	/**
	 * This method creates vector in which dragon moves.
	 * @param f Rotation value.
	 * @return Vector for the dragon.
	 */
	public Vec3D getMovementVector(float f)
	{
		ICustomController controller = this.getCustomControllerManager().getCurrentPhase();
		CustomControllerPhase<? extends ICustomController> phase = controller.getControllerPhase();
		float f1;
		Vec3D vec3d;

		if (phase != CustomControllerPhase.LANDING &&
			phase != CustomControllerPhase.TAKEOFF)
		{
			if (controller.isLanded())
			{
				float f2 = this.pitch;
				this.pitch = -45.0F;
				vec3d = this.f(f);
				this.pitch = f2;
			}
			else
			{
				vec3d = this.f(f);
			}
		}
		else
		{
			BlockPosition blockposition =
				this.world.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, this.getPortalLocation());
			f1 = Math.max(MathHelper.sqrt(blockposition.a(this.getPositionVector(), true)) / 4.0F, 1.0F);
			float f3 = 6.0F / f1;
			float f4 = this.pitch;
			this.pitch = -f3 * 1.5F * 5.0F;
			vec3d = this.f(f);
			this.pitch = f4;
		}

		return vec3d;
	}


	/**
	 * This method calculates offset of the dragon.
	 * @return float for dragon offset.
	 */
	private float getOffset()
	{
		if (this.getCustomControllerManager().getCurrentPhase().isLanded())
		{
			return -1.0F;
		}
		else
		{
			double[] adouble = this.a(5, 1.0F);
			double[] adouble1 = this.a(0, 1.0F);

			return (float) (adouble[1] - adouble1[1]);
		}
	}


	/**
	 * This method updates dragon health from living crystals.
	 */
	private void updateDragonHealth()
	{
		if (this.currentEnderCrystal != null)
		{
			if (this.currentEnderCrystal.dead)
			{
				this.currentEnderCrystal = null;
			}
			else if (this.ticksLived % 10 == 0 && this.getHealth() < this.getMaxHealth())
			{
				this.setHealth(this.getHealth() + 1.0F);
			}
		}

		if (this.random.nextInt(10) == 0)
		{
			List<EntityEnderCrystal> list = this.world.a(EntityEnderCrystal.class, this.getBoundingBox().g(32.0D));
			EntityEnderCrystal closestCrystal = null;
			double d0 = Double.MAX_VALUE;

			for (Iterator<EntityEnderCrystal> iterator = list.iterator(); iterator.hasNext(); )
			{
				EntityEnderCrystal nextCrystal = iterator.next();
				double d1 = nextCrystal.h(this);

				if (d1 < d0)
				{
					d0 = d1;
					closestCrystal = nextCrystal;
				}
			}

			this.currentEnderCrystal = closestCrystal;
		}
	}


	/**
	 * This method moves given dragon part by x,y,z values.
	 * @param entitycomplexpart Part that must be moved.
	 * @param x - x offset.
	 * @param y - y offset.
	 * @param z - z offset.
	 */
	private void movePart(EntityComplexPart entitycomplexpart, double x, double y, double z)
	{
		entitycomplexpart.setPosition(this.locX() + x, this.locY() + y, this.locZ() + z);
	}


	/**
	 * This method apply knock back force to the all living entities, that are too close to the dragon body.
	 * @param list List of entities that must be checked.
	 */
	private void applyKnockBack(List<Entity> list)
	{
		double centerX = (this.children[2].getBoundingBox().minX + this.children[2].getBoundingBox().maxX) / 2.0D;
		double centerZ = (this.children[2].getBoundingBox().minZ + this.children[2].getBoundingBox().maxZ) / 2.0D;
		Iterator<Entity> iterator = list.iterator();

		while (iterator.hasNext())
		{
			Entity entity = iterator.next();

			if (entity instanceof EntityLiving)
			{
				double xOffset = entity.locX() - centerX;
				double zOffset = entity.locZ() - centerZ;
				double squaredDistance = Math.max(xOffset * xOffset + zOffset * zOffset, 0.1D);

				entity.i(xOffset / squaredDistance * 4.0D, 0.20000000298023224D, zOffset / squaredDistance * 4.0D);

				if (!this.getCustomControllerManager().getCurrentPhase().isLanded() && ((EntityLiving) entity).cZ() < entity.ticksLived - 2)
				{
					entity.damageEntity(DamageSource.mobAttack(this), 5.0F);
					this.a(this, entity);
				}
			}
		}
	}


	/**
	 * Perform damaging entities.
	 * @param list List of entities that must be damaged
	 */
	private void damageEntity(List<Entity> list)
	{
		Iterator<Entity> iterator = list.iterator();

		while (iterator.hasNext())
		{
			Entity entity = iterator.next();

			if (entity instanceof EntityLiving)
			{
				entity.damageEntity(DamageSource.mobAttack(this), 10.0F);
				this.a(this, entity);
			}
		}
	}


	/**
	 * This method normalizes degree value so it is between 0-360
	 * @param degree Value that must be normalized.
	 * @return Normalized degree value.
	 */
	private float normalizeDegrees(double degree)
	{
		return (float) MathHelper.g(degree);
	}


	/**
	 * This method checks if dragon intersects with box.
	 * @param box box.
	 * @return {@code true} if some blocks were left in bounding box.
	 */
	private boolean intersect(AxisAlignedBB box)
	{
		int minX = MathHelper.floor(box.minX);
		int minY = MathHelper.floor(box.minY);
		int minZ = MathHelper.floor(box.minZ);
		int maxX = MathHelper.floor(box.maxX);
		int maxY = MathHelper.floor(box.maxY);
		int maxZ = MathHelper.floor(box.maxZ);
		boolean isProcessed = false;
		boolean hasRemovedBlock = false;

		for (int x = minX; x <= maxX; ++x)
		{
			for (int y = minY; y <= maxY; ++y)
			{
				for (int z = minZ; z <= maxZ; ++z)
				{
					BlockPosition blockposition = new BlockPosition(x, y, z);
					IBlockData iblockdata = this.world.getType(blockposition);
					Block block = iblockdata.getBlock();

					if (!iblockdata.isAir() && iblockdata.getMaterial() != Material.FIRE)
					{
						if (this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) &&
							!TagsBlock.DRAGON_IMMUNE.isTagged(block))
						{
							hasRemovedBlock = this.world.a(blockposition, false) || hasRemovedBlock;
						}
						else
						{
							isProcessed = true;
						}
					}
				}
			}
		}

		if (hasRemovedBlock)
		{
			BlockPosition blockposition1 = new BlockPosition(
				minX + this.random.nextInt(maxX - minX + 1),
				minY + this.random.nextInt(maxY - minY + 1),
				minZ + this.random.nextInt(maxZ - minZ + 1));

			this.world.triggerEffect(2008, blockposition1, 0);
		}

		return isProcessed;
	}


	/**
	 * This method saves current entity NBT.
	 * @param nbttagcompound tag that can be populated.
	 */
	@Override
	public void saveData(NBTTagCompound nbttagcompound)
	{
		super.saveData(nbttagcompound);
		nbttagcompound.setInt("DragonPhase", this.getCustomControllerManager().getCurrentPhase().getControllerPhase().phaseIndex());
		nbttagcompound.setLong("SpawnLocation", this.getSpawnLocation().asLong());
		nbttagcompound.setLong("PortalLocation", this.getPortalLocation().asLong());

		nbttagcompound.setInt("PathPointCount", this.getPathPointCount());
		nbttagcompound.setInt("PathPointDistance", this.getPathPointDistance());

		nbttagcompound.setInt("MinAltitude", this.getMinAltitude());
		nbttagcompound.setInt("MaxAltitude", this.getMaxAltitude());
	}


	/**
	 * This method loads current entity NBT.
	 * @param nbttagcompound tag that must be loaded.
	 */
	@Override
	public void loadData(NBTTagCompound nbttagcompound)
	{
		super.loadData(nbttagcompound);
		if (nbttagcompound.hasKey("DragonPhase"))
		{
			this.getCustomControllerManager().setControllerPhase(
				CustomControllerPhase.getById(nbttagcompound.getInt("DragonPhase")));
		}

		if (nbttagcompound.hasKey("SpawnLocation"))
		{
			this.spawnLocation = BlockPosition.fromLong(nbttagcompound.getLong("SpawnLocation"));
		}

		if (nbttagcompound.hasKey("PortalLocation"))
		{
			this.portalLocation = BlockPosition.fromLong(nbttagcompound.getLong("PortalLocation"));
		}

		if (nbttagcompound.hasKey("PathPointCount"))
		{
			this.pathPointCount = nbttagcompound.getInt("PathPointCount");
		}

		if (nbttagcompound.hasKey("PathPointDistance"))
		{
			this.pathPointDistance = nbttagcompound.getInt("PathPointDistance");
		}

		if (nbttagcompound.hasKey("MinAltitude"))
		{
			this.minAltitude = nbttagcompound.getInt("MinAltitude");
		}

		if (nbttagcompound.hasKey("MaxAltitude"))
		{
			this.maxAltitude = nbttagcompound.getInt("MaxAltitude");
		}
	}


	/**
	 * This method performs killing entity.
	 */
	@Override
	public void killEntity()
	{
		super.killEntity();
	}


	/**
	 * This method process entity death event.
	 */
	@Override
	public void die()
	{
		if (!this.killed)
		{
			// Call dummy event that entity died.
			// Most likely it is done by some event, like bentobox mob removing
			Bukkit.getPluginManager().callEvent(
				new EntityDeathEvent((LivingEntity) this.getBukkitEntity(),
					Collections.emptyList()));
		}
		super.die();
	}


	/**
	 * This method returns that current dragon is not having any ender dragon battle.
	 * @return {@code null}
	 */
	public EnderDragonBattle getEnderDragonBattle()
	{
		return null;
	}


	/**
	 * This method returns custom entity controller.
	 * @return Custom entity controller.
	 */
	public CustomControllerManager getCustomControllerManager()
	{
		return this.customDragonController;
	}


	/**
	 * This method returns current entity type.
	 * @return Type of the current entity.
	 */
	@Override
	public EntityTypes<?> getEntityType()
	{
		if (type == null)
		{
			return super.getEntityType();
		}

		return type;
	}


	/**
	 * This method calculates and returns custom portal location.
	 * @return Location of the custom portal.
	 */
	public BlockPosition getPortalLocation()
	{
		if (this.portalLocation == null)
		{
			// Try to calculate
			NamespacedKey key = new NamespacedKey("bentobox", "location");
			String location = this.getBukkitEntity().getPersistentDataContainer().get(key, PersistentDataType.STRING);

			if (location != null && !location.isEmpty())
			{
				String[] coordinate = location.split(",");
				this.portalLocation = new BlockPosition(Double.parseDouble(coordinate[0]),
					Double.parseDouble(coordinate[1]),
					Double.parseDouble(coordinate[2]));
			}
			else
			{
				return new BlockPosition(this.spawnLocation);
			}
		}

		return portalLocation;
	}


	/**
	 * This method calculates and returns custom portal location.
	 * @return Location of the custom portal.
	 */
	private BlockPosition getSpawnLocation()
	{
		if (this.spawnLocation == null)
		{
			// Try to calculate
			NamespacedKey key = new NamespacedKey("bentobox", "spawn");
			String location = this.getBukkitEntity().getPersistentDataContainer().get(key, PersistentDataType.STRING);

			if (location != null && !location.isEmpty())
			{
				String[] coordinate = location.split(",");
				this.spawnLocation = new BlockPosition(Double.parseDouble(coordinate[0]),
					Double.parseDouble(coordinate[1]),
					Double.parseDouble(coordinate[2]));
			}
			else
			{
				return new BlockPosition(this.locX(), this.locY(), this.locZ());
			}
		}

		return spawnLocation;
	}


	/**
	 * Returns X coordinate offset for dragon.
	 * @return X coordinate offset.
	 */
	public double xOffset()
	{
		return this.getPortalLocation().getX();
	}


	/**
	 * Returns Y coordinate offset for dragon.
	 * @return Y coordinate offset.
	 */
	public double yOffset()
	{
		return this.getPortalLocation().getY();
	}


	/**
	 * Returns Z coordinate offset for dragon.
	 * @return Z coordinate offset.
	 */
	public double zOffset()
	{
		return this.getPortalLocation().getZ();
	}


	/**
	 * Returns maximal altitude of the dragon.
	 * @return maxAltitude value.
	 */
	public int getMaxAltitude()
	{
		if (this.maxAltitude == -1)
		{
			// Try to calculate
			NamespacedKey key = new NamespacedKey("bentobox", "max_altitude");
			this.maxAltitude = this.getBukkitEntity().getPersistentDataContainer().getOrDefault(key, PersistentDataType.INTEGER,
				this.getPortalLocation().getY() + 30);
		}

		return this.maxAltitude;
	}


	/**
	 * Returns minimal altitude of the dragon.
	 * @return minAltitude value.
	 */
	public int getMinAltitude()
	{
		if (this.minAltitude == -1)
		{
			// Try to calculate
			NamespacedKey key = new NamespacedKey("bentobox", "min_altitude");
			this.minAltitude = this.getBukkitEntity().getPersistentDataContainer().getOrDefault(key, PersistentDataType.INTEGER,
				this.getPortalLocation().getY());
		}

		return this.minAltitude;
	}


	/**
	 * Returns number of path points in outer ring.
	 * @return Number of path points.
	 */
	public int getPathPointCount()
	{
		if (this.pathPointCount == -1)
		{
			// Try to calculate
			NamespacedKey key = new NamespacedKey("bentobox", "tower_count");
			this.pathPointCount = this.getBukkitEntity().getPersistentDataContainer().getOrDefault(key, PersistentDataType.INTEGER, 8);
		}

		return this.pathPointCount;
	}


	/**
	 * Returns distance from center to outer path point ring.
	 * @return distance till outer path point ring.
	 */
	public int getPathPointDistance()
	{
		if (this.pathPointDistance == -1)
		{
			// Try to calculate
			NamespacedKey key = new NamespacedKey("bentobox", "tower_distance");
			this.pathPointDistance = this.getBukkitEntity().getPersistentDataContainer().getOrDefault(key, PersistentDataType.INTEGER, 40);
		}

		return this.pathPointDistance;
	}


	/**
	 * Returns an area of the given vector.
	 * @param vec3D given vector.
	 * @return Vector area.
	 */
	public static double area(Vec3D vec3D)
	{
		return vec3D.getX() * vec3D.getX() + vec3D.getZ() * vec3D.getZ();
	}


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------


	/**
	 * Custom entity type.
	 */
	private final EntityTypes<? extends EntityEnderDragon> type;

	/**
	 * Dragon controller.
	 */
	private final CustomControllerManager customDragonController;

	/**
	 * Contains amount of damage received.
	 */
	private int damageReceived;

	/**
	 * Position search heep.
	 */
	private final Path openSet = new Path();

	/**
	 * Ender Dragon Tower points.
	 */
	private PathPoint[] nodePathPoints = null;

	/**
	 * Ender dragon Tower connections.
	 */
	private int[] nodeConnections = null;

	/**
	 * Custom portal location.
	 */
	private BlockPosition portalLocation;

	/**
	 * Custom portal location.
	 */
	private BlockPosition spawnLocation;

	/**
	 * Number of path points for dragon to fly.
	 */
	private int pathPointCount = -1;

	/**
	 * Distance between center and path point.
	 */
	private int pathPointDistance = -1;

	/**
	 * Number of maximal allowed dragon fly altitude.
	 */
	private int maxAltitude = -1;

	/**
	 * Number of minimal allowed dragon fly altitude.
	 */
	private int minAltitude = -1;
}