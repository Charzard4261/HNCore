package hn.core.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import hn.core.Core;

public abstract class ACommand extends BukkitCommand {

	String name;
	String command_host;

	public ACommand(String name, String description, String permission, String... alias)
	{
		super(name);
		this.name = name;
		this.description = description;
		this.command_host = Core.getInstance().getName();
		if (permission != null)
		{
			this.setPermission(permission);
			this.setPermissionMessage(ChatColor.RED + "You do not have permission to use this command");
		}
		for (String ali : alias)
		{
			this.getAliases().add(ali);
		}
	}

	@Override
	public final boolean execute(CommandSender sender, String command, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "The Console Command Sender cannot send this command!");
			return true;
		}
		Player pl = (Player) sender;

		if (this.getPermission() != null)
			if (!pl.hasPermission(this.getPermission()))
			{
				pl.sendMessage(this.getPermissionMessage());
				return true;
			}

		this.callCommand(pl, args);

		return true;
	}

	protected abstract void callCommand(Player player, String[] args);

	public String getName()
	{
		return name;
	}

	public String getHost()
	{
		return command_host;
	}

}
