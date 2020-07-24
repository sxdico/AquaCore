package me.activated.core.commands.permission;

import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class BlacklistedPermissionsCommand extends BaseCommand {

    @Command(name = "blacklistedpermissions", permission = "Aqua.command.blacklistedpermissions", inGameOnly = false)
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            CommandSender sender = command.getSender();
            String[] args = command.getArgs();

            if (args.length != 2) {
                sender.sendMessage(Color.translate("&cCorrect usage: /" + command.getLabel() + " [add|remove] <permission>"));
                return;
            }
            if (args[0].equalsIgnoreCase("add")) {
                if (plugin.getCoreConfig().getStringList("blacklisted-permissions").stream().map(String::toLowerCase).collect(Collectors.toList()).contains(args[1].toLowerCase())) {
                    sender.sendMessage(Language.BLACKLISTED_PERMISSIONS_ALREADY_ADDED.toString());
                    return;
                }
                List<String> current = plugin.getCoreConfig().getStringList("blacklisted-permissions");
                current.add(args[1].toLowerCase());
                plugin.getCoreConfig().set("blacklisted-permissions", current);
                plugin.getCoreConfig().save();

                sender.sendMessage(Language.BLACKLISTED_PERMISSIONS_ADDED.toString()
                        .replace("<permission>", args[1]));

                for (Player online : Utilities.getOnlinePlayers()) {
                    PlayerData playerData = plugin.getPlayerManagement().getPlayerData(online.getUniqueId());
                    if (playerData != null) {
                        playerData.loadAttachments(online);
                    }
                }
                return;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                if (!plugin.getCoreConfig().getStringList("blacklisted-permissions").stream().map(String::toLowerCase).collect(Collectors.toList()).contains(args[1].toLowerCase())) {
                    sender.sendMessage(Language.BLACKLISTED_PERMISSIONS_DONT_EXISTS.toString());
                    return;
                }
                List<String> current = plugin.getCoreConfig().getStringList("blacklisted-permissions");
                current.remove(args[1].toLowerCase());
                plugin.getCoreConfig().set("blacklisted-permissions", current);
                plugin.getCoreConfig().save();

                sender.sendMessage(Language.BLACKLISTED_PERMISSIONS_REMOVED.toString()
                        .replace("<permission>", args[1]));

                for (Player online : Utilities.getOnlinePlayers()) {
                    PlayerData playerData = plugin.getPlayerManagement().getPlayerData(online.getUniqueId());
                    if (playerData != null) {
                        playerData.loadAttachments(online);
                    }
                }
                return;
            }
            sender.sendMessage(Color.translate("&cCorrect usage: /" + command.getLabel() + " [add|remove] <permission>"));
        });
    }
}
