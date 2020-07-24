package me.activated.core.database.redis.other.suscriber.handle;

import com.google.gson.JsonObject;

public interface JedisHandle {

    void handleMessage(JsonObject json);
}
