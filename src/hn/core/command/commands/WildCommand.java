package hn.core.command.commands;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import hn.core.Core;
import hn.core.command.ACommand;

public class WildCommand extends ACommand {

	HashMap<UUID, Integer> cooldown = new HashMap<UUID, Integer>();

	public WildCommand()
	{
		super("wild", "Warps the player to a random location", null, new String[]
		{ "rtp" });
	}

	@Override
	protected void callCommand(Player player, String[] args)
	{

		if (cooldown.containsKey(player.getUniqueId()))
		{
			player.sendMessage(ChatColor.RED + "Command is on cooldown for another " + cooldown.get(player.getUniqueId()) + " seconds");
			return;
		}

		if (!player.hasPermission("HN.manage"))
		{
			cooldown.put(player.getUniqueId(), 300);
			CooldownTimer task = new CooldownTimer(player.getUniqueId());
			task.setId(Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.getInstance(), task, 0, 20));
		}

		Random random = new Random();

		Location location = null;

		int x = random.nextInt(10000) - 5000;
		int y = 250;
		int z = random.nextInt(10000) - 5000;

		boolean onLand = false;

		while (onLand == false)
		{
			location = new Location(player.getWorld(), x + 0.5, y, z + 0.5);

			if (location.getBlock().getType() != Material.AIR)
			{
				onLand = true;
			} else
				y--;
		}

		Core.getInstance().back.replace(player.getUniqueId(), player.getLocation());
		
		player.teleport(new Location(player.getWorld(), x + 0.5, y + 1, z + 0.5));

		player.sendMessage(ChatColor.DARK_GREEN + "Teleported to a random location!");
	}

	public class CooldownTimer implements Runnable {

		private int id;
		private UUID uuid;

		public CooldownTimer(UUID uuid)
		{
			this.uuid = uuid;
		}

		@Override
		public void run()
		{
			if (cooldown.get(uuid) <= 0)
			{
				cooldown.remove(uuid);
				Bukkit.getScheduler().cancelTask(id);
				return;
			}

			cooldown.replace(uuid, cooldown.get(uuid) - 1);
		}

		public void setId(int id)
		{
			this.id = id;
		}
	}
}
