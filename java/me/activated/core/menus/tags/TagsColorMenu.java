package me.activated.core.menus.tags;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.menu.menu.SwitchableMenu;
import me.activated.core.menu.slots.Slot;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.ColorUtil;
import me.activated.core.utilities.general.Tasks;
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
public class TagsColorMenu extends SwitchableMenu {

    @Override
    public String getPagesTitle(Player player) {
        return "&7Tags";
    }

    @Override
    public List<Slot> getSwitchableSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        Stream.of(ChatColor.values()).filter(ChatColor::isColor).forEach(chatColor -> {
            slots.add(new TagColorSlot(chatColor));
        });

        return slots;
    }

    @Override
    public List<Slot> getEveryMenuSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        slots.add(new Slot() {

            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.ARROW).setName("&c&lGo Back!").toItemStack();
            }

            @Override
            public void onClick(Player player, int slot, ClickType clickType) {
                new TagsMainMenu().open(player);
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
    private class TagColorSlot extends Slot {
        private final ChatColor chatColor;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setName(chatColor + ColorUtil.convertChatColor(chatColor, true));
            item.setDurability(WoolUtil.convertChatColorToWoolData(chatColor));
            item.addLoreLine("&8&m--------------------------------");
            item.addLoreLine("&7Click to set your tag color to " + chatColor + ColorUtil.convertChatColor(chatColor, true) + "&7!");
            item.addLoreLine("&8&m--------------------------------");
            return item.toItemStack();
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            playerData.setTagColor(ColorUtil.convertChatColor(chatColor));

            player.closeInventory();
            Utilities.playSound(player, Sound.LEVEL_UP);

            player.sendMessage(Language.TAGS_COLOR_APPLIED.toString().replace("<color>", chatColor + ColorUtil.convertChatColor(chatColor, true)));
            Tasks.runAsync(plugin, playerData::saveData);
        }

        @Override
        public int getSlot() {
            return 0;
        }
    }
}
