package me.activated.core.punishments.utilities.punishments;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.activated.core.database.redis.other.bson.JsonChain;
import me.activated.core.database.redis.payload.action.JedisAction;
import me.activated.core.plugin.AquaCore;
import me.activated.core.punishments.player.PunishPlayerData;
import me.activated.core.utilities.general.DateUtils;
import me.activated.core.utilities.general.StringUtils;
import me.activated.core.api.player.PlayerData;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
@Setter
public class Punishment {
    private final AquaCore plugin;
    private final PunishPlayerData playerData;
    private final PunishmentType punishmentType;

    private boolean active = true, permanent = true, silent = false, removedSilent = false, last = false, IPRelative = false;
    private long addedAt = -5L;
    private long durationTime = -5L, whenRemoved;
    private String reason = "", removedBy = "", enteredDuration = "", removedFor = "", addedBy = "", name = "";

    public void save(boolean replace) {
        Document document = new Document();
        document.put("uuid", playerData.getUniqueId().toString());
        document.put("name", playerData.getPlayerName());
        document.put("active", this.active);
        document.put("permanent", this.permanent);
        document.put("durationTime", this.durationTime);
        document.put("addedAt", this.addedAt);
        document.put("reason", this.reason);
        document.put("silent", this.silent);
        document.put("enteredDuration", enteredDuration);
        document.put("addedBy", this.addedBy);
        document.put("removedBy", this.removedBy);
        document.put("removedFor", this.removedFor);
        document.put("removedSilent", this.removedSilent);
        document.put("whenRemoved", this.whenRemoved);
        document.put("last", this.last);
        document.put("IPRelative", this.IPRelative);
        document.put("BannedIP", this.playerData.getAddress());

        if (replace) {
            this.getCollection().replaceOne(Filters.and(Filters.eq("uuid", this.playerData.getUniqueId().toString()), Filters.eq("last", true)), document, new UpdateOptions().upsert(true));
        } else {
            this.getCollection().insertOne(document);
        }
    }

    public void save() {
        this.save(false);
    }

    public void load(Document document) {
        this.active = document.getBoolean("active");
        this.permanent = document.getBoolean("permanent");
        this.durationTime = document.getLong("durationTime");
        this.addedAt = document.getLong("addedAt");
        this.reason = document.getString("reason");
        this.silent = document.getBoolean("silent");
        this.removedBy = document.getString("removedBy");
        this.removedFor = document.getString("removedFor");
        this.removedSilent = document.getBoolean("removedSilent");
        this.whenRemoved = document.getLong("whenRemoved");
        this.enteredDuration = document.getString("enteredDuration");
        this.addedBy = document.getString("addedBy");
        this.last = document.getBoolean("last");
        this.IPRelative = document.getBoolean("IPRelative");
        this.name = document.getString("name");
    }

    public String getNiceDuration() {
        if (this.permanent) return "Permanent";
        if (this.durationTime == -5L) return "";

        return DurationFormatUtils.formatDurationWords(DateUtils.handleParseTime(this.enteredDuration), true, true);
    }

    public String getNiceExpire() {
        if (this.permanent) return "Never";
        if (hasExpired()) return "Expired";
        if (this.durationTime == -5L) return "";

        return DateUtils.formatDateDiff(this.getDurationTime());
    }

    public boolean hasExpired() {
        if (!isActive()) return true;
        if (isPermanent()) return false;
        if (!isLast()) return true;

        return System.currentTimeMillis() >= durationTime;
    }

    private MongoCollection<Document> getCollection() {
        if (this.punishmentType == PunishmentType.BAN) {
            return this.plugin.getMongoManager().getBans();
        } else if (this.punishmentType == PunishmentType.MUTE) {
            return this.plugin.getMongoManager().getMutes();
        } else if (this.punishmentType == PunishmentType.KICK) {
            return this.plugin.getMongoManager().getKicks();
        } else if (this.punishmentType == PunishmentType.BLACKLIST) {
            return this.plugin.getMongoManager().getBlacklists();
        }
        return this.plugin.getMongoManager().getWarns();
    }

    public void execute(CommandSender sender) {
        JsonChain jsonChain = new JsonChain();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            jsonChain.addProperty("sender", player.getDisplayName());

            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            jsonChain.addProperty("coloredName", playerData.getHighestRank().getColor() + playerData.getPlayerName());
        } else {
            jsonChain.addProperty("sender", sender.getName());
        }
        jsonChain.addProperty("senderName", sender.getName());
        jsonChain.addProperty("name", this.getPlayerData().getPlayerName());
        jsonChain.addProperty("reason", this.getReason());
        jsonChain.addProperty("duration", this.getDurationTime());
        jsonChain.addProperty("niceDuration", this.getNiceDuration());
        jsonChain.addProperty("permanent", this.isPermanent());
        jsonChain.addProperty("uuid", this.getPlayerData().getUniqueId().toString());
        jsonChain.addProperty("silent", this.isSilent());
        jsonChain.addProperty("server", plugin.getPunishmentPlugin().getConfigFile().getString("SERVER-NAME"));
        jsonChain.addProperty("type", this.punishmentType.toString());
        jsonChain.addProperty("IPRelative", this.IPRelative);
        List<Punishment> warns = playerData.getPunishData().getPunishments().stream().filter(punishment -> !punishment.hasExpired() && punishment.getPunishmentType() == PunishmentType.WARN).collect(Collectors.toList());
        jsonChain.addProperty("warns", warns.size());
        jsonChain.addProperty("alts", StringUtils.getStringFromList(this.playerData.getAlts().stream().map(Alt::getName).collect(Collectors.toList())));

        this.getPlugin().getRedisData().write(JedisAction.EXECUTE_PUNISHMENT, jsonChain.get());
    }
}
