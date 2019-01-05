package hn.core.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import hn.core.Core;
import hn.core.command.ACommand;
import hn.core.util.Utils;

public class SetWarpCommand extends ACommand {

	public SetWarpCommand()
	{
		super("setwarp", "Sets the warp-point of a warp", "hn.manage", new String[] {});
	}

	@Override
	protected void callCommand(Player player, String[] args)
	{
		if (args.length != 1)
		{
			player.sendMessage(ChatColor.RED + "Error: Command usage is /setwarp <name>");
			return;
		}
		
		if (Core.getInstance().config.getString("warps." + args[0].toLowerCase() + ".world") != null)
		{
			player.sendMessage(ChatColor.RED + "The warp " + args[0].toLowerCase() + " already exists!");
			return;
		}
		
		
		Core.getInstance().config.set("warps." + args[0].toLowerCase() + ".world", player.getWorld().getName());
		Core.getInstance().config.set("warps." + args[0].toLowerCase() + ".x", player.getLocation().getBlockX() + 0.5);
		Core.getInstance().config.set("warps." + args[0].toLowerCase() + ".y", player.getLocation().getBlockY());
		Core.getInstance().config.set("warps." + args[0].toLowerCase() + ".z", player.getLocation().getBlockZ() + 0.5);
		Core.getInstance().config.set("warps." + args[0].toLowerCase() + ".pitch", player.getLocation().getPitch());
		Core.getInstance().config.set("warps." + args[0].toLowerCase() + ".yaw", player.getLocation().getYaw());
		Core.getInstance().saveConfig();
		
		player.sendMessage(Utils.serverName + ">> The warp " + args[0].toLowerCase() + " is set to your location!");
	}

}
