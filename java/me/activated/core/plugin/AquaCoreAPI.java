package me.activated.core.plugin;

import lombok.NonNull;
import me.activated.core.api.ServerData;
import me.activated.core.api.player.PlayerData;
import me.activated.core.api.rank.RankData;
import me.activated.core.api.rank.grant.Grant;
import me.activated.core.api.tags.Tag;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class AquaCoreAPI {

    public static AquaCore plugin = AquaCore.INSTANCE;
    public static AquaCoreAPI INSTANCE;

    public AquaCoreAPI() {
        INSTANCE = this;
    }

    public PlayerData getPlayerData(UUID uuid) {
        return plugin.getPlayerManagement().getPlayerData(uuid);
    }

    @NonNull
    public ChatColor getPlayerNameColor(UUID uuid) {
        PlayerData playerData = this.getPlayerData(uuid);
        if (playerData == null) {
            return ChatColor.WHITE;
        }
        if (playerData.getNameColor() == null) {
            return ChatColor.WHITE;
        }
        return playerData.getHighestRank().getColor();
    }

    public RankData getPlayerRank(UUID uuid) {
        if (this.getPlayerData(uuid) == null) return new RankData("Default");
        return this.getPlayerData(uuid).getHighestRank();
    }

    public List<Grant> getActiveGrants(UUID uuid) {
        return this.getPlayerData(uuid).getActiveGrants();
    }

    public List<Grant> getAllGrants(UUID uuid) {
        return this.getPlayerData(uuid).getGrants();
    }

    public Tag getTag(UUID uuid) {
        return this.getPlayerData(uuid).getTag();
    }

    public ServerData getServerData(String server) {
        return plugin.getServerManagement().getServerData(server);
    }

    public boolean hasTag(Player player, Tag tag) {
        return player.hasPermission("Aqua.tags." + tag.getName().toLowerCase());
    }

    public static boolean isRegistered() {
        return INSTANCE != null;
    }
}
