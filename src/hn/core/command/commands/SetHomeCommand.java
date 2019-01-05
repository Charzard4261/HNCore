package hn.core.command.commands;

import org.bukkit.entity.Player;

import hn.core.Core;
import hn.core.command.ACommand;
import hn.core.util.Utils;

public class SetHomeCommand extends ACommand {

	public SetHomeCommand()
	{
		super("sethome", "Sets the player's home", null, new String[] {});
	}

	@Override
	protected void callCommand(Player player, String[] args)
	{
		String home = "home";
		if (args.length >= 1 && player.hasPermission("HN.donator.multiplehomes"))
			home = args[0];
		
		Core.getInstance().playerdata.set(player.getUniqueId() + ".homes." + home + ".world", player.getWorld().getName());
		Core.getInstance().playerdata.set(player.getUniqueId() + ".homes." + home + ".x", player.getLocation().getBlockX() + 0.5);
		Core.getInstance().playerdata.set(player.getUniqueId() + ".homes." + home + ".y", player.getLocation().getBlockY());
		Core.getInstance().playerdata.set(player.getUniqueId() + ".homes." + home + ".z", player.getLocation().getBlockZ() + 0.5);
		Core.getInstance().playerdata.set(player.getUniqueId() + ".homes." + home + ".pitch", player.getLocation().getPitch());
		Core.getInstance().playerdata.set(player.getUniqueId() + ".homes." + home + ".yaw", player.getLocation().getYaw());
		Core.getInstance().savePlayerdata();
		
		player.sendMessage(Utils.serverName + ">> Your home " + home + " was set to your location! Be careful, if you do this again you will overwrite your old home without warning!");
	}

}
