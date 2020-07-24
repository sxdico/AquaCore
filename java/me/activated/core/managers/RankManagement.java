package me.activated.core.managers;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import me.activated.core.database.redis.other.bson.JsonChain;
import me.activated.core.database.redis.payload.action.JedisAction;
import me.activated.core.events.PlayerGrantEvent;
import me.activated.core.plugin.AquaCore;
import me.activated.core.enums.RankType;
import me.activated.core.api.player.GlobalPlayer;
import me.activated.core.api.player.PlayerData;
import me.activated.core.api.rank.RankData;
import me.activated.core.api.rank.grant.Grant;
import me.activated.core.data.grant.GrantSerilization;
import me.activated.core.enums.Language;
import me.activated.core.utilities.Manager;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.chat.ColorUtil;
import me.activated.core.utilities.file.ConfigFile;
import me.activated.core.utilities.general.StringUtils;
import me.activated.core.utilities.general.Tasks;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class RankManagement extends Manager {
    private Set<RankData> ranks = new HashSet<>();

    public RankManagement(AquaCore plugin) {
        super(plugin);

        Tasks.runAsync(plugin, () -> {
            this.importRanks();
            this.loadRanks();
            this.saveRanks();
        });
    }

    public RankData getRank(String name) {
        return this.ranks.stream().filter(rankData -> rankData.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public RankData getDefaultRank() {
        List<RankData> ranks = new ArrayList<>(this.ranks);
        List<RankData> defaults = ranks.stream().sorted(Comparator.comparingInt(RankData::getWeight).reversed()).filter(RankData::isDefaultRank).collect(Collectors.toList());
        if (defaults.size() == 0) {
            return null;
        }
        return defaults.get(0);
    }

    public void deleteRank(RankData rankData) {
        Document document = plugin.getMongoManager().getRanks().find(Filters.eq("name", rankData.getName())).first();
        if (document != null) {
            plugin.getMongoManager().getRanks().deleteOne(document);
        }
        this.ranks.remove(rankData);
    }

    public void requestRankUpdate() {
        plugin.getRedisData().write(JedisAction.RANKS_UPDATE, new JsonChain().get());
    }

    public void requestPermissionsUpdate() {
        plugin.getRedisData().write(JedisAction.PERMISSIONS_UPDATE, new JsonChain().get());
    }

    public void sendPermissionToBungee(Player player, String name, String permission, boolean set) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("AquaChannel");
            out.writeUTF(name);
            out.writeUTF(permission);
            out.writeUTF(String.valueOf(set));
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(Color.translate("&cFailed to send permission to bungee for &a'" +
                    player.getName() + "' &ccontact plugin developer if you think this is an issue."));
        }
        player.sendPluginMessage(plugin, "AquaPermissions", b.toByteArray());
    }

    public void saveRanks() {
        ranks.forEach(rankData -> {
            Document document = new Document();
            document.put("name", rankData.getName());
            document.put("permissions", StringUtils.getStringFromList(rankData.getPermissions()));
            document.put("bungeePermissions", StringUtils.getStringFromList(rankData.getBungeePermissions()));
            document.put("prefix", rankData.getPrefix().replace("§", "&"));
            document.put("suffix", rankData.getSuffix().replace("§", "&"));
            document.put("color", ColorUtil.convertChatColor(rankData.getColor()));
            document.put("default", rankData.isDefaultRank());
            document.put("inheritance", StringUtils.getStringFromList(rankData.getInheritance()));
            document.put("weight", rankData.getWeight());
            document.put("italic", rankData.isItalic());
            document.put("bold", rankData.isBold());
            document.put("coinsCost", rankData.getCoinsCost());
            document.put("purchasable", rankData.isPurchasable());
            document.put("chatColor", ColorUtil.convertChatColor(rankData.getChatColor()));
            document.put("bungee", rankData.isBungee());
            document.put("type", rankData.getRankType().toString());

            plugin.getMongoManager().getRanks().replaceOne(Filters.eq("name", rankData.getName()), document, new UpdateOptions().upsert(true));
        });
    }

    public void loadRanks() {
        this.ranks.clear();
        plugin.getMongoManager().getRanks().find().into(new ArrayList<>()).forEach(document -> {
            RankData rankData = new RankData(document.getString("name"));
            rankData.setPermissions(StringUtils.getListFromString(document.getString("permissions")));
            if (document.containsKey("bungeePermissions")) {
                rankData.setBungeePermissions(StringUtils.getListFromString(document.getString("bungeePermissions")));
            }
            rankData.setPrefix(document.getString("prefix"));
            rankData.setSuffix(document.getString("suffix"));
            rankData.setDefaultRank(document.getBoolean("default"));
            rankData.setInheritance(StringUtils.getListFromString(document.getString("inheritance")));
            rankData.setWeight(document.getInteger("weight"));
            rankData.setBold(document.containsKey("bold") && document.getBoolean("bold"));
            rankData.setItalic(document.containsKey("italic") && document.getBoolean("italic"));
            rankData.setPurchasable(document.containsKey("purchasable") && document.getBoolean("purchasable"));
            if (document.containsKey("type")) {
                rankData.setRankType(RankType.valueOf(document.getString("type")));
            } else {
                rankData.setRankType(RankType.DEFAULT);
            }
            if (document.containsKey("coinsCost")) {
                rankData.setCoinsCost(document.getInteger("coinsCost"));
            }
            rankData.setBungee(document.containsKey("bungee") && document.getBoolean("bungee"));
            if (document.containsKey("chatColor")) {
                ChatColor chatColor;
                try {
                    chatColor = ChatColor.valueOf(document.getString("chatColor"));
                } catch (Exception ignored) {
                    chatColor = ChatColor.WHITE;
                }
                rankData.setChatColor(chatColor);
            }

            ChatColor color;
            try {
                color = ChatColor.valueOf(document.getString("color"));
            } catch (Exception e) {
                color = ChatColor.GRAY;
            }
            rankData.setColor(color);

            this.ranks.add(rankData);
        });
        if (plugin.getMongoManager().getRanks().find().into(new ArrayList<>()).size() == 0) {
            this.importRanks();
        }
        RankData defaultRank = this.getDefaultRank();
        if (defaultRank == null) {
            this.createDefaultRank();
        }
    }

    public void createDefaultRank() {
        RankData defaultRank = this.getDefaultRank();
        if (defaultRank == null) {
            RankData rankData = new RankData("Default");
            rankData.setPrefix("&a");
            rankData.setSuffix("");
            rankData.setDefaultRank(true);
            rankData.setColor(ChatColor.GREEN);
            rankData.setWeight(1);
            rankData.setRankType(RankType.DEFAULT);

            this.ranks.add(rankData);
            this.saveRanks();
            this.saveRanksToConfig();
        }
    }

    public void importRanks() {
        this.ranks.clear();
        plugin.getRanks().getKeys(false).forEach(key -> {
            RankData rankData = new RankData(plugin.getRanks().getString(key + ".name"));
            rankData.setPermissions(plugin.getRanks().getStringList(key + ".permissions"));
            rankData.setBungeePermissions(plugin.getRanks().getStringList(key + ".bungee-permissions"));
            rankData.setPrefix(plugin.getRanks().getString(key + ".prefix"));
            rankData.setSuffix(plugin.getRanks().getString(key + ".suffix"));
            rankData.setDefaultRank(plugin.getRanks().getBoolean(key + ".default-rank"));
            rankData.setInheritance(plugin.getRanks().getStringList(key + ".inheritance"));
            rankData.setWeight(plugin.getRanks().getInt(key + ".weight"));
            rankData.setBold(plugin.getRanks().getBoolean(key + ".bold"));
            rankData.setItalic(plugin.getRanks().getBoolean(key + ".italic"));
            rankData.setPurchasable(plugin.getRanks().getBoolean(key + ".purchasable"));
            rankData.setCoinsCost(plugin.getRanks().getInt(key + ".coinsCost"));
            rankData.setBungee(plugin.getRanks().getBoolean(key + ".bungee"));
            rankData.setRankType(RankType.valueOf(plugin.getRanks().getString(key + ".type", "DEFAULT")));

            ChatColor color;
            try {
                color = ChatColor.valueOf(plugin.getRanks().getString(key + ".rank-color"));
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(Color.translate(Language.PREFIX.toString() + "&cFailed to set &e"
                        + plugin.getRanks().getString(key + ".rank-color") + " &cas color, using gray for now."));
                color = ChatColor.GRAY;
            }
            rankData.setColor(color);

            ChatColor chatColor;
            try {
                chatColor = ChatColor.valueOf(plugin.getRanks().getString(key + ".chat-color"));
            } catch (Exception ignored) {
                chatColor = ChatColor.WHITE;
            }
            rankData.setChatColor(chatColor);

            this.ranks.add(rankData);
        });
    }

    public void saveRanksToConfig() {
        ConfigFile file = plugin.getRanks();
        file.getKeys(false).forEach(key -> {
            file.set(key, null);
        });
        this.ranks.stream().sorted(Comparator.comparingInt(RankData::getWeight).reversed()).forEach(rankData -> {
            file.set(rankData.getName() + ".name", rankData.getName());
            file.set(rankData.getName() + ".prefix", rankData.getPrefix().replace("§", "&"));
            file.set(rankData.getName() + ".suffix", rankData.getSuffix().replace("§", "&"));
            file.set(rankData.getName() + ".default-rank", rankData.isDefaultRank());
            file.set(rankData.getName() + ".rank-color", ColorUtil.convertChatColor(rankData.getColor()));
            file.set(rankData.getName() + ".permissions", rankData.getPermissions());
            file.set(rankData.getName() + ".inheritance", rankData.getInheritance());
            file.set(rankData.getName() + ".weight", rankData.getWeight());
            file.set(rankData.getName() + ".bold", rankData.isBold());
            file.set(rankData.getName() + ".italic", rankData.isItalic());
            file.set(rankData.getName() + ".purchasable", rankData.isPurchasable());
            file.set(rankData.getName() + ".coinsCost", rankData.getCoinsCost());
            file.set(rankData.getName() + ".chat-color", ColorUtil.convertChatColor(rankData.getChatColor()));
            file.set(rankData.getName() + ".bungee", rankData.isBungee());
            file.set(rankData.getName() + ".bungee-permissions", rankData.getBungeePermissions());
            file.set(rankData.getName() + ".type", rankData.getRankType().toString());
        });
        file.save();
    }

    public void giveRank(CommandSender sender, PlayerData targetData, long duration, boolean permanent, String reason, RankData rankData, String server) {
        Grant grant = new Grant();
        grant.setRankName(rankData.getName());
        grant.setActive(true);
        grant.setAddedAt(System.currentTimeMillis());
        grant.setAddedBy(sender.getName());
        grant.setDuration(duration);
        grant.setPermanent(permanent);
        grant.setReason(reason);
        grant.setServer(server);

        PlayerGrantEvent event = new PlayerGrantEvent(grant, targetData, sender);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        Tasks.runAsync(plugin, () -> {
            targetData.getGrants().add(grant);

            GlobalPlayer globalPlayer = plugin.getServerManagement().getGlobalPlayer(targetData.getPlayerName());
            if (grant.isPermanent()) {
                sender.sendMessage(Language.GRANT_RANK_GRANTED_PERM.toString()
                        .replace("<rank>", grant.getRankName())
                        .replace("<user>", targetData.getPlayerName()));
                if (globalPlayer != null) {
                    globalPlayer.sendMessage(Language.GRANT_RANK_GRANTED_PERM_TARGET.toString()
                            .replace("<rank>", grant.getRankName())
                            .replace("<user>", targetData.getPlayerName()));
                }
            } else {
                sender.sendMessage(Language.GRANT_RANK_GRANTED_TEMP.toString()
                        .replace("<rank>", grant.getRankName())
                        .replace("<time>", grant.getNiceDuration())
                        .replace("<user>", targetData.getPlayerName()));
                if (globalPlayer != null) {
                    globalPlayer.sendMessage(Language.GRANT_RANK_GRANTED_TEMP_TARGET.toString()
                            .replace("<rank>", grant.getRankName())
                            .replace("<time>", grant.getNiceDuration())
                            .replace("<user>", targetData.getPlayerName()));
                }
            }
            targetData.saveData();

            Player target = Bukkit.getPlayer(targetData.getPlayerName());
            if (target == null) {
                plugin.getRedisData().write(JedisAction.GRANTS_UPDATE,
                        new JsonChain().addProperty("name", targetData.getPlayerName())
                                .addProperty("grants", GrantSerilization.serilizeGrants(targetData.getGrants())).get());
            } else {
                PlayerData playerData = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());
                playerData.loadAttachments(target);
            }

            plugin.getPlayerManagement().deleteData(targetData.getUniqueId());
        });
    }

    public boolean canGrant(PlayerData granter, RankData rankData) {
        RankData granterRank = granter.getHighestRank();
        return granterRank.getWeight() > rankData.getWeight();
    }
}
