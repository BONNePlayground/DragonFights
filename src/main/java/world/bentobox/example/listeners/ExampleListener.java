package world.bentobox.example.listeners;


import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import world.bentobox.bentobox.api.user.User;
import world.bentobox.example.ExampleAddon;


/**
 * Example Listener for Example Addon.
 */
public class ExampleListener implements Listener
{
	/**
	 * Local variable that stores ExampleAddon.
	 * It is useful to store it so we could access some addon internal methods.
	 */
	private ExampleAddon addon;


	/**
	 * Simple Constructor that inits example addon variable.
	 * @param addon Example addon
	 */
	public ExampleListener(ExampleAddon addon)
	{
		this.addon = addon;
	}


	/**
	 * Creating Event listeners for BentoBox is the same as in any other plugin.
	 *
	 * @param event PlayerJoinEvent object
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		// BentoBox uses its own Player concept called User. And it is easy to create one.
		// We need User object for sending correct message to it, that use string from
		// language file.

		User user = User.getInstance(event.getPlayer());

		if (user.isOp())
		{
			user.sendMessage("example.messages.admin-join");

			// We can send admin message about VaultHook status each time he joins server.

			if (this.addon.getVaulHook() != null)
			{
				user.sendMessage("example.messages.vault-hook-enabled");
			}
			else
			{
				user.sendMessage("example.warnings.vault-hook-disabled");
			}

			// We can send admin message about Level status each time he joins server.

			if (this.addon.getLevelAddon() != null)
			{
				user.sendMessage("example.messages.level-addon-found");
			}
			else
			{
				user.sendMessage("example.warnings.level-addon-not-found");
			}
		}

		// We can add parameters to messages
		user.sendMessage("example.messages.player-join", "[name]", user.getName());

		// We can get also Translation Strings.
		String balance = this.addon.getVaulHook() != null ?
			Double.toString(this.addon.getVaulHook().getBalance(user)) :
			user.getTranslation("example.translation.missing-vault");

		String level = this.addon.getLevelAddon() != null ?
			Long.toString(this.addon.getLevelAddon().getIslandLevel(user.getWorld(), user.getUniqueId())) :
			user.getTranslation("example.translation.missing-level");

		// And messages could have multiple variables.
		// All variables must be strings!
		user.sendMessage("example.messages.balance-and-level", "[balance]", balance, "[level]", level);
	}
}
