package me.activated.core.commands.rank;

import me.activated.core.api.player.PlayerData;
import me.activated.core.menus.grant.GrantsMenu;
import me.activated.core.enums.Language;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantsCommand extends BaseCommand {

    @Command(name = "grants", permission = "Aqua.command.grants", inGameOnly = false)
    public void onCommand(CommandArgs command) {
        if (!(command.getSender() instanceof Player)) {
            String[] args = command.getArgs();
            CommandSender sender = command.getSender();

            if (args.length == 0) {
                sender.sendMessage(Color.translate("&cCorrect usage: /grants <player>"));
                return;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.getPlayerManagement().getFixedName(args[0]));
            if (target.isOnline()) {
                PlayerData targetData = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());

                targetData.getActiveGrants().forEach(grant -> {
                    sender.sendMessage(Color.translate(grant.getRank().getDisplayName() + " &7- &7(&bExpire&7: &3" + grant.getNiceExpire() + "&7) &7(&bBy&7: &3" + grant.getAddedBy() + "&7) &7(&bReason&7: &3" + grant.getReason() + "&7)"));
                });
            } else {
                sender.sendMessage(Language.LOADING_OFFLINE_DATA.toString());
                PlayerData targetData = plugin.getPlayerManagement().loadData(target.getUniqueId());

                if (targetData == null) {
                    sender.sendMessage(Language.DOESNT_HAVE_DATA.toString());
                    return;
                }
                targetData.loadData();

                targetData.getActiveGrants().forEach(grant -> {
                    sender.sendMessage(Color.translate(grant.getRank().getDisplayName() + " &7- &7(&bExpire&7: &3" + grant.getNiceExpire() + "&7) &7(&bBy&7: &3" + grant.getAddedBy() + "&7) &7(&bReason&7: &3" + grant.getReason() + "&7)"));
                });
                plugin.getPlayerManagement().deleteData(targetData.getUniqueId());
            }
            return;
        }
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Color.translate("&cCorrect usage: /grants <player>"));
                return;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.getPlayerManagement().getFixedName(args[0]));
            if (target.isOnline()) {
                PlayerData targetData = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());

                new GrantsMenu(targetData).open(player);
            } else {
                player.sendMessage(Language.LOADING_OFFLINE_DATA.toString());
                PlayerData targetData = plugin.getPlayerManagement().loadData(target.getUniqueId());

                if (targetData == null) {
                    player.sendMessage(Language.DOESNT_HAVE_DATA.toString());
                    return;
                }
                targetData.loadData();

                new GrantsMenu(targetData).open(player);
            }
        });
    }
}
