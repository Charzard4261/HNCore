package hn.core.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import hn.core.Core;
import hn.core.command.ACommand;
import hn.core.util.Utils;

public class DeleteHomeCommand extends ACommand {

	public DeleteHomeCommand()
	{
		super("delhome", "Deletes a player's home", null, new String[] {});
	}

	@Override
	protected void callCommand(Player player, String[] args)
	{
		String home = "home";

		if (args.length >= 1)
			home = args[0];

		if (Core.getInstance().playerdata.getString(player.getUniqueId() + ".homes." + home + ".world") == null)
		{
			player.sendMessage(ChatColor.RED + "The home " + home + " is not set!");
			return;
		} else
		{
			Core.getInstance().playerdata.set(player.getUniqueId() + ".homes." + home, null);
			player.sendMessage(Utils.serverName + ">> Your home " + home + " was deleted!");
		}
	}

}
