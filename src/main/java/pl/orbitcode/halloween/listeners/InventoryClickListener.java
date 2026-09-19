/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.plugin.Plugin
 */
package pl.orbitcode.halloween.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import pl.orbitcode.halloween.HalloweenPlugin;

public class InventoryClickListener
implements Listener {
    private final HalloweenPlugin plugin;

    public InventoryClickListener(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        String title = e.getView().getTitle();
        if (!ChatColor.stripColor((String)title).contains("WYMIANA")) {
            return;
        }
        if (!title.contains("WYMIANA DY")) {
            return;
        }
        e.setCancelled(true);
        if (e.getCurrentItem() == null) {
            return;
        }
        Player p = (Player)e.getWhoClicked();
        int slot = e.getRawSlot();
        if (slot == 11) {
            this.plugin.getRewardManager().exchange(p, 1);
        } else if (slot == 13) {
            this.plugin.getRewardManager().exchange(p, 2);
        } else if (slot == 15) {
            this.plugin.getRewardManager().exchange(p, 3);
        }
        this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, () -> {
            if (p.getOpenInventory().getTitle().equals(title)) {
                p.closeInventory();
                this.plugin.getRewardManager().openExchangeGUI(p);
            }
        }, 2L);
    }
}

