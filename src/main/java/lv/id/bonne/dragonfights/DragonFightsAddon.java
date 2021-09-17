package lv.id.bonne.dragonfights;


import org.bukkit.Bukkit;
import org.bukkit.World;
import java.util.Optional;


import io.github.iltotore.customentity.CustomRegistry;
import lv.id.bonne.dragonfights.config.Settings;
import lv.id.bonne.dragonfights.entity.BentoBoxEnderDragonRoot;
import lv.id.bonne.dragonfights.entity.CustomEntityAPI;
import lv.id.bonne.dragonfights.listeners.ActivationListener;
import lv.id.bonne.dragonfights.listeners.JoinLeaveListener;
import lv.id.bonne.dragonfights.managers.DragonFightManager;
import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.hooks.VaultHook;
import world.bentobox.level.Level;


/**
 * This is main Addon class. It allows to load it into BentoBox hierarchy.
 */
public class DragonFightsAddon extends Addon
{
	// ---------------------------------------------------------------------
	// Section: Methods
	// ---------------------------------------------------------------------


	/**
	 * Executes code when loading the addon. This is called before {@link #onEnable()}.
	 * This <b>must</b> be used to setup configuration, worlds and commands.
	 */
	@Override
	public void onLoad()
	{
		// Registration must happen regardless of addon enabling status.
		CustomRegistry registry = CustomEntityAPI.getAPI().getRegistry();
		registry.register(new BentoBoxEnderDragonRoot());

		super.onLoad();
		this.loadConfig();
	}


	/**
	 * Executes code when enabling the addon. This is called after {@link #onLoad()}.
	 * <br/> Note that commands and worlds registration <b>must</b> be done in {@link
	 * #onLoad()}, if need be. Failure to do so <b>will</b> result in issues such as
	 * tab-completion not working for commands.
	 */
	@Override
	public void onEnable()
	{
		// Check if it is enabled - it might be loaded, but not enabled.

		if (this.getPlugin() == null || !this.getPlugin().isEnabled())
		{
			Bukkit.getLogger().severe("BentoBox is not available or disabled!");
			this.setState(State.DISABLED);
			return;
		}

		// Check if addon is not disabled before.

		if (this.getState().equals(State.DISABLED))
		{
			Bukkit.getLogger().severe("DragonFightsAddon is not available or disabled!");
			return;
		}

		this.addonManager = new DragonFightManager(this);

		// If your addon wants to hook into other GameModes, f.e. use flags, then you should
		// hook these flags into each GameMode.

		// Fortunately BentoBox provides ability to a list of all loaded GameModes.

		this.getPlugin().getAddonsManager().getGameModeAddons().stream().
			filter(gameMode -> !this.settings.getDisabledGameModes().contains(gameMode.getDescription().getName())).
			forEach(this::hookIntoGameMode);

		if (this.hooked)
		{
			this.setupAddon();
		}
		else
		{
			this.logError("Dragon Fights could not hook into any GameMode.");
			this.setState(State.DISABLED);
		}
	}


	/**
	 * Load dragons when everything is loaded.
	 */
	@Override
	public void allLoaded()
	{
		super.allLoaded();
		this.addonManager.load();
	}


	/**
	 * This method hooks addon into GameMode.
	 * @param gameModeAddon GameMode addon in which this addon must be hooked.
	 */
	private void hookIntoGameMode(GameModeAddon gameModeAddon)
	{
		// Only enable if end islands are enabled.
		if (this.getPlugin().getIWM().isIslandEnd(gameModeAddon.getEndWorld()))
		{
			this.addonManager.addWorld(gameModeAddon.getOverWorld());
			// add end world too for easier contains check.
			this.addonManager.addWorld(gameModeAddon.getEndWorld());

			this.hooked = true;

			// Add Placeholders
			this.registerPlaceholders(gameModeAddon);
		}
	}


	/**
	 * Registers the placeholders
	 * @param addon GameMode addon where placeholders are added.
	 * @since 1.0.0
	 */
	private void registerPlaceholders(GameModeAddon addon)
	{
		final String addonName = this.getDescription().getName().toLowerCase();
		final World world = addon.getOverWorld();

		// Placeholder returns currently active count.
		this.getPlugin().getPlaceholdersManager().registerPlaceholder(addon,
			addonName + "_killed_dragon_count",
			user ->
			{
				Island island = this.getIslands().getIsland(world, user);

				if (island != null)
				{
					return String.valueOf(this.getAddonManager().getIslandData(island).getDragonsKilled());
				}
				else
				{
					// Return empty string as user do not have an island.
					return "0";
				}
			});

		// Placeholder returns maximal active generator count, that user can activate.
		this.getPlugin().getPlaceholdersManager().registerPlaceholder(addon,
			addonName + "_visited_killed_dragon_count",
			user ->
			{
				if (!addon.inWorld(user.getLocation()))
				{
					// Return empty string as user is not on the island.
					return "";
				}

				return this.getIslands().getIslandAt(user.getLocation()).
					map(island -> String.valueOf(this.getAddonManager().getIslandData(island).getDragonsKilled())).
					orElse("0");
			});
	}


	/**
	 * Sets up everything once the addon is hooked into Game Modes
	 */
	private void setupAddon()
	{
		// We can also search for certain addon where we want to integrate. I suggest to do it
		// once and keep it as variable to avoid addon searching when we want to access its data.

		this.levelAddon = this.getAddonByName("Level");

		// We could also send a message to console to inform if level addon was not found.

		if (!this.levelAddon.isPresent())
		{
			this.logWarning("Level add-on not found by DragonFights Addon!");
		}

		// BentoBox does not manage money, but it provides VaultHook that does it.
		// I suggest to do the same trick as with Level addon. Create local variable and
		// store if Vault is present there.

		this.vaultHook = this.getPlugin().getVault();

		// Even if Vault is installed, it does not mean that economy can be used. It is
		// necessary to check it via VaultHook#hook() method.

		if (!this.vaultHook.isPresent())
		{
			this.logWarning("Vault plugin not found by DragonFights Addon!");
		}

		// Registering addon listeners
		this.registerListener(new ActivationListener(this));
		this.registerListener(new JoinLeaveListener(this));
	}


	/**
	 * Executes code when reloading the addon.
	 */
	@Override
	public void onReload()
	{
		super.onReload();
		this.loadConfig();
	}


	/**
	 * Executes code when disabling the addon.
	 */
	@Override
	public void onDisable()
	{
		// onDisable we would like to save existing settings. It is not necessary for
		// addons that does not have interface for settings editing!
		if (this.addonManager != null)
		{
			this.addonManager.save();
		}

		if (this.settings != null)
		{
			new Config<>(this, Settings.class).saveConfigObject(this.settings);
		}
	}


	/**
	 * This method loads config.
	 */
	private void loadConfig()
	{
		// Save default config.yml
		this.saveDefaultConfig();
		// Load Addon Settings
		this.settings = new Config<>(this, Settings.class).loadConfigObject();

		if (this.settings == null)
		{
			// If we failed to load Settings then we should not enable addon.
			// We can log error and set state to DISABLED.

			this.logError("DragonFightsAddon settings could not load! Addon disabled.");
			this.setState(State.DISABLED);
		}
	}


	// ---------------------------------------------------------------------
	// Section: Getters
	// ---------------------------------------------------------------------


	/**
	 * Gets settings.
	 *
	 * @return the settings
	 */
	public Settings getSettings()
	{
		return settings;
	}


	/**
	 * Gets manager.
	 *
	 * @return the manager
	 */
	public DragonFightManager getAddonManager()
	{
		return addonManager;
	}


	/**
	 * This getter will allow to access to Level addon. It is written so that it could
	 * return null, if Level is not present.
	 * @return {@code Level} addon if it is present, {@code null} otherwise.
	 */
	public Level getLevelAddon()
	{
		return (Level) this.levelAddon.orElse(null);
	}


	/**
	 * This getter will allow to access to VaultHook. It is written so that it could
	 * return null, if Vault is not present.
	 * @return {@code VaultHook} if it is present, {@code null} otherwise.
	 */
	public VaultHook getVaultHook()
	{
		return this.vaultHook.orElse(null);
	}


	// ---------------------------------------------------------------------
	// Section: Variables
	// ---------------------------------------------------------------------

	/**
	 * Settings object contains
	 */
	private Settings settings;

	/**
	 * Indicates if addon managed to hook into any gamemode.
	 */
	private boolean hooked;

	/**
	 * Stores instance of addon manager.
	 */
	private DragonFightManager addonManager;

	/**
	 * Local variable that stores if level addon is present or not.
	 */
	private Optional<Addon> levelAddon;

	/**
	 * Local variable that stores if vaultHook is present.
	 */
	private Optional<VaultHook> vaultHook;
}
