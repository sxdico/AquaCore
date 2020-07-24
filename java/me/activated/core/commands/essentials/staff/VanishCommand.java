package me.activated.core.commands.essentials.staff;

import me.activated.core.events.VanishUpdateEvent;
import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class VanishCommand extends BaseCommand {

    @Command(name = "vanish", permission = "Aqua.command.vanish", aliases = "v")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (playerData.isVanished()) {
            plugin.getVanishManagement().unvanishPlayer(player);
            player.sendMessage(Language.VANISH_UN_VANISHED.toString());
        } else {
            plugin.getVanishManagement().vanishPlayer(player);
            player.sendMessage(Language.VANISH_VANISHED.toString());
        }
        plugin.getServer().getPluginManager().callEvent(new VanishUpdateEvent(player));
    }
}
