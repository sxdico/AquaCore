package me.activated.core.commands.essentials.messages.ignore;

import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.utilities.chat.ChatComponentBuilder;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;

public class IgnoreCommand extends BaseCommand {

    @Command(name = "ignore")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            if (args.length == 0) {
                player.sendMessage(Color.translate("&7&m---------------------------------------------"));
                player.sendMessage(Color.translate("&e&lIgnore Help"));
                player.sendMessage(" ");
                player.sendMessage(Color.translate("&e/ignore add <name> &8- &7add player to ignore list."));
                player.sendMessage(Color.translate("&e/ignore remove <name> &8- &7remove player from ignore list."));
                player.sendMessage(Color.translate("&e/ignore list &8- &7see who you are ignoring."));
                player.sendMessage(Color.translate("&7&m---------------------------------------------"));
                return;
            }
            if (args[0].equalsIgnoreCase("list")) {
                if (playerData.getMessageSystem().getIgnoreList().size() > 0) {
                    plugin.getCoreConfig().getStringList("ignore-list-format").forEach(message -> {
                        if (!message.contains("{names}")) {
                            player.sendMessage(message);
                        } else {
                            ChatComponentBuilder chatComponentBuilder = new ChatComponentBuilder("");
                            playerData.getMessageSystem().getIgnoreList().forEach(name -> {
                                chatComponentBuilder.append(Color.translate("&a" + name));

                                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new ChatComponentBuilder(ChatColor.RED + "Click to un-ignore.").create());
                                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                        "/ignore remove " + name);

                                chatComponentBuilder.setCurrentHoverEvent(hoverEvent).setCurrentClickEvent(clickEvent);

                                chatComponentBuilder.append(Color.translate("&7, "));
                            });
                            chatComponentBuilder.getCurrent().setText(chatComponentBuilder.getCurrent().getText().substring(0, chatComponentBuilder.getCurrent().getText().length() - 4));
                            chatComponentBuilder.append(Color.translate("&7."));
                            player.spigot().sendMessage(chatComponentBuilder.create());
                        }
                    });
                } else {
                    player.sendMessage(Language.MESSAGES_IGNORE_LIST_EMPTY.toString());
                }
                return;
            }
            if (args.length < 2) {
                Tasks.run(plugin, () -> player.performCommand(command.getLabel()));
                return;
            }
            if (args[0].equalsIgnoreCase("add")) {
                if (args[1].equalsIgnoreCase(player.getName())) {
                    player.sendMessage(Color.translate(Language.PREFIX.toString() + "&cYou can't add yourself to ignore list."));
                    return;
                }
                if (playerData.getMessageSystem().isIgnoring(args[1])) {
                    player.sendMessage(Language.MESSAGES_IGNORE_ALREADY_IGNORING.toString().replace("<player>", args[1]));
                    return;
                }
                playerData.getMessageSystem().getIgnoreList().add(args[1]);
                player.sendMessage(Language.MESSAGES_IGNORE_IGNORED.toString().replace("<player>", args[1]));
                return;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                if (!playerData.getMessageSystem().isIgnoring(args[1])) {
                    player.sendMessage(Language.MESSAGES_IGNORE_NOT_IGNORING.toString().replace("<player>", args[1]));
                    return;
                }
                playerData.getMessageSystem().getIgnoreList().remove(args[1]);
                player.sendMessage(Language.MESSAGES_IGNORE_UNIGNORED.toString().replace("<player>", args[1]));
                return;
            }
            Tasks.run(plugin, () -> player.performCommand(command.getLabel()));
        });
    }
}
