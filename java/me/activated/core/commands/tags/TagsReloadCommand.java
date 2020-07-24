package me.activated.core.commands.tags;

import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.file.ConfigFile;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.entity.Player;

public class TagsReloadCommand extends BaseCommand {

    @Command(name = "tagsreload", aliases = "reloadtags")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        plugin.setTags(new ConfigFile(plugin, "tags.yml"));

        Tasks.runAsync(plugin, () -> {
            player.sendMessage(Color.translate("&aRe-importing tags. Please wait."));
            plugin.getTagManagement().deleteTags();
            plugin.getTagManagement().importTags();
            plugin.getTagManagement().saveTags();
            plugin.getTagManagement().requestTagsUpdate();
            player.sendMessage(Color.translate("&aTags have been imported and updated on all servers."));
        });
    }
}
