package me.activated.core.api.tags;

import lombok.Getter;
import lombok.Setter;
import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.chat.Replacement;
import org.bukkit.ChatColor;

@Getter
@Setter
public class Tag {
    private AquaCore plugin = AquaCore.INSTANCE;

    private String prefix = "", name = "";
    private ChatColor color = ChatColor.WHITE;
    private int weight;

    public String getFormat() {
        Replacement format = new Replacement(plugin.getCoreConfig().getString("tags-format"));
        format.add("<color>", color.toString());
        format.add("<uniqueColor>", "");
        format.add("<tag>", this.prefix);
        return format.toString();
    }
}
