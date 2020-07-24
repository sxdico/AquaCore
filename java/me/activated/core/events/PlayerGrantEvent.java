package me.activated.core.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.activated.core.api.player.PlayerData;
import me.activated.core.api.rank.grant.Grant;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called whenever player or console tries to grant rank to a player
 */
@RequiredArgsConstructor
@Getter
public class PlayerGrantEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Grant grant;
    private final PlayerData targetData;
    private final CommandSender executor;

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
