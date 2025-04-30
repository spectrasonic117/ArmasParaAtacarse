package com.spectrasonic.ArmasParaAtacarse.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import com.spectrasonic.ArmasParaAtacarse.Game.GameManager;
import com.spectrasonic.ArmasParaAtacarse.Main;
import com.spectrasonic.ArmasParaAtacarse.Utils.MessageUtils;
import org.bukkit.command.CommandSender;

@CommandAlias("armaparaatacar|apa")
@CommandPermission("apa.game")
public class GameCommand extends BaseCommand {

    private final Main plugin;
    private final GameManager gameManager;

    public GameCommand(Main plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @Subcommand("game start")
    @CommandAlias("game start")
    @CommandCompletion("1|2|3")
    public void onGameStart(CommandSender sender, @Optional Integer round) {
        if (gameManager.isGameRunning()) {
            MessageUtils.sendMessage(sender, "<red>El juego ya está en curso.</red>");
            return;
        }

        if (round == null || (round < 1 || round > 3)) {
            MessageUtils.sendMessage(sender, "<red>Por favor, selecciona una ronda válida (1, 2 o 3).</red>");
            return;
        }

        gameManager.startGame(round);
        MessageUtils.sendMessage(sender, "<green>El juego ha comenzado en la Ronda " + round + ".</green>");
    }

    @Subcommand("game stop")
    @CommandAlias("game stop")
    public void onGameStop(CommandSender sender) {
        if (!gameManager.isGameRunning()) {
            MessageUtils.sendMessage(sender, "<red>El juego no está en curso.</red>");
            return;
        }
        gameManager.stopGame();
        MessageUtils.sendMessage(sender, "<green>El juego ha terminado.</green>");
    }

    @Subcommand("game")
    @CommandAlias("game")
    @Default
    public void onGameHelp(CommandSender sender) {
        MessageUtils.sendMessage(sender, "<yellow>Comandos del juego:</yellow>");
        MessageUtils.sendMessage(sender, "<green>/apa game start <1|2|3> - Inicia el juego en la ronda especificada");
        MessageUtils.sendMessage(sender, "<green>/apa game stop - Detiene el juego actual");
    }
}
