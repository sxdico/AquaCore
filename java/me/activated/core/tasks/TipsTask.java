package me.activated.core.tasks;

import me.activated.core.api.player.PlayerData;
import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.Color;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TipsTask implements Runnable {
    private final AquaCore plugin = AquaCore.INSTANCE;

    private final AtomicInteger size = new AtomicInteger(0);

    @Override
    public void run() {
        List<String> tips = plugin.getCoreConfig().getStringList("tips.messages");

        Utilities.getOnlinePlayers().forEach(player -> {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            if (playerData != null && playerData.isTipsAlerts()) {
                player.sendMessage(Color.translate(tips.get(size.get()).replace("{0}", "\n")));
            }
        });
        size.getAndIncrement();
        if (size.get() > tips.size() - 1) {
            size.set(0);
        }
    }
}
