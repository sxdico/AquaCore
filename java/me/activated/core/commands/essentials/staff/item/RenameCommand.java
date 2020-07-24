package me.activated.core.commands.essentials.staff.item;

import me.activated.core.enums.Language;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.StringUtils;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RenameCommand extends BaseCommand {

    @Command(name = "rename", permission = "Aqua.command.rename", aliases = "renameitem")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Color.translate("&cCorrect usage: /rename <displayName>"));
                return;
            }
            ItemStack item = player.getItemInHand();
            if (item == null || item.getType() == Material.AIR) {
                player.sendMessage(Language.RENAME_ITEM_NULL.toString());
                return;
            }
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(Color.translate(StringUtils.buildMessage(args, 0)));
            item.setItemMeta(itemMeta);

            player.updateInventory();
            player.sendMessage(Language.RENAME_SUCCESS.toString()
                    .replace("<name>", Color.translate(StringUtils.buildMessage(args, 0))));
        });
    }
}
