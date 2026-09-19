/*
 * Decompiled with CFR 0.152.
 */
package pl.orbitcode.halloween.tasks;

import pl.orbitcode.halloween.HalloweenPlugin;

public class BossHealthTask
implements Runnable {
    private final HalloweenPlugin plugin;

    public BossHealthTask(HalloweenPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.plugin.getMobManager().updateAllBossNames();
    }
}

