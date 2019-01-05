package hn.core.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.SkullMeta;

import hn.core.Core;
import hn.core.util.CoreUI;
import hn.core.util.Utils;
import net.md_5.bungee.api.ChatColor;

public class CoreCommandListener implements Listener {

	private CoreUI CUI = new CoreUI();

	public ArrayList<UUID> newAnnouncement = new ArrayList<UUID>();

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		if (event.getClickedInventory() == null || !Arrays.asList(CUI.inventoryNames).contains(event.getInventory().getName()))
			return;

		if (!event.getCurrentItem().hasItemMeta())
		{
			event.setCancelled(true);
			return;
		}

		if (event.getClickedInventory().getName().equals(CUI.inventoryNames[0]))
		{
			if (event.getClickedInventory().getItem(37).equals(event.getCurrentItem()))
				event.getWhoClicked().openInventory(CUI.createInventoryConfigs());
			if (event.getClickedInventory().getItem(20).equals(event.getCurrentItem()))
				event.getWhoClicked().openInventory(CUI.createInventoryPlayers());
		}

		else if (event.getClickedInventory().getName().equals(CUI.inventoryNames[1]))
		{
			if (event.getClickedInventory().getItem(22).equals(event.getCurrentItem()))
				event.getWhoClicked().openInventory(CUI.createInventoryCore());

			else if (event.getClickedInventory().getItem(10).equals(event.getCurrentItem()))
				Core.getInstance().reloadConfig();

			else if (event.getClickedInventory().getItem(12).equals(event.getCurrentItem()))
				event.getWhoClicked().openInventory(CUI.createInventoryAnnouncements());
		}

		else if (event.getClickedInventory().getName().equals(CUI.inventoryNames[2]))
		{
			if (event.getClickedInventory().getItem(49).equals(event.getCurrentItem()))
				event.getWhoClicked().openInventory(CUI.createInventoryConfigs());

			if (event.getCurrentItem().getType() == Material.WRITTEN_BOOK && event.isRightClick() && event.isShiftClick())
			{
				List<String> list = Core.getInstance().config.getStringList("announcements");
				for (int i = 0; i < 28; i++)
					if (10 + (i % 7) + (9 * (i / 7)) == event.getSlot())
					{
						list.remove(i);
						break;
					}
				Core.getInstance().config.set("announcements", list);
				Core.getInstance().saveConfig();
				event.getWhoClicked().openInventory(CUI.createInventoryAnnouncements());
			}

			if (event.getCurrentItem().getType() == Material.WRITABLE_BOOK)
			{
				newAnnouncement.add(event.getWhoClicked().getUniqueId());
				event.getWhoClicked().closeInventory();
				event.getWhoClicked().sendMessage(Utils.serverName + ChatColor.WHITE + ">> Type the new announcement");
			}
		} else if (event.getClickedInventory().getName().equals(CUI.inventoryNames[3]))
		{
			if (event.getClickedInventory().getItem(49).equals(event.getCurrentItem()))
				event.getWhoClicked().openInventory(CUI.createInventoryCore());

			if (event.getCurrentItem().getType() == Material.PLAYER_HEAD)
			{
				event.getWhoClicked()
						.openInventory(CUI.createInventoryPlayerSettings(((SkullMeta) event.getCurrentItem().getItemMeta()).getOwningPlayer()));
			}
		} else if (event.getClickedInventory().getName().equals(CUI.inventoryNames[4]))
		{
			if (event.getClickedInventory().getItem(49).equals(event.getCurrentItem()))
				event.getWhoClicked().openInventory(CUI.createInventoryPlayers());
		}

		event.setCancelled(true);

	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event)
	{
		if (newAnnouncement.contains(event.getPlayer().getUniqueId()))
			newAnnouncement.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event)
	{
		if (!newAnnouncement.contains(event.getPlayer().getUniqueId()))
			return;

		newAnnouncement.remove(event.getPlayer().getUniqueId());
		List<String> announcements = Core.getInstance().config.getStringList("announcements");
		announcements.add(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
		Core.getInstance().config.set("announcements", announcements);
		Core.getInstance().saveConfig();

		event.setCancelled(true);
	}

}
