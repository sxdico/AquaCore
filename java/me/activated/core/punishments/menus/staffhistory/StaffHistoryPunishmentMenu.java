package me.activated.core.punishments.menus.staffhistory;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.activated.core.punishments.PunishmentPlugin;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.general.DateUtils;
import me.activated.core.utilities.general.Tasks;
import me.activated.core.utilities.item.ItemBuilder;
import me.activated.core.api.player.PlayerData;
import me.activated.core.data.other.punishments.PunishHistory;
import me.activated.core.menu.menu.SwitchableMenu;
import me.activated.core.menu.slots.Slot;
import me.activated.core.menus.slots.PlayerInfoSlot;
import me.activated.core.punishments.utilities.punishments.PunishmentType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class StaffHistoryPunishmentMenu extends SwitchableMenu {
    private final PlayerData playerData;
    private final PunishmentType punishmentType;
    private boolean activeOnly = true;

    @Override
    public String getPagesTitle(Player player) {
        return "&7Checking: " + playerData.getPlayerName();
    }

    @Override
    public List<Slot> getSwitchableSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        AtomicInteger order = new AtomicInteger(1);
        PunishHistory.getPunishments(playerData, this.punishmentType, this.activeOnly)
                .stream().sorted(Comparator.comparingLong(PunishHistory::getAddedAt).reversed()).forEach(punishHistory -> {
            slots.add(new PunishSlot(punishHistory, order.getAndIncrement()));
        });

        return slots;
    }

    @Override
    public List<Slot> getEveryMenuSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        slots.add(new PlayerInfoSlot(playerData, 4));
        slots.add(new ActiveOnlySlot(40));

        slots.add(new Slot() {

            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.ARROW).setName("&c&lGo Back!").toItemStack();
            }

            @Override
            public void onClick(Player player, int slot, ClickType clickType) {
                new StaffHistoryMenu(playerData).open(player);
            }

            @Override
            public int getSlot() {
                return 41;
            }

            @Override
            public int[] getSlots() {
                return new int[]{39};
            }
        });
        return slots;
    }

    @AllArgsConstructor
    private class PunishSlot extends  Slot {
        private final PunishHistory punishHistory;
        private final int order;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(punishHistory.hasExpired() ? Material.BOOK : Material.ENCHANTED_BOOK);
            item.setName(PunishmentPlugin.MAIN_COLOR + "#" + order + " &7(" + PunishmentPlugin.SECONDARY_COLOR + DateUtils.getDate(punishHistory.getAddedAt()) + "&7)");
            item.addLoreLine("&7&m---&8*&7&m---------------------&8*&7&m---");
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Target&7: " + PunishmentPlugin.SECONDARY_COLOR + punishHistory.getTarget());
            if (punishHistory.getPunishmentType() != PunishmentType.KICK) {
                item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Duration&7: " + PunishmentPlugin.SECONDARY_COLOR + punishHistory.getNiceDuration());
                item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Expire&7: " + PunishmentPlugin.SECONDARY_COLOR + punishHistory.getNiceExpire());
            }
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Reason&7: " + PunishmentPlugin.SECONDARY_COLOR + punishHistory.getReason());
            item.addLoreLine("&7&m---&8*&7&m---------------------&8*&7&m---");
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Permanent&7: " + (punishHistory.isPermanent() ? "&aYes" : "&cNo"));
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Active&7: " + (!punishHistory.hasExpired() ? "&aYes" : "&cNo"));
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Silent&7: " + (punishHistory.isSilent() ? "&aYes" : "&cNo"));
            item.addLoreLine("&7&m---&8*&7&m---------------------&8*&7&m---");
            item.addLoreLine(PunishmentPlugin.SECONDARY_COLOR + "Click to check " + PunishmentPlugin.MAIN_COLOR + punishHistory.getTarget() + "'s " + PunishmentPlugin.SECONDARY_COLOR + "punishments");
            item.addLoreLine("&7&m---&8*&7&m---------------------&8*&7&m---");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            Tasks.run(plugin, () -> player.performCommand("check " + punishHistory.getTarget()));
        }
    }

    @AllArgsConstructor
    private class ActiveOnlySlot extends Slot {
        private final int slot;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.PAPER);
            item.setName("&aPunishments to show");
            item.addLoreLine(" ");
            if (activeOnly) {
                item.addLoreLine("&7Currently showing");
                item.addLoreLine("&7active punishments only!");
            } else {
                item.addLoreLine("&7Currently showing all");
                item.addLoreLine("&7active/expired punishments!");
            }
            item.addLoreLine(" ");
            item.addLoreLine("&aClick to change!");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return slot;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            activeOnly = !activeOnly;
            update(player);
            Utilities.playSound(player, Sound.ORB_PICKUP);
        }
    }
}
