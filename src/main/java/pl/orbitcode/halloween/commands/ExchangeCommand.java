/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package pl.orbitcode.halloween.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.orbitcode.halloween.HalloweenPlugin;

public class ExchangeCommand
implements CommandExecutor {
    private final HalloweenPlugin plugin;

    public ExchangeCommand(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Tylko gracz");
            return true;
        }
        Player p = (Player)sender;
        this.plugin.getRewardManager().openExchangeGUI(p);
        return true;
    }
}

