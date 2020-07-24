package me.activated.core.punishments.menus.alts;

import lombok.AllArgsConstructor;
import me.activated.core.punishments.PunishmentPlugin;
import me.activated.core.punishments.menus.slots.PreviousMenuSlot;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.item.ItemBuilder;
import me.activated.core.menu.menu.SwitchableMenu;
import me.activated.core.menu.slots.Slot;
import me.activated.core.menu.slots.pages.PageSlot;
import me.activated.core.punishments.player.PunishPlayerData;
import me.activated.core.punishments.utilities.punishments.Alt;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class AltsMenu extends SwitchableMenu {
    private final PunishPlayerData playerData;

    @Override
    public String getPagesTitle(Player player) {
        return Color.translate("&7" + playerData.getPlayerName() + "'s alts");
    }

    @Override
    public List<Slot> getEveryMenuSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        slots.add(new Slot() {

            @Override
            public ItemStack getItem(Player player) {
                ItemBuilder item = new ItemBuilder(Material.PAPER);
                item.setName(PunishmentPlugin.MAIN_COLOR + "About");
                item.addLoreLine(" ");
                item.addLoreLine("&7This menu is showing all &f" + playerData.getPlayerName() + "'s &7alts");
                item.addLoreLine("&7that are recorded on the last ip of the user.");
                item.addLoreLine(" ");
                item.addLoreLine(PunishmentPlugin.MIDDLE_COLOR + "Alts amount&7: " + PunishmentPlugin.SECONDARY_COLOR + playerData.getAlts().size());
                item.addLoreLine(PunishmentPlugin.MIDDLE_COLOR + "Banned alts&7: " + PunishmentPlugin.SECONDARY_COLOR + playerData.getAlts().stream().filter(Alt::isBanned).collect(Collectors.toList()).size());
                item.addLoreLine(" ");

                return item.toItemStack();
            }

            @Override
            public int getSlot() {
                return 4;
            }

        });
        slots.add(new PreviousMenuSlot());
        slots.add(new PageSlot(this, 40));

        return slots;
    }

    @Override
    public List<Slot> getSwitchableSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        playerData.getAlts().forEach(alt -> slots.add(new AltSlot(alt)));

        return slots;
    }

    @AllArgsConstructor
    private class AltSlot extends Slot {
        private final Alt alt;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM);
            item.setDurability(3);
            item.setSkullOwner(alt.getName());
            item.setName(PunishmentPlugin.MAIN_COLOR + alt.getName() + "&7(" + (alt.isBanned() ? "&cBanned" : Bukkit.getPlayer(alt.getName()) == null ? "&eOffline" : "&aOnline") + "&7)");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 0;
        }
    }
}
