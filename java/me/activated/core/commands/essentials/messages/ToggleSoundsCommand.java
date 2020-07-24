package me.activated.core.commands.essentials.messages;

import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.entity.Player;

public class ToggleSoundsCommand extends BaseCommand {

    @Command(name = "togglesounds", aliases = {"tpms", "togglepms", "sounds"})
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            playerData.getMessageSystem().setSoundsEnabled(!playerData.getMessageSystem().isSoundsEnabled());
            if (playerData.getMessageSystem().isSoundsEnabled()) {
                player.sendMessage(Language.MESSAGES_TOGGLED_ON_SOUNDS.toString());
                Utilities.playSound(player, plugin.getCoreConfig().getString("private-message-sound"));
            } else {
                player.sendMessage(Language.MESSAGES_TOGGLED_OFF_SOUNDS.toString());
            }
        });
    }
}
