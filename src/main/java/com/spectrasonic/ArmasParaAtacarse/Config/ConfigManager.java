package com.spectrasonic.ArmasParaAtacarse.Config;

import com.spectrasonic.ArmasParaAtacarse.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private final Main plugin;
    private FileConfiguration config;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public List<Location> getRespawnPoints() {
        List<Location> respawnPoints = new ArrayList<>();
        for (String key : config.getConfigurationSection("respawn_points").getKeys(false)) {
            double x = config.getDouble("respawn_points." + key + ".x");
            double y = config.getDouble("respawn_points." + key + ".y");
            double z = config.getDouble("respawn_points." + key + ".z");
            respawnPoints.add(new Location(Bukkit.getWorlds().get(0), x, y, z));
        }
        return respawnPoints;
    }

    public int getRespawnHeight() {
        return config.getInt("respawn_height");
    }

    public JumpPlatform getJumpPlatform() {
        return new JumpPlatform(
                config.getDouble("jump_platform.jump"),
                config.getDouble("jump_platform.dash"));
    }

    public int getPointsToAdd(int round) {
        return config.getInt("add_points.round_" + round);
    }

    public int getPointsToSubtract(int round) {
        return config.getInt("subtract_points.round_" + round);
    }

    public static class JumpPlatform {
        private final double jump;
        private final double dash;

        public JumpPlatform(double jump, double dash) {
            this.jump = jump;
            this.dash = dash;
        }

        public double getJump() {
            return jump;
        }

        public double getDash() {
            return dash;
        }
    }
}
