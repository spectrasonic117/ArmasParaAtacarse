package com.spectrasonic.ArmasParaAtacarse.Listeners;

import com.spectrasonic.ArmasParaAtacarse.Main;
import com.spectrasonic.ArmasParaAtacarse.Utils.MessageUtils;
import com.spectrasonic.ArmasParaAtacarse.Utils.PointsManager;
import com.spectrasonic.ArmasParaAtacarse.Utils.SoundUtils;
import com.spectrasonic.ArmasParaAtacarse.Utils.TeleportEffectUtils;
import com.spectrasonic.ArmasParaAtacarse.Game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.Random;

public class PlayerListener implements Listener {

    private final Main plugin;
    private final PointsManager pointsManager;
    private final GameManager gameManager;
    private final Random random = new Random();
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_TICKS = 10; // 10 ticks = 0.5 segundos
    private static final double MAX_DISTANCE = 50.0; // Distancia máxima para el raytracing

    public PlayerListener(Main plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.pointsManager = new PointsManager(plugin);
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Verificar si el juego está en ejecución antes de procesar interacciones con armas
        if (!gameManager.isGameRunning()) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.PAPER && item.getItemMeta().hasCustomModelData()
                && item.getItemMeta().getCustomModelData() == 999) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                // Verificar cooldown
                long currentTime = System.currentTimeMillis();
                if (cooldowns.containsKey(player.getUniqueId())) {
                    long lastShot = cooldowns.get(player.getUniqueId());
                    long elapsedTicks = (currentTime - lastShot) / 50;

                    if (elapsedTicks < COOLDOWN_TICKS) {
                        MessageUtils.sendActionBar(player, "<red><b>Arma Recargando");
                        return;
                    }
                }

                // Actualizar cooldown
                cooldowns.put(player.getUniqueId(), currentTime);

                // Reproducir sonido solo después de que pase la verificación de cooldown
                player.playSound(player.getLocation(), "minecraft:laser_shoot", 1.0f, 1.0f);

                // Obtener el jugador objetivo usando raytracing
                Player target = getTargetPlayerWithRaytracing(player, MAX_DISTANCE);
                
                // Lógica de disparo - línea recta de partículas
                Location start = player.getEyeLocation();
                Vector direction = start.getDirection().normalize();
                
                // Determinar la distancia final para las partículas
                double particleDistance = MAX_DISTANCE;
                if (target != null) {
                    particleDistance = start.distance(target.getEyeLocation());
                }
                
                // Dibujar una línea perfectamente recta de partículas
                for (double i = 0; i <= particleDistance; i += 0.5) {
                    Location particleLocation = start.clone().add(direction.clone().multiply(i));
                    player.getWorld().spawnParticle(
                            Particle.END_ROD,
                            particleLocation,
                            1, // count
                            0, // offset X (0 = no spread)
                            0, // offset Y (0 = no spread)
                            0, // offset Z (0 = no spread)
                            0 // extra/speed (0 = no randomness)
                    );
                }

                if (target != null) {
                    // Efecto de impacto en el objetivo
                    target.getWorld().spawnParticle(
                            Particle.PORTAL,
                            target.getLocation().add(0, 1, 0),
                            10, 0.5, 0.5, 0.5, 0.1
                    );

                    pointsManager.addPoints(player, 1);
                    MessageUtils.sendActionBar(player, "<green><b>+1 Punto");

                    pointsManager.subtractPoints(target, 3);
                    MessageUtils.sendActionBar(target, "<red><b>-3 Puntos");

                    teleportToRespawn(target);
                    SoundUtils.playerSound(target, org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 0.7f, 1.0f);
                }
            }
        }
    }

    /**
     * Obtiene el jugador objetivo usando raytracing para una detección precisa
     * @param shooter El jugador que dispara
     * @param maxDistance La distancia máxima para buscar
     * @return El jugador objetivo o null si no hay ninguno
     */
    private Player getTargetPlayerWithRaytracing(Player shooter, double maxDistance) {
        Location eyeLocation = shooter.getEyeLocation();
        Vector direction = eyeLocation.getDirection().normalize();
        
        // Verificar si hay jugadores en la línea de visión
        for (double d = 0; d <= maxDistance; d += 0.5) {
            Location checkLocation = eyeLocation.clone().add(direction.clone().multiply(d));
            
            // Verificar si hay un bloque sólido que bloquee la visión
            if (checkLocation.getBlock().getType().isSolid()) {
                return null;
            }
            
            // Buscar jugadores cercanos a este punto
            for (Entity entity : checkLocation.getWorld().getNearbyEntities(checkLocation, 1, 1, 1)) {
                if (entity instanceof Player && entity != shooter) {
                    Player target = (Player) entity;
                    
                    // Verificar si el jugador está en modo aventura (jugando)
                    if (target.getGameMode() == GameMode.ADVENTURE) {
                        // Verificar si la línea de visión pasa cerca de la cabeza del jugador
                        if (isLookingAt(shooter, target)) {
                            return target;
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Verifica si un jugador está mirando a otro
     * @param shooter El jugador que dispara
     * @param target El jugador objetivo potencial
     * @return true si el shooter está mirando al target
     */
    private boolean isLookingAt(Player shooter, Player target) {
        Location eyeLocation = shooter.getEyeLocation();
        Vector toTarget = target.getEyeLocation().toVector().subtract(eyeLocation.toVector());
        double dot = toTarget.normalize().dot(eyeLocation.getDirection());
        
        // El valor de 0.98 representa aproximadamente un ángulo de 11 grados
        // Puedes ajustar este valor para hacer la detección más o menos precisa
        return dot > 0.98;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Verificar si el juego está en ejecución antes de procesar mecánicas de movimiento
        if (!gameManager.isGameRunning()) {
            return;
        }

        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.ADVENTURE) {
            return; // No hacer nada si no está en modo AVENTURE
        }

        if (player.getLocation().getY() <= plugin.getConfigManager().getRespawnHeight()) {
            pointsManager.subtractPoints(player, 3);
            MessageUtils.sendActionBar(player, "<red><b>-3 Puntos");
            // Ejecutar el comando externo para respawn
            String command = "multiwarp tp 3_16 " + player.getName();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            SoundUtils.playerSound(player, org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        }
        
        Block block = player.getLocation().getBlock().getRelative(0, 0, 0);
        if (block.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
            // Obtener vector de dirección y normalizarlo
            Vector direction = player.getLocation().getDirection().normalize();

            // Aplicar fuerzas de salto (vertical) y dash (horizontal)
            direction.setY(direction.getY() + plugin.getConfigManager().getJumpPlatform().getJump());
            direction.multiply(plugin.getConfigManager().getJumpPlatform().getDash());

            // Establecer la velocidad combinada
            player.setVelocity(direction);
        }
    }

    private void teleportToRespawn(Player player) {
        // Ejecutar el comando de respawn externo
        String command = "multiwarp tp 3_16 " + player.getName();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}
