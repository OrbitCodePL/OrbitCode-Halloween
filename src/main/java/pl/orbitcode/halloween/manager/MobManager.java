/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.NamespacedKey
 *  org.bukkit.World
 *  org.bukkit.attribute.Attribute
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.persistence.PersistentDataType
 *  org.bukkit.plugin.Plugin
 */
package pl.orbitcode.halloween.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import pl.orbitcode.halloween.HalloweenPlugin;
import pl.orbitcode.halloween.model.BossPumpkin;

public class MobManager {
    private final HalloweenPlugin plugin;
    private final Set<UUID> eventMobs = ConcurrentHashMap.newKeySet();
    private final Set<UUID> bossMobs = ConcurrentHashMap.newKeySet();
    private final NamespacedKey key;
    private final NamespacedKey waveKey;
    private static final EntityType[] EVENT_TYPES = new EntityType[]{EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER, EntityType.HUSK, EntityType.STRAY};
    private static final EntityType[] BOSS_TYPES = new EntityType[]{EntityType.ZOMBIE, EntityType.SKELETON, EntityType.WITHER_SKELETON};

    public MobManager(HalloweenPlugin plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey((Plugin)plugin, "halloween_mob");
        this.waveKey = new NamespacedKey((Plugin)plugin, "halloween_wave");
    }

    public LivingEntity spawnBossDefender(Location loc, double health, int wave) {
        EntityType type = BOSS_TYPES[(int)(Math.random() * (double)BOSS_TYPES.length)];
        LivingEntity e = (LivingEntity)loc.getWorld().spawnEntity(loc, type);
        e.setCustomNameVisible(true);
        e.setRemoveWhenFarAway(false);
        if (e.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
            e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
            e.setHealth(health);
        }
        e.getPersistentDataContainer().set(this.key, PersistentDataType.STRING, (Object)"boss");
        e.getPersistentDataContainer().set(this.waveKey, PersistentDataType.INTEGER, (Object)wave);
        this.bossMobs.add(e.getUniqueId());
        this.updateBossName(e);
        return e;
    }

    public void updateBossName(LivingEntity e) {
        double ratio;
        if (e == null || e.isDead() || !e.isValid()) {
            return;
        }
        double max = e.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null ? e.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() : 20.0;
        double cur = Math.max(0.0, e.getHealth());
        int curInt = (int)Math.ceil(cur);
        int maxInt = (int)Math.ceil(max);
        double d = ratio = max > 0.0 ? cur / max : 0.0;
        String hpColor = ratio > 0.6 ? "&a" : (ratio > 0.3 ? "&e" : "&c");
        e.setCustomName(this.color("&4&lObro\u0144ca Ska\u0142ki &7[" + hpColor + curInt + "&7/&c" + maxInt + "\u2764&7]"));
        e.setCustomNameVisible(true);
    }

    public void updateAllBossNames() {
        for (BossPumpkin bp : this.plugin.getPumpkinManager().getAll()) {
            for (LivingEntity def : new ArrayList<LivingEntity>(bp.getDefenders())) {
                if (def == null || def.isDead() || !def.isValid()) continue;
                this.updateBossName(def);
            }
        }
        for (UUID id : new HashSet<UUID>(this.bossMobs)) {
            Entity ent = this.plugin.getServer().getEntity(id);
            if (!(ent instanceof LivingEntity)) continue;
            this.updateBossName((LivingEntity)ent);
        }
    }

    public void spawnEventMob(Location loc) {
        EntityType type = EVENT_TYPES[(int)(Math.random() * (double)EVENT_TYPES.length)];
        World w = loc.getWorld();
        LivingEntity e = (LivingEntity)w.spawnEntity(loc, type);
        e.setCustomName(this.color("&6&lHalloween Mob"));
        e.setCustomNameVisible(true);
        e.setRemoveWhenFarAway(true);
        double health = this.plugin.getConfig().getDouble("boss-base-health", 20.0);
        if (e.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
            e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
            e.setHealth(health);
        }
        e.getPersistentDataContainer().set(this.key, PersistentDataType.STRING, (Object)"event");
        this.eventMobs.add(e.getUniqueId());
    }

    public void spawnRandomEventMobs() {
        for (World w : this.plugin.getServer().getWorlds()) {
            if (w.getPlayers().isEmpty()) continue;
            Player p = (Player)w.getPlayers().get((int)(Math.random() * (double)w.getPlayers().size()));
            Location loc = p.getLocation().clone().add((Math.random() - 0.5) * 30.0, 0.0, (Math.random() - 0.5) * 30.0);
            loc.setY((double)(w.getHighestBlockYAt(loc) + 1));
            if (loc.getBlock().getType().isSolid()) {
                loc.add(0.0, 1.0, 0.0);
            }
            this.spawnEventMob(loc);
        }
    }

    public boolean isEventMob(Entity e) {
        return e.getPersistentDataContainer().has(this.key, PersistentDataType.STRING) || this.eventMobs.contains(e.getUniqueId()) || this.bossMobs.contains(e.getUniqueId());
    }

    public boolean isBossMob(Entity e) {
        String v = (String)e.getPersistentDataContainer().get(this.key, PersistentDataType.STRING);
        return "boss".equals(v) || this.bossMobs.contains(e.getUniqueId());
    }

    public boolean isRegularEventMob(Entity e) {
        String v = (String)e.getPersistentDataContainer().get(this.key, PersistentDataType.STRING);
        return "event".equals(v) || this.eventMobs.contains(e.getUniqueId()) && !this.bossMobs.contains(e.getUniqueId());
    }

    private int getDropChance(EntityType type, boolean isBoss) {
        String base;
        String string = base = isBoss ? "defender-drop-chances" : "mob-drop-chances";
        if (this.plugin.getConfig().isConfigurationSection(base)) {
            if (this.plugin.getConfig().isInt(base + "." + type.name())) {
                return this.plugin.getConfig().getInt(base + "." + type.name());
            }
            if (this.plugin.getConfig().isInt(base + "." + type.name().toLowerCase())) {
                return this.plugin.getConfig().getInt(base + "." + type.name().toLowerCase());
            }
        }
        if (isBoss && this.plugin.getConfig().isInt("defender-drop-chance")) {
            return this.plugin.getConfig().getInt("defender-drop-chance", 35);
        }
        return this.plugin.getConfig().getInt("mob-dye-chance", 25);
    }

    private ItemStack rollPumpkinForMob(EntityType type, boolean isBoss) {
        double r = Math.random() * 100.0;
        if (isBoss) {
            double c1 = this.plugin.getConfig().getDouble("defender-drop-lv1-weight", 50.0);
            double c2 = this.plugin.getConfig().getDouble("defender-drop-lv2-weight", 35.0);
            if (r < c1) {
                return this.plugin.getRewardManager().createPumpkin(1, 1);
            }
            if (r < c1 + c2) {
                return this.plugin.getRewardManager().createPumpkin(2, 1);
            }
            return this.plugin.getRewardManager().createPumpkin(3, 1);
        }
        switch (type) {
            case WITHER_SKELETON: {
                if (r < 40.0) {
                    return this.plugin.getRewardManager().createPumpkin(1, 1);
                }
                if (r < 75.0) {
                    return this.plugin.getRewardManager().createPumpkin(2, 1);
                }
                return this.plugin.getRewardManager().createPumpkin(3, 1);
            }
            case ZOMBIE: 
            case HUSK: {
                if (r < 80.0) {
                    return this.plugin.getRewardManager().createPumpkin(1, 1);
                }
                if (r < 95.0) {
                    return this.plugin.getRewardManager().createPumpkin(2, 1);
                }
                return this.plugin.getRewardManager().createPumpkin(3, 1);
            }
            case SKELETON: 
            case STRAY: {
                if (r < 70.0) {
                    return this.plugin.getRewardManager().createPumpkin(1, 1);
                }
                if (r < 90.0) {
                    return this.plugin.getRewardManager().createPumpkin(2, 1);
                }
                return this.plugin.getRewardManager().createPumpkin(3, 1);
            }
        }
        if (r < 85.0) {
            return this.plugin.getRewardManager().createPumpkin(1, 1);
        }
        if (r < 97.0) {
            return this.plugin.getRewardManager().createPumpkin(2, 1);
        }
        return this.plugin.getRewardManager().createPumpkin(3, 1);
    }

    public void handleDeath(LivingEntity entity) {
        UUID id = entity.getUniqueId();
        boolean wasBoss = this.bossMobs.remove(id);
        boolean wasEvent = this.eventMobs.remove(id);
        EntityType type = entity.getType();
        Player killer = entity.getKiller();
        if (killer != null && (wasBoss || wasEvent || this.isRegularEventMob((Entity)entity)) && this.plugin.getStatsManager() != null) {
            this.plugin.getStatsManager().addKilled(killer.getUniqueId(), 1);
        }
        if (wasBoss) {
            for (BossPumpkin bp : this.plugin.getPumpkinManager().getAll()) {
                bp.getDefenders().removeIf(d -> d == null || d.getUniqueId().equals(id) || d.isDead());
                if (!wasBoss) continue;
                this.plugin.getPumpkinManager().onDefenderDeath(bp);
                int chance = this.getDropChance(type, true);
                if (Math.random() * 100.0 < (double)chance) {
                    ItemStack drop = this.rollPumpkinForMob(type, true);
                    entity.getWorld().dropItemNaturally(entity.getLocation(), drop);
                }
                break;
            }
        } else if (wasEvent || this.isRegularEventMob((Entity)entity)) {
            this.eventMobs.remove(id);
            int chance = this.getDropChance(type, false);
            if (Math.random() * 100.0 < (double)chance) {
                ItemStack drop = this.rollPumpkinForMob(type, false);
                entity.getWorld().dropItemNaturally(entity.getLocation(), drop);
            }
        }
    }

    public void cleanup() {
        Entity e;
        for (UUID id : new HashSet<UUID>(this.eventMobs)) {
            e = this.plugin.getServer().getEntity(id);
            if (e == null) continue;
            e.remove();
        }
        for (UUID id : new HashSet<UUID>(this.bossMobs)) {
            e = this.plugin.getServer().getEntity(id);
            if (e == null) continue;
            e.remove();
        }
        this.eventMobs.clear();
        this.bossMobs.clear();
    }

    public NamespacedKey getKey() {
        return this.key;
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)s);
    }
}

