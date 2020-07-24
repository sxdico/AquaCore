package me.activated.core.commands;

import me.activated.core.menus.settings.SettingsMenu;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;

public class SettingsCommand extends BaseCommand {

    @Command(name = "settings", aliases = "options")
    public void onCommand(CommandArgs command) {
        new SettingsMenu().open(command.getPlayer());
    }
}
