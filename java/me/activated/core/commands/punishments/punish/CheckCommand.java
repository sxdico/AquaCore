package me.activated.core.commands.punishments.punish;

import me.activated.core.punishments.menus.CheckMenu;
import me.activated.core.punishments.player.PunishPlayerData;
import me.activated.core.enums.PunishmentsLanguage;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CheckCommand extends BaseCommand {

    @Command(name = "check", permission = "punishments.command.check", aliases = {"c", "cpunishments", "checkpunishments"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        Tasks.runAsync(plugin, () -> {
            if (args.length == 0) {
                player.sendMessage(Color.translate("&cUsage: /" + command.getLabel() + " <player>"));
                return;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.getPunishmentPlugin().getProfileManager().correctName(args[0]));

            PunishPlayerData targetData = plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());

            if (targetData == null || !target.isOnline()) {
                player.sendMessage(Color.translate("&aPlease wait..."));
                plugin.getPunishmentPlugin().getProfileManager().createPlayerDate(target.getUniqueId(), target.getName());
                targetData = plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());

                if (!targetData.hasPlayedBefore()) {
                    player.sendMessage(PunishmentsLanguage.HAVENT_PLAYED_BEFORE.toString());
                    plugin.getPunishmentPlugin().getProfileManager().unloadData(target.getUniqueId());
                    return;
                }

                targetData.getPunishData().load();
                targetData.load();
            }
            new CheckMenu(targetData.getPunishData()).open(player);
        });
    }
}
