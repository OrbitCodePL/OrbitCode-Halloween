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

public class OpenCommand
implements CommandExecutor {
    private final HalloweenPlugin plugin;

    public OpenCommand(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Tylko gracz");
            return true;
        }
        Player p = (Player)sender;
        if (p.getInventory().getItemInMainHand() == null) {
            p.sendMessage(this.color("&cTrzymaj dyni\u0119 w r\u0119ce!"));
            return true;
        }
        Integer lvl = this.plugin.getRewardManager().getPumpkinLevel(p.getInventory().getItemInMainHand());
        if (lvl == null) {
            p.sendMessage(this.color(this.plugin.getConfig().getString("prefix", "") + " " + this.plugin.getConfig().getString("message-no-pumpkin", "&cTo nie jest Straszna Dynia!")));
            return true;
        }
        this.plugin.getRewardManager().openPumpkin(p, lvl);
        return true;
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)s);
    }
}

