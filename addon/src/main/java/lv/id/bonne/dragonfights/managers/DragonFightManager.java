//
// Created by BONNe
// Copyright - 2020
//


package lv.id.bonne.dragonfights.managers;


import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;

import lv.id.bonne.dragonfights.DragonFightsAddon;
import lv.id.bonne.dragonfights.database.objects.DragonFightsObject;
import lv.id.bonne.dragonfights.tasks.PortalGenerator;
import lv.id.bonne.dragonfights.utils.Constants;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.Database;
import world.bentobox.bentobox.database.objects.Island;


/**
 * This class manages data from DragonFightAddon.
 */
public class DragonFightManager
{
	/**
	 * Default constructor.
	 * @param addon Instance of DragonsFightsAddon
	 */
	public DragonFightManager(DragonFightsAddon addon)
	{
		this.addon = addon;
		this.operationWorlds = new HashSet<>(6);

		this.dragonFightsDatabase = new Database<>(addon, DragonFightsObject.class);
		this.dragonFightsCache = new HashMap<>();

		this.portalGeneratorCache = new HashMap<>();
	}


	/**
	 * Adds given world to operation worlds where dragonFights will work.
	 *
	 * @param world List of game mode names where this addon should work.
	 */
	public void addWorld(@Nullable World world)
	{
		if (world != null)
		{
			this.operationWorlds.add(world);
		}
	}


	/**
	 * Returns if addon operates in given world.
	 * @param world World that must be checked.
	 * @return {@code true} if addon can operate in given world, {@code false} otherwise.
	 */
	public boolean operatesInWorld(World world)
	{
		return this.operationWorlds.contains(world);
	}


// ---------------------------------------------------------------------
// Section: Data related methods
// ---------------------------------------------------------------------


	/**
	 * This method checks every island in stored worlds for user and loads them in cache.
	 *
	 * @param uniqueId User unique id.
	 */
	public void loadUserIslands(UUID uniqueId)
	{
		this.operationWorlds.stream().
			map(world -> this.addon.getIslands().getIsland(world, uniqueId)).
			filter(Objects::nonNull).
			forEach(this::addIslandData);
	}


	/**
	 * This method checks if island can be added to the cache.
	 *
	 * @param island Island object.
	 */
	public void loadIslandData(@NotNull Island island)
	{
		if (this.operationWorlds.contains(island.getWorld()))
		{
			this.addIslandData(island);
		}
	}


	/**
	 * This method allows to store single dragonFightsObject object.
	 *
	 * @param dragonFightsObject object that must be saved in database.
	 */
	public void saveDragonFightsData(DragonFightsObject dragonFightsObject)
	{
		this.dragonFightsDatabase.saveObjectAsync(dragonFightsObject);
	}


	/**
	 * This method returns data object for given island.
	 * @param island Island which data must be returned.
	 * @return instance of DragonFightsObject that stores data for given island.
	 */
	public DragonFightsObject getIslandData(@NotNull Island island)
	{
		this.addIslandData(island);
		return this.dragonFightsCache.get(island.getUniqueId());
	}


	/**
	 * This method tries to get addon data for island based on given string.
	 * @param islandUUID Island UUID value.
	 * @return instance of DragonFightsObject that stores data for given island or null.
	 */
	public @Nullable DragonFightsObject getIslandData(@NotNull String islandUUID)
	{
		// This is very very bad code, but bukkit Namespace generator does not support upper letters.

		return this.addon.getIslands().getIslandById(islandUUID).map(this::getIslandData).orElse(
			this.addon.getIslands().getIslandCache().getIslands().stream().
				filter(island -> island.getUniqueId().equalsIgnoreCase(islandUUID)).
				findFirst().
				map(this::getIslandData).
				orElse(null));
	}


	/**
	 * Load island from database into the cache or create new island data
	 *
	 * @param island - island that must be loaded
	 */
	private void addIslandData(@NotNull Island island)
	{
		final String uniqueID = island.getUniqueId();

		if (this.dragonFightsCache.containsKey(uniqueID))
		{
			return;
		}

		// The island is not in the cache
		// Check if the island exists in the database

		if (this.dragonFightsDatabase.objectExists(uniqueID))
		{
			// Load player from database
			DragonFightsObject data = this.dragonFightsDatabase.loadObject(uniqueID);
			// Store in cache

			if (data != null)
			{
				this.dragonFightsCache.put(uniqueID, data);
			}
			else
			{
				this.addon.logError("Could not load NULL generator data object.");
			}
		}
		else
		{
			// Create the island data
			DragonFightsObject pd = new DragonFightsObject();
			pd.setUniqueId(uniqueID);

			// Save data.
			this.saveDragonFightsData(pd);

			// Add to cache
			this.dragonFightsCache.put(uniqueID, pd);
		}
	}


	/**
	 * This method removes island data from cache and database.
	 * @param island Island data that must be removed.
	 */
	public void removeIslandData(@NotNull Island island)
	{
		this.addIslandData(island);

		if (this.dragonFightsCache.containsKey(island.getUniqueId()))
		{
			this.dragonFightsCache.remove(island.getUniqueId());
			this.dragonFightsDatabase.deleteID(island.getUniqueId());
		}
	}


// ---------------------------------------------------------------------
// Section: Portal Generator related methods
// ---------------------------------------------------------------------


	/**
	 * Gets portal generator.
	 *
	 * @param uniqueId the unique id
	 * @return the portal generator
	 */
	public @Nullable PortalGenerator getPortalGenerator(String uniqueId)
	{
		return this.portalGeneratorCache.get(uniqueId);
	}


	/**
	 * Add portal cache.
	 *
	 * @param uniqueId the unique id
	 * @param portalGenerator the portal generator
	 */
	public void addPortalCache(String uniqueId, PortalGenerator portalGenerator)
	{
		this.portalGeneratorCache.put(uniqueId, portalGenerator);
	}


// ---------------------------------------------------------------------
// Section: Dragon Name
// ---------------------------------------------------------------------


	/**
	 * This method generates dragon name based on island owner localization.
	 * @param island Island which dragon name must be generated.
	 * @return String of dragon name for the island.
	 */
	public String generateDragonName(Island island)
	{
		User owner = User.getInstance(island.getOwner());

		if (owner == null)
		{
			return island.getName();
		}
		else
		{
			return owner.getTranslation(Constants.DRAGON_NAME,
				Constants.PARAMETER_ISLAND, island.getName() == null ? "" : island.getName(),
				Constants.PARAMETER_OWNER, owner.getName());
		}
	}


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------


	/**
	 * Instance of dragon fights addon
	 */
	private final DragonFightsAddon addon;

	/**
	 * This variable holds worlds where addon should work.
	 */
	private final Set<World> operationWorlds;

	/**
	 * Database storage object.
	 */
	private final Database<DragonFightsObject> dragonFightsDatabase;

	/**
	 * Database cache.
	 */
	private final Map<String, DragonFightsObject> dragonFightsCache;

	/**
	 * Stores portal generator cache.
	 */
	private final Map<String, PortalGenerator> portalGeneratorCache;
}
