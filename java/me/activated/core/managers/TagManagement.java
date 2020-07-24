package me.activated.core.managers;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import me.activated.core.database.redis.other.bson.JsonChain;
import me.activated.core.database.redis.payload.action.JedisAction;
import me.activated.core.plugin.AquaCore;
import me.activated.core.api.player.PlayerData;
import me.activated.core.api.tags.Tag;
import me.activated.core.enums.Language;
import me.activated.core.utilities.Manager;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.chat.ColorUtil;
import me.activated.core.utilities.chat.Replacement;
import me.activated.core.utilities.file.ConfigFile;
import me.activated.core.utilities.general.Tasks;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
public class TagManagement extends Manager {
    private List<Tag> tags = new ArrayList<>();

    public TagManagement(AquaCore plugin) {
        super(plugin);

        Tasks.runAsync(plugin, () -> {
            this.importTags();
            this.loadTags();
            this.saveTags();
        });
    }

    public Tag getTag(String name) {
        return this.tags.stream().filter(rankData -> rankData.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void deleteTag(Tag tag) {
        Document document = plugin.getMongoManager().getTags().find(Filters.eq("name", tag.getName())).first();
        if (document != null) {
            plugin.getMongoManager().getTags().deleteOne(document);
        }
        this.tags.remove(tag);
    }

    public void requestTagsUpdate() {
        plugin.getRedisData().write(JedisAction.TAGS_UPDATE, new JsonChain().get());
    }

    public void saveTags() {
        tags.forEach(tag -> {
            Document document = new Document();
            document.put("name", tag.getName());
            document.put("prefix", tag.getPrefix().replace("§", "&"));
            document.put("color", ColorUtil.convertChatColor(tag.getColor()));
            document.put("weight", tag.getWeight());

            plugin.getMongoManager().getTags().replaceOne(Filters.eq("name", tag.getName()), document, new UpdateOptions().upsert(true));
        });
    }

    public void loadTags() {
        this.tags.clear();
        plugin.getMongoManager().getTags().find().into(new ArrayList<>()).forEach(document -> {

            Tag tag = new Tag();
            tag.setName(document.getString("name"));
            tag.setPrefix(document.getString("prefix"));
            tag.setWeight(document.getInteger("weight"));
            try {
                tag.setColor(ChatColor.valueOf(document.getString("color")));
            } catch (Exception ignored) {
            }

            this.tags.add(tag);
        });
        if (plugin.getMongoManager().getTags().find().into(new ArrayList<>()).size() == 0) {
            this.importTags();
        }
    }

    public void importTags() {
        this.tags.clear();
        plugin.getTags().getKeys(false).forEach(key -> {
            ChatColor color;
            try {
                color = ChatColor.valueOf(plugin.getTags().getString(key + ".color"));
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(Color.translate(Language.PREFIX.toString() + "&cFailed to set &e"
                        + plugin.getTags().getString(key + ".color") + " &cas color, using white for now."));
                color = ChatColor.WHITE;
            }

            Tag tag = new Tag();
            tag.setName(plugin.getTags().getString(key + ".name"));
            tag.setPrefix(plugin.getTags().getString(key + ".prefix"));
            tag.setWeight(plugin.getTags().getInt(key + ".weight"));
            tag.setColor(color);

            this.tags.add(tag);
        });
    }

    public void saveTagsToConfig() {
        ConfigFile file = plugin.getTags();
        file.getKeys(false).forEach(key -> {
            file.set(key, null);
        });
        this.tags.stream().sorted(Comparator.comparingInt(Tag::getWeight).reversed()).forEach(tag -> {
            file.set(tag.getName() + ".name", tag.getName().replace(" ", "_"));
            file.set(tag.getName() + ".prefix", tag.getPrefix().replace("§", "&"));
            file.set(tag.getName() + ".color", ColorUtil.convertChatColor(tag.getColor()));
            file.set(tag.getName() + ".weight", tag.getWeight());
        });
        file.save();
    }

    public String getTagPrefix(Player player) {
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        Tag tag = playerData.getTag();

        if (tag == null) return "";
        if (!player.hasPermission("Aqua.tags." + tag.getName().toLowerCase())) return "";

        Replacement format = new Replacement(plugin.getCoreConfig().getString("tags-format"));
        format.add("<color>", tag.getColor().toString());
        ChatColor uniqueColor;
        try {
            uniqueColor = ChatColor.valueOf(playerData.getTagColor());
        } catch (Exception e) {
            uniqueColor = null;
        }
        format.add("<uniqueColor>", uniqueColor != null ? uniqueColor.toString() : "");
        format.add("<tag>", tag.getPrefix());

        return format.toString();
    }

    public void deleteTags() {
        plugin.getMongoManager().getTags().drop();
        this.tags.clear();
    }
}
