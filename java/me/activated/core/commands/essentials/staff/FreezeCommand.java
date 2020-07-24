package me.activated.core.commands.essentials.staff;



import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.chat.Replacement;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FreezeCommand extends BaseCommand {

    @Command(name = "freeze", permission = "Aqua.command.freeze", aliases = {"ss", "screenshare"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(Color.translate("&cCorrect usage: /freeze <player>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Language.NOT_ONLINE.toString());
            return;
        }

        PlayerData targetProfile = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());

        player.sendMessage(this.getFreezeMessage(false, !targetProfile.isFrozen(), player, target));
        target.sendMessage(this.getFreezeMessage(true, !targetProfile.isFrozen(), player, target));

        targetProfile.setFrozen(!targetProfile.isFrozen());
        if (targetProfile.isFrozen()) {
            plugin.getCoreConfig().getStringList("freeze-message").forEach(target::sendMessage);
        }
    }

    private String getFreezeMessage(boolean isTarget, boolean isToFreeze, Player player, Player target) {
        if (isToFreeze && isTarget) {
            Replacement replacement = new Replacement(Language.FREEZE_TARGET.toString());
            replacement.add("{player}", player.getDisplayName()).add("{target}", target.getDisplayName());
            return replacement.toString();
        }
        if (isToFreeze) {
            Replacement replacement = new Replacement(Language.FREEZE_PLAYER.toString());
            replacement.add("{player}", player.getDisplayName()).add("{target}", target.getDisplayName());
            return replacement.toString();
        }
        if (isTarget) {
            Replacement replacement = new Replacement(Language.UN_FREEZE_TARGET.toString());
            replacement.add("{player}", player.getDisplayName()).add("{target}", target.getDisplayName());
            return replacement.toString();
        }
        Replacement replacement = new Replacement(Language.UN_FREEZE_PLAYER.toString());
        replacement.add("{player}", player.getDisplayName()).add("{target}", target.getDisplayName());
        return replacement.toString();
    }
}
