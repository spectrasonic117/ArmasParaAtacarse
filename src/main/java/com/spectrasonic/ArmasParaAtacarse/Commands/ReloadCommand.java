package com.spectrasonic.ArmasParaAtacarse.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.spectrasonic.ArmasParaAtacarse.Main;
import com.spectrasonic.ArmasParaAtacarse.Utils.MessageUtils;
import org.bukkit.command.CommandSender;

@CommandAlias("armaparaatacar|apa")
public class ReloadCommand extends BaseCommand {

    private final Main plugin;

    public ReloadCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Subcommand("reload")
    public void onReload(CommandSender sender) {
        plugin.getConfigManager().reloadConfig();
        MessageUtils.sendMessage(sender, "<green>Configuración recargada.</green>");
    }
}
