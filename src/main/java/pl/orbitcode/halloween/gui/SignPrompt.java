/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.Sign
 *  org.bukkit.block.sign.Side
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.SignChangeEvent
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.plugin.Plugin
 */
package pl.orbitcode.halloween.gui;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import pl.orbitcode.halloween.HalloweenPlugin;

public class SignPrompt
implements Listener {
    private static final Map<UUID, Consumer<String>> pending = new ConcurrentHashMap<UUID, Consumer<String>>();
    private static final Map<UUID, Block> signBlocks = new ConcurrentHashMap<UUID, Block>();
    private static final Map<UUID, Material> originalMat = new ConcurrentHashMap<UUID, Material>();

    public static void open(Player player, int tier, Consumer<String> callback) {
        Block block = SignPrompt.findFreeBlock(player);
        if (block == null) {
            player.sendMessage("\u00a7cNie mozna otworzyc tabliczki, wpisz procent na czacie (np. 5.5):");
            pending.put(player.getUniqueId(), callback);
            return;
        }
        originalMat.put(player.getUniqueId(), block.getType());
        signBlocks.put(player.getUniqueId(), block);
        block.setType(Material.OAK_SIGN);
        try {
            Sign sign = (Sign)block.getState();
            sign.getSide(Side.FRONT).setLine(0, "Ustaw %");
            sign.getSide(Side.FRONT).setLine(1, "Tier " + tier);
            sign.getSide(Side.FRONT).setLine(2, "np. 5.5");
            sign.getSide(Side.FRONT).setLine(3, "wpisz liczbe");
            sign.update();
            pending.put(player.getUniqueId(), callback);
            try {
                player.openSign(sign, Side.FRONT);
            }
            catch (NoSuchMethodError e) {
                try {
                    player.getClass().getMethod("openSign", Sign.class).invoke((Object)player, sign);
                }
                catch (Exception exception) {}
            }
        }
        catch (Exception ex) {
            block.setType(originalMat.getOrDefault(player.getUniqueId(), Material.AIR));
            pending.put(player.getUniqueId(), callback);
            player.sendMessage("\u00a77Wpisz procent na czacie (np. 5.5 lub 5,5%):");
        }
    }

    private static Block findFreeBlock(Player p) {
        try {
            for (int dy = 5; dy < 15; ++dy) {
                Block b = p.getLocation().clone().add(0.0, (double)dy, 0.0).getBlock();
                if (b.getType() != Material.AIR) continue;
                return b;
            }
            Block b = p.getWorld().getBlockAt(p.getLocation().getBlockX(), 250, p.getLocation().getBlockZ());
            if (b.getType() == Material.AIR || b.getType() == Material.OAK_SIGN) {
                return b;
            }
            b.setType(Material.AIR);
            return b;
        }
        catch (Exception e) {
            return null;
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        String input;
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();
        Consumer<String> cb = pending.remove(id);
        if (cb == null) {
            return;
        }
        Block b = signBlocks.remove(id);
        Material orig = originalMat.remove(id);
        if (b != null && orig != null) {
            HalloweenPlugin.getInstance().getServer().getScheduler().runTask((Plugin)HalloweenPlugin.getInstance(), () -> {
                if (b.getType() == Material.OAK_SIGN) {
                    b.setType(orig);
                }
            });
        }
        if ((input = e.getLine(0)) == null) {
            input = "";
        }
        if (input.trim().isEmpty()) {
            for (int i = 1; i < 4; ++i) {
                String l = e.getLine(i);
                if (l == null || l.trim().isEmpty() || l.toLowerCase().contains("ustaw") || l.toLowerCase().contains("tier") || l.toLowerCase().contains("np.")) continue;
                input = l;
                break;
            }
        }
        String finalInput = input = input.trim();
        HalloweenPlugin.getInstance().getServer().getScheduler().runTask((Plugin)HalloweenPlugin.getInstance(), () -> cb.accept(finalInput));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        UUID id = e.getPlayer().getUniqueId();
        pending.remove(id);
        Block b = signBlocks.remove(id);
        Material orig = originalMat.remove(id);
        if (b != null && orig != null && b.getType() == Material.OAK_SIGN) {
            b.setType(orig);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        String msg;
        UUID id = e.getPlayer().getUniqueId();
        Consumer<String> cb = pending.get(id);
        if (cb != null && !signBlocks.containsKey(id) && (msg = e.getMessage().trim()).matches(".*\\d.*")) {
            e.setCancelled(true);
            pending.remove(id);
            String finalInput = msg;
            HalloweenPlugin.getInstance().getServer().getScheduler().runTask((Plugin)HalloweenPlugin.getInstance(), () -> cb.accept(finalInput));
            e.getPlayer().sendMessage("\u00a77Ustawiono procent: \u00a7e" + finalInput);
        }
    }
}

