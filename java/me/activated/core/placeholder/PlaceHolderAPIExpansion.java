package me.activated.core.placeholder;

import lombok.RequiredArgsConstructor;
import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.chat.Color;
import me.activated.core.api.player.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class PlaceHolderAPIExpansion extends PlaceholderExpansion {
    private final AquaCore plugin;

    @Override
    public String getIdentifier() {
        return "Aqua";
    }

    @Override
    public String getAuthor() {
        return "Activated_";
    }

    @Override
    public String getPlugin() {
        return null;
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.equalsIgnoreCase("player_rank")) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            if (playerData == null) {
                return "Default";
            }
            return Color.translate(playerData.getHighestRank().getDisplayName());
        }
        if (identifier.equalsIgnoreCase("player_color")) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            if (playerData == null) {
                return "Default";
            }
            if (playerData.getNameColor() == null) {
                return ChatColor.WHITE.toString();
            }
            return playerData.getNameColor();
        }
        if (identifier.equalsIgnoreCase("player_prefix")) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            if (playerData == null) {
                return "Default";
            }
            return Color.translate(playerData.getHighestRank().getPrefix());
        }
        if (identifier.equalsIgnoreCase("player_suffix")) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            if (playerData == null) {
                return "Default";
            }
            return Color.translate(playerData.getHighestRank().getSuffix());
        }
        if (identifier.equalsIgnoreCase("player_tag")) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            if (playerData == null) {
                return "";
            }
            if (playerData.getTag() == null) return  "";
            return Color.translate(playerData.getTag().getPrefix());
        }
        if (identifier.equalsIgnoreCase("player_coins")) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            if (playerData == null) {
                return "";
            }
            return String.valueOf(playerData.getCoins());
        }
        return null;
    }
}
