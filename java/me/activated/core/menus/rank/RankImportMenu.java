package me.activated.core.menus.rank;

import me.activated.core.enums.Language;
import me.activated.core.menu.menu.AquaMenu;
import me.activated.core.menu.slots.Slot;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.file.ConfigFile;
import me.activated.core.utilities.general.Tasks;
import me.activated.core.utilities.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RankImportMenu extends AquaMenu {

    @Override
    public String getName(Player player) {
        return "&7Ranks imports";
    }

    @Override
    public List<Slot> getSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        //slots.add(new ImportLuckPerm());
        slots.add(new ImportPermissionsEx());
        slots.add(new ImportConfig());

        for (int i = 0; i < 27; i++) {
            if (!Slot.hasSlot(slots, i)) {
                slots.add(Slot.getGlass(i));
            }
        }
        return slots;
    }

    private class ImportLuckPerm extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.EMERALD);
            item.setName("&bFrom Luck Perms" + (plugin.getServer().getPluginManager().getPlugin("LuckPerms") == null ? " &7[&cNot available&7]" : ""));
            if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") == null) {
                item.addLoreLine(" ");
                item.addLoreLine("&cThis option is currently not available.");
                item.addLoreLine("&cWe couldn't locate &7'&c&lLuckPerms&7' &cplugin");
                item.addLoreLine("&cin your plugin list.");
                item.addLoreLine(" ");
            } else {

            }
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 10;
        }
    }

    private class ImportPermissionsEx extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.BLAZE_POWDER);
            item.setName("&bFrom PermissionsEx" + (plugin.getServer().getPluginManager().getPlugin("PermissionsEx") == null ? " &7[&cNot available&7]" : ""));
            if (plugin.getServer().getPluginManager().getPlugin("PermissionsEx") == null) {
                item.addLoreLine(" ");
                item.addLoreLine("&cThis option is currently not available.");
                item.addLoreLine("&cWe couldn't locate &7'&c&lPermissionsEx&7' &cplugin");
                item.addLoreLine("&cin your plugin list.");
                item.addLoreLine(" ");
            } else {
                item.addLoreLine("&bOption I&7:");
                item.addLoreLine("    &aThis will delete all ranks from data base");
                item.addLoreLine("    &aand import all ranks from &bPermissionsEx&a.");
                item.addLoreLine(" ");
                item.addLoreLine("&bLeft click if you would");
                item.addLoreLine("&blike to request this.");
                item.addLoreLine(" ");
                item.addLoreLine("&bOption II&7:");
                item.addLoreLine("    &aThis will replace or create all user's data and import");
                item.addLoreLine("    &adata from &bPermissionsEx&a.");
                item.addLoreLine("");
                item.addLoreLine("&c&oNote:");
                item.addLoreLine("    &cIf you want ranks to be applied, you should");
                item.addLoreLine("    &cimport ranks first&c&l!");
                item.addLoreLine("    &4This will kick all online players!");
                item.addLoreLine(" ");
                item.addLoreLine("&bRight click if you would");
                item.addLoreLine("&blike to request this.");
                item.addLoreLine(" ");
            }
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 13;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            if (plugin.getServer().getPluginManager().getPlugin("PermissionsEx") == null) {
                return;
            }
            player.closeInventory();
            if (clickType == ClickType.LEFT) {
                player.closeInventory();
                Tasks.runAsync(plugin, () -> {
                    player.sendMessage(Color.translate("&aPlease wait."));

                    plugin.getImportManagement().importPermissionEx();
                    plugin.getRankManagement().saveRanks();

                    player.sendMessage(Color.translate(Language.PREFIX + "&aRanks have been successfully imported and loaded on every server!"));
                });
            } else if (clickType == ClickType.RIGHT) {
                player.closeInventory();
                String name = player.getName();

                Tasks.runLater(plugin, () -> {
                    for (Player online : Utilities.getOnlinePlayers()) {
                        online.kickPlayer(Color.translate("&cKicked due to users importing. Try again later."));
                    }
                    Tasks.runAsync(plugin, () -> {
                        plugin.getImportManagement().setImportingUsersPlayer(name);
                        Bukkit.getConsoleSender().sendMessage(Color.translate("&aPlease wait."));
                        plugin.getImportManagement().importPermissionExUsers();
                        Bukkit.getConsoleSender().sendMessage(Color.translate(Language.PREFIX + "&aUser's data successfully loaded.!"));
                    });
                }, 10L);
            }
        }
    }

    private class ImportConfig extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.PAPER);
            item.setName("&bFrom config &7(&3ranks.yml&7)");
            item.addLoreLine("&aThis will delete all ranks from data base");
            item.addLoreLine("&aand import all ranks from &branks.yml&a.");
            item.addLoreLine(" ");
            item.addLoreLine("&bPlease click if you would");
            item.addLoreLine("&blike to request this.");
            item.addLoreLine(" ");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 16;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            Tasks.runAsync(plugin, () -> {
                player.sendMessage(Color.translate("&aPlease wait."));

                plugin.setRanks(new ConfigFile(plugin, "ranks.yml"));

                plugin.getRankManagement().getRanks().clear();
                plugin.getMongoManager().getRanks().drop();

                plugin.getRankManagement().importRanks();

                plugin.getRankManagement().saveRanks();
                plugin.getRankManagement().requestRankUpdate();

                player.sendMessage(Color.translate(Language.PREFIX + "&aRanks have been successfully imported and loaded on every server!"));
            });
        }
    }
}
