/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package pl.orbitcode.halloween.commands;

import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.orbitcode.halloween.HalloweenPlugin;
import pl.orbitcode.halloween.util.RewardManager;

public class DropEditCommand
implements CommandExecutor {
    private final HalloweenPlugin plugin;

    public DropEditCommand(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("halloween.admin")) {
            sender.sendMessage(String.valueOf(ChatColor.RED) + "Brak permisji.");
            return true;
        }
        if (args.length == 0) {
            if (sender instanceof Player) {
                this.plugin.getDropEditorGui().openMain((Player)sender);
                return true;
            }
            this.sendHelp(sender);
            return true;
        }
        String first = args[0].toLowerCase();
        if (first.equals("gui") || first.equals("menu") || first.equals("edit") || first.equals("edycja")) {
            int t;
            if (!(sender instanceof Player)) {
                sender.sendMessage("Tylko gracz");
                return true;
            }
            if (args.length >= 2 && (t = this.parseTier(args[1])) != -1) {
                this.plugin.getDropEditorGui().openTier((Player)sender, t);
                return true;
            }
            this.plugin.getDropEditorGui().openMain((Player)sender);
            return true;
        }
        String sub = args[0].toLowerCase();
        if (sub.equals("reload")) {
            this.plugin.reloadConfig();
            sender.sendMessage(this.color(this.plugin.getConfig().getString("prefix", "") + " &aPrze\u0142adowano config.yml"));
            return true;
        }
        if (sub.equals("list")) {
            if (args.length < 2) {
                sender.sendMessage(this.color("&cU\u017cyj: /halloweendrop list <1|2|3>"));
                return true;
            }
            int tier = this.parseTier(args[1]);
            if (tier == -1) {
                sender.sendMessage(this.color("&cTier musi by\u0107 1,2 lub 3"));
                return true;
            }
            this.listTier(sender, tier);
            return true;
        }
        if (sub.equals("clear")) {
            if (args.length < 2) {
                sender.sendMessage(this.color("&cU\u017cyj: /halloweendrop clear <1|2|3>"));
                return true;
            }
            int tier = this.parseTier(args[1]);
            if (tier == -1) {
                sender.sendMessage(this.color("&cTier musi by\u0107 1,2 lub 3"));
                return true;
            }
            String path = "custom-rewards.lv" + tier;
            this.plugin.getConfig().set(path, null);
            this.plugin.saveConfig();
            sender.sendMessage(this.color(this.plugin.getConfig().getString("prefix", "") + " &aWyczyszczono custom dropy dla Lv" + tier));
            return true;
        }
        if (sub.equals("add")) {
            int weight;
            if (!(sender instanceof Player)) {
                sender.sendMessage("Tylko gracz");
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage(this.color("&cU\u017cyj: /halloweendrop add <1|2|3> <waga/procent>"));
                return true;
            }
            int tier = this.parseTier(args[1]);
            if (tier == -1) {
                sender.sendMessage(this.color("&cTier musi by\u0107 1,2 lub 3"));
                return true;
            }
            try {
                weight = Integer.parseInt(args[2]);
            }
            catch (NumberFormatException e) {
                sender.sendMessage(this.color("&cWaga musi by\u0107 liczb\u0105"));
                return true;
            }
            Player p = (Player)sender;
            ItemStack hand = p.getInventory().getItemInMainHand();
            if (hand == null || hand.getType() == Material.AIR) {
                sender.sendMessage(this.color("&cTrzymaj przedmiot w r\u0119ce kt\u00f3ry chcesz doda\u0107 jako drop!"));
                return true;
            }
            if (this.plugin.getRewardManager().isPumpkin(hand)) {
                sender.sendMessage(this.color("&cNie mo\u017cesz doda\u0107 Strasznej Dyni jako jej w\u0142asny drop!"));
                return true;
            }
            this.plugin.getRewardManager().addCustomReward(tier, hand.clone(), weight);
            sender.sendMessage(this.color(this.plugin.getConfig().getString("prefix", "") + " &aDodano custom drop &e" + String.valueOf(hand.getType()) + " &7(&f" + (hand.hasItemMeta() && hand.getItemMeta().hasDisplayName() ? hand.getItemMeta().getDisplayName() : hand.getType().name()) + "&7) &ado Lv" + tier + " z wag\u0105 &e" + weight));
            this.listTier(sender, tier);
            return true;
        }
        if (sub.equals("remove") || sub.equals("delete") || sub.equals("usuwanie")) {
            boolean ok;
            if (args.length < 3) {
                sender.sendMessage(this.color("&cU\u017cyj: /halloweendrop remove <1|2|3> <nazwa/id/material>"));
                return true;
            }
            int tier = this.parseTier(args[1]);
            if (tier == -1) {
                sender.sendMessage(this.color("&cTier musi by\u0107 1,2 lub 3"));
                return true;
            }
            String key = args[2];
            if (args.length > 3) {
                StringBuilder sb = new StringBuilder(key);
                for (int i = 3; i < args.length; ++i) {
                    sb.append(" ").append(args[i]);
                }
                key = sb.toString();
            }
            if (ok = this.plugin.getRewardManager().removeReward(tier, key)) {
                sender.sendMessage(this.color(this.plugin.getConfig().getString("prefix", "") + " &aUsuni\u0119to &e" + key + " &az Lv" + tier));
            } else {
                sender.sendMessage(this.color("&cNie znaleziono '" + key + "' w Lv" + tier + " (sprawd\u017a /halloweendrop list " + tier + ")"));
            }
            return true;
        }
        if (sub.equals("set") || sub.equals("setchance") || sub.equals("setweight") || sub.equals("ustaw")) {
            boolean ok;
            int weight;
            if (args.length < 4) {
                sender.sendMessage(this.color("&cU\u017cyj: /halloweendrop set <1|2|3> <nazwa/id> <waga/procent>"));
                return true;
            }
            int tier = this.parseTier(args[1]);
            if (tier == -1) {
                sender.sendMessage(this.color("&cTier musi by\u0107 1,2 lub 3"));
                return true;
            }
            String key = args[2];
            try {
                weight = Integer.parseInt(args[3]);
            }
            catch (NumberFormatException e) {
                sender.sendMessage(this.color("&cWaga musi by\u0107 liczb\u0105"));
                return true;
            }
            if (args.length > 4) {
                try {
                    weight = Integer.parseInt(args[args.length - 1]);
                }
                catch (NumberFormatException ex) {
                    sender.sendMessage(this.color("&cOstatni argument musi by\u0107 wag\u0105"));
                    return true;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 2; i < args.length - 1; ++i) {
                    if (i > 2) {
                        sb.append(" ");
                    }
                    sb.append(args[i]);
                }
                key = sb.toString();
            }
            if (ok = this.plugin.getRewardManager().setRewardWeight(tier, key, weight)) {
                sender.sendMessage(this.color(this.plugin.getConfig().getString("prefix", "") + " &aUstawiono wag\u0119 &e" + key + " &ana &e" + weight + " &aw Lv" + tier));
            } else {
                sender.sendMessage(this.color("&cNie znaleziono '" + key + "' w Lv" + tier));
            }
            return true;
        }
        this.sendHelp(sender);
        return true;
    }

    private void listTier(CommandSender sender, int tier) {
        RewardManager rm = this.plugin.getRewardManager();
        Map<String, Integer> standard = rm.getStandardWeights(tier);
        Map<String, RewardManager.WeightedEntry> custom = rm.getCustomEntries(tier);
        int total = rm.getTotalWeight(tier);
        sender.sendMessage(this.color("&8&m        &4&l DROP LV" + tier + " &8&m        "));
        if (total == 0) {
            sender.sendMessage(this.color("&cBrak wpis\u00f3w!"));
        } else {
            sender.sendMessage(this.color("&7Suma wag: &e" + total + " &7(100% = " + total + ")"));
        }
        if (!standard.isEmpty()) {
            sender.sendMessage(this.color("&6Standardowe:"));
            for (Map.Entry<String, Integer> e : standard.entrySet()) {
                double pct = total > 0 ? (double)e.getValue().intValue() * 100.0 / (double)total : 0.0;
                sender.sendMessage(this.color(" &8- &e" + e.getKey() + " &7waga &a" + String.valueOf(e.getValue()) + " &7(" + String.format("%.1f", pct) + "%)"));
            }
        }
        if (!custom.isEmpty()) {
            sender.sendMessage(this.color("&dCustom (przedmiot w r\u0119ce):"));
            int idx = 0;
            for (Map.Entry<String, RewardManager.WeightedEntry> e : custom.entrySet()) {
                double pct = total > 0 ? (double)e.getValue().weight * 100.0 / (double)total : 0.0;
                ItemStack it = e.getValue().item;
                String name = it == null ? "null" : (it.hasItemMeta() && it.getItemMeta().hasDisplayName() ? it.getItemMeta().getDisplayName() : it.getType().name());
                sender.sendMessage(this.color(" &8- &b[" + idx++ + "] &f" + name + " &7(" + (it == null ? "AIR" : it.getType().name()) + ") &7id:&7" + e.getKey() + " &7waga &a" + e.getValue().weight + " &7(" + String.format("%.1f", pct) + "%)"));
            }
        }
        if (standard.isEmpty() && custom.isEmpty()) {
            sender.sendMessage(this.color("&7Brak drop\u00f3w. Dodaj via &e/halloweendrop add <tier> <waga> &7(trzymaj\u0105c item)"));
        }
        sender.sendMessage(this.color("&7Edycja: &e/halloweendrop set <tier> <nazwa/id> <waga> &7| &e/halloweendrop remove <tier> <nazwa/id>"));
    }

    private int parseTier(String s) {
        try {
            int v = Integer.parseInt(s);
            if (v >= 1 && v <= 3) {
                return v;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return -1;
    }

    private void sendHelp(CommandSender s) {
        s.sendMessage(this.color("&8&m        &4&l HALLOWEENDROP &8&m        "));
        s.sendMessage(this.color("&a&lGUI: &6/halloweendrop &7lub &6/halloweendrop gui &7- edytor (wsadz przedmiot -> tabliczka % )"));
        s.sendMessage(this.color("&6/halloweendrop gui <1|2|3> &7- bezposrednio tier I/II/III"));
        s.sendMessage(this.color("&8--- Komendy tekstowe (legacy) ---"));
        s.sendMessage(this.color("&6/halloweendrop list <1|2|3> &7- lista drop\u00f3w"));
        s.sendMessage(this.color("&6/halloweendrop add <1|2|3> <waga> &7- dodaj przedmiot z r\u0119ki"));
        s.sendMessage(this.color("&6/halloweendrop set <1|2|3> <nazwa/id> <waga> &7- ustaw wag\u0119"));
        s.sendMessage(this.color("&6/halloweendrop remove <1|2|3> <nazwa/id> &7- usu\u0144 drop"));
        s.sendMessage(this.color("&6/halloweendrop clear <1|2|3> &7- wyczy\u015b\u0107 tier"));
        s.sendMessage(this.color("&6/halloweendrop reload &7- prze\u0142aduj config"));
        s.sendMessage(this.color("&7W GUI: &eWsad\u017a przedmiot do INPUT &7\u2192 klik &aZATWIERD\u0179 &7\u2192 wpisz &e% &7na tabliczce (np. &e5.5 &7lub &e5,5%)"));
        s.sendMessage(this.color("&7Waga = szansa wzgl\u0119dna. Procent = waga/suma*100% - GUI przelicza automatycznie"));
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)s);
    }
}

