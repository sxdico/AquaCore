package me.activated.core.tasks;

import me.activated.core.api.player.PlayerData;
import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FreezeTask implements Runnable {
    private final AquaCore plugin = AquaCore.INSTANCE;

    @Override
    public void run() {
        Utilities.getOnlinePlayers().stream()
                .map(Player::getUniqueId).map(plugin.getPlayerManagement()::getPlayerData)
                .filter(PlayerData::isFrozen).map(PlayerData::getUniqueId)
                .map(Bukkit::getPlayer).forEach(player -> {
            plugin.getCoreConfig().getStringList("freeze-message").forEach(player::sendMessage);
        });
        Utilities.getOnlinePlayers().stream()
                .map(Player::getUniqueId).map(plugin.getPlayerManagement()::getPlayerData)
                .filter(playerData -> playerData.getPanicSystem().isInPanic()).map(PlayerData::getUniqueId)
                .map(Bukkit::getPlayer).forEach(player -> {
            plugin.getCoreConfig().getStringList("panic-message").forEach(player::sendMessage);
        });
    }
}
