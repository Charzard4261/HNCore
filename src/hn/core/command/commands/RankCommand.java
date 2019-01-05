package hn.core.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import hn.core.Core;
import hn.core.command.ACommand;
import hn.core.rank.Rank;

public class RankCommand extends ACommand {

	public RankCommand()
	{
		super("rank", "Manage user ranks", "HN.ranks", new String[] {});
	}

	@Override
	protected void callCommand(Player player, String[] args)
	{
		if (args.length == 2)
			if (Bukkit.getPlayer(args[0]) != null)
			{
				if (Rank.valueOf(args[1].toUpperCase()) != null)
				{
					if (Core.getInstance().getRank(player.getUniqueId()).getWeight() > Rank.valueOf(args[1]).getWeight()
							&& Core.getInstance().getRank(player.getUniqueId()).getWeight() > Core.getInstance()
									.getRank(Bukkit.getPlayer(args[0]).getUniqueId()).getWeight())
					{
						Core.getInstance().setRank(Bukkit.getPlayer(args[0]).getUniqueId(), Rank.valueOf(args[1]));
						player.sendMessage("Player's rank set");
					}
				} else
					player.sendMessage("Rank is invalid");
			} else
				player.sendMessage("Player cannot be found");
	}
}
