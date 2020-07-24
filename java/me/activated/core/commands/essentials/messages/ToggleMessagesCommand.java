package me.activated.core.commands.essentials.messages;

import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.entity.Player;

public class ToggleMessagesCommand extends BaseCommand {

    @Command(name = "togglemessages", aliases = {"tpm", "msgtoggle", "togglepm"})
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            playerData.getMessageSystem().setMessagesToggled(!playerData.getMessageSystem().isMessagesToggled());
            if (playerData.getMessageSystem().isMessagesToggled()) {
                player.sendMessage(Language.MESSAGES_TOGGLED_ON.toString());
            } else {
                player.sendMessage(Language.MESSAGES_TOGGLED_OFF.toString());
            }
        });
    }
}
