/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.clip.placeholderapi.expansion.PlaceholderExpansion
 *  org.bukkit.entity.Player
 */
package pl.orbitcode.halloween.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import pl.orbitcode.halloween.HalloweenPlugin;
import pl.orbitcode.halloween.manager.StatsManager;

public class HalloweenExpansion
extends PlaceholderExpansion {
    private final HalloweenPlugin plugin;

    public HalloweenExpansion(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    public String getIdentifier() {
        return "halloween";
    }

    public String getAuthor() {
        return "OrbitCode";
    }

    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    public boolean persist() {
        return true;
    }

    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) {
            return "0";
        }
        StatsManager sm = this.plugin.getStatsManager();
        if (sm == null) {
            return "0";
        }
        String p = params.toLowerCase();
        if (p.equals("destroyed_rocks") || p.equals("destroyed") || p.equals("skalki") || p.equals("zniszczone_skalki") || p.equals("zniszczonych_skalek") || p.equals("rocks") || p.equals("skalka")) {
            return String.valueOf(sm.getDestroyed(player.getUniqueId()));
        }
        if (p.equals("killed_mobs") || p.equals("killed") || p.equals("mobs") || p.equals("moby") || p.equals("zabite_moby") || p.equals("zabitych_mobow") || p.equals("zabitych_mob\u00f3w")) {
            return String.valueOf(sm.getKilled(player.getUniqueId()));
        }
        if (p.equals("opened_pumpkins") || p.equals("opened") || p.equals("pumpkins") || p.equals("dynie") || p.equals("otwarte_dynie") || p.equals("otwartych_dyn") || p.equals("otwarte")) {
            return String.valueOf(sm.getOpened(player.getUniqueId()));
        }
        if (p.equals("total_destroyed") || p.equals("total_rocks")) {
            return String.valueOf(sm.getTotalDestroyed());
        }
        if (p.equals("total_killed") || p.equals("total_mobs")) {
            return String.valueOf(sm.getTotalKilled());
        }
        if (p.equals("total_opened") || p.equals("total_pumpkins")) {
            return String.valueOf(sm.getTotalOpened());
        }
        if (p.contains("skal")) {
            return String.valueOf(sm.getDestroyed(player.getUniqueId()));
        }
        if (p.contains("mob")) {
            return String.valueOf(sm.getKilled(player.getUniqueId()));
        }
        if (p.contains("dyn") || p.contains("pumpkin")) {
            return String.valueOf(sm.getOpened(player.getUniqueId()));
        }
        return null;
    }
}

