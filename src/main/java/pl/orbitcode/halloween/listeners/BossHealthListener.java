/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityRegainHealthEvent
 *  org.bukkit.plugin.Plugin
 */
package pl.orbitcode.halloween.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.plugin.Plugin;
import pl.orbitcode.halloween.HalloweenPlugin;

public class BossHealthListener
implements Listener {
    private final HalloweenPlugin plugin;

    public BossHealthListener(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof LivingEntity)) {
            return;
        }
        LivingEntity ent = (LivingEntity)e.getEntity();
        if (!this.plugin.getMobManager().isBossMob((Entity)ent)) {
            return;
        }
        this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> {
            if (!ent.isDead() && ent.isValid()) {
                this.plugin.getMobManager().updateBossName(ent);
            }
        });
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onHeal(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof LivingEntity)) {
            return;
        }
        LivingEntity ent = (LivingEntity)e.getEntity();
        if (!this.plugin.getMobManager().isBossMob((Entity)ent)) {
            return;
        }
        this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> {
            if (!ent.isDead() && ent.isValid()) {
                this.plugin.getMobManager().updateBossName(ent);
            }
        });
    }
}

