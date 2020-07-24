package me.activated.core.api.rank;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.chat.ColorUtil;
import me.activated.core.utilities.general.StringUtils;
import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.RankType;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class RankData {
    private AquaCore plugin = AquaCore.INSTANCE;

    private final String name;

    private boolean defaultRank = false;
    private int weight = 1;

    private RankType rankType = RankType.DEFAULT;

    private List<String> permissions = new ArrayList<>();
    private List<String> bungeePermissions = new ArrayList<>();
    private List<String> inheritance = new ArrayList<>();

    private String prefix = "", suffix = "";
    private ChatColor color = ChatColor.WHITE, chatColor = ChatColor.WHITE;
    private boolean bold = false, italic = false, purchasable = false, bungee = false;
    private int coinsCost = 0;

    public void save() {
        Document document = new Document();
        document.put("name", this.getName());
        document.put("permissions", StringUtils.getStringFromList(this.getPermissions()));
        document.put("bungeePermissions", StringUtils.getStringFromList(this.getBungeePermissions()));
        document.put("prefix", this.getPrefix().replace("§", "&"));
        document.put("suffix", this.getSuffix().replace("§", "&"));
        document.put("color", ColorUtil.convertChatColor(this.getColor()));
        document.put("default", this.isDefaultRank());
        document.put("inheritance", StringUtils.getStringFromList(this.getInheritance()));
        document.put("weight", this.getWeight());
        document.put("italic", this.italic);
        document.put("bold", this.bold);
        document.put("coinsCost", this.coinsCost);
        document.put("purchasable", this.purchasable);
        document.put("chatColor", ColorUtil.convertChatColor(this.getChatColor()));
        document.put("bungee", this.bungee);
        document.put("type", this.rankType.toString());
        plugin.getMongoManager().getRanks().replaceOne(Filters.eq("name", this.getName()), document, new UpdateOptions().upsert(true));
    }

    public boolean hasPermission(String value) {
        return this.permissions.stream().filter(permission -> permission.equalsIgnoreCase(value)).findFirst().orElse(null) != null;
    }

    public boolean hasBungeePermission(String value) {
        return this.bungeePermissions.stream().filter(permission -> permission.equalsIgnoreCase(value)).findFirst().orElse(null) != null;
    }

    public boolean hasInheritance(String value) {
        return this.inheritance.stream().filter(inheritance -> inheritance.equalsIgnoreCase(value)).findFirst().orElse(null) != null;
    }

    public String getDisplayName() {
        if (this.isItalic() && this.isBold()) {
            return this.getColor() + ChatColor.BOLD.toString() + ChatColor.ITALIC.toString() + this.getName();
        }
        if (this.isBold()) {
            return this.getColor() + ChatColor.BOLD.toString() + this.getName();
        }
        if (this.isItalic()) {
            return this.getColor() + ChatColor.ITALIC.toString() + this.getName();
        }
        return this.getColor() + this.getName();
    }

    public String getDisplayColor() {
        if (this.isItalic() && this.isBold()) {
            return this.getColor().toString() + ChatColor.BOLD.toString() + ChatColor.ITALIC.toString();
        }
        if (this.isBold()) {
            return this.getColor().toString() + ChatColor.BOLD.toString();
        }
        if (this.isItalic()) {
            return this.getColor().toString() + ChatColor.ITALIC.toString();
        }
        return this.getColor().toString();
    }

    public String formatName(Player player) {
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        String nameColor = this.getColor().toString();
        if (playerData != null && playerData.getNameColor() != null) {
            nameColor = playerData.getNameColor();
        }
        if (this.isItalic() && this.isBold()) {
            return nameColor + ChatColor.BOLD.toString() + ChatColor.ITALIC.toString() + player.getName();
        }
        if (this.isBold()) {
            return nameColor + ChatColor.BOLD.toString() + player.getName();
        }
        if (this.isItalic()) {
            return nameColor + ChatColor.ITALIC.toString() + player.getName();
        }
        return nameColor + player.getName();
    }
}
