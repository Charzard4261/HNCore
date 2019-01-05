package hn.core.command.commands;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import hn.core.Core;
import hn.core.command.ACommand;
import hn.core.util.Utils;

public class MuteCommand extends ACommand {

	public MuteCommand()
	{
		super("mute", "Mute a player", "HN.punish.mute", new String[] {});
	}

	@Override
	protected void callCommand(Player player, String[] args)
	{
		if (args.length < 2)
		{
			player.sendMessage(ChatColor.RED + "Incorrect usage: /mute <player> <minutes> [reason]");
			return;
		}

		if (!args[1].equalsIgnoreCase("permanent"))
			try
			{
				Integer.valueOf(args[1]);
			} catch (Exception e)
			{
				player.sendMessage(ChatColor.RED + "Incorrect usage: /mute <player> <minutes> [reason]");
				return;
			}
		else
			args[1] = "Permanent";

		if (Bukkit.getPlayer(args[0]) == null)
		{
			player.sendMessage(ChatColor.RED + "The specified player is not online!");
			return;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		if (!args[1].equalsIgnoreCase("permanent"))
			cal.add(Calendar.MINUTE, Integer.valueOf(args[1]));

		String expiry = args[1].equalsIgnoreCase("permanent") ? "Permanent" : Utils.sdf.format(cal.getTime());

		Core.getInstance().punishments.set("chat.muted." + Bukkit.getPlayer(args[0]).getUniqueId() + ".expiry", expiry);
		Core.getInstance().punishments.set("chat.muted." + Bukkit.getPlayer(args[0]).getUniqueId() + ".punisher", player.getDisplayName());

		String reason = "";
		if (args.length >= 3)
			for (int i = 2; i < args.length; i++)
				reason.concat(" " + args[i]);
		else
			reason = "no reason provided";

		Core.getInstance().punishments.set("chat.muted." + Bukkit.getPlayer(args[0]).getUniqueId() + ".reason", reason);
		Core.getInstance().savePunishments();

		List<String> punishments = Core.getInstance().playerdata.getStringList(Bukkit.getPlayer(args[0]).getUniqueId() + ".punishments");
		punishments.add(Utils.sdf.format(new Date()) + " | Mute | " + args[1] + " | " + player.getDisplayName() + " | " + reason);
		Core.getInstance().playerdata.set(Bukkit.getPlayer(args[0]).getUniqueId() + ".punishments", punishments);
		Core.getInstance().savePlayerdata();

		String time = "";
		if (!args[1].equalsIgnoreCase("permanent"))
		{
			int seconds = Integer.valueOf(args[1]) * 60;
			int minutes = seconds / 60;
			int hours = minutes / 60;
			int days = hours / 24;
			seconds = seconds % 60;
			minutes = minutes % 60;
			hours = hours % 24;

			time = time + ChatColor.GREEN + days + " days" + ChatColor.RED + ", " + ChatColor.GREEN + hours + " hours" + ChatColor.RED + ", "
					+ ChatColor.GREEN + minutes + " minutes" + ChatColor.RED + ", " + ChatColor.GREEN + seconds + " seconds";
		} else
			time = args[1];

		Bukkit.getPlayer(args[0]).sendMessage(
				ChatColor.RED + "You have been muted for " + time + ChatColor.RED + " for " + ChatColor.GREEN + reason + ChatColor.RED + ".");

	}

}
