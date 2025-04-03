package com.spectrasonic.ArmasParaAtacarse.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.spectrasonic.ArmasParaAtacarse.Game.GameManager;
import com.spectrasonic.ArmasParaAtacarse.Main;
import com.spectrasonic.ArmasParaAtacarse.Utils.MessageUtils;
import org.bukkit.command.CommandSender;

@CommandAlias("armaparaatacar|apa")
public class GameCommand extends BaseCommand {

    private final Main plugin;
    private final GameManager gameManager;

    public GameCommand(Main plugin) {
        this.plugin = plugin;
        this.gameManager = new GameManager(plugin);
    }

    @Subcommand("game start")
    public void onGameStart(CommandSender sender) {
        if (gameManager.isGameRunning()) {
            MessageUtils.sendMessage(sender, "<red>El juego ya está en curso.</red>");
            return;
        }
        gameManager.startGame();
        MessageUtils.sendMessage(sender, "<green>El juego ha comenzado.</green>");
    }

    @Subcommand("game stop")
    public void onGameStop(CommandSender sender) {
        if (!gameManager.isGameRunning()) {
            MessageUtils.sendMessage(sender, "<red>El juego no está en curso.</red>");
            return;
        }
        gameManager.stopGame();
        MessageUtils.sendMessage(sender, "<green>El juego ha terminado.</green>");
    }
}
