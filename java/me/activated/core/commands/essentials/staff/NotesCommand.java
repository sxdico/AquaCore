package me.activated.core.commands.essentials.staff;

import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.StringUtils;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.concurrent.atomic.AtomicInteger;

public class NotesCommand extends BaseCommand {

    @Command(name = "notes", aliases = {"note"}, permission = "Aqua.command.notes", inGameOnly = false)
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            CommandSender sender = command.getSender();
            String[] args = command.getArgs();

            if (args.length == 0) {
                this.sendUsage(sender);
                return;
            }
            if (args.length == 1) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(plugin.getPlayerManagement().getFixedName(args[0]));
                PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

                if (playerData == null) {
                    sender.sendMessage(Language.LOADING_OFFLINE_DATA.toString());
                    playerData = plugin.getPlayerManagement().loadData(player.getUniqueId());

                    if (playerData == null) {
                        sender.sendMessage(Language.DOESNT_HAVE_DATA.toString());
                        return;
                    }
                    playerData.loadData();
                }
                if (playerData.getNotes().size() == 0) {
                    sender.sendMessage(Language.NOTE_DONT_HAVE.toString());
                    return;
                }

                PlayerData finalPlayerData = playerData;
                plugin.getCoreConfig().getStringList("notes-format").forEach(message -> {
                    if (!message.toLowerCase().contains("<notes>")) {
                        sender.sendMessage(message
                                .replace("<player>", player.getName()));
                    } else {
                        AtomicInteger id = new AtomicInteger(1);
                        finalPlayerData.getNotes().forEach(note -> {
                            sender.sendMessage(plugin.getCoreConfig().getString("note-format")
                                    .replace("<note>", ChatColor.stripColor(note))
                                    .replace("<id>", String.valueOf(id.getAndIncrement())));
                        });
                    }
                });
                return;
            }
            if (args.length >= 3) {
                if (args[0].equalsIgnoreCase("add")) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(plugin.getPlayerManagement().getFixedName(args[1]));
                    PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

                    if (playerData == null) {
                        sender.sendMessage(Language.LOADING_OFFLINE_DATA.toString());
                        playerData = plugin.getPlayerManagement().loadData(player.getUniqueId());

                        if (playerData == null) {
                            sender.sendMessage(Language.DOESNT_HAVE_DATA.toString());
                            return;
                        }
                        playerData.loadData();
                    }
                    playerData.getNotes().add(StringUtils.buildMessage(args, 2));

                    sender.sendMessage(Language.NOTE_ADDED.toString()
                            .replace("<id>", String.valueOf(playerData.getNotes().size()))
                            .replace("<player>", player.getName()));

                    playerData.saveData();
                    return;
                }
                if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("remove")) {
                        if (!Utilities.isNumberInteger(args[2])) {
                            sender.sendMessage(Language.USE_NUMBERS.toString());
                            return;
                        }
                        OfflinePlayer player = Bukkit.getOfflinePlayer(plugin.getPlayerManagement().getFixedName(args[1]));
                        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
                        int id = Integer.parseInt(args[2]);

                        if (id <= 0) {
                            sender.sendMessage(Color.translate("&cId must be a positive number."));
                            return;
                        }
                        if (playerData == null) {
                            sender.sendMessage(Language.LOADING_OFFLINE_DATA.toString());
                            playerData = plugin.getPlayerManagement().loadData(player.getUniqueId());

                            if (playerData == null) {
                                sender.sendMessage(Language.DOESNT_HAVE_DATA.toString());
                                return;
                            }
                            playerData.loadData();
                        }
                        if (id > playerData.getNotes().size()) {
                            sender.sendMessage(Language.NOTE_INVALID_ID.toString()
                                    .replace("<player>", player.getName()));
                            return;
                        }
                        playerData.getNotes().remove(id - 1);

                        sender.sendMessage(Language.NOTE_REMOVED.toString()
                                .replace("<id>", String.valueOf(id))
                                .replace("<player>", player.getName()));

                        playerData.saveData();
                        return;
                    }
                }
                this.sendUsage(sender);
            }
        });
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Color.translate("&7&m-----------------------------------"));
        sender.sendMessage(Color.translate("&c&lNotes Help"));
        sender.sendMessage(" ");
        sender.sendMessage(Color.translate("&c/notes <player> &7- &ccheck notes of player"));
        sender.sendMessage(Color.translate("&c/notes add <player> <note> &7- &cadd notes to player"));
        sender.sendMessage(Color.translate("&c/notes remove <player> <id> &7- &cremove notes from player"));
        sender.sendMessage(Color.translate("&7&m-----------------------------------"));
    }
}
