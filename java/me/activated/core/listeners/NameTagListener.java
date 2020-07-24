package me.activated.core.listeners;

import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NameTagListener implements Listener {
    private final AquaCore plugin = AquaCore.INSTANCE;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleJoin(PlayerJoinEvent event) {
        if (!plugin.getCoreConfig().getBoolean("use-nametags")) return;
        Player player = event.getPlayer();

        Tasks.runLater(plugin, () -> plugin.getNameTagManagement().createScoreboard(player), 60L);
    }
}
