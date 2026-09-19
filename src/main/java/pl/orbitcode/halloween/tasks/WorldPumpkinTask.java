/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 */
package pl.orbitcode.halloween.tasks;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import pl.orbitcode.halloween.HalloweenPlugin;

public class WorldPumpkinTask
implements Runnable {
    private final HalloweenPlugin plugin;

    public WorldPumpkinTask(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        int max = this.plugin.getConfig().getInt("max-world-pumpkins", 10);
        for (World w : this.plugin.getServer().getWorlds()) {
            int z;
            int y;
            Player p;
            Location center;
            int x;
            Location loc;
            if (w.getPlayers().isEmpty() || !(loc = new Location(w, (double)(x = (center = (p = (Player)w.getPlayers().get((int)(Math.random() * (double)w.getPlayers().size()))).getLocation()).getBlockX() + (int)(Math.random() * 40.0 - 20.0)), (double)((y = w.getHighestBlockYAt(x, z = center.getBlockZ() + (int)(Math.random() * 40.0 - 20.0))) + 1), (double)z)).getBlock().getType().isAir() || !loc.clone().subtract(0.0, 1.0, 0.0).getBlock().getType().isSolid()) continue;
            this.plugin.getPumpkinManager().spawnWorldPumpkin(loc);
            break;
        }
    }
}

