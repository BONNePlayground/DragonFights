//
// Created by BONNe
// Copyright - 2023
//


package lv.id.bonne.dragonfights.listeners;


import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.Optional;

import lv.id.bonne.dragonfights.DragonFightsAddon;
import lv.id.bonne.dragonfights.database.objects.DragonFightsObject;
import lv.id.bonne.dragonfights.managers.DragonFightManager;
import world.bentobox.bentobox.database.objects.Island;


/**
 * This class checks dragon related things.
 */
public class DragonListener implements Listener
{
    /**
     * @param addon - addon
     */
    public DragonListener(DragonFightsAddon addon)
    {
        this.addon = addon;
        this.addonManager = addon.getAddonManager();
    }


    /**
     * This listener manages entity spawning advancements.
     * @param event Dragon spawn event.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDragonSummon(EntitySpawnEvent event)
    {
        if (!EntityType.ENDER_DRAGON.equals(event.getEntityType()))
        {
            // Interested only in custom dragon entity.
            return;
        }

        if (this.addon.getSettings().getSummonAdvancementList().isEmpty() &&
            this.addon.getSettings().getReSummonAdvancementList().isEmpty())
        {
            // Advancement granting is not necessary.
            return;
        }

        Optional<Island> optionalIsland =
            this.addon.getIslands().getIslandAt(event.getLocation());

        optionalIsland.ifPresent(island ->
        {
            DragonFightsObject islandData = this.addonManager.getIslandData(island);

            if (islandData != null)
            {
                long dragonsKilled = islandData.getDragonsKilled();

                if (dragonsKilled == 0)
                {
                    this.addonManager.grantAdvancements(island,
                        this.addon.getSettings().getSummonAdvancementList());
                }
                else
                {
                    this.addonManager.grantAdvancements(island,
                        this.addon.getSettings().getReSummonAdvancementList());
                }
            }
        });
    }


    /**
     * This listener manages entity death advancements.
     * @param event Dragon death event.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDragonKilled(EntityDeathEvent event)
    {
        if (event.getEntityType() != EntityType.ENDER_DRAGON)
        {
            // Only interested in ender dragon kills.
            return;
        }

        this.addon.getIslands().getIslandAt(event.getEntity().getLocation()).
            ifPresent(island -> {
                this.addonManager.getDragonBattle(island.getUniqueId()).ifPresent(battle ->
                {
                    if (event.getEntity().getUniqueId().equals(battle.getLastDragonUUID()))
                    {
                        // Grant advancement to all players on island
                        this.addonManager.grantAdvancements(island,
                            this.addon.getSettings().getKilledAdvancementList());

                        // Grant advancements to the killer.
                        this.addonManager.grantAdvancements(event.getEntity().getKiller(),
                            this.addon.getSettings().getKillerAdvancementList());
                    }
                });
            });
    }


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------


    /**
     * DragonFightsAddon instance.
     */
    private final DragonFightsAddon addon;

    /**
     * Addon Manager Instance.
     */
    private final DragonFightManager addonManager;
}
