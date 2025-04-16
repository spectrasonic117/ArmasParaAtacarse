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
import org.bukkit.Bukkit;

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
        // Ejecutar el comando externo para respawn
        String command = "multiwarp tp 3_16 " + player.getName();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}
