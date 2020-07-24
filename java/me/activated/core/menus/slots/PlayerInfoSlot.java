package me.activated.core.menus.slots;

import lombok.RequiredArgsConstructor;
import me.activated.core.api.player.PlayerData;
import me.activated.core.menu.slots.Slot;
import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.chat.Replacement;
import me.activated.core.utilities.general.DateUtils;
import me.activated.core.utilities.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class PlayerInfoSlot extends Slot {
    private final PlayerData playerData;
    private final int slot;

    private final AquaCore plugin = AquaCore.INSTANCE;

    @Override
    public ItemStack getItem(Player player) {
        ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM);
        item.setDurability(3);
        item.setSkullOwner(playerData.getPlayerName());
        plugin.getCoreConfig().getStringList("player-info-gui-format").forEach(message -> {
            Replacement replacement = new Replacement(message);
            replacement.add("<name>", playerData.getPlayerName());
            replacement.add("<uuid>", playerData.getUniqueId().toString());
            replacement.add("<lastSeen>", (Bukkit.getPlayer(playerData.getPlayerName()) != null ? "Now" : DateUtils.getDate(playerData.getLastSeen())));
            replacement.add("<lastSeenAgo>", playerData.getLastSeenAgo());
            replacement.add("<rank>", playerData.getHighestRank().getDisplayName());

            item.addLoreLine(replacement.toString());
        });
        return item.toItemStack();
    }

    @Override
    public int getSlot() {
        return slot;
    }
}
