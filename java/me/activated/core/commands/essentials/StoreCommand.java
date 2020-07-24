package me.activated.core.commands.essentials;

import me.activated.core.enums.Language;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;

public class StoreCommand extends BaseCommand {

    @Command(name = "store")
    public void onCommand(CommandArgs command) {
        command.getPlayer().sendMessage(Language.STORE.toString());
    }
}
