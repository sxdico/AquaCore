package me.activated.core.commands.punishments.punish;

import me.activated.core.api.player.PlayerData;
import me.activated.core.data.other.punishments.PunishHistory;
import me.activated.core.punishments.player.PunishPlayerData;
import me.activated.core.punishments.utilities.punishments.Punishment;
import me.activated.core.punishments.utilities.punishments.PunishmentType;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand extends BaseCommand {

    @Command(name = "kick", inGameOnly = false, permission = "punishments.command.kick", aliases = {"kickplayer"})
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        Tasks.runAsync(plugin, () -> {
            if (args.length < 2) {
                sender.sendMessage(Color.translate("&cUsage: /kick <player> <reason> [-s]"));
                return;
            }
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(Color.translate("&cThat player is currently offline!"));
                return;
            }

            PunishPlayerData targetData = plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());

            if (targetData == null || !target.isOnline()) {
                plugin.getPunishmentPlugin().getProfileManager().createPlayerDate(target.getUniqueId(), target.getName());
                targetData = plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());
                targetData.getPunishData().load();
            }
            StringBuilder reasonBuilder = new StringBuilder();

            for(int i = 1; i < args.length; ++i) {
                reasonBuilder.append(args[i]).append(" ");
            }
            if (reasonBuilder.length() == 0) reasonBuilder.append("Kicked");

            String reason = reasonBuilder.toString().trim();
            boolean silent = reason.contains("-silent") || reason.contains("-s");

            if(reason.contains("-silent")) {
                reason = reason.replace("-silent", "");
            } else if(reason.contains("-s")) {
                reason = reason.replace("-s", "");
            }

            Punishment punishment = new Punishment(plugin, targetData, PunishmentType.KICK);
            punishment.setSilent(silent);
            punishment.setPermanent(false);
            punishment.setIPRelative(false);
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
                PunishHistory punishHistory = new PunishHistory(sender.getName(), this.plugin, PunishmentType.KICK);
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
        });
    }
}
