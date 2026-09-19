/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 */
package pl.orbitcode.halloween.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pl.orbitcode.halloween.HalloweenPlugin;
import pl.orbitcode.halloween.model.BossPumpkin;

public class BlockBreakListener
implements Listener {
    private final HalloweenPlugin plugin;

    public BlockBreakListener(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        BossPumpkin bp = this.plugin.getPumpkinManager().getPumpkin(e.getBlock().getLocation());
        if (bp != null) {
            e.setCancelled(true);
            return;
        }
        if (this.plugin.getPumpkinManager().isWorldPumpkin(e.getBlock())) {
            e.setCancelled(true);
            this.plugin.getPumpkinManager().collectWorldPumpkin(e.getBlock(), e.getPlayer());
        }
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)s);
    }
}

