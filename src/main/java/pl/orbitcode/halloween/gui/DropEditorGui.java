/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 */
package pl.orbitcode.halloween.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import pl.orbitcode.halloween.HalloweenPlugin;
import pl.orbitcode.halloween.gui.SignPrompt;
import pl.orbitcode.halloween.util.RewardManager;

public class DropEditorGui
implements Listener {
    private final HalloweenPlugin plugin;
    private static final String MAIN_TITLE = "\u00a78Edycja dropu \u00a78- wybierz tier";
    private static final String TIER_TITLE_PREFIX = "\u00a75Drop Lv";
    private static final int TIER_INPUT_SLOT = 40;
    private static final int TIER_CONFIRM_SLOT = 42;
    private static final int TIER_BACK_SLOT = 45;
    private static final int TIER_CLOSE_SLOT = 53;
    private static final int TIER_CLEAR_SLOT = 47;
    private final Map<UUID, Integer> openTier = new HashMap<UUID, Integer>();
    private final Map<UUID, ItemStack> pendingItem = new HashMap<UUID, ItemStack>();
    private final Map<UUID, Inventory> openInventories = new HashMap<UUID, Inventory>();

    public DropEditorGui(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    public void openMain(Player p) {
        Inventory inv = this.plugin.getServer().createInventory(null, 27, MAIN_TITLE);
        ItemStack filler = this.filler();
        for (int i = 0; i < 27; ++i) {
            inv.setItem(i, filler);
        }
        inv.setItem(11, this.tierItem(1));
        inv.setItem(13, this.tierItem(2));
        inv.setItem(15, this.tierItem(3));
        inv.setItem(26, this.closeItem());
        p.openInventory(inv);
    }

    private ItemStack tierItem(int tier) {
        Material mat = tier == 1 ? Material.GOLD_INGOT : (tier == 2 ? Material.DIAMOND : Material.NETHER_STAR);
        ItemStack it = new ItemStack(mat, 1);
        ItemMeta m = it.getItemMeta();
        m.setDisplayName(this.color(tier == 1 ? "&6&lTier I &7- Najpospolitszy" : (tier == 2 ? "&5&lTier II &7- Rzadszy" : "&c&lTier III &7- Najrzadszy")));
        ArrayList<String> lore = new ArrayList<String>();
        RewardManager rm = this.plugin.getRewardManager();
        int total = rm.getTotalWeight(tier);
        int std = rm.getStandardWeights(tier).size();
        int custom = rm.getCustomEntries(tier).size();
        lore.add(this.color("&7Wpisy: &e" + (std + custom) + " &8(" + std + " std + " + custom + " custom)"));
        lore.add(this.color("&7Suma wag: &e" + total));
        lore.add("");
        lore.add(this.color("&aKliknij aby edytowac!"));
        lore.add(this.color("&7Bedziesz mogl wsadzic przedmiot"));
        lore.add(this.color("&7i ustawic % na tabliczce"));
        m.setLore(lore);
        it.setItemMeta(m);
        return it;
    }

    public void openTier(Player p, int tier) {
        this.openTier.put(p.getUniqueId(), tier);
        String title = TIER_TITLE_PREFIX + tier + " \u00a78- Edycja";
        Inventory inv = this.plugin.getServer().createInventory(null, 54, title);
        this.refreshTierInventory(inv, tier, p);
        this.openInventories.put(p.getUniqueId(), inv);
        p.openInventory(inv);
    }

    private void refreshTierInventory(Inventory inv, int tier, Player p) {
        ItemStack it;
        inv.clear();
        ItemStack filler = this.fillerGray();
        for (int i = 0; i < 54; ++i) {
            if (i < 45) continue;
            inv.setItem(i, filler);
        }
        RewardManager rm = this.plugin.getRewardManager();
        Map<String, Integer> std = rm.getStandardWeights(tier);
        Map<String, RewardManager.WeightedEntry> custom = rm.getCustomEntries(tier);
        int total = rm.getTotalWeight(tier);
        int slot = 0;
        for (Map.Entry<String, Integer> entry : std.entrySet()) {
            if (slot >= 36) break;
            it = this.createDisplayForKey(entry.getKey(), tier, entry.getValue(), total, false, entry.getKey());
            inv.setItem(slot++, it);
        }
        for (Map.Entry<String, Object> entry : custom.entrySet()) {
            if (slot >= 36) break;
            it = ((RewardManager.WeightedEntry)entry.getValue()).item != null ? ((RewardManager.WeightedEntry)entry.getValue()).item.clone() : new ItemStack(Material.BARRIER);
            ItemMeta m = it.getItemMeta();
            if (m == null) continue;
            ArrayList<String> lore = m.hasLore() ? new ArrayList<String>(m.getLore()) : new ArrayList();
            lore.add("");
            lore.add(this.color("&8--- Custom ---"));
            lore.add(this.color("&7ID: &8" + entry.getKey()));
            double pct = total > 0 ? (double)((RewardManager.WeightedEntry)entry.getValue()).weight * 100.0 / (double)total : 0.0;
            lore.add(this.color("&7Waga: &a" + ((RewardManager.WeightedEntry)entry.getValue()).weight + " &8(" + String.format("%.2f", pct) + "%)"));
            lore.add(this.color("&cLewy klik = USU\u0143"));
            lore.add(this.color("&ePrawy klik = ZMIE\u0143 %"));
            m.setLore(lore);
            String disp = m.hasDisplayName() ? m.getDisplayName() : ((RewardManager.WeightedEntry)entry.getValue()).item.getType().name();
            m.setDisplayName(disp + this.color(" \u00a78[custom]"));
            it.setItemMeta(m);
            inv.setItem(slot++, it);
        }
        if (slot == 0) {
            ItemStack empty = new ItemStack(Material.GRAY_DYE);
            ItemMeta itemMeta = empty.getItemMeta();
            itemMeta.setDisplayName(this.color("&7Brak wpis\u00f3w"));
            itemMeta.setLore(Arrays.asList(this.color("&7Dodaj przedmiot poni\u017cej"), this.color("&7W\u0142\u00f3\u017c item do slotu INPUT")));
            empty.setItemMeta(itemMeta);
            inv.setItem(10, empty);
        }
        inv.setItem(45, this.backItem());
        ItemStack pending = this.pendingItem.get(p.getUniqueId());
        if (pending != null && pending.getType() != Material.AIR) {
            ItemStack itemStack = pending.clone();
            im = itemStack.getItemMeta();
            ArrayList<String> lore = im.hasLore() ? new ArrayList<String>(im.getLore()) : new ArrayList();
            lore.add("");
            lore.add(this.color("&aGotowy do dodania!"));
            lore.add(this.color("&7Kliknij \u00a7aZATWIERD\u0179 \u00a77aby ustawi\u0107 %"));
            im.setLore(lore);
            itemStack.setItemMeta(im);
            inv.setItem(40, itemStack);
        } else {
            ItemStack itemStack = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            im = itemStack.getItemMeta();
            im.setDisplayName(this.color("&a\u00a7lINPUT \u00a77- Wsadz przedmiot"));
            im.setLore(Arrays.asList(this.color("&7Przeci\u0105gnij item z EQ"), this.color("&7na ten slot"), this.color("&7Nast\u0119pnie kliknij \u00a7aZatwierd\u017a"), this.color("&7i wpisz % na tabliczce"), this.color("&7np. &e5.5 &7lub &e5,5%")));
            itemStack.setItemMeta(im);
            inv.setItem(40, itemStack);
        }
        inv.setItem(47, this.clearItem(tier));
        inv.setItem(42, this.confirmItem());
        inv.setItem(53, this.closeItem());
        inv.setItem(49, this.infoItem(tier, total));
    }

    private ItemStack createDisplayForKey(String key, int tier, int weight, int total, boolean custom, String id) {
        ItemStack base;
        try {
            base = new ItemStack(Material.valueOf((String)key.toUpperCase()));
        }
        catch (Exception e) {
            base = this.plugin.getRewardManager().rollReward(tier);
            base = new ItemStack(Material.PAPER);
        }
        if (base.getType() == Material.PAPER && !key.equalsIgnoreCase("paper")) {
            try {
                base = new ItemStack(Material.valueOf((String)key.toUpperCase().replace(" ", "_")));
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        ItemMeta m = base.getItemMeta();
        m.setDisplayName(this.color("&e" + key));
        double pct = total > 0 ? (double)weight * 100.0 / (double)total : 0.0;
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(this.color("&7Waga: &a" + weight + " &8(" + String.format("%.2f", pct) + "%)"));
        lore.add(this.color("&7Tier: &e" + tier));
        lore.add("");
        lore.add(this.color("&cLewy klik = USU\u0143"));
        lore.add(this.color("&ePrawy klik = ZMIE\u0143 %"));
        if (!custom) {
            lore.add(this.color("&8Standardowy wpis z configu"));
        }
        m.setLore(lore);
        base.setItemMeta(m);
        return base;
    }

    private ItemStack filler() {
        ItemStack it = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta m = it.getItemMeta();
        m.setDisplayName(" ");
        it.setItemMeta(m);
        return it;
    }

    private ItemStack fillerGray() {
        ItemStack it = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta m = it.getItemMeta();
        m.setDisplayName(" ");
        it.setItemMeta(m);
        return it;
    }

    private ItemStack backItem() {
        ItemStack it = new ItemStack(Material.ARROW);
        ItemMeta m = it.getItemMeta();
        m.setDisplayName(this.color("&7\u00ab Powr\u00f3t"));
        m.setLore(Arrays.asList(this.color("&7Do wyboru tieru")));
        it.setItemMeta(m);
        return it;
    }

    private ItemStack closeItem() {
        ItemStack it = new ItemStack(Material.BARRIER);
        ItemMeta m = it.getItemMeta();
        m.setDisplayName(this.color("&cZamknij"));
        it.setItemMeta(m);
        return it;
    }

    private ItemStack confirmItem() {
        ItemStack it = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta m = it.getItemMeta();
        m.setDisplayName(this.color("&a\u00a7lZATWIERD\u0179 &7- Ustaw %"));
        m.setLore(Arrays.asList(this.color("&7Kliknij aby otworzy\u0107 tabliczk\u0119"), this.color("&7i wpisa\u0107 procent np. &e5.5%")));
        it.setItemMeta(m);
        return it;
    }

    private ItemStack clearItem(int tier) {
        ItemStack it = new ItemStack(Material.TNT);
        ItemMeta m = it.getItemMeta();
        m.setDisplayName(this.color("&cWyczy\u015b\u0107 tier " + tier));
        m.setLore(Arrays.asList(this.color("&cUsuwa WSZYSTKIE wpisy"), this.color("&7custom + standard"), this.color("&cLewy klik aby potwierdzi\u0107")));
        it.setItemMeta(m);
        return it;
    }

    private ItemStack infoItem(int tier, int total) {
        ItemStack it = new ItemStack(Material.BOOK);
        ItemMeta m = it.getItemMeta();
        m.setDisplayName(this.color("&eInfo Tier " + tier));
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(this.color("&7Suma wag: &e" + total));
        lore.add(this.color("&7Procent = waga/suma*100%"));
        lore.add(this.color("&7Przyk\u0142ad: 5.5% \u2192 waga ~5-6"));
        lore.add(this.color("&7Wpisz na tabliczce np. &e5.5 &7lub &e5,5%"));
        m.setLore(lore);
        it.setItemMeta(m);
        return it;
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)s);
    }

    private int parseWeightFromPercent(String input, int total, int tier) {
        if (input == null) {
            return -1;
        }
        if ((input = input.trim().replace("%", "").replace(",", ".").trim()).isEmpty()) {
            return -1;
        }
        try {
            double pct = Double.parseDouble(input);
            if (pct <= 0.0 || pct > 100.0) {
                return -1;
            }
            if (total <= 0) {
                return (int)Math.max(1L, Math.round(pct * 10.0));
            }
            double w = (double)total * pct / (100.0 - pct);
            int wi = (int)Math.max(1L, Math.round(w));
            if (total < 50) {
                wi = (int)Math.max(1L, Math.round(pct * 10.0));
            }
            return wi;
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player p = (Player)e.getWhoClicked();
        String title = e.getView().getTitle();
        if (title.equals(MAIN_TITLE)) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            int slot = e.getRawSlot();
            if (slot == 11) {
                this.openTier(p, 1);
            } else if (slot == 13) {
                this.openTier(p, 2);
            } else if (slot == 15) {
                this.openTier(p, 3);
            } else if (slot == 26) {
                p.closeInventory();
            }
            return;
        }
        if (title.startsWith(TIER_TITLE_PREFIX)) {
            Inventory top;
            int slot;
            int tier = this.openTier.getOrDefault(p.getUniqueId(), -1);
            if (tier == -1) {
                try {
                    tier = Integer.parseInt(title.replaceAll("[^0-9]", "").substring(0, 1));
                }
                catch (Exception ex) {
                    return;
                }
            }
            if ((slot = e.getRawSlot()) >= (top = e.getView().getTopInventory()).getSize()) {
                ItemStack clicked;
                if (e.isShiftClick() && (clicked = e.getCurrentItem()) != null && clicked.getType() != Material.AIR && !this.plugin.getRewardManager().isPumpkin(clicked)) {
                    if (!this.pendingItem.containsKey(p.getUniqueId()) || this.pendingItem.get(p.getUniqueId()) == null || this.pendingItem.get(p.getUniqueId()).getType() == Material.AIR) {
                        e.setCancelled(true);
                        ItemStack copy = clicked.clone();
                        copy.setAmount(1);
                        this.pendingItem.put(p.getUniqueId(), copy);
                        if (clicked.getAmount() <= 1) {
                            e.setCurrentItem(new ItemStack(Material.AIR));
                        } else {
                            clicked.setAmount(clicked.getAmount() - 1);
                            e.setCurrentItem(clicked);
                        }
                        p.sendMessage(this.color("&7Wsadzono \u00a7e" + String.valueOf(copy.getType()) + " \u00a77shift-click \u2192 kliknij \u00a7aZATWIERD\u0179"));
                        this.refreshTierInventory(top, tier, p);
                        p.updateInventory();
                    } else {
                        e.setCancelled(true);
                        p.sendMessage(this.color("&cINPUT ju\u017c zaj\u0119ty! Kliknij INPUT aby go opr\u00f3\u017cni\u0107 lub ZATWIERD\u0179."));
                    }
                }
                return;
            }
            e.setCancelled(true);
            if (slot >= 0 && slot < 36) {
                ItemStack clicked = e.getCurrentItem();
                if (clicked == null || clicked.getType() == Material.AIR || clicked.getType() == Material.GRAY_DYE) {
                    return;
                }
                boolean isCustom = clicked.hasItemMeta() && clicked.getItemMeta().hasLore() && clicked.getItemMeta().getLore().toString().contains("Custom");
                String key = null;
                if (clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName() && (key = ChatColor.stripColor((String)clicked.getItemMeta().getDisplayName()).trim()).endsWith("[custom]")) {
                    key = key.replace("[custom]", "").trim();
                }
                if (key == null || key.isEmpty()) {
                    return;
                }
                boolean isRight = e.isRightClick();
                if (e.isLeftClick() && !isRight) {
                    boolean ok = this.plugin.getRewardManager().removeReward(tier, key);
                    if (!ok) {
                        RewardManager rm = this.plugin.getRewardManager();
                        for (String id : rm.getCustomEntries(tier).keySet()) {
                            String disp;
                            ItemStack it = rm.getCustomEntries((int)tier).get((Object)id).item;
                            String string = disp = it.hasItemMeta() && it.getItemMeta().hasDisplayName() ? ChatColor.stripColor((String)it.getItemMeta().getDisplayName()) : it.getType().name();
                            if (!disp.equalsIgnoreCase(key) && !it.getType().name().equalsIgnoreCase(key)) continue;
                            ok = this.plugin.getRewardManager().removeReward(tier, id);
                            break;
                        }
                    }
                    p.sendMessage(ok ? this.color("&aUsuni\u0119to &e" + key + " &az Lv" + tier) : this.color("&cNie znaleziono " + key));
                    this.refreshTierInventory(top, tier, p);
                    p.updateInventory();
                } else if (isRight) {
                    String finalKey = key;
                    Player fp1 = p;
                    int fTier1 = tier;
                    Inventory topFinal = top;
                    p.closeInventory();
                    this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> SignPrompt.open(fp1, fTier1, input -> {
                        int total = this.plugin.getRewardManager().getTotalWeight(fTier1);
                        int oldW = 0;
                        Map<String, Integer> std = this.plugin.getRewardManager().getStandardWeights(fTier1);
                        if (std.containsKey(finalKey)) {
                            oldW = std.get(finalKey);
                        } else {
                            RewardManager.WeightedEntry we = this.plugin.getRewardManager().getCustomEntries(fTier1).get(finalKey);
                            if (we != null) {
                                oldW = we.weight;
                            } else {
                                for (Map.Entry<String, RewardManager.WeightedEntry> en : this.plugin.getRewardManager().getCustomEntries(fTier1).entrySet()) {
                                    String disp = en.getValue().item.hasItemMeta() && en.getValue().item.getItemMeta().hasDisplayName() ? ChatColor.stripColor((String)en.getValue().item.getItemMeta().getDisplayName()) : en.getValue().item.getType().name();
                                    if (!disp.equalsIgnoreCase(finalKey)) continue;
                                    oldW = en.getValue().weight;
                                    break;
                                }
                            }
                        }
                        int totalWithout = Math.max(0, total - oldW);
                        int newW = this.parseWeightFromPercent((String)input, totalWithout, fTier1);
                        if (newW <= 0) {
                            fp1.sendMessage(this.color("&cNieprawid\u0142owy procent: " + input + " (wpisz np. 5.5 lub 5,5%)"));
                            this.openTier(fp1, fTier1);
                            return;
                        }
                        boolean ok2 = this.plugin.getRewardManager().setRewardWeight(fTier1, finalKey, newW);
                        if (!ok2) {
                            for (String id : this.plugin.getRewardManager().getCustomEntries(fTier1).keySet()) {
                                if (!id.equalsIgnoreCase(finalKey)) continue;
                                ok2 = this.plugin.getRewardManager().setRewardWeight(fTier1, id, newW);
                                break;
                            }
                        }
                        fp1.sendMessage(ok2 ? this.color("&aUstawiono &e" + finalKey + " &ana &e" + newW + " &7(" + input + "%) &aw Lv" + fTier1) : this.color("&cNie znaleziono " + finalKey));
                        this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> this.openTier(fp1, fTier1));
                    }));
                }
                return;
            }
            if (slot == 45) {
                this.openMain(p);
                return;
            }
            if (slot == 53) {
                p.closeInventory();
                return;
            }
            if (slot == 47) {
                if (e.isLeftClick()) {
                    this.plugin.getConfig().set("reward-lv" + tier, new HashMap());
                    this.plugin.getConfig().set("custom-rewards.lv" + tier, null);
                    this.plugin.saveConfig();
                    p.sendMessage(this.color("&aWyczyszczono Tier " + tier));
                    this.refreshTierInventory(top, tier, p);
                }
                return;
            }
            if (slot == 40) {
                ItemStack cursor = e.getCursor();
                ItemStack current = e.getCurrentItem();
                if (cursor != null && cursor.getType() != Material.AIR) {
                    if (this.plugin.getRewardManager().isPumpkin(cursor)) {
                        p.sendMessage(this.color("&cNie mo\u017cesz doda\u0107 dyni jako dropu!"));
                        return;
                    }
                    this.pendingItem.put(p.getUniqueId(), cursor.clone());
                    e.setCursor(new ItemStack(Material.AIR));
                    p.sendMessage(this.color("&7Wsadzono \u00a7e" + String.valueOf(cursor.getType()) + " \u00a77- kliknij \u00a7aZATWIERD\u0179"));
                    this.refreshTierInventory(top, tier, p);
                } else if (this.pendingItem.containsKey(p.getUniqueId()) && (cursor == null || cursor.getType() == Material.AIR)) {
                    ItemStack pend = this.pendingItem.remove(p.getUniqueId());
                    e.setCursor(pend);
                    p.sendMessage(this.color("&7Usuni\u0119to przedmiot z INPUT"));
                    this.refreshTierInventory(top, tier, p);
                }
                return;
            }
            if (slot == 42) {
                ItemStack pend = this.pendingItem.get(p.getUniqueId());
                if (pend == null || pend.getType() == Material.AIR) {
                    p.sendMessage(this.color("&cNajpierw wsad\u017a przedmiot do INPUT (\u015brodkowy slot)"));
                    return;
                }
                ItemStack finalPend = pend.clone();
                Player fp2 = p;
                int fTier2 = tier;
                UUID pid = p.getUniqueId();
                p.closeInventory();
                this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> SignPrompt.open(fp2, fTier2, input -> {
                    int total = this.plugin.getRewardManager().getTotalWeight(fTier2);
                    int newW = this.parseWeightFromPercent((String)input, total, fTier2);
                    if (newW <= 0) {
                        fp2.sendMessage(this.color("&cNieprawid\u0142owy procent: " + input));
                        this.openTier(fp2, fTier2);
                        return;
                    }
                    this.plugin.getRewardManager().addCustomReward(fTier2, finalPend, newW);
                    this.pendingItem.remove(pid);
                    double pct = this.plugin.getRewardManager().getTotalWeight(fTier2) > 0 ? (double)newW * 100.0 / (double)this.plugin.getRewardManager().getTotalWeight(fTier2) : 0.0;
                    fp2.sendMessage(this.color("&aDodano &e" + String.valueOf(finalPend.getType()) + " &7waga &a" + newW + " &7(" + String.format("%.2f", pct) + "% " + input + "%) &ado Lv" + fTier2));
                    this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> this.openTier(fp2, fTier2));
                }));
                return;
            }
            return;
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        String title = e.getView().getTitle();
        if (title.equals(MAIN_TITLE)) {
            e.setCancelled(true);
            return;
        }
        if (title.startsWith(TIER_TITLE_PREFIX)) {
            Inventory top = e.getView().getTopInventory();
            boolean hasTop = false;
            Iterator iterator = e.getRawSlots().iterator();
            while (iterator.hasNext()) {
                int slot = (Integer)iterator.next();
                if (slot >= top.getSize()) continue;
                hasTop = true;
                break;
            }
            if (hasTop) {
                e.setCancelled(true);
                if (e.getWhoClicked() instanceof Player) {
                    ItemStack dragged;
                    Player p = (Player)e.getWhoClicked();
                    if (e.getRawSlots().contains(40) && e.getRawSlots().size() == 1 && (dragged = (ItemStack)e.getNewItems().values().iterator().next()) != null && dragged.getType() != Material.AIR && !this.plugin.getRewardManager().isPumpkin(dragged)) {
                        this.pendingItem.put(p.getUniqueId(), dragged.clone());
                        this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> {
                            Inventory topInv = e.getView().getTopInventory();
                            int tier = this.openTier.getOrDefault(p.getUniqueId(), -1);
                            if (tier == -1) {
                                try {
                                    tier = Integer.parseInt(title.replaceAll("[^0-9]", "").substring(0, 1));
                                }
                                catch (Exception ignored) {
                                    return;
                                }
                            }
                            this.refreshTierInventory(topInv, tier, p);
                        });
                        p.sendMessage(this.color("&7Wsadzono \u00a7e" + String.valueOf(dragged.getType()) + " \u00a77(drag) \u2192 kliknij \u00a7aZATWIERD\u0179"));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
    }
}

