package me.activated.core.punishments.utilities.punishments;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Alt {

    private UUID uniqueId;
    private String name;
    private boolean banned;

    public ChatColor getNameColor() {
        if (isBanned()) return ChatColor.RED;
        Player player = Bukkit.getPlayer(this.name);
        if (player == null) {
            return ChatColor.YELLOW;
        } else {
            return ChatColor.GREEN;
        }
    }
}
