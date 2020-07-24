package me.activated.core.commands.essentials.staff;

import me.activated.core.enums.Language;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChatCommand extends BaseCommand {

    @Command(name = "chat", permission = "Aqua.command.chat")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Color.translate("&7&m---------------------------------------------"));
                player.sendMessage(Color.translate("&e&lChat Help"));
                player.sendMessage(" ");
                player.sendMessage(Color.translate("&e/chat mute &8- &7mute chat."));
                player.sendMessage(Color.translate("&e/chat unmute &8- &7un-mute chat."));
                player.sendMessage(Color.translate("&e/chat clear &8- &7clear the chat"));
                player.sendMessage(Color.translate("&e/chat slow <seconds> &8- &7slow down the chat."));
                player.sendMessage(Color.translate("&7&m---------------------------------------------"));
                return;
            }
            if (args[0].equalsIgnoreCase("mute")) {
                if (!plugin.getChatManagement().isMuted()) {
                    plugin.getChatManagement().setMuted(true);
                    Bukkit.broadcastMessage(Language.CHAT_MUTED.toString()
                            .replace("<player>", player.getDisplayName()));
                } else {
                    player.sendMessage(Language.CHAT_ALREADY_MUTED.toString());
                }
                return;
            }
            if (args[0].equalsIgnoreCase("unmute")) {
                if (plugin.getChatManagement().isMuted()) {
                    plugin.getChatManagement().setMuted(false);
                    Bukkit.broadcastMessage(Language.CHAT_UN_MUTED.toString()
                            .replace("<player>", player.getDisplayName()));
                } else {
                    player.sendMessage(Language.CHAT_ALREADY_UN_MUTED.toString());
                }
                return;
            }
            if (args[0].equalsIgnoreCase("clear")) {
                for (int i = 0; i < 100; i++) {
                    for (Player online : Utilities.getOnlinePlayers()) {
                        if (!online.hasPermission("Aqua.chat.bypass.clear")) {
                            online.sendMessage(" ");
                        }
                    }
                }
                Bukkit.broadcastMessage(Language.CHAT_CLEAR.toString()
                        .replace("<player>", player.getDisplayName()));
                return;
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("slow")) {
                if (!Utilities.isNumberInteger(args[1])) {
                    player.sendMessage(Language.USE_NUMBERS.toString());
                    return;
                }
                plugin.getChatManagement().setDelay(Integer.parseInt(args[1]));
                Bukkit.broadcastMessage(Language.CHAT_SLOWED.toString()
                        .replace("<seconds>", args[1])
                        .replace("<player>", player.getDisplayName()));
                return;
            }
            Tasks.run(plugin, () -> player.performCommand(command.getLabel()));
        });
    }
}
