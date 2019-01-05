package hn.core.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import hn.core.Core;
import net.md_5.bungee.api.ChatColor;

public class CoreUI {

	public String[] inventoryNames = new String[]
	{ Utils.serverName + ">> Core", Utils.serverName + ">> Configs",
			Utils.serverName + ">> Announcements", Utils.serverName + ">> Players",
			Utils.serverName + ">> Player Settings" };

	public ItemStack getBackArrow()
	{
		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(ChatColor.WHITE + "Back");
		back.setItemMeta(backMeta);

		return back;
	}

	public Inventory createInventoryCore()
	{
		Inventory core = Bukkit.createInventory(null, 54, inventoryNames[0]);

		ItemStack configs = new ItemStack(Material.NAME_TAG);
		ItemMeta configsMeta = configs.getItemMeta();
		configsMeta.setDisplayName(ChatColor.WHITE + "Configs");
		List<String> configsLore = new ArrayList<String>();
		configsLore.add("Adjust config settings, like rank");
		configsLore.add("perms, announcements and the MOTD");
		configsMeta.setLore(configsLore);
		configs.setItemMeta(configsMeta);
		core.setItem(37, configs);

		ItemStack playersettings = new ItemStack(Material.PLAYER_HEAD);
		ItemMeta playersettingsMeta = playersettings.getItemMeta();
		playersettingsMeta.setDisplayName(ChatColor.WHITE + "Player Settings");
		List<String> playersettingsLore = new ArrayList<String>();
		playersettingsLore.add("Adjust player settings, like bans");
		playersettingsLore.add("mutes, ranks and maybe claims");
		playersettingsMeta.setLore(playersettingsLore);
		playersettings.setItemMeta(playersettingsMeta);
		core.setItem(20, playersettings);

		return core;
	}

	public Inventory createInventoryConfigs()
	{
		Inventory configs = Bukkit.createInventory(null, 27, inventoryNames[1]);

		configs.setItem(22, getBackArrow());

		ItemStack reloadconfigs = new ItemStack(Material.REDSTONE_BLOCK);
		ItemMeta reloadconfigsMeta = reloadconfigs.getItemMeta();
		reloadconfigsMeta.setDisplayName(ChatColor.WHITE + "Reload Configs");
		List<String> reloadconfigsLore = new ArrayList<String>();
		reloadconfigsLore.add("Reloads the config files which");
		reloadconfigsLore.add("lets manual changes take effect");
		reloadconfigsMeta.setLore(reloadconfigsLore);
		reloadconfigs.setItemMeta(reloadconfigsMeta);
		configs.setItem(10, reloadconfigs);

		ItemStack announcements = new ItemStack(Material.WRITABLE_BOOK);
		ItemMeta announcementsMeta = announcements.getItemMeta();
		announcementsMeta.setDisplayName(ChatColor.WHITE + "Announcements");
		List<String> announcementsLore = new ArrayList<String>();
		announcementsLore.add("Edit the announcements that");
		announcementsLore.add("come up every so often");
		announcementsMeta.setLore(announcementsLore);
		announcements.setItemMeta(announcementsMeta);
		configs.setItem(12, announcements);

		return configs;
	}

	public Inventory createInventoryAnnouncements()
	{
		Inventory announcements = Bukkit.createInventory(null, 54, inventoryNames[2]);

		announcements.setItem(49, getBackArrow());

		List<String> list = Core.getInstance().config.getStringList("announcements");
		for (int i = 0; i < 28; i++)
		{
			ItemStack ann = i < list.size() ? new ItemStack(Material.WRITTEN_BOOK) : new ItemStack(Material.WRITABLE_BOOK);
			ItemMeta annMeta = ann.getItemMeta();
			annMeta.setDisplayName(i < list.size() ? ChatColor.WHITE + list.get(i) : ChatColor.WHITE + "New Announcement");
			List<String> lore = new ArrayList<String>();
			lore.add(i < list.size() ? "Shift-Right click" : "Click to add a");
			lore.add(i < list.size() ? "to delete" : "new announcement");
			annMeta.setLore(lore);
			ann.setItemMeta(annMeta);
			announcements.setItem(10 + (i % 7) + (9 * (i / 7)), ann);
		}

		return announcements;
	}

	public Inventory createInventoryPlayers()
	{
		Inventory players = Bukkit.createInventory(null, 54, inventoryNames[3]);

		players.setItem(49, getBackArrow());

		int count = 0;
		for (OfflinePlayer p : Bukkit.getOfflinePlayers())
		{
			if (count == 45)
				break;
			ItemStack pl = new ItemStack(Material.PLAYER_HEAD);
			SkullMeta plMeta = (SkullMeta) pl.getItemMeta();
			plMeta.setDisplayName(ChatColor.WHITE + p.getName());
			plMeta.setOwningPlayer(p);
			List<String> lore = new ArrayList<String>();
			lore.add("");
			lore.add("");
			plMeta.setLore(lore);
			pl.setItemMeta(plMeta);
			players.setItem(count, pl);
			count++;
		}

		return players;
	}

	public Inventory createInventoryPlayerSettings(OfflinePlayer player)
	{
		Inventory players = Bukkit.createInventory(null, 54, inventoryNames[4]);

		players.setItem(49, getBackArrow());

		ItemStack pl = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta plMeta = (SkullMeta) pl.getItemMeta();
		plMeta.setDisplayName(ChatColor.WHITE + player.getName());
		plMeta.setOwningPlayer(player);
		List<String> lore = new ArrayList<String>();
		lore.add("Modify this player's settings");
		plMeta.setLore(lore);
		pl.setItemMeta(plMeta);
		players.setItem(4, pl);

		return players;
	}

}
