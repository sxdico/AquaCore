package me.activated.core.menus.grant.procedure;

import lombok.AllArgsConstructor;
import me.activated.core.api.player.GlobalPlayer;
import me.activated.core.api.player.PlayerData;
import me.activated.core.api.rank.RankData;
import me.activated.core.api.rank.grant.Grant;
import me.activated.core.data.grant.GrantProcedure;
import me.activated.core.data.grant.GrantSerilization;
import me.activated.core.database.redis.other.bson.JsonChain;
import me.activated.core.database.redis.payload.action.JedisAction;
import me.activated.core.enums.Language;
import me.activated.core.events.PlayerGrantEvent;
import me.activated.core.menu.menu.AquaMenu;
import me.activated.core.menu.slots.Slot;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.general.Tasks;
import me.activated.core.utilities.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GrantConfirmMenu extends AquaMenu {

    @Override
    public List<Slot> getSlots(Player player) {
        List<Slot> slots = new ArrayList<>();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        for (int i = 0; i < 27; i++) {
            if (Arrays.asList(0, 1, 2, 9, 10, 11, 18, 19, 20).contains(i)) {
                slots.add(new AbortSlot(i));
            }
            if (Arrays.asList(6, 7, 8, 15, 16, 17, 24, 25, 26).contains(i)) {
                slots.add(new ConfirmSlot(playerData.getGrantProcedure(), i));
            }
        }

        slots.add(new InfoSlot(playerData.getGrantProcedure(), 13));

        return slots;
    }

    @Override
    public String getName(Player player) {
        return "&7Confirm Grant";
    }

    @Override
    public void onClose(Player player) {
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        if (playerData.getGrantProcedure() != null) {
            plugin.getPlayerManagement().deleteData(playerData.getGrantProcedure().getTargetData().getUniqueId());
        }
    }

    @AllArgsConstructor
    private class InfoSlot extends Slot {
        private final GrantProcedure grantProcedure;
        private final int slot;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.PAPER);
            item.setDurability(5);
            item.setName("&b&lGrant Information");
            item.addLoreLine(" ");
            item.addLoreLine("&aRank&7: &f" + grantProcedure.getRankName());
            item.addLoreLine("&aTarget&7: &f" + grantProcedure.getTargetData().getPlayerName());
            item.addLoreLine("&aDuration&7: &f" + (!grantProcedure.isPermanent() ? grantProcedure.getNiceDuration() : "Permanent"));
            item.addLoreLine("&aReason&7: &f" + grantProcedure.getEnteredReason());
            item.addLoreLine(" ");
            RankData rankData = plugin.getRankManagement().getRank(grantProcedure.getRankName());
            item.addLoreLine("&aRank to set&7: &f" + (rankData != null ? rankData.getDisplayName() : grantProcedure.getRankName()));
            item.addLoreLine("&aCurrent rank&7: &f" + grantProcedure.getTargetData().getHighestRank().getDisplayName());
            item.addLoreLine(" ");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return slot;
        }
    }

    @AllArgsConstructor
    private class AbortSlot extends Slot {

        private final int slot;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.STAINED_CLAY);
            item.setDurability(14);
            item.setName("&c&lCancel");
            item.setLore("&cClick to abort", "&cthis grant procedure!");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return slot;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            player.closeInventory();
        }
    }

    @AllArgsConstructor
    private class ConfirmSlot extends Slot {
        private final GrantProcedure grantProcedure;
        private final int slot;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.STAINED_CLAY);
            item.setDurability(5);
            item.setName("&a&lConfirm");
            item.setLore("&aClick to confirm", "&athis grant procedure!");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return slot;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            RankData rankData = plugin.getRankManagement().getRank(grantProcedure.getRankName());

            if (rankData == null) {
                player.closeInventory();
                player.sendMessage(Language.RANK_NOT_EXISTS.toString().replace("<rank>", grantProcedure.getRankName()));
                return;
            }

            Grant grant = new Grant();
            grant.setRankName(rankData.getName());
            grant.setActive(true);
            grant.setServer(grantProcedure.getServer());
            grant.setAddedAt(System.currentTimeMillis());
            grant.setAddedBy(player.getName());
            grant.setDuration(grantProcedure.getEnteredDuration());
            grant.setPermanent(grantProcedure.isPermanent());
            grant.setReason(grantProcedure.getEnteredReason());
            player.closeInventory();

            PlayerGrantEvent event = new PlayerGrantEvent(grant, grantProcedure.getTargetData(), player);
            plugin.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) return;

            Tasks.runAsync(plugin, () -> {
                PlayerData targetData = plugin.getPlayerManagement().getPlayerData(grantProcedure.getTargetData().getUniqueId());
                if (targetData == null) {
                    targetData = plugin.getPlayerManagement().loadData(grantProcedure.getTargetData().getUniqueId());
                    if (targetData != null) {
                        targetData.loadData();
                    } else {
                        targetData = plugin.getPlayerManagement().createPlayerData(grantProcedure.getTargetData().getUniqueId(), grantProcedure.getTargetData().getPlayerName());
                    }
                }
                targetData.getGrants().add(grant);

                Utilities.playSound(player, Sound.LEVEL_UP);
                GlobalPlayer globalPlayer = plugin.getServerManagement().getGlobalPlayer(grantProcedure.getTargetData().getPlayerName());
                if (grant.isPermanent()) {
                    player.sendMessage(Language.GRANT_RANK_GRANTED_PERM.toString()
                            .replace("<rank>", grant.getRankName())
                            .replace("<user>", grantProcedure.getTargetData().getPlayerName()));
                    if (globalPlayer != null) {
                        globalPlayer.sendMessage(Language.GRANT_RANK_GRANTED_PERM_TARGET.toString()
                                .replace("<rank>", grant.getRankName())
                                .replace("<user>", grantProcedure.getTargetData().getPlayerName()));
                    }
                } else {
                    player.sendMessage(Language.GRANT_RANK_GRANTED_TEMP.toString()
                            .replace("<rank>", grant.getRankName())
                            .replace("<time>", grant.getNiceDuration())
                            .replace("<user>", grantProcedure.getTargetData().getPlayerName()));
                    if (globalPlayer != null) {
                        globalPlayer.sendMessage(Language.GRANT_RANK_GRANTED_TEMP_TARGET.toString()
                                .replace("<rank>", grant.getRankName())
                                .replace("<time>", grant.getNiceDuration())
                                .replace("<user>", grantProcedure.getTargetData().getPlayerName()));
                    }
                }
                targetData.saveData();

                Player target = Bukkit.getPlayer(targetData.getPlayerName());
                if (target == null) {
                    plugin.getRedisData().write(JedisAction.GRANTS_UPDATE,
                            new JsonChain().addProperty("name", targetData.getPlayerName())
                                    .addProperty("grants", GrantSerilization.serilizeGrants(targetData.getGrants())).get());
                } else {
                    PlayerData playerData = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());
                    playerData.loadAttachments(target);
                }
                plugin.getPlayerManagement().deleteData(targetData.getUniqueId());
            });
        }
    }
}
