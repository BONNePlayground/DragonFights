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
	 * Is start on first join boolean.
	 *
	 * @return the boolean
	 */
	public boolean isStartOnFirstJoin()
	{
		return startOnFirstJoin;
	}


	/**
	 * Sets start on first join.
	 *
	 * @param startOnFirstJoin the start on first join
	 */
	public void setStartOnFirstJoin(boolean startOnFirstJoin)
	{
		this.startOnFirstJoin = startOnFirstJoin;
	}


	/**
	 * Gets tower count.
	 *
	 * @return the tower count
	 */
	public int getTowerCount()
	{
		return towerCount;
	}


	/**
	 * Sets tower count.
	 *
	 * @param towerCount the tower count
	 */
	public void setTowerCount(int towerCount)
	{
		this.towerCount = towerCount;
	}


	/**
	 * Gets tower distance.
	 *
	 * @return the tower distance
	 */
	public int getTowerDistance()
	{
		return towerDistance;
	}


	/**
	 * Sets tower distance.
	 *
	 * @param towerDistance the tower distance
	 */
	public void setTowerDistance(int towerDistance)
	{
		this.towerDistance = towerDistance;
	}


	/**
	 * Gets min tower height.
	 *
	 * @return the min tower height
	 */
	public int getMinTowerHeight()
	{
		return minTowerHeight;
	}


	/**
	 * Sets min tower height.
	 *
	 * @param minTowerHeight the min tower height
	 */
	public void setMinTowerHeight(int minTowerHeight)
	{
		this.minTowerHeight = minTowerHeight;
	}


	/**
	 * Gets max tower height.
	 *
	 * @return the max tower height
	 */
	public int getMaxTowerHeight()
	{
		return maxTowerHeight;
	}


	/**
	 * Sets max tower height.
	 *
	 * @param maxTowerHeight the max tower height
	 */
	public void setMaxTowerHeight(int maxTowerHeight)
	{
		this.maxTowerHeight = maxTowerHeight;
	}


	/**
	 * Gets number of protected towers.
	 *
	 * @return the number of protected towers
	 */
	public int getNumberOfProtectedTowers()
	{
		return numberOfProtectedTowers;
	}


	/**
	 * Sets number of protected towers.
	 *
	 * @param numberOfProtectedTowers the number of protected towers
	 */
	public void setNumberOfProtectedTowers(int numberOfProtectedTowers)
	{
		this.numberOfProtectedTowers = numberOfProtectedTowers;
	}


	/**
	 * Is play music boolean.
	 *
	 * @return the boolean
	 */
	public boolean isPlayMusic()
	{
		return playMusic;
	}


	/**
	 * Sets play music.
	 *
	 * @param playMusic the play music
	 */
	public void setPlayMusic(boolean playMusic)
	{
		this.playMusic = playMusic;
	}


	/**
	 * Is enable fog boolean.
	 *
	 * @return the boolean
	 */
	public boolean isEnableFog()
	{
		return enableFog;
	}


	/**
	 * Sets enable fog.
	 *
	 * @param enableFog the enable fog
	 */
	public void setEnableFog(boolean enableFog)
	{
		this.enableFog = enableFog;
	}


	/**
	 * Gets battle seed.
	 *
	 * @return the battle seed
	 */
	public long getBattleSeed()
	{
		return battleSeed;
	}


	/**
	 * Sets battle seed.
	 *
	 * @param battleSeed the battle seed
	 */
	public void setBattleSeed(long battleSeed)
	{
		this.battleSeed = battleSeed;
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

	@ConfigComment("This option allows to enable dragon fight when player joins the end first time.")
	@ConfigComment("On first join it will start dragon summoning sequence.")
	@ConfigEntry(path = "battle.start-on-join")
	private boolean startOnFirstJoin;

	@ConfigComment("Number of towers generated for the battle.")
	@ConfigComment("Default value is 12.")
	@ConfigEntry(path = "battle.tower-count")
	private int towerCount = 12;

	@ConfigComment("Distance from portal till the towers.")
	@ConfigComment("Default value is 40.")
	@ConfigEntry(path = "battle.tower-distance")
	private int towerDistance = 40;

	@ConfigComment("Minimal height for the tower.")
	@ConfigComment("Default value is 120.")
	@ConfigEntry(path = "battle.tower-min-height")
	private int minTowerHeight = 120;

	@ConfigComment("Maximal height for the tower.")
	@ConfigComment("Default value is 150.")
	@ConfigEntry(path = "battle.tower-max-height")
	private int maxTowerHeight = 150;

	@ConfigComment("Number of protected towers.")
	@ConfigComment("Default value is 2.")
	@ConfigEntry(path = "battle.protected-towers")
	private int numberOfProtectedTowers = 2;

	@ConfigComment("Play the battle music.")
	@ConfigComment("Default value is true.")
	@ConfigEntry(path = "battle.play-music")
	private boolean playMusic = true;

	@ConfigComment("Set the battle fog.")
	@ConfigComment("Default value is false.")
	@ConfigEntry(path = "battle.enable-fog")
	private boolean enableFog;

	@ConfigComment("Battle Seed is a semi-random number that generates towers in the end.")
	@ConfigComment("This allows to give equal arena for each player.")
	@ConfigEntry(path = "battle.battle-seed")
	private long battleSeed = 0;

	@ConfigComment("")
	@ConfigComment("This list stores GameModes in which DragonFights addon should not work.")
	@ConfigComment("To disable addon it is necessary to write its name in new line that starts with -. Example:")
	@ConfigComment("disabled-gamemodes:")
	@ConfigComment(" - BSkyBlock")
	@ConfigEntry(path = "disabled-gamemodes")
	private Set<String> disabledGameModes = new HashSet<>();
}
