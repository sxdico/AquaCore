package me.activated.core.commands.essentials.staff;

import me.activated.core.punishments.player.PunishPlayerData;
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

public class IPsCommand extends BaseCommand {

    @Command(name = "getips", inGameOnly = false, permission = "Aqua.command.ips", aliases = "getip")
    public void onCommand(CommandArgs command) {
        if (command.getSender() instanceof Player) {
            command.getSender().sendMessage(Color.translate("&cThis is for a console only!"));
            return;
        }
        Tasks.runAsync(plugin, () -> {
            CommandSender player = command.getSender();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Color.translate("&cCorrect usage: /getip <player>"));
                return;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.getPlayerManagement().getFixedName(args[0]));
            if (target.isOnline()) {
                PunishPlayerData targetData = plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());

                player.sendMessage(Color.translate("&7&m-----------------------------------------"));
                player.sendMessage(Color.translate("&e" + targetData.getPlayerName() + "'s &cIPs"));
                player.sendMessage(" ");
                player.sendMessage(Color.translate("&eLast IP&7: &c" + targetData.getAddress()));
                player.sendMessage(Color.translate("&eAll recorded IPs&7:"));
                targetData.getAddresses().forEach(address -> {
                    player.sendMessage(Color.translate("&7- &c" + address));
                });
                player.sendMessage(Color.translate("&7&m-----------------------------------------"));
            } else {
                player.sendMessage(Language.LOADING_OFFLINE_DATA.toString());

                PunishPlayerData targetData = plugin.getPunishmentPlugin().getProfileManager().loadData(target.getUniqueId());

                if (targetData == null) {
                    player.sendMessage(Language.DOESNT_HAVE_DATA.toString());
                    return;
                }
                targetData.load();

                player.sendMessage(Color.translate("&7&m-----------------------------------------"));
                player.sendMessage(Color.translate("&e" + targetData.getPlayerName() + "'s &cIPs"));
                player.sendMessage(" ");
                player.sendMessage(Color.translate("&eLast IP&7: &c" + targetData.getAddress()));
                player.sendMessage(Color.translate("&eAll recorded IPs&7:"));
                targetData.getAddresses().forEach(address -> {
                    player.sendMessage(Color.translate("&7- &c" + address));
                });
                player.sendMessage(Color.translate("&7&m-----------------------------------------"));
                plugin.getPunishmentPlugin().getProfileManager().unloadData(target.getUniqueId());
            }
        });
    }
}
