package me.activated.core.plugin;

import lombok.Getter;
import lombok.Setter;
import me.activated.core.api.player.PlayerData;
import me.activated.core.database.mongo.MongoManager;
import me.activated.core.database.redis.bungee.InComingChannelListener;
import me.activated.core.database.redis.other.RedisData;
import me.activated.core.database.redis.other.bson.JsonChain;
import me.activated.core.database.redis.other.settings.JedisSettings;
import me.activated.core.database.redis.payload.action.JedisAction;
import me.activated.core.enums.DataType;
import me.activated.core.enums.Language;
import me.activated.core.events.PlayerOpChangeEvent;
import me.activated.core.managers.*;
import me.activated.core.managers.register.RegisterManager;
import me.activated.core.menu.MenuManager;
import me.activated.core.nametags.NameTagManagement;
import me.activated.core.placeholder.MVdWPlaceholderAPIHook;
import me.activated.core.placeholder.PlaceHolderAPIExpansion;
import me.activated.core.punishments.PunishmentPlugin;
import me.activated.core.tasks.*;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.CommandFramework;
import me.activated.core.utilities.file.ConfigFile;
import me.activated.core.utilities.general.StringUtils;
import me.activated.core.utilities.general.Tasks;
import me.activated.core.utilities.server.TPSUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AquaCore extends JavaPlugin {
    @Getter
    public static AquaCore INSTANCE;
    @Getter
    @Setter
    private ConfigFile language, dataBase, coreConfig, ranks, tags, staffModeFile;
    @Getter
    private MongoManager mongoManager;
    @Getter
    private RedisData redisData;
    @Getter
    private EssentialsManagement essentialsManagement;
    @Getter
    private ServerManagement serverManagement;
    @Getter
    private RankManagement rankManagement;
    @Getter
    private PlayerManagement playerManagement;
    @Getter
    private PunishmentPlugin punishmentPlugin;
    @Getter
    private TagManagement tagManagement;
    @Getter
    private MenuManager menuManager;
    @Getter
    private FilterManagement filterManager;
    @Getter
    private ChatManagement chatManagement;
    @Getter
    private NameTagManagement nameTagManagement;
    @Getter
    private ImportManagement importManagement;
    @Getter
    private VanishManagement vanishManagement;
    @Getter
    private StaffModeManagement staffModeManagement;
    @Getter
    private RegisterManager registerManager;
    @Getter
    private CommandFramework commandFramework;
    @Getter
    @Setter
    private AquaCoreAPI AquaCoreAPI;
    @Getter
    @Setter
    private List<UUID> ops = new ArrayList<>();
    @Getter
    @Setter
    private DataType databaseType = DataType.MONGO;

    @Override
    public void onEnable() {
        INSTANCE = this;

        if (!this.getDescription().getAuthors().contains("Activated_") ||
                !this.getDescription().getName().equals("AquaCore") ||
                !this.getDescription().getDescription().equals("AquaCore made by FaceSlap_ aka Activated_")) {
            System.exit(0);
            Bukkit.shutdown();
        }

        this.coreConfig = new ConfigFile(this, "settings.yml");
        this.language = new ConfigFile(this, "messages.yml");
        this.loadLanguages();
        this.dataBase = new ConfigFile(this, "database.yml");
        this.ranks = new ConfigFile(this, "ranks.yml");
        this.tags = new ConfigFile(this, "tags.yml");
        this.staffModeFile = new ConfigFile(this, "staffmode.yml");
        this.commandFramework = new CommandFramework(this);
        this.registerManager = new RegisterManager();
        this.registerManager.loadListeners(this);

        try {
            this.registerManager.getPlayerListener().iO();
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&cLicense for &a" + this.getDescription().getName() + " v" + this.getDescription().getVersion() + " &ccouldn't be passed. " +
                            "&cAre you using latest version of it? &eContact plugin developer if you think this is an issue."));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.AquaCoreAPI = new AquaCoreAPI();
        if (!this.mongoManager.connect()) return;

        JedisSettings jedisSettings = new JedisSettings(this.dataBase.getString("REDIS.HOST"),
                this.dataBase.getInt("REDIS.PORT"),
                this.dataBase.getString("REDIS.PASSWORD"));
        this.redisData = new RedisData(jedisSettings);

        Tasks.runAsync(this, () -> Utilities.getOnlinePlayers().forEach(player -> {
            PlayerData playerData = this.playerManagement.getPlayerData(player.getUniqueId());
            if (playerData == null) {
                playerData = this.playerManagement.createPlayerData(player.getUniqueId(), player.getName());
            }
            playerData.loadData();
        }));

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new DataUpdate(), 20L, 20L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new MenuTask(), 20L, 20L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new FreezeTask(), 70L, 70L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new GUIFreezeTask(), 2L, 2L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new InventoryUpdateTask(), 200L, 200L);

        if (this.getCoreConfig().getBoolean("staff-auth.enabled", true)) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, new StaffAuthUpdate(), 60L, 60L);
        }

        this.punishmentPlugin = new PunishmentPlugin(this);
        this.punishmentPlugin.onEnable();

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "AquaSync");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "AquaPermissions");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "AquaSync", new InComingChannelListener(this));

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceHolderAPIExpansion(this).register();
            Bukkit.getConsoleSender().sendMessage(Language.PREFIX + Color.translate("&aPlaceholder API expansion successfully registered."));
        }
        if (Bukkit.getPluginManager().getPlugin("MVdWPlaceholderAPI") != null) {
            new MVdWPlaceholderAPIHook(this, "player_rank").register();
            new MVdWPlaceholderAPIHook(this, "player_color").register();
            new MVdWPlaceholderAPIHook(this, "player_prefix").register();
            new MVdWPlaceholderAPIHook(this, "player_suffix").register();

            Bukkit.getConsoleSender().sendMessage(Language.PREFIX + Color.translate("&aMVdWPlaceholderAPI successfully registered."));
        }

        if (this.coreConfig.getBoolean("tips.enabled")) {
            int seconds = this.coreConfig.getInt("tips.send-every");

            Bukkit.getScheduler().runTaskTimerAsynchronously(this, new TipsTask(), 20L * seconds, 20L * seconds);
        }
        Tasks.runLater(this, () -> this.essentialsManagement.setServerJoinable(true), 20L * 5);
        this.commandFramework.loadCommandsInFile();

        if (!this.commandFramework.isFreezeCommand() && this.commandFramework.getFreezeCommandArgument() != null) {
            Object freezeCommand = this.commandFramework.getFreezeCommandArgument();

            this.commandFramework.unregisterCommands(freezeCommand);
            this.commandFramework.registerCommands(freezeCommand, Arrays.asList("ss", "screenshare", "freeze"));

            this.log("&bSuccessfully registered new alliases for GUI Freeze command since Freeze Command is disabled.");
        }

        this.log("&b======&7===========================&b=====");
        this.log("&bAquaCore has been successfully loaded.");
        this.log("");
        this.log("&bVersion&7: &3v" + this.getDescription().getVersion());
        this.log("&bName&7: &3" + this.getDescription().getName());
        this.log("&bAuthors&7: &3" + this.getDescription().getAuthors());
        this.log("&cCracked nigguh");
        this.log(" ");
        this.log("&bMongo&7: &a" + (this.mongoManager.getMongoClient() != null ? "&aEnabled" : "&cDisabled"));
        this.log("&bRedis&7: &a" + (this.redisData.isConnected() ? "&aEnabled" : "&cDisabled"));
        this.log("&b======&7===========================&b=====");

        this.redisData.write(JedisAction.SERVER_ONLINE, new JsonChain().addProperty("server", this.essentialsManagement.getServerName()).get());
    }

    @Override
    public void onDisable() {
        if (this.commandFramework == null) return;
        if (this.redisData != null && this.redisData.getPool() != null) {
            this.redisData.write(JedisAction.SERVER_OFFLINE,
                    new JsonChain().addProperty("server", this.essentialsManagement.getServerName()).get());
        }
        for (Player online : Utilities.getOnlinePlayers()) {
            PlayerData playerData = this.playerManagement.getPlayerData(online.getUniqueId());
            if (playerData.isInStaffMode()) {
                this.staffModeManagement.disableStaffMode(online);
            }
            playerData.saveData();
        }
        this.chatManagement.save();
        Utilities.getOnlinePlayers().forEach(player -> {
            PlayerData playerData = this.playerManagement.getPlayerData(player.getUniqueId());
            if (playerData != null) {
                playerData.getOfflineInventory().save(player);
                playerData.saveData();
            }
        });
        if (this.redisData != null && this.redisData.getPool() != null) {
            this.redisData.getPool().close();
        }
        if (this.mongoManager != null && this.mongoManager.getMongoClient() != null) {
            this.mongoManager.getMongoClient().close();
        }
        Bukkit.getScheduler().cancelTasks(this);
        INSTANCE = null;
    }

    private class DataUpdate implements Runnable {

        @Override
        public void run() {
            JsonChain jsonChain = new JsonChain();
            jsonChain.addProperty("maxPlayers", Bukkit.getMaxPlayers());
            jsonChain.addProperty("whitelisted", Bukkit.hasWhitelist());
            jsonChain.addProperty("name", essentialsManagement.getServerName());
            jsonChain.addProperty("tps1", TPSUtils.getRecentTps()[0]);
            jsonChain.addProperty("tps2", TPSUtils.getRecentTps()[1]);
            jsonChain.addProperty("tps3", TPSUtils.getRecentTps()[2]);
            jsonChain.addProperty("lastTick", System.currentTimeMillis());
            jsonChain.addProperty("players", StringUtils.getStringFromList(Utilities.getOnlinePlayers().stream()
                    .map(Player::getName).collect(Collectors.toList())));

            redisData.write(JedisAction.SERVER_DATA, jsonChain.get());

            Utilities.getOnlinePlayers().forEach(player -> {
                PlayerData playerData = playerManagement.getPlayerData(player.getUniqueId());
                if (playerData == null) return;
                if (!playerData.isFullJoined()) return;

                if (player.isOp() && !ops.contains(player.getUniqueId())) {
                    ops.add(player.getUniqueId());
                    getServer().getPluginManager().callEvent(new PlayerOpChangeEvent(player, true));
                } else if (!player.isOp() && ops.contains(player.getUniqueId())) {
                    ops.remove(player.getUniqueId());
                    getServer().getPluginManager().callEvent(new PlayerOpChangeEvent(player, false));
                }

                JsonChain playerDataChain = new JsonChain();
                playerDataChain.addProperty("name", player.getName());
                playerDataChain.addProperty("uuid", player.getUniqueId().toString());
                playerDataChain.addProperty("server", essentialsManagement.getServerName());
                try {
                    playerDataChain.addProperty("permissions", StringUtils.getStringFromList(playerData.getAllPermissions()));
                } catch (Exception ignored) {
                    playerDataChain.addProperty("permissions", "Empty");
                }
                playerDataChain.addProperty("rank", playerData.getHighestRank().getName());
                playerDataChain.addProperty("address", playerData.getAddress());
                playerDataChain.addProperty("lastSeen", playerData.getLastSeen());
                playerDataChain.addProperty("rankWeight", playerData.getHighestRank().getWeight());
                playerDataChain.addProperty("firstJoined", playerData.getFirstJoined());
                playerDataChain.addProperty("op", player.isOp());
                playerDataChain.addProperty("vanished", playerData.isVanished());
                playerDataChain.addProperty("lastActivity", System.currentTimeMillis());
                playerDataChain.addProperty("lastServer", essentialsManagement.getServerName());

                playerDataChain.addProperty("staffChatAlerts", playerData.isStaffChatAlerts());
                playerDataChain.addProperty("adminChatAlerts", playerData.isAdminChatAlerts());
                playerDataChain.addProperty("helpopAlerts", playerData.isHelpopAlerts());
                playerDataChain.addProperty("reportAlerts", playerData.isReportAlerts());

                redisData.write(JedisAction.PLAYER_DATA, playerDataChain.get());
            });
        }
    }

    private void loadLanguages() {
        if (this.language == null) {
            return;
        }
        Arrays.stream(Language.values()).forEach(language -> {
            if (this.language.getString(language.getPath(), true) == null) {
                if (language.getValue() != null) {
                    this.language.set(language.getPath(), language.getValue());
                } else if (language.getListValue() != null && this.language.getStringList(language.getPath(), true) == null) {
                    this.language.set(language.getPath(), language.getListValue());
                }
            }
        });
        this.language.save();
    }

    private void log(String message) {
        Bukkit.getConsoleSender().sendMessage(Color.translate(message));
    }

    public void reloadFiles() {
        this.coreConfig = new ConfigFile(this, "settings.yml");
        this.language = new ConfigFile(this, "messages.yml");
        this.loadLanguages();
        this.dataBase = new ConfigFile(this, "database.yml");
        this.ranks = new ConfigFile(this, "ranks.yml");
        this.tags = new ConfigFile(this, "tags.yml");
        this.staffModeFile = new ConfigFile(this, "staffmode.yml");
    }

    private class StaffAuthUpdate implements Runnable {

        @Override
        public void run() {
            if (!getCoreConfig().getBoolean("staff-auth.enabled", true)) return;

            for (Player online : Utilities.getOnlinePlayers()) {
                PlayerData playerData = playerManagement.getPlayerData(online.getUniqueId());

                if (playerData == null) continue;
                if (!online.hasPermission(coreConfig.getString("staff-auth.permission"))) continue;

                if (playerData.isStaffAuth()) {
                    if (!playerData.getAuthPassword().equalsIgnoreCase("")) {
                        coreConfig.getStringList("auth-message").forEach(online::sendMessage);
                    } else {
                        coreConfig.getStringList("auth-message-register").forEach(online::sendMessage);
                    }
                }
            }
        }
    }
}
