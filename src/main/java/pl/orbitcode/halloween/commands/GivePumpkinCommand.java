/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package pl.orbitcode.halloween.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.orbitcode.halloween.HalloweenPlugin;

public class GivePumpkinCommand
implements CommandExecutor {
    private final HalloweenPlugin plugin;

    public GivePumpkinCommand(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("halloween.admin")) {
            sender.sendMessage(String.valueOf(ChatColor.RED) + "Brak permisji.");
            return true;
        }
        if (args.length < 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("U\u017cyj: /givepumpkin <gracz> [ilosc] [poziom]");
                return true;
            }
            int amount = args.length >= 2 ? this.parseInt(args[1], 1) : 1;
            int lvl = args.length >= 3 ? this.parseInt(args[2], 1) : 1;
            lvl = Math.max(1, Math.min(3, lvl));
            ItemStack it = this.plugin.getRewardManager().createPumpkin(lvl, amount);
            ((Player)sender).getInventory().addItem(new ItemStack[]{it});
            sender.sendMessage(this.color(this.plugin.getConfig().getString("prefix", "") + " &aOtrzyma\u0142e\u015b &e" + amount + "x Lv" + lvl));
            return true;
        }
        Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(this.color("&cGracz offline"));
            return true;
        }
        int amount = args.length >= 2 ? this.parseInt(args[1], 1) : 1;
        int lvl = args.length >= 3 ? this.parseInt(args[2], 1) : 1;
        lvl = Math.max(1, Math.min(3, lvl));
        ItemStack it = this.plugin.getRewardManager().createPumpkin(lvl, amount);
        target.getInventory().addItem(new ItemStack[]{it});
        sender.sendMessage(this.color(this.plugin.getConfig().getString("prefix", "") + " &aDano &e" + amount + "x Lv" + lvl + " &agraczowi &e" + target.getName()));
        target.sendMessage(this.color(this.plugin.getConfig().getString("prefix", "") + " &aOtrzyma\u0142e\u015b &e" + amount + "x Straszna Dynia Lv" + lvl));
        return true;
    }

    private int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        }
        catch (Exception e) {
            return def;
        }
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)s);
    }
}

