package me.activated.core.menus.tags;

import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.menu.menu.AquaMenu;
import me.activated.core.menu.slots.Slot;
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

public class TagsMainMenu extends AquaMenu {

    @Override
    public String getName(Player player) {
        return "&7Tags";
    }

    @Override
    public List<Slot> getSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        slots.add(new TagsSlot());
        slots.add(new ResetTagSlot());
        slots.add(new StoreSlot());
        slots.add(new ChooseColorSlot());

        for (int i = 0; i < 45; i++) {
            if (!Slot.hasSlot(slots, i)) {
                slots.add(Slot.getGlass(i));
            }
        }

        return slots;
    }

    private class TagsSlot extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.CHEST);
            item.setName("&aTags");
            item.addLoreLine(" ");
            item.addLoreLine("&7Click to see all");
            item.addLoreLine("&7server's tags!");
            item.addLoreLine(" ");
            return item.toItemStack();
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            if (plugin.getTagManagement().getTags().size() == 0) return;
            new TagsMenu().open(player);
        }

        @Override
        public int getSlot() {
            return 19;
        }
    }

    private class StoreSlot extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.PAPER);
            item.setName("&aStore");
            item.addLoreLine("");
            item.addLoreLine("&7Want to purchase tags?");
            item.addLoreLine("&7Head over to: &b" + plugin.getEssentialsManagement().getStore());
            item.addLoreLine(" ");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 22;
        }
    }

    private class ChooseColorSlot extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            ItemBuilder item = new ItemBuilder(Material.WOOL);
            if (playerData.getTagColor() != null && !playerData.getTagColor().equals("")) {
                item.setDurability(WoolUtil.convertChatColorToWoolData(ChatColor.valueOf(playerData.getTagColor())));
            }
            item.setName("&aChange color");
            item.addLoreLine(" ");
            item.addLoreLine("&7Click to customize");
            item.addLoreLine("&7your tag color!");
            item.addLoreLine(" ");
            return item.toItemStack();
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            if (playerData.getTag() == null) {
                player.sendMessage(Language.TAGS_DONT_HAVE_APPLIED.toString());
                return;
            }
            new TagsColorMenu().open(player);
        }

        @Override
        public int getSlot() {
            return 25;
        }
    }

    private class ResetTagSlot extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.INK_SACK);
            item.setDurability(1);
            item.setName("&aReset tag");
            item.addLoreLine(" ");
            item.addLoreLine("&7Click to reset");
            item.addLoreLine("&7your tag!");
            item.addLoreLine(" ");
            return item.toItemStack();
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            if (playerData.getTag() == null) {
                player.playSound(player.getLocation(), Sound.DIG_GRASS, 20F, 0.1F);
                player.sendMessage(Language.TAGS_DONT_HAVE_APPLIED.toString());
                return;
            }
            player.closeInventory();
            playerData.setTag("");
            player.sendMessage(Language.TAGS_TAG_REMOVE.toString());
        }

        @Override
        public int getSlot() {
            return 12;
        }

        @Override
        public int[] getSlots() {
            return new int[]{13,14,21,23,30,31,32};
        }
    }
}
