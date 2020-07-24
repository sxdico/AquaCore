package me.activated.core.bungee;

import me.activated.core.bungee.color.BungeeColor;
import me.activated.core.bungee.listeners.InComingChannelListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class AquaBungee extends Plugin {

    public static AquaBungee INSTANCE;

    @Override
    public void onEnable() {
        ProxyServer.getInstance().registerChannel("AquaPermissions");
        ProxyServer.getInstance().registerChannel("AquaSync");
        ProxyServer.getInstance().getPluginManager().registerListener(this, new InComingChannelListener());

        ProxyServer.getInstance().getConsole().sendMessage(BungeeColor.translate("&aAqua Bungee is now enabled."));
    }
}
