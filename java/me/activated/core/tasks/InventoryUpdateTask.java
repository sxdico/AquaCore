package me.activated.core.tasks;

import me.activated.core.api.player.PlayerData;
import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.Utilities;

public class InventoryUpdateTask implements Runnable {
    private final AquaCore plugin = AquaCore.INSTANCE;

    @Override
    public void run() {
        Utilities.getOnlinePlayers().forEach(player -> {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            if (playerData != null && playerData.isFullJoined()) {
                playerData.getOfflineInventory().update(player);
                playerData.getOfflineInventory().save(player);
            }
        });
    }
}
