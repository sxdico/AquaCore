package me.activated.core.commands.punishments;

import me.activated.core.enums.Language;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.DateUtils;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class StaffRollBackCommand extends BaseCommand {

    @Command(name = "staffrollback", permission = "punishments.command.staffrollback")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length < 2) {
                player.sendMessage(Color.translate("&cCorrect usage: /staffrollback <staff> <time> <type>"));
                player.sendMessage(Color.translate("&cFor the type you can use 'Bans, Mutes, Blacklists or Warns'"));
                return;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.getPlayerManagement().getFixedName(args[0]));
            if (!plugin.getPlayerManagement().hasData(target.getUniqueId()) && !args[0].equalsIgnoreCase("Console")) {
                player.sendMessage(Language.DOESNT_HAVE_DATA.toString());
                return;
            }
            long time = -1L;
            try {
                time = System.currentTimeMillis() - DateUtils.parseDateDiff(args[1], false);
            } catch (Exception e) {
                player.sendMessage(Language.INVALID_TIME_DURAITON.toString());
                return;
            }
            long check = System.currentTimeMillis() - time;
            if (args[2].equalsIgnoreCase("Bans")) {
                player.sendMessage(Language.STAFF_ROLLBACK_WIPING.toString()
                        .replace("<type>", "bans"));
                AtomicInteger expired = new AtomicInteger(0);
                AtomicInteger active = new AtomicInteger(0);
                plugin.getMongoManager().getBans().find().into(new ArrayList<>()).forEach(document -> {
                    if (document.containsKey("addedBy") && document.getString("addedBy").equalsIgnoreCase(args[0])) {
                        if (document.containsKey("addedAt")) {
                            long addedAt = document.getLong("addedAt");

                            if (check <= addedAt) {
                                plugin.getMongoManager().getBans().deleteOne(document);
                                if (document.containsKey("active") && document.getBoolean("active")) {
                                    active.getAndIncrement();
                                } else {
                                    expired.getAndIncrement();
                                }
                            }
                        }
                    }
                });
                if (expired.get() + active.get() == 0) {
                    player.sendMessage(Language.STAFF_ROLLBACK_DONT_HAVE_HISTORY.toString()
                            .replace("<type>", "bans")
                            .replace("<name>", target.getName()));
                    return;
                }
                player.sendMessage(Language.STAFF_ROLLBACK_WIPED.toString()
                        .replace("<amount>", String.valueOf(expired.get() + active.get()))
                        .replace("<type>", "bans")
                        .replace("<name>", target.getName())
                        .replace("<active>", String.valueOf(active.get()))
                        .replace("<expired>", String.valueOf(expired.get())));
                return;
            }
            if (args[2].equalsIgnoreCase("Mutes")) {
                player.sendMessage(Language.STAFF_ROLLBACK_WIPING.toString()
                        .replace("<type>", "mutes"));
                AtomicInteger expired = new AtomicInteger(0);
                AtomicInteger active = new AtomicInteger(0);
                plugin.getMongoManager().getMutes().find().into(new ArrayList<>()).forEach(document -> {
                    if (document.containsKey("addedBy") && document.getString("addedBy").equalsIgnoreCase(args[0])) {
                        if (document.containsKey("addedAt")) {
                            long addedAt = document.getLong("addedAt");

                            if (check <= addedAt) {
                                plugin.getMongoManager().getBans().deleteOne(document);
                                if (document.containsKey("active") && document.getBoolean("active")) {
                                    active.getAndIncrement();
                                } else {
                                    expired.getAndIncrement();
                                }
                            }
                        }
                    }
                });
                if (expired.get() + active.get() == 0) {
                    player.sendMessage(Language.STAFF_ROLLBACK_DONT_HAVE_HISTORY.toString()
                            .replace("<type>", "mutes")
                            .replace("<name>", target.getName()));
                    return;
                }
                player.sendMessage(Language.STAFF_ROLLBACK_WIPED.toString()
                        .replace("<amount>", String.valueOf(expired.get() + active.get()))
                        .replace("<type>", "mutes")
                        .replace("<name>", target.getName())
                        .replace("<active>", String.valueOf(active.get()))
                        .replace("<expired>", String.valueOf(expired.get())));
                return;
            }
            if (args[2].equalsIgnoreCase("blacklists")) {
                player.sendMessage(Language.STAFF_ROLLBACK_WIPING.toString()
                        .replace("<type>", "blacklists"));
                AtomicInteger expired = new AtomicInteger(0);
                AtomicInteger active = new AtomicInteger(0);
                plugin.getMongoManager().getBlacklists().find().into(new ArrayList<>()).forEach(document -> {
                    if (document.containsKey("addedBy") && document.getString("addedBy").equalsIgnoreCase(args[0])) {
                        if (document.containsKey("addedAt")) {
                            long addedAt = document.getLong("addedAt");

                            if (check <= addedAt) {
                                plugin.getMongoManager().getBans().deleteOne(document);
                                if (document.containsKey("active") && document.getBoolean("active")) {
                                    active.getAndIncrement();
                                } else {
                                    expired.getAndIncrement();
                                }
                            }
                        }
                    }
                });
                if (expired.get() + active.get() == 0) {
                    player.sendMessage(Language.STAFF_ROLLBACK_DONT_HAVE_HISTORY.toString()
                            .replace("<type>", "blacklists")
                            .replace("<name>", target.getName()));
                    return;
                }
                player.sendMessage(Language.STAFF_ROLLBACK_WIPED.toString()
                        .replace("<amount>", String.valueOf(expired.get() + active.get()))
                        .replace("<type>", "blacklists")
                        .replace("<name>", target.getName())
                        .replace("<active>", String.valueOf(active.get()))
                        .replace("<expired>", String.valueOf(expired.get())));
                return;
            }
            if (args[2].equalsIgnoreCase("warns")) {
                player.sendMessage(Language.STAFF_ROLLBACK_WIPING.toString()
                        .replace("<type>", "warns"));
                AtomicInteger expired = new AtomicInteger(0);
                AtomicInteger active = new AtomicInteger(0);
                plugin.getMongoManager().getWarns().find().into(new ArrayList<>()).forEach(document -> {
                    if (document.containsKey("addedBy") && document.getString("addedBy").equalsIgnoreCase(args[0])) {
                        if (document.containsKey("addedAt")) {
                            long addedAt = document.getLong("addedAt");

                            if (check <= addedAt) {
                                plugin.getMongoManager().getBans().deleteOne(document);
                                if (document.containsKey("active") && document.getBoolean("active")) {
                                    active.getAndIncrement();
                                } else {
                                    expired.getAndIncrement();
                                }
                            }
                        }
                    }
                });
                if (expired.get() + active.get() == 0) {
                    player.sendMessage(Language.STAFF_ROLLBACK_DONT_HAVE_HISTORY.toString()
                            .replace("<type>", "warns")
                            .replace("<name>", target.getName()));
                    return;
                }
                player.sendMessage(Language.STAFF_ROLLBACK_WIPED.toString()
                        .replace("<amount>", String.valueOf(expired.get() + active.get()))
                        .replace("<type>", "warns")
                        .replace("<name>", target.getName())
                        .replace("<active>", String.valueOf(active.get()))
                        .replace("<expired>", String.valueOf(expired.get())));
                return;
            }
            player.sendMessage(Color.translate("&cCorrect usage: /staffrollback <staff> <time> <type>"));
            player.sendMessage(Color.translate("&cFor the type you can use 'Bans, Mutes, Blacklists or Warns'"));
        });
    }
}
