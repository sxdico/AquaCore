package me.activated.core.commands.coins;

import me.activated.core.api.player.PlayerData;
import me.activated.core.menus.coins.CoinsMenu;
import me.activated.core.enums.Language;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoinsCommand extends BaseCommand {

    @Command(name = "coins", permission = "Aqua.command.coins", inGameOnly = false)
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            String[] args = command.getArgs();
            CommandSender sender = command.getSender();

            if (command.isPlayer()) {
                Player player = command.getPlayer();
                PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

                if (args.length == 0) {
                    player.sendMessage(Language.COINS_MESSAGE.toString()
                            .replace("<coins>", String.valueOf(playerData.getCoins()))
                            .replace("<amount>", String.valueOf(playerData.getPurchasableRanks().size())));
                    return;
                }
                if (args[0].equalsIgnoreCase("buy")) {
                    new CoinsMenu().open(player);
                    return;
                }
                if (!player.hasPermission("Aqua.command.coins.admin")) {
                    player.sendMessage(" ");
                    player.sendMessage(Color.translate("&eIf you want to purchase ranks use"));
                    player.sendMessage(Color.translate("&b/coins buy &eto purchase!"));
                    player.sendMessage(" ");
                    return;
                }
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("set")) {
                    Player target = Bukkit.getPlayer(args[1]);

                    if (target == null) {
                        sender.sendMessage(Language.NOT_ONLINE.toString());
                        return;
                    }
                    PlayerData targetAccount = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());
                    if (!Utilities.isNumberInteger(args[2]) || Integer.parseInt(args[2]) < 0) {
                        sender.sendMessage(Language.USE_NUMBERS.toString());
                        return;
                    }

                    targetAccount.setCoins(Integer.parseInt(args[2]));

                    sender.sendMessage(Language.COINS_SET.toString()
                            .replace("<player>", target.getName())
                            .replace("<amount>", String.valueOf(targetAccount.getCoins())));
                    return;
                }
                if (args[0].equalsIgnoreCase("add")) {
                    Player target = Bukkit.getPlayer(args[1]);

                    if (target == null) {
                        sender.sendMessage(Language.NOT_ONLINE.toString());
                        return;
                    }
                    PlayerData targetAccount = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());
                    if (!Utilities.isNumberInteger(args[2]) || Integer.parseInt(args[2]) <= 0) {
                        sender.sendMessage(Language.USE_NUMBERS.toString());
                        return;
                    }

                    targetAccount.setCoins(targetAccount.getCoins() + Integer.parseInt(args[2]));

                    sender.sendMessage(Language.COINS_SET.toString()
                            .replace("<player>", target.getName())
                            .replace("<amount>", String.valueOf(targetAccount.getCoins())));
                    return;
                }
                if (args[0].equalsIgnoreCase("remove")) {
                    Player target = Bukkit.getPlayer(args[1]);

                    if (target == null) {
                        sender.sendMessage(Language.NOT_ONLINE.toString());
                        return;
                    }
                    PlayerData targetAccount = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());
                    if (!Utilities.isNumberInteger(args[2]) || Integer.parseInt(args[2]) <= 0) {
                        sender.sendMessage(Language.USE_NUMBERS.toString());
                        return;
                    }
                    if (Integer.parseInt(args[2]) > targetAccount.getCoins()) {
                        sender.sendMessage(Color.translate("&cInvalid amount."));
                        return;
                    }

                    targetAccount.setCoins(targetAccount.getCoins() - Integer.parseInt(args[2]));

                    sender.sendMessage(Language.COINS_SET.toString()
                            .replace("<player>", target.getName())
                            .replace("<amount>", String.valueOf(targetAccount.getCoins())));
                    return;
                }
                sender.sendMessage(" ");
                sender.sendMessage(Color.translate("&e&lCoins HELP"));
                sender.sendMessage(Color.translate("&e/coins set <player> <amount>"));
                sender.sendMessage(Color.translate("&e/coins add <player> <amount>"));
                sender.sendMessage(Color.translate("&e/coins remove <player> <amount>"));
                sender.sendMessage(" ");
            }
        });
    }
}
