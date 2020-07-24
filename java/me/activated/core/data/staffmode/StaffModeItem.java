package me.activated.core.data.staffmode;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.file.ConfigFile;
import me.activated.core.utilities.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
public class StaffModeItem {
    private final String key;
    private final ConfigFile configFile;

    private String name;
    private Material item;
    private int durability, slot;
    private Action action;
    private Command command;
    private List<String> lore;

    public StaffModeItem setup() {
        this.name = configFile.getString("items." + key + ".name");
        this.lore = configFile.getStringList("items." + key + ".lore");
        try {
            this.item = Material.valueOf(configFile.getString("items." + key + ".item"));
        } catch (Exception ignored) {
            this.item = Material.REDSTONE;
            this.name = Color.translate("&c&lERROR ! ! !");
        }
        this.durability = configFile.getInt("items." + key + ".durability");
        this.slot = configFile.getInt("items." + key + ".slot") - 1;
        this.action = new Action(configFile.getBoolean("items." + key + ".action.enabled"),
                configFile.getString("items." + key + ".action.action"));
        this.command = new Command(configFile.getBoolean("items." + key + ".command.enabled"),
                configFile.getString("items." + key + ".command.command"));
        return this;
    }

    public ItemStack build() {
        ItemBuilder item = new ItemBuilder(this.item);
        item.setName(this.name);
        item.setDurability(this.durability);
        item.setLore(this.lore);
        return item.toItemStack();
    }

    @AllArgsConstructor
    @Getter
    public class Action {
        private final boolean enabled;
        private final String action;
    }

    @AllArgsConstructor
    @Getter
    public class Command {
        private final boolean enabled;
        private final String command;
    }
}