package com.spectrasonic.ArmasParaAtacarse.Listeners;

import com.spectrasonic.ArmasParaAtacarse.Config.ConfigManager;
import com.spectrasonic.ArmasParaAtacarse.Game.GameManager;
import com.spectrasonic.ArmasParaAtacarse.Main;
import com.spectrasonic.ArmasParaAtacarse.Utils.PointsManager;
import com.spectrasonic.ArmasParaAtacarse.Utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShotEventListener implements Listener {

    private final Main plugin;
    private final GameManager gameManager;
    private final PointsManager pointsManager;
    private final ConfigManager configManager;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TICKS = 20; // 1 segundo
    private static final int MAX_DISTANCE = 100;

    public ShotEventListener(Main plugin, GameManager gameManager, PointsManager pointsManager, ConfigManager configManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.pointsManager = pointsManager;
        this.configManager = configManager;
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

                    // Obtener puntos según la ronda actual
                    int round = gameManager.getCurrentRound();
                    int pointsToAdd = configManager.getPointsToAdd(round);
                    int pointsToSubtract = configManager.getPointsToSubtract(round);

                    pointsManager.addPoints(player, pointsToAdd);
                    MessageUtils.sendActionBar(player, String.format("<green><b>+" + pointsToAdd + " Puntos"));

                    pointsManager.subtractPoints(target, pointsToSubtract);
                    MessageUtils.sendActionBar(target, String.format("<red><b>" + pointsToSubtract + " Puntos"));

                    teleportToRespawn(target);
                    player.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.7f, 1.0f);
                }
            }
        }
    }

    private Player getTargetPlayerWithRaytracing(Player shooter, int maxDistance) {
        // Implementación existente de raytracing
        return null;
    }

    private void teleportToRespawn(Player player) {
        // Implementación existente de teletransportación
    }
}
