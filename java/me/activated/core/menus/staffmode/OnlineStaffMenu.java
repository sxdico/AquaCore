package me.activated.core.menus.staffmode;

import lombok.AllArgsConstructor;
import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.menu.menu.SwitchableMenu;
import me.activated.core.menu.slots.Slot;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.chat.Replacement;
import me.activated.core.utilities.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OnlineStaffMenu extends SwitchableMenu {

    @Override
    public List<Slot> getSwitchableSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        Utilities.getOnlinePlayers().stream().filter(online -> online.hasPermission(plugin.getStaffModeManagement().getStaffPermission())).forEach(online -> {
            slots.add(new PlayerSlot(online));
        });

        return slots;
    }

    @Override
    public List<Slot> getEveryMenuSlots(Player player) {

        return null;
    }

    @Override
    public String getPagesTitle(Player player) {
        return Color.translate("&7Staff Online");
    }

    @AllArgsConstructor
    private class PlayerSlot extends Slot {
        private final Player target;

        @Override
        public ItemStack getItem(Player player) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());

            ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM);
            item.setName(playerData.getNameWithColor());
            item.setDurability(3);
            item.setSkullOwner(target.getName());
            plugin.getStaffModeFile().getStringList("online-staff-gui.lore").forEach(message -> {
                Replacement replacement = new Replacement(message);
                replacement.add("<isVanished>", playerData.isVanished() ? "&aYes" : "&cNo");
                replacement.add("<isStaffMode>", playerData.isInStaffMode() ? "&aYes" : "&cNo");
                replacement.add("<rank>", playerData.getHighestRank().getDisplayName());
                replacement.add("<target>", target.getName());
                item.addLoreLine(replacement.toString());
            });
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            Player to = Bukkit.getPlayer(target.getName());

            if (to == null) {
                update(player);
                return;
            }
            player.teleport(to);
            player.sendMessage(Language.TELEPORTED_TO_PLAYER.toString()
                    .replace("<target>", to.getName()));
        }
    }
}
