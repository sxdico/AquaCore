package me.activated.core.commands.essentials.staff;

import me.activated.core.enums.Language;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.StringUtils;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SudoCommand extends BaseCommand {

    @Command(name = "sudo", permission = "Aqua.command.sudo", aliases = "sudoplayer")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Color.translate("&cCorrect usage: /sudo <player> <chat|command>"));
                return;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Language.NOT_ONLINE.toString());
                return;
            }

            String chat = StringUtils.buildMessage(args, 1);
            Tasks.run(plugin, () -> target.chat(chat));

            if (chat.startsWith("/")) {
                player.sendMessage(Language.SUDO_USED_COMMAND.toString()
                        .replace("<player>", target.getName())
                        .replace("<command>", chat));
            } else {
                player.sendMessage(Language.SUDO_USED_CHAT.toString()
                        .replace("<player>", target.getName())
                        .replace("<message>", chat));
            }
        });
    }
}
