package lv.id.bonne.dragonfights.config;


import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import java.util.HashSet;
import java.util.Set;

import world.bentobox.bentobox.api.configuration.ConfigComment;
import world.bentobox.bentobox.api.configuration.ConfigEntry;
import world.bentobox.bentobox.api.configuration.ConfigObject;
import world.bentobox.bentobox.api.configuration.StoreAt;


/**
 * Settings that implements ConfigObject is powerful and dynamic Config Objects that does not need custom parsing. If it
 * is correctly loaded, all its values will be available.
 * <p>
 * Without Getter and Setter this class will not work.
 * <p>
 * To specify location for config object to be stored, you should use @StoreAt(filename="{config file name}",
 * path="{Path to your addon}") To save comments in config file you should use @ConfigComment("{message}") that adds any
 * message you want to be in file.
 */
@StoreAt(filename="config.yml", path="addons/DragonFights")
@ConfigComment("DragonFights Configuration [version]")
@ConfigComment("This config file is dynamic and saved when the server is shutdown.")
@ConfigComment("")
public class Settings implements ConfigObject
{
	// ---------------------------------------------------------------------
	// Section: Getters and Setters
	// ---------------------------------------------------------------------


	/**
	 * This method returns the disabledGameModes value.
	 * @return the value of disabledGameModes.
	 */
	public Set<String> getDisabledGameModes()
	{
		return disabledGameModes;
	}


	/**
	 * This method sets the disabledGameModes value.
	 * @param disabledGameModes the disabledGameModes new value.
	 *
	 */
	public void setDisabledGameModes(Set<String> disabledGameModes)
	{
		this.disabledGameModes = disabledGameModes;
	}


	/**
	 * Gets boss bar colour.
	 *
	 * @return the boss bar colour
	 */
	public BarColor getBossBarColour()
	{
		return bossBarColour;
	}


	/**
	 * Sets boss bar colour.
	 *
	 * @param bossBarColour the boss bar colour
	 */
	public void setBossBarColour(BarColor bossBarColour)
	{
		this.bossBarColour = bossBarColour;
	}


	/**
	 * Gets boss bar style.
	 *
	 * @return the boss bar style
	 */
	public BarStyle getBossBarStyle()
	{
		return bossBarStyle;
	}


	/**
	 * Sets boss bar style.
	 *
	 * @param bossBarStyle the boss bar style
	 */
	public void setBossBarStyle(BarStyle bossBarStyle)
	{
		this.bossBarStyle = bossBarStyle;
	}


	/**
	 * Gets warm up timer.
	 *
	 * @return the warm up timer
	 */
	public int getWarmUpTimer()
	{
		return warmUpTimer;
	}


	/**
	 * Sets warm up timer.
	 *
	 * @param warmUpTimer the warm up timer
	 */
	public void setWarmUpTimer(int warmUpTimer)
	{
		this.warmUpTimer = warmUpTimer;
	}


	/**
	 * Gets fly points.
	 *
	 * @return the fly points
	 */
	public int getFlyPoints()
	{
		return flyPoints;
	}


	/**
	 * Sets point distance.
	 *
	 * @param pointDistance the point distance
	 */
	public void setPointDistance(int pointDistance)
	{
		this.pointDistance = pointDistance;
	}


	/**
	 * Sets fly points.
	 *
	 * @param flyPoints the fly points
	 */
	public void setFlyPoints(int flyPoints)
	{
		this.flyPoints = flyPoints;
	}


	/**
	 * Gets min altitude.
	 *
	 * @return the min altitude
	 */
	public int getMinAltitude()
	{
		return minAltitude;
	}


	/**
	 * Sets min altitude.
	 *
	 * @param minAltitude the min altitude
	 */
	public void setMinAltitude(int minAltitude)
	{
		this.minAltitude = minAltitude;
	}


	/**
	 * Gets max altitude.
	 *
	 * @return the max altitude
	 */
	public int getMaxAltitude()
	{
		return maxAltitude;
	}


	/**
	 * Sets max altitude.
	 *
	 * @param maxAltitude the max altitude
	 */
	public void setMaxAltitude(int maxAltitude)
	{
		this.maxAltitude = maxAltitude;
	}


	/**
	 * Gets point distance.
	 *
	 * @return the point distance
	 */
	public int getPointDistance()
	{
		return pointDistance;
	}


	// ---------------------------------------------------------------------
	// Section: Variables
	// ---------------------------------------------------------------------


	@ConfigComment("")
	@ConfigComment("Allows to change default colour for boss bar.")
	@ConfigComment("Supports values from: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/boss/BarColor.html")
	@ConfigEntry(path = "boss-bar.colour")
	private BarColor bossBarColour = BarColor.GREEN;

	@ConfigComment("Allows to change default style for the boss bar.")
	@ConfigComment("Supports values from: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/boss/BarStyle.html")
	@ConfigEntry(path = "boss-bar.style")
	private BarStyle bossBarStyle = BarStyle.SOLID;

	@ConfigComment("Setting a number of seconds before dragon will be summoned.")
	@ConfigComment("0 and below will summon dragon instantly.")
	@ConfigEntry(path = "boss-bar.counter")
	private int warmUpTimer = 5;

	@ConfigComment("Number of path points for dragon to fly towards.")
	@ConfigComment("These fly points are on a circle around the exit portal position with `points-distance` from it.")
	@ConfigComment("Default value is 8.")
	@ConfigEntry(path = "boss.fly-points")
	private int flyPoints = 8;

	@ConfigComment("Distance between the exit portal position and fly point position.")
	@ConfigComment("Larger distance means that dragon will need to fly longer till it reach it.")
	@ConfigComment("Default value is 40.")
	@ConfigEntry(path = "boss.point-distance")
	private int pointDistance = 40;


	@ConfigComment("Minimal altitude for dragon to fly.")
	@ConfigComment("Allows to change how low dragon can fly by Y axis.")
	@ConfigComment("It is recommended to set it at the same height as island height.")
	@ConfigEntry(path = "boss.min-altitude")
	private int minAltitude = 120;

	@ConfigComment("Maximal altitude for dragon to fly.")
	@ConfigComment("Allows to change how high dragon can fly by Y axis.")
	@ConfigEntry(path = "boss.max-altitude")
	private int maxAltitude = 150;


	@ConfigComment("")
	@ConfigComment("This list stores GameModes in which DragonFights addon should not work.")
	@ConfigComment("To disable addon it is necessary to write its name in new line that starts with -. Example:")
	@ConfigComment("disabled-gamemodes:")
	@ConfigComment(" - BSkyBlock")
	@ConfigEntry(path = "disabled-gamemodes")
	private Set<String> disabledGameModes = new HashSet<>();
}
