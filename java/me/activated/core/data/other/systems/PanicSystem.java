package me.activated.core.data.other.systems;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.activated.core.api.player.PlayerData;
import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.general.DateUtils;
import org.bson.Document;

import java.util.Calendar;
import java.util.Date;

@Getter
@RequiredArgsConstructor
public class PanicSystem {

    private final PlayerData playerData;
    private final AquaCore plugin;

    private long duration;
    private long commandCooldown;
    private long performedAt;
    private String server;

    public void panicPlayer() {
        String commandCooldownTime = plugin.getCoreConfig().getString("panic.command-cooldown");
        String panicTime = plugin.getCoreConfig().getString("panic.duration");

        try {
            duration = System.currentTimeMillis() - DateUtils.parseDateDiff(panicTime, false);
        } catch (Exception e) {
            try {
                duration = System.currentTimeMillis() - DateUtils.parseDateDiff("10m", false);
            } catch (Exception ignored) {
            }
        }

        try {
            commandCooldown = System.currentTimeMillis() - DateUtils.parseDateDiff(commandCooldownTime, false);
        } catch (Exception e) {
            try {
                commandCooldown = System.currentTimeMillis() - DateUtils.parseDateDiff("15m", false);
            } catch (Exception ignored) {
            }
        }
        this.performedAt = System.currentTimeMillis();
        if (plugin.getCoreConfig().getBoolean("panic.global-panic")) {
            this.server = "Global";
        } else {
            this.server = plugin.getEssentialsManagement().getServerName();
        }
    }

    public boolean isInPanic() {
        return System.currentTimeMillis() < this.performedAt + this.duration && (this.server.equalsIgnoreCase(plugin.getEssentialsManagement().getServerName())
                || this.server.equalsIgnoreCase("Global"));
    }

    public boolean isOnCommandCooldown() {
        return System.currentTimeMillis() < this.performedAt + this.commandCooldown && (this.server.equalsIgnoreCase(plugin.getEssentialsManagement().getServerName())
                || this.server.equalsIgnoreCase("Global"));
    }

    public void unPanicPlayer() {
        this.duration = 0L;
        this.commandCooldown = 0L;
    }

    public String getTimeExpiration() {
        if (System.currentTimeMillis() >= this.performedAt + this.duration) return "Now";

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();

        from.setTime(new Date(System.currentTimeMillis()));
        to.setTime(new Date(this.performedAt + this.duration));

        return DateUtils.formatDateDiff(from, to);
    }

    public String getCommandExpiration() {
        if (System.currentTimeMillis() >= this.performedAt + this.commandCooldown) return "Now";

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();

        from.setTime(new Date(System.currentTimeMillis()));
        to.setTime(new Date(this.performedAt + this.commandCooldown));

        return DateUtils.formatDateDiff(from, to);
    }

    public void load(Document document) {
        this.duration = document.getLong("panicDuration");
        this.commandCooldown = document.getLong("panicCommand");
        this.performedAt = document.getLong("panicPerformedAt");
        this.server = document.getString("panicServer");
    }

    public void save(Document document) {
        document.put("panicDuration", this.duration);
        document.put("panicCommand", this.commandCooldown);
        document.put("panicPerformedAt", this.performedAt);
        document.put("panicServer", this.server);
    }
}
