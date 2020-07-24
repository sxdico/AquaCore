package me.activated.core.menu;

import lombok.Getter;
import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.Manager;
import me.activated.core.menu.menu.AquaMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class MenuManager extends Manager {
    public Map<UUID, AquaMenu> openedMenus = new HashMap<>();
    public Map<UUID, AquaMenu> lastOpenedMenus = new HashMap<>();

    public MenuManager(AquaCore plugin) {
        super(plugin);
    }
}
