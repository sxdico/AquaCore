package me.activated.core.punishments.player;

import com.mongodb.Block;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.general.StringUtils;
import me.activated.core.punishments.utilities.punishments.Alt;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class PunishPlayerData {
    private AquaCore plugin = AquaCore.INSTANCE;

    private final UUID uniqueId;
    private final String playerName;
    private String lastSeen, address;
    private boolean loading = true;

    private List<String> addresses = new ArrayList<>();
    private List<Alt> potentialAlts = new ArrayList<>();
    private List<Alt> alts = new ArrayList<>();

    private PunishData punishData = new PunishData(this);

    public void save() {
        Document document = new Document();

        document.put("uuid", this.uniqueId.toString());
        document.put("name", this.playerName);
        document.put("lowerCaseName", this.playerName.toLowerCase());

        document.put("last_seen", new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date()));
        document.put("addresses", StringUtils.getStringFromList(this.addresses));
        document.put("address", this.address);

        plugin.getMongoManager().getPunishPlayerData().replaceOne(Filters.eq("uuid", this.getUniqueId().toString()), document, new UpdateOptions().upsert(true));
    }

    public boolean hasPlayedBefore() {
        Document document = plugin.getMongoManager().getPunishPlayerData().find(Filters.eq("uuid", this.uniqueId.toString())).first();
        return document != null;
    }

    public void load() {
        this.loading = true;
        Document document = plugin.getMongoManager().getPunishPlayerData().find(Filters.eq("uuid", this.uniqueId.toString())).first();

        if (document == null) {
            this.save();
            this.checkForPotentialAlts();
            return;
        }

        Player player = Bukkit.getPlayer(this.playerName);

        if (player != null) {
            this.lastSeen = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date());
        } else if (document.containsKey("last_seen")) {
            this.lastSeen = document.getString("last_seen");
        }
        this.addresses = StringUtils.getListFromString(document.getString("addresses"));
        this.address = document.getString("address");

        this.checkForPotentialAlts();

        this.loading = false;
    }

    public void checkForAddressChanges(String address) {
        if (!this.addresses.contains(address)) {
            this.addresses.add(address);
        }
    }

    public Alt getPotentialAltByName(String name) {
        return this.potentialAlts.stream().filter(alt -> alt.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Alt getIPAltByName(String name) {
        return this.alts.stream().filter(alt -> alt.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void updateBannedAlts() {
        this.potentialAlts.forEach(alt -> {
            if (Bukkit.getPlayer(alt.getName()) == null) {
                alt.setBanned(plugin.getPunishmentPlugin().getProfileManager().isBanned(Bukkit.getOfflinePlayer(alt.getUniqueId())));
            }
        });
        this.alts.forEach(alt -> {
            if (Bukkit.getPlayer(alt.getName()) == null) {
                alt.setBanned(plugin.getPunishmentPlugin().getProfileManager().isBanned(Bukkit.getOfflinePlayer(alt.getUniqueId())));
            }
        });
    }

    public void checkForPotentialAlts() {
        this.alts.clear();
        this.potentialAlts.clear();

        //Loading potential alts, alts that are recorded on every ip of this player data
        plugin.getMongoManager().getPunishPlayerData().find().forEach((Block<Document>) document -> {
            if (!document.getString("uuid").equalsIgnoreCase(uniqueId.toString())) {
                if (document.containsKey("name") && document.containsKey("uuid")
                        && document.containsKey("addresses")) {

                    String name = document.getString("name");
                    UUID uuid = UUID.fromString(document.getString("uuid"));
                    List<String> addresses = StringUtils.getListFromString(document.getString("addresses"));

                    for (String address : this.addresses) {
                        if ((this.getPotentialAltByName(name) == null || !this.potentialAlts.contains(this.getPotentialAltByName(name))) && addresses.contains(address)) {
                            this.potentialAlts.add(new Alt(uuid, name, plugin.getPunishmentPlugin().getProfileManager().isBanned(Bukkit.getOfflinePlayer(uuid))));
                        }
                    }
                }
            }
        });
        //Loading more potential alts, alts that are on the last ip recorded for this player data
        plugin.getMongoManager().getPunishPlayerData().find().forEach((Block<Document>) document -> {
            if (!document.getString("uuid").equalsIgnoreCase(uniqueId.toString())) {
                if (document.containsKey("name") && document.containsKey("uuid")
                        && document.containsKey("address")) {
                    String name = document.getString("name");
                    UUID uuid = UUID.fromString(document.getString("uuid"));
                    String address = document.getString("address");

                    if ((this.getIPAltByName(name) == null || !this.alts.contains(this.getIPAltByName(name))) && address != null && address.equalsIgnoreCase(this.address)) {
                        this.alts.add(new Alt(uuid, name, plugin.getPunishmentPlugin().getProfileManager().isBanned(Bukkit.getOfflinePlayer(uuid))));
                    }
                }
            }
        });
    }
}
