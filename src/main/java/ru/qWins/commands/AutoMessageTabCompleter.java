package ru.qWins.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AutoMessageTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!sender.hasPermission("automessage.admin")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return Stream.of("enable", "disable", "reload", "toggle")
                    .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("toggle")) {
            return List.of("0", "1");
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("toggle")) {
            return List.of("on", "off");
        }

        return Collections.emptyList();
    }
}