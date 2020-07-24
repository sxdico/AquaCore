package me.activated.core.tasks;

import me.activated.core.menu.menu.AquaMenu;
import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.Utilities;

public class MenuTask implements Runnable {
    private final AquaCore plugin = AquaCore.INSTANCE;

    @Override
    public void run() {
        Utilities.getOnlinePlayers().forEach(player -> {
            AquaMenu AquaMenu = plugin.getMenuManager().getOpenedMenus().get(player.getUniqueId());
            if (AquaMenu != null && AquaMenu.isUpdateInTask()) {
                AquaMenu.update(player);
            }
        });
    }
}
