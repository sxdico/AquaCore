package me.activated.core.commands.essentials.staff;

import me.activated.core.enums.Language;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RepairCommand extends BaseCommand {

    @Command(name = "repair", permission = "Aqua.command.repair", aliases = "fix")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Color.translate("&cCorrect usage: /repair <hand|all|armor>"));
                return;
            }
            if (args[0].equalsIgnoreCase("hand")) {
                ItemStack item = player.getItemInHand();

                if (item == null
                        || item.getType() == Material.AIR
                        || item.getType() == Material.POTION
                        || item.getType() == Material.GOLDEN_APPLE
                        || item.getType().isBlock()
                        || item.getType().getMaxDurability() < 1) {
                    player.sendMessage(Language.REPAIR_ITEM_NULL.toString());
                    return;
                }
                if (item.getDurability() == 0) {
                    player.sendMessage(Language.REPAIR_ALREADY_REPAIRED.toString());
                    return;
                }
                item.setDurability((short) 0);
                player.updateInventory();
                player.sendMessage(Language.REPAIR_ITEM_REPAIRED.toString());
                return;
            }
            if (args[0].equalsIgnoreCase("all")) {
                for (ItemStack piece : player.getInventory().getContents()) {
                    if (piece == null
                            || piece.getType() == Material.AIR
                            || piece.getType() == Material.POTION
                            || piece.getType() == Material.GOLDEN_APPLE
                            || piece.getType().isBlock()
                            || piece.getType().getMaxDurability() < 1) {
                        continue;
                    }
                    piece.setDurability((short) 0);
                }
                for (ItemStack piece : player.getInventory().getArmorContents()) {
                    if (piece == null
                            || piece.getType() == Material.AIR
                            || piece.getType() == Material.POTION
                            || piece.getType() == Material.GOLDEN_APPLE
                            || piece.getType().isBlock()
                            || piece.getType().getMaxDurability() < 1) {
                        continue;
                    }
                    piece.setDurability((short) 0);
                }
                player.updateInventory();
                player.sendMessage(Language.REPAIR_REPAIRED.toString());
                return;
            }
            if (args[0].equalsIgnoreCase("armor")) {
                for (ItemStack piece : player.getInventory().getArmorContents()) {
                    if (piece == null
                            || piece.getType() == Material.AIR
                            || piece.getType() == Material.POTION
                            || piece.getType() == Material.GOLDEN_APPLE
                            || piece.getType().isBlock()
                            || piece.getType().getMaxDurability() < 1) {
                        continue;
                    }
                    piece.setDurability((short) 0);
                }
                player.updateInventory();
                player.sendMessage(Language.REPAIR_ARMOR_REPAIRED.toString());
                return;
            }
            player.sendMessage(Color.translate("&cCorrect usage: /repair <hand|all|armor>"));
        });
    }
}
