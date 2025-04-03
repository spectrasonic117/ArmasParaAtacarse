package com.spectrasonic.ArmasParaAtacarse.Listeners;

import com.spectrasonic.ArmasParaAtacarse.Main;
import com.spectrasonic.ArmasParaAtacarse.Utils.ItemBuilder;
import com.spectrasonic.ArmasParaAtacarse.Utils.MessageUtils;
import com.spectrasonic.ArmasParaAtacarse.Utils.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
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
    private final Random random = new Random();
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_TICKS = 10; // 10 ticks = 0.5 seconds

    public PlayerListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.PAPER && item.getItemMeta().hasCustomModelData()
                && item.getItemMeta().getCustomModelData() == 999) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                // Check cooldown
                long currentTime = System.currentTimeMillis();
                if (cooldowns.containsKey(player.getUniqueId())) {
                    long lastShot = cooldowns.get(player.getUniqueId());
                    long elapsedTicks = (currentTime - lastShot) / 50; 

                    if (elapsedTicks < COOLDOWN_TICKS) {
                        MessageUtils.sendActionBar(player, "<red><b>Arma Recargando");
                        return;
                    }
                }

                // Update cooldown
                cooldowns.put(player.getUniqueId(), currentTime);

                // Existing shooting logic
                Location start = player.getEyeLocation();
                Vector direction = start.getDirection().normalize();
                Location end = start.clone().add(direction.multiply(25));

                for (double i = 0; i < 25; i += 0.5) {
                    Location particleLocation = start.clone().add(direction.clone().multiply(i));
                    player.getWorld().spawnParticle(Particle.FLAME, particleLocation, 1);
                }

                Player target = getTargetPlayer(player, end);
                if (target != null) {
                    teleportToRespawn(target);
                    SoundUtils.playerSound(target, org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getLocation().getY() < plugin.getConfigManager().getRespawnHeight()) {
            teleportToRespawn(player);
            SoundUtils.playerSound(player, org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        }

        Block block = player.getLocation().getBlock().getRelative(0, -1, 0);
        if (block.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
            player.setVelocity(player.getVelocity().setY(plugin.getConfigManager().getJumpPlatform().getJump()));
            player.setVelocity(player.getVelocity().add(player.getLocation().getDirection()
                    .multiply(plugin.getConfigManager().getJumpPlatform().getDash())));
        }
    }

    private Player getTargetPlayer(Player shooter, Location end) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != shooter && player.getLocation().distance(end) < 1.5) {
                return player;
            }
        }
        return null;
    }

    private void teleportToRespawn(Player player) {
        List<Location> respawnPoints = plugin.getConfigManager().getRespawnPoints();
        Location respawnPoint = respawnPoints.get(random.nextInt(respawnPoints.size()));
        player.teleport(respawnPoint);
    }
}
