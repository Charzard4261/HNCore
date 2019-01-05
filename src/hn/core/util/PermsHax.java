package hn.core.util;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;

import hn.core.Core;

public class PermsHax extends PermissibleBase {
    UUID uuid;

    public PermsHax(Player p) {
        super(p);
        this.uuid = p.getUniqueId();
    }

    public boolean hasPermission(String s) {
        return Core.getInstance().getRank(uuid).getPermissions().contains("*") 
        		|| isOp() 
        		|| (s != null
        		&& (children(s) 
        				|| Core.getInstance().getRank(uuid).getPermissions().contains(s)));
    }

    public boolean hasPermission(Permission p) {
        return hasPermission(p.getName());
    }

    public boolean children(String perm) {
        String[] split = perm.split("\\.");
        if (split.length > 0) {
            String s = split[0];
            for (int i = 1; i < split.length; i++) {
                if (Core.getInstance().getRank(uuid).getPermissions().contains(s + ".*")) return true;
                s += split[i];
            }
        }
        return false;
    }
}