/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 */
package pl.orbitcode.halloween;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pl.orbitcode.halloween.commands.DropEditCommand;
import pl.orbitcode.halloween.commands.ExchangeCommand;
import pl.orbitcode.halloween.commands.GivePumpkinCommand;
import pl.orbitcode.halloween.commands.HelpCommand;
import pl.orbitcode.halloween.commands.OpenCommand;
import pl.orbitcode.halloween.commands.PlaceCommand;
import pl.orbitcode.halloween.commands.RemoveCommand;
import pl.orbitcode.halloween.commands.RespawnCommand;
import pl.orbitcode.halloween.commands.SetHpCommand;
import pl.orbitcode.halloween.commands.StatsCommand;
import pl.orbitcode.halloween.gui.DropEditorGui;
import pl.orbitcode.halloween.gui.SignPrompt;
import pl.orbitcode.halloween.listeners.BlockBreakListener;
import pl.orbitcode.halloween.listeners.BlockDamageListener;
import pl.orbitcode.halloween.listeners.BossHealthListener;
import pl.orbitcode.halloween.listeners.CrystalListener;
import pl.orbitcode.halloween.listeners.EntityDeathListener;
import pl.orbitcode.halloween.listeners.InventoryClickListener;
import pl.orbitcode.halloween.listeners.PlayerInteractListener;
import pl.orbitcode.halloween.listeners.PlayerJoinListener;
import pl.orbitcode.halloween.manager.LocationManager;
import pl.orbitcode.halloween.manager.MobManager;
import pl.orbitcode.halloween.manager.PumpkinManager;
import pl.orbitcode.halloween.manager.StatsManager;
import pl.orbitcode.halloween.placeholder.HalloweenExpansion;
import pl.orbitcode.halloween.tasks.BossHealthTask;
import pl.orbitcode.halloween.tasks.MobSpawnTask;
import pl.orbitcode.halloween.tasks.PumpkinRefreshTask;
import pl.orbitcode.halloween.tasks.WorldPumpkinTask;
import pl.orbitcode.halloween.util.RewardManager;

public class HalloweenPlugin
extends JavaPlugin {
    private static HalloweenPlugin instance;
    private PumpkinManager pumpkinManager;
    private MobManager mobManager;
    private LocationManager locationManager;
    private RewardManager rewardManager;
    private StatsManager statsManager;
    private DropEditorGui dropEditorGui;
    private SignPrompt signPrompt;

    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        this.loadManagers();
        this.registerEvents();
        this.registerCommands();
        this.registerTasks();
        this.hookPlaceholderAPI();
        this.getLogger().info("OrbitCode Halloween Plugin zaladowany! Serwer: " + this.getServer().getVersion());
        this.getLogger().info("Autor: xGamereq | Witryna: orbitcode.pl");
    }

    public void onDisable() {
        if (this.pumpkinManager != null) {
            this.pumpkinManager.cleanup();
        }
        if (this.mobManager != null) {
            this.mobManager.cleanup();
        }
        if (this.statsManager != null) {
            this.statsManager.save();
        }
        this.getLogger().info("OrbitCode Halloween Plugin wylaczony!");
    }

    private void loadManagers() {
        this.locationManager = new LocationManager(this);
        this.statsManager = new StatsManager(this);
        this.rewardManager = new RewardManager(this);
        this.mobManager = new MobManager(this);
        this.pumpkinManager = new PumpkinManager(this);
        this.dropEditorGui = new DropEditorGui(this);
        this.signPrompt = new SignPrompt();
    }

    private void hookPlaceholderAPI() {
        if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            try {
                HalloweenExpansion expansion = new HalloweenExpansion(this);
                if (expansion.register()) {
                    this.getLogger().info("Zarejestrowano PlaceholderAPI: %halloween_destroyed_rocks%, %halloween_killed_mobs%, %halloween_opened_pumpkins%");
                }
            }
            catch (Exception e) {
                this.getLogger().warning("Nie udalo sie zarejestrowac PlaceholderAPI: " + e.getMessage());
            }
        } else {
            this.getLogger().info("PlaceholderAPI nie znaleziony - placeholdery %halloween_*% nieaktywne (zainstaluj PAPI aby wlaczyc)");
        }
    }

    private void registerEvents() {
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents((Listener)new BlockBreakListener(this), (Plugin)this);
        pm.registerEvents((Listener)new PlayerInteractListener(this), (Plugin)this);
        pm.registerEvents((Listener)new EntityDeathListener(this), (Plugin)this);
        pm.registerEvents((Listener)new InventoryClickListener(this), (Plugin)this);
        pm.registerEvents((Listener)new PlayerJoinListener(this), (Plugin)this);
        pm.registerEvents((Listener)new BlockDamageListener(this), (Plugin)this);
        pm.registerEvents((Listener)new BossHealthListener(this), (Plugin)this);
        pm.registerEvents((Listener)new CrystalListener(this), (Plugin)this);
        pm.registerEvents((Listener)this.dropEditorGui, (Plugin)this);
        pm.registerEvents((Listener)this.signPrompt, (Plugin)this);
    }

    private void registerCommands() {
        this.getCommand("halloweenplace").setExecutor((CommandExecutor)new PlaceCommand(this));
        this.getCommand("halloweenremove").setExecutor((CommandExecutor)new RemoveCommand(this));
        this.getCommand("halloweensethp").setExecutor((CommandExecutor)new SetHpCommand(this));
        this.getCommand("halloweenstats").setExecutor((CommandExecutor)new StatsCommand(this));
        this.getCommand("halloweenopen").setExecutor((CommandExecutor)new OpenCommand(this));
        this.getCommand("exchange").setExecutor((CommandExecutor)new ExchangeCommand(this));
        this.getCommand("halloweenrespawn").setExecutor((CommandExecutor)new RespawnCommand(this));
        this.getCommand("givepumpkin").setExecutor((CommandExecutor)new GivePumpkinCommand(this));
        this.getCommand("halloweenhelp").setExecutor((CommandExecutor)new HelpCommand(this));
        this.getCommand("halloweendrop").setExecutor((CommandExecutor)new DropEditCommand(this));
    }

    private void registerTasks() {
        long refreshTime = this.getConfig().getLong("pumpkin-refresh-time", 180L) * 20L;
        long mobRefresh = this.getConfig().getLong("mob-refresh-time", 60L);
        long worldPumpkinInterval = this.getConfig().getLong("world-pumpkin-interval", 120L) * 20L;
        this.getServer().getScheduler().runTaskTimer((Plugin)this, (Runnable)new PumpkinRefreshTask(this), refreshTime, refreshTime);
        this.getServer().getScheduler().runTaskTimer((Plugin)this, (Runnable)new MobSpawnTask(this), 20L, mobRefresh);
        this.getServer().getScheduler().runTaskTimer((Plugin)this, (Runnable)new WorldPumpkinTask(this), 60L, worldPumpkinInterval);
        this.getServer().getScheduler().runTaskTimer((Plugin)this, (Runnable)new BossHealthTask(this), 10L, 2L);
        this.getServer().getScheduler().runTaskTimer((Plugin)this, () -> {
            if (this.pumpkinManager != null) {
                this.pumpkinManager.tickHolograms();
            }
        }, 20L, 20L);
    }

    public static HalloweenPlugin getInstance() {
        return instance;
    }

    public PumpkinManager getPumpkinManager() {
        return this.pumpkinManager;
    }

    public MobManager getMobManager() {
        return this.mobManager;
    }

    public LocationManager getLocationManager() {
        return this.locationManager;
    }

    public RewardManager getRewardManager() {
        return this.rewardManager;
    }

    public StatsManager getStatsManager() {
        return this.statsManager;
    }

    public DropEditorGui getDropEditorGui() {
        return this.dropEditorGui;
    }

    public SignPrompt getSignPrompt() {
        return this.signPrompt;
    }
}

