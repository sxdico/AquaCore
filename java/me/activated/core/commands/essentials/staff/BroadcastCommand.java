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

public class BroadcastCommand extends BaseCommand {

    @Command(name = "broadcast", permission = "Aqua.command.broadcast", inGameOnly = false, aliases = "bc")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            CommandSender sender = command.getSender();
            String[] args = command.getArgs();

            if (args.length == 0) {
                sender.sendMessage(Color.translate("&cCorrect usage: /broadcast <message...>"));
                return;
            }
            Bukkit.broadcastMessage(Language.BROADCAST_FORMAT.toString()
                    .replace("<message>", Color.translate(StringUtils.buildMessage(args, 0))));
        });
    }
}
