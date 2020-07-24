package me.activated.core.commands;

import me.activated.core.menus.color.NameColorMenu;
import me.activated.core.enums.Language;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class ColorCommand extends BaseCommand {

    @Command(name = "color", aliases = "namecolor")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        if (!player.hasPermission("Aqua.command.color")) {
            player.sendMessage(Language.NAME_COLOR_NO_ACCESS.toString());
            return;
        }
        new NameColorMenu().open(player);
    }
}
