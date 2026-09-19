/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockDamageEvent
 */
package pl.orbitcode.halloween.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import pl.orbitcode.halloween.HalloweenPlugin;
import pl.orbitcode.halloween.model.BossPumpkin;

public class BlockDamageListener
implements Listener {
    private final HalloweenPlugin plugin;

    public BlockDamageListener(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(BlockDamageEvent e) {
        BossPumpkin bp = this.plugin.getPumpkinManager().getPumpkin(e.getBlock().getLocation());
        if (bp != null) {
            if (bp.isDestroyed()) {
                e.setCancelled(true);
            }
            return;
        }
    }
}

