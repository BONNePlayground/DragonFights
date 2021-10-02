//
// Created by BONNe
// Copyright - 2020
//


package lv.id.bonne.dragonfights.managers;


import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.function.Consumer;

import lv.id.bonne.custombattle.CustomDragonBattle;
import lv.id.bonne.custombattle.DragonBattleBuilder;
import lv.id.bonne.dragonfights.DragonFightsAddon;
import lv.id.bonne.dragonfights.database.objects.DragonFightsObject;
import lv.id.bonne.dragonfights.entity.CustomEntityAPI;
import lv.id.bonne.dragonfights.utils.Constants;
import world.bentobox.bentobox.BentoBox;
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

		this.generatedBattles = new HashMap<>();
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
	 * This method saves every active battle.
	 */
	public void save()
	{
		this.generatedBattles.forEach((s, battle) -> {
			DragonFightsObject data = this.getIslandData(s);

			if (data != null)
			{
				Optional<Island> islandById = this.addon.getIslands().getIslandById(data.getUniqueId());
				World world = islandById.get().getWorld();

				data.setLatestBattleData(battle.saveData());
				data.setPortalLocation(battle.getGeneratedPortalLocation());
				data.setWorld(this.addon.getPlugin().getIWM().getEndWorld(world));
			}
		});

		// Save all dragons fights objects from cache to the database.
		this.dragonFightsCache.forEach((id, dragonFightsObject) ->
			this.dragonFightsDatabase.saveObjectAsync(dragonFightsObject));
	}


	/**
	 * This method loads everything from the database into local cache.
	 */
	public void load()
	{
		this.dragonFightsDatabase.loadObjects().forEach(dragonFightsObject -> {
			// Load into cache.
			this.dragonFightsCache.put(dragonFightsObject.getUniqueId(), dragonFightsObject);

			// If there was an active battle, put it into start list.
			if (dragonFightsObject.getLatestBattleData() != null &&
				!dragonFightsObject.getLatestBattleData().isEmpty())
			{
				DragonBattleBuilder builder =
					CustomEntityAPI.getAPI().createDragonBattleBuilder(dragonFightsObject.getUniqueId());
				builder.setWorld(dragonFightsObject.getWorld());

				CustomDragonBattle customDragonBattle =
					builder.buildFromNBT(dragonFightsObject.getLatestBattleData());

				this.generatedBattles.put(dragonFightsObject.getUniqueId(), customDragonBattle);

				if (customDragonBattle != null)
				{
					// Start the battle with 5 sec delay.
					this.startBattleTask(dragonFightsObject, customDragonBattle, 20 * 5);
				}
			}
		});
	}


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
// Section: Dragon Battle Generation
// ---------------------------------------------------------------------


	/**
	 * This method returns Optional with generated dragon battle with given UniqueId.
	 * @param uniqueId Battle which must be returned.
	 * @return Optional with custom dragon battle.
	 */
	public Optional<CustomDragonBattle> getDragonBattle(String uniqueId)
	{
		return Optional.ofNullable(this.generatedBattles.get(uniqueId));
	}


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


	/**
	 * This method creates a new dragon battle instance using CustomEntityAPI.
	 * @param world World where battle can be generated.
	 * @param island Island on which battle is starting.
	 * @return Instance of CustomDragonBattle that is started.
	 */
	public CustomDragonBattle createDragonBattle(World world, Island island)
	{
		DragonFightsObject dragonFightsObject = this.getIslandData(island);
		dragonFightsObject.setWorld(world);

		DragonBattleBuilder dragonBattleBuilder = CustomEntityAPI.getAPI().createDragonBattleBuilder(island.getUniqueId());
		dragonBattleBuilder.setDragonKilled(true);
		dragonBattleBuilder.setPreviouslyKilled(dragonFightsObject.getDragonsKilled() > 0);
		dragonBattleBuilder.setWorld(world);

		if (dragonFightsObject.getPortalLocation() != null)
		{
			dragonBattleBuilder.setPortalLocation(dragonFightsObject.getPortalLocation());
		}
		else
		{
			dragonBattleBuilder.setPortalLocation(island.getCenter().toVector());
		}

		dragonBattleBuilder.setBoundingBox(island.getBoundingBox());
		dragonBattleBuilder.setRange(island.getRange());

		dragonBattleBuilder.setBattleSeed(this.addon.getSettings().getBattleSeed());
		dragonBattleBuilder.setNumberOfTowers(this.addon.getSettings().getTowerCount());
		dragonBattleBuilder.setNumberOfProtectedTowers(this.addon.getSettings().getNumberOfProtectedTowers());
		dragonBattleBuilder.setMaxTowerHeight(this.addon.getSettings().getMaxTowerHeight());
		dragonBattleBuilder.setMinTowerHeight(this.addon.getSettings().getMinTowerHeight());
		dragonBattleBuilder.setDistanceTillTowers(this.addon.getSettings().getTowerDistance());

		dragonBattleBuilder.setPlayMusic(this.addon.getSettings().isPlayMusic());
		dragonBattleBuilder.setEnableFog(this.addon.getSettings().isEnableFog());

		dragonBattleBuilder.setBossBarStyle(this.addon.getSettings().getBossBarStyle());
		dragonBattleBuilder.setBossBarColor(this.addon.getSettings().getBossBarColour());
		dragonBattleBuilder.setBossBarText(this.generateDragonName(island));

		CustomDragonBattle battle = dragonBattleBuilder.build();

		// start the battle

		if (battle != null)
		{
			this.generatedBattles.put(island.getUniqueId(), battle);
			this.startBattleTask(dragonFightsObject, battle, 0);
		}

		return battle;
	}


	/**
	 * This is a battle task timer. It should be called just once for each battle.
	 * TODO: probably need to implement max active battles at once.
	 * @param databaseObject The database object.
	 * @param battle Battle that must be started.
	 * @param delay The delay after which task will start to run.
	 */
	public void startBattleTask(DragonFightsObject databaseObject, CustomDragonBattle battle, long delay)
	{
		Bukkit.getScheduler().runTaskTimer(BentoBox.getInstance(),
			new BattleTick(databaseObject, battle),
			delay,
			1);
	}


	/**
	 * This method process battle finishing data.
	 * @param databaseObject The database object.
	 * @param battle that must be finished.
	 */
	public void finishTheBattle(DragonFightsObject databaseObject, CustomDragonBattle battle)
	{
		// Reset data to the null value.
		databaseObject.setLatestBattleData("");
		databaseObject.setDragonsKilled(databaseObject.getDragonsKilled() + 1);
		databaseObject.setPortalLocation(battle.getGeneratedPortalLocation());
		// Sava data.
		this.saveDragonFightsData(databaseObject);
	}


// ---------------------------------------------------------------------
// Section: Classes
// ---------------------------------------------------------------------


	/**
	 * This class process battle ticking.
	 */
	private class BattleTick implements Consumer<BukkitTask>
	{
		/**
		 * Instantiates a new Battle tick.
		 *
		 * @param databaseObject the database object
		 * @param battle the battle
		 */
		protected BattleTick(DragonFightsObject databaseObject, CustomDragonBattle battle)
		{
			this.databaseObject = databaseObject;
			this.battle = battle;
		}

		@Override
		public void accept(BukkitTask task)
		{
			boolean loadedChunks;

			if (this.continueChecks &&
				this.battle.getLastDragonUUID() != null &&
				this.battle.getLastDragonLocation() != null)
			{
				int chunkX = this.battle.getLastDragonLocation().getBlockX() >> 4;
				int chunkZ = this.battle.getLastDragonLocation().getBlockZ() >> 4;

				// Get world.
				World world = this.databaseObject.getWorld();

				// Check if chunks are loaded try to find dragon entity.
				if (world.isChunkLoaded(chunkX, chunkZ))
				{
					// Find entity with a given id.
					// Wait 5 seconds till restart the dragon
					// Dragon exists... load the battle
					loadedChunks = this.ticksWithoutDragons++ > 5 * 20 ||
						world.getLivingEntities().stream().anyMatch(entity ->
							entity.getUniqueId().equals(this.battle.getLastDragonUUID()));
				}
				else
				{
					loadedChunks = false;
				}
			}
			else
			{
				// No portal location means that battle must be force started.
				loadedChunks = true;
			}

			if (loadedChunks)
			{
				this.battle.tickBattle();

				if (this.battle.isFinished())
				{
					DragonFightManager.this.finishTheBattle(this.databaseObject, this.battle);
					task.cancel();
				}

				this.continueChecks = false;
			}
		}


	// ---------------------------------------------------------------------
	// Section: Variables
	// ---------------------------------------------------------------------

		/**
		 * Boolean that enables entity searching.
		 */
		private boolean continueChecks = true;

		/**
		 * This method checks ticks without living dragon.
		 */
		private int ticksWithoutDragons;

		/**
		 * Database object instance.
		 */
		private final DragonFightsObject databaseObject;

		/**
		 * Battle object instance.
		 */
		private final CustomDragonBattle battle;
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
	private final Map<String, CustomDragonBattle> generatedBattles;
}
