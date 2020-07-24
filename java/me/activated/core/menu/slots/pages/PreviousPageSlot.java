package me.activated.core.menu.slots.pages;

import lombok.RequiredArgsConstructor;
import me.activated.core.menu.menu.SwitchableMenu;
import me.activated.core.menu.slots.Slot;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.item.ItemBuilder;
import me.activated.core.enums.Language;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class PreviousPageSlot extends Slot {
    private final SwitchableMenu switchableMenu;

    @Override
    public ItemStack getItem(Player player) {
        ItemBuilder item = new ItemBuilder(Material.GOLD_NUGGET);
        item.setName("&bPrevious page");
        if (this.switchableMenu.getPage() != 1) {
            item.addLoreLine(" ");
            item.addLoreLine("&3Click to head");
            item.addLoreLine("&3over to previous page.");
            item.addLoreLine(" ");
        } else {
            item.addLoreLine(" ");
            item.addLoreLine("&cThere is no previous page.");
            item.addLoreLine("&cYou're on the first page.");
            item.addLoreLine(" ");
        }
        return item.toItemStack();
    }

    @Override
    public void onClick(Player player, int slot, ClickType clickType) {
        if (this.switchableMenu.getPage() != 1) {
            Utilities.playSound(player, Sound.ORB_PICKUP);
        } else {
            player.sendMessage(Color.translate(Language.PREFIX + "&bYou're on the first page of the menu!"));
            return;
        }
        this.switchableMenu.changePage(player, -1);
    }

    @Override
    public int getSlot() {
        return 0;
    }

    @Override
    public int[] getSlots() {
        return new int[]{36};
    }
}
