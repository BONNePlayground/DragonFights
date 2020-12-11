package world.bentobox.example;


import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.Optional;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.bentobox.api.flags.Flag;
import world.bentobox.bentobox.hooks.VaultHook;
import world.bentobox.bentobox.managers.RanksManager;
import world.bentobox.example.commands.ExampleCommand;
import world.bentobox.example.commands.admin.ExampleAdminCommand;
import world.bentobox.example.commands.island.ExamplePlayerCommand;
import world.bentobox.example.configs.Settings;
import world.bentobox.example.listeners.ExampleListener;
import world.bentobox.level.Level;


/**
 * This is main Addon class. It allows to load it into BentoBox hierarchy.
 */
public class ExampleAddon extends Addon
{
	// ---------------------------------------------------------------------
	// Section: Variables
	// ---------------------------------------------------------------------

	/**
	 * Settings object contains
	 */
	private Settings settings;

	/**
	 * Local variable that stores if level addon is present or not.
	 */
	private Optional<Addon> levelAddon;

	/**
	 * Local variable that stores if vaultHook is present.
	 */
	private Optional<VaultHook> vaultHook;

	// ---------------------------------------------------------------------
	// Section: Flags
	// ---------------------------------------------------------------------


	/**
	 * World flags allows to change some property for whole world.
	 *
	 * It can only be modified by administrators (permission or operator).
	 * This is usually an on/off setting.
	 *
	 * EXAMPLE_WORLD_FLAG should also be defined in language file under
	 * protection.flags section.
	 *
	 * By default setting is set to false.
	 */
	public final static Flag EXAMPLE_WORLD_FLAG =
		new Flag.Builder("EXAMPLE_WORLD_FLAG", Material.WHITE_BANNER).
			type(Flag.Type.WORLD_SETTING).
			defaultSetting(true).
			build();


	/**
	 * Settings flags allows to modifying parameters of the island.
	 *
	 * It can be modified by the players (island owner).
	 * This is usually an on/off setting.
	 *
	 * EXAMPLE_SETTINGS_FLAG should also be defined in language file under
	 * protection.flags section.
	 *
	 * By default setting is set to false.
	 */
	public final static Flag EXAMPLE_SETTINGS_FLAG =
		new Flag.Builder("EXAMPLE_SETTINGS_FLAG", Material.GREEN_BANNER).
			type(Flag.Type.SETTING).
			build();


	/**
	 * Permission flags allows to modifying access for players to certain actions.
	 *
	 * It can be modified by the players (island owner).
	 * It applies differently depending on the rank of the player who performs the action
	 * protected by the flag.
	 *
	 * EXAMPLE_PERMISSION_FLAG should also be defined in language file under
	 * protection.flags section.
	 *
	 * By default rank is set to RanksManager.MEMBER_RANK.
	 */
	public final static Flag EXAMPLE_PERMISSION_FLAG =
		new Flag.Builder("EXAMPLE_PERMISSION_FLAG", Material.ORANGE_BANNER).
			type(Flag.Type.PROTECTION).
			defaultRank(RanksManager.VISITOR_RANK).
			build();


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
		super.onLoad();

		// in most of addons, onLoad we want to store default configuration if it does not
		// exist and load it.

		// Storing default configuration is simple. But be aware, you need
		// @StoreAt(filename="config.yml", path="addons/Example") in header of your Config file.
		this.saveDefaultConfig();

		this.settings = new Config<>(this, Settings.class).loadConfigObject();

		if (this.settings == null)
		{
			// If we failed to load Settings then we should not enable addon.
			// We can log error and set state to DISABLED.

			this.logError("Example settings could not load! Addon disabled.");
			this.setState(State.DISABLED);
		}
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
			Bukkit.getLogger().severe("Example Addon is not available or disabled!");
			return;
		}

		// If your addon wants to hook into other GameModes, f.e. use flags, then you should
		// hook these flags into each GameMode.

		// Fortunately BentoBox provides ability to a list of all loaded GameModes.

		this.getPlugin().getAddonsManager().getGameModeAddons().forEach(gameModeAddon -> {
			// In Settings (and config) we define DisabledGameModes, list of GameModes where
			// current Addon should not work.
			// This is where we do not hook current addon into GameMode addon.

			if (!this.settings.getDisabledGameModes().contains(gameModeAddon.getDescription().getName()))
			{
				// Now we add GameModes to our Flags
				EXAMPLE_WORLD_FLAG.addGameModeAddon(gameModeAddon);
				EXAMPLE_SETTINGS_FLAG.addGameModeAddon(gameModeAddon);
				EXAMPLE_PERMISSION_FLAG.addGameModeAddon(gameModeAddon);

				// Each GameMode could have Player Command and Admin Command and we could
				// want to integrate our Example Command into these commands.
				// It provides ability to call command with GameMode command f.e. "/island example"

				// Of course we should check if these commands exists, as it is possible to
				// create GameMode without them.

				gameModeAddon.getPlayerCommand().ifPresent(
					playerCommand -> new ExamplePlayerCommand(this, playerCommand));

				gameModeAddon.getAdminCommand().ifPresent(
					adminCommand -> new ExampleAdminCommand(this, adminCommand));
			}
		});

		// After we added all GameModes into flags, we need to register these flags into BentoBox.

		this.registerFlag(EXAMPLE_WORLD_FLAG);
		this.registerFlag(EXAMPLE_SETTINGS_FLAG);
		this.registerFlag(EXAMPLE_PERMISSION_FLAG);


		// We could also like to create separate command that will be independent from any GameMode.
		// And it is as simple as it looks.
		new ExampleCommand(this);

		// We can also search for certain addon where we want to integrate. I suggest to do it
		// once and keep it as variable to avoid addon searching when we want to access its data.

		this.levelAddon = this.getAddonByName("Level");

		// We could also send a message to console to inform if level addon was not found.

		if (!this.levelAddon.isPresent())
		{
			this.logWarning("Level add-on not found by Example Addon!");
		}


		// BentoBox does not manage money, but it provides VaultHook that does it.
		// I suggest to do the same trick as with Level addon. Create local variable and
		// store if Vault is present there.

		this.vaultHook = this.getPlugin().getVault();

		// Even if Vault is installed, it does not mean that economy can be used. It is
		// necessary to check it via VaultHook#hook() method.

		if (!this.vaultHook.isPresent() || !this.vaultHook.get().hook())
		{
			this.logWarning("Economy plugin not found by Example Addon!");
		}


		// Registering Listeners also is easy. You can do it from Addon class, without
		// necessarily to register it into Bukkit.pluginManger.
		// Registering it trough addon class also provides ability to relaod listener
		// with BentoBox reload command.
		this.registerListener(new ExampleListener(this));

		// Register Request Handlers
		//this.registerRequestHandler(EXAMPLE_REQUEST_HANDLER);
	}


	/**
	 * Executes code when reloading the addon.
	 */
	@Override
	public void onReload()
	{
		super.onReload();

		// onReload most of addons just need to reload configuration.
		// If flags, listeners and handlers were set up correctly via Addon.class then
		// they will be reloaded automatically.

		this.settings = new Config<>(this, Settings.class).loadConfigObject();

		if (this.settings == null)
		{
			// If we failed to load Settings then we should not enable addon.
			// We can log error and set state to DISABLED.

			this.logError("Example settings could not load! Addon disabled.");
			this.setState(State.DISABLED);
		}
	}


	/**
	 * Executes code when disabling the addon.
	 */
	@Override
	public void onDisable()
	{
		// onDisable we would like to save exisitng settings. It is not necessary for
		// addons that does not have interface for settings editing!

		if (this.settings != null)
		{
			new Config<>(this, Settings.class).saveConfigObject(this.settings);
		}
	}


	// ---------------------------------------------------------------------
	// Section: Getters
	// ---------------------------------------------------------------------


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
	public VaultHook getVaulHook()
	{
		return this.vaultHook.orElse(null);
	}
}
