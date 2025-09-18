package ru.qWins.commands;

import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.qWins.Main;
import ru.qWins.managers.AutoMessageManager;
import ru.qWins.utils.ColorUtil;

import java.util.List;

public class AutoMessageCommand implements CommandExecutor {

    private final Main plugin;

    public AutoMessageCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if (!sender.hasPermission("automessage.admin")) {
            sendConfigMessage(sender, "no-permission");
            return true;
        }

        if (args.length == 0) {
            sendConfigMessage(sender, "help");
            return true;
        }

        AutoMessageManager autoMessageManager = plugin.getAutoMessageManager();
        switch (args[0].toLowerCase()) {
            case "enable":
                autoMessageManager.setEnabled(true);
                sendConfigMessage(sender, "success-enable");
                break;
            case "disable":
                autoMessageManager.setEnabled(false);
                sendConfigMessage(sender, "success-disable");
                break;
            case "reload":
                plugin.getConfigUtil().reload();
                autoMessageManager.setEnabled(plugin.getConfigUtil().getConfig().getBoolean("auto-messages.enabled", true));
                sendConfigMessage(sender, "success-reload");
                break;
            case "toggle":
                if (args.length < 3) {
                    sendConfigMessage(sender, "help");
                    return true;
                }
                try {
                    int index = Integer.parseInt(args[1]);
                    boolean enabled = args[2].equalsIgnoreCase("on");
                    autoMessageManager.toggleMessage(index, enabled);
                    sendConfigMessage(sender, index, enabled ? "включено" : "отключено");
                } catch (NumberFormatException e) {
                    sendConfigMessage(sender, "invalid-index");
                }
                break;
            default:
                sendConfigMessage(sender, "help");
        }
        return true;
    }

    private void sendConfigMessage(CommandSender sender, String key) {
        List<String> messages = plugin.getConfigUtil().getConfig().getStringList("command-messages." + key);
        for (String message : messages) {
            sender.sendMessage(ColorUtil.use(message));
        }
    }

    private void sendConfigMessage(CommandSender sender, int index, String state) {
        List<String> messages = plugin.getConfigUtil().getConfig().getStringList("command-messages." + "success-toggle");
        for (String message : messages) {
            sender.sendMessage(ColorUtil.use(message.replace("{index}", String.valueOf(index)).replace("{state}", state)));
        }
    }
}