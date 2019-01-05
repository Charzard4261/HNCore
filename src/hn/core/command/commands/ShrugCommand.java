package hn.core.command.commands;

import org.bukkit.entity.Player;

import hn.core.command.ACommand;

public class ShrugCommand extends ACommand {

	public ShrugCommand()
	{
		super("shrug", "idk", null, new String[] {});
	}

	@Override
	protected void callCommand(Player player, String[] args)
	{
		player.chat("¯\\_(ツ)_/¯");
	}

}
