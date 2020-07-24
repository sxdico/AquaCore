package me.activated.core.punishments.menus.slots;

import lombok.AllArgsConstructor;
import me.activated.core.punishments.PunishmentPlugin;
import me.activated.core.utilities.general.DateUtils;
import me.activated.core.utilities.item.ItemBuilder;
import me.activated.core.menu.slots.Slot;
import me.activated.core.punishments.player.PunishPlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class DataInfoSlot extends Slot {
    private final PunishPlayerData playerData;
    private final int slot;

    @Override
    public ItemStack getItem(Player player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerData.getUniqueId());

        ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM);
        item.setDurability(3);
        item.setSkullOwner(playerData.getPlayerName());
        item.setName(PunishmentPlugin.MAIN_COLOR + "Player Info");
        item.addLoreLine("");
        item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Name&7: " + PunishmentPlugin.SECONDARY_COLOR + playerData.getPlayerName());
        item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "UUID&7: " + PunishmentPlugin.SECONDARY_COLOR + playerData.getUniqueId().toString());

        Player onlinePlayer = Bukkit.getPlayer(playerData.getPlayerName());
        if (onlinePlayer != null) {
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Last seen&7: " + PunishmentPlugin.SECONDARY_COLOR + "&aNow");
        } else {
            if (playerData.getLastSeen() != null) {
                item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Last seen&7: " + PunishmentPlugin.SECONDARY_COLOR + playerData.getLastSeen());
            } else {
                item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Last seen&7: " + PunishmentPlugin.SECONDARY_COLOR + "&cNever played before!");
            }
        }
        item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "First Joined&7: " + PunishmentPlugin.SECONDARY_COLOR + (offlinePlayer.getFirstPlayed() != 0 ? DateUtils.getDate(offlinePlayer.getFirstPlayed()) : "&cNever played before!"));
        item.addLoreLine("");

        return item.toItemStack();
    }

    @Override
    public int getSlot() {
        return this.slot;
    }
}
