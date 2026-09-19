/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 */
package pl.orbitcode.halloween.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.orbitcode.halloween.HalloweenPlugin;

public class RespawnCommand
implements CommandExecutor {
    private final HalloweenPlugin plugin;

    public RespawnCommand(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("halloween.admin")) {
            sender.sendMessage(String.valueOf(ChatColor.RED) + "Brak permisji.");
            return true;
        }
        this.plugin.getPumpkinManager().respawnAll();
        sender.sendMessage(ChatColor.translateAlternateColorCodes((char)'&', (String)(this.plugin.getConfig().getString("prefix", "") + " &aOdnowiono wszystkie ska\u0142ki!")));
        return true;
    }
}

