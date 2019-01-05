package hn.core.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import hn.core.Core;
import hn.core.command.ACommand;
import hn.core.util.Utils;

public class WarpCommand extends ACommand {

	public WarpCommand()
	{
		super("warp", "Warps the player to a warp", null, new String[] {});
	}

	@Override
	protected void callCommand(Player player, String[] args)
	{
		if (args.length != 1)
		{
			player.sendMessage(ChatColor.RED + "Error: Command usage is /warp <name>");
			return;
		}
		if (Core.getInstance().config.getString("warps." + args[0].toLowerCase() + ".world") == null)
		{
			player.sendMessage(ChatColor.RED + "The warp " + args[0].toLowerCase() + " does not exist!");
			return;
		} else
		{
			try
			{
				Core.getInstance().back.replace(player.getUniqueId(), player.getLocation());
				player.teleport(new Location(Bukkit.getWorld(Core.getInstance().config.getString("warps." + args[0].toLowerCase() + ".world")),
						Core.getInstance().config.getDouble("warps." + args[0].toLowerCase() + ".x"), Core.getInstance().config.getDouble("warps." + args[0].toLowerCase() + ".y"),
						Core.getInstance().config.getDouble("warps." + args[0].toLowerCase() + ".z"), (float) Core.getInstance().config.getDouble("warps." + args[0].toLowerCase() + ".yaw"),
						(float) Core.getInstance().config.getDouble("warps." + args[0].toLowerCase() + ".pitch")));
				player.sendMessage(Utils.serverName + ">> You have been warped to the " + args[0].toLowerCase() + " warp!");
			} catch (Exception e)
			{
				player.sendMessage(ChatColor.RED + "Something went wrong! Get your server admin to verify the warp settings!");
				return;
			}
		}
	}

}
