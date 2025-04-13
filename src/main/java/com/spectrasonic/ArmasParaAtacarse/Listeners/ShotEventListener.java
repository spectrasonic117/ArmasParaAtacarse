package com.spectrasonic.ArmasParaAtacarse.Listeners;

import com.spectrasonic.ArmasParaAtacarse.Main;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.GameMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShotEventListener implements Listener {

    private final Main plugin;
    private final Random random = new Random();

    public ShotEventListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (!(projectile.getShooter() instanceof Player)) {
                return;
            }

            Player hitPlayer = (Player) event.getEntity();

            if (hitPlayer.getGameMode() == GameMode.ADVENTURE) {
                event.setCancelled(true);
                teleportToRespawn(hitPlayer);
            }
        }
    }

    private void teleportToRespawn(Player player) {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection respawnSection = config.getConfigurationSection("respawn_points");

        if (respawnSection == null || respawnSection.getKeys(false).isEmpty()) {
            plugin.getLogger().warning("No se han definido puntos de respawn en config.yml");
            return;
        }

        List<String> keys = new ArrayList<>(respawnSection.getKeys(false));
        String randomKey = keys.get(random.nextInt(keys.size()));

        double x = respawnSection.getDouble(randomKey + ".x");
        double z = respawnSection.getDouble(randomKey + ".z");
        double y = config.getDouble("respawn_height", respawnSection.getDouble(randomKey + ".y"));

        World world = player.getWorld();
        Location respawnLocation = new Location(world, x, y, z);

        player.teleport(respawnLocation);
    }
}
