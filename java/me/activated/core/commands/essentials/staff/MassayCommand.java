package me.activated.core.commands.essentials.staff;

import me.activated.core.enums.Language;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.StringUtils;
import org.bukkit.entity.Player;

public class MassayCommand extends BaseCommand {

    @Command(name = "massay", permission = "Aqua.command.massay")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(Color.translate("&cCorrect usage: /massay <message>"));
            return;
        }

        Utilities.getOnlinePlayers().forEach(online -> {
            online.chat(StringUtils.buildMessage(args, 0));
        });
        player.sendMessage(Language.MASSAY_SUCCESS.toString()
                .replace("<message>", StringUtils.buildMessage(args, 0)));
    }
}
