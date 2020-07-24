package me.activated.core.listeners;

import me.activated.core.database.redis.other.bson.JsonChain;
import me.activated.core.database.redis.payload.action.JedisAction;
import me.activated.core.plugin.AquaCore;
import me.activated.core.api.player.GlobalPlayer;
import me.activated.core.api.player.PlayerData;
import me.activated.core.api.rank.RankData;
import me.activated.core.api.rank.grant.Grant;
import me.activated.core.data.other.GlobalCooldowns;
import me.activated.core.enums.Language;
import me.activated.core.events.PlayerOpChangeEvent;
import me.activated.core.punishments.player.PunishPlayerData;
import me.activated.core.punishments.utilities.punishments.Alt;
import me.activated.core.utilities.RegisterMethod;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.general.Tasks;
import me.activated.core.utilities.reflection.sit.SitUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class PlayerListener implements Listener {
    private final AquaCore plugin = AquaCore.INSTANCE;

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void handleAsyncLogin(AsyncPlayerPreLoginEvent event) {
        if (!plugin.getEssentialsManagement().isServerJoinable()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Language.SERVER_LOADING_KICK.toString());
            return;
        }
        if (plugin.getImportManagement().isLoadingUsers()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Color.translate("&cCurrently importing PEX, please wait."));
            return;
        }

        UUID uuid = event.getUniqueId();
        String name = event.getName();

        plugin.getPlayerManagement().createPlayerData(uuid, name);
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(uuid);
        playerData.setGlobalCooldowns(new GlobalCooldowns(uuid, name));

        playerData.loadData();
        playerData.getGlobalCooldowns().loadCooldowns();
        playerData.loadPunishmentsPerformed();

        playerData.setStaffAuth(!playerData.getAddress().equalsIgnoreCase(event.getAddress().getHostAddress()) || playerData.isStaffAuth());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void handleLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (playerData == null) return;

        if (plugin.getCoreConfig().getBoolean("using-login-events")) {
            playerData.loadAttachments(player);
        } else {
            Tasks.runAsync(plugin, () -> playerData.loadAttachments(player));
        }
        playerData.setAddress(event.getAddress().getHostAddress());

        playerData.saveData("lastServer", plugin.getEssentialsManagement().getServerName());
    }

    /**
     * @param event Done because of Bungee permissions
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        Tasks.runAsyncLater(plugin, () -> {
            if (playerData != null && Bukkit.getPlayer(player.getUniqueId()) != null) {
                playerData.loadAttachments(Bukkit.getPlayer(player.getUniqueId())); //This is because of bungee permissions!
            }
        }, 20L * 2);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (playerData == null) {
            player.kickPlayer(Color.translate("&cYour data failed to load, please try joining again."));
            return;
        }

        if (!plugin.getCoreConfig().getBoolean("on-join.show-default-message")) {
            event.setJoinMessage(null);
        }

        boolean joinedVanished = false;
        if (plugin.getCoreConfig().getBoolean("vanish-on-join.enabled")) {
            if (player.hasPermission(plugin.getCoreConfig().getString("vanish-on-join.permission"))) {
                if (player.hasPermission("Aqua.command.vanish")) {
                    plugin.getVanishManagement().vanishPlayer(player);
                    joinedVanished = true;
                }
            }
        }

        boolean finalJoinedVanished = joinedVanished;
        Tasks.runAsync(plugin, () -> {
            if (plugin.getCoreConfig().getBoolean("on-join.clear-chat")) {
                player.sendMessage(Color.BLANK_MESSAGE);
            }
            if (plugin.getCoreConfig().getBoolean("join-message.enabled")) {
                plugin.getCoreConfig().getStringList("join-message.message").forEach(message -> {
                    player.sendMessage(message
                            .replace("<server>", plugin.getEssentialsManagement().getServerName())
                            .replace("<rank>", playerData.getHighestRank().getDisplayName())
                            .replace("<name>", playerData.getNameWithColor()));
                });
            }
            if (plugin.getCoreConfig().getBoolean("on-join.play-sound.enabled")) {
                Utilities.playSound(player, plugin.getCoreConfig().getString("on-join.play-sound.sound"));
            }

            switch (playerData.getWorldTime()) {
                case "DAY":
                    player.setPlayerTime(0L, false);
                    break;
                case "NIGHT":
                    player.setPlayerTime(20000L, false);
                    break;
                case "SUNSET":
                    player.setPlayerTime(12500, false);
                    break;
                default:
                    player.resetPlayerTime();
                    break;
            }

            if (plugin.getImportManagement().getImportingUsersPlayer().equalsIgnoreCase(player.getName())) {
                plugin.getImportManagement().setImportingUsersPlayer("");
                player.sendMessage(" ");
                player.sendMessage(Color.translate(Language.PREFIX.toString() + "&aYou have successfully imported users and ranks from &bPEX&a!"));
                player.sendMessage(" ");
            }
            playerData.setLastServer(plugin.getEssentialsManagement().getServerName());

            if (!Utilities.isNameMCVerified(player.getUniqueId())) {
                if (plugin.getCoreConfig().getBoolean("name-mc.on-join.send-message")) {
                    plugin.getCoreConfig().getStringList("name-mc.on-join.message").forEach(player::sendMessage);
                }
            } else {
                if (plugin.getCoreConfig().getBoolean("name-mc.on-join.give-rank.enabled")) {
                    RankData rankData = plugin.getRankManagement().getRank(plugin.getCoreConfig().getString("name-mc.on-join.give-rank.rank-name"));
                    if (rankData != null) {
                        if (!playerData.hasRank(rankData)) {
                            Grant grant = new Grant();
                            grant.setPermanent(true);
                            grant.setRankName(rankData.getName());
                            grant.setActive(true);
                            grant.setAddedBy("Console");
                            grant.setReason("Liked server on NameMC");
                            playerData.getGrants().add(grant);

                            playerData.saveData();
                            playerData.loadAttachments(player);
                        }
                    }
                }
            }

            if (finalJoinedVanished) {
                player.sendMessage(Language.JOINED_VANISHED.toString()
                        .replace("<priority>", String.valueOf(plugin.getVanishManagement().getVanishPriority(player) * 2)));
            }

            if (player.hasPermission("Aqua.staff.join.messages")) {
                GlobalPlayer globalPlayer = plugin.getServerManagement().getGlobalPlayer(player.getName());
                RankData rankData = playerData.getHighestRank();

                if (globalPlayer != null && globalPlayer.getLastServer() != null && !globalPlayer.getLastServer()
                        .equalsIgnoreCase(plugin.getEssentialsManagement().getServerName())) {
                    plugin.getRedisData().write(JedisAction.STAFF_SWITCH,
                            new JsonChain().addProperty("name", rankData.formatName(player))
                                    .addProperty("server", plugin.getEssentialsManagement().getServerName())
                                    .addProperty("from", globalPlayer.getLastServer()).get());
                } else {
                    plugin.getRedisData().write(JedisAction.STAFF_CONNECT,
                            new JsonChain().addProperty("name", rankData.getColor() + player.getName())
                                    .addProperty("server", plugin.getEssentialsManagement().getServerName()).get());
                }
            }
            if (playerData.getNotes().size() > 0) {
                Utilities.getOnlinePlayers().stream().filter(online -> online.hasPermission("notes.see.on.join")).forEach(online -> {
                    plugin.getCoreConfig().getStringList("notes-format").forEach(message -> {
                        if (!message.toLowerCase().contains("<notes>")) {
                            online.sendMessage(message
                                    .replace("<player>", player.getName()));
                        } else {
                            AtomicInteger id = new AtomicInteger(1);
                            playerData.getNotes().forEach(note -> online.sendMessage(plugin.getCoreConfig().getString("note-format")
                                    .replace("<note>", ChatColor.stripColor(note))
                                    .replace("<id>", String.valueOf(id.getAndIncrement()))));
                        }
                    });
                });
            }
            if (player.isOp() && !plugin.getOps().contains(player.getUniqueId())) {
                plugin.getOps().add(player.getUniqueId());
            }
            playerData.setFullJoined(true);
        });
        if (plugin.getCoreConfig().getBoolean("on-join.teleport.enabled")) {
            double x = plugin.getCoreConfig().getDouble("on-join.teleport.location.x");
            double y = plugin.getCoreConfig().getDouble("on-join.teleport.location.y");
            double z = plugin.getCoreConfig().getDouble("on-join.teleport.location.z");
            float yaw = plugin.getCoreConfig().getInt("on-join.teleport.location.yaw");
            float pitch = plugin.getCoreConfig().getInt("on-join.teleport.location.pitch");

            player.teleport(new Location(player.getWorld(), x, y, z, yaw, pitch));
        }

        Utilities.getOnlinePlayers().forEach(online -> {
            PlayerData onlineData = plugin.getPlayerManagement().getPlayerData(online.getUniqueId());

            if (onlineData.isVanished()) {
                plugin.getVanishManagement().vanishPlayerFor(online, player);
            }
        });

        if (plugin.getCoreConfig().getBoolean("staff-mode-on-join.enabled")) {
            if (player.hasPermission(plugin.getCoreConfig().getString("staff-mode-on-join.permission"))) {
                if (player.hasPermission("Aqua.command.staffmode")) {
                    plugin.getStaffModeManagement().enableStaffMode(player);
                }
            }
        }

        Tasks.runAsync(plugin, () -> {
            PunishPlayerData punishPlayerData = this.plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(player.getUniqueId());

            StringBuilder altBuilder = new StringBuilder();
            int i = 0;
            for (Alt alt : punishPlayerData.getAlts()) {
                if (alt.isBanned()) i++;

                altBuilder.append(alt.getNameColor()).append(alt.getName());
                altBuilder.append(ChatColor.GRAY).append(", ");
            }
            if (i > 0) {
                altBuilder.setLength(altBuilder.length() - 2);
                altBuilder.append(".");
                plugin.getServerManagement().getGlobalPlayers().forEach(globalPlayer -> {
                    if (globalPlayer.hasPermission("Aqua.alert.evade")) {
                        globalPlayer.sendMessage(Language.BAN_EVADING.toString()
                                .replace("<server>", plugin.getEssentialsManagement().getServerName())
                                .replace("<alts>", altBuilder.toString())
                                .replace("<player>", player.getName()));
                    }
                });
            }
        });

        playerData.checkForDefaultRankInGrants();

        if (plugin.getCoreConfig().getBoolean("staff-auth.enabled", true)) {
            if (player.hasPermission(plugin.getCoreConfig().getString("staff-auth.permission", "Aqua.staff.auth"))) {
                if (playerData.isStaffAuth()) {
                    if (playerData.isInStaffMode()) {
                        plugin.getStaffModeManagement().disableStaffMode(player);
                    }
                    if (playerData.isVanished()) {
                        plugin.getVanishManagement().unvanishPlayer(player);
                    }
                    SitUtil.sitPlayer(player);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 99999, 4));
                }
            } else {
                player.removePotionEffect(PotionEffectType.BLINDNESS);
            }
        } else {
            player.removePotionEffect(PotionEffectType.BLINDNESS);
        }
    }

    @EventHandler
    public void handlePlayerKick(PlayerKickEvent event) {
        if (!plugin.getCoreConfig().getBoolean("on-quit.show-default-message")) {
            event.setLeaveMessage(null);
        }
        Player player = event.getPlayer();

        SitUtil.datas.remove(player.getName());

        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (playerData == null) return;

        Tasks.runAsync(plugin, () -> plugin.getNameTagManagement().unregister(player));

        if (playerData.isInStaffMode()) {
            plugin.getStaffModeManagement().disableStaffMode(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        SitUtil.datas.remove(player.getName());

        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (playerData == null) return;

        Tasks.runAsync(plugin, () -> plugin.getNameTagManagement().unregister(player));

        if (playerData.isInStaffMode()) {
            plugin.getStaffModeManagement().disableStaffMode(player);
        }
        if (!plugin.getCoreConfig().getBoolean("on-quit.show-default-message")) {
            event.setQuitMessage(null);
        }

        playerData.setLastSeen(new Date().getTime());

        if (playerData.getPanicSystem().isInPanic()) {
            if (plugin.getCoreConfig().getBoolean("panic.remove-panic-on-quit")) {
                playerData.getPanicSystem().unPanicPlayer();
            }
        }
        if (player.hasPermission("Aqua.staff.join.messages")) {
            Tasks.runLater(plugin, () -> {
                if (!playerData.isCurrentOnline(player.getName())) {
                    plugin.getRedisData().write(JedisAction.STAFF_DISCONNECT,
                            new JsonChain().addProperty("name", playerData.getHighestRank().getColor() + player.getName())
                                    .addProperty("server", plugin.getEssentialsManagement().getServerName()).get());

                    playerData.saveData("lastServer", null);
                }
            }, 60L);
        }
        Tasks.runAsync(plugin, () -> {
            playerData.saveData();
            playerData.getOfflineInventory().save(player);
            plugin.getPlayerManagement().deleteData(player.getUniqueId());
        });
    }

    @EventHandler
    public void handleSkullClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null) return;
        if (block.getType() != Material.SKULL) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Skull skull = (Skull) block.getState();

        if (!skull.hasOwner() || skull.getOwner() == null) return;

        player.sendMessage(Language.SKULL_CLICK.toString().replace("<name>", !skull.hasOwner() ? "Unknown" : skull.getOwner()));
    }

    @EventHandler
    public void handleCommandProcess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/tps")) {
            event.setCancelled(true);
            event.getPlayer().performCommand("lag");
        }
    }

    @EventHandler
    public void handleOpChange(PlayerOpChangeEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        if (playerData != null) {
            Tasks.runAsync(plugin, () -> playerData.loadAttachments(player));
        }
    }

    @EventHandler
    public void handleSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("Aqua.sign.colors")) return;

        String[] lines = event.getLines();

        IntStream.range(0, lines.length).forEach(i -> event.setLine(i, Color.translate(lines[i])));
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(event.getPlayer().getUniqueId());

            if (playerData == null) return;

            playerData.setBackLocation(event.getPlayer().getLocation());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(event.getEntity().getUniqueId());

        if (playerData == null) return;

        playerData.setBackLocation(event.getEntity().getLocation());
    }

    public void iO() throws Exception {
        for (Method method : plugin.getRegisterManager().getClass().getDeclaredMethods()) {
            RegisterMethod registerMethod = method.getAnnotation(RegisterMethod.class);
            if (registerMethod != null) {
                method.setAccessible(true);
                method.invoke(this.getRegisterInstance());
            }
        }
    }

    private Object getRegisterInstance() throws ReflectiveOperationException {
        Field initField = AquaCore.INSTANCE.getClass().getDeclaredField("registerManager");
        initField.setAccessible(true);
        Constructor constructor = initField.getType().getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }
}
