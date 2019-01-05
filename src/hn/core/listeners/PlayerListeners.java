package hn.core.listeners;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftHumanEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Sign;

import hn.core.Core;
import hn.core.util.PermsHax;
import hn.core.util.Utils;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerListeners implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent event)
	{
		event.setCancelled(true);

		if (Core.getInstance().punishments.contains("chat.muted." + event.getPlayer().getUniqueId().toString()))
		{
			if (Core.getInstance().punishments.getString("chat.muted." + event.getPlayer().getUniqueId().toString() + ".expiry")
					.equalsIgnoreCase("Permanent"))
			{
				event.getPlayer().sendMessage(ChatColor.RED + "You are muted " + ChatColor.GREEN + "permanently" + ChatColor.RED + ".");
				return;
			}

			try
			{
				Date expiry = Utils.sdf
						.parse(Core.getInstance().punishments.getString("chat.muted." + event.getPlayer().getUniqueId().toString() + ".expiry"));
				if (new Date().before(expiry))
				{
					long difference = (expiry.getTime() - new Date().getTime()) / 1000; // seconds
					int seconds = (int) difference;
					int minutes = seconds / 60;
					int hours = minutes / 60;
					int days = hours / 24;
					seconds = seconds % 60;
					minutes = minutes % 60;
					hours = hours % 24;

					String time = "" + ChatColor.GREEN + days + " days" + ChatColor.RED + ", " + ChatColor.GREEN + hours + " hours" + ChatColor.RED
							+ ", " + ChatColor.GREEN + minutes + " minutes" + ChatColor.RED + ", " + ChatColor.GREEN + seconds + " seconds";

					event.getPlayer().sendMessage(ChatColor.RED + "You are muted for another " + time + ChatColor.RED + ".");
					return;
				} else
				{
					Core.getInstance().punishments.set("chat.muted." + event.getPlayer().getUniqueId().toString(), null);
					Core.getInstance().savePunishments();
				}
			} catch (ParseException e)
			{
				e.printStackTrace();
			}
		}

		TextComponent blank = new TextComponent("");
		TextComponent t = new TextComponent(ChatColor.translateAlternateColorCodes('&',
				"<" + Core.getInstance().getRank(event.getPlayer().getUniqueId()).getRankString() + event.getPlayer().getDisplayName() + "&f>"));
		ArrayList<TextComponent> tcs = new ArrayList<TextComponent>();
		tcs.add(new TextComponent(ChatColor.translateAlternateColorCodes('&', "           &3&lHN&r&bServer&r           ")));
		tcs.add(new TextComponent("\n" + ChatColor.translateAlternateColorCodes('&', "&eStatistics")));
		tcs.add(new TextComponent("\n" + ChatColor.translateAlternateColorCodes('&', "    &8» &fName: &a") + event.getPlayer().getDisplayName()));
		tcs.add(new TextComponent("\n" + ChatColor.translateAlternateColorCodes('&', "    &8» &fRank: &a")
				+ Core.getInstance().getRank(event.getPlayer().getUniqueId())));
		tcs.add(new TextComponent("\n"));
		// tcs.add();
		int treesFelled = Core.getInstance().playerdata.getInt(event.getPlayer().getUniqueId() + ".statistics.treesFelled", 0);
		if (treesFelled > 5)
		{
			String tier = "\n       ";
			if (treesFelled >= 5 && treesFelled < 75)
				tier = tier + ChatColor.GRAY + "New Woodcutter";
			else if (treesFelled >= 75 && treesFelled < 250)
				tier = tier + ChatColor.DARK_AQUA + "Novice Woodcutter";
			else if (treesFelled >= 250 && treesFelled < 500)
				tier = tier + ChatColor.AQUA + "Experienced Woodcutter";
			else if (treesFelled >= 500)
				tier = tier + ChatColor.GOLD + "True Lumberjack";
			tcs.add(new TextComponent(tier));
		}
		int diamondsMined = Core.getInstance().playerdata.getInt(event.getPlayer().getUniqueId() + ".statistics.diamondOresMined", 0);
		if (diamondsMined > 5)
		{
			String tier = "\n       ";
			if (diamondsMined >= 5 && diamondsMined < 25)
				tier = tier + ChatColor.GRAY + "New Miner";
			else if (diamondsMined >= 25 && diamondsMined < 50)
				tier = tier + ChatColor.DARK_AQUA + "Novice Miner";
			else if (diamondsMined >= 50 && diamondsMined < 75)
				tier = tier + ChatColor.AQUA + "Experienced Miner";
			else if (diamondsMined >= 100)
				tier = tier + ChatColor.GOLD + "Diamonds Galore";
			tcs.add(new TextComponent(tier));
		}

		TextComponent[] hover = tcs.toArray(new TextComponent[0]);

		// new TextComponent[]
		// { new TextComponent(ChatColor.translateAlternateColorCodes('&', "
		// &3&lHN&r&bServer&r ")),
		// new TextComponent("\n" + ChatColor.translateAlternateColorCodes('&',
		// "&eStatistics")),
		// new TextComponent("\n" + ChatColor.translateAlternateColorCodes('&', " &8»
		// &fName: &a") + event.getPlayer().getDisplayName()),
		// new TextComponent("\n" + ChatColor.translateAlternateColorCodes('&', " &8»
		// &fRank: &a") + Core.getInstance().getRank(event.getPlayer().getUniqueId()))
		// };
		t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
		blank.addExtra(t);

		if (event.getPlayer().hasPermission("HN.colourchat"))
			blank.addExtra(" " + ChatColor.translateAlternateColorCodes('&', event.getMessage()));
		else
			blank.addExtra(" " + event.getMessage());

		for (Player player : event.getRecipients())
			player.spigot().sendMessage(blank);

		Bukkit.getConsoleSender().sendMessage(blank.toLegacyText());

	}

	@EventHandler
	public void onSleep(PlayerBedEnterEvent event)
	{
		if (event.getBedEnterResult() != BedEnterResult.OK)
			return;

		int count = 1;
		int total = 1;
		int needed;

		for (Player pl : Bukkit.getOnlinePlayers())
			if (pl.getWorld() == event.getPlayer().getWorld() && pl != event.getPlayer())
				if (pl.getGameMode() == GameMode.SURVIVAL && !Core.getInstance().isAFK(pl.getUniqueId()))
				{
					if (pl.isSleeping())
						count++;
					total++;
				}

		needed = (int) Math.ceil(((double) total) / 3);
		System.out.println("Needed to sleep: " + ((double) total) / 3 + " " + Math.ceil(((double) total) / 3));

		for (Player pl : Bukkit.getOnlinePlayers())
			if (pl.getWorld() == event.getPlayer().getWorld())
				pl.sendMessage(event.getPlayer().getDisplayName() + " is now sleeping (" + count + "/" + needed + ")");

		if (count >= needed)
		{
			if (13000 <= event.getPlayer().getWorld().getTime() || event.getPlayer().getWorld().getTime() < 1000)
				event.getPlayer().getWorld().setTime(0l);
			if (event.getPlayer().getWorld().hasStorm())
				event.getPlayer().getWorld().setWeatherDuration(1);
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event)
	{
		switch (event.getBlock().getType())
		{
		case SPAWNER:
			if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.DIAMOND_PICKAXE
					&& event.getPlayer().getInventory().getItemInMainHand().getType() != Material.IRON_PICKAXE)
				return;

			if (!event.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH))
				return;

			if (!Core.getInstance().config.getBoolean("silk-spawners"))
				return;

			event.setExpToDrop(0);

			ItemStack spawner = new ItemStack(Material.SPAWNER);
			ItemMeta spawnermeta = spawner.getItemMeta();

			String name = "";
			String[] namesplit = ((CreatureSpawner) event.getBlock().getState()).getSpawnedType().toString().split("_");
			for (String split : namesplit)
				name = name + split.substring(0, 1) + split.toLowerCase().substring(1, split.length()) + " ";

			spawnermeta.setDisplayName(ChatColor.WHITE + name + "Spawner");
			spawner.setItemMeta(spawnermeta);

			event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), spawner);
			break;
		case CHEST:
			// case TRAPPED_CHEST:
			// case ACACIA_DOOR:
			// case BIRCH_DOOR:
			// case DARK_OAK_DOOR:
			// case JUNGLE_DOOR:
			// case OAK_DOOR:
			// case SPRUCE_DOOR:
			for (String s : Core.getInstance().config.getStringList("shops"))
			{
				String[] details = s.split(";:;");
				String[] claim = details[1].split(",");
				double x = Double.valueOf(claim[0]);
				double y = Double.valueOf(claim[1]);
				double z = Double.valueOf(claim[2]);
				String world = claim[3];
				if (event.getBlock().getLocation().equals(new Location(Bukkit.getWorld(world), x, y, z)))
				{
					if (!details[2].contains(event.getPlayer().getUniqueId().toString()))
						event.setCancelled(true);
					else
					{
						List<String> claims = Core.getInstance().config.getStringList("shops");
						claims.remove(s);
						Core.getInstance().config.set("shops", claims);
						Core.getInstance().saveConfig();
					}
					break;
				}
			}
			break;
		case SIGN:
		case WALL_SIGN:
			for (String s : Core.getInstance().config.getStringList("shops"))
			{
				String[] details = s.split(";:;");
				String[] claim = details[0].split(",");
				double x = Double.valueOf(claim[0]);
				double y = Double.valueOf(claim[1]);
				double z = Double.valueOf(claim[2]);
				String world = claim[3];
				if (event.getBlock().getLocation().equals(new Location(Bukkit.getWorld(world), x, y, z)))
				{
					if (!details[2].contains(event.getPlayer().getUniqueId().toString()))
						event.setCancelled(true);
					else
					{
						List<String> claims = Core.getInstance().config.getStringList("shops");
						claims.remove(s);
						Core.getInstance().config.set("shops", claims);
						Core.getInstance().saveConfig();
					}
					break;
				}
			}
			break;
		case ACACIA_LOG:
		case BIRCH_LOG:
		case DARK_OAK_LOG:
		case JUNGLE_LOG:
		case OAK_LOG:
		case SPRUCE_LOG:
			if (event.getPlayer().getGameMode() != GameMode.CREATIVE && event.isDropItems() != false)
			{
				Core.getInstance().playerdata.set(event.getPlayer().getUniqueId() + ".statistics.treesFelled",
						Core.getInstance().playerdata.getInt(event.getPlayer().getUniqueId() + ".statistics.treesFelled", 0) + 1);
				Core.getInstance().savePlayerdata();
			}
			break;
		case DIAMOND_ORE:
			if (!event.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)
					&& event.getPlayer().getGameMode() != GameMode.CREATIVE && event.isDropItems() != false)
			{
				Core.getInstance().playerdata.set(event.getPlayer().getUniqueId() + ".statistics.diamondOresMined",
						Core.getInstance().playerdata.getInt(event.getPlayer().getUniqueId() + ".statistics.diamondOresMined", 0) + 1);
				Core.getInstance().savePlayerdata();
			}
			break;
		default:
			break;
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event)
	{
		if (event.getBlockPlaced().getType() == Material.SPAWNER && event.getItemInHand().hasItemMeta())
		{
			try
			{
				String name = "";
				String[] namesplit = ChatColor.stripColor(event.getItemInHand().getItemMeta().getDisplayName().substring(0,
						event.getItemInHand().getItemMeta().getDisplayName().length() - 8)).split(" ");
				for (String split : namesplit)
					name = name + split.toUpperCase() + "_";

				name = name.substring(0, name.length() - 1);

				try
				{
					CreatureSpawner creaturespawner = (CreatureSpawner) event.getBlockPlaced().getState();
					creaturespawner.setSpawnedType(EntityType.valueOf(name));
					creaturespawner.update();
				} catch (Exception e)
				{
					System.out.println("Spawner was placed with display name, but type couldn't be found: " + name);
				}
			} catch (Exception e)
			{
				System.out.println("Display name could not be converted: " + event.getItemInHand().getItemMeta().getDisplayName());
			}
		}

	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event)
	{
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			switch (event.getClickedBlock().getType())
			{
			case WALL_SIGN:
				for (String s : Core.getInstance().config.getStringList("shops"))
				{
					String[] details = s.split(";:;");
					String[] sign = details[0].split(",");
					double x = Double.valueOf(sign[0]);
					double y = Double.valueOf(sign[1]);
					double z = Double.valueOf(sign[2]);
					String world = sign[3];
					if (event.getClickedBlock().getLocation().equals(new Location(Bukkit.getWorld(world), x, y, z)))
					{
						Material mat = Material.valueOf(((org.bukkit.block.Sign) event.getClickedBlock().getState()).getLine(2).split(" ")[1]);
						int amount = Integer.valueOf(((org.bukkit.block.Sign) event.getClickedBlock().getState()).getLine(2).split(" ")[0]);
						int price = Integer.valueOf(((org.bukkit.block.Sign) event.getClickedBlock().getState()).getLine(3).split(" ")[0]);

						Chest chest = (Chest) event.getClickedBlock()
								.getRelative(((Sign) event.getClickedBlock().getState().getData()).getAttachedFace()).getState();

						for (ItemStack is : chest.getInventory().getContents())
							if (is != null && is.hasItemMeta())
							{
								event.getPlayer().sendMessage(ChatColor.RED + "This shop has an NBT item in it! Tell the owner to remove it!");
								return;
							}

						if (!chest.getInventory().contains(mat, amount))
						{
							event.getPlayer().sendMessage(ChatColor.RED + "This shop is out of stock!");
							return;
						}
						if (!event.getPlayer().getInventory().contains(Material.DIAMOND, price))
						{
							event.getPlayer().sendMessage(ChatColor.RED + "You don't have enough money for this!");
							return;
						}
						HashMap<Integer, ItemStack> notAddedWhenBuying = chest.getInventory().addItem(new ItemStack(Material.DIAMOND, price));
						if (!notAddedWhenBuying.isEmpty())
						{
							chest.getInventory().removeItem(new ItemStack(Material.DIAMOND, price - notAddedWhenBuying.size()));
							event.getPlayer().sendMessage(ChatColor.RED + "This shop is out of space!");
							return;
						} else
						{
							chest.getInventory().removeItem(new ItemStack(mat, amount));
							event.getPlayer().getInventory().removeItem(new ItemStack(Material.DIAMOND, price));
							HashMap<Integer, ItemStack> notAddedSoDropping = event.getPlayer().getInventory().addItem(new ItemStack(mat, amount));
							for (ItemStack item : notAddedSoDropping.values())
							{
								event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), item);
							}
						}

						break;
					}
				}
				break;
			case CHEST:
				// case TRAPPED_CHEST:
				// case ACACIA_DOOR:
				// case BIRCH_DOOR:
				// case DARK_OAK_DOOR:
				// case JUNGLE_DOOR:
				// case OAK_DOOR:
				// case SPRUCE_DOOR:
				for (String s : Core.getInstance().config.getStringList("shops"))
				{
					String[] details = s.split(";:;");
					String[] claim = details[1].split(",");
					double x = Double.valueOf(claim[0]);
					double y = Double.valueOf(claim[1]);
					double z = Double.valueOf(claim[2]);
					String world = claim[3];
					if (event.getClickedBlock().getLocation().equals(new Location(Bukkit.getWorld(world), x, y, z)))
					{
						if (!details[2].contains(event.getPlayer().getUniqueId().toString()) && !event.getPlayer().hasPermission("HN.manage"))
							event.setCancelled(true);
						break;
					}
				}
				break;
			default:
				break;
			}
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent event)
	{
		if (event.getInventory() != null)
			if (event.getInventory() instanceof AnvilInventory)
				if (event.getCurrentItem().getType() == Material.SPAWNER)
					event.setCancelled(true);
	}

	@EventHandler
	public void onSignWrite(SignChangeEvent event)
	{

		if (event.getLine(0).equalsIgnoreCase("[Buy]"))
			switch (event.getBlock().getRelative(((Sign) event.getBlock().getState().getData()).getAttachedFace()).getType())
			{
			case CHEST:
				// case TRAPPED_CHEST:
				// case ACACIA_DOOR:
				// case BIRCH_DOOR:
				// case DARK_OAK_DOOR:
				// case JUNGLE_DOOR:
				// case OAK_DOOR:
				// case SPRUCE_DOOR:
				Material mat;
				int amount, price;

				try
				{
					mat = Material.valueOf(event.getLine(1).toUpperCase());
					amount = Integer.valueOf(event.getLine(2));
					price = Integer.valueOf(event.getLine(3));
				} catch (Exception e)
				{
					return;
				}

				if (amount > 64 || price > 64)
					return;
				if (amount < 1 || price < 0)
					return;

				event.setLine(0, ChatColor.RESET + "[" + ChatColor.GOLD + "Buy" + ChatColor.RESET + "]");
				event.setLine(1, event.getPlayer().getDisplayName());
				event.setLine(2, amount + " " + mat.toString());
				event.setLine(3, price + " " + ChatColor.AQUA + "Diamond" + (price != 1 ? "s" : ""));
				Block sign = event.getBlock();
				Block lock = event.getBlock().getRelative(((Sign) event.getBlock().getState().getData()).getAttachedFace());
				String claim = sign.getLocation().getX() + "," + sign.getLocation().getY() + "," + sign.getLocation().getZ() + ","
						+ sign.getLocation().getWorld().getName() + ";:;" + lock.getLocation().getX() + "," + lock.getLocation().getY() + ","
						+ lock.getLocation().getZ() + "," + lock.getLocation().getWorld().getName() + ";:;"
						+ event.getPlayer().getUniqueId().toString();
				// for (OfflinePlayer player : Bukkit.getOfflinePlayers())
				// if (player.getName().equalsIgnoreCase(event.getLine(2)))
				// {
				// event.setLine(2, player.getName());
				// claim.concat("," + player.getUniqueId().toString());
				// } else if (player.getName().equalsIgnoreCase(event.getLine(3)))
				// {
				// event.setLine(3, player.getName());
				// claim.concat("," + player.getUniqueId().toString());
				// }
				List<String> claims = Core.getInstance().config.getStringList("shops");
				claims.add(claim);
				Core.getInstance().config.set("shops", claims);
				Core.getInstance().saveConfig();
				break;
			default:
				break;
			}

	}

	@EventHandler
	public void move(PlayerMoveEvent event)
	{
		if (Core.getInstance().isAFK(event.getPlayer().getUniqueId()))
			if (event.getTo().distance(event.getFrom()) >= 0.1)
				Core.getInstance().setAFK(event.getPlayer().getUniqueId(), false, true);
	}

	@EventHandler
	public void onDie(PlayerDeathEvent event)
	{
		Core.getInstance().back.replace(event.getEntity().getUniqueId(), event.getEntity().getLocation());
		System.out.println(event.getEntity().getDisplayName() + " died in world " + event.getEntity().getLocation().getWorld() + " at "
				+ event.getEntity().getLocation().getX() + " " + event.getEntity().getLocation().getY() + " "
				+ event.getEntity().getLocation().getZ());
	}

	public static Field pf;

	static
	{
		try
		{
			pf = CraftHumanEntity.class.getDeclaredField("perm");
			pf.setAccessible(true);
		} catch (Exception e1)
		{
			e1.printStackTrace();
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event)
	{
		try
		{
			pf.set(event.getPlayer(), new PermsHax(event.getPlayer()));
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}

		if (!event.getPlayer().hasPlayedBefore())
		{
			if (Core.getInstance().config.getString("spawn.world") != null)
				event.getPlayer()
						.teleport(new Location(Bukkit.getWorld(Core.getInstance().config.getString("spawn.world")),
								Core.getInstance().config.getDouble("spawn.x"), Core.getInstance().config.getDouble("spawn.y"),
								Core.getInstance().config.getDouble("spawn.z"), (float) Core.getInstance().config.getDouble("spawn.pitch"),
								(float) Core.getInstance().config.getDouble("spawn.yaw")));
			event.setJoinMessage(ChatColor.BLUE + "---" + ChatColor.AQUA + "===" + ChatColor.WHITE + "Welcome " + event.getPlayer().getDisplayName()
					+ " to the server!" + ChatColor.AQUA + "===" + ChatColor.BLUE + "---");
		} else
			event.setJoinMessage(ChatColor.BLUE + "---" + ChatColor.AQUA + "===" + ChatColor.WHITE + event.getPlayer().getDisplayName()
					+ " has joined! Welcome back!" + ChatColor.AQUA + "===" + ChatColor.BLUE + "---");

		if (!Core.getInstance().playerdata.contains("" + event.getPlayer().getUniqueId()))
		{
			Core.getInstance().playerdata.set(event.getPlayer().getUniqueId() + ".names", new ArrayList<String>());
			Core.getInstance().playerdata.set(event.getPlayer().getUniqueId() + ".rank", "member");
			Core.getInstance().playerdata.set(event.getPlayer().getUniqueId() + ".punishments", new ArrayList<String>());
			Core.getInstance().savePlayerdata();
		}

		List<String> names = Core.getInstance().playerdata.getStringList(event.getPlayer().getUniqueId() + ".names");
		if (names.size() == 0 || !names.get(names.size() - 1).equalsIgnoreCase(event.getPlayer().getDisplayName()))
		{
			names.add("" + event.getPlayer().getDisplayName());
			Core.getInstance().playerdata.set(event.getPlayer().getUniqueId() + ".names", names);
			Core.getInstance().savePlayerdata();
		}

		event.getPlayer()
				.setPlayerListName(Core.getInstance().getRank(event.getPlayer().getUniqueId()).getRankString() + event.getPlayer().getDisplayName());

		for (Player player : Bukkit.getOnlinePlayers())
			Utils.sendTabHF(player,
					Utils.serverName + ChatColor.GRAY + "\nOnline: " + ChatColor.ITALIC + "" + ChatColor.AQUA + Bukkit.getOnlinePlayers().size()
							+ ChatColor.GRAY + "/" + ChatColor.DARK_AQUA + Bukkit.getMaxPlayers() + ChatColor.GRAY + "\n========================",
					ChatColor.GRAY + "========================");

		Core.getInstance().back.put(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event)
	{
		if (Core.getInstance().isAFK(event.getPlayer().getUniqueId()))
			Core.getInstance().setAFK(event.getPlayer().getUniqueId(), false, false);

		for (Player player : Bukkit.getOnlinePlayers())
			Utils.sendTabHF(player,
					Utils.serverName + ChatColor.GRAY + "HN" + ChatColor.AQUA + "Server" + ChatColor.GRAY + "\nOnline: " + ChatColor.ITALIC + ""
							+ ChatColor.AQUA + Bukkit.getOnlinePlayers().size() + ChatColor.GRAY + "/" + ChatColor.DARK_AQUA + Bukkit.getMaxPlayers()
							+ ChatColor.GRAY + "\n========================",
					ChatColor.GRAY + "========================");

		event.setQuitMessage(ChatColor.BLUE + "---" + ChatColor.AQUA + "===" + ChatColor.WHITE + event.getPlayer().getDisplayName() + " has left!"
				+ ChatColor.AQUA + "===" + ChatColor.BLUE + "---");
	}

}
