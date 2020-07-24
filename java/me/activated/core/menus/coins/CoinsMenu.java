package me.activated.core.menus.coins;

import me.activated.core.api.player.PlayerData;
import me.activated.core.api.rank.RankData;
import me.activated.core.enums.Language;
import me.activated.core.menu.menu.AquaMenu;
import me.activated.core.menu.slots.Slot;
import me.activated.core.utilities.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CoinsMenu extends AquaMenu {

    @Override
    public List<Slot> getSlots(Player player) {
        List<Slot> slots = new ArrayList<>();
        slots.add(new InfoSlot());
        slots.add(new BuySlot());

        for (int i = 0; i < 27; i++) {
            if (!Slot.hasSlot(slots, i)) {
                slots.add(Slot.getGlass(i));
            }
        }
        return slots;
    }

    @Override
    public String getName(Player player) {
        return "&bCoins &7[&a$$&7]";
    }

    private class InfoSlot extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM);
            item.setDurability(3);
            item.setName("&bAbout " + playerData.getNameWithColor());
            item.setSkullOwner(player.getName());
            item.addLoreLine(" ");
            item.addLoreLine("&bCurrent coins&7: &3" + playerData.getCoins());
            item.addLoreLine("&bYou can purchase a total of &3" +
                    playerData.getPurchasableRanks().size() + " ranks&b.");
            item.addLoreLine(" ");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 11;
        }

        @Override
        public int[] getSlots() {
            return new int[]{15};
        }
    }

    private class BuySlot extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.INK_SACK).setDurability(1);
            item.setName("&bPurchase Ranks");
            item.addLoreLine(" ");
            item.addLoreLine("&7Click to purchase temporary");
            item.addLoreLine("&7ranks using your coins!");
            item.addLoreLine(" ");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 13;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            if (plugin.getRankManagement().getRanks().stream().filter(RankData::isPurchasable).collect(Collectors.toList()).size() == 0) {
                player.sendMessage(Language.COINS_NO_RANKS_TO_PURCHASE.toString());
                return;
            }
            if (playerData.getPurchasableRanks().size() == 0) {
                player.sendMessage(Language.COINS_DONT_HAVE_ENOUGH.toString());
                return;
            }
            new CoinsRankMenu().open(player);
        }
    }
}
