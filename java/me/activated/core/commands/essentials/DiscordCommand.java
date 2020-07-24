package me.activated.core.commands.essentials;

import me.activated.core.enums.Language;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;

public class DiscordCommand extends BaseCommand {

    @Command(name = "discord")
    public void onCommand(CommandArgs command) {
        command.getPlayer().sendMessage(Language.DISCORD.toString());
    }
}
