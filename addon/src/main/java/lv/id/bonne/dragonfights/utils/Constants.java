//
// Created by BONNe
// Copyright - 2020
//


package lv.id.bonne.dragonfights.utils;


import org.bukkit.World;


/**
 * Constants for translations.
 */
public class Constants
{
	/**
	 * Main link to translation string.
	 */
	public static final String ADDON = "dragon-fights.";

	/**
	 * Link to conversations block.
	 */
	public static final String CONVERSATIONS = Constants.ADDON + "conversations.";

	/**
	 * Link to messages block.
	 */
	public static final String MESSAGES = Constants.ADDON + "messages.";

	/**
	 * Link to errors block.
	 */
	public static final String ERRORS = Constants.ADDON + "errors.";

	// ---------------------------------------------------------------------
	// Section: Target Points
	// ---------------------------------------------------------------------

	/**
	 * Name of the dragon.
	 */
	public static final String DRAGON_NAME = Constants.ADDON + "dragon-name";

	/**
	 * Text that appears above BossBar when countdown timer is active.
	 */
	public static String TIME_TILL_SPAWN = Constants.ADDON + "count-down-timer";

	// ---------------------------------------------------------------------
	// Section: Parameters
	// ---------------------------------------------------------------------

	/**
	 * Parameter for island name
	 */
	public static final String PARAMETER_ISLAND = "[island]";

	/**
	 * Parameter for owner name
	 */
	public static final String PARAMETER_OWNER = "[owner]";

	/**
	 * Parameter for owner number
	 */
	public static final String PARAMETER_NUMBER = "[number]";
}
