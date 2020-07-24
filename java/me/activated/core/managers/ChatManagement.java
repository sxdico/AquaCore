package me.activated.core.managers;

import lombok.Getter;
import lombok.Setter;
import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.Manager;

@Getter
@Setter
public class ChatManagement extends Manager {
    private boolean muted = false;
    private int delay = 3;

    public ChatManagement(AquaCore plugin) {
        super(plugin);
        this.load();
    }

    public void save() {
        plugin.getCoreConfig().set("chat-data.muted", this.isMuted());
        plugin.getCoreConfig().set("chat-data.delay", this.getDelay());
        plugin.getCoreConfig().save();
    }

    public void load() {
        this.muted = plugin.getCoreConfig().getBoolean("chat-data.muted");
        this.delay = plugin.getCoreConfig().getInt("chat-data.delay");
    }
}
