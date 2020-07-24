package me.activated.core.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called whenever a staf player execute vanish command
 */
@Getter
@RequiredArgsConstructor
public class PlayerOpChangeEvent extends Event{
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final boolean isOped;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
