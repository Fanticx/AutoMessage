package ru.qWins.managers;

import lombok.Getter;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.qWins.Main;
import ru.qWins.managers.data.MessageConfig;
import ru.qWins.utils.ColorUtil;

import java.util.ArrayList;
import java.util.List;

public class AutoMessageManager {

    private final Main plugin;
    @Getter
    private boolean enabled;
    private final List<BukkitRunnable> messageTasks = new ArrayList<>();

    public AutoMessageManager(Main plugin) {
        this.plugin = plugin;
        this.enabled = plugin.getConfigUtil().getConfig().getBoolean("auto-messages.enabled", true);
    }

    public void startAutoMessages() {
        stopAutoMessages();
        if (!enabled) return;

        ConfigurationSection messagesSection = plugin.getConfigUtil().getConfig().getConfigurationSection("auto-messages.messages");
        if (messagesSection == null) return;

        int index = 0;
        for (String key : messagesSection.getKeys(false)) {
            ConfigurationSection messageSection = messagesSection.getConfigurationSection(key);
            if (messageSection == null) continue;

            MessageConfig config = new MessageConfig();
            config.setText(messageSection.getStringList("text"));
            config.setHover(messageSection.getStringList("hover"));
            config.setEnabled(messageSection.getBoolean("enabled", true));
            config.setInterval(messageSection.getLong("interval", 60L) * 20L);
            config.setSound(messageSection.getString("sound", ""));
            config.setClickUrl(messageSection.getString("click-url", ""));
            config.setClickEnabled(messageSection.getBoolean("click-enabled", false));

            if (!config.isEnabled()) continue;

            final int messageIndex = index;
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    ComponentBuilder componentBuilder = new ComponentBuilder();
                    for (String line : config.getText()) {
                        TextComponent lineComponent = new TextComponent(TextComponent.fromLegacyText(ColorUtil.use(line)));
                        if (config.isClickEnabled() && !config.getClickUrl().isEmpty()) {
                            lineComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, config.getClickUrl()));
                        }
                        componentBuilder.append(lineComponent);
                        componentBuilder.append("\n");
                    }
                    TextComponent message = new TextComponent(componentBuilder.create());

                    if (!config.getHover().isEmpty()) {
                        ComponentBuilder hoverBuilder = new ComponentBuilder();
                        for (String hoverLine : config.getHover()) {
                            hoverBuilder.append(TextComponent.fromLegacyText(ColorUtil.use(hoverLine)));
                            hoverBuilder.append("\n");
                        }
                        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.create()));
                    }

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.spigot().sendMessage(message);
                    }

                    if (!config.getSound().isEmpty()) {
                        try {
                            String sound = config.getSound();
                            if (sound.contains(":")) {
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.playSound(player.getLocation(), sound, 1000.0f, 1.0f);
                                }
                            } else {
                                Sound bukkitSound = Sound.valueOf(sound.replace("minecraft:", "").toUpperCase());
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.playSound(player.getLocation(), bukkitSound, 1000.0f, 1.0f);
                                }
                            }
                        } catch (IllegalArgumentException ignored) {
                            plugin.getLogger().warning("нЕ найден звук: " + config.getSound());
                        }
                    }
                }
            };
            task.runTaskTimer(plugin, config.getInterval(), config.getInterval());
            messageTasks.add(task);
            index++;
        }
    }

    public void stopAutoMessages() {
        for (BukkitRunnable task : messageTasks) {
            if (!task.isCancelled()) {
                task.cancel();
            }
        }
        messageTasks.clear();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        plugin.getConfigUtil().getConfig().set("auto-messages.enabled", enabled);
        plugin.getConfigUtil().save();

        if (enabled) {
            startAutoMessages();
        } else {
            stopAutoMessages();
        }
    }

    public void toggleMessage(int index, boolean enabled) {
        ConfigurationSection messagesSection = plugin.getConfigUtil().getConfig().getConfigurationSection("auto-messages.messages");
        if (messagesSection == null) return;

        String key = String.valueOf(index);
        if (!messagesSection.contains(key)) return;

        messagesSection.set(key + ".enabled", enabled);
        plugin.getConfigUtil().save();
        stopAutoMessages();
        startAutoMessages();
    }

    public List<MessageConfig> getMessages() {
        List<MessageConfig> messages = new ArrayList<>();
        ConfigurationSection messagesSection = plugin.getConfigUtil().getConfig().getConfigurationSection("auto-messages.messages");
        if (messagesSection == null) return messages;

        for (String key : messagesSection.getKeys(false)) {
            ConfigurationSection messageSection = messagesSection.getConfigurationSection(key);
            if (messageSection == null) continue;

            MessageConfig config = new MessageConfig();
            config.setText(messageSection.getStringList("text"));
            config.setHover(messageSection.getStringList("hover"));
            config.setEnabled(messageSection.getBoolean("enabled", true));
            config.setInterval(messageSection.getLong("interval", 60L));
            config.setSound(messageSection.getString("sound", ""));
            config.setClickUrl(messageSection.getString("click-url", ""));
            config.setClickEnabled(messageSection.getBoolean("click-enabled", false));
            messages.add(config);
        }
        return messages;
    }
}