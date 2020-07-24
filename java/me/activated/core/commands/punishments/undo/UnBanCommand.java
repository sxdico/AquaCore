package me.activated.core.commands.punishments.undo;


import me.activated.core.api.player.PlayerData;
import me.activated.core.database.redis.other.bson.JsonChain;
import me.activated.core.database.redis.payload.action.JedisAction;
import me.activated.core.punishments.player.PunishPlayerData;
import me.activated.core.enums.PunishmentsLanguage;
import me.activated.core.punishments.utilities.punishments.Punishment;
import me.activated.core.punishments.utilities.punishments.PunishmentType;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnBanCommand extends BaseCommand {

    @Command(name = "unban", inGameOnly = false, permission = "punishments.command.unban")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        Tasks.runAsync(plugin, () -> {
            if (args.length < 2) {
                sender.sendMessage(Color.translate("&cUsage: /unban <player> <reason> [-s]"));
                return;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.getPunishmentPlugin().getProfileManager().correctName(args[0]));

            PunishPlayerData targetData = plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());

            if (targetData == null || !target.isOnline()) {
                plugin.getPunishmentPlugin().getProfileManager().createPlayerDate(target.getUniqueId(), target.getName());
                targetData = plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());
                targetData.getPunishData().load();
            }
            if (!targetData.getPunishData().isBanned()) {
                sender.sendMessage(PunishmentsLanguage.BAN_NOT_BANNED.toString().replace("<user>", target.getName()));
                plugin.getPunishmentPlugin().getProfileManager().unloadData(target);
                return;
            }


            StringBuilder reasonBuilder = new StringBuilder();

            for (int i = 1; i < args.length; ++i) {
                reasonBuilder.append(args[i]).append(" ");
            }
            if (reasonBuilder.length() == 0) reasonBuilder.append("Un-Banned");

            String reason = reasonBuilder.toString().trim();
            boolean silent = reason.contains("-silent") || reason.contains("-s");

            if (reason.contains("-silent")) {
                reason = reason.replace("-silent", "");
            } else if (reason.contains("-s")) {
                reason = reason.replace("-s", "");
            }

            Punishment punishment = targetData.getPunishData().getActiveBan();
            punishment.setActive(false);
            punishment.setLast(false);
            punishment.setRemovedBy(sender.getName());
            punishment.setRemovedFor(reason);
            punishment.setRemovedSilent(silent);
            punishment.setWhenRemoved(System.currentTimeMillis());

            JsonChain jsonChain = new JsonChain();
            if (sender instanceof Player) {
                Player player = (Player) sender;
                jsonChain.addProperty("senderDisplay", player.getDisplayName());

                PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
                jsonChain.addProperty("coloredName", playerData.getHighestRank().getColor() + playerData.getPlayerName());
            } else {
                jsonChain.addProperty("senderDisplay", sender.getName());
            }
            jsonChain.addProperty("sender", sender.getName());
            jsonChain.addProperty("target", targetData.getPlayerName());
            jsonChain.addProperty("silent", punishment.isRemovedSilent());
            jsonChain.addProperty("reason", reason);

            plugin.getRedisData().write(JedisAction.EXECUTE_UNBAN, jsonChain.get());

            punishment.save(true);

            Player addedBy = Bukkit.getPlayer(punishment.getAddedBy());
            if (addedBy != null) {
                PlayerData addedByData = plugin.getPlayerManagement().getPlayerData(addedBy.getUniqueId());
                addedByData.getPunishmentsExecuted().forEach(punishHistory -> {
                    if (punishHistory.getPunishmentType() == PunishmentType.BAN) {
                        if (punishHistory.getTarget().equals(target.getName())) {
                            if (punishHistory.getAddedAt() == punishment.getAddedAt()) {
                                punishHistory.setActive(false);
                            }
                        }
                    }
                });
            }

            plugin.getPunishmentPlugin().getProfileManager().unloadData(target);
        });
    }
}
