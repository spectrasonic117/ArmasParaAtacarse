package com.spectrasonic.ArmasParaAtacarse;

import co.aikar.commands.PaperCommandManager;
import com.spectrasonic.ArmasParaAtacarse.Commands.GameCommand;
import com.spectrasonic.ArmasParaAtacarse.Commands.ReloadCommand;
import com.spectrasonic.ArmasParaAtacarse.Config.ConfigManager;
import com.spectrasonic.ArmasParaAtacarse.Listeners.PlayerListener;
import com.spectrasonic.ArmasParaAtacarse.Utils.MessageUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private ConfigManager configManager;
    private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        registerCommands();
        registerEvents();
        MessageUtils.sendStartupMessage(this);
    }

    @Override
    public void onDisable() {
        MessageUtils.sendShutdownMessage(this);
    }

    private void registerCommands() {
        this.commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new GameCommand(this));
        commandManager.registerCommand(new ReloadCommand(this));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
