package me.activated.core.commands.essentials;

import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class GodCommand extends BaseCommand {

    @Command(name = "god", permission = "Aqua.command.god")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        playerData.setGodMode(!playerData.isGodMode());
        player.sendMessage(playerData.isGodMode() ? Language.GOD_MODE_ENABLED.toString() : Language.GOD_MODE_DISABLED.toString());
    }
}
