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

import java.util.ArrayList;
import java.util.List;

public class AddLoreCommand extends BaseCommand {

    @Command(name = "addlore", permission = "Aqua.command.addlore", aliases = "additemlore")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Color.translate("&cCorrect usage: /addlore <lore>"));
                return;
            }
            ItemStack item = player.getItemInHand();
            if (item == null || item.getType() == Material.AIR) {
                player.sendMessage(Language.LORE_ITEM_NULL.toString());
                return;
            }
            ItemMeta itemMeta = item.getItemMeta();
            if (!itemMeta.hasLore()) {
                itemMeta.setLore(new ArrayList<>());
            }

            String add = Color.translate(StringUtils.buildMessage(args, 0));

            List<String> lore = itemMeta.getLore();
            lore.add(add);
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);

            player.updateInventory();
            player.sendMessage(Language.LORE_SUCCESS.toString()
                    .replace("<lore>", Color.translate(StringUtils.buildMessage(args, 0))));
        });
    }
}
