//
// Created by BONNe
// Copyright - 2020
//


package lv.id.bonne.dragonfights.listeners;


import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Optional;

import lv.id.bonne.dragonfights.DragonFightsAddon;
import lv.id.bonne.dragonfights.database.objects.DragonFightsObject;
import lv.id.bonne.dragonfights.utils.Utils;
import world.bentobox.bentobox.api.events.island.IslandEvent;
import world.bentobox.bentobox.database.objects.Island;


/**
 * This listener loads player islands in cache when they login.
 */
public class JoinLeaveListener implements Listener
{
	/**
	 * @param addon - addon
	 */
	public JoinLeaveListener(DragonFightsAddon addon)
	{
		this.addon = addon;
	}


	/**
	 * This method handles player join event. When player joins it loads all its islands
	 * into local cache.
	 * @param event PlayerJoinEvent instance.
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		// Load player into cache
		this.addon.getAddonManager().loadUserIslands(event.getPlayer().getUniqueId());
		this.updatesBossBars(event.getPlayer());
	}


	/**
	 * This method handles Island Created, Resetted and Registered events.
	 * @param event Event that must be handled.
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onNewIsland(IslandEvent event)
	{
		if (event.getReason().equals(IslandEvent.Reason.CREATED) ||
			event.getReason().equals(IslandEvent.Reason.RESETTED) ||
			event.getReason().equals(IslandEvent.Reason.REGISTERED))
		{
			this.addon.getAddonManager().loadIslandData(event.getIsland());
		}
	}


	/**
	 * This method handles island deletion. On island deletion it should remove
	 * generator data too.
	 * @param event IslandDeletedEvent instance.
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onIslandDelete(IslandEvent.IslandDeleteEvent event)
	{
		this.addon.getAddonManager().removeIslandData(event.getIsland());
	}


	/**
	 * Just monitor if anything should be changed.
	 * @param event PlayerChangedWorldEvent instance
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWorldSwitch(PlayerChangedWorldEvent event)
	{
		// If player moves from end world, then remove it from all bossbars.
		if (this.addon.getPlugin().getIWM().isIslandEnd(event.getFrom()))
		{
			Bukkit.getBossBars().forEachRemaining(bossBar -> bossBar.removePlayer(event.getPlayer()));
		}
		// If player moves to the end, check if boss battle is active.
		else if (this.addon.getPlugin().getIWM().isIslandEnd(event.getPlayer().getWorld()))
		{
			this.updatesBossBars(event.getPlayer());
		}
	}


	/**
	 * This method updates boss bar for a given player, if that is required.
	 * @param player Player who boss bars must be updated.
	 */
	private void updatesBossBars(Player player)
	{
		Optional<Island> islandAt = this.addon.getIslands().getIslandAt(player.getLocation());

		if (islandAt.isPresent() && player.getWorld().getEnvironment() == World.Environment.THE_END)
		{
			DragonFightsObject object = this.addon.getAddonManager().getIslandData(islandAt.get());

			if (object.isActiveFight())
			{
				BossBar bossBar = this.getBossBar(islandAt.get());

				if (object.getDragonUUID() != null)
				{
					// Update boss bar 1 tick later so entity can be loaded in game
					Bukkit.getScheduler().runTaskLater(this.addon.getPlugin(),
						bukkitTask ->
						{
							bossBar.addPlayer(player);
							bossBar.setVisible(true);
							Utils.updateBossBar(bossBar, Bukkit.getEntity(object.getDragonUUID()));
						},
						10);

				}
			}
		}
	}


	/**
	 * This method returns boss bar for given island.
	 * @param island Island which boss bar must be returned.
	 * @return BossBar instance.
	 */
	private BossBar getBossBar(Island island)
	{
		NamespacedKey bossBarKey = Utils.getNamespaceKey(this.addon.getPlugin(), island.getUniqueId());
		// Get active BossBar
		BossBar bossBar = Bukkit.getBossBar(bossBarKey);

		if (bossBar == null)
		{
			bossBar = Bukkit.createBossBar(
				bossBarKey,
				this.addon.getAddonManager().generateDragonName(island),
				this.addon.getSettings().getBossBarColour(),
				this.addon.getSettings().getBossBarStyle(),
				BarFlag.PLAY_BOSS_MUSIC);
			bossBar.setProgress(1.0);
		}

		return bossBar;
	}


	/**
	 * stores addon instance
	 */
	private final DragonFightsAddon addon;
}