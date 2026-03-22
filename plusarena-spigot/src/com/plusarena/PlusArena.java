package com.plusarena;

import com.plusarena.commands.ArenaCommand;
import com.plusarena.commands.ArenaSetRespawnCommand;
import com.plusarena.commands.ArenaSetSpawnCommand;
import com.plusarena.listeners.ArenaListener;
import com.plusarena.listeners.WandListener;
import com.plusarena.managers.ArenaManager;
import com.plusarena.managers.ConfigManager;
import com.plusarena.managers.KitManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PlusArena extends JavaPlugin {

    private static PlusArena instance;
    private ConfigManager configManager;
    private ArenaManager arenaManager;
    private KitManager kitManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.arenaManager = new ArenaManager(this);
        this.kitManager = new KitManager(this);

        // Register commands
        getCommand("arena").setExecutor(new ArenaCommand(this));
        getCommand("arenasetspawn").setExecutor(new ArenaSetSpawnCommand(this));
        getCommand("arenasetrespawn").setExecutor(new ArenaSetRespawnCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new ArenaListener(this), this);
        getServer().getPluginManager().registerEvents(new WandListener(this), this);

        getLogger().info("PlusArena v1.0.0 ativado com sucesso!");
    }

    @Override
    public void onDisable() {
        // Safely remove all players from arena on shutdown
        if (arenaManager != null) {
            arenaManager.removeAllPlayersOnShutdown();
        }
        getLogger().info("PlusArena desativado.");
    }

    public static PlusArena getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public KitManager getKitManager() {
        return kitManager;
    }
}
