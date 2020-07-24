package me.activated.core.managers;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import me.activated.core.plugin.AquaCore;
import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.utilities.Manager;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.general.Tasks;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class PlayerManagement extends Manager {
    private Map<UUID, PlayerData> playerData = new HashMap<>();

    public PlayerManagement(AquaCore plugin) {
        super(plugin);
    }

    public PlayerData createPlayerData(UUID uuid, String name) {
        if (this.playerData.containsKey(uuid)) return getPlayerData(uuid);
        this.playerData.put(uuid, new PlayerData(uuid, name));
        return getPlayerData(uuid);
    }

    public PlayerData getPlayerData(UUID uuid) {
        return this.playerData.get(uuid);
    }

    public void deleteData(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) return;
        this.playerData.remove(uuid);
    }

    public PlayerData loadData(UUID uuid) {
        Document document = plugin.getMongoManager().getDocumentation().find(Filters.eq("uuid", uuid.toString())).first();

        if (document == null) {
            return null;
        }
        this.createPlayerData(uuid, document.getString("name"));
        return this.getPlayerData(uuid);
    }

    public String getFixedName(String name) {
        Document document = plugin.getMongoManager().getDocumentation().find(Filters.eq("lowerCaseName", name.toLowerCase())).first();
        if (document == null) return name;
        return document.getString("name");
    }


    public void sendStaffChatMessage(PlayerData playerData, String message) {
        plugin.getServerManagement().getGlobalPlayers().stream().filter(globalPlayer -> globalPlayer.hasPermission("Aqua.staffchat") && globalPlayer.isStaffChatAlerts()).forEach(globalPlayer -> {
            globalPlayer.sendMessage(Language.STAFF_CHAT_FORMAT.toString()
                    .replace("<player>", playerData.getNameColor() != null ? playerData.getNameColor() + playerData.getPlayerName() : playerData.getHighestRank().getColor() + playerData.getPlayerName())
                    .replace("<prefix>", Color.translate(playerData.getHighestRank().getPrefix()))
                    .replace("<suffix>", Color.translate(playerData.getHighestRank().getSuffix()))
                    .replace("<server>", plugin.getEssentialsManagement().getServerName())
                    .replace("<message>", message));
        });
    }

    public void sendAdminChatMessage(PlayerData playerData, String message) {
        plugin.getServerManagement().getGlobalPlayers().stream().filter(globalPlayer -> globalPlayer.hasPermission("Aqua.adminchat") && globalPlayer.isAdminChatAlerts()).forEach(globalPlayer -> {
            globalPlayer.sendMessage(Language.ADMIN_CHAT_FORMAT.toString()
                    .replace("<player>", playerData.getNameColor() != null ? playerData.getNameColor() + playerData.getPlayerName() : playerData.getHighestRank().getColor() + playerData.getPlayerName())
                    .replace("<prefix>", Color.translate(playerData.getHighestRank().getPrefix()))
                    .replace("<suffix>", Color.translate(playerData.getHighestRank().getSuffix()))
                    .replace("<server>", plugin.getEssentialsManagement().getServerName())
                    .replace("<message>", message));
        });
    }

    public void saveData(UUID uniqueId, String value, Object key) {
        Tasks.runAsync(plugin, () -> {
            Document document = plugin.getMongoManager().getDocumentation().find(Filters.eq("uuid", uniqueId.toString())).first();

            if (document != null && document.containsKey(value)) {
                document.put(value, key);

                plugin.getMongoManager().getDocumentation().replaceOne(Filters.eq("uuid", uniqueId.toString()), document, new UpdateOptions().upsert(true));
            }
        });
    }

    public boolean hasData(UUID uuid) {
        Document document = plugin.getMongoManager().getDocumentation().find(Filters.eq("uuid", uuid.toString())).first();

        return document != null;
    }
}
