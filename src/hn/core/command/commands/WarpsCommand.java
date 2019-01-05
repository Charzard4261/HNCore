package hn.core.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import hn.core.Core;
import hn.core.command.ACommand;
import hn.core.util.Utils;

public class WarpsCommand extends ACommand {

	public WarpsCommand()
	{
		super("warps", "Lists all warps", null, new String[] {});
	}

	@Override
	protected void callCommand(Player player, String[] args)
	{
		if (Core.getInstance().config.getConfigurationSection("warps").getKeys(false) == null)
		{
			player.sendMessage(ChatColor.RED + "No warps have been set!");
			return;
		}

		String warps = "";
		for (String s : Core.getInstance().config.getConfigurationSection("warps").getKeys(false))
			warps = warps + ChatColor.GREEN + s + ChatColor.WHITE + ", ";

		player.sendMessage(Utils.serverName + ">> The warps are " + warps.substring(0, warps.length() - 4) + ChatColor.WHITE + ".");
	}

}
