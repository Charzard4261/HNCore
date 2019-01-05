package hn.core.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import hn.core.Core;
import hn.core.command.ACommand;
import hn.core.listeners.CoreCommandListener;
import hn.core.util.CoreUI;

public class CoreCommand extends ACommand {

	public CoreCommand()
	{
		super("hncore", "Manage the core through a GUI", "HN.manage", new String[] {});
		Bukkit.getPluginManager().registerEvents(new CoreCommandListener(), Core.getInstance());
	}

	@Override
	protected void callCommand(Player player, String[] args)
	{
		player.openInventory(new CoreUI().createInventoryCore());
	}

}
