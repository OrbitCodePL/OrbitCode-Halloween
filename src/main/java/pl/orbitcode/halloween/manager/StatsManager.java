/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.plugin.Plugin
 */
package pl.orbitcode.halloween.manager;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import pl.orbitcode.halloween.HalloweenPlugin;

public class StatsManager {
    private final HalloweenPlugin plugin;
    private final File file;
    private FileConfiguration config;
    private final Map<UUID, PlayerStats> cache = new ConcurrentHashMap<UUID, PlayerStats>();

    public StatsManager(HalloweenPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "stats.yml");
        this.load();
    }

    private void load() {
        if (!this.file.exists()) {
            try {
                this.plugin.getDataFolder().mkdirs();
                this.file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration((File)this.file);
        this.cache.clear();
        if (this.config.isConfigurationSection("stats")) {
            for (String key : this.config.getConfigurationSection("stats").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    PlayerStats ps = new PlayerStats();
                    String base = "stats." + key + ".";
                    ps.destroyedRocks = this.config.getInt(base + "destroyed-rocks", 0);
                    if (ps.destroyedRocks == 0) {
                        ps.destroyedRocks = this.config.getInt(base + "destroyed", 0);
                    }
                    ps.killedMobs = this.config.getInt(base + "killed-mobs", 0);
                    if (ps.killedMobs == 0) {
                        ps.killedMobs = this.config.getInt(base + "killed", 0);
                    }
                    ps.openedPumpkins = this.config.getInt(base + "opened-pumpkins", 0);
                    if (ps.openedPumpkins == 0) {
                        ps.openedPumpkins = this.config.getInt(base + "opened", 0);
                    }
                    this.cache.put(uuid, ps);
                }
                catch (Exception exception) {}
            }
        }
    }

    public void save() {
        for (Map.Entry<UUID, PlayerStats> e : this.cache.entrySet()) {
            String base = "stats." + e.getKey().toString() + ".";
            this.config.set(base + "destroyed-rocks", (Object)e.getValue().destroyedRocks);
            this.config.set(base + "killed-mobs", (Object)e.getValue().killedMobs);
            this.config.set(base + "opened-pumpkins", (Object)e.getValue().openedPumpkins);
        }
        try {
            this.config.save(this.file);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveAsync() {
        this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, this::save);
    }

    private PlayerStats getOrCreate(UUID uuid) {
        return this.cache.computeIfAbsent(uuid, k -> new PlayerStats());
    }

    public int getDestroyed(UUID uuid) {
        return this.getOrCreate((UUID)uuid).destroyedRocks;
    }

    public int getKilled(UUID uuid) {
        return this.getOrCreate((UUID)uuid).killedMobs;
    }

    public int getOpened(UUID uuid) {
        return this.getOrCreate((UUID)uuid).openedPumpkins;
    }

    public void addDestroyed(UUID uuid, int amount) {
        PlayerStats ps = this.getOrCreate(uuid);
        ps.destroyedRocks += amount;
        this.saveAsync();
    }

    public void addKilled(UUID uuid, int amount) {
        PlayerStats ps = this.getOrCreate(uuid);
        ps.killedMobs += amount;
        this.saveAsync();
    }

    public void addOpened(UUID uuid, int amount) {
        PlayerStats ps = this.getOrCreate(uuid);
        ps.openedPumpkins += amount;
        this.saveAsync();
    }

    public void setDestroyed(UUID uuid, int v) {
        this.getOrCreate((UUID)uuid).destroyedRocks = Math.max(0, v);
        this.saveAsync();
    }

    public void setKilled(UUID uuid, int v) {
        this.getOrCreate((UUID)uuid).killedMobs = Math.max(0, v);
        this.saveAsync();
    }

    public void setOpened(UUID uuid, int v) {
        this.getOrCreate((UUID)uuid).openedPumpkins = Math.max(0, v);
        this.saveAsync();
    }

    public Map<UUID, PlayerStats> getCache() {
        return this.cache;
    }

    public int getTotalDestroyed() {
        int s = 0;
        for (PlayerStats ps : this.cache.values()) {
            s += ps.destroyedRocks;
        }
        return s;
    }

    public int getTotalKilled() {
        int s = 0;
        for (PlayerStats ps : this.cache.values()) {
            s += ps.killedMobs;
        }
        return s;
    }

    public int getTotalOpened() {
        int s = 0;
        for (PlayerStats ps : this.cache.values()) {
            s += ps.openedPumpkins;
        }
        return s;
    }

    public static class PlayerStats {
        public int destroyedRocks = 0;
        public int killedMobs = 0;
        public int openedPumpkins = 0;
    }
}

