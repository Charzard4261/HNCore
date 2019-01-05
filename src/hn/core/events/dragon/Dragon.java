package hn.core.events.dragon;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import hn.core.Core;

public class Dragon {

	private UUID dragonUUID, playerUUID;
	private BossBar bossBar;
	private int task;
	public int cooldown0 = 1, cooldown1 = 1, cooldown2 = 1, cooldown3 = 1, cooldown4 = 1;
	public ItemStack item0, item1, item2, item3, item4;
	int tick = 19;

	public UUID getDragonUUID()
	{
		return dragonUUID;
	}

	public UUID getPlayerUUID()
	{
		return playerUUID;
	}

	public BossBar getBossBar()
	{
		return bossBar;
	}

	public Dragon(Player player)
	{
		setupItems();
		playerUUID = player.getUniqueId();
		player.setGameMode(GameMode.ADVENTURE);
		player.setAllowFlight(true);
		player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
		player.setFoodLevel(16);
		bossBar = Bukkit.createBossBar("Dragon", BarColor.PURPLE, BarStyle.SEGMENTED_20, BarFlag.PLAY_BOSS_MUSIC);
		for (Player pl : Bukkit.getOnlinePlayers())
		{
			if (pl != player)
				pl.hidePlayer(Core.getInstance(), player);
			bossBar.addPlayer(pl);
		}

		EnderDragon dragon = (EnderDragon) player.getWorld().spawnEntity(player.getLocation(), EntityType.ENDER_DRAGON);
		dragonUUID = dragon.getUniqueId();
		player.getPassengers().add(dragon);

		task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Core.getInstance(), new Runnable() {

			@Override
			public void run()
			{
				dragon.teleport(new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY() + 1,
						player.getLocation().getZ(), player.getLocation().getYaw() + 180, player.getLocation().getPitch()));
				bossBar.setProgress(dragon.getHealth() / dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
				if (player.isSneaking())
					dragon.setPhase(Phase.HOVER);
				else
					dragon.setPhase(Phase.CIRCLING);

				tick++;
				if (tick == 20)
				{
					if (cooldown0 != 0)
					{
						cooldown0--;
						if (cooldown0 != 0)
							player.getInventory().getItem(0).setAmount(cooldown0);
						else
							player.getInventory().setItem(0, item0);
					}
					if (cooldown1 != 0)
					{
						cooldown1--;
						if (cooldown1 != 0)
							player.getInventory().getItem(1).setAmount(cooldown1);
						else
							player.getInventory().setItem(1, item1);
					}
//					if (cooldown2 != 0)
//					{
//						cooldown2--;
//						if (cooldown2 != 0)
//							player.getInventory().getItem(2).setAmount(cooldown2);
//						else
//							player.getInventory().setItem(2, item2);
//					}
					if (cooldown3 != 0)
					{
						cooldown3--;
						if (cooldown3 != 0)
							player.getInventory().getItem(3).setAmount(cooldown3);
						else
							player.getInventory().setItem(3, item3);
					}
					if (cooldown4 != 0)
					{
						cooldown4--;
						if (cooldown4 != 0)
							player.getInventory().getItem(4).setAmount(cooldown4);
						else
							player.getInventory().setItem(4, item4);
					}
					tick = 0;
				}

			}

		}, 0, 1);
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());

		Core.getInstance().dragons.put(playerUUID, this);
	}

	private void setupItems()
	{
		item0 = new ItemStack(Material.BLAZE_POWDER);
		ItemMeta meta0 = item0.getItemMeta();
		meta0.setDisplayName("Fire");
		item0.setItemMeta(meta0);
		
		item1 = new ItemStack(Material.FIRE_CHARGE);
		ItemMeta meta1 = item1.getItemMeta();
		meta1.setDisplayName("Fireball");
		item1.setItemMeta(meta1);
		
		item3 = new ItemStack(Material.FIRE_CHARGE);
		ItemMeta meta3 = item3.getItemMeta();
		meta3.setDisplayName("World Cracker");
		item3.setItemMeta(meta3);

		item4 = new ItemStack(Material.GHAST_TEAR);
		ItemMeta meta4 = item4.getItemMeta();
		meta4.addEnchant(Enchantment.DAMAGE_ALL, 30000, true);
		meta4.setDisplayName("suicide");
		item4.setItemMeta(meta4);
	}

	public void takeDamage(double amount, Entity source)
	{
		Player player = Bukkit.getPlayer(playerUUID);
		player.damage(amount, null);
	}

	public void onDeath()
	{
		Bukkit.getScheduler().cancelTask(task);

		Player player = Bukkit.getPlayer(playerUUID);
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
		player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0);
		if (player.getGameMode() != GameMode.CREATIVE)
			player.setAllowFlight(false);
		bossBar.removeAll();
		EnderDragon dragon = (EnderDragon) Bukkit.getEntity(dragonUUID);
		if (dragon != null && !dragon.isDead())
			dragon.damage(1000);

		Core.getInstance().dragons.remove(playerUUID);
	}

	public void onDragonDeath()
	{
		Player player = Bukkit.getPlayer(playerUUID);
		player.damage(1000000);
	}

}
