//
// Created by BONNe
// Copyright - 2020
//


package lv.id.bonne.dragonfights.listeners;


import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.Optional;

import lv.id.bonne.dragonfights.DragonFightsAddon;
import lv.id.bonne.dragonfights.database.objects.DragonFightsObject;
import lv.id.bonne.dragonfights.tasks.PortalGenerator;
import lv.id.bonne.dragonfights.utils.Constants;
import lv.id.bonne.dragonfights.utils.Utils;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;


/**
 * This method activates ender dragon battle.
 */
public class ActivationListener implements Listener
{
	/**
	 * Main class Constructor.
	 * @param addon DragonFightsAddon instance.
	 */
	public ActivationListener(DragonFightsAddon addon)
	{
		this.addon = addon;
	}


	/**
	 * This event checks if dragon battle must be started based on placed crystals around spawn portal.
	 * @param event Entity Spawn event that must be monitored.
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCrystalPlacement(EntitySpawnEvent event)
	{
		if (event.getEntityType() != EntityType.ENDER_CRYSTAL)
		{
			// Not an ender crystal.
			return;
		}

		World world = event.getLocation().getWorld();

		if (world == null || !this.addon.getPlugin().getIWM().isIslandEnd(world))
		{
			// Not a bentobox end island.
			return;
		}

		if (!this.addon.getAddonManager().operatesInWorld(world))
		{
			// Not operating in given gamemode.
			return;
		}

		if (event.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.BEDROCK)
		{
			// Not on the bedrock.
			return;
		}

		Location location = event.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation();
		Optional<Island> optionalIsland = this.addon.getPlugin().getIslands().getIslandAt(location);

		if (!optionalIsland.isPresent())
		{
			// Not on the island
			return;
		}

		// Get fight object.
		DragonFightsObject fightData = this.addon.getAddonManager().getIslandData(optionalIsland.get());
		PortalGenerator portalGenerator = this.addon.getAddonManager().getPortalGenerator(fightData.getUniqueId());

		if (portalGenerator == null)
		{
			// There are no cached portal generators. Construct a new one.
			portalGenerator = new PortalGenerator(this.addon, optionalIsland.get(), location);
			portalGenerator.searchPortalCenter();

			this.addon.getAddonManager().addPortalCache(fightData.getUniqueId(), portalGenerator);
			fightData.setLatestPortalLocation(portalGenerator.getCenterLocation());
		}
		else if (portalGenerator.getCenterLocation() == null)
		{
			portalGenerator.updateCenterLocation(location);
			portalGenerator.searchPortalCenter();
			fightData.setLatestPortalLocation(portalGenerator.getCenterLocation());

			Utils.sendMessageToUsers(world, optionalIsland.get(), Constants.MESSAGES + "portal-position-updated");
		}

		if (fightData.isActiveFight())
		{
			if (!portalGenerator.isSummoning() &&
				event.getLocation().getBlockX() == portalGenerator.getCenterLocation().getBlockX() &&
				event.getLocation().getBlockZ() == portalGenerator.getCenterLocation().getBlockZ())
			{
				// Revalidate if entity is still alive.

				if (fightData.getDragonUUID() != null)
				{
					Entity entity = Bukkit.getEntity(fightData.getDragonUUID());

					if (entity == null)
					{
						fightData.setActiveFight(false);
					}
				}
				else
				{
					fightData.setActiveFight(false);
				}
			}
			else
			{
				// Battle is active.
				return;
			}
		}

		// Check if there is a portal?
		if (portalGenerator.isActivePortal() || portalGenerator.isGenerated())
		{
			// Last placed crystal location
			portalGenerator.trySummoning(event.getLocation());
		}
		else if (portalGenerator.isValidPortal())
		{
			// Error to the user that portal cannot be created.
			Utils.sendMessageToUsers(world, optionalIsland.get(), Constants.MESSAGES + "starting-portal-generation");
			this.addon.log("User created a new portal at location: " + portalGenerator.getCenterLocation().toString());

			// create portal. Portal generator will not summon dragon.
			portalGenerator.generateFullPortalSlow();

			// Do not remove entity if it is not generating yet.
			if (!portalGenerator.isGenerating())
			{
				// Remove entity after 10 ticks
				Bukkit.getScheduler().runTaskLater(BentoBox.getInstance(),
					bukkitTask -> event.getEntity().remove(),
					10);
			}
		}
		else
		{
			// Error to the user that portal cannot be created.
			Utils.sendMessageToUsers(world, optionalIsland.get(), Constants.ERRORS + "could-not-construct-portal");
			this.addon.logWarning("Could not construct valid portal at location: " + event.getLocation().toString());
		}
	}


	/**
	 * DragonFightsAddon instance.
	 */
	private final DragonFightsAddon addon;
}
