package ru.qWins;

import lombok.Getter;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import ru.qWins.commands.AutoMessageCommand;
import ru.qWins.commands.AutoMessageTabCompleter;
import ru.qWins.managers.AutoMessageManager;
import ru.qWins.utils.ConfigUtil;

public class Main extends JavaPlugin {

    @Getter
    private ConfigUtil configUtil;
    @Getter
    private AutoMessageManager autoMessageManager;

    @Override
    public void onEnable() {
        configUtil = new ConfigUtil(this);
        autoMessageManager = new AutoMessageManager(this);

        final PluginCommand automessageCommand = super.getCommand("automessage");
        automessageCommand.setExecutor(new AutoMessageCommand(this));
        automessageCommand.setTabCompleter(new AutoMessageTabCompleter());

        autoMessageManager.startAutoMessages();
    }

    @Override
    public void onDisable() {
        autoMessageManager.stopAutoMessages();
    }
}