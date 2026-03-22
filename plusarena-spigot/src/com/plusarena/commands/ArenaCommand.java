package com.plusarena.commands;

import com.plusarena.PlusArena;
import com.plusarena.managers.ArenaManager;
import com.plusarena.utils.ChatUtil;
import com.plusarena.utils.WandUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaCommand implements CommandExecutor {

    private final PlusArena plugin;

    public ArenaCommand(PlusArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatUtil.color("&cApenas jogadores podem usar este comando."));
            return true;
        }

        ArenaManager arenaManager = plugin.getArenaManager();

        // /arena sair
        if (args.length >= 1 && args[0].equalsIgnoreCase("sair")) {
            handleSair(player, arenaManager);
            return true;
        }

        // /arena wand
        if (args.length >= 1 && args[0].equalsIgnoreCase("wand")) {
            handleWand(player);
            return true;
        }

        // /arena (entrar)
        if (args.length == 0) {
            handleEntrar(player, arenaManager);
            return true;
        }

        player.sendMessage(ChatUtil.color("&cUso: &e/arena &7| &e/arena sair &7| &e/arena wand"));
        return true;
    }

    private void handleEntrar(Player player, ArenaManager arenaManager) {
        // Check if pos1/pos2 and spawn are set
        if (plugin.getConfigManager().getSpawn() == null) {
            player.sendMessage(ChatUtil.color("&cERRO! O spawn da arena não foi definido. Use &e/arenasetspawn&c."));
            return;
        }

        if (plugin.getConfigManager().getPos1() == null || plugin.getConfigManager().getPos2() == null) {
            player.sendMessage(ChatUtil.color("&cERRO! A região da arena não foi definida. Use &e/arena wand&c para definir POS1 e POS2."));
            return;
        }

        // Check if already in arena
        if (arenaManager.isInArena(player)) {
            player.sendMessage(ChatUtil.color("&cVocê já está na arena!"));
            return;
        }

        // Check inventory
        if (!arenaManager.isInventoryEmpty(player)) {
            player.sendMessage(ChatUtil.color("&cERRO! Inventário cheio, esvazie seu inventário para poder entrar na arena!"));
            return;
        }

        // Enter arena
        arenaManager.enterArena(player);
        player.sendMessage(ChatUtil.color("&aVocê entrou na arena! &7Boa sorte!"));
    }

    private void handleSair(Player player, ArenaManager arenaManager) {
        if (!arenaManager.isInArena(player)) {
            player.sendMessage(ChatUtil.color("&cVocê não está na arena!"));
            return;
        }

        // Check cooldown
        if (arenaManager.hasSairCooldown(player)) {
            long remaining = arenaManager.getSairCooldownRemaining(player);
            player.sendMessage(ChatUtil.color("&cAguarde &e" + remaining + " segundo(s) &cpara sair da arena."));
            return;
        }

        // Apply cooldown before exiting to prevent spam
        arenaManager.setSairCooldown(player);
        arenaManager.exitArena(player);
        player.sendMessage(ChatUtil.color("&aVocê saiu da arena!"));
    }

    private void handleWand(Player player) {
        if (!player.hasPermission("plusarena.admin")) {
            player.sendMessage(ChatUtil.color("&cVocê não tem permissão para usar este comando."));
            return;
        }
        player.getInventory().addItem(WandUtil.createWand());
        player.sendMessage(ChatUtil.color("&aVocê recebeu a Wand da arena!"));
        player.sendMessage(ChatUtil.color("&7• Clique Esquerdo: &aDefinir POS1"));
        player.sendMessage(ChatUtil.color("&7• Clique Direito: &cDefinir POS2"));
    }
}
