package hn.core.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import hn.core.command.ACommand;

public class PingCommand extends ACommand {

	public PingCommand()
	{
		super("ping", "Check your ping", null, new String[] {});
	}

	@Override
	protected void callCommand(Player player, String[] args)
	{
		player.sendMessage(ChatColor
				.translateAlternateColorCodes('&', "&8&m&l-------------------------------------"));
		player.sendMessage(ChatColor
				.translateAlternateColorCodes('&', "&fYour Ping: &e&n" +  ((CraftPlayer) player).getHandle().ping));
		player.sendMessage(ChatColor
				.translateAlternateColorCodes('&', "&8&m&l-------------------------------------"));
	}
}
