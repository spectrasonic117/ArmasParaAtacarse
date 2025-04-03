package com.spectrasonic.ArmasParaAtacarse.Game;

import com.spectrasonic.ArmasParaAtacarse.Main;
import com.spectrasonic.ArmasParaAtacarse.Utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        }
    }

    public void stopGame() {
        gameRunning = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
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
