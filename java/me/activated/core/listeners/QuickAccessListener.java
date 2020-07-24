package me.activated.core.listeners;

import me.activated.core.plugin.AquaCore;
import me.activated.core.api.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class QuickAccessListener implements Listener {
    private final AquaCore plugin = AquaCore.INSTANCE;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleAdminChatQuickAccess(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("Aqua.adminchat")) return;

        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        String message = event.getMessage();

        if (playerData.isAdminChat()) return;

        String prefix = plugin.getCoreConfig().getString("quick-access-prefixes.admin-chat");

        if (message.startsWith(prefix) && !message.equalsIgnoreCase(prefix)) {
            message = message.substring(1);
        } else {
            return;
        }

        if (!playerData.isStaffChat() || playerData.isAdminChat() && playerData.isStaffChat()) {
            event.setCancelled(true);
            plugin.getPlayerManagement().sendAdminChatMessage(playerData, message);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleStaffChatQuickAccess(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("Aqua.staffchat")) return;

        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        String message = event.getMessage();

        if (playerData.isStaffChat()) return;

        String prefix = plugin.getCoreConfig().getString("quick-access-prefixes.staff-chat");

        if (message.startsWith(prefix) && !message.equalsIgnoreCase(prefix)) {
            message = message.substring(1);
        } else {
            return;
        }

        if (!playerData.isAdminChat()) {
            event.setCancelled(true);
            plugin.getPlayerManagement().sendStaffChatMessage(playerData, message);
        }
    }
}
