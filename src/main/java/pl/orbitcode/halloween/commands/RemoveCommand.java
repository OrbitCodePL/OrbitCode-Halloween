/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package pl.orbitcode.halloween.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.orbitcode.halloween.HalloweenPlugin;

public class RemoveCommand
implements CommandExecutor {
    private final HalloweenPlugin plugin;

    public RemoveCommand(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean ok;
        Player p;
        if (!sender.hasPermission("halloween.admin")) {
            sender.sendMessage(String.valueOf(ChatColor.RED) + "Brak permisji.");
            return true;
        }
        if (args.length > 0) {
            p = this.plugin.getServer().getPlayer(args[0]);
            if (p == null) {
                sender.sendMessage(this.color("&cGracz offline"));
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage("U\u017cyj: /halloweenremove <gracz>");
                return true;
            }
            p = (Player)sender;
        }
        if (ok = this.plugin.getPumpkinManager().removePumpkin(p.getLocation())) {
            sender.sendMessage(this.color(this.plugin.getConfig().getString("prefix", "") + " &aUsuni\u0119to najbli\u017csz\u0105 ska\u0142k\u0119"));
        } else {
            sender.sendMessage(this.color(this.plugin.getConfig().getString("prefix", "") + " &cNie znaleziono ska\u0142ki w promieniu " + this.plugin.getConfig().getInt("pumpkin-detection-radius", 15)));
        }
        return true;
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)s);
    }
}

