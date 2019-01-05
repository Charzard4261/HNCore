package hn.core.command.commands;

import java.util.Calendar;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import hn.core.Core;
import hn.core.command.ACommand;

public class BackCommand extends ACommand {

	public BackCommand()
	{
		super("back", "Warps the player to their cached back position", null, new String[] {});
	}

	@Override
	protected void callCommand(Player player, String[] args)
	{
		if (!Core.getInstance().back.containsKey(player.getUniqueId()))
		{
			player.sendMessage(ChatColor.RED + "You don't have a chached location!");
		} else
		{
			boolean tp = true;
			if (Core.getInstance().backCooldown.containsKey(player.getUniqueId()))
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(Core.getInstance().backCooldown.get(player.getUniqueId()));
				cal.add(Calendar.MINUTE, 5);
				if (new Date().before(cal.getTime()))
				{
					tp = false;
					long difference = (cal.getTime().getTime() - new Date().getTime()) / 1000; // seconds
					int seconds = (int) difference;
					int minutes = seconds / 60;
					seconds = seconds % 60;
					minutes = minutes % 60;

					String time = "" + ChatColor.GREEN + minutes + " minutes" + ChatColor.RED + ", " + ChatColor.GREEN + seconds + " seconds";

					player.sendMessage(ChatColor.RED + "You must wait another " + time + ChatColor.RED + " to warp back.");
				}
			}

			if (tp)
			{
				Location location = player.getLocation().clone();
				player.teleport(Core.getInstance().back.get(player.getUniqueId()));
				if (!player.hasPermission("HN.manage"))
					Core.getInstance().backCooldown.put(player.getUniqueId(), new Date());
				Core.getInstance().back.replace(player.getUniqueId(), location);
			}

		}
	}

}
