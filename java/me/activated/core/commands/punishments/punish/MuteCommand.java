package me.activated.core.commands.punishments.punish;

import me.activated.core.api.player.PlayerData;
import me.activated.core.data.other.punishments.PunishHistory;
import me.activated.core.punishments.player.PunishPlayerData;
import me.activated.core.enums.PunishmentsLanguage;
import me.activated.core.punishments.utilities.punishments.Punishment;
import me.activated.core.punishments.utilities.punishments.PunishmentType;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.DateUtils;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteCommand extends BaseCommand {

    @Command(name = "mute", permission = "punishments.command.mute", inGameOnly = false, aliases = "tempmute")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        Tasks.runAsync(plugin, () -> {
            if (args.length < 2) {
                sender.sendMessage(Color.translate("&cUsage: /mute <player> [optional:<duration>] <reason> [-s]"));
                return;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.getPunishmentPlugin().getProfileManager().correctName(args[0]));

            PunishPlayerData targetData = plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());

            if (targetData == null || !target.isOnline()) {
                plugin.getPunishmentPlugin().getProfileManager().createPlayerDate(target.getUniqueId(), target.getName());
                targetData = plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());
                targetData.getPunishData().load();
            }
            if (targetData.getPunishData().isMuted()) {
                sender.sendMessage(PunishmentsLanguage.MUTE_ALREADY_MUTED.toString().replace("<user>", target.getName()));
                plugin.getPunishmentPlugin().getProfileManager().unloadData(target);
                return;
            }

            long duration = -5L;
            int reasonStart = 2;
            boolean durationCorrect = false;

            if(args[1].equalsIgnoreCase("perm") || args[1].equalsIgnoreCase("permanent")) {
                duration = -5L;
            } else {
                try {
                    duration = DateUtils.parseDateDiff(args[1], true);
                    durationCorrect = true;
                } catch(Exception e) {
                    reasonStart = 1;
                }
            }
            if (reasonStart == 2 && !durationCorrect) {
                sender.sendMessage(PunishmentsLanguage.WRONG_DATE_FORMAT.toString());
                return;
            }

            StringBuilder reasonBuilder = new StringBuilder();

            for(int i = reasonStart; i < args.length; ++i) {
                reasonBuilder.append(args[i]).append(" ");
            }
            if (reasonBuilder.length() == 0) reasonBuilder.append("Muted");

            String reason = reasonBuilder.toString().trim();
            boolean silent = reason.contains("-silent") || reason.contains("-s");

            if(reason.contains("-silent")) {
                reason = reason.replace("-silent", "");
            } else if(reason.contains("-s")) {
                reason = reason.replace("-s", "");
            }

            Punishment punishment = new Punishment(plugin, targetData, PunishmentType.MUTE);
            punishment.setSilent(silent);
            if (duration != -5L) {
                punishment.setPermanent(false);
                punishment.setDurationTime(duration);
            } else {
                punishment.setPermanent(true);
            }
            punishment.setEnteredDuration(args[1]);
            punishment.setLast(true);
            punishment.setAddedBy(sender.getName());
            punishment.setAddedAt(System.currentTimeMillis());
            punishment.setReason(reason);

            targetData.getPunishData().getPunishments().add(punishment);

            punishment.execute(sender);
            punishment.save();

            if (sender instanceof Player) {
                Player player = (Player) sender;
                PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
                if (playerData == null) {
                    return;
                }
                PunishHistory punishHistory = new PunishHistory(sender.getName(), this.plugin, PunishmentType.MUTE);
                punishHistory.setAddedAt(punishment.getAddedAt());
                punishHistory.setDuration(punishment.getDurationTime());
                punishHistory.setPermanent(punishment.isPermanent());
                punishHistory.setExecutor(sender.getName());
                punishHistory.setTarget(targetData.getPlayerName());
                punishHistory.setReason(punishment.getReason());
                punishHistory.setActive(punishment.isActive());
                punishHistory.setLast(punishment.isLast());
                punishHistory.setSilent(punishment.isSilent());
                punishHistory.setEnteredDuration(punishment.getEnteredDuration());

                playerData.getPunishmentsExecuted().add(punishHistory);
            }

            plugin.getPunishmentPlugin().getProfileManager().unloadData(target);
        });
    }
}
