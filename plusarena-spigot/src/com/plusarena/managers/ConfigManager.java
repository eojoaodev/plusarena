package com.plusarena.managers;

import com.plusarena.PlusArena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {

    private final PlusArena plugin;

    public ConfigManager(PlusArena plugin) {
        this.plugin = plugin;
    }

    private FileConfiguration cfg() {
        return plugin.getConfig();
    }

    public void reload() {
        plugin.reloadConfig();
    }

    public int getSairCooldown() {
        return cfg().getInt("sair-cooldown", 10);
    }

    public boolean isImpedirSaida() {
        return cfg().getBoolean("impedir-saida", true);
    }

    public String getMensagemSaidaBloqueada() {
        return cfg().getString("mensagem-saida-bloqueada", "&cVocê não pode sair da arena!");
    }

    public List<String> getComandosPermitidos() {
        return cfg().getStringList("comandos-permitidos");
    }

    public Location getSpawn() {
        return loadLocation("spawn");
    }

    public Location getRespawn() {
        return loadLocation("respawn");
    }

    public Location getPos1() {
        return loadLocationSimple("pos1");
    }

    public Location getPos2() {
        return loadLocationSimple("pos2");
    }

    public void saveSpawn(Location loc) {
        saveLocation("spawn", loc);
    }

    public void saveRespawn(Location loc) {
        saveLocation("respawn", loc);
    }

    public void savePos1(Location loc) {
        saveLocationSimple("pos1", loc);
    }

    public void savePos2(Location loc) {
        saveLocationSimple("pos2", loc);
    }

    private Location loadLocation(String path) {
        String worldName = cfg().getString(path + ".world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        double x = cfg().getDouble(path + ".x", 0);
        double y = cfg().getDouble(path + ".y", 64);
        double z = cfg().getDouble(path + ".z", 0);
        float yaw = (float) cfg().getDouble(path + ".yaw", 0);
        float pitch = (float) cfg().getDouble(path + ".pitch", 0);

        return new Location(world, x, y, z, yaw, pitch);
    }

    private Location loadLocationSimple(String path) {
        String worldName = cfg().getString(path + ".world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        double x = cfg().getDouble(path + ".x", 0);
        double y = cfg().getDouble(path + ".y", 64);
        double z = cfg().getDouble(path + ".z", 0);

        return new Location(world, x, y, z);
    }

    private void saveLocation(String path, Location loc) {
        cfg().set(path + ".world", loc.getWorld().getName());
        cfg().set(path + ".x", loc.getX());
        cfg().set(path + ".y", loc.getY());
        cfg().set(path + ".z", loc.getZ());
        cfg().set(path + ".yaw", loc.getYaw());
        cfg().set(path + ".pitch", loc.getPitch());
        plugin.saveConfig();
    }

    private void saveLocationSimple(String path, Location loc) {
        cfg().set(path + ".world", loc.getWorld().getName());
        cfg().set(path + ".x", loc.getBlockX());
        cfg().set(path + ".y", loc.getBlockY());
        cfg().set(path + ".z", loc.getBlockZ());
        plugin.saveConfig();
    }
}
