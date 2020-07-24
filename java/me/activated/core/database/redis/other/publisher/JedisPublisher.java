package me.activated.core.database.redis.other.publisher;


import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import me.activated.core.database.redis.other.settings.JedisSettings;
import me.activated.core.plugin.AquaCore;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RequiredArgsConstructor
public class JedisPublisher {

    private final AquaCore plugin = AquaCore.INSTANCE;

    private JedisSettings jedisSettings;

    public JedisPublisher(JedisSettings settings) {
        this.jedisSettings = settings;
    }

    public void write(String channel, JsonObject payload) {
        JedisPool pool = plugin.getRedisData().getPool();
        if (pool == null) return;

        Jedis jedis = null;

        try {
            jedis = plugin.getRedisData().getPool().getResource();

            if(plugin.getRedisData().getSettings().hasPassword()) {
                jedis.auth(plugin.getRedisData().getSettings().getPassword());
            }

            jedis.publish(channel, payload.toString());
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }
}
