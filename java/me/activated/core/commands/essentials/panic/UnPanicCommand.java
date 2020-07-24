package me.activated.core.commands.essentials.panic;

import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class UnPanicCommand extends BaseCommand {

    @Command(name = "unpanic", permission = "Aqua.command.unpanic")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(Color.translate("&cCorrect usage: /unpanic <player>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Language.NOT_ONLINE.toString());
            return;
        }

        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());

        if (!playerData.getPanicSystem().isInPanic()) {
            player.sendMessage(Language.UNPANIC_NOT_IN_PANIC.toString()
                    .replace("<player>", target.getName()));
            return;
        }

        playerData.getPanicSystem().unPanicPlayer();
        player.sendMessage(Language.UNPANIC_UNPANICED_SENDER.toString()
                .replace("<player>", target.getName()));
        target.sendMessage(Language.UNPANIC_UNPANICED_TARGET.toString()
                .replace("<sender>", player.getName()));
    }
}
