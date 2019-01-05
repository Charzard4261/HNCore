package hn.core.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import hn.core.Core;
import hn.core.command.ACommand;
import hn.core.util.Utils;

public class HomesCommand extends ACommand {

	public HomesCommand()
	{
		super("homes", "Lists all of a player's homes", null, new String[] {});
	}

	@Override
	protected void callCommand(Player player, String[] args)
	{
		if (Core.getInstance().playerdata.getConfigurationSection(player.getUniqueId() + ".homes") == null
				|| Core.getInstance().playerdata.getConfigurationSection(player.getUniqueId() + ".homes").getKeys(false) == null)
		{
			player.sendMessage(ChatColor.RED + "You haven't set any homes!");
			return;
		}

		String homes = "";
		for (String s : Core.getInstance().playerdata.getConfigurationSection(player.getUniqueId() + ".homes").getKeys(false))
			homes = homes + ChatColor.GREEN + s + ChatColor.WHITE + ", ";

		player.sendMessage(Utils.serverName + ">> Your homes are " + homes.substring(0, homes.length() - 4) + ChatColor.WHITE + ".");
	}

}
