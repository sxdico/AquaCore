package me.activated.core.commands.essentials.panic;

import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class PanicCommand extends BaseCommand {

    @Command(name = "panic", permission = "Aqua.command.panic")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (playerData.getPanicSystem().isInPanic()) {
            player.sendMessage(Language.PANIC_COMMAND_ALREADY_IN_PANIC.toString()
                    .replace("<time>", playerData.getPanicSystem().getTimeExpiration()));
            return;
        }
        if (playerData.getPanicSystem().isOnCommandCooldown()) {
            player.sendMessage(Language.PANIC_COMMAND_COOLDOWN.toString()
                    .replace("<time>", playerData.getPanicSystem().getCommandExpiration()));
            return;
        }

        playerData.getPanicSystem().panicPlayer();
        plugin.getCoreConfig().getStringList("panic-message").forEach(player::sendMessage);
        
        player.sendMessage(Language.PANIC_COMMAND_USED.toString()
                .replace("<time>", playerData.getPanicSystem().getTimeExpiration()));

        plugin.getServerManagement().getGlobalPlayers().stream().filter(globalPlayer -> globalPlayer.hasPermission("Aqua.panic.alerts")).forEach(globalPlayer -> {
            globalPlayer.sendMessage(Language.PANIC_STAFF_ALERT.toString()
                    .replace("<server>", plugin.getEssentialsManagement().getServerName())
                    .replace("<name>", player.getDisplayName()));
        });
    }
}
