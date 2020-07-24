package me.activated.core.punishments.menus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.activated.core.menu.menu.AquaMenu;
import me.activated.core.menu.slots.Slot;
import me.activated.core.punishments.PunishmentPlugin;
import me.activated.core.punishments.menus.alts.AltsMenu;
import me.activated.core.punishments.menus.alts.PotentialAltsMenu;
import me.activated.core.punishments.menus.punishments.*;
import me.activated.core.punishments.menus.slots.DataInfoSlot;
import me.activated.core.punishments.player.PunishData;
import me.activated.core.punishments.player.PunishPlayerData;
import me.activated.core.punishments.utilities.punishments.Punishment;
import me.activated.core.punishments.utilities.punishments.PunishmentType;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class CheckMenu extends AquaMenu {
    private final PunishData punishData;

    @Override
    public String getName(Player player) {
        return Color.translate("&7" + punishData.getPlayerData().getPlayerName() + "'s punishments");
    }

    @Override
    public List<Slot> getSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        slots.add(new DataInfoSlot(punishData.getPlayerData(), 4));

        slots.add(new BansSlot(punishData.getPlayerData()));
        slots.add(new BlacklistsSlot(punishData.getPlayerData()));
        slots.add(new MutesSlot(punishData.getPlayerData()));
        slots.add(new WarnsSlot(punishData.getPlayerData()));
        slots.add(new KicksSlot(punishData.getPlayerData()));

        slots.add(new AltsSlot(punishData.getPlayerData()));

        return slots;
    }

    @AllArgsConstructor
    private class BansSlot extends Slot {
        private final PunishPlayerData playerData;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setDurability(3);
            item.setName("&bBans");
            item.addLoreLine("");
            List<Punishment> bans = playerData.getPunishData().getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.BAN).collect(Collectors.toList());
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Currently banned&7: " + (playerData.getPunishData().isBanned() ? "&aYes" : "&cNo"));
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "User was banned " + PunishmentPlugin.SECONDARY_COLOR + bans.size() + PunishmentPlugin.MAIN_COLOR + " times.");
            item.addLoreLine("");

            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 18;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            List<Punishment> punishments = playerData.getPunishData().getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.BAN).collect(Collectors.toList());
            if (punishments.size() == 0) return;
            new BansMenu(playerData.getPunishData()).open(player);
        }
    }

    @AllArgsConstructor
    private class BlacklistsSlot extends Slot {
        private final PunishPlayerData playerData;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setDurability(14);
            item.setName("&4Blacklists");
            item.addLoreLine("");
            List<Punishment> blacklists = playerData.getPunishData().getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.BLACKLIST).collect(Collectors.toList());
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Currently blacklisted&7: " + (playerData.getPunishData().isBlacklisted() ? "&aYes" : "&cNo"));
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "User was blacklisted " + PunishmentPlugin.SECONDARY_COLOR + blacklists.size() + PunishmentPlugin.MAIN_COLOR + " times.");
            item.addLoreLine("");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 20;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            List<Punishment> punishments = playerData.getPunishData().getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.BLACKLIST).collect(Collectors.toList());
            if (punishments.size() == 0) return;
            new BlacklistsMenu(playerData.getPunishData()).open(player);
        }
    }

    @AllArgsConstructor
    private class MutesSlot extends Slot {
        private final PunishPlayerData playerData;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setDurability(4);
            item.setName("&eMutes");
            item.addLoreLine("");
            List<Punishment> mutes = playerData.getPunishData().getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.MUTE).collect(Collectors.toList());
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Currently muted&7: " + (playerData.getPunishData().isMuted() ? "&aYes" : "&cNo"));
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "User was muted " + PunishmentPlugin.SECONDARY_COLOR + mutes.size() + PunishmentPlugin.MAIN_COLOR + " times.");
            item.addLoreLine("");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 22;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            List<Punishment> punishments = playerData.getPunishData().getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.MUTE).collect(Collectors.toList());
            if (punishments.size() == 0) return;
            new MutesMenu(playerData.getPunishData()).open(player);
        }
    }

    @AllArgsConstructor
    private class KicksSlot extends Slot {
        private final PunishPlayerData playerData;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setDurability(9);
            item.setName("&3Kicks");
            item.addLoreLine("");
            List<Punishment> kicks = playerData.getPunishData().getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.KICK).collect(Collectors.toList());
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "User was kicked " + PunishmentPlugin.SECONDARY_COLOR + kicks.size() + PunishmentPlugin.MAIN_COLOR + " times.");
            item.addLoreLine("");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 26;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            List<Punishment> punishments = playerData.getPunishData().getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.KICK).collect(Collectors.toList());
            if (punishments.size() == 0) return;
            new KicksMenu(playerData.getPunishData()).open(player);
        }
    }

    @AllArgsConstructor
    private class WarnsSlot extends Slot {
        private final PunishPlayerData playerData;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setDurability(6);
            item.setName("&dWarns");
            item.addLoreLine("");
            List<Punishment> warns = playerData.getPunishData().getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.WARN).collect(Collectors.toList());
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Currently warned&7: " + (playerData.getPunishData().isWarned() ? "&aYes" : "&cNo"));
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "User was warned " + PunishmentPlugin.SECONDARY_COLOR + warns.size() + PunishmentPlugin.MAIN_COLOR + " times.");
            item.addLoreLine("");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 24;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            List<Punishment> punishments = playerData.getPunishData().getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.WARN).collect(Collectors.toList());
            if (punishments.size() == 0) return;
            new WarnsMenu(playerData.getPunishData()).open(player);
        }
    }

    @AllArgsConstructor
    private class AltsSlot extends Slot {
        private final PunishPlayerData playerData;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.ANVIL);
            item.setSkullOwner(punishData.getPlayerData().getPlayerName());
            item.setName(PunishmentPlugin.MAIN_COLOR + "Alts &7(" + PunishmentPlugin.SECONDARY_COLOR + playerData.getPotentialAlts().size() + " potential&7, " + PunishmentPlugin.SECONDARY_COLOR + playerData.getAlts().size() + " on last ip&7)");
            item.addLoreLine("&7(&cBanned&7, &aOnline&7, &eOffline&7)");
            item.addLoreLine(" ");
            item.addLoreLine(PunishmentPlugin.SECONDARY_COLOR + "Potential Alts");
            if (playerData.getPotentialAlts().size() == 0) {
                item.addLoreLine(PunishmentPlugin.MIDDLE_COLOR + "- &cNone found!");
            } else {
                playerData.getPotentialAlts().stream().limit(5).forEach(alt -> item.addLoreLine(PunishmentPlugin.MIDDLE_COLOR + "- " + alt.getNameColor() + alt.getName()));
            }
            item.addLoreLine(PunishmentPlugin.SECONDARY_COLOR + "Alts on last ip &7(More secured)");
            if (playerData.getAlts().size() == 0) {
                item.addLoreLine(PunishmentPlugin.MIDDLE_COLOR + "- &cNone found!");
            } else {
                playerData.getAlts().stream().limit(5).forEach(alt -> item.addLoreLine(PunishmentPlugin.MIDDLE_COLOR + "- " + alt.getNameColor() + alt.getName()));
            }
            item.addLoreLine(" ");
            item.addLoreLine(PunishmentPlugin.MIDDLE_COLOR + "This item can show up to 5 alts,");
            item.addLoreLine(PunishmentPlugin.MIDDLE_COLOR + "if there's more, right-click to see");
            item.addLoreLine(PunishmentPlugin.MIDDLE_COLOR + "potential ones and left-click for last ip ones.");
            item.addLoreLine(" ");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 40;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            if (clickType == ClickType.RIGHT) {
                new PotentialAltsMenu(playerData).open(player);
            } else if (clickType == ClickType.LEFT) {
                new AltsMenu(playerData).open(player);
            }
        }
    }
}
