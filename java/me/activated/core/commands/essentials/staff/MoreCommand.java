package me.activated.core.commands.essentials.staff;

import me.activated.core.enums.Language;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MoreCommand extends BaseCommand {

    @Command(name = "more", permission = "Aqua.command.more")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            ItemStack item = player.getItemInHand();

            if (item == null || item.getType() == Material.AIR) {
                player.sendMessage(Language.MORE_ITEM_NULL.toString());
                return;
            }

            item.setAmount(item.getMaxStackSize());
            player.updateInventory();

            Utilities.playSound(player, Sound.LEVEL_UP);
            player.sendMessage(Language.MORE_SUCCESS.toString());
        });
    }
}
