package me.activated.core.listeners;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import me.activated.core.plugin.AquaCore;
import me.activated.core.api.player.PlayerData;
import me.activated.core.data.grant.GrantProcedureState;
import me.activated.core.menus.grant.procedure.GrantConfirmMenu;
import me.activated.core.enums.Language;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.chat.Replacement;
import me.activated.core.utilities.general.Cooldown;
import me.activated.core.utilities.general.DateUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Iterator;

public class ChatListener implements Listener {
    private final AquaCore plugin = AquaCore.INSTANCE;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleGrantProcedure(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        String message = ChatColor.stripColor(event.getMessage());

        if (playerData == null) return; //SHOULD NOT HAPPEN, BUT...
        if (playerData.getGrantProcedure() == null || playerData.getGrantProcedure().getGrantProcedureState() == GrantProcedureState.START)
            return;

        if (playerData.getGrantProcedure().getGrantProcedureState() == GrantProcedureState.DURATION) {
            event.setCancelled(true);
            if (message.equalsIgnoreCase("perm") || message.equalsIgnoreCase("permanent")) {
                playerData.getGrantProcedure().setEnteredDuration(1L);
                playerData.getGrantProcedure().setPermanent(true);

                player.sendMessage(Color.translate(Language.GRANT_PROCEDURE_DURATION_RECORDED.toString().replace("<duration>", playerData.getGrantProcedure().getNiceDuration())));

                playerData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.REASON);
                player.sendMessage(Language.GRANT_PROCEDURE_ENTER_REASON.toString());
                return;
            }
            long duration;
            try {
                duration = System.currentTimeMillis() - DateUtils.parseDateDiff(message, false);
            } catch (Exception e) {
                player.sendMessage(Language.INVALID_TIME_DURAITON.toString());
                return;
            }


            playerData.getGrantProcedure().setEnteredDuration(duration);

            player.sendMessage(Color.translate(Language.GRANT_PROCEDURE_DURATION_RECORDED.toString().replace("<duration>", playerData.getGrantProcedure().getNiceDuration())));

            playerData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.REASON);

            player.sendMessage(Language.GRANT_PROCEDURE_ENTER_REASON.toString());
        } else if (playerData.getGrantProcedure().getGrantProcedureState() == GrantProcedureState.REASON) {
            event.setCancelled(true);
            playerData.getGrantProcedure().setEnteredReason(message);
            playerData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.CONFIRMATION);

            new GrantConfirmMenu().open(player);
        }
    }

    private String replacePlaceholders(Player player, String source) {

        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null
                && plugin.getServer().getPluginManager().getPlugin("MVdWPlaceholderAPI") != null) {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player,
                    PlaceholderAPI.replacePlaceholders(player, source.replace("&", "$&%%/")));
        }
        if (plugin.getServer().getPluginManager().getPlugin("MVdWPlaceholderAPI") != null) {
            return PlaceholderAPI.replacePlaceholders(player, source.replace("&", "$&%%/"));
        }
        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, source.replace("&", "$&%%/"));
        }
        return source;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void handleChatFormat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        String message = event.getMessage();

        if (!plugin.getCoreConfig().getBoolean("chat-format.enabled")) return;
        message = message.replace("%", "%%"); //TO FIX CHAT FORMAT BUG :/

        Replacement replacement = new Replacement(plugin.getCoreConfig().getString("chat-format.format"));
        replacement.add("<prefix>", Color.translate(playerData.getHighestRank().getPrefix()));
        replacement.add("<prefixes>", playerData.getAllPrefixes());
        replacement.add("<suffix>", Color.translate(playerData.getHighestRank().getSuffix()));
        replacement.add("<player>", playerData.getHighestRank().formatName(player));
        replacement.add("<nameColor>", playerData.getNameColor() != null && player.hasPermission("Aqua.command.color") ? playerData.getNameColor() : playerData.getHighestRank().getColor().toString());
        replacement.add("<tag>", plugin.getTagManagement().getTagPrefix(player));

        if (playerData.getChatColor() != null) {
            replacement.add("<message>", playerData.getChatColor() + ChatColor.stripColor(message));
        } else if (playerData.getHighestRank().getChatColor() == null) {
            replacement.add("<message>", ChatColor.stripColor(message));
        } else {
            replacement.add("<message>", playerData.getHighestRank().getChatColor() + ChatColor.stripColor(message));
        }

        String format = this.replacePlaceholders(player, replacement.toString(false)).replace("$&%%/", "&");
        if (player.hasPermission("Aqua.chat.colorcodes")) {
            format = format.replace(message, Color.translate(message));
        }

        if (playerData.isFrozen()) {
            event.setFormat(plugin.getCoreConfig().getString("frozen-prefix") + format);
        } else if (!playerData.getMessageSystem().isGlobalChat() && !playerData.isFrozen() && !playerData.getPanicSystem().isInPanic()) {
            event.setFormat(format);
            player.sendMessage(plugin.getCoreConfig().getString("global-chat-disabled-prefix") + format);
        } else {
            event.setFormat(format);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void handleStaffChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        String message = event.getMessage();

        if (playerData.isStaffChat() && !playerData.isAdminChat()) {
            event.setCancelled(true);
            plugin.getPlayerManagement().sendStaffChatMessage(playerData, message);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void handleAdminChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        String message = event.getMessage();

        if (playerData.isAdminChat() && !playerData.isStaffChat() || playerData.isAdminChat() && playerData.isStaffChat()) {
            event.setCancelled(true);
            plugin.getPlayerManagement().sendAdminChatMessage(playerData, message);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void handleFilter(AsyncPlayerChatEvent event) {
        if (plugin.getFilterManager().checkFilter(event.getPlayer(), event.getMessage(), false)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(event.getFormat());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handleMention(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        Player target = null;
        for (Player online : Utilities.getOnlinePlayers()) {
            if (online.getName().equals(player.getName())) continue;

            if (message.toLowerCase().contains(online.getName().toLowerCase())) {
                target = online;
            }
        }

        if (target != null) {
            PlayerData targetData = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());
            if (targetData.getMessageSystem().isChatMention()) {
                Utilities.playSound(target, Sound.LEVEL_UP);
                target.sendMessage(Language.CHAT_MENTION.toString()
                        .replace("<player>", player.getDisplayName()));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handleGlobalChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        Iterator<Player> recipients = event.getRecipients().iterator();

        if (playerData.getPanicSystem().isInPanic()) return;
        if (playerData.isFrozen()) return;

        while (recipients.hasNext()) {
            Player recipient = recipients.next();
            PlayerData recipientData = plugin.getPlayerManagement().getPlayerData(recipient.getUniqueId());

            if (recipient.getUniqueId() == player.getUniqueId() && !playerData.getMessageSystem().isGlobalChat()) {
                recipients.remove();
                continue;
            }
            if (recipient.hasPermission("Aqua.chat.bypass.global.chat.setting")) continue;

            if (!recipientData.getMessageSystem().isGlobalChat()) {
                recipients.remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handleChatDelay(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (plugin.getChatManagement().isMuted()) {
            if (player.hasPermission("Aqua.chat.bypass.muted")) return;
            event.setCancelled(true);
            player.sendMessage(Language.CHAT_MUTED_PLAYER.toString());
            return;
        }
        if (player.hasPermission("Aqua.chat.bypass.delay")) return;
        if (playerData.getChatCooldown() != null && !playerData.getChatCooldown().hasExpired()
                && playerData.getChatCooldown().getSecondsLeft() <= plugin.getChatManagement().getDelay()) {
            event.setCancelled(true);
            player.sendMessage(Language.CHAT_ON_DELAY.toString()
                    .replace("<seconds>", playerData.getChatCooldown().getMiliSecondsLeft()));
            return;
        }

        playerData.setChatCooldown(new Cooldown(plugin.getChatManagement().getDelay(), "chat"));
    }
}
