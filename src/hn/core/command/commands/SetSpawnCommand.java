package hn.core.command.commands;

import org.bukkit.entity.Player;

import hn.core.Core;
import hn.core.command.ACommand;
import hn.core.util.Utils;

public class SetSpawnCommand extends ACommand {

	public SetSpawnCommand()
	{
		super("setspawn", "Sets the spawnpoint", "hn.manage", new String[] {});
	}

	@Override
	protected void callCommand(Player player, String[] args)
	{
		Core.getInstance().config.set("spawn.world", player.getWorld().getName());
		Core.getInstance().config.set("spawn.x", player.getLocation().getBlockX() + 0.5);
		Core.getInstance().config.set("spawn.y", player.getLocation().getBlockY());
		Core.getInstance().config.set("spawn.z", player.getLocation().getBlockZ() + 0.5);
		Core.getInstance().config.set("spawn.pitch", player.getLocation().getPitch());
		Core.getInstance().config.set("spawn.yaw", player.getLocation().getYaw());
		Core.getInstance().saveConfig();
		
		player.sendMessage(Utils.serverName + ">> Spawn is set to your location!");
	}

}
