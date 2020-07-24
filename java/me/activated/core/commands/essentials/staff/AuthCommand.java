package me.activated.core.commands.essentials.staff;

import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import me.activated.core.utilities.reflection.sit.SitUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class AuthCommand extends BaseCommand {

    @Command(name = "auth", permission = "Aqua.command.auth")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            if (!plugin.getCoreConfig().getBoolean("staff-auth.enabled")) {
                player.sendMessage(Color.translate("&cAuthentication is currently disabled."));
                return;
            }
            if (args.length == 0) {
                this.sendUsage(player);
                return;
            }
            if (args.length == 1) {
                String password = args[0];

                if (!playerData.isStaffAuth()) {
                    player.sendMessage(Language.STAFF_AUTH_DONT_NEED.toString());
                    return;
                }
                if (!password.equals(playerData.getAuthPassword())) {
                    player.sendMessage(Language.STAFF_AUTH_WRONT_PASS.toString());
                    return;
                }

                playerData.setStaffAuth(false);
                Tasks.run(plugin, () -> SitUtil.unsitPlayer(player));
                player.removePotionEffect(PotionEffectType.BLINDNESS);

                player.sendMessage(Language.STAFF_AUTH_AUTHENTICATED.toString());
                return;
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("register")) {
                    if (!playerData.isStaffAuth() || !playerData.getAuthPassword().equalsIgnoreCase("")) {
                        player.sendMessage(Language.STAFF_AUTH_DONT_NEED.toString());
                        return;
                    }
                    if (!args[1].equals(args[2])) {
                        player.sendMessage(Language.STAFF_AUTH_REGISTER_WRONG_REPEAT.toString());
                        return;
                    }
                    playerData.setAuthPassword(args[1]);

                    Tasks.run(plugin, () -> player.kickPlayer(ChatColor.RED + "Staff Auth successfully completed!\nPlease re-log and authenticate using your password!"));
                    return;
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("reset") && player.hasPermission("Aqua.command.auth.reset")) {
                    Player target = Bukkit.getPlayer(args[1]);

                    if (target == null) {
                        player.sendMessage(Language.NOT_ONLINE.toString());
                        return;
                    }
                    PlayerData targetData = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());

                    if (targetData.isStaffAuth() || targetData.getAuthPassword().equalsIgnoreCase("")) {
                        player.sendMessage(Language.STAFF_AUTH_RESET_ERROR.toString());
                        return;
                    }

                    targetData.setAuthPassword("");
                    targetData.setStaffAuth(true);

                    player.sendMessage(Language.STAFF_AUTH_RESET.toString()
                            .replace("<player>", target.getDisplayName()));

                    Tasks.run(plugin, () -> player.kickPlayer(ChatColor.RED + "Your staff auth has been reseted!\nPlease re-log and register again!"));
                    return;
                }
            }
            this.sendUsage(player);
        });
    }

    private void sendUsage(Player player) {
        player.sendMessage(Color.translate("&7&m--------------------------------"));
        player.sendMessage(Color.translate("&c&lAuth Help"));
        player.sendMessage(" ");
        player.sendMessage(Color.translate("&c/auth <password>"));
        player.sendMessage(Color.translate("&c/auth register <password> <password>"));
        if (player.hasPermission("Aqua.command.auth.reset")) {
            player.sendMessage(Color.translate("&c/auth reset <player>"));
        }
        player.sendMessage(Color.translate("&7&m--------------------------------"));
    }
}
