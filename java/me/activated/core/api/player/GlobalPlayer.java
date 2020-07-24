package me.activated.core.api.player;

import lombok.Getter;
import lombok.Setter;
import me.activated.core.database.redis.other.bson.JsonChain;
import me.activated.core.database.redis.payload.action.JedisAction;
import me.activated.core.plugin.AquaCore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class GlobalPlayer {
    private AquaCore plugin = AquaCore.INSTANCE;

    private UUID uniqueId;
    private String name, server, address, rankName, firstJoined, lastServer;
    private List<String> permissions = new ArrayList<>();
    private long lastSeen, lastActivity = -1L;
    private int rankWeight;
    private boolean op, vanished;
    private boolean staffChatAlerts, adminChatAlerts, helpopAlerts, reportAlerts;

    public boolean hasPermission(String value) {
        if (isOp()) return true;
        return this.permissions.stream().filter(permission -> permission.equalsIgnoreCase(value)).findFirst().orElse(null) != null;
    }

    public void sendMessage(String message) {
        plugin.getRedisData().write(JedisAction.PLAYER_MESSAGE,
                new JsonChain().addProperty("name", this.name).addProperty("message", message).get());
    }

    public boolean isCurrentOnline() {
        return plugin.getServerManagement().getConnectedServers().stream().filter(serverData ->
                serverData.getNames().stream().map(String::toLowerCase).collect(Collectors.toList())
                        .contains(name.toLowerCase())).findFirst().orElse(null) != null;
    }
}
