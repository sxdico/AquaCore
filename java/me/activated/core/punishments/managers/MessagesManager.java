package me.activated.core.punishments.managers;

import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.Manager;
import me.activated.core.utilities.chat.ChatComponentBuilder;
import me.activated.core.utilities.chat.Clickable;
import me.activated.core.utilities.chat.Replacement;
import me.activated.core.utilities.general.StringUtils;
import net.md_5.bungee.api.chat.HoverEvent;

import java.util.HashMap;
import java.util.Map;

public class MessagesManager extends Manager {

    private final Map<String, Clickable> messages = new HashMap<>();
    private final Map<String, String> kickMessages = new HashMap<>();

    public MessagesManager(AquaCore plugin) {
        super(plugin);
    }

    public void setup() {
        this.messages.clear();
        plugin.getPunishmentPlugin().getConfigFile().getConfigurationSection("BROADCASTS").getKeys(false).forEach(key -> {
            String punishMessage = plugin.getPunishmentPlugin().getConfigFile().getString("BROADCASTS." + key + ".MESSAGE");
            String hoverMessage = StringUtils.getStringFromList(plugin.getPunishmentPlugin().getConfigFile().getStringList("BROADCASTS." + key + ".HOVER.MESSAGE")).replace(", ", "\n");
            boolean hover = plugin.getPunishmentPlugin().getConfigFile().getBoolean("BROADCASTS." + key + ".HOVER.ENABLED");

            ChatComponentBuilder chatComponentBuilder = new ChatComponentBuilder("");
            chatComponentBuilder.append(punishMessage);
            if (hover) {
                chatComponentBuilder.setCurrentHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder(hoverMessage).create()));
            }
            Clickable clickable = new Clickable(punishMessage, hover ? hoverMessage : null,  null);
            this.messages.put(key, clickable);
        });
        this.kickMessages.clear();
        plugin.getPunishmentPlugin().getConfigFile().getConfigurationSection("KICK-MESSAGES").getKeys(false).forEach(key -> {
            this.kickMessages.put(key, StringUtils.getStringFromList(plugin.getPunishmentPlugin().getConfigFile().getStringList("KICK-MESSAGES." + key)));
        });
    }

    public Clickable getMessage(String key) {
        return this.messages.get(key);
    }

    public Clickable getMessageWithReplacements(String key, Replacement replacement) {
        Clickable current = this.getMessage(key);

        String toAppend = plugin.getPunishmentPlugin().getConfigFile().getString("BROADCASTS." + key + ".MESSAGE");
        replacement.setMessage(toAppend);

        Clickable clickable = new Clickable(replacement.toString(), current.getHoverText(), null);


        if (current.getHoverText() != null) {
            String currentMessage = clickable.getText();
            String hoverMessage = StringUtils.getStringFromList(plugin.getPunishmentPlugin().getConfigFile().getStringList("BROADCASTS." + key + ".HOVER.MESSAGE")).replace(", ", "\n");

            replacement.setMessage(hoverMessage);
            clickable = new Clickable(currentMessage, replacement.toString(), null);

        }
        return clickable;
    }

    public String getKickMessage(String key) {
        return this.kickMessages.get(key);
    }
}
