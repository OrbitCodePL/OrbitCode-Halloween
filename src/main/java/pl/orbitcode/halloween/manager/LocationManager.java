/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 */
package pl.orbitcode.halloween.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.orbitcode.halloween.HalloweenPlugin;

public class LocationManager {
    private final HalloweenPlugin plugin;
    private final File file;
    private FileConfiguration config;

    public LocationManager(HalloweenPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "locations.yml");
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
    }

    public void save() {
        try {
            this.config.save(this.file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addLocation(Location loc) {
        List list = this.config.getStringList("pumpkins");
        list.add(this.serialize(loc));
        this.config.set("pumpkins", (Object)list);
        this.save();
    }

    public void removeLocation(Location loc) {
        List list = this.config.getStringList("pumpkins");
        list.remove(this.serialize(loc));
        this.config.set("pumpkins", (Object)list);
        this.save();
    }

    public List<Location> getLocations() {
        ArrayList<Location> out = new ArrayList<Location>();
        for (String s : this.config.getStringList("pumpkins")) {
            Location l = this.deserialize(s);
            if (l == null) continue;
            out.add(l);
        }
        return out;
    }

    public void clear() {
        this.config.set("pumpkins", new ArrayList());
        this.save();
    }

    private String serialize(Location l) {
        return l.getWorld().getName() + ";" + l.getBlockX() + ";" + l.getBlockY() + ";" + l.getBlockZ();
    }

    private Location deserialize(String s) {
        try {
            String[] p = s.split(";");
            World w = this.plugin.getServer().getWorld(p[0]);
            if (w == null) {
                return null;
            }
            return new Location(w, (double)Integer.parseInt(p[1]), (double)Integer.parseInt(p[2]), (double)Integer.parseInt(p[3]));
        }
        catch (Exception e) {
            return null;
        }
    }
}

