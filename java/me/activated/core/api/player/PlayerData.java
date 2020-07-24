package me.activated.core.api.player;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.activated.core.api.rank.RankData;
import me.activated.core.api.rank.grant.Grant;
import me.activated.core.api.tags.Tag;
import me.activated.core.plugin.AquaCore;
import me.activated.core.punishments.utilities.punishments.PunishmentType;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.chat.ColorUtil;
import me.activated.core.utilities.general.Cooldown;
import me.activated.core.utilities.general.DateUtils;
import me.activated.core.utilities.general.StringUtils;
import me.activated.core.utilities.general.Tasks;
import me.activated.core.enums.RankType;
import me.activated.core.data.grant.GrantProcedure;
import me.activated.core.data.grant.GrantSerilization;
import me.activated.core.data.other.GlobalCooldowns;
import me.activated.core.data.other.OfflineInventory;
import me.activated.core.data.other.punishments.PunishHistory;
import me.activated.core.data.other.report.Report;
import me.activated.core.data.other.report.ReportSerilization;
import me.activated.core.data.other.systems.MessageSystem;
import me.activated.core.data.other.systems.PanicSystem;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@RequiredArgsConstructor
public class PlayerData {
    private AquaCore plugin = AquaCore.INSTANCE;

    private final UUID uniqueId;
    private final String playerName;

    private OfflineInventory offlineInventory = new OfflineInventory(plugin, this);
    private GlobalCooldowns globalCooldowns;
    private Cooldown chatCooldown;
    private GrantProcedure grantProcedure = null;
    private MessageSystem messageSystem = new MessageSystem();
    private PanicSystem panicSystem = new PanicSystem(this, plugin);
    private Location backLocation;

    private boolean fullJoined = false, godMode = false, staffChat = false, adminChat = false, frozen = false, guiFrozen = false;
    private boolean pexImport = false, vanished = false, inStaffMode = false, tipsAlerts = true;
    private boolean staffAuth = true;

    private boolean staffChatAlerts = true, adminChatAlerts = true, helpopAlerts = true, reportAlerts = true;

    private List<Grant> grants = new ArrayList<>();
    private List<String> permissions = new ArrayList<>();
    private List<Report> reports = new ArrayList<>();
    private List<PunishHistory> punishmentsExecuted = new ArrayList<>();
    private List<String> notes = new ArrayList<>();

    //private Map<String, String> permissions = new HashMap<>(); //TODO

    private String address = "", lastServer = "", tag = "", tagColor = "", worldTime = "DAY", authPassword = "";
    private ChatColor nameColor, chatColor;
    private boolean nameColorBold = false, nameColorItalic = false;

    private long lastSeen;

    private int coins = 0;

    private int points;

    private PermissionAttachment permissionAttachment;

    public boolean hasPermission(String permission) {
        return this.permissions.stream().filter(perm -> perm.equalsIgnoreCase(permission)).findFirst().orElse(null) != null;
    }

    public int getCoins() {
        return this.coins;
    }

    public void setCoins(int amount) {
        this.coins = amount;
    }

    public void addCoins(int amount) {
        this.coins = this.coins + amount;
    }

    public void removeCoins(int amount) {
        if (amount > this.coins) {
            this.coins = 0;
        } else {
            this.coins = this.coins - amount;
        }
    }

    public void loadData() {
        Document document = plugin.getMongoManager().getDocumentation().find(Filters.eq("uuid", this.uniqueId.toString())).first();

        if (document == null) {
            return;
        }

        this.grants = GrantSerilization.deserilizeGrants(document.getString("grants"));
        this.grants.removeIf(Objects::isNull);
        this.permissions = StringUtils.getListFromString(document.getString("permissions"));
        this.reports = ReportSerilization.deserilizeReports(document.getString("reports"));
        this.address = document.getString("address");
        this.lastSeen = document.getLong("lastSeen");
        this.lastServer = document.getString("lastServer");
        this.staffChat = document.getBoolean("staffChat");
        this.adminChat = document.getBoolean("adminChat");
        this.tag = document.getString("tag");
        this.tagColor = document.getString("tagColor");
        if (document.containsKey("nameColor")) {
            this.nameColor = ChatColor.valueOf(document.getString("nameColor"));
        }
        if (document.containsKey("chatColor")) {
            this.chatColor = ChatColor.valueOf(document.getString("chatColor"));
        }
        if (document.containsKey("tipsAlerts")) {
            this.tipsAlerts = document.getBoolean("tipsAlerts");
        }
        this.messageSystem.setMessagesToggled(document.getBoolean("msgToggled"));
        this.messageSystem.getIgnoreList().clear();
        this.messageSystem.getIgnoreList().addAll(StringUtils.getListFromString(document.getString("ignoreList")));
        this.messageSystem.setSoundsEnabled(document.getBoolean("sounds"));
        this.messageSystem.setGlobalChat(document.getBoolean("globalChat"));
        this.messageSystem.setChatMention(document.containsKey("chatMention") && document.getBoolean("chatMention"));

        this.worldTime = document.getString("worldTime");
        this.frozen = document.getBoolean("frozen");
        this.guiFrozen = document.containsKey("guiFrozen") && document.getBoolean("guiFrozen");

        this.panicSystem.load(document);
        this.nameColorBold = document.containsKey("nameColorBold") && document.getBoolean("nameColorBold");
        this.nameColorItalic = document.containsKey("nameColorItalic") && document.getBoolean("nameColorItalic");
        if (document.containsKey("coins")) {
            this.coins = document.getInteger("coins");
        } else {
            this.coins = plugin.getCoreConfig().getInt("coins.starting-coins");
        }
        if (document.containsKey("notes")) {
            this.notes = StringUtils.getListFromString(document.getString("notes"));
        }
        if (document.containsKey("staffAuth")) {
            this.staffAuth = document.getBoolean("staffAuth");
        }
        if (document.containsKey("authPass")) {
            this.authPassword = document.getString("authPass");
        }
        this.staffChatAlerts = document.containsKey("staffChatAlerts") ? document.getBoolean("staffChatAlerts") : true;
        this.adminChatAlerts = document.containsKey("adminChatAlerts") ? document.getBoolean("adminChatAlerts") : true;
        this.helpopAlerts = document.containsKey("helpopAlerts") ? document.getBoolean("helpopAlerts") : true;
        this.reportAlerts = document.containsKey("reportAlerts") ? document.getBoolean("reportAlerts") : true;

    }

    public void saveData(String key, Object value) {
        Tasks.runAsync(plugin, () -> {
            Document document = plugin.getMongoManager().getDocumentation().find(Filters.eq("uuid", this.uniqueId.toString())).first();

            if (document != null && document.containsKey(key)) {
                Document update = new Document();
                document.keySet().stream().filter(s -> !s.equalsIgnoreCase(key)).forEach(s -> update.put(s, document.get(s)));
                update.put(key, value);

                //Bukkit.getConsoleSender().sendMessage(Color.translate("&eUpdated &b" + this.playerName + "'s &edocumentation value &b'" + document.getString(key) + "' &eto new value&7. &7(&b" + value + "&7)."));

                plugin.getMongoManager().getDocumentation().replaceOne(Filters.eq("uuid", this.uniqueId.toString()), update, new UpdateOptions().upsert(true));
            }
        });
    }

    public void saveData() {
        Document document = new Document();

        document.put("uuid", this.uniqueId.toString());
        document.put("name", this.playerName);
        document.put("lowerCaseName", this.playerName.toLowerCase());
        document.put("grants", GrantSerilization.serilizeGrants(this.grants));
        document.put("permissions", StringUtils.getStringFromList(this.permissions));
        document.put("reports", ReportSerilization.serilizeReports(this.reports));
        document.put("address", this.address);
        document.put("lastSeen", this.lastSeen);
        document.put("staffChat", this.staffChat);
        document.put("adminChat", this.adminChat);
        document.put("lastServer", plugin.getEssentialsManagement().getServerName());
        document.put("tag", this.tag);
        document.put("tagColor", this.tagColor);
        if (this.nameColor != null) {
            document.put("nameColor", ColorUtil.convertChatColor(this.nameColor));
        }
        if (this.chatColor != null) {
            document.put("chatColor", ColorUtil.convertChatColor(this.chatColor));
        }
        document.put("worldTime", this.worldTime);

        document.put("sounds", this.messageSystem.isSoundsEnabled());
        document.put("msgToggled", this.messageSystem.isMessagesToggled());
        document.put("ignoreList", StringUtils.getStringFromList(new ArrayList<>(this.messageSystem.getIgnoreList())));
        document.put("globalChat", this.messageSystem.isGlobalChat());
        document.put("chatMention", this.messageSystem.isChatMention());
        document.put("tipsAlerts", this.tipsAlerts);

        document.put("frozen", this.frozen);
        document.put("guiFrozen", this.guiFrozen);
        document.put("nameColorBold", this.nameColorBold);
        document.put("nameColorItalic", this.nameColorItalic);
        document.put("notes", StringUtils.getStringFromList(this.notes));

        document.put("coins", this.coins);
        document.put("staffAuth", this.staffAuth);
        document.put("authPass", this.authPassword);

        document.put("staffChatAlerts", this.staffChatAlerts);
        document.put("adminChatAlerts", this.adminChatAlerts);
        document.put("helpopAlerts", this.helpopAlerts);
        document.put("reportAlerts", this.reportAlerts);

        this.panicSystem.save(document);

        plugin.getMongoManager().getDocumentation().replaceOne(Filters.eq("uuid", this.uniqueId.toString()), document, new UpdateOptions().upsert(true));
    }

    public boolean isCurrentOnline(String name) {
        return plugin.getServerManagement().getConnectedServers().stream().filter(serverData ->
                serverData.getNames().stream().map(String::toLowerCase).collect(Collectors.toList())
                        .contains(name.toLowerCase())).findFirst().orElse(null) != null;
    }

    public void loadAttachments(Player player) {
        List<String> bungeePermissions = new ArrayList<>();

        try {
            Set<PermissionAttachmentInfo> currentPermissions = new HashSet<>(player.getEffectivePermissions());
            for (PermissionAttachmentInfo permissionInfo : currentPermissions) {
                if (permissionInfo.getAttachment() == null) continue;

                Iterator<String> permissions = permissionInfo.getAttachment().getPermissions().keySet().iterator();

                while (permissions.hasNext()) {
                    String permission = permissions.next();
                    permissionInfo.getAttachment().unsetPermission(permission);
                }
            }
        } catch (Exception e) {
            //Bukkit.getConsoleSender().sendMessage(Color.translate("&cError while loading attachments (1). Please contact developer if you think this is and issue"));
        }
        PermissionAttachment attachment = player.addAttachment(plugin);

        if (attachment == null) return;

        attachment.getPermissions().keySet().forEach(attachment::unsetPermission);

        List<Grant> currentGrants = new ArrayList<>(this.grants);
        Iterator<Grant> grantIterator = currentGrants.iterator();
        while (grantIterator.hasNext()) {
            Grant grant = grantIterator.next();

            if (grant.hasExpired()) continue;

            RankData rankData = plugin.getRankManagement().getRank(grant.getRankName());

            if (rankData != null) {
                List<String> rankPermissions = new ArrayList<>(rankData.getPermissions());
                rankPermissions.forEach(permission -> {
                    attachment.setPermission(permission, true);
                });
                if (rankData.isBungee()) {
                    bungeePermissions.addAll(rankData.getBungeePermissions());
                }

                List<String> inheritances = new ArrayList<>(rankData.getInheritance());
                inheritances.forEach(inheritance -> {
                    RankData rankInheritance = plugin.getRankManagement().getRank(inheritance);

                    if (rankInheritance != null) {
                        List<String> inheritancePermissions = new ArrayList<>(rankInheritance.getPermissions());
                        inheritancePermissions.forEach(permission -> {
                            attachment.setPermission(permission, true);
                        });
                        if (rankInheritance.isBungee()) {
                            bungeePermissions.addAll(rankInheritance.getBungeePermissions());
                        }
                    }
                });
            }
        }

        RankData defaultRank = plugin.getRankManagement().getDefaultRank();
        if (defaultRank != null) {
            List<String> defaultPermissions = new ArrayList<>(defaultRank.getPermissions());

            defaultPermissions.forEach(permission -> {
                attachment.setPermission(permission, true);
            });
            if (defaultRank.isBungee()) {
                bungeePermissions.addAll(defaultRank.getBungeePermissions());
            }

            List<String> inheritances = new ArrayList<>(defaultRank.getInheritance());
            inheritances.forEach(inheritance -> {
                RankData rankInheritance = plugin.getRankManagement().getRank(inheritance);

                if (rankInheritance != null) {
                    List<String> inheritancePermissions = new ArrayList<>(rankInheritance.getPermissions());
                    inheritancePermissions.forEach(permission -> {
                        attachment.setPermission(permission, true);
                    });
                    if (rankInheritance.isBungee()) {
                        bungeePermissions.addAll(rankInheritance.getBungeePermissions());
                    }
                }
            });
        }
        List<String> playerPermissions = new ArrayList<>(this.permissions);
        playerPermissions.forEach(permission -> {
            attachment.setPermission(permission, true);
        });
        if (Utilities.isNameMCVerified(player.getUniqueId())) {
            if (plugin.getCoreConfig().getBoolean("name-mc.on-join.special-permissions-if-liked.enabled")) {
                plugin.getCoreConfig().getStringList("name-mc.on-join.special-permissions-if-liked.permissions").forEach(permission -> {
                    attachment.setPermission(permission, true);
                });
            }
        }
        if (!player.hasPermission("Aqua.blacklisted.permissions.bypass")) {
            plugin.getCoreConfig().getStringList("blacklisted-permissions").forEach(blacklisted -> {
                attachment.setPermission(blacklisted, false);
            });
        }
        player.recalculatePermissions();

        RankData rankData = this.getHighestRank();
        if (!player.getDisplayName().equals(Color.translate(rankData.getPrefix() + rankData.getColor().toString() + (this.nameColor != null ? this.nameColor.toString() : "") + this.getPlayerName() + rankData.getSuffix()) + ChatColor.RESET)) {
            player.setDisplayName(Color.translate(rankData.getPrefix() + rankData.getColor().toString() + (this.nameColor != null ? this.nameColor.toString() : "") + this.getPlayerName() + rankData.getSuffix()) + ChatColor.RESET);
        }

        bungeePermissions.forEach(permission -> {
            plugin.getRankManagement().sendPermissionToBungee(player, player.getName(), permission, true);
        });
    }

    public List<Grant> getActiveGrants() {
        return this.grants.stream().filter(grant -> !grant.hasExpired() && plugin.getRankManagement().getRank(grant.getRankName()) != null).collect(Collectors.toList());
    }

    public boolean hasRank(RankData rankData) {
        for (Grant grant : this.getActiveGrants()) {
            if (grant.getRankName().equalsIgnoreCase(rankData.getName())) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    public RankData getHighestRank() {
        RankData defaultRank = plugin.getRankManagement().getDefaultRank();
        if (defaultRank == null) {
            defaultRank = new RankData("Default");
            defaultRank.setDefaultRank(true);
        }
        return this.getActiveGrants().stream().map(Grant::getRank)
                .max(Comparator.comparingInt(RankData::getWeight)).orElse(defaultRank);
    }

    public String getFirstJoined() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(this.uniqueId);
        return offlinePlayer.getFirstPlayed() != 0 ? DateUtils.getDate(offlinePlayer.getFirstPlayed()) : "Never";
    }

    public String getLastSeenAgo() {
        if (Bukkit.getPlayer(this.uniqueId) != null) return "Now";

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();

        from.setTime(new Date(this.getLastSeen()));
        to.setTime(new Date(System.currentTimeMillis()));

        return DateUtils.formatDateDiff(from, to) + " ago";
    }

    public String getNameColor() {
        if (this.nameColor == null) {
            return this.getHighestRank().getDisplayColor();
        }
        if (this.isNameColorBold() && this.isNameColorItalic()) {
            return this.nameColor.toString() + ChatColor.BOLD.toString() + ChatColor.ITALIC.toString();
        }
        if (this.isNameColorBold()) {
            return this.nameColor.toString() + ChatColor.BOLD.toString();
        }
        if (this.isNameColorItalic()) {
            return this.nameColor.toString() + ChatColor.ITALIC.toString();
        }
        return this.nameColor.toString();
    }

    public String getNameWithColor() {
        return this.getNameColor() + this.playerName;
    }

    public Tag getTag() {
        return plugin.getTagManagement().getTag(this.tag);
    }

    public void loadPunishmentsPerformed() {
        this.punishmentsExecuted.clear();
        Stream.of(PunishmentType.values()).forEach(punishmentType -> {
            List<Document> punishments = this.getCollection(punishmentType).find(Filters.eq("addedBy", this.playerName)).into(new ArrayList<>());

            punishments.forEach(document -> {
                PunishHistory punishHistory = new PunishHistory(this.playerName, this.plugin, punishmentType);
                punishHistory.setAddedAt(document.getLong("addedAt"));
                punishHistory.setDuration(document.getLong("durationTime"));
                punishHistory.setPermanent(document.getBoolean("permanent"));
                punishHistory.setExecutor(document.getString("addedBy"));
                punishHistory.setTarget(document.getString("name"));
                punishHistory.setReason(document.getString("reason"));
                punishHistory.setActive(document.getBoolean("active"));
                punishHistory.setLast(document.getBoolean("last"));
                punishHistory.setSilent(document.getBoolean("silent"));
                punishHistory.setEnteredDuration(document.getString("enteredDuration"));
                punishmentsExecuted.add(punishHistory);
            });
        });
    }

    private MongoCollection<Document> getCollection(PunishmentType punishmentType) {
        if (punishmentType == PunishmentType.BAN) {
            return this.plugin.getMongoManager().getBans();
        } else if (punishmentType == PunishmentType.MUTE) {
            return this.plugin.getMongoManager().getMutes();
        } else if (punishmentType == PunishmentType.KICK) {
            return this.plugin.getMongoManager().getKicks();
        } else if (punishmentType == PunishmentType.BLACKLIST) {
            return this.plugin.getMongoManager().getBlacklists();
        }
        return this.plugin.getMongoManager().getWarns();
    }

    //This check is due to 3.0 Update and Changes
    public void checkForDefaultRankInGrants() {
        Tasks.runAsync(plugin, () -> {
            boolean removed = false;
            Iterator<Grant> grantIterator = this.grants.iterator();
            while (grantIterator.hasNext()) {
                Grant grant = grantIterator.next();
                RankData rankData = plugin.getRankManagement().getRank(grant.getRankName());

                if (rankData != null && rankData.isDefaultRank()) {
                    grantIterator.remove();
                    removed = true;
                }
            }
            if (removed) {
                this.saveData();
            }
        });
    }

    public List<RankData> getPurchasableRanks() {
        return plugin.getRankManagement().getRanks().stream().filter(RankData::isPurchasable)
                .filter(rankData -> rankData.getCoinsCost() <= this.coins).collect(Collectors.toList());
    }

    private RankData getHighestRankFromType(RankType type) {
        RankData defaultRank = plugin.getRankManagement().getDefaultRank();
        if (defaultRank == null) {
            defaultRank = new RankData("Default");
            defaultRank.setDefaultRank(true);
        }
        return this.getActiveGrants().stream().filter(grant -> grant.getRank().getRankType() == type).map(Grant::getRank)
                .max(Comparator.comparingInt(RankData::getWeight)).orElse(defaultRank);
    }

    public String getAllPrefixes() {
        StringBuilder builder = new StringBuilder();
        Stream.of(RankType.values()).forEach(rankType -> {
            RankData rankData = this.getHighestRankFromType(rankType);

            if (rankData.isDefaultRank() && rankType != RankType.DEFAULT) return;

            builder.append(Color.translate(rankData.getPrefix()));
        });
        return builder.toString();
    }

    public List<String> getAllPermissions() {
        List<String> permissions = new ArrayList<>();

        List<Grant> currentGrants = new ArrayList<>(this.grants);
        Iterator<Grant> grantIterator = currentGrants.iterator();
        while (grantIterator.hasNext()) {
            Grant grant = grantIterator.next();

            if (grant.hasExpired()) continue;

            RankData rankData = plugin.getRankManagement().getRank(grant.getRankName());

            if (rankData != null) {
                permissions.addAll(rankData.getPermissions());

                List<String> inheritances = new ArrayList<>(rankData.getInheritance());
                inheritances.forEach(inheritance -> {
                    RankData rankInheritance = plugin.getRankManagement().getRank(inheritance);

                    if (rankInheritance != null) {
                        permissions.addAll(rankInheritance.getPermissions());
                    }
                });
            }
        }

        RankData defaultRank = plugin.getRankManagement().getDefaultRank();
        if (defaultRank != null) {
            permissions.addAll(defaultRank.getPermissions());

            List<String> inheritances = new ArrayList<>(defaultRank.getInheritance());
            inheritances.forEach(inheritance -> {
                RankData rankInheritance = plugin.getRankManagement().getRank(inheritance);

                if (rankInheritance != null) {
                    permissions.addAll(rankInheritance.getPermissions());
                }
            });
        }

        permissions.addAll(this.getPermissions());

        return permissions;
    }

    /*public void loadMySQL() {
        try {
            PreparedStatement statement = plugin.getSQLManager().getConnection().prepareStatement("SELECT * FROM docs WHERE uuid=?");
            statement.setString(1, this.uniqueId.toString());

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                this.points = result.getInt("points");

                Bukkit.broadcastMessage(ChatColor.GREEN + "MySQL data has been loaded.");

                plugin.getSQLManager().close(result, statement);
            } else {
                PreparedStatement preparedStatement = plugin.getSQLManager().getConnection().prepareStatement(
                        "INSERT INTO docs(uuid, points) VALUES (?,?)");
                preparedStatement.setString(1, this.uniqueId.toString());
                preparedStatement.setInt(2, this.points);

                Bukkit.broadcastMessage(ChatColor.GREEN + "MySQL data has been created.");


                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

    /*public void saveMySQL() {
        if (!plugin.getSQLManager().isCreated("docs", "uuid", this.uniqueId.toString())) return;

        try {
            PreparedStatement statement = plugin.getSQLManager().getConnection().prepareStatement("UPDATE docs SET 'points'=? WHERE uuid=?");
            statement.setInt(1, this.points);
            statement.setString(2, this.uniqueId.toString());

            Bukkit.broadcastMessage(ChatColor.GREEN + "MySQL data has been updated.");

            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/
}
