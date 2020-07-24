package me.activated.core.utilities.command;


import me.activated.core.plugin.AquaCore;

public abstract class BaseCommand {
    public AquaCore plugin = AquaCore.INSTANCE;

    public BaseCommand() {
        plugin.getCommandFramework().registerCommands(this, null);
    }

    public abstract void onCommand(CommandArgs command);
}
