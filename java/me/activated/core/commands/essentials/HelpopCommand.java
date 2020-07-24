package me.activated.core.commands.essentials;

import me.activated.core.api.player.PlayerData;
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

public class HelpopCommand extends BaseCommand {

    @Command(name = "helpop", permission = "Aqua.command.helpop", aliases = {"request", "help"})
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            if (args.length == 0) {
                player.sendMessage(Color.translate("&cCorrect usage: /helpop <reason>"));
                return;
            }
            if (playerData.getGlobalCooldowns().hasCooldown("helpop")) {
                player.sendMessage(Language.HELPOP_COOLDOWN.toString().replace("<seconds>",
                        playerData.getGlobalCooldowns().getMiliSecondsLeft("helpop")));
                return;
            }

            player.sendMessage(Language.HELPOP_TO_PLAYER.toString());

            Replacement replacement = new Replacement(Language.HELPOP_FORMAT.toString());
            replacement.add("<player>", player.getName());
            replacement.add("<reason>", ChatColor.stripColor(StringUtils.buildMessage(args, 0)));
            replacement.add("<player_server>", plugin.getEssentialsManagement().getServerName());

            plugin.getServerManagement().getGlobalPlayers().stream().filter(online -> online.hasPermission("Aqua.helpop.see") && online.isHelpopAlerts()).forEach(online -> {
                online.sendMessage(replacement.toString(false));
            });

            try {
                playerData.getGlobalCooldowns().createCooldown("helpop", System.currentTimeMillis(), System.currentTimeMillis() - DateUtils.parseDateDiff(
                        plugin.getCoreConfig().getString("helpop-cooldown"), false));
            } catch (Exception exception) {
                try {
                    playerData.getGlobalCooldowns().createCooldown("helpop", System.currentTimeMillis(), System.currentTimeMillis() - DateUtils.parseDateDiff("1m", false));
                } catch (Exception ignored) {
                }
            }
            playerData.getGlobalCooldowns().saveCooldowns();
        });
    }
}
