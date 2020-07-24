package me.activated.core.listeners;

import me.activated.core.plugin.AquaCore;
import me.activated.core.punishments.player.PunishData;
import me.activated.core.punishments.player.PunishPlayerData;
import me.activated.core.enums.PunishmentsLanguage;
import me.activated.core.punishments.utilities.punishments.Punishment;
import me.activated.core.utilities.chat.Replacement;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class PunishmentsListener implements Listener {
    private final AquaCore plugin = AquaCore.INSTANCE;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleAsyncLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        String name = event.getName();

        if (this.plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(uuid) == null) {
            this.plugin.getPunishmentPlugin().getProfileManager().createPlayerDate(uuid, name);
        }

        PunishPlayerData playerData = this.plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(uuid);
        playerData.setLoading(true);


        String address = event.getAddress().getHostAddress();

        playerData.setAddress(address);
        playerData.checkForAddressChanges(address);

        if (plugin.getPunishmentPlugin().getConfigFile().getBoolean("ALTS.DISALLOW-ALTING")) {
            playerData.checkForPotentialAlts();
            int max = plugin.getPunishmentPlugin().getConfigFile().getInt("ALTS.MAX-ALTS-ON-ALL-IPS", -1);

            if (max != -1) {
                if (playerData.getPotentialAlts().size() > max) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                            plugin.getPunishmentPlugin().getConfigFile().getString("ALTS.KICK-MESSAGE")
                                    .replace("<current>", String.valueOf(playerData.getPotentialAlts().size()))
                                    .replace("<max>", String.valueOf(max)));
                    return;
                }
            }
        }

        playerData.load();
        playerData.getPunishData().load();

        playerData.getPunishData().getPunishments().forEach(punishment -> {
            if (punishment.hasExpired() && punishment.isLast()) {
                punishment.setLast(false);
                punishment.save(true);
            }
        });

        AtomicReference<Punishment> blacklist = new AtomicReference<>();
        playerData.getAlts().forEach(alt -> {
            PunishPlayerData altData = plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(alt.getUniqueId());
            if (altData != null && altData.getPunishData().isBlacklisted() && Bukkit.getPlayer(alt.getName()) != null) {
                blacklist.set(altData.getPunishData().getActiveBlacklist());
            } else {
                PunishData punishData = new PunishData(null);
                punishData.forceLoadBlacklists(alt.getUniqueId());

                if (punishData.isBlacklisted()) {
                    blacklist.set(punishData.getActiveBlacklist());
                }
            }
        });
        if (blacklist.get() != null) {
            Punishment activeBlacklist = blacklist.get();

            Replacement kickMessage = new Replacement(plugin.getPunishmentPlugin().getMessagesManager().getKickMessage("BLACKLIST-OTHER"));

            kickMessage.add("<bannedUser>", activeBlacklist.getName());
            kickMessage.add("<executedBy>", activeBlacklist.getAddedBy());
            kickMessage.add("<reason>", activeBlacklist.getReason());
            kickMessage.add("<duration>", activeBlacklist.getNiceDuration());
            kickMessage.add("<expire>", activeBlacklist.getNiceExpire());
            kickMessage.add("<IPRelative>", activeBlacklist.isIPRelative() ? "&aYes" : "&cNo");

            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, kickMessage.toString().replace(", ", "\n"));

            plugin.getServerManagement().getGlobalPlayers().stream().filter(player -> player.hasPermission("Aqua.punishments.join.alert")).forEach(player -> {
                player.sendMessage(PunishmentsLanguage.JOIN_BLACKLISTED.toString()
                        .replace("<player>", name)
                        .replace("<expire>", "Never"));
            });
            return;
        }

        if (playerData.getPunishData().isBlacklisted()) {
            Punishment activeBlacklist = playerData.getPunishData().getActiveBlacklist();

            Replacement kickMessage = new Replacement(plugin.getPunishmentPlugin().getMessagesManager().getKickMessage("BLACKLIST"));

            kickMessage.add("<bannedUser>", activeBlacklist.getName());
            kickMessage.add("<executedBy>", activeBlacklist.getAddedBy());
            kickMessage.add("<reason>", activeBlacklist.getReason());
            kickMessage.add("<duration>", activeBlacklist.getNiceDuration());
            kickMessage.add("<expire>", activeBlacklist.getNiceExpire());
            kickMessage.add("<IPRelative>", activeBlacklist.isIPRelative() ? "&aYes" : "&cNo");

            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, kickMessage.toString().replace(", ", "\n"));

            plugin.getServerManagement().getGlobalPlayers().stream().filter(player -> player.hasPermission("Aqua.punishments.join.alert")).forEach(player -> {
                player.sendMessage(PunishmentsLanguage.JOIN_BLACKLISTED.toString()
                        .replace("<player>", name)
                        .replace("<expire>", "Never"));
            });
            return;
        }

        if (playerData.getPunishData().isBanned()) {
            Punishment activeBan = playerData.getPunishData().getActiveBan();
            Replacement kickMessage = new Replacement(plugin.getPunishmentPlugin().getMessagesManager().getKickMessage(activeBan.isPermanent() ? "PERM-BAN" : "TEMP-BAN"));

            kickMessage.add("<bannedUser>", activeBan.getName());
            kickMessage.add("<executedBy>", activeBan.getAddedBy());
            kickMessage.add("<reason>", activeBan.getReason());
            kickMessage.add("<duration>", activeBan.getNiceDuration());
            kickMessage.add("<expire>", activeBan.getNiceExpire());
            kickMessage.add("<IPRelative>", activeBan.isIPRelative() ? "&aYes" : "&cNo");

            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, kickMessage.toString().replace(", ", "\n"));

            plugin.getServerManagement().getGlobalPlayers().stream().filter(player -> player.hasPermission("Aqua.punishments.join.alert")).forEach(player -> {
                player.sendMessage(PunishmentsLanguage.JOIN_BANNED.toString()
                        .replace("<player>", name)
                        .replace("<expire>", activeBan.getNiceExpire()));
            });
            return;
        }
        AtomicReference<Punishment> IPBan = new AtomicReference<>();
        playerData.getAlts().forEach(alt -> {
            PunishPlayerData altData = plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(alt.getUniqueId());
            if (altData != null && altData.getPunishData().isIPBanned() && Bukkit.getPlayer(alt.getName()) != null) {
                IPBan.set(altData.getPunishData().getActiveBan());
            } else {
                PunishData punishData = new PunishData(null);
                punishData.forceLoadBans(alt.getUniqueId());

                if (punishData.isIPBanned()) {
                    IPBan.set(punishData.getActiveBan());
                }
            }
        });
        if (IPBan.get() != null) {
            Punishment activeBan = IPBan.get();

            Replacement kickMessage = new Replacement(plugin.getPunishmentPlugin().getMessagesManager().getKickMessage(activeBan.isPermanent() ? "PERM-IP-BAN" : "TEMP-IP-BAN"));

            kickMessage.add("<bannedUser>", activeBan.getName());
            kickMessage.add("<executedBy>", activeBan.getAddedBy());
            kickMessage.add("<reason>", activeBan.getReason());
            kickMessage.add("<duration>", activeBan.getNiceDuration());
            kickMessage.add("<expire>", activeBan.getNiceExpire());
            kickMessage.add("<IPRelative>", activeBan.isIPRelative() ? "&aYes" : "&cNo");

            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, kickMessage.toString().replace(", ", "\n"));

            plugin.getServerManagement().getGlobalPlayers().stream().filter(player -> player.hasPermission("Aqua.punishments.join.alert")).forEach(player -> {
                player.sendMessage(PunishmentsLanguage.JOIN_BANNED.toString()
                        .replace("<player>", name)
                        .replace("<expire>", activeBan.getNiceExpire()));
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleMute(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PunishPlayerData playerData = plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(player.getUniqueId());

        if (playerData == null) return;

        if (!playerData.getPunishData().isMuted()) return;

        Punishment mute = playerData.getPunishData().getActiveMute();
        event.setCancelled(true);

        if (mute.isPermanent()) {
            player.sendMessage(PunishmentsLanguage.MUTE_CANT_TALK_PERM.toString().replace("<duration>", mute.getNiceExpire()));
        } else {
            player.sendMessage(PunishmentsLanguage.MUTE_CANT_TALK_TEMP.toString().replace("<duration>", mute.getNiceExpire()));
        }
    }

    @EventHandler
    public void handleQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PunishPlayerData playerData = this.plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(player.getUniqueId());
        UUID uuid = player.getUniqueId();

        if (playerData == null) return;

        Tasks.runAsync(plugin, () -> {
            playerData.save();
            plugin.getPunishmentPlugin().getProfileManager().unloadData(uuid);
        });
    }
}
