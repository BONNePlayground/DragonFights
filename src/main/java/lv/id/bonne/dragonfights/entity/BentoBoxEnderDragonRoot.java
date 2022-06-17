//
// Created by BONNe
// Copyright - 2020
//


package lv.id.bonne.dragonfights.entity;


import org.bukkit.entity.EnderDragon;
import java.util.Collection;
import java.util.Collections;

import io.github.iltotore.customentity.BiomeSpawn;
import io.github.iltotore.customentity.CreatureType;
import io.github.iltotore.customentity.type.CompositeEntityRoot;
import io.github.iltotore.customentity.util.ServerVersion;


/**
 * This class allows to load ender dragon for different minecraft versions.
 */
public class BentoBoxEnderDragonRoot extends CompositeEntityRoot<EnderDragon>
{
	/**
	 * Base entity key.
	 * @return "ender_dragon"
	 */
	@Override
	public String getBaseKey()
	{
		return "ender_dragon";
	}


	/**
	 * Custom entity key. For summoning.
	 * @return "bentobox_ender_dragon"
	 */
	@Override
	public String getKey()
	{
		return "bentobox_ender_dragon";
	}


	/**
	 * Creature type.
	 * @return Type of the creature.
	 */
	@Override
	public CreatureType getCreatureType()
	{
		return CreatureType.MONSTER;
	}


	/**
	 * Indicates if vanilla entity must be overwritten with it.
	 * @param version ServerVersion on which entity is created.
	 * @return false always.
	 */
	@Override
	public boolean isVanilla(ServerVersion version)
	{
		return false;
	}


	/**
	 * Biomes where entity should be spawned.
	 * @param version ServerVersion on which entity is created.
	 * @return Empty collection.
	 */
	@Override
	public Collection<BiomeSpawn> getSpawns(ServerVersion version)
	{
		// No natural spawning.
		return Collections.emptyList();
	}


	// ---------------------------------------------------------------------
	// Section: Static method calls on construction
	// ---------------------------------------------------------------------


	/**
	 * Populate entities based on server version.
	 */
	{
		this.setVersion(ServerVersion.v1_18_1, lv.id.bonne.dragonfights.v1_18_R1.entity.BentoBoxEnderDragonType::new);
		this.setVersion(ServerVersion.v1_18_2, lv.id.bonne.dragonfights.v1_18_R2.entity.BentoBoxEnderDragonType::new);
		this.setVersion(ServerVersion.v1_19, lv.id.bonne.dragonfights.v1_19_R1.entity.BentoBoxEnderDragonType::new);
	}
}

