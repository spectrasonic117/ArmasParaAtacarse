package com.spectrasonic.ArmasParaAtacarse.Game;

import com.spectrasonic.ArmasParaAtacarse.Main;
import com.spectrasonic.ArmasParaAtacarse.Utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;

public class GameManager {

    private final Main plugin;
    private boolean gameRunning = false;
    private int currentRound = 0;

    public GameManager(Main plugin) {
        this.plugin = plugin;
    }

    public void startGame(int round) {
        if (round < 1 || round > 3) {
            throw new IllegalArgumentException("Round must be between 1 and 3");
        }
        
        gameRunning = true;
        currentRound = round;
        FileConfiguration config = plugin.getConfig();
        int speedLevel = config.getInt("speed_level", 7);
        int amplifier = Math.max(0, speedLevel - 1);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), "minecraft:laser_gun", 1.0f, 1.0f);
            if (player.getGameMode() == GameMode.ADVENTURE) {
                giveWeapon(player);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, amplifier, true, false));
            }
        }
    }

    public void stopGame() {
        gameRunning = false;
        currentRound = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.ADVENTURE) {
                player.getInventory().clear();
                player.removePotionEffect(PotionEffectType.SPEED);
            }
        }
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public int getCurrentRound() {
        return currentRound;
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
