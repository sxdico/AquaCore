package me.activated.core.commands.essentials.staff;

import me.activated.core.enums.Language;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class SpeedCommand extends BaseCommand {

    @Command(name = "speed", permission = "Aqua.command.speed")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(Color.translate("&cCorrect usage: /speed <fly|walk> <amount>"));
            return;
        }
        if (!args[0].equalsIgnoreCase("fly")
                && !args[0].equalsIgnoreCase("walk")) {
            player.sendMessage(Color.translate("&cCorrect usage: /speed <fly|walk> <amount>"));
            return;
        }
        if (!Utilities.isNumberInteger(args[1])) {
            player.sendMessage(Language.USE_NUMBERS.toString());
            return;
        }

        int amount = Integer.parseInt(args[1]);

        if (amount < 1 || amount > 10) {
            player.sendMessage(Language.SPEED_LIMITED.toString());
            return;
        }

        if (args[0].equalsIgnoreCase("fly")) {
            player.setFlySpeed(amount * 0.1F);
            player.sendMessage(Language.SPEED_FLY_SET.toString()
                    .replace("<amount>", String.valueOf(amount)));
        } else if (args[0].equalsIgnoreCase("walk")) {
            player.setWalkSpeed(amount * 0.1F);
            player.sendMessage(Language.SPEED_WALK_SET.toString()
                    .replace("<amount>", String.valueOf(amount)));
        }
    }
}
