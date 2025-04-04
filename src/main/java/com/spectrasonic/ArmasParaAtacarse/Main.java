package com.spectrasonic.ArmasParaAtacarse;

import co.aikar.commands.PaperCommandManager;
import com.spectrasonic.ArmasParaAtacarse.Commands.GameCommand;
import com.spectrasonic.ArmasParaAtacarse.Commands.ReloadCommand;
import com.spectrasonic.ArmasParaAtacarse.Config.ConfigManager;
import com.spectrasonic.ArmasParaAtacarse.Game.GameManager;
import com.spectrasonic.ArmasParaAtacarse.Listeners.PlayerListener;
import com.spectrasonic.ArmasParaAtacarse.Utils.MessageUtils;
import com.spectrasonic.ArmasParaAtacarse.Utils.PointsManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private ConfigManager configManager;
    private PaperCommandManager commandManager;
    private PointsManager pointsManager;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        pointsManager = new PointsManager(this);
        gameManager = new GameManager(this);

        registerCommands();
        registerEvents();
        MessageUtils.sendStartupMessage(this);
    }

    @Override
    public void onDisable() {
        // Make sure game is stopped when plugin is disabled
        if (gameManager.isGameRunning()) {
            gameManager.stopGame();
        }
        MessageUtils.sendShutdownMessage(this);
    }

    private void registerCommands() {
        this.commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new GameCommand(this, gameManager));
        commandManager.registerCommand(new ReloadCommand(this));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this, gameManager), this);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PointsManager getPointsManager() {
        return pointsManager;
    }
    
    public GameManager getGameManager() {
        return gameManager;
    }
}
