package me.activated.core.data.other;

import com.mongodb.client.model.Filters;
import lombok.Getter;
import me.activated.core.api.player.PlayerData;
import org.bson.Document;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class GlobalCooldowns extends PlayerData {
    private final DecimalFormat SECONDS_FORMAT = new DecimalFormat("#0.0");

    private final Map<String, Map<Long, Long>> cooldowns = new HashMap<>();

    public GlobalCooldowns(UUID uniqueId, String playerName) {
        super(uniqueId, playerName);
    }

    public void createCooldown(String name, long current, long endTime) {
        if (this.hasCooldown(name)) return;
        this.cooldowns.remove(name);
        Map<Long, Long> cooldown = new HashMap<>();
        cooldown.put(current, endTime);
        this.cooldowns.put(name.toLowerCase(), cooldown);
    }

    public void removeCooldown(String name) {
        this.cooldowns.remove(name.toLowerCase());
    }

    public boolean hasCooldown(String name) {
        if (!this.cooldowns.containsKey(name.toLowerCase())) return false;
        return this.getStartTime(name) + this.getEndTime(name) >= System.currentTimeMillis();
    }

    private long getEndTime(String name) {
        return new ArrayList<>(this.cooldowns.get(name.toLowerCase()).values()).get(0);
    }

    private long getStartTime(String name) {
        return new ArrayList<>(this.cooldowns.get(name.toLowerCase()).keySet()).get(0);
    }

    public long getRemaining(String name) {
        return getEndTime(name) + getStartTime(name) - System.currentTimeMillis();
    }

    public int getSecondsLeft(String name) {
        return (int) getRemaining(name) / 1000;
    }

    public String getMiliSecondsLeft(String name) {
        if (!hasCooldown(name)) return "0";
        return formatSeconds(this.getRemaining(name));
    }

    private String formatSeconds(long time) {
        return SECONDS_FORMAT.format(time / 1000.0F);
    }

    public void loadCooldowns() {
        this.cooldowns.clear();
        getPlugin().getMongoManager().getGlobalCooldowns().find(Filters.eq("uuid", this.getUniqueId().toString())).into(new ArrayList<>()).forEach(document -> {
            this.createCooldown(document.getString("name"), document.getLong("start"), document.getLong("endTime"));
        });
    }

    public void saveCooldowns() {
        getPlugin().getMongoManager().getGlobalCooldowns().find(Filters.eq("uuid", this.getUniqueId().toString())).into(new ArrayList<>()).forEach(
                getPlugin().getMongoManager().getGlobalCooldowns()::deleteOne);
        this.cooldowns.keySet().forEach(key -> {
            Document document = new Document();
            document.put("uuid", this.getUniqueId().toString());
            document.put("name", key);
            document.put("endTime", this.getEndTime(key));
            document.put("start", this.getStartTime(key));

            getPlugin().getMongoManager().getGlobalCooldowns().insertOne(document);
        });
    }
}
