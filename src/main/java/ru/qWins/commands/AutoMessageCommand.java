package ru.qWins.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.qWins.Main;
import ru.qWins.utils.ColorUtil;

import java.util.List;
import java.util.logging.Logger;

public class AutoMessageCommand implements CommandExecutor {

    private final Main plugin;

    public AutoMessageCommand(Main plugin) {
        this.plugin = plugin;
        Logger logger = plugin.getLogger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("automessage.admin")) {
            sendConfigMessage(sender, "no-permission");
            return true;
        }

        if (args.length == 0) {
            sendConfigMessage(sender, "help");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "enable":
                plugin.getAutoMessageManager().setEnabled(true);
                sendConfigMessage(sender, "success-enable");
                break;
            case "disable":
                plugin.getAutoMessageManager().setEnabled(false);
                sendConfigMessage(sender, "success-disable");
                break;
            case "reload":
                plugin.getConfigUtil().reload();
                plugin.getAutoMessageManager().stopAutoMessages();
                plugin.getAutoMessageManager().setEnabled(plugin.getConfigUtil().getConfig().getBoolean("auto-messages.enabled", true));
                plugin.getAutoMessageManager().startAutoMessages();
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
                    plugin.getAutoMessageManager().toggleMessage(index, enabled);
                    sendConfigMessage(sender, index, enabled ? "включено" : "отключено");
                } catch (NumberFormatException e) {
                    sendConfigMessage(sender, "неверный номер списка");
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
            sender.sendMessage(ColorUtil.color(message));
        }
    }

    private void sendConfigMessage(CommandSender sender, int index, String state) {
        List<String> messages = plugin.getConfigUtil().getConfig().getStringList("command-messages." + "success-toggle");
        for (String message : messages) {
            sender.sendMessage(ColorUtil.color(message.replace("{index}", String.valueOf(index)).replace("{state}", state)));
        }
    }
}