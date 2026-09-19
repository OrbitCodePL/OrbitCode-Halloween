/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.Particle
 *  org.bukkit.Sound
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.persistence.PersistentDataType
 *  org.bukkit.plugin.Plugin
 */
package pl.orbitcode.halloween.util;

import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import pl.orbitcode.halloween.HalloweenPlugin;

public class RewardManager {
    private final HalloweenPlugin plugin;
    private final NamespacedKey levelKey;
    private final NamespacedKey pumpkinKey;
    private final Random random = new Random();

    public RewardManager(HalloweenPlugin plugin) {
        this.plugin = plugin;
        this.levelKey = new NamespacedKey((Plugin)plugin, "pumpkin_level");
        this.pumpkinKey = new NamespacedKey((Plugin)plugin, "halloween_pumpkin");
    }

    public ItemStack createPumpkin(int level, int amount) {
        int modelData;
        String name;
        Material mat = Material.JACK_O_LANTERN;
        ItemStack item = new ItemStack(mat, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        ArrayList<CallSite> lore = new ArrayList<CallSite>();
        if (level == 1) {
            name = String.valueOf(ChatColor.GOLD) + String.valueOf(ChatColor.BOLD) + "Straszna Dynia " + String.valueOf(ChatColor.YELLOW) + "[I]";
            modelData = this.plugin.getConfig().getInt("pumpkin-lv1-model", 2000001);
            lore.add((CallSite)((Object)(String.valueOf(ChatColor.GRAY) + "Poziom 1 - Najpospolitsza")));
            lore.add((CallSite)((Object)(String.valueOf(ChatColor.GRAY) + "Zbierz " + this.plugin.getConfig().getInt("exchange-lv1-to-lv2-amount", 5) + " by wymieni\u0107 na Lv2")));
            lore.add((CallSite)((Object)(String.valueOf(ChatColor.DARK_GRAY) + "PPM - Otw\u00f3rz aby otrzyma\u0107 nagrod\u0119")));
        } else if (level == 2) {
            name = String.valueOf(ChatColor.DARK_PURPLE) + String.valueOf(ChatColor.BOLD) + "Straszna Dynia " + String.valueOf(ChatColor.LIGHT_PURPLE) + "[II]";
            modelData = this.plugin.getConfig().getInt("pumpkin-lv2-model", 2000002);
            lore.add((CallSite)((Object)(String.valueOf(ChatColor.GRAY) + "Poziom 2 - Rzadsza")));
            lore.add((CallSite)((Object)(String.valueOf(ChatColor.GRAY) + "Zbierz " + this.plugin.getConfig().getInt("exchange-lv2-to-lv3-amount", 3) + " by wymieni\u0107 na Lv3")));
            lore.add((CallSite)((Object)(String.valueOf(ChatColor.DARK_GRAY) + "PPM - Otw\u00f3rz aby otrzyma\u0107 nagrod\u0119")));
        } else {
            name = String.valueOf(ChatColor.RED) + String.valueOf(ChatColor.BOLD) + "Straszna Dynia " + String.valueOf(ChatColor.DARK_RED) + "[III]";
            modelData = this.plugin.getConfig().getInt("pumpkin-lv3-model", 2000003);
            lore.add((CallSite)((Object)(String.valueOf(ChatColor.GRAY) + "Poziom 3 - Najrzadsza")));
            lore.add((CallSite)((Object)(String.valueOf(ChatColor.GRAY) + "Zbierz " + this.plugin.getConfig().getInt("exchange-lv3-to-reward-amount", 5) + " by wymieni\u0107 na nagrod\u0119")));
            lore.add((CallSite)((Object)(String.valueOf(ChatColor.DARK_GRAY) + "PPM - Otw\u00f3rz aby otrzyma\u0107 nagrod\u0119")));
        }
        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.setCustomModelData(Integer.valueOf(modelData));
        meta.getPersistentDataContainer().set(this.levelKey, PersistentDataType.INTEGER, (Object)level);
        meta.getPersistentDataContainer().set(this.pumpkinKey, PersistentDataType.BYTE, (Object)1);
        item.setItemMeta(meta);
        return item;
    }

    public Integer getPumpkinLevel(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return null;
        }
        if (!item.hasItemMeta()) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (!meta.getPersistentDataContainer().has(this.pumpkinKey, PersistentDataType.BYTE)) {
            return null;
        }
        return (Integer)meta.getPersistentDataContainer().get(this.levelKey, PersistentDataType.INTEGER);
    }

    public boolean isPumpkin(ItemStack item) {
        return this.getPumpkinLevel(item) != null;
    }

    public int countPumpkins(Player p, int level) {
        int c = 0;
        for (ItemStack it : p.getInventory().getContents()) {
            Integer lv = this.getPumpkinLevel(it);
            if (lv == null || lv != level) continue;
            c += it.getAmount();
        }
        return c;
    }

    public void removePumpkins(Player p, int level, int amount) {
        int toRemove = amount;
        for (int i = 0; i < p.getInventory().getSize(); ++i) {
            ItemStack it = p.getInventory().getItem(i);
            Integer lv = this.getPumpkinLevel(it);
            if (lv == null || lv != level) continue;
            int cur = it.getAmount();
            if (cur <= toRemove) {
                p.getInventory().setItem(i, null);
                toRemove -= cur;
            } else {
                it.setAmount(cur - toRemove);
                toRemove = 0;
            }
            if (toRemove <= 0) break;
        }
        p.updateInventory();
    }

    public String getExchangeResult(int level) {
        return level == 1 ? "Lv2" : (level == 2 ? "Lv3" : "Nagroda");
    }

    public void openPumpkin(Player player, int level) {
        Integer has = this.countPumpkins(player, level);
        if (has < 1) {
            player.sendMessage(this.color(this.plugin.getConfig().getString("prefix", "") + " " + this.plugin.getConfig().getString("message-no-pumpkin", "&cTo nie jest Straszna Dynia!")));
            return;
        }
        this.removePumpkins(player, level, 1);
        ItemStack reward = this.rollReward(level);
        HashMap leftover = player.getInventory().addItem(new ItemStack[]{reward});
        for (ItemStack it : leftover.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), it);
        }
        if (this.plugin.getStatsManager() != null) {
            this.plugin.getStatsManager().addOpened(player.getUniqueId(), 1);
        }
        String prefix = this.plugin.getConfig().getString("prefix", "&4&lHalloween");
        String msg = this.plugin.getConfig().getString("message-drop-received", "&aOtrzyma\u0142e\u015b nagrod\u0119!");
        player.sendMessage(this.color(prefix + " " + msg + " &7(" + (reward.hasItemMeta() && reward.getItemMeta().hasDisplayName() ? reward.getItemMeta().getDisplayName() : reward.getType().name()) + "&7)"));
        player.sendMessage(this.color("&7Otworzy\u0142e\u015b &eStraszn\u0105 Dyni\u0119 &7poziomu &e" + level));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);
        player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().clone().add(0.0, 1.0, 0.0), 10, 0.5, 0.5, 0.5, 0.1);
    }

    public ItemStack rollReward(int level) {
        String path = "reward-lv" + level;
        ConfigurationSection sec = this.plugin.getConfig().getConfigurationSection(path);
        ArrayList<WeightedItem> pool = new ArrayList<WeightedItem>();
        int total = 0;
        if (sec != null) {
            for (String k : sec.getKeys(false)) {
                int w = sec.getInt(k, 0);
                if (w <= 0) continue;
                pool.add(new WeightedItem(this.materialToItem(k, level), w, k, false));
                total += w;
            }
        }
        String customPath = "custom-rewards.lv" + level;
        ConfigurationSection customSec = this.plugin.getConfig().getConfigurationSection(customPath);
        if (customSec != null) {
            for (String id : customSec.getKeys(false)) {
                ConfigurationSection entry = customSec.getConfigurationSection(id);
                if (entry == null) continue;
                int w = entry.getInt("weight", 0);
                ItemStack it = entry.getItemStack("item");
                if (it == null || w <= 0) continue;
                pool.add(new WeightedItem(it.clone(), w, id, true));
                total += w;
            }
        }
        if (pool.isEmpty()) {
            return new ItemStack(Material.DIAMOND, 1);
        }
        if (total <= 0) {
            total = 1;
        }
        int r = this.random.nextInt(total);
        int acc = 0;
        for (WeightedItem wi : pool) {
            if (r >= (acc += wi.weight)) continue;
            return wi.item.clone();
        }
        return ((WeightedItem)pool.get((int)0)).item.clone();
    }

    public void addCustomReward(int level, ItemStack item, int weight) {
        if (level < 1 || level > 3) {
            return;
        }
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        if (weight <= 0) {
            weight = 1;
        }
        String customPath = "custom-rewards.lv" + level;
        ConfigurationSection sec = this.plugin.getConfig().getConfigurationSection(customPath);
        if (sec == null) {
            sec = this.plugin.getConfig().createSection(customPath);
        }
        String id = UUID.randomUUID().toString().substring(0, 8);
        while (this.plugin.getConfig().contains(customPath + "." + id)) {
            id = UUID.randomUUID().toString().substring(0, 8);
        }
        this.plugin.getConfig().set(customPath + "." + id + ".weight", (Object)weight);
        this.plugin.getConfig().set(customPath + "." + id + ".item", (Object)item.clone());
        this.plugin.saveConfig();
    }

    public boolean removeReward(int level, String key) {
        if (level < 1 || level > 3) {
            return false;
        }
        String base = "reward-lv" + level;
        if (this.plugin.getConfig().contains(base + "." + key)) {
            this.plugin.getConfig().set(base + "." + key, null);
            this.plugin.saveConfig();
            return true;
        }
        String customPath = "custom-rewards.lv" + level;
        ConfigurationSection sec = this.plugin.getConfig().getConfigurationSection(customPath);
        if (sec != null) {
            for (String id : new ArrayList(sec.getKeys(false))) {
                String name;
                ItemStack it;
                if (id.equalsIgnoreCase(key)) {
                    this.plugin.getConfig().set(customPath + "." + id, null);
                    this.plugin.saveConfig();
                    return true;
                }
                ConfigurationSection entry = sec.getConfigurationSection(id);
                if (entry == null || (it = entry.getItemStack("item")) == null) continue;
                String mat = it.getType().name();
                String string = name = it.hasItemMeta() && it.getItemMeta().hasDisplayName() ? ChatColor.stripColor((String)it.getItemMeta().getDisplayName()) : "";
                if (!mat.equalsIgnoreCase(key) && !name.equalsIgnoreCase(key) && !id.equalsIgnoreCase(key)) continue;
                this.plugin.getConfig().set(customPath + "." + id, null);
                this.plugin.saveConfig();
                return true;
            }
            try {
                int idx = Integer.parseInt(key);
                ArrayList ids = new ArrayList(sec.getKeys(false));
                if (idx >= 0 && idx < ids.size()) {
                    this.plugin.getConfig().set(customPath + "." + (String)ids.get(idx), null);
                    this.plugin.saveConfig();
                    return true;
                }
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return false;
    }

    public boolean setRewardWeight(int level, String key, int weight) {
        if (weight <= 0) {
            weight = 1;
        }
        if (level < 1 || level > 3) {
            return false;
        }
        String base = "reward-lv" + level;
        if (this.plugin.getConfig().contains(base + "." + key)) {
            this.plugin.getConfig().set(base + "." + key, (Object)weight);
            this.plugin.saveConfig();
            return true;
        }
        String customPath = "custom-rewards.lv" + level;
        ConfigurationSection sec = this.plugin.getConfig().getConfigurationSection(customPath);
        if (sec != null) {
            for (String id : sec.getKeys(false)) {
                String name;
                ItemStack it;
                if (id.equalsIgnoreCase(key)) {
                    this.plugin.getConfig().set(customPath + "." + id + ".weight", (Object)weight);
                    this.plugin.saveConfig();
                    return true;
                }
                ConfigurationSection entry = sec.getConfigurationSection(id);
                if (entry == null || (it = entry.getItemStack("item")) == null) continue;
                String mat = it.getType().name();
                String string = name = it.hasItemMeta() && it.getItemMeta().hasDisplayName() ? ChatColor.stripColor((String)it.getItemMeta().getDisplayName()) : "";
                if (!mat.equalsIgnoreCase(key) && !name.toLowerCase().contains(key.toLowerCase())) continue;
                this.plugin.getConfig().set(customPath + "." + id + ".weight", (Object)weight);
                this.plugin.saveConfig();
                return true;
            }
            try {
                int idx = Integer.parseInt(key);
                ArrayList ids = new ArrayList(sec.getKeys(false));
                if (idx >= 0 && idx < ids.size()) {
                    this.plugin.getConfig().set(customPath + "." + (String)ids.get(idx) + ".weight", (Object)weight);
                    this.plugin.saveConfig();
                    return true;
                }
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return false;
    }

    public Map<String, Integer> getStandardWeights(int level) {
        LinkedHashMap<String, Integer> out = new LinkedHashMap<String, Integer>();
        ConfigurationSection sec = this.plugin.getConfig().getConfigurationSection("reward-lv" + level);
        if (sec != null) {
            for (String k : sec.getKeys(false)) {
                out.put(k, sec.getInt(k));
            }
        }
        return out;
    }

    public Map<String, WeightedEntry> getCustomEntries(int level) {
        LinkedHashMap<String, WeightedEntry> out = new LinkedHashMap<String, WeightedEntry>();
        ConfigurationSection sec = this.plugin.getConfig().getConfigurationSection("custom-rewards.lv" + level);
        if (sec != null) {
            for (String id : sec.getKeys(false)) {
                ConfigurationSection e = sec.getConfigurationSection(id);
                if (e == null) continue;
                out.put(id, new WeightedEntry(e.getItemStack("item"), e.getInt("weight", 0)));
            }
        }
        return out;
    }

    public int getTotalWeight(int level) {
        int tot = 0;
        Iterator<Object> iterator = this.getStandardWeights(level).values().iterator();
        while (iterator.hasNext()) {
            int v = iterator.next();
            tot += v;
        }
        for (WeightedEntry we : this.getCustomEntries(level).values()) {
            tot += we.weight;
        }
        return tot;
    }

    private ItemStack materialToItem(String key, int level) {
        switch (key.toLowerCase()) {
            case "paper": {
                return this.named(Material.PAPER, "&fKartka", 1);
            }
            case "bucket": {
                return new ItemStack(Material.BUCKET, 1);
            }
            case "string": {
                return new ItemStack(Material.STRING, 8 + this.random.nextInt(8));
            }
            case "glowstone_dust": {
                return new ItemStack(Material.GLOWSTONE_DUST, 4 + this.random.nextInt(6));
            }
            case "emerald": {
                return new ItemStack(Material.EMERALD, level == 3 ? 5 + this.random.nextInt(5) : 1 + this.random.nextInt(3));
            }
            case "tnt": {
                return new ItemStack(Material.TNT, 2 + this.random.nextInt(4));
            }
            case "diamond": {
                return new ItemStack(Material.DIAMOND, 1 + this.random.nextInt(level == 1 ? 2 : 4));
            }
            case "enchanted_book": {
                return this.enchantedBook();
            }
            case "skull": {
                return new ItemStack(Material.SKELETON_SKULL, 1);
            }
            case "iron_block": {
                return new ItemStack(Material.IRON_BLOCK, 1 + this.random.nextInt(2));
            }
            case "gold_block": {
                return new ItemStack(Material.GOLD_BLOCK, 1 + this.random.nextInt(2));
            }
            case "netherite_ingot": {
                return new ItemStack(Material.NETHERITE_SCRAP, 2);
            }
            case "stone_axe": {
                return new ItemStack(Material.STONE_AXE, 1);
            }
            case "totem_of_undying": {
                return new ItemStack(Material.TOTEM_OF_UNDYING, 1);
            }
            case "golden_apple": {
                return new ItemStack(Material.GOLDEN_APPLE, 1 + this.random.nextInt(2));
            }
            case "enchanted_golden_apple": {
                return new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1);
            }
            case "experience_bottle": {
                return new ItemStack(Material.EXPERIENCE_BOTTLE, 8 + this.random.nextInt(12));
            }
            case "nether_star": {
                return new ItemStack(Material.NETHER_STAR, 1);
            }
            case "netherite_pickaxe": {
                return new ItemStack(Material.NETHERITE_PICKAXE, 1);
            }
            case "dragon_egg": {
                return new ItemStack(Material.DRAGON_EGG, 1);
            }
            case "elytra": {
                return new ItemStack(Material.ELYTRA, 1);
            }
            case "diamond_block": {
                return new ItemStack(Material.DIAMOND_BLOCK, 1);
            }
        }
        try {
            return new ItemStack(Material.valueOf((String)key.toUpperCase()), 1);
        }
        catch (Exception e) {
            return new ItemStack(Material.EMERALD, 1);
        }
    }

    private ItemStack enchantedBook() {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
        ItemMeta m = book.getItemMeta();
        Enchantment[] ench = new Enchantment[]{Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.DAMAGE_ALL, Enchantment.DIG_SPEED, Enchantment.ARROW_DAMAGE};
        Enchantment pick = ench[this.random.nextInt(ench.length)];
        m.addEnchant(pick, 1 + this.random.nextInt(3), true);
        book.setItemMeta(m);
        return book;
    }

    private ItemStack named(Material mat, String name, int amount) {
        ItemStack it = new ItemStack(mat, amount);
        ItemMeta m = it.getItemMeta();
        m.setDisplayName(this.color(name));
        it.setItemMeta(m);
        return it;
    }

    public boolean exchange(Player p, int fromLevel) {
        int toLevel;
        int need;
        if (fromLevel == 1) {
            need = this.plugin.getConfig().getInt("exchange-lv1-to-lv2-amount", 5);
            toLevel = 2;
        } else if (fromLevel == 2) {
            need = this.plugin.getConfig().getInt("exchange-lv2-to-lv3-amount", 3);
            toLevel = 3;
        } else {
            if (fromLevel == 3) {
                int need2 = this.plugin.getConfig().getInt("exchange-lv3-to-reward-amount", 5);
                if (this.countPumpkins(p, 3) < need2) {
                    p.sendMessage(this.color(this.plugin.getConfig().getString("prefix", "") + " " + this.plugin.getConfig().getString("message-not-enough", "&cNie masz wystarczaj\u0105cej ilo\u015bci dy\u0144!")));
                    return false;
                }
                this.removePumpkins(p, 3, need2);
                ItemStack reward = this.rollReward(3);
                HashMap left = p.getInventory().addItem(new ItemStack[]{reward});
                for (ItemStack it : left.values()) {
                    p.getWorld().dropItemNaturally(p.getLocation(), it);
                }
                p.sendMessage(this.color(this.plugin.getConfig().getString("prefix", "") + " &aWymieni\u0142e\u015b &e" + need2 + "x Lv3 &ana losow\u0105 nagrod\u0119!"));
                return true;
            }
            return false;
        }
        if (this.countPumpkins(p, fromLevel) < need) {
            p.sendMessage(this.color(this.plugin.getConfig().getString("prefix", "") + " " + this.plugin.getConfig().getString("message-not-enough", "&cNie masz wystarczaj\u0105cej ilo\u015bci dy\u0144!")));
            return false;
        }
        this.removePumpkins(p, fromLevel, need);
        ItemStack out = this.createPumpkin(toLevel, 1);
        HashMap left = p.getInventory().addItem(new ItemStack[]{out});
        for (ItemStack it : left.values()) {
            p.getWorld().dropItemNaturally(p.getLocation(), it);
        }
        p.sendMessage(this.color(this.plugin.getConfig().getString("prefix", "") + " &aWymieni\u0142e\u015b &e" + need + "x Lv" + fromLevel + " &ana &eLv" + toLevel + "&a!"));
        return true;
    }

    public void openExchangeGUI(Player p) {
        Inventory inv = this.plugin.getServer().createInventory(null, 27, this.color("&4&lWYMIANA DY\u0143 &8- Halloween"));
        ItemStack to2 = this.createPumpkin(2, 1);
        ItemMeta m2 = to2.getItemMeta();
        ArrayList<String> lore2 = m2.getLore() != null ? new ArrayList<String>(m2.getLore()) : new ArrayList();
        lore2.add("");
        lore2.add(this.color("&eKoszt: &c" + this.plugin.getConfig().getInt("exchange-lv1-to-lv2-amount", 5) + "x &6Straszna Dynia I"));
        lore2.add(this.color("&aKliknij aby wymieni\u0107!"));
        int need1 = this.plugin.getConfig().getInt("exchange-lv1-to-lv2-amount", 5);
        lore2.add(this.color((String)(this.countPumpkins(p, 1) >= need1 ? "&aMasz wystarczaj\u0105co!" : "&cBrakuje: " + (need1 - this.countPumpkins(p, 1)))));
        m2.setLore(lore2);
        if (this.plugin.getConfig().isInt("exchange-model-1to2")) {
            m2.setCustomModelData(Integer.valueOf(this.plugin.getConfig().getInt("exchange-model-1to2")));
        }
        to2.setItemMeta(m2);
        ItemStack to3 = this.createPumpkin(3, 1);
        ItemMeta m3 = to3.getItemMeta();
        ArrayList<String> lore3 = m3.getLore() != null ? new ArrayList<String>(m3.getLore()) : new ArrayList();
        lore3.add("");
        lore3.add(this.color("&eKoszt: &c" + this.plugin.getConfig().getInt("exchange-lv2-to-lv3-amount", 3) + "x &5Straszna Dynia II"));
        lore3.add(this.color("&aKliknij aby wymieni\u0107!"));
        int need2 = this.plugin.getConfig().getInt("exchange-lv2-to-lv3-amount", 3);
        lore3.add(this.color((String)(this.countPumpkins(p, 2) >= need2 ? "&aMasz wystarczaj\u0105co!" : "&cBrakuje: " + (need2 - this.countPumpkins(p, 2)))));
        m3.setLore(lore3);
        to3.setItemMeta(m3);
        ItemStack reward = new ItemStack(Material.NETHER_STAR, 1);
        ItemMeta mr = reward.getItemMeta();
        mr.setDisplayName(this.color("&6&lLOSOWA NAGRODA &7(Lv3)"));
        ArrayList<String> lr = new ArrayList<String>();
        lr.add(this.color("&7Wymie\u0144 &c" + this.plugin.getConfig().getInt("exchange-lv3-to-reward-amount", 5) + "x &cStraszna Dynia III"));
        lr.add(this.color("&7na &elosanowan\u0105 nagrod\u0119 &7z tabeli Lv3"));
        lr.add(this.color("&7Mo\u017cliwe: &fNether Star, Elytra, Dragon Egg ..."));
        lr.add("");
        lr.add(this.color("&aKliknij aby wymieni\u0107!"));
        int need3 = this.plugin.getConfig().getInt("exchange-lv3-to-reward-amount", 5);
        lr.add(this.color((String)(this.countPumpkins(p, 3) >= need3 ? "&aMasz wystarczaj\u0105co!" : "&cBrakuje: " + (need3 - this.countPumpkins(p, 3)))));
        mr.setLore(lr);
        reward.setItemMeta(mr);
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta mf = filler.getItemMeta();
        mf.setDisplayName(" ");
        filler.setItemMeta(mf);
        for (int i = 0; i < 27; ++i) {
            if (i == 11 || i == 13 || i == 15) continue;
            inv.setItem(i, filler);
        }
        inv.setItem(11, to2);
        inv.setItem(13, to3);
        inv.setItem(15, reward);
        p.openInventory(inv);
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)s);
    }

    private static class WeightedItem {
        ItemStack item;
        int weight;
        String key;
        boolean custom;

        WeightedItem(ItemStack item, int weight, String key, boolean custom) {
            this.item = item;
            this.weight = weight;
            this.key = key;
            this.custom = custom;
        }
    }

    public static class WeightedEntry {
        public ItemStack item;
        public int weight;

        public WeightedEntry(ItemStack item, int weight) {
            this.item = item;
            this.weight = weight;
        }
    }
}

