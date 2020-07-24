package me.activated.core.commands.permission;

import me.activated.core.api.player.PlayerData;
import me.activated.core.database.redis.other.bson.JsonChain;
import me.activated.core.database.redis.payload.action.JedisAction;
import me.activated.core.enums.Language;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.StringUtils;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPermissionCommand extends BaseCommand {

    @Command(name = "setpermission", permission = "Aqua.command.setpermission", aliases = {"setperm"}, inGameOnly = false)
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            CommandSender player = command.getSender();
            String[] args = command.getArgs();

            if (args.length < 3) {
                player.sendMessage(Color.translate("&cCorrect usage: /" + command.getLabel() + " <user> <permission> <true|false>"));
                return;
            }
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(plugin.getPlayerManagement().getFixedName(args[0]));
            if (offlinePlayer.isOnline()) {
                Player target = offlinePlayer.getPlayer();
                PlayerData targetData = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());

                this.setPermission(player, targetData, args[1], args[2].equalsIgnoreCase("true"));
                targetData.loadAttachments(target);
            } else {
                player.sendMessage(Language.LOADING_OFFLINE_DATA.toString());
                PlayerData targetData = plugin.getPlayerManagement().loadData(offlinePlayer.getUniqueId());

                if (targetData == null) {
                    player.sendMessage(Language.DOESNT_HAVE_DATA.toString());
                    return;
                }
                this.setPermission(player, targetData, args[1], args[2].equalsIgnoreCase("true"));
                plugin.getPlayerManagement().deleteData(targetData.getUniqueId());
            }
        });
    }

    private void setPermission(CommandSender sender, PlayerData targetData, String permission, boolean set) {
        if (set) {
            if (targetData.hasPermission(permission)) {
                sender.sendMessage(Language.PLAYER_ALREADY_HAVE_PERMISSION.toString()
                        .replace("<player>", targetData.getPlayerName())
                        .replace("<permission>", permission));
                return;
            }
            targetData.getPermissions().add(permission);
            sender.sendMessage(Language.PLAYER_PERMISSION_SET.toString()
                    .replace("<player>", targetData.getPlayerName())
                    .replace("<permission>", permission));
            targetData.saveData();
        } else {
            if (!targetData.hasPermission(permission)) {
                sender.sendMessage(Language.PLAYER_NOT_HAVE_PERMISSION.toString()
                        .replace("<player>", targetData.getPlayerName())
                        .replace("<permission>", permission));
                return;
            }
            targetData.getPermissions().remove(permission);
            sender.sendMessage(Language.PLAYER_PERMISSION_REMOVED.toString()
                    .replace("<player>", targetData.getPlayerName())
                    .replace("<permission>", permission));
            targetData.saveData();
        }
        Player target = Bukkit.getPlayer(targetData.getPlayerName());
        if (target == null) {
            plugin.getRedisData().write(JedisAction.PLAYER_PERMISSIONS_UPDATE,
                    new JsonChain().addProperty("name", targetData.getPlayerName())
                            .addProperty("permissions", StringUtils.getStringFromList(targetData.getPermissions())).get());
        }
    }
}
