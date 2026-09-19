/*
 * Decompiled with CFR 0.152.
 */
package pl.orbitcode.halloween.tasks;

import pl.orbitcode.halloween.HalloweenPlugin;

public class MobSpawnTask
implements Runnable {
    private final HalloweenPlugin plugin;

    public MobSpawnTask(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.plugin.getMobManager().spawnRandomEventMobs();
    }
}

