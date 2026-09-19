/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 */
package pl.orbitcode.halloween.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.orbitcode.halloween.HalloweenPlugin;

public class PlayerInteractListener
implements Listener {
    private final HalloweenPlugin plugin;

    public PlayerInteractListener(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        ItemStack item = e.getItem();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        Integer lvl = this.plugin.getRewardManager().getPumpkinLevel(item);
        if (lvl == null) {
            return;
        }
        e.setCancelled(true);
        this.plugin.getRewardManager().openPumpkin(e.getPlayer(), lvl);
    }
}

