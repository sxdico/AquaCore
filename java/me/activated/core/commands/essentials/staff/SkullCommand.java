package me.activated.core.commands.essentials.staff;

import me.activated.core.enums.Language;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import me.activated.core.utilities.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SkullCommand extends BaseCommand {

    @Command(name = "skull", permission = "Aqua.command.skull", aliases = {"head", "getskull", "gethead"})
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Color.translate("&cCorrect usage: /skull <name>"));
                return;
            }
            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(Language.SKULL_INV_FULL.toString());
                return;
            }
            player.getInventory().addItem(new ItemBuilder(Material.SKULL_ITEM).setDurability(3).setSkullOwner(args[0]).toItemStack());

            player.sendMessage(Language.SKULL_GIVEN.toString()
                    .replace("<name>", args[0]));
        });
    }
}
