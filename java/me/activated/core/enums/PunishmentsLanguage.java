package me.activated.core.enums;

import lombok.Getter;
import lombok.Setter;
import me.activated.core.punishments.utilities.PunishmentsConfigFile;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.chat.Replacement;

public enum PunishmentsLanguage {

    PREFIX("PREFIX", "&7[&bPunishments&7] "),
    HAVENT_PLAYED_BEFORE("HAVENT-PLAYED-BEFORE", "{prefix} &cWe couldn't find that player in our data base. It seems that the player never joined any of our data base servers."),
    NO_PERMISSION("NO_PERMISSION", "{prefix} &cYou don't have permission to use that command."),
    NO_CONSOLE("NO_CONSOLE", "{prefix} &cFor player usage only."),
    WRONG_DATE_FORMAT("WRONG_DATE_FORMAT", "{prefix} &bYou have entered wrong date format. &3Example &7(&b1d&7, &b1h&7, &b1m&7)&b."),

    BAN_ALREADY_BANNED("BAN.ALREADY-BANNED", "{prefix} &bUser by name &7'&3<user>&7' &bis already banned."),
    BAN_NOT_BANNED("BAN.NOT-BANNED", "{prefix} &bUser by name &7'&3<user>&7' &bis not currently banned."),

    BLACKLIST_ALREADY_BLACKLISTED("BLACKLIST.ALREADY-BLACKLISTED", "{prefix} &bUuser by name &7'&3<user>&7' &bis already blacklisted."),
    BLACKLIST_NOT_BLACKLISTED("BLACKLIST.NOT-BLACKLISTED", "{prefix} &bUser by name &7'&3<user>&7' &bis not currently blacklisted."),

    MUTE_ALREADY_MUTED("MUTE.ALREADY-MUTED", "{prefix} &bUser by name &7'&3<user>&7' &bis already muted."),
    MUTE_NOT_MUTED("MUTE.NOT-MUTED", "{prefix} &bUser by name &7'&3<user>&7' &bis not currently muted."),

    MUTE_BEEN_PERM_MUTED("MUTE.BEEN-MUTED-PERM", "{prefix} &bYou have been &epermanently muted &bby &3<sender> &bfor &3<reason>&b. &bTo h your status for this mute do &3/punishinfo&b."),
    MUTE_BEEN_TEMP_MUTED("MUTE.BEEN-MUTED-TEMP", "{prefix} &bYou have been &etemporarily muted &bby &3<sender> &bfor &3<reason> &bfor &3<duration>&b. &bTo h your status for this mute do &3/punishinfo&b."),

    MUTE_CANT_TALK_TEMP("MUTE.CANT-TALK-TEMP", "{prefix} &bYou are currently muted for another &e<duration>&b. &bTo check your status for this mute do &3/punishinfo&b."),
    MUTE_CANT_TALK_PERM("MUTE.CANT-TALK-PERM", "{prefix} &bYou are muted forever. &bTo check your status for this mute do &3/punishinfo&b."),

    WARN_BEEN_PERM_WARNED("WARN.BEEN-WARNED-PERM", "{prefix} &bYou have been &epermanently warned &bby &3<sender> &bfor &3<reason>&b. &bTo check your status on warns do &3/punishinfo&b."),
    WARN_BEEN_TEMP_WARNED("WARN.BEEN-WARNED-TEMP", "{prefix} &bYou have been &etemporarily warned &bby &3<sender> &bfor &3<reason> &bfor &3<duration>&b. &bTo check your status on warns do &3/punishinfo&b."),

    JOIN_BANNED("JOIN-BANNED", "{prefix} &b<player> &3tried to join but is &bBANNED&b. &7(&b<expire>&7)"),
    JOIN_BLACKLISTED("JOIN-BLACKLISTED", "{prefix} &4<player> &ctried to join but is &4BLACKLISTED&c. &7(&c<expire>&7)"),

    END("", "");

    @Getter private final String path;
    @Getter private final String value;
    @Setter private static PunishmentsConfigFile config;

    PunishmentsLanguage(String path, String value) {
        this.path = path;
        this.value = value;
    }

    public String toString() {
        Replacement replacement = new Replacement(Color.translate(config.getString(this.path)));
        replacement.add("{prefix} ", config.getString("PREFIX"));
        return replacement.toString();
    }
}
