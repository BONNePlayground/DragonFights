//
// Created by BONNe
// Copyright - 2020
//


package lv.id.bonne.dragonfights.entity;


import org.bukkit.Bukkit;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import io.github.iltotore.customentity.NMSHandler;
import io.github.iltotore.customentity.util.ServerVersion;


/**
 * CustomEntityLoading API.
 */
public class CustomEntityAPI
{
	/**
	 * Get the {@link NMSHandler} instance.
	 *
	 * @return the {@link NMSHandler} instance for the server's version
	 */
	public static NMSHandler getAPI()
	{
		if (api == null)
		{
			ServerVersion version = ServerVersion.fromServer(Bukkit.getServer());
			Bukkit.getLogger().info("[CustomEntityAPI] Loading version " + version.getNMSVersion());
			api = versions.get(version).get();
		}
		return api;
	}


	/**
	 * Api Handlers.
	 */
	private static NMSHandler api;

	/**
	 * Map that contains all versions/handlers.
	 */
	private static Map<ServerVersion, Supplier<NMSHandler>> versions = new HashMap<>();

	/**
	 * Populate map vit all supported versions paired with their handlers.
	 */
	static
	{
		versions.put(ServerVersion.v1_19_1, lv.id.bonne.dragonfights.v1_19_R1.NMSHandler::new);
		versions.put(ServerVersion.v1_19_2, lv.id.bonne.dragonfights.v1_19_R2.NMSHandler::new);
		versions.put(ServerVersion.v1_19_3, lv.id.bonne.dragonfights.v1_19_R3.NMSHandler::new);
		versions.put(ServerVersion.v1_20, lv.id.bonne.dragonfights.v1_20_R1.NMSHandler::new);
		versions.put(ServerVersion.v1_20_2, lv.id.bonne.dragonfights.v1_20_R2.NMSHandler::new);
	}
}