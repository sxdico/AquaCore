package me.activated.core.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.activated.core.api.player.GlobalPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called whenever global player tries to report someone
 */
@Getter
@RequiredArgsConstructor
public class PlayerReportEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final GlobalPlayer hacker;
    private final String reason;

    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
