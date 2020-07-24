package me.activated.core.commands.essentials;

import me.activated.core.api.player.GlobalPlayer;
import me.activated.core.api.player.PlayerData;
import me.activated.core.database.redis.other.bson.JsonChain;
import me.activated.core.database.redis.payload.action.JedisAction;
import me.activated.core.events.PlayerReportEvent;
import me.activated.core.enums.Language;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.chat.Replacement;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.DateUtils;
import me.activated.core.utilities.general.StringUtils;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ReportCommand extends BaseCommand {

    @Command(name = "report", permission = "Aqua.command.report")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            if (args.length < 2) {
                player.sendMessage(Color.translate("&cCorrect usage: /report <player> <reason>"));
                return;
            }
            if (playerData.getGlobalCooldowns().hasCooldown("report")) {
                player.sendMessage(Language.REPORT_COOLDOWN.toString().replace("<seconds>",
                        playerData.getGlobalCooldowns().getMiliSecondsLeft("report")));
                return;
            }

            GlobalPlayer globalPlayer = plugin.getServerManagement().getGlobalPlayer(args[0]);

            if (globalPlayer == null) {
                player.sendMessage(Language.NOT_CONNECTED.toString()
                        .replace("<name>", args[0]));
                return;
            }
            if (globalPlayer.getName().equalsIgnoreCase(player.getName())) {
                player.sendMessage(Language.REPORT_CANT_REPORT_YOURSELF.toString());
                return;
            }

            PlayerReportEvent event = new PlayerReportEvent(player, globalPlayer, ChatColor.stripColor(StringUtils.buildMessage(args, 1)));
            plugin.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) return;

            player.sendMessage(Language.REPORT_TO_PLAYER.toString());

            Replacement replacement = new Replacement(Language.REPORT_FORMAT.toString());
            replacement.add("<player>", player.getName());
            replacement.add("<target>", globalPlayer.getName());
            replacement.add("<reason>", ChatColor.stripColor(StringUtils.buildMessage(args, 1)));
            replacement.add("<player_server>", plugin.getEssentialsManagement().getServerName());
            replacement.add("<target_server>", globalPlayer.getServer());

            plugin.getServerManagement().getGlobalPlayers().stream().filter(online -> online.hasPermission("Aqua.reports.see") && online.isReportAlerts()).forEach(online -> {
                online.sendMessage(replacement.toString(false));
            });

            plugin.getRedisData().write(JedisAction.REPORT_SAVE, new JsonChain()
                    .addProperty("date", DateUtils.getDate(System.currentTimeMillis()))
                    .addProperty("reporter", player.getName())
                    .addProperty("reason", StringUtils.buildMessage(args, 1))
                    .addProperty("reporterServer", plugin.getEssentialsManagement().getServerName())
                    .addProperty("reportedServer", globalPlayer.getServer())
                    .addProperty("addedAt", System.currentTimeMillis())
                    .addProperty("uuid", globalPlayer.getUniqueId().toString()).get());

            try {
                playerData.getGlobalCooldowns().createCooldown("report", System.currentTimeMillis(), System.currentTimeMillis() - DateUtils.parseDateDiff(
                        plugin.getCoreConfig().getString("helpop-cooldown"), false));
            } catch (Exception exception) {
                try {
                    playerData.getGlobalCooldowns().createCooldown("report", System.currentTimeMillis(), System.currentTimeMillis() - DateUtils.parseDateDiff("1m", false));
                } catch (Exception ignored) {
                }
            }
            playerData.getGlobalCooldowns().saveCooldowns();
        });
    }
}
