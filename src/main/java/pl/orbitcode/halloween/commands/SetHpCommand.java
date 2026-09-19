/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.block.Block
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package pl.orbitcode.halloween.commands;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.orbitcode.halloween.HalloweenPlugin;
import pl.orbitcode.halloween.model.BossPumpkin;

public class SetHpCommand
implements CommandExecutor {
    private final HalloweenPlugin plugin;

    public SetHpCommand(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        int hp;
        if (!sender.hasPermission("halloween.admin")) {
            sender.sendMessage(String.valueOf(ChatColor.RED) + "Brak permisji.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(this.color("&cU\u017cycie: /halloweensethp <hp> [maxHp]"));
            sender.sendMessage(this.color("&7Patrz na ska\u0142k\u0119 lub sta\u0144 w pobli\u017cu (15 blok\u00f3w)"));
            return true;
        }
        int maxHp = -1;
        try {
            hp = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e) {
            sender.sendMessage(this.color("&cNieprawid\u0142owe HP: " + args[0]));
            return true;
        }
        if (args.length >= 2) {
            try {
                maxHp = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e) {
                sender.sendMessage(this.color("&cNieprawid\u0142owe maxHp: " + args[1]));
                return true;
            }
        }
        if (hp < 1 || hp > 100000) {
            sender.sendMessage(this.color("&cHP musi by\u0107 1-100000"));
            return true;
        }
        if (maxHp != -1 && (maxHp < 1 || maxHp > 100000)) {
            sender.sendMessage(this.color("&cmaxHp musi by\u0107 1-100000"));
            return true;
        }
        BossPumpkin bp = null;
        if (sender instanceof Player) {
            Player p = (Player)sender;
            Block target = p.getTargetBlock(null, 6);
            if (target != null) {
                bp = this.plugin.getPumpkinManager().getPumpkin(target.getLocation());
            }
            if (bp == null) {
                bp = this.plugin.getPumpkinManager().getNearest(p.getLocation(), this.plugin.getConfig().getInt("pumpkin-detection-radius", 15));
            }
        }
        if (bp == null) {
            sender.sendMessage(this.color(this.plugin.getConfig().getString("prefix", "") + " &cNie znaleziono ska\u0142ki w pobli\u017cu. Sta\u0144 przy niej i sp\u00f3jrz na ni\u0105."));
            return true;
        }
        int oldHp = bp.getHealth();
        int oldMax = bp.getMaxHealth();
        if (maxHp != -1) {
            this.plugin.getPumpkinManager().setPumpkinHealth(bp, hp, maxHp);
            sender.sendMessage(this.color(this.plugin.getConfig().getString("prefix", "") + " &aUstawiono Ska\u0142k\u0119 &7" + bp.getLocation().getBlockX() + "," + bp.getLocation().getBlockY() + "," + bp.getLocation().getBlockZ() + " &aHP: &c" + hp + "&7/&c" + maxHp + " &8(wcze\u015bniej " + oldHp + "/" + oldMax + ")"));
        } else {
            int newMax = Math.max(bp.getMaxHealth(), hp);
            this.plugin.getPumpkinManager().setPumpkinHealth(bp, hp, newMax);
            sender.sendMessage(this.color(this.plugin.getConfig().getString("prefix", "") + " &aUstawiono Ska\u0142k\u0119 HP: &c" + hp + "&7/&c" + newMax + " &8(wcze\u015bniej " + oldHp + "/" + oldMax + ")"));
        }
        return true;
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)s);
    }
}

