/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.ArmorStand
 *  org.bukkit.entity.EnderCrystal
 *  org.bukkit.entity.LivingEntity
 */
package pl.orbitcode.halloween.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.LivingEntity;

public class BossPumpkin {
    private final Location location;
    private boolean destroyed;
    private long destroyedAt;
    private int maxHealth;
    private int health;
    private ArmorStand hologram;
    private EnderCrystal crystal;
    private double spinAngle = 0.0;
    private final List<LivingEntity> defenders = new ArrayList<LivingEntity>();
    private long lastDefenderSpawn = 0L;

    public BossPumpkin(Location location, int maxHealth) {
        this.location = location;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.destroyed = false;
    }

    public BossPumpkin(Location location) {
        this(location, 250);
    }

    public Location getLocation() {
        return this.location;
    }

    public boolean isDestroyed() {
        return this.destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
        if (destroyed) {
            this.destroyedAt = System.currentTimeMillis();
        }
    }

    public long getDestroyedAt() {
        return this.destroyedAt;
    }

    public int getMaxHealth() {
        return this.maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getHealth() {
        return this.health;
    }

    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(health, this.maxHealth));
    }

    public ArmorStand getHologram() {
        return this.hologram;
    }

    public void setHologram(ArmorStand hologram) {
        this.hologram = hologram;
    }

    public EnderCrystal getCrystal() {
        return this.crystal;
    }

    public void setCrystal(EnderCrystal crystal) {
        this.crystal = crystal;
    }

    public double getSpinAngle() {
        return this.spinAngle;
    }

    public void setSpinAngle(double v) {
        this.spinAngle = v;
    }

    public void addSpinAngle(double d) {
        this.spinAngle += d;
        if (this.spinAngle > 360.0) {
            this.spinAngle -= 360.0;
        }
    }

    public List<LivingEntity> getDefenders() {
        return this.defenders;
    }

    public long getLastDefenderSpawn() {
        return this.lastDefenderSpawn;
    }

    public void setLastDefenderSpawn(long t) {
        this.lastDefenderSpawn = t;
    }

    public boolean isDefendersAlive() {
        this.defenders.removeIf(e -> e == null || e.isDead() || !e.isValid());
        return !this.defenders.isEmpty();
    }

    public boolean isFighting() {
        return !this.destroyed && this.health < this.maxHealth && this.health > 0;
    }

    public int getCurrentWave() {
        return 0;
    }

    public void setCurrentWave(int v) {
    }

    public int getMaxWaves() {
        return 1;
    }

    public void setMaxWaves(int v) {
    }

    public UUID getFightingPlayer() {
        return null;
    }

    public void setFightingPlayer(UUID uuid) {
    }

    public void setFighting(boolean b) {
    }
}

