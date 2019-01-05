package hn.core.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftLargeFireball;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import hn.core.Core;
import hn.core.events.dragon.Dragon;
import hn.core.util.Utils;

public class DragonListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		if (event.getClickedInventory() == null || !event.getClickedInventory().getName().equals(ChatColor.stripColor("EVENT - DRAGON")))
			return;

		if (event.getSlot() != 13)
			event.setCancelled(true);
		else if (event.getCurrentItem().equals(Core.getInstance().getKeyItem()))
			new Dragon((Player) event.getWhoClicked());

	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event)
	{
		if (!Core.getInstance().dragons.isEmpty())
			for (Dragon dr : Core.getInstance().dragons.values())
			{
				event.getPlayer().hidePlayer(Core.getInstance(), Bukkit.getPlayer(dr.getPlayerUUID()));
				dr.getBossBar().addPlayer(event.getPlayer());
			}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event)
	{
		if (Core.getInstance().dragons.containsKey(event.getEntity().getUniqueId()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event)
	{
		if (Core.getInstance().dragons.containsKey(event.getEntity().getUniqueId()))
			event.setCancelled(true);

		if (Core.getInstance().dragons.isEmpty() || event.getEntityType() != EntityType.ENDER_DRAGON)
			return;

		Dragon dragon = null;

		for (Dragon dr : Core.getInstance().dragons.values())
			if (dr.getDragonUUID() == event.getEntity().getUniqueId())
				dragon = dr;

		if (dragon == null)
			return;

		dragon.takeDamage(event.getFinalDamage(), event.getDamager());
	}

	@EventHandler
	public void onDeath(EntityDeathEvent event)
	{
		if (Core.getInstance().dragons.containsKey(event.getEntity().getUniqueId()))
		{
			event.getDrops().clear();
			Core.getInstance().dragons.get(event.getEntity().getUniqueId()).onDeath();
			return;
		}
		
		Dragon dragon = null;

		for (Dragon dr : Core.getInstance().dragons.values())
			if (dr.getDragonUUID() == event.getEntity().getUniqueId())
				dragon = dr;

		if (dragon == null)
			return;
		
		dragon.onDragonDeath();
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event)
	{
		if (!Core.getInstance().dragons.containsKey(event.getPlayer().getUniqueId()) || event.getHand() != EquipmentSlot.HAND
				|| event.getPlayer().getInventory().getItemInMainHand() == null
				|| !event.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) // Dragon gets in way
			return;

		Dragon dr = Core.getInstance().dragons.get(event.getPlayer().getUniqueId());

		if (ChatColor.stripColor(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName()).equals("Fire"))
		{
			FireSpray fs = new FireSpray(event.getPlayer());
			fs.setTask(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Core.getInstance(), fs, 0, 3));
		}
		if (ChatColor.stripColor(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName()).equals("Fireball"))
		{
			dr.cooldown1 = 2;
			event.getPlayer().getInventory().setItem(1, new ItemStack(Material.GRAY_DYE, dr.cooldown1));

			ShootFireball fb = new ShootFireball(event.getPlayer(), 5);
			fb.setTask(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Core.getInstance(), fb, 0, 6));
		}
		if (ChatColor.stripColor(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName()).equals("World Cracker"))
		{
			dr.cooldown3 = 20;
			event.getPlayer().getInventory().setItem(3, new ItemStack(Material.GRAY_DYE, dr.cooldown3));

			WorldCracker wc = new WorldCracker(event.getPlayer());
			wc.setTask(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Core.getInstance(), wc, 0, 5));
		}
	}

}

class ShootFireball implements Runnable {
	int task, iterations = 0;
	Player player;
	Vector vector;
	int max;

	public ShootFireball(Player player, int iterations)
	{
		this.player = player;
		this.max = iterations;
	}

	@Override
	public void run()
	{
		iterations++;

		vector = Utils.beautifulVector(player.getLocation()).normalize().multiply(10).setY(3);

		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 100, 0.5f);
		CraftLargeFireball fb = (CraftLargeFireball) player.getWorld().spawnEntity(player.getLocation().add(vector), EntityType.FIREBALL);
		fb.setMomentum(player.getLocation().getDirection());
		fb.setIsIncendiary(true);

		if (iterations == max)
			Bukkit.getScheduler().cancelTask(task);
	}

	public void setTask(int task)
	{
		this.task = task;
	}

}

class WorldCracker implements Runnable {
	int task, iterations = 0;
	Player player;
	Vector vector;

	public WorldCracker(Player player)
	{
		this.player = player;
	}

	@Override
	public void run()
	{
		iterations++;

		vector = Utils.beautifulVector(player.getLocation()).normalize().multiply(10).setY(3);

		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 100, 1);

		Random random = new Random();

		CraftLargeFireball fb = (CraftLargeFireball) player.getWorld().spawnEntity(player.getLocation().add(vector), EntityType.FIREBALL);
		fb.setMomentum(player.getLocation().getDirection().multiply(random.nextDouble() + 0.1));
		fb.setIsIncendiary(true);

		Location location = null;
		boolean onLand = false;
		int x = random.nextInt(10) - 5;
		int y = 250;
		int z = random.nextInt(10) - 5;
		while (onLand == false)
		{
			BlockIterator iter = new BlockIterator(player, 100);
			Block lastBlock = iter.next();
			while (iter.hasNext())
			{
				lastBlock = iter.next();
				if (lastBlock.getType() == Material.AIR)
				{
					continue;
				}
				break;
			}
			location = new Location(player.getWorld(), lastBlock.getX() + x + 0.5, y, lastBlock.getZ() + z + 0.5);

			if (location.getBlock().getType() != Material.AIR)
			{
				onLand = true;
			} else
				y--;
		}
		player.getWorld().strikeLightning(location);

		if (iterations == 20)
			Bukkit.getScheduler().cancelTask(task);
	}

	public void setTask(int task)
	{
		this.task = task;
	}

}

class FireSpray implements Runnable {
	int task, iterations = 0;
	Player player;
	Vector vector;

	public FireSpray(Player player)
	{
		this.player = player;
	}

	@Override
	public void run()
	{
		iterations++;

		vector = Utils.beautifulVector(player.getLocation()).normalize().multiply(10).setY(3);

		@SuppressWarnings("deprecation")
		FallingBlock fb = player.getWorld().spawnFallingBlock(player.getLocation().add(vector), Material.FIRE, (byte) 0);
		fb.setVelocity(player.getLocation().getDirection());

		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 100, 1);

		if (iterations == 1)
			Bukkit.getScheduler().cancelTask(task);
	}

	public void setTask(int task)
	{
		this.task = task;
	}

}