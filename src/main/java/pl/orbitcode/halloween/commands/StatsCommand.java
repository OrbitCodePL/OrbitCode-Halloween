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
import pl.orbitcode.halloween.model.BossPumpkin;

public class StatsCommand
implements CommandExecutor {
    private final HalloweenPlugin plugin;

    public StatsCommand(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player)sender;
            int lv1 = this.plugin.getRewardManager().countPumpkins(p, 1);
            int lv2 = this.plugin.getRewardManager().countPumpkins(p, 2);
            int lv3 = this.plugin.getRewardManager().countPumpkins(p, 3);
            sender.sendMessage(this.color("&8&m        &4&l HALLOWEEN STATS &8&m        "));
            sender.sendMessage(this.color("&7Twoje dynie: &6Lv1: &e" + lv1 + " &7| &5Lv2: &d" + lv2 + " &7| &cLv3: &4" + lv3));
            if (this.plugin.getStatsManager() != null) {
                int rocks = this.plugin.getStatsManager().getDestroyed(p.getUniqueId());
                int mobs = this.plugin.getStatsManager().getKilled(p.getUniqueId());
                int opened = this.plugin.getStatsManager().getOpened(p.getUniqueId());
                sender.sendMessage(this.color("&7Zniszczone ska\u0142ki: &5&l" + rocks + " &8| &7Zabite moby: &c&l" + mobs + " &8| &7Otwarte dynie: &e&l" + opened));
                sender.sendMessage(this.color("&7Placeholdery: &8%halloween_destroyed_rocks% &7%halloween_killed_mobs% &7%halloween_opened_pumpkins%"));
            }
        }
        long active = this.plugin.getPumpkinManager().getAll().stream().filter(bp -> !bp.isDestroyed()).count();
        long destroyed = (long)this.plugin.getPumpkinManager().getAll().size() - active;
        sender.sendMessage(this.color("&7Aktywne ska\u0142ki: &a" + active + " &7| Zniszczone (cooldown): &c" + destroyed));
        sender.sendMessage(this.color("&7World pumpkins limit: &e" + this.plugin.getConfig().getInt("max-world-pumpkins", 10)));
        if (this.plugin.getPumpkinManager().getAll().size() > 0) {
            sender.sendMessage(this.color("&8Lista ska\u0142ek (Jajo Smoka):"));
            for (BossPumpkin bp2 : this.plugin.getPumpkinManager().getAll()) {
                String hp = bp2.isDestroyed() ? "&8[Zniszczona]" : "&c" + bp2.getHealth() + "&7/&c" + bp2.getMaxHealth() + " &4\u2764";
                String defenders = " &7Obro\u0144cy: &e" + bp2.getDefenders().size();
                sender.sendMessage(this.color((bp2.isDestroyed() ? "&c\u2716" : "&a\u2714") + " &5\ud83e\udd5a &7" + bp2.getLocation().getWorld().getName() + " " + bp2.getLocation().getBlockX() + " " + bp2.getLocation().getBlockY() + " " + bp2.getLocation().getBlockZ() + " " + hp + defenders + (bp2.isFighting() ? " &4[WALKA]" : "") + (bp2.isDestroyed() ? " &8[odnawia si\u0119]" : "")));
            }
        }
        return true;
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)s);
    }
}

