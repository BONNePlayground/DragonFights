//
// Created by BONNe
// Copyright - 2020
//


package lv.id.bonne.dragonfights.tasks;


import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.boss.*;
import org.bukkit.entity.Boss;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import java.util.*;

import lv.id.bonne.dragonfights.DragonFightsAddon;
import lv.id.bonne.dragonfights.database.objects.DragonFightsObject;
import lv.id.bonne.dragonfights.entity.BentoBoxEnderDragonRoot;
import lv.id.bonne.dragonfights.entity.CustomEntityAPI;
import lv.id.bonne.dragonfights.utils.Constants;
import lv.id.bonne.dragonfights.utils.Pair;
import lv.id.bonne.dragonfights.utils.Utils;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;


/**
 * This class manages portal generation and dragon summoning.
 */
public class PortalGenerator
{

	/**
	 * Instantiates a new Portal generator.
	 *
	 * @param addon the addon
	 * @param island the island
	 * @param blockLocation the block location
	 */
	public PortalGenerator(DragonFightsAddon addon, Island island, Location blockLocation)
	{
		this.addon = addon;

		this.island = island;
		this.world = blockLocation.getWorld();

		// Normalize given location.
		this.centerLocation = blockLocation.clone();

		// Gets data about island.
		this.islandData = this.addon.getAddonManager().getIslandData(this.island);
	}


	/**
	 * This method searches for portal center location.
	 */
	public void searchPortalCenter()
	{
		Block searchBlock = this.centerLocation.getBlock();

		if (searchBlock.getRelative(BlockFace.SOUTH).getType() == Material.END_PORTAL)
		{
			// Validate that block is in the middle of the row.
			if (searchBlock.getRelative(BlockFace.EAST).getType() != Material.BEDROCK &&
				searchBlock.getRelative(BlockFace.WEST).getType() != Material.BEDROCK)
			{
				// Wrong block.
				// Check diagonal
				if (searchBlock.getRelative(BlockFace.NORTH_WEST).getType() == Material.BEDROCK &&
					searchBlock.getRelative(BlockFace.SOUTH_EAST).getType() == Material.BEDROCK)
				{
					// I know my location.
					searchBlock = searchBlock.getRelative(BlockFace.NORTH_WEST).getRelative(BlockFace.WEST);
				}
				else if (searchBlock.getRelative(BlockFace.NORTH_EAST).getType() == Material.BEDROCK &&
					searchBlock.getRelative(BlockFace.SOUTH_WEST).getType() == Material.BEDROCK)
				{
					// I know my locaiton
					searchBlock = searchBlock.getRelative(BlockFace.NORTH_EAST).getRelative(BlockFace.EAST);
				}
			}
			else if (searchBlock.getRelative(BlockFace.EAST).getType() != Material.BEDROCK)
			{
				// Move one block west, to be in the middle of the side.
				searchBlock = searchBlock.getRelative(BlockFace.WEST);
			}
			else if (searchBlock.getRelative(BlockFace.WEST).getType() != Material.BEDROCK)
			{
				// Move one block east, to be in the middle of the side.
				searchBlock = searchBlock.getRelative(BlockFace.EAST);
			}

			// search center to the south.
			while (searchBlock.getRelative(BlockFace.SOUTH).getType() == Material.END_PORTAL)
			{
				searchBlock = searchBlock.getRelative(BlockFace.SOUTH);
			}

			searchBlock = searchBlock.getRelative(BlockFace.SOUTH);
		}
		else if (searchBlock.getRelative(BlockFace.NORTH).getType() == Material.END_PORTAL)
		{
			// Validate that block is in the middle of the row.
			if (searchBlock.getRelative(BlockFace.EAST).getType() != Material.BEDROCK &&
				searchBlock.getRelative(BlockFace.WEST).getType() != Material.BEDROCK)
			{
				// Wrong block.
				// Check diagonal
				if (searchBlock.getRelative(BlockFace.NORTH_WEST).getType() == Material.BEDROCK &&
					searchBlock.getRelative(BlockFace.SOUTH_EAST).getType() == Material.BEDROCK)
				{
					// I know my location.
					searchBlock = searchBlock.getRelative(BlockFace.SOUTH_EAST).getRelative(BlockFace.EAST);
				}
				else if (searchBlock.getRelative(BlockFace.NORTH_EAST).getType() == Material.BEDROCK &&
					searchBlock.getRelative(BlockFace.SOUTH_WEST).getType() == Material.BEDROCK)
				{
					// I know my locaiton
					searchBlock = searchBlock.getRelative(BlockFace.SOUTH_WEST).getRelative(BlockFace.WEST);
				}
			}
			else if (searchBlock.getRelative(BlockFace.EAST).getType() != Material.BEDROCK)
			{
				// Move one block west, to be in the middle of the side.
				searchBlock = searchBlock.getRelative(BlockFace.WEST);
			}
			else if (searchBlock.getRelative(BlockFace.WEST).getType() != Material.BEDROCK)
			{
				// Move one block east, to be in the middle of the side.
				searchBlock = searchBlock.getRelative(BlockFace.EAST);
			}

			// search center to the south.
			while (searchBlock.getRelative(BlockFace.NORTH).getType() == Material.END_PORTAL)
			{
				searchBlock = searchBlock.getRelative(BlockFace.NORTH);
			}

			searchBlock = searchBlock.getRelative(BlockFace.NORTH);
		}
		else if (searchBlock.getRelative(BlockFace.EAST).getType() == Material.END_PORTAL)
		{
			// Validate that block is in the middle of the row.
			if (searchBlock.getRelative(BlockFace.NORTH).getType() != Material.BEDROCK &&
				searchBlock.getRelative(BlockFace.SOUTH).getType() != Material.BEDROCK)
			{
				// Wrong block.
				// Check diagonal
				if (searchBlock.getRelative(BlockFace.NORTH_WEST).getType() == Material.BEDROCK &&
					searchBlock.getRelative(BlockFace.SOUTH_EAST).getType() == Material.BEDROCK)
				{
					// I know my location.
					searchBlock = searchBlock.getRelative(BlockFace.NORTH_WEST).getRelative(BlockFace.NORTH);
				}
				else if (searchBlock.getRelative(BlockFace.NORTH_EAST).getType() == Material.BEDROCK &&
					searchBlock.getRelative(BlockFace.SOUTH_WEST).getType() == Material.BEDROCK)
				{
					// I know my locaiton
					searchBlock = searchBlock.getRelative(BlockFace.SOUTH_WEST).getRelative(BlockFace.SOUTH);
				}
			}
			else if (searchBlock.getRelative(BlockFace.NORTH).getType() != Material.BEDROCK)
			{
				// Move one block west, to be in the middle of the side.
				searchBlock = searchBlock.getRelative(BlockFace.SOUTH);
			}
			else if (searchBlock.getRelative(BlockFace.SOUTH).getType() != Material.BEDROCK)
			{
				// Move one block east, to be in the middle of the side.
				searchBlock = searchBlock.getRelative(BlockFace.NORTH);
			}

			// search center to the south.
			while (searchBlock.getRelative(BlockFace.EAST).getType() == Material.END_PORTAL)
			{
				searchBlock = searchBlock.getRelative(BlockFace.EAST);
			}

			searchBlock = searchBlock.getRelative(BlockFace.EAST);
		}
		else if (searchBlock.getRelative(BlockFace.WEST).getType() == Material.END_PORTAL)
		{
			// Validate that block is in the middle of the row.
			if (searchBlock.getRelative(BlockFace.NORTH).getType() != Material.BEDROCK &&
				searchBlock.getRelative(BlockFace.SOUTH).getType() != Material.BEDROCK)
			{
				// Wrong block.
				// Check diagonal
				if (searchBlock.getRelative(BlockFace.NORTH_WEST).getType() == Material.BEDROCK &&
					searchBlock.getRelative(BlockFace.SOUTH_EAST).getType() == Material.BEDROCK)
				{
					// I know my location.
					searchBlock = searchBlock.getRelative(BlockFace.SOUTH_EAST).getRelative(BlockFace.SOUTH);
				}
				else if (searchBlock.getRelative(BlockFace.NORTH_EAST).getType() == Material.BEDROCK &&
					searchBlock.getRelative(BlockFace.SOUTH_WEST).getType() == Material.BEDROCK)
				{
					// I know my locaiton
					searchBlock = searchBlock.getRelative(BlockFace.NORTH_EAST).getRelative(BlockFace.NORTH);
				}
			}
			else if (searchBlock.getRelative(BlockFace.NORTH).getType() != Material.BEDROCK)
			{
				// Move one block west, to be in the middle of the side.
				searchBlock = searchBlock.getRelative(BlockFace.SOUTH);
			}
			else if (searchBlock.getRelative(BlockFace.SOUTH).getType() != Material.BEDROCK)
			{
				// Move one block east, to be in the middle of the side.
				searchBlock = searchBlock.getRelative(BlockFace.NORTH);
			}

			// search center to the south.
			while (searchBlock.getRelative(BlockFace.WEST).getType() == Material.END_PORTAL)
			{
				searchBlock = searchBlock.getRelative(BlockFace.WEST);
			}

			searchBlock = searchBlock.getRelative(BlockFace.WEST);
		}
		else if (searchBlock.getRelative(BlockFace.DOWN).getType() == Material.BEDROCK)
		{
			// Move 4 blocks down.
			searchBlock =
				searchBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN);
			this.generated = true;
		}

		if (searchBlock.getType() != Material.BEDROCK ||
			searchBlock.getRelative(BlockFace.NORTH).getType() == Material.BEDROCK ||
			searchBlock.getRelative(BlockFace.EAST).getType() == Material.BEDROCK ||
			searchBlock.getRelative(BlockFace.SOUTH).getType() == Material.BEDROCK ||
			searchBlock.getRelative(BlockFace.WEST).getType() == Material.BEDROCK)
		{
			this.centerLocation = null;
		}
		else
		{
			// Assign center location based on search block value.
			this.centerLocation = searchBlock.getLocation();
		}
	}


	/**
	 * This method indicates that portal is active.
	 * @return {@code true} if portal is active, {@code false} otherwise.
	 */
	public boolean isActivePortal()
	{
		return this.centerLocation != null &&
			this.centerLocation.getBlock().getRelative(BlockFace.WEST).getType() == Material.END_PORTAL;
	}


	/**
	 * Indicates if portal location is valid.
	 * @return {@code true} if portal location is valid, {@code false} otherwise.
	 */
	public boolean isValidPortal()
	{
		return this.centerLocation != null;
	}


	/**
	 * This method generates empty portal.
	 */
	public void generateFullPortalSlow()
	{
		this.generatePortal(true, 1);
	}


	/**
	 * This method generates full portal very fast.
	 */
	public void generateFullPortalFast()
	{
		this.generatePortal(true, 0);
	}


	/**
	 * This method generates end exit portal.
	 * @param finished Boolean that indicate if portal should be finished.
	 * @param timer Timer how fast portal will be generated.
	 */
	private void generatePortal(boolean finished, long timer)
	{
		if (this.generating)
		{
			// Already in process.
			return;
		}

		this.generating = true;

		Queue<Pair<Location, BlockData>> blockGenerationQueue = new LinkedList<>();

		Vector inputVector = this.centerLocation.toVector();

		Iterator<Vector> vectorIterator = Utils.getVectorIterator(
			new Vector(inputVector.getX() - 4, inputVector.getY() - 1, inputVector.getZ() - 4),
			new Vector(inputVector.getX() + 4, inputVector.getY() + 32, inputVector.getZ() + 4));

		// Create end trophy.
		while (vectorIterator.hasNext())
		{
			Vector currentVector = vectorIterator.next();
			boolean inSphere = inputVector.isInSphere(currentVector, 2.5D);

			if (inSphere || inputVector.isInSphere(currentVector, 3.5D))
			{
				if (currentVector.getY() < inputVector.getY())
				{
					if (inSphere)
					{
						blockGenerationQueue.add(new Pair<>(currentVector.toLocation(world),
							Material.BEDROCK.createBlockData()));
					}
					else if (currentVector.getY() < inputVector.getY())
					{
						blockGenerationQueue.add(new Pair<>(currentVector.toLocation(world),
							Material.END_STONE.createBlockData()));
					}
				}
				else if (currentVector.getY() > inputVector.getY())
				{
					blockGenerationQueue.add(new Pair<>(currentVector.toLocation(world),
						Material.AIR.createBlockData()));
				}
				else if (!inSphere)
				{
					blockGenerationQueue.add(new Pair<>(currentVector.toLocation(world),
						Material.BEDROCK.createBlockData()));
				}
				else if (finished)
				{
					blockGenerationQueue.add(new Pair<>(currentVector.toLocation(world),
						Material.END_PORTAL.createBlockData()));
				}
				else
				{
					blockGenerationQueue.add(new Pair<>(currentVector.toLocation(world),
						Material.AIR.createBlockData()));
				}
			}
		}

		Vector currentVector = inputVector.clone();
		Vector up = new Vector(0, 1, 0);

		// Create central pillar
		for (int i = 0; i < 4; ++i)
		{
			blockGenerationQueue.add(new Pair<>(currentVector.toLocation(world),
				Material.BEDROCK.createBlockData()));
			// Move up.
			currentVector.add(up);
		}

		// Get torch block.
		Block torchBlock = inputVector.add(new Vector(0, 2, 0)).toLocation(world).getBlock();

		// Add torches.
		blockGenerationQueue.add(new Pair<>(torchBlock.getRelative(BlockFace.EAST).getLocation(),
			Material.WALL_TORCH.createBlockData(blockData -> ((Directional) blockData).setFacing(BlockFace.EAST))));
		blockGenerationQueue.add(new Pair<>(torchBlock.getRelative(BlockFace.NORTH).getLocation(),
			Material.WALL_TORCH.createBlockData(blockData -> ((Directional) blockData).setFacing(BlockFace.NORTH))));
		blockGenerationQueue.add(new Pair<>(torchBlock.getRelative(BlockFace.WEST).getLocation(),
			Material.WALL_TORCH.createBlockData(blockData -> ((Directional) blockData).setFacing(BlockFace.WEST))));
		blockGenerationQueue.add(new Pair<>(torchBlock.getRelative(BlockFace.SOUTH).getLocation(),
			Material.WALL_TORCH.createBlockData(blockData -> ((Directional) blockData).setFacing(BlockFace.SOUTH))));

		if (timer != 0)
		{
			// Now Create Bukkit Task to generate portal.
			Bukkit.getScheduler().runTaskTimer(BentoBox.getInstance(),
				bukkitTask -> {
					if (blockGenerationQueue.isEmpty())
					{
						// Cancel task.
						bukkitTask.cancel();
						this.generating = false;
						this.generated = true;
					}
					else
					{
						// Else generate block at the given location.
						Pair<Location, BlockData> blockPair = blockGenerationQueue.poll();

						if (blockPair == null)
						{
							// Just to avoid NPE.
							bukkitTask.cancel();
						}
						else
						{
							blockPair.getKey().getBlock().setBlockData(blockPair.getValue());
						}
					}
				},
				10,
				timer);
		}
		else
		{
			Bukkit.getScheduler().runTask(BentoBox.getInstance(),
				bukkitTask ->
				{
					while (!blockGenerationQueue.isEmpty())
					{
						// Else generate block at the given location.
						Pair<Location, BlockData> blockPair = blockGenerationQueue.poll();
						blockPair.getKey().getBlock().setBlockData(blockPair.getValue());
					}

					this.generating = false;
					this.generated = true;

					bukkitTask.cancel();
				});
		}
	}


	/**
	 * This method tries to summon ender dragon at given location.
	 * @param location location where dragon should be summoned.
	 */
	public void trySummoning(Location location)
	{
		Collection<Entity> enderCrystals = this.world.getNearbyEntities(this.centerLocation,
			7,
			2,
			7,
			entity -> entity.getType().equals(EntityType.ENDER_CRYSTAL));

		if (enderCrystals.size() == 3)
		{
			// can be spawned, as last ender crystal was placed.

			boolean canContinue = location.getBlockX() == this.centerLocation.getBlockX() ^
				location.getBlockZ() == this.centerLocation.getBlockZ();

			for (Iterator<Entity> iterator = enderCrystals.iterator(); canContinue && iterator.hasNext(); )
			{
				Location entityLocation = iterator.next().getLocation();
				canContinue = entityLocation.getBlockX() == this.centerLocation.getBlockX() ^
					entityLocation.getBlockZ() == this.centerLocation.getBlockZ();
			}

			if (canContinue)
			{
				Utils.sendMessageToUsers(this.world, this.island, Constants.MESSAGES + "starting-countdown");

				this.summoning = true;

				// Something went wrong.
				this.islandData.setActiveFight(true);
				this.addon.getAddonManager().saveDragonFightsData(this.islandData);

				NamespacedKey namespacedKey =
					new NamespacedKey(BentoBox.getInstance(), this.island.getUniqueId());

				BossBar bossBar =
					Bukkit.createBossBar(
						namespacedKey,
						this.addon.getAddonManager().generateDragonName(this.island),
						this.addon.getSettings().getBossBarColour(),
						this.addon.getSettings().getBossBarStyle(),
						BarFlag.PLAY_BOSS_MUSIC);
				bossBar.setProgress(0.0);
				bossBar.setVisible(true);

				// For each player on the island add to boss bar.
				this.island.getPlayersOnIsland().
					forEach(entity -> {
						if (entity.getWorld().getEnvironment() == World.Environment.THE_END)
						{
							bossBar.addPlayer(entity);
						}
					});

				int warmUpTimer = this.addon.getSettings().getWarmUpTimer();

				if (warmUpTimer > 0)
				{
					final float increment = 1.0f / (warmUpTimer * 20);

					Bukkit.getScheduler().runTaskTimer(this.addon.getPlugin(),
						bukkitTask ->
						{
							this.displayTimer(bossBar, increment, warmUpTimer);

							if (bossBar.getProgress() + increment > 1.0f)
							{
								bukkitTask.cancel();
							}
						},
						0,
						1);
				}

				// Summon dragon 5 seconds later.
				Bukkit.getScheduler().runTaskLater(BentoBox.getInstance(),
					bukkitTask -> this.summonEnderDragon(bossBar),
					Math.max(0, warmUpTimer) * 20L);
			}
			else
			{
				Utils.sendMessageToUsers(this.world, this.island,
					Constants.ERRORS + "wrong-positions");
			}
		}
		else
		{
			Utils.sendMessageToUsers(this.world, this.island,
				Constants.MESSAGES + "missing-crystals",
				Constants.PARAMETER_NUMBER, String.valueOf(enderCrystals.size() + 1));
		}
	}


	/**
	 * Summon the dragon and add boss bar.
	 */
	private void summonEnderDragon(BossBar bossBar)
	{
		Optional<EnderDragon> optional = CustomEntityAPI.getAPI().getSpawnManager().spawn(
			new BentoBoxEnderDragonRoot(),
			this.centerLocation.clone().add(0, 30, 0));

		if (optional.isPresent())
		{
			// remove portal blocks at the end.
			this.generatePortal(false, 0);

			// Set progress to max value.
			bossBar.setProgress(1.0);
			bossBar.setTitle(this.addon.getAddonManager().generateDragonName(this.island));

			EnderDragon enderDragon = optional.get();

			// BentoBox Dragon namespace.
			enderDragon.getPersistentDataContainer().set(
				new NamespacedKey(BentoBox.getInstance(), "dragon"),
				PersistentDataType.STRING,
				this.island.getUniqueId());

			// Add portal location
			Vector vector = this.centerLocation.toVector().add(new Vector(0.5, 4, 0.5));
			enderDragon.getPersistentDataContainer().set(
				new NamespacedKey(BentoBox.getInstance(), "location"),
				PersistentDataType.STRING,
				vector.toString());
			Vector vector2 = enderDragon.getLocation().toVector();
			enderDragon.getPersistentDataContainer().set(
				new NamespacedKey(BentoBox.getInstance(), "spawn"),
				PersistentDataType.STRING,
				vector2.toString());

			// Get number of fly points.
			enderDragon.getPersistentDataContainer().set(
				new NamespacedKey(BentoBox.getInstance(), "tower_count"),
				PersistentDataType.INTEGER,
				this.addon.getSettings().getFlyPoints());
			// Get fly point distance.
			enderDragon.getPersistentDataContainer().set(
				new NamespacedKey(BentoBox.getInstance(), "tower_distance"),
				PersistentDataType.INTEGER,
				this.addon.getSettings().getPointDistance());
			// Get min fly altitude.
			enderDragon.getPersistentDataContainer().set(
				new NamespacedKey(BentoBox.getInstance(), "min_altitude"),
				PersistentDataType.INTEGER,
				this.addon.getSettings().getMinAltitude());
			// Get max fly altitude.
			enderDragon.getPersistentDataContainer().set(
				new NamespacedKey(BentoBox.getInstance(), "max_altitude"),
				PersistentDataType.INTEGER,
				this.addon.getSettings().getMaxAltitude());

			this.islandData.setDragonUUID(enderDragon.getUniqueId());
			this.addon.getAddonManager().saveDragonFightsData(this.islandData);
		}
		else
		{
			// Something went wrong.
			this.islandData.setActiveFight(false);
			this.addon.getAddonManager().saveDragonFightsData(this.islandData);

			// Remove boss bar
			bossBar.setProgress(0);
			bossBar.removeAll();
			bossBar.setVisible(false);

			Utils.sendMessageToUsers(this.world, this.island,
				Constants.ERRORS + "missing-entity-definition");
			this.addon.logError("Missing entity definition class in server!");
		}

		this.summoning = false;
	}


	/**
	 * This method updates time for boss bar.
	 * @param bossBar BossBar that requires update.
	 * @param increment Increment for boss bar counter.
	 */
	private void displayTimer(BossBar bossBar, float increment, int maxTimer)
	{
		bossBar.setProgress(bossBar.getProgress() + increment);
		long seconds = Math.round((1 - bossBar.getProgress()) * maxTimer);

		User user = User.getInstance(this.island.getOwner());

		if (user != null)
		{
			bossBar.setTitle(user.getTranslation(Constants.TIME_TILL_SPAWN,
				Constants.PARAMETER_NUMBER, String.valueOf(seconds)));
		}
		else
		{
			bossBar.setTitle(seconds + "");
		}
	}


	/**
	 * This method returns center location vector of the portal block.
	 * @return Center location of the protal location.
	 */
	public Vector getCenterLocation()
	{
		if (this.centerLocation != null)
		{
			return this.centerLocation.toVector();
		}
		else
		{
			return null;
		}
	}


	/**
	 * This method updates center location for searching algorithm.
	 * @param location new location.
	 */
	public void updateCenterLocation(Location location)
	{
		this.centerLocation = location;
	}


	/**
	 * Is generating boolean.
	 *
	 * @return the boolean
	 */
	public boolean isGenerating()
	{
		return this.generating;
	}


	/**
	 * Returns if portal is correctly generated already.
	 * @return the boolean
	 */
	public boolean isGenerated()
	{
		return generated;
	}


	/**
	 * Is summoning boolean.
	 *
	 * @return the boolean
	 */
	public boolean isSummoning()
	{
		return summoning;
	}


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------


	/**
	 * Instance of the addon.
	 */
	private final DragonFightsAddon addon;

	/**
	 * Island object where fight will happen.
	 */
	private final Island island;

	/**
	 * Data object that holds information about dragon fight adodn.
	 */
	private final DragonFightsObject islandData;

	/**
	 * World where fight is happening.
	 */
	private final World world;

	/**
	 * Center location of the portal.
	 */
	private Location centerLocation;

	/**
	 * Generator task to avoid calling multiple.
	 */
	private boolean generating = false;

	/**
	 * Indicates that generator is generated.
	 */
	private boolean generated = false;

	/**
	 * Indicates if summoning has started.
	 */
	private boolean summoning = false;
}
