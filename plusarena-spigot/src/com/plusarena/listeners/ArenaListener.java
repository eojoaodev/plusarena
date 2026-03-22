package com.plusarena.listeners;

import com.plusarena.PlusArena;
import com.plusarena.managers.ArenaManager;
import com.plusarena.utils.ChatUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.List;

public class ArenaListener implements Listener {

    private final PlusArena plugin;

    public ArenaListener(PlusArena plugin) {
        this.plugin = plugin;
    }

    // ─── Death handling ───────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        ArenaManager arenaManager = plugin.getArenaManager();

        if (!arenaManager.isInArena(player)) return;

        // Prevent item drops - anti-duplication
        event.getDrops().clear();
        event.setDroppedExp(0);
        event.setKeepInventory(true); // We'll clear it ourselves on respawn

        arenaManager.handleDeath(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        ArenaManager arenaManager = plugin.getArenaManager();

        if (!arenaManager.isInArena(player)) return;

        // Clear inventory right after respawn (was kept from death)
        arenaManager.clearInventory(player);

        // Set respawn location
        Location respawn = plugin.getConfigManager().getRespawn();
        if (respawn != null) {
            event.setRespawnLocation(respawn);
        }

        // Remove from arena set (they've been sent to respawn)
        plugin.getArenaManager().getPlayersInArena(); // just access to trigger - actual remove below
        // We use a 1-tick scheduler to ensure the inventory is cleared after respawn fully processes
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                arenaManager.clearInventory(player);
                // Remove from arena if still tracked
                if (arenaManager.isInArena(player)) {
                    // Just clear them out - they were already handled
                    arenaManager.exitArena(player);
                }
            }
        }, 2L);
    }

    // ─── Quit handling ────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ArenaManager arenaManager = plugin.getArenaManager();

        if (!arenaManager.isInArena(player)) return;

        // Clear inventory and remove from arena on disconnect
        arenaManager.clearInventory(player);
        arenaManager.exitArena(player);
        arenaManager.removeCooldown(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        ArenaManager arenaManager = plugin.getArenaManager();

        if (!arenaManager.isInArena(player)) return;

        arenaManager.clearInventory(player);
        arenaManager.exitArena(player);
        arenaManager.removeCooldown(player);
    }

    // ─── Command blocking ─────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        ArenaManager arenaManager = plugin.getArenaManager();

        if (!arenaManager.isInArena(player)) return;
        if (player.hasPermission("plusarena.admin")) return; // Admins bypass

        String fullCommand = event.getMessage().substring(1).toLowerCase().trim(); // Remove leading /

        List<String> allowed = plugin.getConfigManager().getComandosPermitidos();
        for (String perm : allowed) {
            if (fullCommand.equalsIgnoreCase(perm) || fullCommand.startsWith(perm.toLowerCase() + " ")) {
                return; // Command is allowed
            }
        }

        event.setCancelled(true);
        player.sendMessage(ChatUtil.color("&cVocê não pode usar comandos dentro da arena!"));
    }

    // ─── Region boundary ──────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!plugin.getConfigManager().isImpedirSaida()) return;

        Player player = event.getPlayer();
        ArenaManager arenaManager = plugin.getArenaManager();

        if (!arenaManager.isInArena(player)) return;

        Location to = event.getTo();
        if (to == null) return;

        // Only check if the player actually moved to a new block (optimization)
        Location from = event.getFrom();
        if (from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        if (!arenaManager.isInsideArena(to)) {
            // Push player back to where they came from
            event.setCancelled(true);
            player.sendMessage(ChatUtil.color(plugin.getConfigManager().getMensagemSaidaBloqueada()));
        }
    }

    // ─── Teleport out of arena ─────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        ArenaManager arenaManager = plugin.getArenaManager();

        if (!arenaManager.isInArena(player)) return;

        Location to = event.getTo();
        if (to == null) return;

        // If player is being teleported outside of arena by some external cause (not our plugin)
        // Causes: PLUGIN (our own teleports), COMMAND (blocked anyway), ENDER_PEARL, CHORUS_FRUIT, etc.
        PlayerTeleportEvent.TeleportCause cause = event.getCause();

        // Allow our own teleports (from exitArena/respawn)
        if (cause == PlayerTeleportEvent.TeleportCause.PLUGIN) return;

        // If teleporting outside arena region, cancel and notify
        if (!arenaManager.isInsideArena(to)) {
            event.setCancelled(true);
            player.sendMessage(ChatUtil.color("&cVocê não pode se teleportar para fora da arena!"));
        }
    }
}
