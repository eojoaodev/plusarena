package com.plusarena.commands;

import com.plusarena.PlusArena;
import com.plusarena.utils.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaSetSpawnCommand implements CommandExecutor {

    private final PlusArena plugin;

    public ArenaSetSpawnCommand(PlusArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatUtil.color("&cApenas jogadores podem usar este comando."));
            return true;
        }

        if (!player.hasPermission("plusarena.admin")) {
            player.sendMessage(ChatUtil.color("&cVocê não tem permissão para usar este comando."));
            return true;
        }

        plugin.getConfigManager().saveSpawn(player.getLocation());
        player.sendMessage(ChatUtil.color("&aSpawn da arena &7(entrada)&a definido com sucesso!"));
        player.sendMessage(ChatUtil.color("&7Local: &e" + formatLoc(player)));
        return true;
    }

    private String formatLoc(Player p) {
        return String.format("%.1f, %.1f, %.1f (%s)",
                p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(),
                p.getWorld().getName());
    }
}
