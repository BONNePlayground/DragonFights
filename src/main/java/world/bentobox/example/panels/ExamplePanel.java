package world.bentobox.example.panels;


import org.bukkit.Material;

import world.bentobox.bentobox.api.panels.PanelItem;
import world.bentobox.bentobox.api.panels.builders.PanelBuilder;
import world.bentobox.bentobox.api.panels.builders.PanelItemBuilder;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.example.ExampleAddon;


/**
 * This class shows how to set up easy panel by using BentoBox PanelBuilder API
 */
public class ExamplePanel
{
	// ---------------------------------------------------------------------
	// Section: Variables
	// ---------------------------------------------------------------------

	/**
	 * This variable allows to access addon object.
	 */
	private ExampleAddon addon;

	/**
	 * This variable holds user who opens panel. Without it panel cannot be opened.
	 */
	private User user;

	// ---------------------------------------------------------------------
	// Section: Internal Constructor
	// ---------------------------------------------------------------------


	/**
	 * This is internal constructor. It is used internally in current class to avoid
	 * creating objects everywhere.
	 * @param addon ExampleAddon object.
	 */
	private ExamplePanel(ExampleAddon addon, User user)
	{
		this.addon = addon;
		this.user = user;
	}


	/**
	 * This method is used to open UserPanel outside this class. It will be much easier
	 * to open panel with single method call then initializing new object.
	 * @param addon Example Addon object
	 * @param user User who opens panel
	 */
	public static void openPanel(ExampleAddon addon, User user)
	{
		new ExamplePanel(addon, user).build();
	}


	// ---------------------------------------------------------------------
	// Section: Methods
	// ---------------------------------------------------------------------


	/**
	 * Build method manages current panel opening. It uses BentoBox PanelAPI that is easy
	 * to use and users can get nice panels.
	 */
	private void build()
	{
		// PanelBuilder is a BentoBox API that provides ability to easy create Panels.
		PanelBuilder panelBuilder = new PanelBuilder().
			// Each panel must have panel name.
			name("Example Addon User Panel").
			// Each panel must have target user who opens it.
			user(this.user);

		// We can allow to assing next button automatically by PanelBuilder.
		panelBuilder.item(this.createMessageItem());

		// Or set manually with slot number
		// Be aware that numbering starts with 0!
		panelBuilder.item(8, this.createPlayerHeadItem());

		// We can add UP to 54 elements in single GUI.
		panelBuilder.item(22, this.createEmptyItem());

		// At the end we just call build method that creates and opens panel.
		panelBuilder.build();
	}


	/**
	 * This method creates Simple Button that will send message to player after he clicks
	 * on it.
	 * @return PanelItem object.
	 */
	private PanelItem createMessageItem()
	{
		PanelItemBuilder builder = new PanelItemBuilder();

		// To get button name in different languages we can use user object to get correct
		// translation string.
		builder.name(this.user.getTranslation("example.user-button.name"));

		// We can modify PanelItem icon.
		builder.icon(Material.COMMAND_BLOCK);

		// And even add lore to it.
		// We can do the same as in button name, to change its lore in lang file without
		// changing it in code.
		builder.description(this.user.getTranslation("example.user-button.description"));

		// Click handler allows to define action what will happen when player clicks on
		// this PanelItem.
		builder.clickHandler((panel, user, clickType, slot) -> {

			user.sendMessage(this.user.getTranslation("example.user-button.action"));

			// returning true mean, that click will be canceled and player will not be able
			// to take item out of GUI.
			// If false, then users can take this item.
			return true;
		});

		// At the end we build our button.
		return builder.build();
	}


	/**
	 * This method creates button that will do nothing when player clicks on it.
	 * @return PanelItem object.
	 */
	private PanelItem createEmptyItem()
	{
		// We can easily define empty button, without any data.
		// It will use AIR icon without a name.
		return new PanelItemBuilder().build();
	}


	/**
	 * This method creates button that will do nothing when player clicks on it, but it
	 * will use player head as icon.
	 * @return PanelItem object.
	 */
	private PanelItem createPlayerHeadItem()
	{
		PanelItemBuilder builder = new PanelItemBuilder();

		// With BentoBox API you can easy create PlayerHead button. You can use any
		// existing player name.
		builder.icon(this.user.getName());

		// At the end we build our button.
		return builder.build();
	}
}
