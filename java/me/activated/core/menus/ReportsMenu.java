package me.activated.core.menus;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.activated.core.api.player.PlayerData;
import me.activated.core.data.other.report.Report;
import me.activated.core.menu.menu.SwitchableMenu;
import me.activated.core.menu.slots.Slot;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.Replacement;
import me.activated.core.utilities.general.Tasks;
import me.activated.core.utilities.item.ItemBuilder;
import me.activated.core.menus.slots.PlayerInfoSlot;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class ReportsMenu extends SwitchableMenu {
    private final PlayerData playerData;
    private boolean activeOnly = true;

    @Override
    public String getPagesTitle(Player player) {
        return "&7" + playerData.getPlayerName() + "'s reports";
    }

    @Override
    public List<Slot> getSwitchableSlots(Player player) {
        List<Slot> slots = new ArrayList<>();
        if (activeOnly) {
            playerData.getReports().stream().sorted(Comparator.comparingLong(Report::getAddedAt).reversed()).filter(report -> !report.isSolved()).forEach(report -> {
                slots.add(new ReportSlot(report));
            });
        } else {
            playerData.getReports().stream().sorted(Comparator.comparingLong(Report::getAddedAt).reversed()).forEach(report -> {
                slots.add(new ReportSlot(report));
            });
        }
        return slots;
    }

    @Override
    public List<Slot> getEveryMenuSlots(Player player) {
        List<Slot> slots = new ArrayList<>();
        
        slots.add(new ActiveOnly());
        slots.add(new PlayerInfoSlot(playerData, 4));
        
        return slots;
    }

    @Override
    public void onClose(Player player) {
        plugin.getPlayerManagement().deleteData(playerData.getUniqueId());
    }

    @AllArgsConstructor
    private class ReportSlot extends Slot {
        private final Report report;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(!report.isSolved() ? Material.ENCHANTED_BOOK : Material.BOOK);
            item.setName("&f" + report.getDate());
            plugin.getCoreConfig().getStringList("report-info-gui-format").forEach(message -> {
                Replacement replacement = new Replacement(message);
                replacement.add("<reportedBy>", report.getReportedBy());
                replacement.add("<reason>", report.getReason());
                replacement.add("<reporter_server>", report.getReporterServer());
                replacement.add("<target_server>", report.getReportedServer());
                replacement.add("<target>", playerData.getPlayerName());
                if (report.getSolvedBy() != null) {
                    replacement.add("<solvedBy>", report.getSolvedBy());
                }

                item.addLoreLine(replacement.toString());
            });

            plugin.getCoreConfig().getStringList(report.getSolvedBy() == null ? "report-info-gui-format-unsolved" : "report-info-gui-format-solved").forEach(message -> {
                Replacement replacement = new Replacement(message);
                replacement.add("<reportedBy>", report.getReportedBy());
                replacement.add("<reason>", report.getReason());
                replacement.add("<reporter_server>", report.getReporterServer());
                replacement.add("<target_server>", report.getReportedServer());
                replacement.add("<target>", playerData.getPlayerName());
                replacement.add("<solvedBy>", report.getSolvedBy() != null ? report.getSolvedBy() : "Unknown");

                item.addLoreLine(replacement.toString());
            });

            return item.toItemStack();
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            if (report.isSolved()) return;
            report.setSolved(true);
            report.setSolvedBy(player.getName());

            update(player);

            Tasks.runAsync(plugin, player::saveData);
        }
        
        @Override
        public int getSlot() {
            return 0;
        }
    }

    private class ActiveOnly extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.ARROW);
            item.setName("&aReports to show");
            item.addLoreLine(" ");
            if (activeOnly) {
                item.addLoreLine("&7Currently showing");
                item.addLoreLine("&7unsolved reports only!");
            } else {
                item.addLoreLine("&7Currently showing all");
                item.addLoreLine("&7active/solved reports!");
            }
            item.addLoreLine(" ");
            item.addLoreLine("&aClick to change!");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 40;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            activeOnly = !activeOnly;

            update(player);
            
            Utilities.playSound(player, Sound.ORB_PICKUP);
        }
    }
}
