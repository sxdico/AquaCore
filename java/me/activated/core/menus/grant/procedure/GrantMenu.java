package me.activated.core.menus.grant.procedure;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.activated.core.api.player.PlayerData;
import me.activated.core.api.rank.RankData;
import me.activated.core.data.grant.GrantProcedure;
import me.activated.core.data.grant.GrantProcedureState;
import me.activated.core.enums.Language;
import me.activated.core.menu.menu.SwitchableMenu;
import me.activated.core.menu.slots.Slot;
import me.activated.core.utilities.chat.Replacement;
import me.activated.core.utilities.general.Tasks;
import me.activated.core.utilities.general.WoolUtil;
import me.activated.core.utilities.item.ItemBuilder;
import me.activated.core.menus.slots.PlayerInfoSlot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@RequiredArgsConstructor
public class GrantMenu extends SwitchableMenu {
    private final PlayerData target;

    @Override
    public void onClose(Player player) {
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        if (playerData.getGrantProcedure() != null && playerData.getGrantProcedure().getGrantProcedureState() == GrantProcedureState.START) {
            playerData.setGrantProcedure(null);
        }
        plugin.getPlayerManagement().deleteData(target.getUniqueId());
    }

    @Override
    public void onOpen(Player player) {
        this.setUpdateInTask(true);
    }

    @Override
    public String getPagesTitle(Player player) {
        return "&7Ranks";
    }

    @Override
    public List<Slot> getSwitchableSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        plugin.getRankManagement().getRanks().stream().sorted(Comparator.comparingInt(RankData::getWeight).reversed()).forEach(rankData -> {
            slots.add(new RankButton(rankData, target));
        });

        return slots;
    }

    @Override
    public List<Slot> getEveryMenuSlots(Player player) {
        List<Slot> slots = new ArrayList<>();
        slots.add(new PlayerInfoSlot(target, 4));
        slots.add(new Slot() {

            @Override
            public ItemStack getItem(Player player) {
                ItemBuilder item = new ItemBuilder(Material.PAPER);
                item.setName("&aGrants");
                item.addLoreLine(" ");
                item.addLoreLine("&7Click to show");
                item.addLoreLine("&b" + target.getPlayerName() + "'s &7grants!");
                item.addLoreLine(" ");
                return item.toItemStack();
            }

            @Override
            public int getSlot() {
                return 40;
            }

            @Override
            public void onClick(Player player, int slot, ClickType clickType) {
                Tasks.run(plugin, () -> player.performCommand("grants " + target.getPlayerName()));
            }
        });
        return slots;
    }

    @AllArgsConstructor
    private class RankButton extends Slot {
        private final RankData rankData;
        private final PlayerData playerData;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setName(rankData.getDisplayName());
            item.setDurability(rankData.isDefaultRank() ? 4 : WoolUtil.convertChatColorToWoolData(rankData.getColor()));
            plugin.getCoreConfig().getStringList("grant-rank-lore").forEach(message -> {
                Replacement replacement = new Replacement(message).add("<rank>", rankData.getDisplayName())
                        .add("<player>", playerData.getHighestRank().getColor() + playerData.getPlayerName());
                item.addLoreLine(replacement.toString());
            });
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            if (rankData.isDefaultRank()) {
                player.sendMessage(Language.GRANT_PROCEDURE_CANT_GRANT_DEFAULT.toString());
                return;
            }
            if (playerData.hasRank(rankData)) {
                player.sendMessage(Language.GRANT_PROCEDURE_ALREADY_HAVE_RANK.toString().replace("<player>", playerData.getPlayerName()));
                return;
            }
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            if (!plugin.getRankManagement().canGrant(playerData, rankData) && !player.hasPermission("Aqua.grant.all")) {
                player.sendMessage(Language.GRANT_PROCEDURE_CANT_GRANT.toString());
                return;
            }
            if (!player.isOp() && player.hasPermission("Aqua.grant.disallow." + rankData.getName().toLowerCase())) {
                player.sendMessage(Language.GRANT_PROCEDURE_CANT_GRANT_DISALLOWED.toString());
                return;
            }
            playerData.setGrantProcedure(new GrantProcedure(this.playerData));
            playerData.getGrantProcedure().setRankName(rankData.getName());
            playerData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.SERVER_CHOOSE);

            new ChooseGrantServerMenu().open(player);
        }
    }
}
