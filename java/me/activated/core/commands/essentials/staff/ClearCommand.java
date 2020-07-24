package me.activated.core.commands.essentials.staff;

import me.activated.core.enums.Language;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ClearCommand extends BaseCommand {

    @Command(name = "clear", permission = "Aqua.command.clear", aliases = {"clearinv"})
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                int total = this.getTotalItems(player);

                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                player.updateInventory();

                player.sendMessage(Language.INVENTORY_CLEAR_SELF.toString()
                        .replace("<total>", String.valueOf(total)));
                return;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(Language.NOT_ONLINE.toString());
                return;
            }
            int total = this.getTotalItems(target);

            target.getInventory().clear();
            target.getInventory().setArmorContents(null);
            target.updateInventory();

            player.sendMessage(Language.INVENTORY_CLEAR_OTHER_SENDER.toString()
                    .replace("<target>", target.getDisplayName())
                    .replace("<total>", String.valueOf(total)));
            target.sendMessage(Language.INVENTORY_CLEAR_OTHER_TARGET.toString()
                    .replace("<player>", player.getDisplayName())
                    .replace("<total>", String.valueOf(total)));
        });
    }

    private int getTotalItems(Player player) {
        int index = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) index += item.getAmount();
        }
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item != null) index += item.getAmount();
        }
        return index;
    }
}
