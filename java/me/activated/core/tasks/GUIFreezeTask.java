package me.activated.core.tasks;

import me.activated.core.api.player.PlayerData;
import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GUIFreezeTask implements Runnable {
    private final AquaCore plugin = AquaCore.INSTANCE;

    @Override
    public void run() {
        Utilities.getOnlinePlayers().stream()
                .map(Player::getUniqueId).map(plugin.getPlayerManagement()::getPlayerData)
                .filter(PlayerData::isGuiFrozen).map(PlayerData::getUniqueId)
                .map(Bukkit::getPlayer).forEach(player -> {
            player.closeInventory();
            player.openInventory(this.freezeGUI());
        });
    }

    private Inventory freezeGUI() {
        Inventory inventory = Bukkit.createInventory(null, 45, "Frozen");

        inventory.setItem(40, new ItemBuilder(Material.BOOK)
                .setName("&4&lFrozen")
                .addLoreLine("&cYou've been frozen by a staff member.")
                .addLoreLine("&cPlease don't leave server otherwise")
                .addLoreLine("&cyou will be banned.")
                .addLoreLine(" ")
                .addLoreLine("&cJoin our teamspeak server: &4" + plugin.getEssentialsManagement().getTeamspeak())
                .addLoreLine(" ").toItemStack());

        return inventory;
    }
}
