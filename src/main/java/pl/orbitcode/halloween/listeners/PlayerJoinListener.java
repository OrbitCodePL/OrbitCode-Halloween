/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 */
package pl.orbitcode.halloween.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.orbitcode.halloween.HalloweenPlugin;

public class PlayerJoinListener
implements Listener {
    private final HalloweenPlugin plugin;

    public PlayerJoinListener(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (this.plugin.getConfig().getBoolean("join-message-enabled", false)) {
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes((char)'&', (String)(this.plugin.getConfig().getString("prefix", "") + " &7Witaj! Event Halloween trwa! U\u017cyj &e/exchange")));
        }
    }
}

