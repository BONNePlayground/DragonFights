//
// Created by BONNe
// Copyright - 2020
//


package lv.id.bonne.dragonfights.database.objects;


import com.google.gson.annotations.Expose;

import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

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
	 * Gets world.
	 *
	 * @return the world
	 */
	public World getWorld()
	{
		return world;
	}


	/**
	 * Sets world.
	 *
	 * @param world the world
	 */
	public void setWorld(World world)
	{
		this.world = world;
	}


	/**
	 * Gets portal location.
	 *
	 * @return the portal location
	 */
	public Vector getPortalLocation()
	{
		return portalLocation;
	}


	/**
	 * Sets portal location.
	 *
	 * @param portalLocation the portal location
	 */
	public void setPortalLocation(Vector portalLocation)
	{
		this.portalLocation = portalLocation;
	}


	/**
	 * Gets latest battle data.
	 *
	 * @return the latest battle data
	 */
	public String getLatestBattleData()
	{
		return latestBattleData;
	}


	/**
	 * Sets latest battle data.
	 *
	 * @param latestBattleData the latest battle data
	 */
	public void setLatestBattleData(String latestBattleData)
	{
		this.latestBattleData = latestBattleData;
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


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------


	/**
	 * Unique Id for the object based on island UUID.
	 */
	@Expose
	private String uniqueId;

	/**
	 * World data where fight is happening.
	 */
	@Expose
	private World world;

	/**
	 * Portal location.
	 */
	@Expose
	private @Nullable Vector portalLocation;

	/**
	 * Latest battle data.
	 */
	@Expose
	private @Nullable String latestBattleData;

	/**
	 * Number of dragons killed on the island.
	 */
	@Expose
	private long dragonsKilled = 0;
}
