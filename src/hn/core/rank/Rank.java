package hn.core.rank;

import java.util.ArrayList;
import java.util.List;

import hn.core.Core;
import net.md_5.bungee.api.ChatColor;

public enum Rank {

	MEMBER('f', 0), DONATOR('e', 1), HELPER('a', 20), MODERATOR('a', 30), ADMIN('a', 50), COOWNER('a', 60), DEVELOPER('a', 90), OWNER('a', 1000);

	private char suffixColour;
	private int weight;

	Rank(char suffixColour, int weight)
	{
		this.suffixColour = suffixColour;
		this.weight = weight;
	}

	public String getSuffixColour()
	{
		return "&" + suffixColour;
	}

	public int getWeight()
	{
		return weight;
	}

	public String getRankString()
	{
		if (Core.getInstance().ranks.contains("groups." + this.toString().toLowerCase() + ".options.prefix"))
		{
			return ChatColor.translateAlternateColorCodes('&',
					Core.getInstance().ranks.getString("groups." + this.toString().toLowerCase() + ".options.prefix"));
		}
		return "";
	}

	public List<String> getPermissions()
	{
		List<String> permissions = new ArrayList<String>();

		if (Core.getInstance().ranks.contains("groups." + this.toString().toLowerCase() + ".inheritance"))
		{
			for (String s : Rank.valueOf(Core.getInstance().ranks.getString("groups." + this.toString().toLowerCase() + ".inheritance")
					.replace("[", "").replace("]", "").toUpperCase()).getPermissions())
			{
				permissions.add(s);
			}
		}

		if (!Core.getInstance().ranks.getStringList("groups." + this.toString().toLowerCase() + ".permissions").isEmpty())
			for (String s : Core.getInstance().ranks.getStringList("groups." + this.toString().toLowerCase() + ".permissions"))
			{
				permissions.add(s);
			}

		return permissions;
	}

}
