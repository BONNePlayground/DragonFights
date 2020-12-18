//
// Created by BONNe
// Copyright - 2020
//


package lv.id.bonne.dragonfights.utils;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import java.util.Iterator;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;


/**
 * This method contains some useful things.
 */
public class Utils
{
	/**
	 * This method generates vector iterator from min coordinate till max coordinage.
	 * @param minVector starting coordinate.
	 * @param maxVector end coordinate.
	 * @return Vector iterator from min till max coordinate.
	 */
	public static Iterator<Vector> getVectorIterator(Vector minVector, Vector maxVector)
	{
		return Utils.vectorIterator(
			Math.min(minVector.getBlockX(), maxVector.getBlockX()),
			Math.min(minVector.getBlockY(), maxVector.getBlockY()),
			Math.min(minVector.getBlockZ(), maxVector.getBlockZ()),
			Math.max(minVector.getBlockX(), maxVector.getBlockX()),
			Math.max(minVector.getBlockY(), maxVector.getBlockY()),
			Math.max(minVector.getBlockZ(), maxVector.getBlockZ())).
			iterator();
	}


	/**
	 * This method generates Iterable vector object from min till max coordinate.
	 * @param minX Minimal X coordinate.
	 * @param minY Minimal Y coordinate.
	 * @param minZ Minimal Z coordinate.
	 * @param maxX Maximal X coordinate.
	 * @param maxY Maximal Y coordinate.
	 * @param maxZ Maximal Z coordinate.
	 * @return Iterable vector object.
	 */
	private static Iterable<Vector> vectorIterator(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
	{
		int x = maxX - minX + 1;
		int y = maxY - minY + 1;
		int z = maxZ - minZ + 1;
		int end = x * y * z;

		// return iterator that calculates next block vector.
		return () -> new Iterator<Vector>()
		{
			/**
			 * Returns {@code true} if the iteration has more elements. (In other words, returns {@code
			 * true} if {@link #next} would return an element rather than throwing an exception.)
			 *
			 * @return {@code true} if the iteration has more elements
			 */
			@Override
			public boolean hasNext()
			{
				return this.index != end;
			}


			/**
			 * Returns the next element in the iteration.
			 *
			 * @return the next element in the iteration
			 */
			@Override
			public Vector next()
			{
				int modX = this.index % x;
				int divX = this.index / x;
				int modY = divX % y;
				int divY = divX / y;

				++this.index;

				this.mutable.setX(minX + modX);
				this.mutable.setY(minY + modY);
				this.mutable.setZ(minZ + divY);

				return this.mutable.clone();
			}


			/**
			 * Mutable vector that is used for internal calculations.
			 */
			private final Vector mutable = new Vector();

			/**
			 * Index of current element.
			 */
			private int index;
		};
	}


	/**
	 * This method returns String data from persistence object.
	 * @param dragon Entity which contains persistent data container.
	 * @param key Searched key.
	 * @return Required key value.
	 */
	public static @Nullable String getPersistenceString(EnderDragon dragon, NamespacedKey key)
	{
		return dragon.getPersistentDataContainer().get(key, PersistentDataType.STRING);
	}


	/**
	 * This method generates NamespacedKey from plugin and key.
	 * @param plugin Plugin instance.
	 * @param key String value of key.
	 * @return NamespacedKey with required parameters.
	 */
	public static NamespacedKey getNamespaceKey(BentoBox plugin, String key)
	{
		return new NamespacedKey(plugin, key);
	}


	/**
	 * This method updates boss bar value for given entity.
	 * @param bossBar the boss bar
	 * @param entity the entity
	 */
	public static void updateBossBar(@Nullable BossBar bossBar, @Nullable Entity entity)
	{
		if (bossBar != null)
		{
			if (entity != null && entity.getType() == EntityType.ENDER_DRAGON)
			{
				EnderDragon dragon = (EnderDragon) entity;

				AttributeInstance maxHealth = dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH);

				if (maxHealth == null)
				{
					// No max health attribute.
					return;
				}

				// Update boss bar value.
				bossBar.setProgress(dragon.getHealth() / maxHealth.getValue());
			}
			else
			{
				// cannot find dragon.
				bossBar.removeAll();
				bossBar.setVisible(false);
			}
		}
	}


	/**
	 * This method sends all users in the world who is 15 blocks around given location a message with a given constant.
	 * @param world World where users are in.
	 * @param island Island on which user is located.
	 * @param constant String constant for message.
	 * @param variables Array of string that allows to set variables to the constant message.
	 */
	public static void sendMessageToUsers(World world, Island island, String constant, String... variables)
	{
		// Send message with a given constant to all island members who are in given world.
		island.getPlayersOnIsland().stream().
			filter(player -> player.getWorld() == world).
			forEach(player ->
			{
				User user = User.getInstance(player);

				if (user != null)
				{
					user.sendMessage(user.getTranslation(Constants.CONVERSATIONS + "prefix") +
						user.getTranslation(constant, variables));
				}
			});
	}
}
