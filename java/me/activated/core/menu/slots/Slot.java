package me.activated.core.menu.slots;

import me.activated.core.utilities.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;


public abstract class Slot {

    public abstract ItemStack getItem(Player player);
    public abstract int getSlot();

    public void onClick(Player player, int slot, ClickType clickType) {

    }

    public int[] getSlots() {
        return null;
    }


    public boolean hasSlot(int slot) {
        return slot == this.getSlot() || this.getSlots() != null && Arrays.stream(this.getSlots()).anyMatch(i -> i == slot);
    }

    public static boolean hasSlot(List<Slot> slots, int value) {
        return slots.stream()
                .filter(slot -> slot.getSlot() == value || slot.getSlots() != null
                        && Arrays.stream(slot.getSlots()).anyMatch(i -> i == value))
                .findFirst().orElse(null) != null;
    }

    public static Slot getGlass(int slot) {
        return new Slot() {

            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).toItemStack();
            }

            @Override
            public int getSlot() {
                return slot;
            }
        };
    }
}
