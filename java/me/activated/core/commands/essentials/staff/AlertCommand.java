package me.activated.core.commands.essentials.staff;

import me.activated.core.enums.Language;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.StringUtils;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class AlertCommand extends BaseCommand {

    @Command(name = "alert", permission = "Aqua.command.alert", inGameOnly = false)
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            CommandSender sender = command.getSender();
            String[] args = command.getArgs();

            if (args.length == 0) {
                sender.sendMessage(Color.translate("&cCorrect usage: /alert <message...>"));
                return;
            }
            plugin.getServerManagement().getGlobalPlayers().forEach(globalPlayer -> {
                globalPlayer.sendMessage(Language.ALERT_FORMAT.toString()
                        .replace("<message>", Color.translate(StringUtils.buildMessage(args, 0))));
            });
            Bukkit.getConsoleSender().sendMessage(Language.ALERT_FORMAT.toString()
                    .replace("<message>", Color.translate(StringUtils.buildMessage(args, 0))));
        });
    }
}
