package me.activated.core.commands.tags;

import me.activated.core.menus.tags.TagsMainMenu;
import me.activated.core.enums.Language;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class TagsCommand extends BaseCommand {

    @Command(name = "tag", aliases = "tags")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        if (!player.hasPermission("Aqua.command.tags")) {
            player.sendMessage(Language.TAGS_NO_ACCESS.toString());
            return;
        }
        new TagsMainMenu().open(command.getPlayer());
    }
}
