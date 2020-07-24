package me.activated.core.menus.color;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.menu.menu.SwitchableMenu;
import me.activated.core.menu.slots.Slot;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.ColorUtil;
import me.activated.core.utilities.general.WoolUtil;
import me.activated.core.utilities.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class NameColorMenu extends SwitchableMenu {

    @Override
    public String getPagesTitle(Player player) {
        return "&7Name Color";
    }

    @Override
    public List<Slot> getSwitchableSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        Stream.of(ChatColor.values()).filter(ChatColor::isColor).forEach(chatColor -> {
            slots.add(new NameColorSlot(chatColor, player.hasPermission("Aqua.name.color." + ColorUtil.convertChatColor(chatColor).toLowerCase())
                    || player.hasPermission("Aqua.name.color.all")));
        });
        return slots;
    }

    @Override
    public List<Slot> getEveryMenuSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        slots.add(new Slot() {

            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.REDSTONE).setName("&cReset color!").toItemStack();
            }

            @Override
            public int getSlot() {
                return 31;
            }

            @Override
            public void onClick(Player player, int slot, ClickType clickType) {
                PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

                playerData.setNameColor(null);
                playerData.setNameColorItalic(false);
                playerData.setNameColorBold(false);

                player.closeInventory();
                Utilities.playSound(player, Sound.LEVEL_UP);
                player.sendMessage(Language.NAME_COLOR_COLOR_RESET.toString().replace("<color>", playerData.getHighestRank().getColor() + ColorUtil.convertChatColor(playerData.getHighestRank().getColor(), true)));
            }
        });

        slots.add(new NameColorBold());
        slots.add(new NameColorItalic());

        return slots;
    }

    @AllArgsConstructor
    private class NameColorSlot extends Slot {
        private final ChatColor chatColor;
        boolean hasPermission;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setName(chatColor + ColorUtil.convertChatColor(chatColor, true));
            item.setDurability(WoolUtil.convertChatColorToWoolData(chatColor));
            if (hasPermission) {
                item.addLoreLine("&8&m--------------------------------");
                item.addLoreLine("&7Click to set your name color to " + chatColor + ColorUtil.convertChatColor(chatColor, true) + "&7!");
                item.addLoreLine("&8&m--------------------------------");
            } else {
                item.addLoreLine("&8&m--------------------------------");
                item.addLoreLine(" ");
                item.addLoreLine("&cYou don't have permission to use");
                item.addLoreLine("&cthis name color, donate to use");
                item.addLoreLine("&cthis feature: &c&l" + plugin.getEssentialsManagement().getStore());
                item.addLoreLine(" ");
                item.addLoreLine("&8&m--------------------------------");
            }
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            if (!hasPermission) return;

            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            playerData.setNameColor(chatColor);

            player.closeInventory();
            Utilities.playSound(player, Sound.LEVEL_UP);

            player.sendMessage(Language.NAME_COLOR_COLOR_CHANGED.toString().replace("<color>", chatColor + ColorUtil.convertChatColor(chatColor, true)));
        }
    }

    private class NameColorBold extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            ItemBuilder item = new ItemBuilder(Material.FEATHER);
            item.setName(playerData.getNameColor() + "Name Color Bold &7(" + (playerData.isNameColorBold() ? "&aEnabled" : "&cDisabled") + "&7)");
            if (!player.hasPermission("Aqua.name.color.bold")) {
                item.addLoreLine("&8&m--------------------------------");
                item.addLoreLine(" ");
                item.addLoreLine("&cYou don't have permission to use");
                item.addLoreLine("&cthis name color, donate to use");
                item.addLoreLine("&cthis feature: &c&l" + plugin.getEssentialsManagement().getStore());
                item.addLoreLine(" ");
                item.addLoreLine("&8&m--------------------------------");
            } else if (playerData.getNameColor() == null || playerData.getNameColor().equals(playerData.getHighestRank().getDisplayColor())) {
                item.addLoreLine("");
                item.addLoreLine("&cYou must choose color");
                item.addLoreLine("&cif you want to use this option!");
                item.addLoreLine(" ");
            } else if (!playerData.isNameColorBold()) {
                item.addLoreLine("");
                item.addLoreLine("&7Click to set your");
                item.addLoreLine("&7name bold!");
                item.addLoreLine(" ");
            } else {
                item.addLoreLine("");
                item.addLoreLine("&7Click to un-set your");
                item.addLoreLine("&7name bold!");
                item.addLoreLine(" ");
            }
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 39;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            if (!player.hasPermission("Aqua.name.color.bold")) return;

            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            if (playerData.getNameColor() == null || playerData.getNameColor().equals(playerData.getHighestRank().getDisplayColor())) {
                return;
            }
            playerData.setNameColorBold(!playerData.isNameColorBold());
            Utilities.playSound(player, Sound.LEVEL_UP);

            update(player);
        }
    }

    private class NameColorItalic extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            ItemBuilder item = new ItemBuilder(Material.FEATHER);
            item.setName(playerData.getNameColor() + "Name Color Italic &7(" + (playerData.isNameColorItalic() ? "&aEnabled" : "&cDisabled") + "&7)");
            if (!player.hasPermission("Aqua.name.color.italic")) {
                item.addLoreLine("&8&m--------------------------------");
                item.addLoreLine(" ");
                item.addLoreLine("&cYou don't have permission to use");
                item.addLoreLine("&cthis name color, donate to use");
                item.addLoreLine("&cthis feature: &c&l" + plugin.getEssentialsManagement().getStore());
                item.addLoreLine(" ");
                item.addLoreLine("&8&m--------------------------------");
            } else if (playerData.getNameColor() == null || playerData.getNameColor().equals(playerData.getHighestRank().getDisplayColor())) {
                item.addLoreLine("");
                item.addLoreLine("&cYou must choose color");
                item.addLoreLine("&cif you want to use this option!");
                item.addLoreLine(" ");
            } else if (!playerData.isNameColorItalic()) {
                item.addLoreLine("");
                item.addLoreLine("&7Click to set your");
                item.addLoreLine("&7name italic!");
                item.addLoreLine(" ");
            } else {
                item.addLoreLine("");
                item.addLoreLine("&7Click to un-set your");
                item.addLoreLine("&7name italic!");
                item.addLoreLine(" ");
            }
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 41;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            if (!player.hasPermission("Aqua.name.color.italic")) return;

            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            if (playerData.getNameColor() == null || playerData.getNameColor().equals(playerData.getHighestRank().getDisplayColor())) {
                return;
            }
            playerData.setNameColorItalic(!playerData.isNameColorItalic());
            Utilities.playSound(player, Sound.LEVEL_UP);

            update(player);
        }
    }
}
