package me.activated.core.menus.coins;

import lombok.AllArgsConstructor;
import me.activated.core.api.player.PlayerData;
import me.activated.core.api.rank.RankData;
import me.activated.core.api.rank.grant.Grant;
import me.activated.core.enums.Language;
import me.activated.core.menu.menu.SwitchableMenu;
import me.activated.core.menu.slots.Slot;
import me.activated.core.utilities.general.DateUtils;
import me.activated.core.utilities.general.Tasks;
import me.activated.core.utilities.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CoinsRankMenu extends SwitchableMenu {

    @Override
    public String getPagesTitle(Player player) {
        return "&bCoins &7[&a$$&7]";
    }

    @Override
    public List<Slot> getSwitchableSlots(Player player) {
        List<Slot> slots = new ArrayList<>();
        plugin.getRankManagement().getRanks().stream().sorted(Comparator.comparingInt(RankData::getWeight).reversed()).filter(RankData::isPurchasable).forEach(rankData -> {
            slots.add(new RankSlot(rankData));
        });
        return slots;
    }

    @Override
    public List<Slot> getEveryMenuSlots(Player player) {
        return null;
    }

    @AllArgsConstructor
    private class RankSlot extends Slot {
        RankData rankData;

        @Override
        public ItemStack getItem(Player player) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            ItemBuilder item = new ItemBuilder(Material.CHEST);
            item.setName(rankData.getDisplayName());
            if (playerData.hasRank(rankData)) {
                item.addLoreLine(" ");
                item.addLoreLine("&cYou already own this");
                item.addLoreLine("&crank and can't purchase it!");
                item.addLoreLine(" ");
            } else if (rankData.getCoinsCost() > playerData.getCoins()) {
                item.addLoreLine(" ");
                item.addLoreLine("&cYou don't have enough coins");
                item.addLoreLine("&cto afford purchase of &c&l" + rankData.getName() + "&c!");
                item.addLoreLine(" ");
                item.addLoreLine("&7Keep playing server to get coins");
                item.addLoreLine("&7You still need &b" + (rankData.getCoinsCost() - playerData.getCoins()) + " coins &7to");
                item.addLoreLine("&7to purchase this rank");
                item.addLoreLine(" ");
                item.addLoreLine("&c&lNote&7: &c&lAll ranks are temporary");
                item.addLoreLine("&cand will not last forever!");
                item.addLoreLine(" ");
            } else {
                item.addLoreLine(" ");
                item.addLoreLine("&7You have enough coins to");
                item.addLoreLine("&7afford this rank.");
                item.addLoreLine(" ");
                item.addLoreLine("&7Once you purchase this you will stay");
                item.addLoreLine("&7with &b" + (playerData.getCoins() - rankData.getCoinsCost()) + " coins &7in your account!");
                item.addLoreLine(" ");
                item.addLoreLine("&bYour Coins&7: &3" + playerData.getCoins());
                item.addLoreLine("&bPrice&7: &a$" + rankData.getCoinsCost());
                item.addLoreLine("&bCurrent rank&7: " + playerData.getHighestRank().getDisplayName());
                item.addLoreLine(" ");
                item.addLoreLine("&c&lNote&7: &c&lAll ranks are temporary");
                item.addLoreLine("&cand will not last forever!");
                item.addLoreLine(" ");
                if (rankData.getWeight() < playerData.getHighestRank().getWeight()) {
                    item.addLoreLine("&9This rank has lower priority");
                    item.addLoreLine("&9than your current rank and will");
                    item.addLoreLine("&9not display as your main rank!");
                    item.addLoreLine(" ");
                }
            }
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

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            if (playerData.hasRank(rankData)) return;
            if (rankData.getCoinsCost() > playerData.getCoins()) return;

            Grant grant = new Grant();
            grant.setAddedAt(System.currentTimeMillis());
            grant.setRankName(rankData.getName());
            grant.setPermanent(false);
            long duration = -1L;
            try {
                duration = System.currentTimeMillis() - DateUtils.parseDateDiff(plugin.getCoreConfig().getString("purchasable-ranks-duration", "30d"), false);
            } catch (Exception e) {
                try {
                    duration = System.currentTimeMillis() - DateUtils.parseDateDiff("30d", false);
                } catch (Exception ignored) {

                }
            }
            grant.setDuration(duration);
            grant.setAddedBy("Console");
            grant.setReason("Purchased By Coins");
            grant.setActive(true);

            player.closeInventory();
            playerData.setCoins(playerData.getCoins() - rankData.getCoinsCost());
            playerData.getGrants().add(grant);

            player.sendMessage(Language.COINS_RANK_PURCHASED.toString()
                    .replace("<rank>", rankData.getDisplayName())
                    .replace("<coins>", String.valueOf(playerData.getCoins())));

            Tasks.runAsync(plugin, playerData::saveData);
        }
    }
}
