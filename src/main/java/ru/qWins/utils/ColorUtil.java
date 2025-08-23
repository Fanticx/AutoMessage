package ru.qWins.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("§x§[0-9A-Fa-f]§[0-9A-Fa-f]§[0-9A-Fa-f]§[0-9A-Fa-f]§[0-9A-Fa-f]§[0-9A-Fa-f]");
    private static final Pattern SIMPLE_HEX_PATTERN = Pattern.compile("#[A-Fa-f0-9]{6}");

    public static String color(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "";
        }

        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            String hex = matcher.group();
            String hexCode = "#" + hex.replaceAll("§[xX]|§[0-9A-Fa-f]", "");
            if (hexCode.length() != 7 || !hexCode.matches("#[0-9A-Fa-f]{6}")) {
                matcher.appendReplacement(buffer, hex);
                continue;
            }
            try {
                matcher.appendReplacement(buffer, ChatColor.of(hexCode).toString());
            } catch (IllegalArgumentException e) {
                matcher.appendReplacement(buffer, hex);
            }
        }
        matcher.appendTail(buffer);
        matcher = SIMPLE_HEX_PATTERN.matcher(buffer.toString());
        buffer = new StringBuilder();
        while (matcher.find()) {
            String hex = matcher.group();
            try {
                matcher.appendReplacement(buffer, ChatColor.of(hex).toString());
            } catch (IllegalArgumentException e) {
                matcher.appendReplacement(buffer, hex);
            }
        }
        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
}