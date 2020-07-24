package me.activated.core.managers;

import me.activated.core.plugin.AquaCore;
import me.activated.core.enums.Language;
import me.activated.core.utilities.Manager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterManagement extends Manager {
    private final Pattern URL_REGEX = Pattern.compile("^(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?$");
    private final Pattern IP_REGEX = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])([.,])){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

    private List<String> whitelistedLinks = new ArrayList<>();
    private List<String> filteredWords = new ArrayList<>();

    public FilterManagement(AquaCore plugin) {
        super(plugin);

        this.filteredWords = plugin.getCoreConfig().getStringList("filter.filtered-worlds");
        this.whitelistedLinks = plugin.getCoreConfig().getStringList("filter.whitelisted-links");
    }

    public boolean checkFilter(Player player, String message, boolean sendMessage) {
        if (player.hasPermission("filter.bypass")) return false;

        if (this.isFiltered(message)) {
            if (sendMessage) {
                player.sendMessage(Language.FILTER_CANT_SEND.toString());
            }
            plugin.getServerManagement().getGlobalPlayers().stream().filter(globalPlayer -> globalPlayer.hasPermission("filter.alerts")).forEach(globalPlayer -> {
                globalPlayer.sendMessage(Language.FILTER_STAFF_ALERT.toString()
                        .replace("<player>", player.getDisplayName())
                        .replace("<server>", plugin.getEssentialsManagement().getServerName())
                        .replace("<message>", message));
            });
            return true;
        }
        return false;
    }

    public boolean isFiltered(String message) {
        if (!plugin.getCoreConfig().getBoolean("filter.enabled")) return false;
        this.filteredWords = plugin.getCoreConfig().getStringList("filter.filtered-worlds");
        this.whitelistedLinks = plugin.getCoreConfig().getStringList("filter.whitelisted-links");

        String msg = message.toLowerCase()
                .replace("3", "e")
                .replace("1", "i")
                .replace("!", "i")
                .replace("@", "a")
                .replace("7", "t")
                .replace("0", "o")
                .replace("5", "s")
                .replace("8", "b")
                .replaceAll("\\p{Punct}|\\d", "").trim();

        String[] words = msg.trim().split(" ");

        for (String word : words) {
            for (String filteredWord : filteredWords) {
                if (word.equalsIgnoreCase(filteredWord)) {
                    return true;
                }
            }
        }

        for (String word : message
                .replace("(dot)", ".")
                .replace("(.)", ".")
                .replace("/./", ".")
                .replace("[dot]", ".").trim().split(" ")) {
            boolean continueIt = false;

            for (String phrase : whitelistedLinks) {
                if (word.toLowerCase().contains(phrase)) {
                    continueIt = true;
                    break;
                }
            }

            if (continueIt) {
                continue;
            }

            Matcher matcher = IP_REGEX.matcher(word);

            if (matcher.matches()) {
                return true;
            }

            matcher = URL_REGEX.matcher(word);

            if (matcher.matches()) {
                return true;
            }
        }

        return false;
    }
}
