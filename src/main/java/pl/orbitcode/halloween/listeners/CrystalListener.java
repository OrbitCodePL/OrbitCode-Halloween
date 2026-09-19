/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockDamageEvent
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityExplodeEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.projectiles.ProjectileSource
 */
package pl.orbitcode.halloween.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.projectiles.ProjectileSource;
import pl.orbitcode.halloween.HalloweenPlugin;
import pl.orbitcode.halloween.model.BossPumpkin;

public class CrystalListener
implements Listener {
    private final HalloweenPlugin plugin;

    public CrystalListener(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    private boolean isCrystal(EntityType t) {
        String n = t.name();
        return n.equals("END_CRYSTAL") || n.equals("ENDER_CRYSTAL");
    }

    private boolean isCrystal(Entity e) {
        return this.isCrystal(e.getType());
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=false)
    public void onCrystalDamageByEntity(EntityDamageByEntityEvent e) {
        Entity ent = e.getEntity();
        if (!this.isCrystal(ent)) {
            return;
        }
        BossPumpkin bp = this.plugin.getPumpkinManager().getPumpkinByCrystal(ent);
        if (bp == null && (bp = this.plugin.getPumpkinManager().getPumpkinByCrystalLocation(ent.getLocation(), 2.0)) == null) {
            return;
        }
        e.setCancelled(true);
        if (bp.isDestroyed()) {
            return;
        }
        Player damager = null;
        if (e.getDamager() instanceof Player) {
            damager = (Player)e.getDamager();
        } else {
            if (!(e.getDamager() instanceof Projectile)) return;
            ProjectileSource shooter = ((Projectile)e.getDamager()).getShooter();
            if (!(shooter instanceof Player)) return;
            damager = (Player)shooter;
        }
        int dmg = this.plugin.getConfig().getInt("pumpkin-damage-per-hit", 10);
        if (damager.getGameMode() == GameMode.CREATIVE) {
            dmg = Math.max(dmg, 50);
        }
        this.plugin.getPumpkinManager().damagePumpkin(bp, damager, dmg);
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=false)
    public void onCrystalDamage(EntityDamageEvent e) {
        if (!this.isCrystal(e.getEntity())) {
            return;
        }
        BossPumpkin bp = this.plugin.getPumpkinManager().getPumpkinByCrystal(e.getEntity());
        if (bp == null) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onExplode(EntityExplodeEvent e) {
        if (e.getEntity() != null && this.isCrystal(e.getEntity()) && this.plugin.getPumpkinManager().getPumpkinByCrystal(e.getEntity()) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onBlockDamage(BlockDamageEvent e) {
        Block b = e.getBlock();
        BossPumpkin bp = this.plugin.getPumpkinManager().getPumpkin(b.getLocation());
        if (bp == null) {
            return;
        }
        if (bp.isDestroyed()) {
            e.setCancelled(true);
            return;
        }
        e.setCancelled(true);
        int dmg = this.plugin.getConfig().getInt("pumpkin-damage-per-hit", 10);
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
            dmg = Math.max(dmg, 50);
        }
        this.plugin.getPumpkinManager().damagePumpkin(bp, e.getPlayer(), dmg);
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        BossPumpkin bp = this.plugin.getPumpkinManager().getPumpkin(b.getLocation());
        if (bp != null) {
            e.setCancelled(true);
            if (!bp.isDestroyed()) {
                int dmg = this.plugin.getConfig().getInt("pumpkin-damage-per-hit", 10);
                if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    dmg = Math.max(dmg, 50);
                }
                this.plugin.getPumpkinManager().damagePumpkin(bp, e.getPlayer(), dmg);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) {
            return;
        }
        Block b = e.getClickedBlock();
        BossPumpkin bp = this.plugin.getPumpkinManager().getPumpkin(b.getLocation());
        if (bp == null) {
            return;
        }
        if (bp.isDestroyed()) {
            e.setCancelled(true);
            return;
        }
        switch (e.getAction()) {
            case LEFT_CLICK_BLOCK: {
                e.setCancelled(true);
                int dmg = this.plugin.getConfig().getInt("pumpkin-damage-per-hit", 10);
                if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    dmg = Math.max(dmg, 50);
                }
                this.plugin.getPumpkinManager().damagePumpkin(bp, e.getPlayer(), dmg);
                break;
            }
            case RIGHT_CLICK_BLOCK: {
                if (b.getType() != Material.BEDROCK) break;
                e.setCancelled(true);
                break;
            }
        }
    }
}

