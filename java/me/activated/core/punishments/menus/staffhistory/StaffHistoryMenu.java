package me.activated.core.punishments.menus.staffhistory;

import lombok.AllArgsConstructor;
import me.activated.core.punishments.PunishmentPlugin;
import me.activated.core.utilities.item.ItemBuilder;
import me.activated.core.api.player.PlayerData;
import me.activated.core.data.other.punishments.PunishHistory;
import me.activated.core.menu.menu.AquaMenu;
import me.activated.core.menu.slots.Slot;
import me.activated.core.menus.slots.PlayerInfoSlot;
import me.activated.core.punishments.utilities.punishments.PunishmentType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class StaffHistoryMenu extends AquaMenu {
    private final PlayerData playerData;

    @Override
    public List<Slot> getSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        slots.add(new PlayerInfoSlot(playerData, 4));

        slots.add(new BansSlot());
        slots.add(new BlacklistsSlot());
        slots.add(new MutesSlot());
        slots.add(new WarnsSlot());
        slots.add(new KicksSlot());

        for (int i = 0; i < 36; i++) {
            if (!Slot.hasSlot(slots, i)) {
                slots.add(Slot.getGlass(i));
            }
        }
        return slots;
    }

    @Override
    public String getName(Player player) {
        return "&7Punishments";
    }

    @AllArgsConstructor
    private class BansSlot extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            int active = PunishHistory.getPunishments(playerData, PunishmentType.BAN, true).size();
            int all = PunishHistory.getPunishments(playerData, PunishmentType.BAN, false).size();

            ItemBuilder item = new ItemBuilder(Material.INK_SACK);
            item.setDurability(6);
            item.setName("&bBans");
            item.addLoreLine("");
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Bans performed&7: " + PunishmentPlugin.SECONDARY_COLOR + all);
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Active&7: " + PunishmentPlugin.SECONDARY_COLOR + active + PunishmentPlugin.MIDDLE_COLOR + "/" + PunishmentPlugin.SECONDARY_COLOR + all);
            item.addLoreLine("");

            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 18;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            int all = PunishHistory.getPunishments(playerData, PunishmentType.BAN, false).size();
            if (all == 0) return;
            new StaffHistoryPunishmentMenu(playerData, PunishmentType.BAN).open(player);
        }
    }

    @AllArgsConstructor
    private class BlacklistsSlot extends Slot {
        @Override
        public ItemStack getItem(Player player) {
            int active = PunishHistory.getPunishments(playerData, PunishmentType.BLACKLIST, true).size();
            int all = PunishHistory.getPunishments(playerData, PunishmentType.BLACKLIST, false).size();

            ItemBuilder item = new ItemBuilder(Material.INK_SACK);
            item.setDurability(14);
            item.setName("&4Blacklists");
            item.addLoreLine("");
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Blacklists performed&7: " + PunishmentPlugin.SECONDARY_COLOR + all);
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Active&7: " + PunishmentPlugin.SECONDARY_COLOR + active + PunishmentPlugin.MIDDLE_COLOR + "/" + PunishmentPlugin.SECONDARY_COLOR + all);
            item.addLoreLine("");

            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 20;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            int all = PunishHistory.getPunishments(playerData, PunishmentType.BLACKLIST, false).size();
            if (all == 0) return;
            new StaffHistoryPunishmentMenu(playerData, PunishmentType.BLACKLIST).open(player);
        }
    }

    @AllArgsConstructor
    private class MutesSlot extends Slot {
        @Override
        public ItemStack getItem(Player player) {
            int active = PunishHistory.getPunishments(playerData, PunishmentType.MUTE, true).size();
            int all = PunishHistory.getPunishments(playerData, PunishmentType.MUTE, false).size();

            ItemBuilder item = new ItemBuilder(Material.INK_SACK);
            item.setDurability(8);
            item.setName("&eMutes");
            item.addLoreLine("");
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Mutes performed&7: " + PunishmentPlugin.SECONDARY_COLOR + all);
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Active&7: " + PunishmentPlugin.SECONDARY_COLOR + active + PunishmentPlugin.MIDDLE_COLOR + "/" + PunishmentPlugin.SECONDARY_COLOR + all);
            item.addLoreLine("");

            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 22;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            int all = PunishHistory.getPunishments(playerData, PunishmentType.MUTE, false).size();
            if (all == 0) return;
            new StaffHistoryPunishmentMenu(playerData, PunishmentType.MUTE).open(player);
        }
    }

    @AllArgsConstructor
    private class KicksSlot extends Slot {
        @Override
        public ItemStack getItem(Player player) {
            int active = PunishHistory.getPunishments(playerData, PunishmentType.KICK, true).size();
            int all = PunishHistory.getPunishments(playerData, PunishmentType.KICK, false).size();

            ItemBuilder item = new ItemBuilder(Material.INK_SACK);
            item.setDurability(12);
            item.setName("&3Kicks");
            item.addLoreLine("");
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Kicks performed&7: " + PunishmentPlugin.SECONDARY_COLOR + all);
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Active&7: " + PunishmentPlugin.SECONDARY_COLOR + active + PunishmentPlugin.MIDDLE_COLOR + "/" + PunishmentPlugin.SECONDARY_COLOR + all);
            item.addLoreLine("");

            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 26;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            int all = PunishHistory.getPunishments(playerData, PunishmentType.KICK, false).size();
            if (all == 0) return;
            new StaffHistoryPunishmentMenu(playerData, PunishmentType.KICK).open(player);
        }
    }

    @AllArgsConstructor
    private class WarnsSlot extends Slot {
        @Override
        public ItemStack getItem(Player player) {
            int active = PunishHistory.getPunishments(playerData, PunishmentType.WARN, true).size();
            int all = PunishHistory.getPunishments(playerData, PunishmentType.WARN, false).size();

            ItemBuilder item = new ItemBuilder(Material.INK_SACK);
            item.setDurability(7);
            item.setName("&dWarns");
            item.addLoreLine("");
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Warns performed&7: " + PunishmentPlugin.SECONDARY_COLOR + all);
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Active&7: " + PunishmentPlugin.SECONDARY_COLOR + active + PunishmentPlugin.MIDDLE_COLOR + "/" + PunishmentPlugin.SECONDARY_COLOR + all);
            item.addLoreLine("");

            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 24;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            int all = PunishHistory.getPunishments(playerData, PunishmentType.WARN, false).size();
            if (all == 0) return;
            new StaffHistoryPunishmentMenu(playerData, PunishmentType.WARN).open(player);
        }
    }
}
