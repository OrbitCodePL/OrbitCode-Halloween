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

public class HelpCommand
implements CommandExecutor {
    private final HalloweenPlugin plugin;

    public HelpCommand(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(this.color("&8&m            &4&l HALLOWEEN &8&m            "));
        sender.sendMessage(this.color("&6/halloweenplace [gracz] [hp] &7- postaw ska\u0142k\u0119 &5[Jajo Smoka] &7(domy\u015blnie 250HP)"));
        sender.sendMessage(this.color("&6/halloweenremove [gracz] &7- usu\u0144 ska\u0142k\u0119"));
        sender.sendMessage(this.color("&6/halloweensethp <hp> [maxHp] &7- ustaw HP ska\u0142ki na kt\u00f3r\u0105 patrzysz"));
        sender.sendMessage(this.color("&6/halloweenrespawn &7- odnow wszystkie ska\u0142ki"));
        sender.sendMessage(this.color("&6/givepumpkin <gracz> <ilosc> <poziom> &7- daj dyni\u0119"));
        sender.sendMessage(this.color("&6/halloweenstats &7- statystyki (HP ska\u0142ek + obro\u0144cy + Twoje: ska\u0142ki/moby/dynie)"));
        sender.sendMessage(this.color("&6/halloweenopen &7- otw\u00f3rz dyni\u0119 z r\u0119ki"));
        sender.sendMessage(this.color("&6/exchange &7- wymiana dy\u0144 I->II->III"));
        sender.sendMessage(this.color("&6/halloweendrop list <1|2|3> &7- lista drop\u00f3w z dyni per tier"));
        sender.sendMessage(this.color("&6/halloweendrop add <1|2|3> <waga> &7- dodaj custom drop (przedmiot w r\u0119ce)"));
        sender.sendMessage(this.color("&6/halloweendrop set <1|2|3> <nazwa/id> <waga> &7- ustaw wag\u0119/procent"));
        sender.sendMessage(this.color("&6/halloweendrop remove <1|2|3> <nazwa/id> &7- usu\u0144 drop &8| &6/halloweendrop reload"));
        sender.sendMessage(this.color("&8Ska\u0142ka: &5Jajo Smoka &7ma &c250 HP &7(bij aby zadawa\u0107 &e10 HP&7/hit), hologram bez paska tylko &cHP \u2764 &7+ efekty &5portal/dragon breath/flame"));
        sender.sendMessage(this.color("&8Moby: &7zale\u017cnie od typu r\u00f3\u017cne szanse na dyni\u0119 (ZOMBIE 30%, SPIDER 15%, WITHER_SKELETON 40%) + wagi Lv1/Lv2/Lv3"));
        sender.sendMessage(this.color("&8Drop ze ska\u0142ki: &72-4 dynie (60% Lv1/30% Lv2/10% Lv3) + 30% bonus Lv3 reward"));
        sender.sendMessage(this.color("&8Wymiana: &75x I -> II, 3x II -> III, 5x III -> nagroda"));
        sender.sendMessage(this.color("&8Placeholdery: &7%halloween_destroyed_rocks% %halloween_killed_mobs% %halloween_opened_pumpkins% (oraz aliases _zniszczone_skalki, _zabite_moby, _otwarte_dynie)"));
        return true;
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)s);
    }
}

