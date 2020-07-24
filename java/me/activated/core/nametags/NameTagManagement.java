package me.activated.core.nametags;

import lombok.Getter;
import me.activated.core.plugin.AquaCore;
import me.activated.core.api.player.PlayerData;
import me.activated.core.utilities.Manager;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NameTagManagement extends Manager {

    @Getter
    private final List<UUID> players = new ArrayList<>();

    private String getTeamPrefix(String color) {
        return "NT_" + color;
    }

    public NameTagManagement(AquaCore plugin) {
        super(plugin);

        if (plugin.getCoreConfig().getBoolean("use-nametags")) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Update(), 2L, 2L);
        }
    }

    public void createScoreboard(Player player) {
        if (player == null || !player.isOnline()) return;
        Scoreboard scoreboard = player.getScoreboard();

        if (scoreboard == null || scoreboard.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }
        player.setScoreboard(scoreboard);
        this.players.add(player.getUniqueId());
    }

    private void setup(Player player) {
        if (player.getScoreboard() == null) return;
        if (!this.players.contains(player.getUniqueId())) return;

        Scoreboard scoreboard = player.getScoreboard();

        for (ChatColor chatColor : ChatColor.values()) {
            if (!chatColor.isColor()) continue;

            Team team = scoreboard.getTeam(getTeamPrefix(chatColor.toString()));
            Team bold = scoreboard.getTeam(getTeamPrefix(chatColor.toString() + ChatColor.BOLD.toString()));
            Team italic = scoreboard.getTeam(getTeamPrefix(chatColor.toString() + ChatColor.ITALIC.toString()));
            Team italicAndBold = scoreboard.getTeam(getTeamPrefix(chatColor.toString() + ChatColor.BOLD.toString() + ChatColor.ITALIC.toString()));

            if (team == null) {
                team = scoreboard.registerNewTeam(getTeamPrefix(chatColor.toString()));
            }
            if (bold == null) {
                bold = scoreboard.registerNewTeam(getTeamPrefix(chatColor.toString() + ChatColor.BOLD.toString()));
            }
            if (italic == null) {
                italic = scoreboard.registerNewTeam(getTeamPrefix(chatColor.toString() + ChatColor.ITALIC.toString()));
            }
            if (italicAndBold == null) {
                italicAndBold = scoreboard.registerNewTeam(getTeamPrefix(chatColor.toString() + ChatColor.BOLD.toString() + ChatColor.ITALIC.toString()));
            }

            bold.setPrefix(chatColor.toString() + ChatColor.BOLD.toString());
            italic.setPrefix(chatColor.toString() + ChatColor.ITALIC.toString());
            italicAndBold.setPrefix(chatColor.toString() + ChatColor.BOLD.toString() + ChatColor.ITALIC.toString());
            team.setPrefix(chatColor.toString());
        }
        Team staffMode = scoreboard.getTeam("staff-mode");
        Team vanished = scoreboard.getTeam("vanish-mode");

        if (staffMode == null) {
            staffMode = scoreboard.registerNewTeam("staff-mode");
        }
        if (vanished == null) {
            vanished = scoreboard.registerNewTeam("vanish-mode");
        }
        staffMode.setPrefix(Color.translate(plugin.getCoreConfig().getString("staff-tags.staff-mode", "&7[&bS&7] &7")));
        vanished.setPrefix(Color.translate(plugin.getCoreConfig().getString("staff-tags.vanish", "&7[&bV&7] &7")));

        for (Player online : Utilities.getOnlinePlayers()) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(online.getUniqueId());

            if (playerData == null) continue;

            String nameColor;
            if (playerData.isFrozen() || playerData.getPanicSystem().isInPanic()) {
                nameColor = ChatColor.DARK_GREEN.toString();
            } else if (playerData.getNameColor() != null && plugin.getEssentialsManagement().useNameColorBellowName()) {
                nameColor = playerData.getNameColor();
            } else {
                nameColor = playerData.getHighestRank().getColor().toString();
            }

            Team add = scoreboard.getTeam(getTeamPrefix(nameColor));

            if (playerData.isInStaffMode() && !staffMode.hasEntry(online.getName())) {
                staffMode.addEntry(online.getName());
            }
            if (playerData.isVanished() && !vanished.hasEntry(online.getName()) && !playerData.isInStaffMode()) {
                vanished.addEntry(online.getName());
            }

            if (!playerData.isVanished() && vanished.hasEntry(online.getName())) {
                vanished.removeEntry(online.getName());
            }
            if (!playerData.isInStaffMode() && staffMode.hasEntry(online.getName())) {
                staffMode.removeEntry(online.getName());
            }

            if (playerData.isVanished() && playerData.isInStaffMode()) {
                if (vanished.hasEntry(online.getName())) {
                    vanished.removeEntry(online.getName());
                }
                if (!staffMode.hasEntry(online.getName())) {
                    staffMode.addEntry(online.getName());
                }
            }

            if (add != null && !staffMode.hasEntry(online.getName()) && !vanished.hasEntry(online.getName())) {
                if (!add.hasEntry(online.getName())) {
                    add.addEntry(online.getName());
                }
            }
        }

        if (player.getScoreboard() != scoreboard) {
            player.setScoreboard(scoreboard);
        }
    }

    public void unregister(Player player) {
        if (player.getScoreboard() == null) return;
        if (!this.players.contains(player.getUniqueId())) return;

        try {
            Scoreboard scoreboard = player.getScoreboard();

            for (ChatColor chatColor : ChatColor.values()) {
                if (!chatColor.isColor()) continue;

                Team team = scoreboard.getTeam(getTeamPrefix(chatColor.toString()));

                Team bold = scoreboard.getTeam(getTeamPrefix(chatColor.toString() + ChatColor.BOLD.toString()));
                Team italic = scoreboard.getTeam(getTeamPrefix(chatColor.toString() + ChatColor.ITALIC.toString()));
                Team italicAndBold = scoreboard.getTeam(getTeamPrefix(chatColor.toString() + ChatColor.BOLD.toString() + ChatColor.ITALIC.toString()));

                if (team != null) {
                    team.unregister();
                }
                if (bold != null) {
                    bold.unregister();
                }
                if (italic != null) {
                    italic.unregister();
                }
                if (italicAndBold != null) {
                    italicAndBold.unregister();
                }
            }

            Team staffMode = scoreboard.getTeam("staff-mode");
            Team vanished = scoreboard.getTeam("vanish-mode");

            if (staffMode != null) {
                staffMode.unregister();
            }
            if (vanished != null) {
                vanished.unregister();
            }
        } catch (Exception ignored) {
        }
        this.players.remove(player.getUniqueId());
    }

    private class Update implements Runnable {

        @Override
        public void run() {
            Utilities.getOnlinePlayers().forEach(NameTagManagement.this::setup);
        }
    }
}
