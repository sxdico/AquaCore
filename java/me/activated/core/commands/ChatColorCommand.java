package me.activated.core.commands;

import me.activated.core.menus.color.ChatColorMenu;
import me.activated.core.enums.Language;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class ChatColorCommand extends BaseCommand {

    @Command(name = "cc", aliases = "chatcolor")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        if (!player.hasPermission("Aqua.command.chatcolor")) {
            player.sendMessage(Language.CHAT_COLOR_NO_ACCESS.toString());
            return;
        }
        new ChatColorMenu().open(player);
    }
}
