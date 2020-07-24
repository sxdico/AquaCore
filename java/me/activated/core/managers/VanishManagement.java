package me.activated.core.managers;

import me.activated.core.plugin.AquaCore;
import me.activated.core.api.player.PlayerData;
import me.activated.core.utilities.Manager;
import me.activated.core.utilities.Utilities;
import org.bukkit.entity.Player;

public class VanishManagement extends Manager {

    public VanishManagement(AquaCore plugin) {
        super(plugin);
    }

    public int getVanishPriority(Player player) {
        for (int i = 50; i > 0; i--) {
            if (player.hasPermission("Aqua.vanish.priority." + i)) return i;
        }
        return 1;
    }

    public void vanishPlayerFor(Player player, Player target) {
        if (this.getVanishPriority(target) >= this.getVanishPriority(player)) return;

        target.hidePlayer(player);
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        playerData.setVanished(true);
    }

    public void vanishPlayer(Player player) {
        Utilities.getOnlinePlayers().forEach(online -> {
            if (this.getVanishPriority(online) >= this.getVanishPriority(player)) return;

            online.hidePlayer(player);
        });
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        playerData.setVanished(true);
    }

    public void unvanishPlayer(Player player) {
        Utilities.getOnlinePlayers().forEach(online -> {
            if (!online.canSee(player)) {
                online.showPlayer(player);
            }
        });
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        playerData.setVanished(false);
    }
}
