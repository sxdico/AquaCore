package me.activated.core.commands.essentials.staff;

import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.StringUtils;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.entity.Player;

public class StaffChatCommand extends BaseCommand {

    @Command(name = "staffchat", permission = "Aqua.staffchat", aliases = {"sc"})
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            if (args.length == 0) {
                playerData.setStaffChat(!playerData.isStaffChat());
                player.sendMessage(playerData.isStaffChat() ? Language.STAFF_CHAT_ENABLED.toString() : Language.STAFF_CHAT_DISABLED.toString());
                return;
            }
            plugin.getPlayerManagement().sendStaffChatMessage(playerData, StringUtils.buildMessage(args, 0));
        });
    }
}
