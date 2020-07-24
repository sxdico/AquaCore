package me.activated.core.commands.essentials.staff;

import me.activated.core.api.ServerData;
import me.activated.core.database.redis.other.bson.JsonChain;
import me.activated.core.database.redis.payload.action.JedisAction;
import me.activated.core.enums.Language;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.chat.Replacement;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.StringUtils;
import me.activated.core.utilities.general.Tasks;
import me.activated.core.utilities.server.TPSUtils;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class ServerManager extends BaseCommand {

    @Command(name = "servermanager", permission = "Aqua.command.servermanager")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Color.translate("&7&m---------------------------------------------"));
                player.sendMessage(Color.translate("&e&lServer Manager Help"));
                player.sendMessage(" ");
                player.sendMessage(Color.translate("&e/servermanager runcmd <server|all> <cmd> &8- &7run console command on all or spectific server."));
                player.sendMessage(Color.translate("&e/servermanager info <server> &8- &7see info about server."));
                player.sendMessage(Color.translate("&e/servermanager listservers &8- &7see currently connected servers."));
                player.sendMessage(Color.translate("&7&m---------------------------------------------"));
                return;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("listservers")) {
                player.sendMessage(Language.SERVER_MANAGER_SERVER_LIST.toString()
                        .replace("<servers>", this.getServers()));
                return;
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
                ServerData serverData = plugin.getServerManagement().getServerData(args[1]);

                if (serverData == null) {
                    player.sendMessage(Language.SERVER_MANAGER_SERVER_DONT_EXISTS.toString()
                            .replace("<servers>", this.getServers()));
                    return;
                }
                plugin.getCoreConfig().getStringList("server-manager-server-info").forEach(message -> {
                    Replacement replacement = new Replacement(message);
                    replacement.add("<name>", serverData.getServerName());
                    replacement.add("<online>", serverData.getOnlinePlayers().size());
                    replacement.add("<tps>", TPSUtils.getNiceTPS(serverData.getRecentTps()[0]) + "&a, "
                            + TPSUtils.getNiceTPS(serverData.getRecentTps()[1]) + "&a, " + TPSUtils.getNiceTPS(serverData.getRecentTps()[1]));
                    replacement.add("<max>", serverData.getMaxPlayers());
                    replacement.add("<whitelist>", serverData.isWhitelisted() ? "Yes" : "No");
                    replacement.add("<maintenance>", serverData.isMaintenance() ? "Yes" : "No");

                    player.sendMessage(replacement.toString());
                });
                return;
            }
            if (args[0].equalsIgnoreCase("runcmd")) {
                ServerData serverData = plugin.getServerManagement().getServerData(args[1]);

                if (serverData == null && !args[1].equalsIgnoreCase("all")) {
                    player.sendMessage(Language.SERVER_MANAGER_SERVER_DONT_EXISTS.toString()
                            .replace("<servers>", this.getServers()));
                    return;
                }

                JsonChain jsonChain = new JsonChain();
                jsonChain.addProperty("sender", player.getName());
                jsonChain.addProperty("server", args[1]);
                jsonChain.addProperty("command", StringUtils.buildMessage(args, 2));

                plugin.getRedisData().write(JedisAction.SERVER_COMMAND, jsonChain.get());

                if (args[1].equalsIgnoreCase("all")) {
                    player.sendMessage(Language.SERVER_MANAGER_COMMAND_PERFORMED_ALL
                            .toString().replace("<command>", StringUtils.buildMessage(args, 2)));
                } else {
                    player.sendMessage(Language.SERVER_MANAGER_COMMAND_PERFORMED_ALL.toString()
                            .replace("<server>", serverData != null ? serverData.getServerName() : args[1])
                            .replace("<command>", StringUtils.buildMessage(args, 2)));
                }
                return;
            }
            Tasks.run(plugin, () -> player.performCommand(command.getLabel()));
        });
    }

    private String getServers() {
        return StringUtils.getStringFromList(plugin.getServerManagement().getConnectedServers().stream().map(ServerData::getServerName).collect(Collectors.toList()));
    }
}
