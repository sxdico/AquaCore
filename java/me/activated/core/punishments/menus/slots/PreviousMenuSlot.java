package me.activated.core.punishments.menus.slots;

import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.item.ItemBuilder;
import me.activated.core.menu.menu.AquaMenu;
import me.activated.core.menu.slots.Slot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class PreviousMenuSlot extends Slot {
    private final AquaCore plugin = AquaCore.INSTANCE;

    @Override
    public ItemStack getItem(Player player) {
        ItemBuilder item = new ItemBuilder(Material.ARROW);
        AquaMenu lastMenu = plugin.getMenuManager().getLastOpenedMenus().get(player.getUniqueId());
        if (lastMenu == null) {
            item.setName("&c&lClose!");
        } else {
            item.setName("&c&lGo Back!");
        }
        return item.toItemStack();
    }

    @Override
    public int getSlot() {
        return 39;
    }

    @Override
    public int[] getSlots() {
        return new int[] {41};
    }

    @Override
    public void onClick(Player player, int slot, ClickType clickType) {
        AquaMenu lastMenu = plugin.getMenuManager().getLastOpenedMenus().get(player.getUniqueId());
        if (lastMenu == null) {
            player.closeInventory();
        } else {
            lastMenu.open(player);
        }
    }
}
