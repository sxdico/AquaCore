package me.activated.core.events;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.activated.core.api.player.GlobalPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when player joins the network, shouldn't be called on server switch if using bungee.
 */
@Getter
@RequiredArgsConstructor
public class GlobalPlayerCreateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final GlobalPlayer globalPlayer;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
