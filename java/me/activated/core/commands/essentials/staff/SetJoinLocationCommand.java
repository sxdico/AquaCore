package me.activated.core.commands.essentials.staff;

import me.activated.core.enums.Language;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.file.ConfigFile;
import org.bukkit.entity.Player;

public class SetJoinLocationCommand extends BaseCommand {

    @Command(name = "setjoinlocation", permission = "Aqua.command.setjoinlocation")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        ConfigFile configFile = plugin.getCoreConfig();
        configFile.set("on-join.teleport.location.x", player.getLocation().getX());
        configFile.set("on-join.teleport.location.y", player.getLocation().getY());
        configFile.set("on-join.teleport.location.z", player.getLocation().getZ());
        configFile.set("on-join.teleport.location.yaw", player.getLocation().getYaw());
        configFile.set("on-join.teleport.location.pitch", player.getLocation().getPitch());

        configFile.save();
        player.sendMessage(Language.JOIN_SPAWN_SET.toString());
        if (!configFile.getBoolean("on-join.teleport.enabled")) {
            player.sendMessage(Language.JOIN_SPAWN_NOTE.toString());
        }
    }
}
