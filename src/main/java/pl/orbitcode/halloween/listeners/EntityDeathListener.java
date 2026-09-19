/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDeathEvent
 */
package pl.orbitcode.halloween.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import pl.orbitcode.halloween.HalloweenPlugin;

public class EntityDeathListener
implements Listener {
    private final HalloweenPlugin plugin;

    public EntityDeathListener(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        if (!this.plugin.getMobManager().isEventMob((Entity)entity)) {
            return;
        }
        if (this.plugin.getMobManager().isBossMob((Entity)entity)) {
            e.getDrops().clear();
            e.setDroppedExp(0);
        }
        this.plugin.getMobManager().handleDeath(entity);
    }
}

