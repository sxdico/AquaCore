package me.activated.core.managers;

import lombok.Getter;
import lombok.Setter;
import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.Manager;

import java.io.File;

@Getter
@Setter
public class EssentialsManagement extends Manager {
    private String serverName;
    private boolean serverJoinable = false;
    private String website, store, discord, teamspeak, twitter;
    private File inventoriesDirectory;

    public EssentialsManagement(AquaCore plugin) {
        super(plugin);

        this.serverName = plugin.getCoreConfig().getString("server-name");
        this.website = plugin.getCoreConfig().getString("media.website");
        this.store = plugin.getCoreConfig().getString("media.store");
        this.discord = plugin.getCoreConfig().getString("media.discord");
        this.teamspeak = plugin.getCoreConfig().getString("media.teamspeak");
        this.twitter = plugin.getCoreConfig().getString("media.twitter");
        this.inventoriesDirectory = new File(plugin.getDataFolder(), "inventories");
        if (!this.inventoriesDirectory.exists()) this.inventoriesDirectory.mkdir();
    }

    public boolean isBungeeSupport() {
        return false;
        //return plugin.getCoreConfig().getBoolean("using-bungee-cord", false);
    }

    public boolean useNameColorList() {
        return plugin.getCoreConfig().getBoolean("format-name-color.list", true);
    }

    public boolean useNameColorBellowName() {
        return plugin.getCoreConfig().getBoolean("format-name-color.bellow-name", true);
    }
}
