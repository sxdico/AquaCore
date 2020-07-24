package me.activated.core.punishments.player;

import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.activated.core.plugin.AquaCore;
import me.activated.core.punishments.utilities.punishments.Punishment;
import me.activated.core.punishments.utilities.punishments.PunishmentType;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class PunishData {
    private final AquaCore plugin = AquaCore.INSTANCE;

    private final PunishPlayerData playerData;

    private final Set<Punishment> punishments = new HashSet<>();

    public boolean isBanned() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getPunishmentType() == PunishmentType.BAN).findFirst().orElse(null) != null;
    }

    public boolean isIPBanned() {
        return this.punishments.stream().filter(punishment -> punishment.isIPRelative() && !punishment.hasExpired() && punishment.getPunishmentType() == PunishmentType.BAN).findFirst().orElse(null) != null;
    }

    public boolean isBlacklisted() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getPunishmentType() == PunishmentType.BLACKLIST).findFirst().orElse(null) != null;
    }

    public boolean isWarned() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getPunishmentType() == PunishmentType.WARN).findFirst().orElse(null) != null;
    }

    public boolean isMuted() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getPunishmentType() == PunishmentType.MUTE).findFirst().orElse(null) != null;
    }


    public Punishment getActiveBan() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getPunishmentType() == PunishmentType.BAN).findFirst().orElse(null);
    }

    public Punishment getActiveMute() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getPunishmentType() == PunishmentType.MUTE).findFirst().orElse(null);
    }

    public Punishment getActiveBlacklist() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getPunishmentType() == PunishmentType.BLACKLIST).findFirst().orElse(null);
    }

    public List<Punishment> getPunishments(PunishmentType type) {
        return this.punishments.stream().filter(punishment -> punishment.getPunishmentType() == type).collect(Collectors.toList());
    }

    public void load() {
        playerData.setLoading(true);
        this.punishments.clear();

        List<Document> bans = plugin.getMongoManager().getBans().find().filter(Filters.eq("uuid", this.playerData.getUniqueId().toString())).into(new ArrayList<>());
        bans.forEach(saved -> {
            Punishment punishment = new Punishment(plugin, this.playerData, PunishmentType.BAN);
            punishment.load(saved);

            this.punishments.add(punishment);
        });

        List<Document> mutes = plugin.getMongoManager().getMutes().find().filter(Filters.eq("uuid", this.playerData.getUniqueId().toString())).into(new ArrayList<>());
        mutes.forEach(saved -> {
            Punishment punishment = new Punishment(plugin, this.playerData, PunishmentType.MUTE);
            punishment.load(saved);

            this.punishments.add(punishment);
        });

        List<Document> warns = plugin.getMongoManager().getWarns().find().filter(Filters.eq("uuid", this.playerData.getUniqueId().toString())).into(new ArrayList<>());
        warns.forEach(saved -> {
            Punishment punishment = new Punishment(plugin, this.playerData, PunishmentType.WARN);
            punishment.load(saved);

            this.punishments.add(punishment);
        });

        List<Document> blacklists = plugin.getMongoManager().getBlacklists().find().filter(Filters.eq("uuid", this.playerData.getUniqueId().toString())).into(new ArrayList<>());
        blacklists.forEach(saved -> {
            Punishment punishment = new Punishment(plugin, this.playerData, PunishmentType.BLACKLIST);
            punishment.load(saved);

            this.punishments.add(punishment);
        });

        List<Document> kicks = plugin.getMongoManager().getKicks().find().filter(Filters.eq("uuid", this.playerData.getUniqueId().toString())).into(new ArrayList<>());
        kicks.forEach(saved -> {
            Punishment punishment = new Punishment(plugin, this.playerData, PunishmentType.KICK);
            punishment.load(saved);

            this.punishments.add(punishment);
        });
        playerData.setLoading(false);
    }

    public void forceLoadBans(UUID uuid) {
        this.punishments.removeIf(punishment -> punishment.getPunishmentType() == PunishmentType.BAN);

        List<Document> bans = plugin.getMongoManager().getBans().find().filter(Filters.eq("uuid", uuid.toString())).into(new ArrayList<>());
        bans.forEach(saved -> {
            Punishment punishment = new Punishment(plugin, null, PunishmentType.BAN);
            punishment.load(saved);

            this.punishments.add(punishment);
        });
    }

    public void forceLoadBlacklists(UUID uuid) {
        this.punishments.removeIf(punishment -> punishment.getPunishmentType() == PunishmentType.BLACKLIST);

        List<Document> blacklists = plugin.getMongoManager().getBlacklists().find().filter(Filters.eq("uuid", uuid.toString())).into(new ArrayList<>());
        blacklists.forEach(saved -> {
            Punishment punishment = new Punishment(plugin, this.playerData, PunishmentType.BLACKLIST);
            punishment.load(saved);

            this.punishments.add(punishment);
        });
    }
}
