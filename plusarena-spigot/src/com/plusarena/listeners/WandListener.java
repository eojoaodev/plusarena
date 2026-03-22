package com.plusarena.listeners;

import com.plusarena.PlusArena;
import com.plusarena.utils.ChatUtil;
import com.plusarena.utils.WandUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class WandListener implements Listener {

    private final PlusArena plugin;

    public WandListener(PlusArena plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onWandUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("plusarena.admin")) return;

        ItemStack item = event.getItem();
        if (!WandUtil.isWand(item)) return;

        event.setCancelled(true); // Don't break blocks with wand

        Action action = event.getAction();
        Location clickedLoc = event.getClickedBlock() != null
                ? event.getClickedBlock().getLocation()
                : player.getLocation();

        if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
            // POS1
            plugin.getConfigManager().savePos1(clickedLoc);
            player.sendMessage(ChatUtil.color("&aPOS1 definida: &e"
                    + clickedLoc.getBlockX() + ", "
                    + clickedLoc.getBlockY() + ", "
                    + clickedLoc.getBlockZ()));
        } else if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
            // POS2
            plugin.getConfigManager().savePos2(clickedLoc);
            player.sendMessage(ChatUtil.color("&aPOS2 definida: &e"
                    + clickedLoc.getBlockX() + ", "
                    + clickedLoc.getBlockY() + ", "
                    + clickedLoc.getBlockZ()));
        }
    }
}
