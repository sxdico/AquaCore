package me.activated.core.managers;

import lombok.Getter;
import lombok.Setter;
import me.activated.core.plugin.AquaCore;
import me.activated.core.api.ServerData;
import me.activated.core.api.player.GlobalPlayer;
import me.activated.core.utilities.Manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ServerManagement extends Manager {
    private Set<ServerData> connectedServers = new HashSet<>();

    public ServerManagement(AquaCore plugin) {
        super(plugin);
    }

    public ServerData createServerData(String name) {
        if (getServerData(name) != null) return null;
        this.connectedServers.add(new ServerData(name));
        return getServerData(name);
    }

    public ServerData getServerData(String name) {
        return this.connectedServers.stream().filter(serverData -> serverData.getServerName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<GlobalPlayer> getGlobalPlayers() {
        List<GlobalPlayer> players = new ArrayList<>();
        this.connectedServers.forEach(serverData -> players.addAll(serverData.getOnlinePlayers()));
        return players;
    }

    public int getGlobalMaxPlayers() {
        int i = 0;
        for (ServerData serverData : this.connectedServers) {
            i += serverData.getMaxPlayers();
        }
        return i;
    }
    public GlobalPlayer getGlobalPlayer(String name) {
        GlobalPlayer globalPlayerReturn = null;
        for (ServerData server : this.connectedServers) {
            for (GlobalPlayer globalPlayer : server.getOnlinePlayers()) {
                if (globalPlayer.getName().equalsIgnoreCase(name)) {
                    globalPlayerReturn = globalPlayer;
                }
            }
        }
        return globalPlayerReturn;
    }
}
