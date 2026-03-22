package com.plusarena.managers;

import com.plusarena.PlusArena;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class ArenaManager {

    private final PlusArena plugin;

    // Set of players currently inside the arena
    private final Set<UUID> playersInArena = new HashSet<>();

    // Cooldown map: UUID -> timestamp (ms) when they can use /arena sair again
    private final Map<UUID, Long> sairCooldowns = new HashMap<>();

    public ArenaManager(PlusArena plugin) {
        this.plugin = plugin;
    }

    // ─── Player tracking ──────────────────────────────────────────────────────

    public boolean isInArena(Player player) {
        return playersInArena.contains(player.getUniqueId());
    }

    /**
     * Adds a player to the arena: cleans inventory, gives kit, teleports.
     */
    public void enterArena(Player player) {
        playersInArena.add(player.getUniqueId());

        // Clear inventory FIRST to prevent any duplication
        clearInventory(player);

        // Give kit
        plugin.getKitManager().giveKit(player);

        // Teleport to spawn
        Location spawn = plugin.getConfigManager().getSpawn();
        if (spawn != null) {
            player.teleport(spawn);
        }
    }

    /**
     * Removes a player from the arena: cleans inventory, teleports to respawn.
     * Safe to call multiple times (idempotent).
     */
    public void exitArena(Player player) {
        if (!playersInArena.remove(player.getUniqueId())) {
            // Player wasn't in arena - do nothing to prevent duplication issues
            return;
        }

        // Clear inventory immediately on exit
        clearInventory(player);

        // Teleport to respawn
        Location respawn = plugin.getConfigManager().getRespawn();
        if (respawn != null) {
            player.teleport(respawn);
        }
    }

    /**
     * Called when a player dies inside the arena.
     * Inventory is already cleared by Minecraft on death, but we clear again for safety.
     */
    public void handleDeath(Player player) {
        if (!isInArena(player)) return;
        // Keep player in arena set - they'll respawn back to arena respawn point
        // Inventory clear happens in the listener after respawn
    }

    /**
     * Called after a player respawns (was in arena when they died).
     */
    public void handleRespawn(Player player) {
        // Clear inventory (death drops handled by listener cancelling drops)
        clearInventory(player);

        // Teleport to respawn point (done via scheduler in listener)
        Location respawn = plugin.getConfigManager().getRespawn();
        if (respawn != null) {
            // Remove from arena set - they're being sent to respawn
            playersInArena.remove(player.getUniqueId());
            player.teleport(respawn);
        }
    }

    // ─── Cooldown ─────────────────────────────────────────────────────────────

    public boolean hasSairCooldown(Player player) {
        Long ts = sairCooldowns.get(player.getUniqueId());
        if (ts == null) return false;
        return System.currentTimeMillis() < ts;
    }

    public long getSairCooldownRemaining(Player player) {
        Long ts = sairCooldowns.get(player.getUniqueId());
        if (ts == null) return 0;
        long remaining = ts - System.currentTimeMillis();
        return remaining > 0 ? (long) Math.ceil(remaining / 1000.0) : 0;
    }

    public void setSairCooldown(Player player) {
        int seconds = plugin.getConfigManager().getSairCooldown();
        sairCooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (seconds * 1000L));
    }

    public void removeCooldown(Player player) {
        sairCooldowns.remove(player.getUniqueId());
    }

    // ─── Region ───────────────────────────────────────────────────────────────

    public boolean isInsideArena(Location loc) {
        Location pos1 = plugin.getConfigManager().getPos1();
        Location pos2 = plugin.getConfigManager().getPos2();

        if (pos1 == null || pos2 == null) return false;
        if (!loc.getWorld().equals(pos1.getWorld())) return false;

        double minX = Math.min(pos1.getX(), pos2.getX());
        double maxX = Math.max(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double maxY = Math.max(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());

        return loc.getX() >= minX && loc.getX() <= maxX
                && loc.getY() >= minY && loc.getY() <= maxY
                && loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }

    // ─── Utility ──────────────────────────────────────────────────────────────

    public void clearInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.getInventory().setItemInOffHand(null);
        player.updateInventory();
    }

    public boolean isInventoryEmpty(Player player) {
        // Check all slots including armor and offhand
        if (player.getInventory().getHelmet() != null) return false;
        if (player.getInventory().getChestplate() != null) return false;
        if (player.getInventory().getLeggings() != null) return false;
        if (player.getInventory().getBoots() != null) return false;
        if (player.getInventory().getItemInOffHand() != null
                && player.getInventory().getItemInOffHand().getType().isAir()) {
            // offhand is empty
        } else if (player.getInventory().getItemInOffHand() != null
                && !player.getInventory().getItemInOffHand().getType().isAir()) {
            return false;
        }

        for (var item : player.getInventory().getContents()) {
            if (item != null && !item.getType().isAir()) return false;
        }
        return true;
    }

    public Set<UUID> getPlayersInArena() {
        return Collections.unmodifiableSet(playersInArena);
    }

    public void removeAllPlayersOnShutdown() {
        List<UUID> toRemove = new ArrayList<>(playersInArena);
        for (UUID uuid : toRemove) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                clearInventory(player);
                Location respawn = plugin.getConfigManager().getRespawn();
                if (respawn != null) {
                    player.teleport(respawn);
                }
                player.sendMessage(color("&cO servidor está reiniciando. Você foi removido da arena."));
            }
        }
        playersInArena.clear();
    }

    private String color(String s) {
        return s.replace("&", "§");
    }
}
