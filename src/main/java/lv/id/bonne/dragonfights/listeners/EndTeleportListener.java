//
// Created by BONNe
// Copyright - 2020
//


package lv.id.bonne.dragonfights.listeners;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import lv.id.bonne.dragonfights.DragonFightsAddon;


/**
 * This is a hacky implementation for issue when player is not able to teleport to overworld, after stepping into portal
 * at the end.
 */
public class EndTeleportListener implements Listener
{
	/**
	 * @param addon - addon
	 */
	public EndTeleportListener(DragonFightsAddon addon)
	{
		this.addon = addon;
	}


	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerPortalEven(EntityPortalEnterEvent event)
	{
		// I am interested only in player entities.
		if (event.getEntity().getType() != EntityType.PLAYER)
		{
			return;
		}

		// Only in end portal blocks
		if (event.getLocation().getBlock().getType() != Material.END_PORTAL)
		{
			return;
		}

		// Interest only in end worlds
		if (!this.addon.getPlugin().getIWM().isIslandEnd(event.getLocation().getWorld()))
		{
			return;
		}

		// Only in worlds where addon operates
		if (!this.addon.getAddonManager().operatesInWorld(event.getLocation().getWorld()))
		{
			return;
		}

		// Trigger teleport event so BentoBox could handle it.
		PlayerPortalEvent teleportEvent = new PlayerPortalEvent(
			(Player) event.getEntity(),
			event.getLocation(),
			null,
			PlayerTeleportEvent.TeleportCause.END_PORTAL,
			0,
			false,
			0);
		Bukkit.getPluginManager().callEvent(teleportEvent);
	}


	/**
	 * Instance of addon.
	 */
	private final DragonFightsAddon addon;
}
