package me.activated.core.commands.essentials.staff.item;

import me.activated.core.enums.Language;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class RemoveLoreCommand extends BaseCommand {

    @Command(name = "removelore", permission = "Aqua.command.removelore", aliases = "removeitemlore")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Color.translate("&cCorrect usage: /removelore <number>"));
                return;
            }
            if (!Utilities.isNumberInteger(args[0])) {
                player.sendMessage(Language.USE_NUMBERS.toString());
                return;
            }
            ItemStack item = player.getItemInHand();
            if (item == null || item.getType() == Material.AIR) {
                player.sendMessage(Language.LORE_REMOVE_ITEM_NULL.toString());
                return;
            }
            ItemMeta itemMeta = item.getItemMeta();
            if (!itemMeta.hasLore() || Integer.parseInt(args[0]) >= itemMeta.getLore().size()) {
                player.sendMessage(Language.LORE_REMOVE_FAIL.toString());
                return;
            }
            List<String> lore = itemMeta.getLore();
            lore.remove(Integer.parseInt(args[0]));
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);

            player.updateInventory();
            player.sendMessage(Language.LORE_REMOVE_SUCCESS.toString()
                    .replace("<number>", args[0]));
        });
    }
}
