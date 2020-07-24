package me.activated.core.punishments;

import lombok.Getter;
import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.Manager;
import me.activated.core.punishments.api.PunishmentsAPI;
import me.activated.core.punishments.managers.MessagesManager;
import me.activated.core.punishments.managers.PunishmentsProfileManager;
import me.activated.core.punishments.player.PunishPlayerData;
import me.activated.core.punishments.utilities.PunishmentsConfigFile;
import me.activated.core.enums.PunishmentsLanguage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Iterator;

@Getter
public class PunishmentPlugin extends Manager {

    @Getter public static PunishmentPlugin INSTANCE;

    @Getter public static ChatColor MAIN_COLOR = ChatColor.AQUA;
    @Getter public static ChatColor SECONDARY_COLOR = ChatColor.BLUE;
    @Getter public static ChatColor MIDDLE_COLOR = ChatColor.GRAY;

    private PunishmentsConfigFile configFile, languageFile;

    private PunishmentsProfileManager profileManager;
    private MessagesManager messagesManager;

    private PunishmentsAPI api;

    public PunishmentPlugin(AquaCore plugin) {
        super(plugin);
    }

    public void onEnable() {
        INSTANCE = this;
        this.configFile = new PunishmentsConfigFile(plugin, "config.yml");
        this.languageFile = new PunishmentsConfigFile(plugin, "lang.yml");
        PunishmentsLanguage.setConfig(this.languageFile);
        this.loadLanguage();

        try {
            MAIN_COLOR = ChatColor.valueOf(configFile.getString("COLORS.MAIN"));
        } catch (Exception ignored) {}
        try {
            MIDDLE_COLOR = ChatColor.valueOf(configFile.getString("COLORS.MIDDLE"));
        } catch (Exception ignored) {}
        try {
            SECONDARY_COLOR = ChatColor.valueOf(configFile.getString("COLORS.SECONDARY"));
        } catch (Exception ignored) {}

        this.profileManager = new PunishmentsProfileManager(plugin);
        this.messagesManager = new MessagesManager(plugin);
        this.messagesManager.setup();

        api = new PunishmentsAPI(plugin);

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new PlayerDataUpdate(), 20L * 5, 20L * 5);

        AquaCore.INSTANCE.getRegisterManager().getPlayerListener();
    }

    private void loadLanguage() {
        if (this.languageFile == null) {
            return;
        }
        Arrays.stream(PunishmentsLanguage.values()).forEach(language -> {
            if (this.languageFile.getString(language.getPath(), true) == null) {
                this.languageFile.set(language.getPath(), language.getValue());
            }
        });
        this.languageFile.save();
    }

    private class PlayerDataUpdate implements Runnable {

        @Override
        public void run() {
            Iterator<PunishPlayerData> playerDataIterator = profileManager.getPlayerData().values().iterator();
            try {
                do {
                    PunishPlayerData data = playerDataIterator.next();
                    if (!data.isLoading()) {
                        data.updateBannedAlts();
                    }
                } while (playerDataIterator.hasNext());
            } catch (Exception ignored) { }
        }
    }
}
