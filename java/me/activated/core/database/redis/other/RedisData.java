package me.activated.core.database.redis.other;

import com.google.gson.JsonObject;
import lombok.Getter;
import me.activated.core.plugin.AquaCore;
import me.activated.core.database.redis.other.publisher.JedisPublisher;
import me.activated.core.database.redis.other.settings.JedisSettings;
import me.activated.core.database.redis.other.suscriber.JedisSubscriber;
import me.activated.core.database.redis.payload.GlobalSuscription;
import me.activated.core.database.redis.payload.action.JedisAction;
import me.activated.core.enums.Language;
import me.activated.core.utilities.chat.Color;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
public class RedisData {
    private final AquaCore plugin = AquaCore.INSTANCE;

    private JedisSettings settings;
    private JedisPool pool;
    private JedisPublisher publisher;
    private JedisSubscriber subscriber;
    private boolean connected = true;
    private GlobalSuscription globalSuscription;

    public RedisData(JedisSettings settings) {
        this.globalSuscription = new GlobalSuscription();

        try {
            this.settings = settings;
            this.pool = new JedisPool(this.settings.getAddress(), this.settings.getPort());
            Jedis jedis = this.pool.getResource();
            try {
                if (this.settings.hasPassword()) {
                    jedis.auth(this.settings.getPassword());
                }
                this.publisher = new JedisPublisher(this.settings);
                this.subscriber = new JedisSubscriber("Aquacore", this.settings, new GlobalSuscription());

                Bukkit.getConsoleSender().sendMessage(" ");
                Bukkit.getConsoleSender().sendMessage(Color.translate(Language.PREFIX + "&aRedis data has been connected successfully!"));
                Bukkit.getConsoleSender().sendMessage(" ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ignored) {
            if (!plugin.getEssentialsManagement().isBungeeSupport()) {
                Bukkit.getConsoleSender().sendMessage(" ");
                Bukkit.getConsoleSender().sendMessage(Color.translate(Language.PREFIX + "&cRedis failed to setup, please note that the per server synchronization will not work!!!"));
                Bukkit.getConsoleSender().sendMessage(" ");
            } else {
                Bukkit.getConsoleSender().sendMessage(" ");
                Bukkit.getConsoleSender().sendMessage(Color.translate(Language.PREFIX + "&cRedis failed to setup but Bungee support is enabled."));
                Bukkit.getConsoleSender().sendMessage(Color.translate(Language.PREFIX + "&aPlease make sure you're using bungee if you want synchronization to work!"));
                Bukkit.getConsoleSender().sendMessage(" ");
            }
            this.connected = false;
        }
    }

    public void reload() {
        plugin.getDataBase().load();
        this.globalSuscription = new GlobalSuscription();
        JedisSettings jedisSettings = new JedisSettings(plugin.getDataBase().getString("REDIS.HOST"),
                plugin.getDataBase().getInt("REDIS.PORT"),
                plugin.getDataBase().getString("REDIS.PASSWORD"));

        try {
            this.settings = jedisSettings;
            this.pool = new JedisPool(this.settings.getAddress(), this.settings.getPort());
            Jedis jedis = this.pool.getResource();
            try {
                if (this.settings.hasPassword()) {
                    jedis.auth(this.settings.getPassword());
                }
                this.publisher = new JedisPublisher(this.settings);
                this.subscriber = new JedisSubscriber("Aquacore", this.settings, new GlobalSuscription());

                Bukkit.getConsoleSender().sendMessage(" ");
                Bukkit.getConsoleSender().sendMessage(Color.translate(Language.PREFIX + "&aRedis data has been connected successfully!"));
                Bukkit.getConsoleSender().sendMessage(" ");
                this.connected = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ignored) {
            if (!plugin.getEssentialsManagement().isBungeeSupport()) {
                Bukkit.getConsoleSender().sendMessage(" ");
                Bukkit.getConsoleSender().sendMessage(Color.translate(Language.PREFIX + "&cRedis failed to setup, please note that the per server synchronization will not work!!!"));
                Bukkit.getConsoleSender().sendMessage(" ");
            } else {
                Bukkit.getConsoleSender().sendMessage(" ");
                Bukkit.getConsoleSender().sendMessage(Color.translate(Language.PREFIX + "&cRedis failed to setup but Bungee support is enabled."));
                Bukkit.getConsoleSender().sendMessage(Color.translate(Language.PREFIX + "&aPlease make sure you're using bungee if you want synchronization to work!"));
                Bukkit.getConsoleSender().sendMessage(" ");
            }
            this.connected = false;
        }
    }

    public boolean isActive() {
        return this.pool != null && !this.pool.isClosed();
    }

    public void write(JedisAction payload, JsonObject data) {
        if (!isConnected() || !isActive()) {
            JsonObject object = new JsonObject();
            object.addProperty("payload", payload.name());
            object.add("data", data == null ? new JsonObject() : data);

            if (plugin.getEssentialsManagement().isBungeeSupport()) {
                this.sendChannelToBungee(object.toString());
                return;
            }

            this.globalSuscription.handleMessage(object);
            return;
        }
        JsonObject object = new JsonObject();
        object.addProperty("payload", payload.name());
        object.add("data", data == null ? new JsonObject() : data);
        this.publisher.write("Aquacore", object);
    }

    public void sendChannelToBungee(String object) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("AquaChannel");
            out.writeUTF(object);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(Color.translate("&cFailed to send synchronization to bungee, &ccontact plugin developer if you think this is an issue."));
        }
        Bukkit.getServer().sendPluginMessage(plugin, "AquaSync", b.toByteArray());
    }
}
