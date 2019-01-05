package hn.core.util;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_13_R2.PlayerConnection;

public class Utils {

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
	public static final String serverName = ChatColor.DARK_AQUA + "HN" + ChatColor.AQUA + "Server" + ChatColor.WHITE;
	
	public static void sendTabHF(Player player, String header, String footer)
	{

		CraftPlayer craftplayer = (CraftPlayer) player;
		PlayerConnection connection = craftplayer.getHandle().playerConnection;
		IChatBaseComponent headerJSON = ChatSerializer.a("{\"text\": \"" + header + "\"}");
		IChatBaseComponent footerJSON = ChatSerializer.a("{\"text\": \"" + footer + "\"}");
		PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

		try
		{
			Field headerField = packet.getClass().getDeclaredField("header");
			headerField.setAccessible(true);
			headerField.set(packet, headerJSON);
			headerField.setAccessible(!headerField.isAccessible());

			Field footerField = packet.getClass().getDeclaredField("footer");
			footerField.setAccessible(true);
			footerField.set(packet, footerJSON);
			footerField.setAccessible(!footerField.isAccessible());
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		connection.sendPacket(packet);

	}
	
	public static Vector beautifulVector(Location location)
	{
		double pitch = (90 * Math.PI) / 180;
		double yaw = ((location.getYaw() + 90) * Math.PI) / 180;

		double x, y, z;

		x = Math.sin(pitch) * Math.cos(yaw);
		y = Math.sin(pitch) * Math.sin(yaw);
		z = Math.cos(pitch);

		return new Vector(x, z, y);
	}
}
