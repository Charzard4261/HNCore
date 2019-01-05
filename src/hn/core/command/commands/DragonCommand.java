package hn.core.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import hn.core.Core;
import hn.core.command.ACommand;
import hn.core.listeners.DragonListener;

public class DragonCommand extends ACommand {

	public DragonCommand()
	{
		super("dragon", "Turns a player into a dragon", "HN.events.dragon", new String[] {});
		Bukkit.getPluginManager().registerEvents(new DragonListener(), Core.getInstance());
	}

	@Override
	protected void callCommand(Player player, String[] args)
	{
		if (Core.getInstance().dragons.containsKey(player.getUniqueId()))
			return;

		player.sendMessage(ChatColor.RED + "This feature is highly experimental!");

		Inventory inv = Bukkit.createInventory(null, 27, "EVENT - DRAGON");
		player.openInventory(inv);
	}

}
