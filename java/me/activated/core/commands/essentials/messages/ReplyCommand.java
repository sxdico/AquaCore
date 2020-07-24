package me.activated.core.commands.essentials.messages;

import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.StringUtils;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReplyCommand extends BaseCommand {

    @Command(name = "reply", aliases = {"r"})
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            if (args.length == 0) {
                player.sendMessage(Color.translate("&cCorrect usage: /" + command.getLabel() + " <msg>"));
                return;
            }
            if (playerData.getMessageSystem().getLastMessage() == null) {
                player.sendMessage(Language.NOT_ONLINE.toString());
                return;
            }
            Player target = Bukkit.getPlayer(playerData.getMessageSystem().getLastMessage());
            if (target == null) {
                player.sendMessage(Language.NOT_ONLINE.toString());
                return;
            }
            Tasks.run(plugin, () -> player.performCommand("message " + target.getName() + " " + StringUtils.buildMessage(args, 0)));
        });
    }
}
