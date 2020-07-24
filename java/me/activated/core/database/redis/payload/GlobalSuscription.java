package me.activated.core.database.redis.payload;

import com.google.gson.JsonObject;
import me.activated.core.database.redis.other.bson.JsonChain;
import me.activated.core.database.redis.other.suscriber.handle.JedisHandle;
import me.activated.core.plugin.AquaCore;
import me.activated.core.api.ServerData;
import me.activated.core.api.player.GlobalPlayer;
import me.activated.core.api.player.PlayerData;
import me.activated.core.data.other.report.Report;
import me.activated.core.data.grant.GrantSerilization;
import me.activated.core.database.redis.payload.action.JedisAction;
import me.activated.core.events.GlobalPlayerCreateEvent;
import me.activated.core.events.GlobalPlayerDestroyEvent;
import me.activated.core.punishments.player.PunishPlayerData;
import me.activated.core.enums.PunishmentsLanguage;
import me.activated.core.punishments.utilities.punishments.Punishment;
import me.activated.core.punishments.utilities.punishments.PunishmentType;
import me.activated.core.enums.Language;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.ChatComponentBuilder;
import me.activated.core.utilities.chat.Clickable;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.chat.Replacement;
import me.activated.core.utilities.general.DateUtils;
import me.activated.core.utilities.general.StringUtils;
import me.activated.core.utilities.general.Tasks;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.UUID;
import java.util.stream.Collectors;

public class GlobalSuscription implements JedisHandle {
    private final AquaCore plugin = AquaCore.INSTANCE;

    @Override
    public void handleMessage(JsonObject object) {
        JedisAction payload;
        try {
            payload = JedisAction.valueOf(object.get("payload").getAsString());
        } catch (IllegalArgumentException ignored) {
            return;
        }
        JsonObject data = object.get("data").getAsJsonObject();

        if (payload == JedisAction.SERVER_DATA) {
            ServerData serverData = plugin.getServerManagement().getServerData(data.get("name").getAsString());
            if (serverData == null) {
                serverData = plugin.getServerManagement().createServerData(data.get("name").getAsString());
            }
            serverData.setWhitelisted(data.get("whitelisted").getAsBoolean());
            serverData.setLastTick(data.get("lastTick").getAsLong());
            serverData.setMaxPlayers(data.get("maxPlayers").getAsInt());
            serverData.setRecentTps(new double[]{data.get("tps1").getAsDouble(), data.get("tps2").getAsDouble(), data.get("tps3").getAsDouble()});
            serverData.setNames(StringUtils.getListFromString(data.get("players").getAsString()));

            plugin.getServerManagement().getConnectedServers().removeIf(next -> System.currentTimeMillis() - next.getLastTick() >= 15000L);

            Iterator<GlobalPlayer> globalPlayers = serverData.getOnlinePlayers().iterator();
            while (globalPlayers.hasNext()) {
                GlobalPlayer globalPlayer = globalPlayers.next();

                if (System.currentTimeMillis() - globalPlayer.getLastActivity() >= 5000L) {
                    GlobalPlayerDestroyEvent event = new GlobalPlayerDestroyEvent(globalPlayer);
                    plugin.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        globalPlayers.remove();
                    }
                }
            }
        }
        if (payload == JedisAction.PLAYER_DATA) {
            boolean created = false;
            GlobalPlayer globalPlayer = plugin.getServerManagement().getGlobalPlayer(data.get("name").getAsString());
            if (globalPlayer == null) {
                ServerData serverData = plugin.getServerManagement().getServerData(data.get("server").getAsString());
                if (serverData != null) {
                    GlobalPlayer toAdd = new GlobalPlayer();
                    toAdd.setName(data.get("name").getAsString());

                    serverData.getOnlinePlayers().add(toAdd);

                    created = true;
                    globalPlayer = plugin.getServerManagement().getGlobalPlayer(data.get("name").getAsString());
                }
            }
            if (globalPlayer == null) return;

            globalPlayer.setName(data.get("name").getAsString());
            globalPlayer.setUniqueId(UUID.fromString(data.get("uuid").getAsString()));
            globalPlayer.setServer(data.get("server").getAsString());
            globalPlayer.setPermissions(StringUtils.getListFromString(data.get("permissions").getAsString()));
            globalPlayer.setAddress(data.get("address").getAsString());
            globalPlayer.setRankName(data.get("rank").getAsString());
            globalPlayer.setLastSeen(data.get("lastSeen").getAsLong());
            globalPlayer.setFirstJoined(data.get("firstJoined").getAsString());
            globalPlayer.setRankWeight(data.get("rankWeight").getAsInt());
            globalPlayer.setOp(data.get("op").getAsBoolean());
            globalPlayer.setLastActivity(data.get("lastActivity").getAsLong());
            globalPlayer.setVanished(data.has("vanished") && data.get("vanished").getAsBoolean());
            globalPlayer.setLastServer(data.has("lastServer") ? data.get("lastServer").getAsString() : null);
            globalPlayer.setStaffChatAlerts(data.has("staffChatAlerts") && data.get("staffChatAlerts").getAsBoolean());
            globalPlayer.setAdminChatAlerts(data.has("adminChatAlerts") && data.get("adminChatAlerts").getAsBoolean());
            globalPlayer.setHelpopAlerts(data.has("helpopAlerts") && data.get("helpopAlerts").getAsBoolean());
            globalPlayer.setReportAlerts(data.has("reportAlerts") && data.get("reportAlerts").getAsBoolean());

            if (created) {
                plugin.getServer().getPluginManager().callEvent(new GlobalPlayerCreateEvent(globalPlayer));
            }
        }
        if (payload == JedisAction.PLAYER_MESSAGE) {
            Player player = Bukkit.getPlayer(data.get("name").getAsString());
            if (player != null) {
                player.sendMessage(data.get("message").getAsString());
            }
        }
        if (payload == JedisAction.SERVER_ONLINE) {
            String server = data.get("server").getAsString();
            Utilities.getOnlinePlayers().stream().filter(player -> player.hasPermission("Aqua.server.status.messages")).forEach(player -> {
                player.sendMessage(Language.CAME_ONLINE.toString()
                        .replace("<server>", server));
            });
            Bukkit.getConsoleSender().sendMessage(Language.CAME_ONLINE.toString()
                    .replace("<server>", server));
        }
        if (payload == JedisAction.SERVER_OFFLINE) {
            String server = data.get("server").getAsString();
            Utilities.getOnlinePlayers().stream().filter(player -> player.hasPermission("Aqua.server.status.messages")).forEach(player -> {
                player.sendMessage(Language.WENT_OFFLINE.toString()
                        .replace("<server>", server));
            });
            Bukkit.getConsoleSender().sendMessage(Language.WENT_OFFLINE.toString()
                    .replace("<server>", server));
        }
        if (payload == JedisAction.RANKS_UPDATE) {
            Tasks.runAsync(plugin, () -> {
                plugin.getRankManagement().loadRanks();
                plugin.getRankManagement().saveRanksToConfig();
            });
        }
        if (payload == JedisAction.TAGS_UPDATE) {
            Tasks.runAsync(plugin, () -> {
                plugin.getTagManagement().loadTags();
                plugin.getTagManagement().saveTagsToConfig();
            });
        }
        if (payload == JedisAction.GRANTS_UPDATE) {
            String name = data.get("name").getAsString();
            String grants = data.get("grants").getAsString();

            Player player = Bukkit.getPlayer(name);
            if (player != null) {
                PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
                playerData.setGrants(GrantSerilization.deserilizeGrants(grants));
                playerData.loadAttachments(player);
            }
        }
        if (payload == JedisAction.PLAYER_PERMISSIONS_UPDATE) {
            String name = data.get("name").getAsString();
            String permissions = data.get("permissions").getAsString();

            Player player = Bukkit.getPlayer(name);
            if (player != null) {
                PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
                playerData.setPermissions(StringUtils.getListFromString(permissions));

                playerData.loadAttachments(player);

                player.sendMessage(Color.translate(Language.PREFIX + "&aYour permissions have been updated!"));
            }
        }
        if (payload == JedisAction.PERMISSIONS_UPDATE) {
            Tasks.runAsync(plugin, () -> Utilities.getOnlinePlayers().forEach(player -> {
                PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

                if (playerData != null) {
                    playerData.loadAttachments(player);
                }
            }));
        }
        if (payload == JedisAction.REPORT_SAVE) {
            String date = data.get("date").getAsString();
            String reporter = data.get("reporter").getAsString();
            String reason = data.get("reason").getAsString();
            UUID uuid = UUID.fromString(data.get("uuid").getAsString());
            String reporterServer = data.get("reporterServer").getAsString();
            String reportedServer = data.get("reportedServer").getAsString();

            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(uuid);
            if (playerData != null) {
                Report report = new Report(reporter, date, reason, reporterServer, reportedServer, data.get("addedAt").getAsLong(), false, null);
                playerData.getReports().add(report);

                Tasks.runAsync(plugin, playerData::saveData);
            }
        }
        if (payload == JedisAction.STAFF_CONNECT) {
            String name = data.get("name").getAsString();
            String server = data.get("server").getAsString();

            Utilities.getOnlinePlayers().stream().filter(player -> player.hasPermission("Aqua.staff.join.messages")).forEach(player -> {
                player.sendMessage(Language.STAFF_MESSAGES_CONNECT.toString()
                        .replace("<player>", name)
                        .replace("<server>", server));
            });
        }
        if (payload == JedisAction.STAFF_SWITCH) {
            String name = data.get("name").getAsString();
            String server = data.get("server").getAsString();
            String from = data.get("from").getAsString();

            Utilities.getOnlinePlayers().stream().filter(player -> player.hasPermission("Aqua.staff.join.messages")).forEach(player -> {
                player.sendMessage(Language.STAFF_MESSAGES_SWITCH.toString()
                        .replace("<player>", name)
                        .replace("<server>", server)
                        .replace("<from>", from));
            });
        }
        if (payload == JedisAction.STAFF_DISCONNECT) {
            String name = data.get("name").getAsString();
            String server = data.get("server").getAsString();

            Utilities.getOnlinePlayers().stream().filter(player -> player.hasPermission("Aqua.staff.join.messages")).forEach(player -> {
                player.sendMessage(Language.STAFF_MESSAGES_DISCONNECT.toString()
                        .replace("<player>", name)
                        .replace("<server>", server));
            });
        }
        if (payload == JedisAction.SERVER_COMMAND) {
            String server = data.get("server").getAsString();
            String command = data.get("command").getAsString();

            if (command.startsWith("/")) {
                command = command.substring(1);
            }
            if (server.equalsIgnoreCase("all")) {
                Bukkit.getConsoleSender().sendMessage(Color.translate(Language.PREFIX + "&bPerforming &3/" + command + "&7, &brequested by &4" + data.get("sender").getAsString() + "&b."));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            } else if (plugin.getEssentialsManagement().getServerName().equalsIgnoreCase(server)) {
                Bukkit.getConsoleSender().sendMessage(Color.translate(Language.PREFIX + "&bPerforming &3/" + command + "&7, &brequested by &4" + data.get("sender").getAsString() + "&b."));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
        if (payload == JedisAction.LEFT_FROZEN) {
            String displayName = data.get("displayName").getAsString();
            String name = data.get("name").getAsString();
            String server = data.get("server").getAsString();

            ChatComponentBuilder chatComponentBuilder = new ChatComponentBuilder("");
            chatComponentBuilder.append(Language.LEFT_FROZEN.toString()
                    .replace("<name>", Color.translate(displayName))
                    .replace("<server>", server));
            if (plugin.getCoreConfig().getBoolean("left-frozen-click.enabled")) {
                chatComponentBuilder.append(" ");

                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ChatComponentBuilder(plugin.getCoreConfig().getString("left-frozen-click.text-hover")
                                .replace("<name>", name)).create());
                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, plugin.getCoreConfig().getString("left-frozen-click.command")
                        .replace("<name>", name));


                chatComponentBuilder.append(plugin.getCoreConfig().getString("left-frozen-click.text"));
                chatComponentBuilder.setCurrentClickEvent(clickEvent);
                chatComponentBuilder.setCurrentHoverEvent(hoverEvent);
            }
            Utilities.getOnlinePlayers().stream().filter(player -> player.hasPermission("Aqua.command.freeze")).forEach(player -> {
                player.spigot().sendMessage(chatComponentBuilder.create());
            });

        }
        switch (payload) {
            case EXECUTE_UNBAN: {
                String sender = data.get("sender").getAsString();
                String target = data.get("target").getAsString();
                String reason = data.get("reason").getAsString();
                boolean silent = data.get("silent").getAsBoolean();

                String key = silent ? "UNBAN-SILENT" : "UNBAN";

                Clickable clickable = plugin.getPunishmentPlugin().getMessagesManager().getMessage(key);
                if (clickable == null) return;

                Replacement replacement = new Replacement("");
                replacement.add("<senderName>", sender);
                replacement.add("<user>", target);
                replacement.add("<sender>", data.has("senderDisplay") ? data.get("senderDisplay").getAsString() : sender);
                replacement.add("<reason>", reason);
                if (data.has("coloredName")) {
                    replacement.add("<coloredName>", data.get("coloredName").getAsString());
                } else {
                    replacement.add("<coloredName>", sender);
                }

                clickable = plugin.getPunishmentPlugin().getMessagesManager().getMessageWithReplacements(key, replacement);

                Bukkit.getConsoleSender().sendMessage(Color.translate(clickable.getText()));

                if (silent) {
                    for (Player player : Utilities.getOnlinePlayers().stream().filter(player -> player.hasPermission("punishments.see.silent")).collect(Collectors.toList())) {
                        clickable.sendToPlayer(player);
                    }
                } else {
                    for (Player player : Utilities.getOnlinePlayers()) {
                        clickable.sendToPlayer(player);
                    }
                }
                break;
            }
            case EXECUTE_UNMUTE: {
                String sender = data.get("sender").getAsString();
                String target = data.get("target").getAsString();
                String reason = data.get("reason").getAsString();
                boolean silent = data.get("silent").getAsBoolean();

                String key = silent ? "UNMUTE-SILENT" : "UNMUTE";

                Clickable clickable = plugin.getPunishmentPlugin().getMessagesManager().getMessage(key);
                if (clickable == null) return;

                Replacement replacement = new Replacement("");
                replacement.add("<senderName>", sender);
                replacement.add("<user>", target);
                replacement.add("<sender>", data.has("senderDisplay") ? data.get("senderDisplay").getAsString() : sender);
                replacement.add("<reason>", reason);
                if (data.has("coloredName")) {
                    replacement.add("<coloredName>", data.get("coloredName").getAsString());
                } else {
                    replacement.add("<coloredName>", sender);
                }

                clickable = plugin.getPunishmentPlugin().getMessagesManager().getMessageWithReplacements(key, replacement);

                Bukkit.getConsoleSender().sendMessage(Color.translate(clickable.getText()));

                if (silent) {
                    for (Player player : Utilities.getOnlinePlayers().stream().filter(player -> player.hasPermission("punishments.see.silent")).collect(Collectors.toList())) {
                        clickable.sendToPlayer(player);
                    }
                } else {
                    for (Player player : Utilities.getOnlinePlayers()) {
                        clickable.sendToPlayer(player);
                    }
                }
                break;
            }
            case EXECUTE_UNBLACKLIST: {
                String sender = data.get("sender").getAsString();
                String target = data.get("target").getAsString();
                String reason = data.get("reason").getAsString();
                boolean silent = data.get("silent").getAsBoolean();

                String key = !silent ? "UN-BLACKLIST" : "UN-BLACKLIST-SILENT";

                Clickable clickable = plugin.getPunishmentPlugin().getMessagesManager().getMessage(key);

                if (clickable == null) return;

                Replacement replacement = new Replacement("");
                replacement.add("<senderName>", sender);
                replacement.add("<user>", target);
                replacement.add("<sender>", data.has("senderDisplay") ? data.get("senderDisplay").getAsString() : sender);
                replacement.add("<reason>", reason);
                if (data.has("coloredName")) {
                    replacement.add("<coloredName>", data.get("coloredName").getAsString());
                } else {
                    replacement.add("<coloredName>", sender);
                }
                clickable = plugin.getPunishmentPlugin().getMessagesManager().getMessageWithReplacements(key, replacement);

                Bukkit.getConsoleSender().sendMessage(Color.translate(clickable.getText()));

                if (silent) {
                    for (Player player : Utilities.getOnlinePlayers().stream().filter(player -> player.hasPermission("punishments.see.silent")).collect(Collectors.toList())) {
                        clickable.sendToPlayer(player);
                    }
                } else {
                    for (Player player : Utilities.getOnlinePlayers()) {
                        clickable.sendToPlayer(player);
                    }
                }
                break;
            }
            case EXECUTE_ALT_KICK: {
                String name = data.get("name").getAsString();
                String sender = data.get("sender").getAsString();
                String reason = data.get("reason").getAsString();
                String niceDuration = data.get("duration").getAsString();
                boolean permanent = data.get("permanent").getAsBoolean();
                String type = data.get("type").getAsString();
                String alt = data.get("alt").getAsString();

                Tasks.run(plugin, () -> {
                    Player target = Bukkit.getPlayer(name);
                    if (target != null) {
                        if (type.equalsIgnoreCase("BAN")) {
                            Replacement kickMessage = new Replacement(plugin.getPunishmentPlugin().getMessagesManager().getKickMessage(permanent ? "PERM-IP-BAN" : "TEMP-IP-BAN"));

                            kickMessage.add("<bannedUser>", alt);
                            kickMessage.add("<executedBy>", sender);
                            kickMessage.add("<reason>", reason);
                            kickMessage.add("<duration>", niceDuration);
                            kickMessage.add("<expire>", niceDuration);
                            kickMessage.add("<IPRelative>", "&aYes");

                            target.kickPlayer(kickMessage.toString().replace(", ", "\n"));
                        } else if (type.equalsIgnoreCase("BLACKLIST")) {
                            Replacement kickMessage = new Replacement(plugin.getPunishmentPlugin().getMessagesManager().getKickMessage("BLACKLIST-OTHER"));

                            kickMessage.add("<bannedUser>", alt);
                            kickMessage.add("<executedBy>", sender);
                            kickMessage.add("<reason>", reason);
                            kickMessage.add("<duration>", niceDuration);
                            kickMessage.add("<expire>", niceDuration);
                            kickMessage.add("<IPRelative>", "&aYes");

                            target.kickPlayer(kickMessage.toString().replace(", ", "\n"));
                        }
                    }
                });
                break;
            }
            case EXECUTE_PUNISHMENT: {
                UUID uuid = UUID.fromString(data.get("uuid").getAsString());
                String name = data.get("name").getAsString();
                String sender = data.get("sender").getAsString();
                String reason = data.get("reason").getAsString();
                String niceDuration = data.get("niceDuration").getAsString();
                String server = data.get("server").getAsString();
                boolean silent = data.get("silent").getAsBoolean();
                boolean permanent = data.get("permanent").getAsBoolean();
                boolean IPRelative = data.get("IPRelative").getAsBoolean();
                String alts = data.get("alts").getAsString();

                PunishmentType type = PunishmentType.valueOf(data.get("type").getAsString());
                int warns = data.get("warns").getAsInt();

                if (type == PunishmentType.WARN) {
                    if (plugin.getPunishmentPlugin().getConfigFile().getBoolean("WARNS.BAN_ON_REACHED_WARNS")) {
                        int max = plugin.getPunishmentPlugin().getConfigFile().getInt("WARNS.MAX_ACTIVE_WARNS_TO_REACH");
                        boolean removeAll = plugin.getPunishmentPlugin().getConfigFile().getBoolean("WARNS.REMOVE_ALL_ACTIVE_WARNS_ON_BAN");
                        String banReason = plugin.getPunishmentPlugin().getConfigFile().getString("WARNS.BAN.REASON");
                        String durationString = plugin.getPunishmentPlugin().getConfigFile().getString("WARNS.BAN.DURATION");

                        long duration;
                        if (durationString.equalsIgnoreCase("perm") || durationString.equalsIgnoreCase("permanent")) {
                            duration = -5L;
                        } else {
                            try {
                                duration = DateUtils.parseDateDiff(durationString, true);
                            } catch (Exception e) {
                                try {
                                    duration = DateUtils.parseDateDiff("3d", true);
                                } catch (Exception ignored) {
                                    duration = -5L;
                                }
                            }
                        }

                        if (warns >= max) {
                            if (server.equalsIgnoreCase(plugin.getPunishmentPlugin().getConfigFile().getString("SERVER-NAME"))) {
                                long finalDuration = duration;
                                Tasks.runAsync(plugin, () -> {
                                    PunishPlayerData targetData = plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(uuid);

                                    if (targetData == null) {
                                        plugin.getPunishmentPlugin().getProfileManager().createPlayerDate(uuid, name);
                                        targetData = plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(uuid);
                                        targetData.getPunishData().load();
                                    }

                                    Punishment punishment = new Punishment(plugin, targetData, PunishmentType.BAN);
                                    punishment.setSilent(silent);
                                    if (finalDuration != -5L) {
                                        punishment.setPermanent(false);
                                        punishment.setDurationTime(finalDuration);
                                    } else {
                                        punishment.setPermanent(true);
                                    }
                                    punishment.setEnteredDuration(durationString);
                                    punishment.setLast(true);
                                    punishment.setAddedBy("CONSOLE");
                                    punishment.setAddedAt(System.currentTimeMillis());
                                    punishment.setReason(banReason);

                                    targetData.getPunishData().getPunishments().add(punishment);
                                    punishment.save();

                                    if (removeAll) {
                                        targetData.getPunishData().getPunishments().stream().filter(p -> p.getPunishmentType() == PunishmentType.WARN).forEach(warn -> {
                                            warn.setActive(false);
                                            warn.setLast(false);
                                            warn.setRemovedBy("CONSOLE");
                                            warn.setRemovedFor("Not entered.");
                                            warn.setRemovedSilent(false);
                                            warn.setWhenRemoved(System.currentTimeMillis());


                                            Tasks.runAsync(plugin, () -> {
                                                warn.save(true);
                                            });
                                        });
                                    }
                                    punishment.execute(Bukkit.getConsoleSender());

                                    plugin.getPunishmentPlugin().getProfileManager().unloadData(uuid);
                                });
                            }
                        }
                    }
                }

                if (type == PunishmentType.BAN) {
                    Tasks.run(plugin, () -> {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null) {
                            Replacement kickMessage = new Replacement(plugin.getPunishmentPlugin().getMessagesManager().getKickMessage(permanent ? "PERM-BAN" : "TEMP-BAN"));

                            kickMessage.add("<executedBy>", sender);
                            kickMessage.add("<reason>", reason);
                            kickMessage.add("<duration>", niceDuration);
                            kickMessage.add("<expire>", niceDuration);
                            kickMessage.add("<IPRelative>", IPRelative ? "&aYes" : "&cNo");

                            player.kickPlayer(kickMessage.toString().replace(", ", "\n"));
                        }
                    });
                    if (IPRelative) {
                        StringUtils.getListFromString(alts).forEach(alt -> {
                            plugin.getRedisData().write(JedisAction.EXECUTE_ALT_KICK,
                                    new JsonChain()
                                            .addProperty("alt", name)
                                            .addProperty("type", "BAN")
                                            .addProperty("permanent", permanent)
                                            .addProperty("duration", niceDuration)
                                            .addProperty("reason", reason)
                                            .addProperty("sender", sender)
                                            .addProperty("name", alt).get());
                        });
                    }
                    plugin.getPunishmentPlugin().getProfileManager().unloadData(uuid);
                }
                if (type == PunishmentType.BLACKLIST) {
                    Tasks.run(plugin, () -> {
                        Player player = Bukkit.getPlayer(uuid);

                        if (player != null) {
                            Replacement kickMessage = new Replacement(plugin.getPunishmentPlugin().getMessagesManager().getKickMessage("BLACKLIST"));

                            kickMessage.add("<bannedUser>", name);
                            kickMessage.add("<executedBy>", sender);
                            kickMessage.add("<reason>", reason);
                            kickMessage.add("<duration>", niceDuration);
                            kickMessage.add("<expire>", niceDuration);
                            kickMessage.add("<IPRelative>", IPRelative ? "&aYes" : "&cNo");

                            player.kickPlayer(kickMessage.toString().replace(", ", "\n"));
                        }
                    });
                    StringUtils.getListFromString(alts).forEach(alt -> {
                        plugin.getRedisData().write(JedisAction.EXECUTE_ALT_KICK,
                                new JsonChain()
                                        .addProperty("alt", name)
                                        .addProperty("type", "BLACKLIST")
                                        .addProperty("permanent", permanent)
                                        .addProperty("duration", niceDuration)
                                        .addProperty("reason", reason)
                                        .addProperty("sender", sender)
                                        .addProperty("name", alt).get());
                    });
                    plugin.getPunishmentPlugin().getProfileManager().unloadData(uuid);
                }
                if (type == PunishmentType.KICK) {
                    Tasks.run(plugin, () -> {
                        Player player = Bukkit.getPlayer(uuid);

                        if (player != null) {
                            Replacement kickMessage = new Replacement(plugin.getPunishmentPlugin().getMessagesManager().getKickMessage("KICK"));

                            kickMessage.add("<executedBy>", sender);
                            kickMessage.add("<reason>", reason);
                            kickMessage.add("<duration>", niceDuration);
                            kickMessage.add("<expire>", niceDuration);
                            kickMessage.add("<server>", server);
                            kickMessage.add("<IPRelative>", IPRelative ? "&aYes" : "&cNo");

                            player.kickPlayer(kickMessage.toString().replace(", ", "\n"));
                        }
                    });
                }
                if (type == PunishmentType.MUTE) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        if (permanent) {
                            Replacement message = new Replacement(PunishmentsLanguage.MUTE_BEEN_PERM_MUTED.toString());

                            message.add("<executedBy>", sender);
                            message.add("<sender>", sender);
                            message.add("<reason>", reason);
                            message.add("<duration>", niceDuration);
                            message.add("<expire>", niceDuration);
                            message.add("<IPRelative>", IPRelative ? "&aYes" : "&cNo");

                            player.sendMessage(message.toString());
                        } else {
                            Replacement message = new Replacement(PunishmentsLanguage.MUTE_BEEN_TEMP_MUTED.toString());

                            message.add("<executedBy>", sender);
                            message.add("<sender>", sender);
                            message.add("<reason>", reason);
                            message.add("<duration>", niceDuration);
                            message.add("<expire>", niceDuration);
                            message.add("<IPRelative>", IPRelative ? "&aYes" : "&cNo");

                            player.sendMessage(message.toString());
                        }
                    }
                }

                if (type == PunishmentType.WARN) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        if (permanent) {
                            Replacement message = new Replacement(PunishmentsLanguage.WARN_BEEN_PERM_WARNED.toString());

                            message.add("<executedBy>", sender);
                            message.add("<sender>", sender);
                            message.add("<reason>", reason);
                            message.add("<duration>", niceDuration);
                            message.add("<expire>", niceDuration);
                            message.add("<IPRelative>", IPRelative ? "&aYes" : "&cNo");

                            player.sendMessage(message.toString());
                        } else {
                            Replacement message = new Replacement(PunishmentsLanguage.WARN_BEEN_TEMP_WARNED.toString());

                            message.add("<executedBy>", sender);
                            message.add("<sender>", sender);
                            message.add("<reason>", reason);
                            message.add("<duration>", niceDuration);
                            message.add("<expire>", niceDuration);
                            message.add("<IPRelative>", IPRelative ? "&aYes" : "&cNo");

                            player.sendMessage(message.toString());
                        }
                    }
                }

                String key = "";
                switch (type) {
                    case BAN: {
                        if (!IPRelative) {
                            if (permanent) {
                                key = silent ? "PERM-BAN-SILENT" : "PERM-BAN";
                            } else {
                                key = silent ? "TEMP-BAN-SILENT" : "TEMP-BAN";
                            }
                        } else {
                            if (permanent) {
                                key = silent ? "PERM-IP-BAN-SILENT" : "PERM-IP-BAN";
                            } else {
                                key = silent ? "TEMP-IP-BAN-SILENT" : "TEMP-IP-BAN";
                            }
                        }
                        break;
                    }
                    case MUTE: {
                        if (permanent) {
                            key = silent ? "PERM-MUTE-SILENT" : "PERM-MUTE";
                        } else {
                            key = silent ? "TEMP-MUTE-SILENT" : "TEMP-MUTE";
                        }
                        break;
                    }
                    case WARN: {
                        if (permanent) {
                            key = silent ? "PERM-WARN-SILENT" : "PERM-WARN";
                        } else {
                            key = silent ? "TEMP-WARN-SILENT" : "TEMP-WARN";
                        }
                        break;
                    }
                    case BLACKLIST: {
                        key = !silent ? "BLACKLIST" : "BLACKLIST-SILENT";
                        break;
                    }
                    case KICK: {
                        key = !silent ? "KICK" : "KICK-SILENT";
                        break;
                    }
                }
                Clickable clickable = plugin.getPunishmentPlugin().getMessagesManager().getMessage(key);

                if (clickable == null) return;

                Replacement replacement = new Replacement("");
                try {
                    replacement.add("<senderName>", data.has("senderName") ? data.get("senderName").getAsString() : sender);
                } catch (Exception e) {
                    replacement.add("<senderName>", sender);
                }
                if (data.has("coloredName")) {
                    replacement.add("<coloredName>", data.get("coloredName").getAsString());
                } else {
                    replacement.add("<coloredName>", sender);
                }
                replacement.add("<user>", name);
                replacement.add("<sender>", sender);
                replacement.add("<reason>", reason);
                replacement.add("<duration>", niceDuration);

                clickable = plugin.getPunishmentPlugin().getMessagesManager().getMessageWithReplacements(key, replacement);

                Bukkit.getConsoleSender().sendMessage(Color.translate(clickable.getText()));

                if (silent) {
                    for (Player player : Utilities.getOnlinePlayers().stream().filter(player -> player.hasPermission("punishments.see.silent")).collect(Collectors.toList())) {
                        clickable.sendToPlayer(player);
                    }
                } else {
                    for (Player player : Utilities.getOnlinePlayers()) {
                        clickable.sendToPlayer(player);
                    }
                }
                break;
            }
            default:
                break;
        }
    }
}
