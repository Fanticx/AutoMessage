package ru.qWins;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.qWins.commands.AutoMessageCommand;
import ru.qWins.commands.AutoMessageTabCompleter;
import ru.qWins.managers.AutoMessageManager;
import ru.qWins.utils.ConfigUtil;

public class Main extends JavaPlugin {

    @Getter
    private static Main instance;
    @Getter
    private ConfigUtil configUtil;
    @Getter
    private AutoMessageManager autoMessageManager;

    @Override
    public void onEnable() {
        instance = this;
        configUtil = new ConfigUtil(this);
        autoMessageManager = new AutoMessageManager(this);

        getCommand("automessage").setExecutor(new AutoMessageCommand(this));
        getCommand("automessage").setTabCompleter(new AutoMessageTabCompleter());

        autoMessageManager.startAutoMessages();
    }

    @Override
    public void onDisable() {
        autoMessageManager.stopAutoMessages();
    }
}