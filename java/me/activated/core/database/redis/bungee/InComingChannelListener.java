package me.activated.core.database.redis.bungee;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import me.activated.core.plugin.AquaCore;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

@RequiredArgsConstructor
public class InComingChannelListener implements PluginMessageListener {
    private static final JsonParser JSON_PARSER = new JsonParser();

    private final AquaCore plugin;

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        DataInputStream in = new DataInputStream(stream);

        try {
            String channel = in.readUTF();

            if (!channel.equals("AquaChannel")) return;

            String payload = in.readUTF();

            JsonObject object = JSON_PARSER.parse(payload).getAsJsonObject();
            plugin.getRedisData().getGlobalSuscription().handleMessage(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
