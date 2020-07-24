package me.activated.core.menus.rank;

import lombok.AllArgsConstructor;
import me.activated.core.api.rank.RankData;
import me.activated.core.menu.menu.SwitchableMenu;
import me.activated.core.menu.slots.Slot;
import me.activated.core.utilities.chat.Replacement;
import me.activated.core.utilities.general.StringUtils;
import me.activated.core.utilities.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RankListMenu extends SwitchableMenu {

    @Override
    public String getPagesTitle(Player player) {
        return "&7Ranks";
    }

    @Override
    public List<Slot> getSwitchableSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        plugin.getRankManagement().getRanks().stream().sorted(Comparator.comparingInt(RankData::getWeight)).forEach(rankData -> {
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
        private final RankData rankData;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.NETHER_STAR);
            item.setName(rankData.getDisplayName());
            plugin.getCoreConfig().getStringList("rank-list-item-lore").forEach(message -> {
                Replacement replacement = new Replacement(message);
                replacement.add("<color>", rankData.getColor().toString());
                replacement.add("<name>", rankData.getName());
                replacement.add("<prefix>", !rankData.getPrefix().equals("") ? rankData.getPrefix().replace("§", "#mMk2X2") : "None");
                replacement.add("<suffix>", !rankData.getSuffix().equals("") ? rankData.getSuffix() : "None");
                replacement.add("<inheritance>", rankData.getInheritance().size() == 0 ? "None" :
                        StringUtils.getStringFromList(rankData.getInheritance()));
                replacement.add("<totalPerms>", rankData.getPermissions().size());
                replacement.add("<weight>", rankData.getWeight());
                item.addLoreLine(replacement.toString().replace("#mMk2X2", "&"));
            });
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 0;
        }
    }
}
