package hn.core.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import hn.core.Core;
import hn.core.command.ACommand;

public class SpawnCommand extends ACommand {

	public SpawnCommand()
	{
		super("spawn", "Warps the player to the set spawn", null, new String[] {});
	}

	@Override
	protected void callCommand(Player player, String[] args)
	{
		if (Core.getInstance().config.getString("spawn.world") == null)
		{
			player.sendMessage(ChatColor.RED + "The spawn point is not set! Contact your server's administrator!");
			return;
		} else
		{
			try
			{
				Core.getInstance().back.replace(player.getUniqueId(), player.getLocation());
				player.teleport(new Location(Bukkit.getWorld(Core.getInstance().config.getString("spawn.world")),
						Core.getInstance().config.getDouble("spawn.x"), Core.getInstance().config.getDouble("spawn.y"),
						Core.getInstance().config.getDouble("spawn.z"), (float) Core.getInstance().config.getDouble("spawn.yaw"),
						(float) Core.getInstance().config.getDouble("spawn.pitch")));
			} catch (Exception e)
			{
				player.sendMessage(ChatColor.RED + "Something went wrong! Get your server admin to verify the spawn settings!");
				return;
			}
		}
	}

}
