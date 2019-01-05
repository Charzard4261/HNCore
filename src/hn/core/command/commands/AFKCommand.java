package hn.core.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import hn.core.Core;
import hn.core.command.ACommand;

public class AFKCommand extends ACommand {

	public AFKCommand()
	{
		super("afk", "Sets a player's AFK state", null, new String[] {});
	}

	@Override
	protected void callCommand(Player player, String[] args)
	{
		Player target = player;
		
		if (player.hasPermission("hn.manage") && args.length != 0)
			if (Bukkit.getPlayer(args[0]) != null)
				target = Bukkit.getPlayer(args[0]);

		if (Core.getInstance().isAFK(target.getUniqueId()))
			Core.getInstance().setAFK(target.getUniqueId(), false, true);
		else
			Core.getInstance().setAFK(target.getUniqueId(), true, true);
	}

}
