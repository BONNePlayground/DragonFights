//
// Created by BONNe
// Copyright - 2020
//


package lv.id.bonne.dragonfights.listeners;


import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Optional;

import lv.id.bonne.dragonfights.DragonFightsAddon;
import lv.id.bonne.dragonfights.database.objects.DragonFightsObject;
import lv.id.bonne.dragonfights.tasks.PortalGenerator;
import lv.id.bonne.dragonfights.utils.Utils;
import world.bentobox.bentobox.database.objects.Island;


/**
 * This listener checks if damaged entity is ender dragon created by this addon.
 */
public class DragonDamageListener implements Listener
{
	/**
	 * Instantiates a new Dragon damage listener.
	 *
	 * @param addon the addon
	 */
	public DragonDamageListener(DragonFightsAddon addon)
	{
		this.addon = addon;
	}


	/**
	 * This listener monitors entity damage event and if damaged entity is ender dragon,
	 * then it updates boss bar value.
	 * @param event EntityDamageEvent that must be monitored.
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDragonDamages(EntityDamageEvent event)
	{
		if (event.getEntityType() != EntityType.ENDER_DRAGON)
		{
			return;
		}

		EnderDragon dragonEntity = (EnderDragon) event.getEntity();

		// Gets dragon UUID from persistence data.
		String dragonUUID = Utils.getPersistenceString(dragonEntity,
			Utils.getNamespaceKey(this.addon.getPlugin(), "dragon"));

		if (dragonUUID == null || dragonUUID.isEmpty())
		{
			this.addon.logWarning("Dragon without persistence data was damaged!");
			// No data about this ender dragon.
			return;
		}

		Utils.updateBossBar(Bukkit.getBossBar(Utils.getNamespaceKey(this.addon.getPlugin(), dragonUUID)),
			dragonEntity);
	}


	/**
	 * This listener monitors entity death event and if it is ender dragon,
	 * then it removes boss bar.
	 * @param event EntityDeathEvent that must be monitored.
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDragonKilled(EntityDeathEvent event)
	{
		if (event.getEntityType() != EntityType.ENDER_DRAGON)
		{
			return;
		}

		EnderDragon dragonEntity = (EnderDragon) event.getEntity();

		// Gets dragon UUID from persistence data.
		String dragonUUID = Utils.getPersistenceString(dragonEntity,
			Utils.getNamespaceKey(this.addon.getPlugin(), "dragon"));

		if (dragonUUID == null || dragonUUID.isEmpty())
		{
			this.addon.logWarning("Dragon without persistence data was killed!");

			// No data about this ender dragon.
			return;
		}

		// Update bossbar
		Utils.updateBossBar(
			Bukkit.getBossBar(Utils.getNamespaceKey(this.addon.getPlugin(), dragonUUID)),
			dragonEntity);

		// remove boss bar after 5 seconds
		Bukkit.getScheduler().runTaskLater(this.addon.getPlugin(),
			bukkitTask ->
			{
				BossBar bossBar = Bukkit.getBossBar(Utils.getNamespaceKey(this.addon.getPlugin(), dragonUUID));

				if (bossBar != null)
				{
					bossBar.removeAll();
					bossBar.setVisible(false);
					Bukkit.removeBossBar(Utils.getNamespaceKey(this.addon.getPlugin(), dragonUUID));
				}
			},
			15 * 20L);

		// Get island data.
		DragonFightsObject data = this.addon.getAddonManager().getIslandData(dragonUUID);

		if (data == null)
		{
			this.addon.logWarning("Could not find an island fight data with ID: " + dragonUUID);
			// there is no data about this dragon in database.
			return;
		}

		// End battle
		data.setActiveFight(false);
		data.increaseKilledDragons();
		data.setLatestSpawnLocation(null);

		// Regenerate portal
		PortalGenerator portalGenerator = this.addon.getAddonManager().getPortalGenerator(data.getUniqueId());

		if (portalGenerator == null)
		{
			Optional<Island> islandById = this.addon.getIslands().getIslandById(dragonUUID);
			if (islandById.isPresent() && data.getLatestPortalLocation() != null)
			{
				// Create and generate full portal.
				portalGenerator = new PortalGenerator(this.addon,
					islandById.get(),
					data.getLatestPortalLocation().toLocation(dragonEntity.getWorld()));
				portalGenerator.generateFullPortalFast();
			}
		}
		else
		{
			// generate full portal.
			portalGenerator.generateFullPortalFast();
		}

		this.addon.getAddonManager().saveDragonFightsData(data);
	}


	/**
	 * DragonFightsAddon instance.
	 */
	private final DragonFightsAddon addon;
}
