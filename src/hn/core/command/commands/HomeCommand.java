package hn.core.command.commands;

import java.util.Calendar;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import hn.core.Core;
import hn.core.command.ACommand;

public class HomeCommand extends ACommand {

	public HomeCommand()
	{
		super("home", "Warps the player to their set home", null, new String[] {});
	}

	@Override
	protected void callCommand(Player player, String[] args)
	{
		String home = "home";

		if (args.length >= 1 && player.hasPermission("HN.donator.multiplehomes"))
			home = args[0];

		if (Core.getInstance().playerdata.getString(player.getUniqueId() + ".homes." + home + ".world") == null)
		{
			player.sendMessage(ChatColor.RED + "You need to set your home " + home + " first!");
			return;
		} else
		{
			boolean tp = true;
			if (Core.getInstance().homeCooldown.containsKey(player.getUniqueId()))
			{
				Calendar cal = Calendar.getInstance();
				cal.setTime(Core.getInstance().homeCooldown.get(player.getUniqueId()));
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

					player.sendMessage(ChatColor.RED + "You must wait another " + time + ChatColor.RED + " to warp home.");
				}
			}

			if (tp)
				try
				{
					Core.getInstance().back.replace(player.getUniqueId(), player.getLocation());
					player.teleport(
							new Location(Bukkit.getWorld(Core.getInstance().playerdata.getString(player.getUniqueId() + ".homes." + home + ".world")),
									Core.getInstance().playerdata.getDouble(player.getUniqueId() + ".homes." + home + ".x"),
									Core.getInstance().playerdata.getDouble(player.getUniqueId() + ".homes." + home + ".y"),
									Core.getInstance().playerdata.getDouble(player.getUniqueId() + ".homes." + home + ".z"),
									(float) Core.getInstance().playerdata.getDouble(player.getUniqueId() + ".homes." + home + ".yaw"),
									(float) Core.getInstance().playerdata.getDouble(player.getUniqueId() + ".homes." + home + ".pitch")));
					if (!player.hasPermission("HN.manage"))
						Core.getInstance().homeCooldown.put(player.getUniqueId(), new Date());
				} catch (Exception e)
				{
					player.sendMessage(ChatColor.RED + "Something went wrong! Get your server admin to verify your home's settings!");
					return;
				}

		}
	}

}
