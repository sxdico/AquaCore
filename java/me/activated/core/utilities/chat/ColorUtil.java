package me.activated.core.utilities.chat;

import me.activated.core.utilities.general.StringUtils;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class ColorUtil {

    private static final Map<ChatColor, String> colorMap = new HashMap<>();

    static {
        colorMap.put(ChatColor.BLACK, "BLACK");
        colorMap.put(ChatColor.DARK_BLUE, "DARK_BLUE");
        colorMap.put(ChatColor.DARK_GREEN, "DARK_GREEN");
        colorMap.put(ChatColor.DARK_AQUA, "DARK_AQUA");
        colorMap.put(ChatColor.DARK_RED, "DARK_RED");
        colorMap.put(ChatColor.DARK_PURPLE, "DARK_PURPLE");
        colorMap.put(ChatColor.GOLD, "GOLD");
        colorMap.put(ChatColor.GRAY, "GRAY");
        colorMap.put(ChatColor.DARK_GRAY, "DARK_GRAY");
        colorMap.put(ChatColor.BLUE, "BLUE");
        colorMap.put(ChatColor.GREEN, "GREEN");
        colorMap.put(ChatColor.AQUA, "AQUA");
        colorMap.put(ChatColor.RED, "RED");
        colorMap.put(ChatColor.LIGHT_PURPLE, "LIGHT_PURPLE");
        colorMap.put(ChatColor.YELLOW, "YELLOW");
        colorMap.put(ChatColor.WHITE, "WHITE");
        colorMap.put(ChatColor.RESET, "RESET");
        colorMap.put(ChatColor.ITALIC, "ITALIC");
        colorMap.put(ChatColor.UNDERLINE, "UNDERLINE");
        colorMap.put(ChatColor.STRIKETHROUGH, "STRIKETHROUGH");
        colorMap.put(ChatColor.MAGIC, "MAGIC");
        colorMap.put(ChatColor.BOLD, "BOLD");
    }

    public static String convertChatColor(ChatColor color) {
        return colorMap.get(color);
    }

    public static String convertChatColor(ChatColor color, boolean fixed) {
        if (!fixed) return convertChatColor(color);

        String name = convertChatColor(color).toLowerCase();
        if (!name.contains("_")) {
            return StringUtils.convertFirstUpperCase(name);
        }
        StringBuilder builder = new StringBuilder();
        String[] attributes = name.split("_");
        for (String attribute : attributes) {
            builder.append(StringUtils.convertFirstUpperCase(attribute));
            builder.append(" ");
        }
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }
}