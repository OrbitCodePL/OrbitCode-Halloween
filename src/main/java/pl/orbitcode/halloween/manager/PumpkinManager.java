/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Particle
 *  org.bukkit.Sound
 *  org.bukkit.block.Block
 *  org.bukkit.entity.ArmorStand
 *  org.bukkit.entity.EnderCrystal
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package pl.orbitcode.halloween.manager;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.orbitcode.halloween.HalloweenPlugin;
import pl.orbitcode.halloween.model.BossPumpkin;
import pl.orbitcode.halloween.util.RewardManager;

public class PumpkinManager {
    private final HalloweenPlugin plugin;
    private final Map<String, BossPumpkin> pumpkins = new ConcurrentHashMap<String, BossPumpkin>();
    private final Map<String, Location> worldPumpkins = new ConcurrentHashMap<String, Location>();

    public PumpkinManager(HalloweenPlugin plugin) {
        this.plugin = plugin;
        this.loadFromStorage();
    }

    private void loadFromStorage() {
        int defaultHp = this.plugin.getConfig().getInt("pumpkin-health", 250);
        for (Location loc : this.plugin.getLocationManager().getLocations()) {
            BossPumpkin bp = new BossPumpkin(loc, defaultHp);
            this.pumpkins.put(this.key(loc), bp);
            this.spawnBossBlock(bp);
            this.spawnHologram(bp);
        }
    }

    private String key(Location l) {
        return l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ();
    }

    public void placePumpkin(Location loc) {
        this.placePumpkin(loc, this.plugin.getConfig().getInt("pumpkin-health", 250));
    }

    public void placePumpkin(Location loc, int hp) {
        String k = this.key(loc);
        if (this.pumpkins.containsKey(k)) {
            return;
        }
        if (hp <= 0) {
            hp = this.plugin.getConfig().getInt("pumpkin-health", 250);
        }
        BossPumpkin bp = new BossPumpkin(loc, hp);
        this.pumpkins.put(k, bp);
        this.plugin.getLocationManager().addLocation(loc);
        this.spawnBossBlock(bp);
        this.spawnHologram(bp);
        this.spawnCrystalEffects(loc);
        String prefix = this.plugin.getConfig().getString("prefix", "&4&lHalloween &r&c\u00bb");
        this.plugin.getServer().broadcastMessage(this.color(prefix + " &aPostawiono Ska\u0142k\u0119 &7HP: &c" + hp));
    }

    public boolean removePumpkin(Location loc) {
        String k;
        BossPumpkin removed;
        BossPumpkin bp = this.getPumpkin(loc);
        if (bp == null) {
            bp = this.getNearest(loc, this.plugin.getConfig().getInt("pumpkin-detection-radius", 15));
            if (bp == null) {
                return false;
            }
            loc = bp.getLocation();
        }
        if ((removed = this.pumpkins.remove(k = this.key(loc))) != null) {
            for (LivingEntity e : removed.getDefenders()) {
                if (e == null || e.isDead()) continue;
                e.remove();
            }
            removed.getDefenders().clear();
            this.removeHologram(removed);
            this.removeCrystal(removed);
            this.plugin.getLocationManager().removeLocation(loc);
            Block b = loc.getBlock();
            if (b.getType() == Material.BEDROCK || b.getType() == Material.OBSIDIAN || b.getType() == Material.CRYING_OBSIDIAN || b.getType() == Material.DRAGON_EGG || b.getType() == Material.CARVED_PUMPKIN || b.getType() == Material.JACK_O_LANTERN) {
                b.setType(Material.AIR);
            }
            return true;
        }
        return false;
    }

    public BossPumpkin getPumpkin(Location loc) {
        return this.pumpkins.get(this.key(loc));
    }

    public BossPumpkin getPumpkinByCrystal(Entity crystal) {
        if (crystal == null) {
            return null;
        }
        for (BossPumpkin bp : this.pumpkins.values()) {
            EnderCrystal ec = bp.getCrystal();
            if (ec == null || !ec.getUniqueId().equals(crystal.getUniqueId())) continue;
            return bp;
        }
        return null;
    }

    public BossPumpkin getPumpkinByCrystalLocation(Location loc, double radius) {
        BossPumpkin nearest = null;
        double best = Double.MAX_VALUE;
        for (BossPumpkin bp : this.pumpkins.values()) {
            double d;
            EnderCrystal ec;
            if (bp.isDestroyed() || (ec = bp.getCrystal()) == null || ec.isDead() || !ec.isValid() || !ec.getWorld().equals((Object)loc.getWorld()) || !((d = ec.getLocation().distanceSquared(loc)) < radius * radius) || !(d < best)) continue;
            best = d;
            nearest = bp;
        }
        return nearest;
    }

    public BossPumpkin getNearest(Location loc, int radius) {
        BossPumpkin nearest = null;
        double best = Double.MAX_VALUE;
        for (BossPumpkin bp : this.pumpkins.values()) {
            double d;
            if (!bp.getLocation().getWorld().equals((Object)loc.getWorld()) || !((d = bp.getLocation().distanceSquared(loc)) < (double)(radius * radius)) || !(d < best)) continue;
            best = d;
            nearest = bp;
        }
        return nearest;
    }

    public Collection<BossPumpkin> getAll() {
        return this.pumpkins.values();
    }

    public void spawnBossBlock(BossPumpkin bp) {
        if (bp.isDestroyed()) {
            return;
        }
        Location loc = bp.getLocation();
        Block b = loc.getBlock();
        b.setType(Material.BEDROCK);
        this.spawnCrystal(bp);
        this.spawnCrystalEffects(loc);
    }

    public void spawnBossBlock(Location loc) {
        BossPumpkin bp = this.getPumpkin(loc);
        if (bp != null) {
            this.spawnBossBlock(bp);
        } else {
            Block b = loc.getBlock();
            b.setType(Material.BEDROCK);
        }
    }

    private void spawnCrystal(BossPumpkin bp) {
        this.removeCrystal(bp);
        Location loc = bp.getLocation();
        if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
            return;
        }
        Location crystalLoc = loc.clone().add(0.5, 1.0, 0.5);
        for (Entity e : crystalLoc.getWorld().getNearbyEntities(crystalLoc, 0.5, 0.5, 0.5)) {
            if (e.getType() != this.getCrystalType()) continue;
            boolean linked = false;
            for (BossPumpkin other : this.pumpkins.values()) {
                if (other.getCrystal() == null || !other.getCrystal().getUniqueId().equals(e.getUniqueId())) continue;
                linked = true;
                break;
            }
            if (linked) continue;
            e.remove();
        }
        EnderCrystal crystal = (EnderCrystal)loc.getWorld().spawnEntity(crystalLoc, this.getCrystalType());
        crystal.setShowingBottom(false);
        crystal.setBeamTarget(null);
        crystal.setInvulnerable(true);
        crystal.setPersistent(true);
        bp.setCrystal(crystal);
    }

    public void removeCrystal(BossPumpkin bp) {
        EnderCrystal ec = bp.getCrystal();
        if (ec != null && !ec.isDead()) {
            ec.remove();
        }
        bp.setCrystal(null);
        Location loc = bp.getLocation().clone().add(0.5, 1.0, 0.5);
        for (Entity e : loc.getWorld().getNearbyEntities(loc, 1.5, 1.5, 1.5)) {
            if (e.getType() != this.getCrystalType()) continue;
            boolean linkedElsewhere = false;
            for (BossPumpkin other : this.pumpkins.values()) {
                EnderCrystal o;
                if (other == bp || (o = other.getCrystal()) == null || !o.getUniqueId().equals(e.getUniqueId())) continue;
                linkedElsewhere = true;
                break;
            }
            if (linkedElsewhere) continue;
            e.remove();
        }
    }

    public boolean isBossBlock(Block block) {
        return this.getPumpkin(block.getLocation()) != null;
    }

    public boolean isBossCrystal(Entity entity) {
        if (!this.isCrystal(entity.getType())) {
            return false;
        }
        return this.getPumpkinByCrystal(entity) != null;
    }

    public void spawnHologram(BossPumpkin bp) {
        this.removeHologram(bp);
        Location holoLoc = bp.getLocation().clone().add(0.5, 2.7, 0.5);
        if (!holoLoc.getWorld().isChunkLoaded(holoLoc.getBlockX() >> 4, holoLoc.getBlockZ() >> 4)) {
            return;
        }
        ArmorStand as = (ArmorStand)holoLoc.getWorld().spawnEntity(holoLoc, EntityType.ARMOR_STAND);
        as.setVisible(false);
        as.setGravity(false);
        as.setCanPickupItems(false);
        as.setMarker(true);
        as.setCustomNameVisible(true);
        as.setInvulnerable(true);
        as.setSmall(false);
        bp.setHologram(as);
        this.updateHologram(bp);
    }

    public void updateHologram(BossPumpkin bp) {
        ArmorStand as = bp.getHologram();
        if (as == null || as.isDead() || !as.isValid()) {
            this.spawnHologram(bp);
            as = bp.getHologram();
            if (as == null) {
                return;
            }
        }
        if (bp.isDestroyed()) {
            as.setCustomName(this.color("&8&lSka\u0142ka &7[&cZniszczona&7] &8- odnowienie za " + this.formatTime(this.getRefreshRemaining(bp))));
        } else {
            double ratio;
            double d = ratio = bp.getMaxHealth() > 0 ? (double)bp.getHealth() / (double)bp.getMaxHealth() : 0.0;
            String col = ratio > 0.6 ? "&a" : (ratio > 0.3 ? "&e" : "&c");
            as.setCustomName(this.color("&6&lSka\u0142ka &8\u00bb " + col + bp.getHealth() + "&7/&c" + bp.getMaxHealth() + " &4\u2764"));
        }
        Location target = bp.getLocation().clone().add(0.5, 2.7, 0.5);
        if (as.getLocation().distanceSquared(target) > 0.5) {
            as.teleport(target);
        }
    }

    public void tickHolograms() {
        for (BossPumpkin bp : this.pumpkins.values()) {
            if (bp.isDestroyed()) {
                this.updateHologram(bp);
                if (bp.getCrystal() == null || bp.getCrystal().isDead()) continue;
                this.removeCrystal(bp);
                continue;
            }
            EnderCrystal ec = bp.getCrystal();
            if (ec == null || ec.isDead() || !ec.isValid()) {
                this.spawnCrystal(bp);
            }
            this.updateHologram(bp);
            this.tickCrystalEffect(bp);
        }
    }

    private void tickCrystalEffect(BossPumpkin bp) {
        Location p;
        double z;
        double x;
        double a;
        int i;
        EnderCrystal ec = bp.getCrystal();
        if (ec == null || ec.isDead() || !ec.isValid()) {
            return;
        }
        Location base = bp.getLocation().clone().add(0.5, 1.0, 0.5);
        if (!base.getWorld().isChunkLoaded(base.getBlockX() >> 4, base.getBlockZ() >> 4)) {
            return;
        }
        bp.addSpinAngle(18.0);
        double angle = Math.toRadians(bp.getSpinAngle());
        for (i = 0; i < 3; ++i) {
            a = angle + (double)i * Math.PI * 2.0 / 3.0;
            x = Math.cos(a) * 0.7;
            z = Math.sin(a) * 0.7;
            p = base.clone().add(x, 0.4 + Math.sin(angle * 2.0 + (double)i) * 0.2, z);
            base.getWorld().spawnParticle(Particle.PORTAL, p, 1, 0.02, 0.02, 0.02, 0.1);
            base.getWorld().spawnParticle(Particle.REVERSE_PORTAL, p, 1, 0.01, 0.01, 0.01, 0.02);
        }
        for (i = 0; i < 2; ++i) {
            a = -angle + (double)i * Math.PI;
            x = Math.cos(a) * 0.5;
            z = Math.sin(a) * 0.5;
            p = base.clone().add(x, -0.3, z);
            base.getWorld().spawnParticle(Particle.DRAGON_BREATH, p, 1, 0.02, 0.02, 0.02, 0.01);
        }
        if (Math.random() < 0.3) {
            base.getWorld().spawnParticle(Particle.END_ROD, base.clone().add(0.0, 1.0, 0.0), 1, 0.15, 0.15, 0.15, 0.02);
        }
        if (Math.random() < 0.12) {
            base.getWorld().spawnParticle(Particle.FLAME, base.clone().add(0.0, 0.7, 0.0), 2, 0.2, 0.15, 0.2, 0.02);
        }
        if (Math.random() < 0.05) {
            base.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, base, 2, 0.3, 0.3, 0.3, 0.01);
        }
    }

    private void spawnCrystalEffects(Location loc) {
        Location c = loc.clone().add(0.5, 1.0, 0.5);
        c.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, c, 1);
        c.getWorld().spawnParticle(Particle.PORTAL, c, 30, 0.5, 0.6, 0.5, 1.0);
        c.getWorld().spawnParticle(Particle.DRAGON_BREATH, c.clone().add(0.0, 0.5, 0.0), 12, 0.3, 0.3, 0.3, 0.05);
        c.getWorld().spawnParticle(Particle.END_ROD, c, 10, 0.4, 0.4, 0.4, 0.05);
        c.getWorld().playSound(c, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.7f, 1.4f);
        c.getWorld().playSound(c, Sound.BLOCK_BEACON_ACTIVATE, 0.8f, 1.6f);
        c.getWorld().playSound(c, Sound.BLOCK_PORTAL_AMBIENT, 0.4f, 1.2f);
    }

    private String formatTime(long seconds) {
        if (seconds <= 0L) {
            return "0s";
        }
        if (seconds >= 60L) {
            long m = seconds / 60L;
            long s = seconds % 60L;
            return String.format("%d:%02d", m, s);
        }
        return seconds + "s";
    }

    private long getRefreshRemaining(BossPumpkin bp) {
        long refreshSec = this.plugin.getConfig().getLong("pumpkin-refresh-time", 180L);
        long elapsed = (System.currentTimeMillis() - bp.getDestroyedAt()) / 1000L;
        long left = refreshSec - elapsed;
        return Math.max(0L, left);
    }

    public void removeHologram(BossPumpkin bp) {
        ArmorStand as = bp.getHologram();
        if (as != null && !as.isDead()) {
            as.remove();
        }
        bp.setHologram(null);
    }

    public void damagePumpkin(BossPumpkin bp, Player damager, int damage) {
        if (bp.isDestroyed()) {
            return;
        }
        if (damage <= 0) {
            damage = this.plugin.getConfig().getInt("pumpkin-damage-per-hit", 10);
        }
        boolean wasFull = bp.getHealth() == bp.getMaxHealth();
        bp.setHealth(bp.getHealth() - damage);
        this.updateHologram(bp);
        if (wasFull) {
            String prefix = this.plugin.getConfig().getString("prefix", "&4&lHalloween &r&c\u00bb");
            String msg = this.plugin.getConfig().getString("message-boss-activated", "&4&lSKA\u0141KA HALLOWEEN &r&cZOSTA\u0141A AKTYWOWANA!");
            this.broadcastNear(bp.getLocation(), 30, this.color(prefix + " " + msg + " &7(" + damager.getName() + " &7HP: &c" + bp.getHealth() + "&7/&c" + bp.getMaxHealth() + "&7)"));
        }
        this.trySpawnDefender(bp);
        Location c = bp.getLocation().clone().add(0.5, 1.0, 0.5);
        c.getWorld().playSound(c, Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1.0f, 0.9f);
        c.getWorld().playSound(c, Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 0.8f, 1.7f);
        c.getWorld().spawnParticle(Particle.CRIT, c, 12, 0.3, 0.4, 0.3, 0.15);
        c.getWorld().spawnParticle(Particle.PORTAL, c, 8, 0.3, 0.4, 0.3, 0.7);
        c.getWorld().spawnParticle(Particle.END_ROD, c, 4, 0.2, 0.2, 0.2, 0.03);
        bp.addSpinAngle(40.0);
        if (bp.getHealth() <= 0) {
            this.destroyPumpkin(bp, damager);
        }
    }

    private void trySpawnDefender(BossPumpkin bp) {
        if (bp.isDestroyed()) {
            return;
        }
        bp.getDefenders().removeIf(e -> e == null || e.isDead() || !e.isValid());
        int maxAlive = this.plugin.getConfig().getInt("pumpkin-defender-max-alive", 5);
        if (bp.getDefenders().size() >= maxAlive) {
            return;
        }
        long interval = this.plugin.getConfig().getLong("pumpkin-defender-spawn-interval", 30L);
        long now = System.currentTimeMillis();
        if (now - bp.getLastDefenderSpawn() < interval * 50L) {
            return;
        }
        int amount = this.plugin.getConfig().getInt("pumpkin-defender-per-spawn", 1);
        if (amount < 1) {
            amount = 1;
        }
        Location base = bp.getLocation().clone().add(0.5, 1.0, 0.5);
        double mobHealth = this.plugin.getConfig().getDouble("boss-base-health", 20.0);
        for (int i = 0; i < amount && bp.getDefenders().size() < maxAlive; ++i) {
            Location spawn = base.clone().add((Math.random() - 0.5) * 5.0, 0.0, (Math.random() - 0.5) * 5.0);
            spawn.setY((double)(spawn.getWorld().getHighestBlockYAt(spawn) + 1));
            if (spawn.getBlock().getType().isSolid()) {
                spawn.add(0.0, 1.0, 0.0);
            }
            if (spawn.distanceSquared(base) > 64.0) {
                spawn = base.clone().add((Math.random() - 0.5) * 3.0, 0.0, (Math.random() - 0.5) * 3.0);
            }
            LivingEntity mob = this.plugin.getMobManager().spawnBossDefender(spawn, mobHealth, 1);
            bp.getDefenders().add(mob);
        }
        bp.setLastDefenderSpawn(now);
        this.updateHologram(bp);
    }

    public void activatePumpkin(BossPumpkin bp, Player player) {
        this.damagePumpkin(bp, player, this.plugin.getConfig().getInt("pumpkin-damage-per-hit", 10));
    }

    public void spawnWave(BossPumpkin bp, int wave) {
        this.trySpawnDefender(bp);
    }

    public void onDefenderDeath(BossPumpkin bp) {
        bp.getDefenders().removeIf(e -> e == null || e.isDead() || !e.isValid());
    }

    public void destroyPumpkin(BossPumpkin bp, Player breaker) {
        if (bp.isDestroyed()) {
            return;
        }
        bp.setDestroyed(true);
        bp.setHealth(0);
        for (LivingEntity e : bp.getDefenders()) {
            if (e == null || e.isDead()) continue;
            e.remove();
        }
        bp.getDefenders().clear();
        this.removeCrystal(bp);
        Block b = bp.getLocation().getBlock();
        b.setType(Material.AIR);
        this.updateHologram(bp);
        if (this.plugin.getStatsManager() != null && breaker != null) {
            this.plugin.getStatsManager().addDestroyed(breaker.getUniqueId(), 1);
        }
        RewardManager rm = this.plugin.getRewardManager();
        int min = this.plugin.getConfig().getInt("pumpkin-drop-min", 2);
        int max = this.plugin.getConfig().getInt("pumpkin-drop-max", 4);
        int count = min + (int)(Math.random() * (double)(max - min + 1));
        for (int i = 0; i < count; ++i) {
            double r = Math.random();
            double c1 = this.plugin.getConfig().getDouble("pumpkin-drop-lv1-chance", 60.0) / 100.0;
            double c2 = this.plugin.getConfig().getDouble("pumpkin-drop-lv2-chance", 30.0) / 100.0;
            int lv = r < c1 ? 1 : (r < c1 + c2 ? 2 : 3);
            bp.getLocation().getWorld().dropItemNaturally(bp.getLocation().clone().add(0.5, 0.5, 0.5), rm.createPumpkin(lv, 1));
        }
        if (Math.random() < this.plugin.getConfig().getDouble("pumpkin-bonus-drop-chance", 0.3)) {
            bp.getLocation().getWorld().dropItemNaturally(bp.getLocation().clone().add(0.5, 0.5, 0.5), rm.rollReward(3));
        }
        String prefix = this.plugin.getConfig().getString("prefix", "&4&lHalloween");
        String msg = this.plugin.getConfig().getString("message-pumpkin-destroyed", "&4&lHalloween &r&cSka\u0142ka zosta\u0142a zniszczona!");
        this.plugin.getServer().broadcastMessage(this.color(prefix + " " + msg + " &7(" + breaker.getName() + " &7" + bp.getLocation().getBlockX() + "," + bp.getLocation().getBlockY() + "," + bp.getLocation().getBlockZ() + ")"));
        String dropMsg = this.plugin.getConfig().getString("message-drop-received", "&a&lHalloween &r&aOtrzyma\u0142e\u015b nagrod\u0119!");
        breaker.sendMessage(this.color(prefix + " " + dropMsg));
        Location c = bp.getLocation().clone().add(0.5, 1.0, 0.5);
        c.getWorld().playSound(c, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);
        c.getWorld().playSound(c, Sound.ENTITY_ENDER_DRAGON_DEATH, 0.8f, 1.2f);
        c.getWorld().playSound(c, Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1.0f, 0.7f);
        c.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, c, 2);
        c.getWorld().spawnParticle(Particle.DRAGON_BREATH, c, 20, 0.6, 0.6, 0.6, 0.05);
        c.getWorld().spawnParticle(Particle.PORTAL, c, 40, 0.7, 0.7, 0.7, 1.0);
        c.getWorld().spawnParticle(Particle.END_ROD, c, 16, 0.5, 0.5, 0.5, 0.08);
        c.getWorld().spawnParticle(Particle.SOUL, c, 15, 0.5, 0.5, 0.5, 0.02);
    }

    public void setPumpkinHealth(BossPumpkin bp, int hp, int maxHp) {
        if (maxHp > 0) {
            bp.setMaxHealth(maxHp);
        }
        if (hp >= 0) {
            bp.setHealth(Math.min(hp, bp.getMaxHealth()));
        } else {
            bp.setHealth(bp.getMaxHealth());
        }
        if (bp.isDestroyed() && bp.getHealth() > 0) {
            bp.setDestroyed(false);
            this.spawnBossBlock(bp);
            this.spawnHologram(bp);
        }
        this.updateHologram(bp);
        if (!bp.isDestroyed()) {
            EnderCrystal ec = bp.getCrystal();
            if (ec == null || ec.isDead() || !ec.isValid()) {
                this.spawnCrystal(bp);
            }
            if (bp.getLocation().getBlock().getType() == Material.AIR) {
                bp.getLocation().getBlock().setType(Material.BEDROCK);
            }
        }
    }

    public void tryRefresh() {
        long refreshSec = this.plugin.getConfig().getLong("pumpkin-refresh-time", 180L);
        long refreshMs = refreshSec * 1000L;
        long now = System.currentTimeMillis();
        for (BossPumpkin bp : this.pumpkins.values()) {
            if (bp.isDestroyed() && now - bp.getDestroyedAt() >= refreshMs) {
                bp.setDestroyed(false);
                bp.setHealth(bp.getMaxHealth());
                for (LivingEntity e : bp.getDefenders()) {
                    if (e == null) continue;
                    e.remove();
                }
                bp.getDefenders().clear();
                this.spawnBossBlock(bp);
                this.spawnHologram(bp);
                this.updateHologram(bp);
                String prefix = this.plugin.getConfig().getString("prefix", "&4&lHalloween");
                String msg = this.plugin.getConfig().getString("message-pumpkin-refreshed", "&4&lHalloween &r&eSka\u0142ka odnowi\u0142a si\u0119!");
                this.broadcastNear(bp.getLocation(), 40, this.color(prefix + " " + msg + " &7HP: &c" + bp.getHealth()));
                this.spawnCrystalEffects(bp.getLocation());
                continue;
            }
            if (bp.isDestroyed()) continue;
            this.updateHologram(bp);
        }
    }

    public void respawnAll() {
        for (BossPumpkin bp : this.pumpkins.values()) {
            bp.setDestroyed(false);
            bp.setHealth(bp.getMaxHealth());
            for (LivingEntity e : bp.getDefenders()) {
                if (e == null) continue;
                e.remove();
            }
            bp.getDefenders().clear();
            this.removeCrystal(bp);
            this.spawnBossBlock(bp);
            this.spawnHologram(bp);
            this.updateHologram(bp);
        }
    }

    public void spawnWorldPumpkin(Location loc) {
        if (this.worldPumpkins.size() >= this.plugin.getConfig().getInt("max-world-pumpkins", 10)) {
            return;
        }
        loc.getBlock().setType(Material.PUMPKIN);
        this.worldPumpkins.put(this.key(loc), loc);
    }

    public boolean isWorldPumpkin(Block block) {
        return this.worldPumpkins.containsKey(this.key(block.getLocation())) && block.getType() == Material.PUMPKIN;
    }

    public void collectWorldPumpkin(Block block, Player player) {
        this.worldPumpkins.remove(this.key(block.getLocation()));
        block.setType(Material.AIR);
        player.getInventory().addItem(new ItemStack[]{this.plugin.getRewardManager().createPumpkin(1, 1)});
        player.sendMessage(this.color(this.plugin.getConfig().getString("prefix", "") + " &aZebra\u0142e\u015b Straszna Dyni\u0119!"));
        block.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, block.getLocation().clone().add(0.5, 0.5, 0.5), 10, 0.3, 0.3, 0.3, 0.1);
        block.getWorld().playSound(block.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.4f);
    }

    public void cleanup() {
        for (BossPumpkin bp : this.pumpkins.values()) {
            for (LivingEntity e : bp.getDefenders()) {
                if (e == null || e.isDead()) continue;
                e.remove();
            }
            this.removeHologram(bp);
            this.removeCrystal(bp);
        }
        for (Location l : this.worldPumpkins.values()) {
            if (l.getBlock().getType() != Material.PUMPKIN) continue;
            l.getBlock().setType(Material.AIR);
        }
    }

    private void broadcastNear(Location loc, int radius, String msg) {
        for (Player p : loc.getWorld().getPlayers()) {
            if (!(p.getLocation().distanceSquared(loc) < (double)(radius * radius))) continue;
            p.sendMessage(msg);
        }
    }

    private boolean isCrystal(EntityType t) {
        String n = t.name();
        return n.equals("END_CRYSTAL") || n.equals("ENDER_CRYSTAL");
    }

    private boolean isCrystal(Entity e) {
        return this.isCrystal(e.getType());
    }

    private EntityType getCrystalType() {
        try {
            return EntityType.valueOf((String)"END_CRYSTAL");
        }
        catch (Exception ex) {
            try {
                return EntityType.valueOf((String)"ENDER_CRYSTAL");
            }
            catch (Exception ex2) {
                return EntityType.ENDER_PEARL;
            }
        }
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)s);
    }
}

