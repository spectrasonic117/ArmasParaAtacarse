package com.spectrasonic.ArmasParaAtacarse.Game;

import com.spectrasonic.ArmasParaAtacarse.Main;
import com.spectrasonic.ArmasParaAtacarse.Utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GameManager {

    private final Main plugin;
    private boolean gameRunning = false;

    public GameManager(Main plugin) {
        this.plugin = plugin;
    }

    public void startGame() {
        gameRunning = true;
        for (Player player : Bukkit.getOnlinePlayers()) {
            giveWeapon(player);
            player.playSound(player.getLocation(), "minecraft:laser_gun", 1.0f, 1.0f);
            // Apply SPEED 3 effect indefinitely
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3, true, false));
        }
    }

    public void stopGame() {
        gameRunning = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
            // Remove SPEED effect
            player.removePotionEffect(PotionEffectType.SPEED);
        }
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    private void giveWeapon(Player player) {
        ItemStack weapon = ItemBuilder.setMaterial("PAPER")
                .setName("<gold>Lazer Gun</gold>")
                .setLore("<gray>Usa esta arma</gray>",
                        "<gray>para atacar otros jugadores")
                .setCustomModelData(999)
                .build();
        player.getInventory().addItem(weapon);
    }
}
