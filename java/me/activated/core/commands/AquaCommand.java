package me.activated.core.commands;

import me.activated.core.api.player.PlayerData;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AquaCommand extends BaseCommand {

    @Command(name = "Aquacore", inGameOnly = false, aliases = {"core", "Aqua", "Aquacore", "activated"})
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () ->{
            CommandSender sender = command.getSender();
            String[] args = command.getArgs();

            if (args.length == 0) {
                sender.sendMessage(Color.translate("&7&m------------------------------------"));
                sender.sendMessage(Color.translate("&7This server is running &b&l" + plugin.getDescription().getName()));
                sender.sendMessage(Color.translate("&7Author: &b" + plugin.getDescription().getAuthors()));
                sender.sendMessage(Color.translate("&7Version: &b" + plugin.getDescription().getVersion()));
                sender.sendMessage(Color.translate("&cCracked nigguh"));
                sender.sendMessage(Color.translate("&7&m------------------------------------"));
                return;
            }

            if (!sender.isOp()) {
                sender.sendMessage(Color.translate("&7&m------------------------------------"));
                sender.sendMessage(Color.translate("&7This server is running &b&l" + plugin.getDescription().getName()));
                sender.sendMessage(Color.translate("&7Author: &b" + plugin.getDescription().getAuthors()));
                sender.sendMessage(Color.translate("&7Version: &b" + plugin.getDescription().getVersion()));
                sender.sendMessage(Color.translate("&cCracked nigguh"));
                sender.sendMessage(Color.translate("&7&m------------------------------------"));
                return;
            }

            if (args[0].equalsIgnoreCase("debug")) {
                sender.sendMessage(Color.translate("&aPlease wait..."));

                List<Document> bans = plugin.getMongoManager().getBans().find().into(new ArrayList<>());
                List<Document> mutes = plugin.getMongoManager().getMutes().find().into(new ArrayList<>());
                List<Document> warns = plugin.getMongoManager().getWarns().find().into(new ArrayList<>());
                List<Document> blacklists = plugin.getMongoManager().getBlacklists().find().into(new ArrayList<>());
                List<Document> kicks = plugin.getMongoManager().getKicks().find().into(new ArrayList<>());

                sender.sendMessage(Color.translate("&7&m-----------------------------------------"));
                sender.sendMessage(Color.translate("&b&lAqua Core &7| &aDebug Information"));
                sender.sendMessage(" ");
                sender.sendMessage(Color.translate("&7Total Bans: &b" + bans.size()));
                sender.sendMessage(Color.translate("&7Total Kicks: &b" + kicks.size()));
                sender.sendMessage(Color.translate("&7Total Mutes: &b" + mutes.size()));
                sender.sendMessage(Color.translate("&7Total Warns: &b" + warns.size()));
                sender.sendMessage(Color.translate("&7Total Blacklists: &b" + blacklists.size()));
                sender.sendMessage(Color.translate("&7Total: &b" + (bans.size() + mutes.size() + warns.size() + blacklists.size() + kicks.size()) + " punishments."));
                sender.sendMessage(" ");
                sender.sendMessage(Color.translate("&7Plugin: &b&l" + plugin.getDescription().getName()));
                sender.sendMessage(Color.translate("&7Author: &b" + plugin.getDescription().getAuthors()));
                sender.sendMessage(Color.translate("&7Version: &b" + plugin.getDescription().getVersion()));
                sender.sendMessage(Color.translate("&7&m-----------------------------------------"));
                return;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                plugin.reloadFiles();
                plugin.getPunishmentPlugin().getConfigFile().load();
                plugin.getPunishmentPlugin().getLanguageFile().load();
                plugin.getPunishmentPlugin().getMessagesManager().setup();

                for (Player online : Utilities.getOnlinePlayers()) {
                    PlayerData playerData = plugin.getPlayerManagement().getPlayerData(online.getUniqueId());
                    if (playerData != null) {
                        playerData.loadAttachments(online);
                    }
                }

                sender.sendMessage(Color.translate("&7&m-----------------------------------------"));
                sender.sendMessage(Color.translate("&b&lAqua Core &7| &aReload Information"));
                sender.sendMessage(Color.translate(""));
                sender.sendMessage(Color.translate("&aThe following files have been reloaded!"));
                sender.sendMessage(Color.translate("&7(&bconfig.yml&7, &bmessages.yml&7, &bsettings.yml"));
                sender.sendMessage(Color.translate("&7(&branks.yml&7, &btags.yml&7, &blang.yml&7)"));
                sender.sendMessage(Color.translate("&7&m-----------------------------------------"));
                return;
            }

            if (args[0].equalsIgnoreCase("redisreload")) {
                sender.sendMessage(Color.translate("&aPlease wait.."));
                plugin.getRedisData().reload();

                sender.sendMessage(Color.translate("&aYou have reloaded redis!"));
                sender.sendMessage(Color.translate("&aCurrent status&7: " + (plugin.getRedisData().isConnected() ? "&2Connected" : "&cDisconnected")));
                return;
            }
        });
    }
}
