//
// Created by BONNe
// Copyright - 2020
//


package lv.id.bonne.dragonfights.database.objects;


import com.google.gson.annotations.Expose;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import world.bentobox.bentobox.database.objects.DataObject;
import world.bentobox.bentobox.database.objects.Table;


/**
 * This object stores active dragon battles, portal locations and killed dragon count for each island.
 */
@Table(name = "DragonFightsData")
public class DragonFightsObject implements DataObject
{
	/**
	 * Default constructor.
	 */
	public DragonFightsObject()
	{
	}


// ---------------------------------------------------------------------
// Section: Getters and setters
// ---------------------------------------------------------------------


	/**
	 * Returns unique id for the object.
	 * @return uniqueId value
	 */
	@Override
	public String getUniqueId()
	{
		return uniqueId;
	}


	/**
	 * Sets unique id for the object.
	 * @param uniqueId new uniqueId value.
	 */
	@Override
	public void setUniqueId(String uniqueId)
	{
		this.uniqueId = uniqueId;
	}


	/**
	 * Is active fight boolean.
	 *
	 * @return the boolean
	 */
	public boolean isActiveFight()
	{
		return activeFight;
	}


	/**
	 * Sets active fight.
	 *
	 * @param activeFight the active fight
	 */
	public void setActiveFight(boolean activeFight)
	{
		this.activeFight = activeFight;
	}


	/**
	 * Gets latest spawn location.
	 *
	 * @return the latest spawn location
	 */
	public @Nullable Vector getLatestSpawnLocation()
	{
		return latestSpawnLocation;
	}


	/**
	 * Sets latest spawn location.
	 *
	 * @param latestSpawnLocation the latest spawn location
	 */
	public void setLatestSpawnLocation(@Nullable Vector latestSpawnLocation)
	{
		this.latestSpawnLocation = latestSpawnLocation;
	}


	/**
	 * Gets latest portal location.
	 *
	 * @return the latest portal location
	 */
	public @Nullable Vector getLatestPortalLocation()
	{
		return latestPortalLocation;
	}


	/**
	 * Sets latest portal location.
	 *
	 * @param latestPortalLocation the latest portal location
	 */
	public void setLatestPortalLocation(@Nullable Vector latestPortalLocation)
	{
		this.latestPortalLocation = latestPortalLocation;
	}


	/**
	 * Gets dragons killed.
	 *
	 * @return the dragons killed
	 */
	public long getDragonsKilled()
	{
		return dragonsKilled;
	}


	/**
	 * Sets dragons killed.
	 *
	 * @param dragonsKilled the dragons killed
	 */
	public void setDragonsKilled(long dragonsKilled)
	{
		this.dragonsKilled = dragonsKilled;
	}


	/**
	 * Gets boss bar id.
	 *
	 * @return the boss bar id
	 */
	public @Nullable UUID getDragonUUID()
	{
		return dragonUUID;
	}


	/**
	 * Sets boss bar id.
	 *
	 * @param dragonUUID the boss bar id
	 */
	public void setDragonUUID(@Nullable UUID dragonUUID)
	{
		this.dragonUUID = dragonUUID;
	}


	/**
	 * Increase number of killed dragons.
	 */
	public void increaseKilledDragons()
	{
		this.dragonsKilled++;
	}


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------


	/**
	 * Unique Id for the object based on island UUID.
	 */
	@Expose
	private String uniqueId;

	/**
	 * Boolean that indicates that there are a dragon on the island.
	 */
	@Expose
	private boolean activeFight = false;

	/**
	 * UUID of the latest dragon entity.
	 */
	@Expose
	private @Nullable UUID dragonUUID = null;

	/**
	 * Spawn location of the dragon
	 */
	@Expose
	private @Nullable Vector latestSpawnLocation;

	/**
	 * Portal location of the dragon.
	 */
	@Expose
	private @Nullable Vector latestPortalLocation;

	/**
	 * Number of dragons killed on the island.
	 */
	@Expose
	private long dragonsKilled = 0;
}
