package me.activated.core.database.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import me.activated.core.plugin.AquaCore;
import me.activated.core.enums.Language;
import me.activated.core.utilities.Manager;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.file.ConfigFile;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class MongoManager extends Manager {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> documentation,
            ranks,
            globalCooldowns,
            bans,
            mutes,
            kicks,
            warns,
            blacklists,
            punishPlayerData,
            tags,
            punishHistory,
            notes;

    private final ConfigFile configFile = plugin.getDataBase();

    public MongoManager(AquaCore plugin) {
        super(plugin);
    }

    public boolean connect() {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);
        try {
            if (this.configFile.getBoolean("MONGODB.AUTHENTICATION.ENABLED")) {
                MongoCredential credential = MongoCredential.createCredential(
                        this.configFile.getString("MONGODB.AUTHENTICATION.USERNAME"),
                        this.configFile.getString("MONGODB.AUTHENTICATION.DATABASE"),
                        this.configFile.getString("MONGODB.AUTHENTICATION.PASSWORD").toCharArray()
                );

                mongoClient = new MongoClient(new ServerAddress(this.configFile.getString("MONGODB.ADDRESS"),
                        this.configFile.getInt("MONGODB.PORT")), Collections.singletonList(credential));
            } else {
                mongoClient = new MongoClient(this.configFile.getString("MONGODB.ADDRESS"),
                        this.configFile.getInt("MONGODB.PORT"));
            }
            mongoDatabase = mongoClient.getDatabase(this.configFile.getString("MONGODB.DATABASE"));
            documentation = mongoDatabase.getCollection("Aqua-Documentation");
            ranks = mongoDatabase.getCollection("Aqua-Ranks");
            tags = mongoDatabase.getCollection("Aqua-Tags");
            globalCooldowns = mongoDatabase.getCollection("Aqua-GCooldowns");
            bans = mongoDatabase.getCollection("Aqua-Bans");
            mutes = mongoDatabase.getCollection("Aqua-Mutes");
            kicks = mongoDatabase.getCollection("Aqua-Kicks");
            warns = mongoDatabase.getCollection("Aqua-Warns");
            blacklists = mongoDatabase.getCollection("Aqua-Blacklists");
            punishPlayerData = mongoDatabase.getCollection("Aqua-Data");
            punishHistory = mongoDatabase.getCollection("Aqua-PunishHistory");
            notes = mongoDatabase.getCollection("Aqua-Notes");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(Color.translate(Language.PREFIX + "&cDisabling Aqua Core due to issues with mongo database."));
            Bukkit.getServer().getPluginManager().disablePlugin(this.plugin);
            return false;
        }
    }
}
