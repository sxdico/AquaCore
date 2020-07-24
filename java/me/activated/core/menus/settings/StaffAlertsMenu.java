package me.activated.core.menus.settings;

import lombok.RequiredArgsConstructor;
import me.activated.core.api.player.PlayerData;
import me.activated.core.menu.menu.AquaMenu;
import me.activated.core.menu.slots.Slot;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.Symbols;
import me.activated.core.utilities.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class StaffAlertsMenu extends AquaMenu {

    private final PlayerData playerData;

    @Override
    public List<Slot> getSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        slots.add(new StaffChatAlerts());
        slots.add(new AdminChatAlerts());
        slots.add(new ReportAlerts());
        slots.add(new HelpopAlerts());

        for (int i = 0; i < 27; i++) {
            if (!Slot.hasSlot(slots, i)) {
                slots.add(Slot.getGlass(i));
            }
        }

        return slots;
    }

    @Override
    public String getName(Player player) {
        return "&7Staff Alerts";
    }

    private class StaffChatAlerts extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.CHEST);
            item.setName("&bStaff Chat Alerts");
            item.addLoreLine("&7Dou you want to see");
            item.addLoreLine("&7staff chat messages?");
            item.addLoreLine(" ");
            if (playerData.isStaffChatAlerts()) {
                item.addLoreLine(Symbols.X + " &aYes");
                item.addLoreLine("&eNo");
            } else {
                item.addLoreLine("&eYes");
                item.addLoreLine(Symbols.X + " &aNo");
            }
            item.addLoreLine(" ");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 10;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            playerData.setStaffChatAlerts(!playerData.isStaffChatAlerts());
            update(player);

            Utilities.playSound(player, Sound.ORB_PICKUP);
        }
    }

    private class AdminChatAlerts extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.ENDER_CHEST);
            item.setName("&cAdmin Chat Alerts");
            item.addLoreLine("&7Dou you want to see");
            item.addLoreLine("&7admin chat messages?");
            item.addLoreLine(" ");
            if (playerData.isAdminChatAlerts()) {
                item.addLoreLine(Symbols.X + " &aYes");
                item.addLoreLine("&eNo");
            } else {
                item.addLoreLine("&eYes");
                item.addLoreLine(Symbols.X + " &aNo");
            }
            item.addLoreLine(" ");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 12;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            playerData.setAdminChatAlerts(!playerData.isAdminChatAlerts());
            update(player);

            Utilities.playSound(player, Sound.ORB_PICKUP);
        }
    }

    private class HelpopAlerts extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.BOOK);
            item.setName("&eRequests Alerts");
            item.addLoreLine("&7Dou you want to see");
            item.addLoreLine("&7requests messages?");
            item.addLoreLine(" ");
            if (playerData.isHelpopAlerts()) {
                item.addLoreLine(Symbols.X + " &aYes");
                item.addLoreLine("&eNo");
            } else {
                item.addLoreLine("&eYes");
                item.addLoreLine(Symbols.X + " &aNo");
            }
            item.addLoreLine(" ");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 14;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            playerData.setHelpopAlerts(!playerData.isHelpopAlerts());
            update(player);

            Utilities.playSound(player, Sound.ORB_PICKUP);
        }
    }

    private class ReportAlerts extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.BLAZE_POWDER);
            item.setName("&9Reports Alerts");
            item.addLoreLine("&7Dou you want to see");
            item.addLoreLine("&7reports messages?");
            item.addLoreLine(" ");
            if (playerData.isReportAlerts()) {
                item.addLoreLine(Symbols.X + " &aYes");
                item.addLoreLine("&eNo");
            } else {
                item.addLoreLine("&eYes");
                item.addLoreLine(Symbols.X + " &aNo");
            }
            item.addLoreLine(" ");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 16;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            playerData.setReportAlerts(!playerData.isReportAlerts());
            update(player);

            Utilities.playSound(player, Sound.ORB_PICKUP);
        }
    }
}
