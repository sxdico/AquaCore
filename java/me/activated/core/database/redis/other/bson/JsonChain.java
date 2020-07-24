package me.activated.core.database.redis.other.bson;

import com.google.gson.JsonObject;

public class JsonChain {

    private final JsonObject json = new JsonObject();

    public JsonChain addProperty(String property, String value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonChain addProperty(String property, Number value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonChain addProperty(String property, Boolean value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonChain addProperty(String property, Character value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonObject get() {
        return this.json;
    }

}
