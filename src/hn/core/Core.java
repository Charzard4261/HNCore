package hn.core; // TODO - Unmute command

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import hn.core.command.CommandFactory;
import hn.core.command.commands.AFKCommand;
import hn.core.command.commands.BackCommand;
import hn.core.command.commands.BanCommand;
import hn.core.command.commands.CoreCommand;
import hn.core.command.commands.DeleteHomeCommand;
import hn.core.command.commands.DragonCommand;
import hn.core.command.commands.HomeCommand;
import hn.core.command.commands.HomesCommand;
import hn.core.command.commands.MuteCommand;
import hn.core.command.commands.PingCommand;
import hn.core.command.commands.RankCommand;
import hn.core.command.commands.SetHomeCommand;
import hn.core.command.commands.SetSpawnCommand;
import hn.core.command.commands.SetWarpCommand;
import hn.core.command.commands.ShrugCommand;
import hn.core.command.commands.SpawnCommand;
import hn.core.command.commands.WarpCommand;
import hn.core.command.commands.WarpsCommand;
import hn.core.command.commands.WildCommand;
import hn.core.events.dragon.Dragon;
import hn.core.listeners.PlayerListeners;
import hn.core.rank.PlayerDetails;
import hn.core.rank.Rank;
import hn.core.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import net.minecraft.server.v1_13_R2.NBTTagDouble;
import net.minecraft.server.v1_13_R2.NBTTagInt;
import net.minecraft.server.v1_13_R2.NBTTagList;
import net.minecraft.server.v1_13_R2.NBTTagString;

public class Core extends JavaPlugin {

	public static Core instance;
	public File configf, playerdataf, punishmentsf, ranksf;
	public FileConfiguration config, playerdata, punishments, ranks;
	public CommandFactory cf;
	private ArrayList<UUID> afk = new ArrayList<UUID>();
	public Map<UUID, PlayerDetails> pds;
	public Map<UUID, Location> back = new HashMap<UUID, Location>();
	public Map<UUID, Date> backCooldown = new HashMap<UUID, Date>();
	public Map<UUID, Date> homeCooldown = new HashMap<UUID, Date>();
	public Map<UUID, Date> wildCooldown = new HashMap<UUID, Date>();
	public Map<UUID, Dragon> dragons = new HashMap<UUID, Dragon>();
	private int announcement;
	public int cltimer = 300;

	@Override
	public void onEnable()
	{
		instance = this;
		cf = new CommandFactory();
		pds = new HashMap<UUID, PlayerDetails>();

		cf.registerCommand(new AFKCommand());
		cf.registerCommand(new BackCommand());
		cf.registerCommand(new BanCommand());
		cf.registerCommand(new CoreCommand());
		cf.registerCommand(new DeleteHomeCommand());
		cf.registerCommand(new DragonCommand());
		cf.registerCommand(new HomeCommand());
		cf.registerCommand(new HomesCommand());
		cf.registerCommand(new MuteCommand());
		cf.registerCommand(new PingCommand());
		cf.registerCommand(new RankCommand());
		cf.registerCommand(new SetHomeCommand());
		cf.registerCommand(new SetSpawnCommand());
		cf.registerCommand(new SetWarpCommand());
		cf.registerCommand(new ShrugCommand());
		cf.registerCommand(new SpawnCommand());
		cf.registerCommand(new WarpCommand());
		cf.registerCommand(new WarpsCommand());
		cf.registerCommand(new WildCommand());

		createConfigs();

		Bukkit.getPluginManager().registerEvents(new PlayerListeners(), this);

		announcement = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run()
			{
				List<String> announcements = Core.getInstance().config.getStringList("announcements");
				for (Player player : Bukkit.getOnlinePlayers())
					player.sendMessage(Utils.serverName + ChatColor.WHITE + ">> " + announcements.get(new Random().nextInt(announcements.size())));
			}
		}, 0, 12000);

	}

	@Override
	public void onDisable()
	{

		Bukkit.getScheduler().cancelTask(announcement);

		pds = null;
		cf = null;
		instance = null;
	}

	public static Core getInstance()
	{
		return instance;
	}

	public void createConfigs()
	{
		configf = new File(getDataFolder(), "config.yml");
		playerdataf = new File(getDataFolder(), "playerdata.yml");
		punishmentsf = new File(getDataFolder(), "punishments.yml");
		ranksf = new File(getDataFolder(), "ranks.yml");

		if (!configf.exists())
		{
			configf.getParentFile().mkdirs();
			saveResource("config.yml", false);
		}
		if (!playerdataf.exists())
		{
			playerdataf.getParentFile().mkdirs();
			saveResource("playerdata.yml", false);
		}
		if (!punishmentsf.exists())
		{
			punishmentsf.getParentFile().mkdirs();
			saveResource("punishments.yml", false);
		}
		if (!ranksf.exists())
		{
			ranksf.getParentFile().mkdirs();
			saveResource("ranks.yml", false);
		}

		config = new YamlConfiguration();
		playerdata = new YamlConfiguration();
		punishments = new YamlConfiguration();
		ranks = new YamlConfiguration();
		try
		{
			config.load(configf);
			playerdata.load(playerdataf);
			punishments.load(punishmentsf);
			ranks.load(ranksf);
		} catch (IOException | InvalidConfigurationException e)
		{
			e.printStackTrace();
		}
	}

	public void reloadConfig()
	{
		config = null;
		playerdata = null;
		punishments = null;
		ranks = null;

		configf = null;
		playerdataf = null;
		punishmentsf = null;
		ranksf = null;

		createConfigs();
	}

	@Override
	public void saveConfig()
	{
		try
		{
			config.save(configf);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void savePlayerdata()
	{
		try
		{
			playerdata.save(playerdataf);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void savePunishments()
	{
		try
		{
			punishments.save(punishmentsf);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public Rank getRank(UUID uniqueId)
	{
		return Rank.valueOf(playerdata.getString(uniqueId + ".rank").toUpperCase());
	}

	public void setRank(UUID uniqueId, Rank rank)
	{
		playerdata.set(uniqueId + ".rank", rank.toString());
		savePlayerdata();
	}

	public void setAFK(UUID uuid, boolean afk, boolean message)
	{
		if (afk)
		{
			this.afk.add(uuid);
			Bukkit.getPlayer(uuid).setPlayerListName(
					getRank(uuid).getRankString() + Bukkit.getPlayer(uuid).getDisplayName() + " " + ChatColor.GOLD + ChatColor.BOLD + "AFK");
			if (message)
			{
				System.out.println(getRank(uuid).getRankString() + Bukkit.getPlayer(uuid).getDisplayName() + ChatColor.WHITE + " has gone AFK!");
				for (Player player : Bukkit.getOnlinePlayers())
					player.sendMessage(getRank(uuid).getRankString() + Bukkit.getPlayer(uuid).getDisplayName() + ChatColor.WHITE + " has gone AFK!");
			}
		} else
		{
			this.afk.remove(uuid);
			Bukkit.getPlayer(uuid).setPlayerListName(getRank(uuid).getRankString() + Bukkit.getPlayer(uuid).getDisplayName());
			if (message)
			{
				System.out.println(getRank(uuid).getRankString() + Bukkit.getPlayer(uuid).getDisplayName() + ChatColor.WHITE + " is no longer AFK!");
				for (Player player : Bukkit.getOnlinePlayers())
					player.sendMessage(
							getRank(uuid).getRankString() + Bukkit.getPlayer(uuid).getDisplayName() + ChatColor.WHITE + " is no longer AFK!");
			}
		}
	}

	public boolean isAFK(UUID uuid)
	{
		if (this.afk.contains(uuid))
			return true;
		return false;
	}

	public ItemStack getKeyItem()
	{
		ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Dragon Slayer Sword");

		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.DARK_BLUE + "A sword capable of slaying Gods,");
		lore.add(ChatColor.DARK_BLUE + "it was forged in the void with Nether");
		lore.add(ChatColor.DARK_BLUE + "Stars with techniques long forgotten.");
		meta.setLore(lore);

		meta.addEnchant(Enchantment.DAMAGE_ALL, 10, true);
		meta.addEnchant(Enchantment.SWEEPING_EDGE, 5, true);
		meta.addEnchant(Enchantment.KNOCKBACK, 4, true);
		meta.addEnchant(Enchantment.DURABILITY, 10, true);

		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

		item.setItemMeta(meta);

		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();

		NBTTagList modifiers = new NBTTagList();
		{
			NBTTagCompound attackSpeed = new NBTTagCompound();
			attackSpeed.set("AttributeName", new NBTTagString("generic.attackSpeed"));
			attackSpeed.set("Name", new NBTTagString("generic.attackSpeed"));
			attackSpeed.set("Amount", new NBTTagDouble(1.4));
			attackSpeed.set("Operation", new NBTTagInt(0));
			attackSpeed.set("UUIDLeast", new NBTTagInt(894654));
			attackSpeed.set("UUIDMost", new NBTTagInt(2872));
			attackSpeed.set("Slot", new NBTTagString("mainhand"));
			modifiers.add(attackSpeed);
			
			NBTTagCompound speed = new NBTTagCompound();
			speed.set("AttributeName", new NBTTagString("generic.movementSpeed"));
			speed.set("Name", new NBTTagString("generic.movementSpeed"));
			speed.set("Amount", new NBTTagDouble(0.2));
			speed.set("Operation", new NBTTagInt(0));
			speed.set("UUIDLeast", new NBTTagInt(894654));
			speed.set("UUIDMost", new NBTTagInt(2872));
			speed.set("Slot", new NBTTagString("mainhand"));
			modifiers.add(speed);

			NBTTagCompound knockbackResistance = new NBTTagCompound();
			knockbackResistance.set("AttributeName", new NBTTagString("generic.knockbackResistance"));
			knockbackResistance.set("Name", new NBTTagString("generic.knockbackResistance"));
			knockbackResistance.set("Amount", new NBTTagDouble(0.5));
			knockbackResistance.set("Operation", new NBTTagInt(0));
			knockbackResistance.set("UUIDLeast", new NBTTagInt(894654));
			knockbackResistance.set("UUIDMost", new NBTTagInt(2872));
			knockbackResistance.set("Slot", new NBTTagString("mainhand"));
			modifiers.add(knockbackResistance);
		}
		nmsStack.setTag(compound);
		item = CraftItemStack.asBukkitCopy(nmsStack);

		return item;
	}

}
