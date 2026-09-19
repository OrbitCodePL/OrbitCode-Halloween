/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package pl.orbitcode.halloween.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.orbitcode.halloween.HalloweenPlugin;

public class PlaceCommand
implements CommandExecutor {
    private final HalloweenPlugin plugin;

    public PlaceCommand(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("halloween.admin")) {
            sender.sendMessage(String.valueOf(ChatColor.RED) + "Brak permisji.");
            return true;
        }
        Player target = null;
        int hp = this.plugin.getConfig().getInt("pumpkin-health", 250);
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("U\u017cyj: /halloweenplace <gracz> [hp]");
                return true;
            }
            target = (Player)sender;
        } else if (args.length == 1) {
            p = this.plugin.getServer().getPlayer(args[0]);
            if (p != null) {
                target = p;
            } else {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("U\u017cyj: /halloweenplace <gracz> [hp]");
                    return true;
                }
                target = (Player)sender;
                try {
                    hp = Integer.parseInt(args[0]);
                }
                catch (NumberFormatException ex) {
                    sender.sendMessage(this.color("&cNieprawid\u0142owe HP: " + args[0]));
                    return true;
                }
            }
        } else {
            p = this.plugin.getServer().getPlayer(args[0]);
            if (p == null) {
                sender.sendMessage(this.color("&cGracz offline: " + args[0]));
                return true;
            }
            target = p;
            try {
                hp = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException ex) {
                sender.sendMessage(this.color("&cNieprawid\u0142owe HP: " + args[1]));
                return true;
            }
        }
        Block b = target.getTargetBlock(null, 5);
        Location loc = b != null && b.getType().isSolid() ? b.getLocation().clone().add(0.0, 1.0, 0.0) : target.getLocation().getBlock().getLocation();
        if (!loc.getBlock().getType().isAir()) {
            loc.add(0.0, 1.0, 0.0);
        }
        this.plugin.getPumpkinManager().placePumpkin(loc, hp);
        sender.sendMessage(this.color(this.plugin.getConfig().getString("prefix", "") + " &aPostawiono Ska\u0142k\u0119 &7" + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " &7HP: &c" + hp));
        if (!sender.equals((Object)target)) {
            target.sendMessage(this.color(this.plugin.getConfig().getString("prefix", "") + " &aPostawiono przy Tobie Ska\u0142k\u0119 &c" + hp + " HP"));
        }
        return true;
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)s);
    }
}

