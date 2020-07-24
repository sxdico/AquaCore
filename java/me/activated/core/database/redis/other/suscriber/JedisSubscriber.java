package me.activated.core.database.redis.other.suscriber;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import lombok.Getter;
import me.activated.core.database.redis.other.settings.JedisSettings;
import me.activated.core.database.redis.other.suscriber.handle.JedisHandle;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;


@Getter
public class JedisSubscriber {

    private static final JsonParser JSON_PARSER = new JsonParser();

    private final String channel;
    private final Jedis jedis;
    private final JedisPubSub pubSub;
    private final JedisHandle subscriptionHandler;

    public JedisSubscriber(String channel, JedisSettings settings, JedisHandle subscriptionHandler) {
        this.channel = channel;
        this.subscriptionHandler = subscriptionHandler;
        this.pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                try {
                    JsonObject object = JSON_PARSER.parse(message).getAsJsonObject();
                    JedisSubscriber.this.subscriptionHandler.handleMessage(object);
                } catch (JsonParseException e) {
                    System.out.println("Received message that could not be parsed");
                }
            }
        };
        this.jedis = new Jedis(settings.getAddress(), settings.getPort());
        if (settings.hasPassword()) {
            this.jedis.auth(settings.getPassword());
        }
        new Thread(() -> this.jedis.subscribe(this.pubSub, this.channel)).start();
    }

    public void close() {
        if (this.pubSub != null) {
            this.pubSub.unsubscribe();
        }
        if (this.jedis != null) {
            this.jedis.close();
        }
    }

}
