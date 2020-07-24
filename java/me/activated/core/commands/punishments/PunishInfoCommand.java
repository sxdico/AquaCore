package me.activated.core.commands.punishments;

import me.activated.core.punishments.PunishmentPlugin;
import me.activated.core.punishments.player.PunishPlayerData;
import me.activated.core.punishments.utilities.punishments.Punishment;
import me.activated.core.punishments.utilities.punishments.PunishmentType;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.chat.Replacement;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PunishInfoCommand extends BaseCommand {

    @Command(name = "punishinfo", aliases = {"pinfo", "punishi", "baninfo", "warninfo", "warns"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        PunishPlayerData playerData = plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(player.getUniqueId());

        if (args.length == 0) {
            plugin.getPunishmentPlugin().getConfigFile().getStringList("PUNISH-INFO-COMMAND-FORMAT").forEach(message -> {
                Replacement replacement = new Replacement(message);

                replacement.add("<bans>", playerData.getPunishData().getPunishments(PunishmentType.BAN).size());
                replacement.add("<mutes>", playerData.getPunishData().getPunishments(PunishmentType.MUTE).size());
                replacement.add("<blacklists>", playerData.getPunishData().getPunishments(PunishmentType.BLACKLIST).size());
                replacement.add("<kicks>", playerData.getPunishData().getPunishments(PunishmentType.KICK).size());
                replacement.add("<warns>", playerData.getPunishData().getPunishments(PunishmentType.WARN).size());
                replacement.add("<name>", player.getName());

                player.sendMessage(replacement.toString());
            });
            return;
        }
        if (args[0].equalsIgnoreCase("warns")) {
            plugin.getPunishmentPlugin().getConfigFile().getStringList("PUNISH-INFO-COMMAND-WARNS-FORMAT").forEach(message -> {
                if (!message.contains("{warns}")) {
                    player.sendMessage(message.replace("<name>", player.getName()));
                } else {
                    List<Punishment> warns = playerData.getPunishData().getPunishments(PunishmentType.WARN).stream().filter(punishment -> !punishment.hasExpired()).collect(Collectors.toList());

                    if (warns.size() == 0) {
                        player.sendMessage(Color.translate("&7- &cNone"));
                    } else {
                        AtomicInteger order = new AtomicInteger(1);
                        warns.forEach(warn -> {
                            player.sendMessage(Color.translate("&7- &f#" + order.getAndIncrement() + PunishmentPlugin.MAIN_COLOR + " " + player.getName() + PunishmentPlugin.SECONDARY_COLOR + " was warned by "
                            + PunishmentPlugin.MAIN_COLOR + warn.getAddedBy() + " &7(" + PunishmentPlugin.SECONDARY_COLOR + warn.getReason() + "&7) &7(" +
                                    PunishmentPlugin.SECONDARY_COLOR + warn.getNiceExpire() + "&7)"));
                        });
                    }
                }
            });
            return;
        }
        if (args[0].equalsIgnoreCase("mutes")) {
            plugin.getPunishmentPlugin().getConfigFile().getStringList("PUNISH-INFO-COMMAND-MUTES-FORMAT").forEach(message -> {
                if (!message.contains("{mutes}")) {
                    player.sendMessage(message.replace("<name>", player.getName()));
                } else {
                    List<Punishment> mutes = playerData.getPunishData().getPunishments(PunishmentType.MUTE).stream().filter(punishment -> !punishment.hasExpired()).collect(Collectors.toList());

                    if (mutes.size() == 0) {
                        player.sendMessage(Color.translate("&7- &cNone"));
                    } else {
                        AtomicInteger order = new AtomicInteger(1);
                        mutes.forEach(mute -> {
                            player.sendMessage(Color.translate("&7- &f#" + order.getAndIncrement() + PunishmentPlugin.MAIN_COLOR + " " + player.getName() + PunishmentPlugin.SECONDARY_COLOR + " was muted by "
                                    + PunishmentPlugin.MAIN_COLOR + mute.getAddedBy() + " &7(" + PunishmentPlugin.SECONDARY_COLOR + mute.getReason() + "&7) &7(" +
                                    PunishmentPlugin.SECONDARY_COLOR + mute.getNiceExpire() + "&7)"));
                        });
                    }
                }
            });
            return;
        }
        Tasks.run(plugin, () -> player.performCommand(command.getLabel()));
    }
}
