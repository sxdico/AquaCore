package me.activated.core.data.other;

import lombok.Getter;
import lombok.Setter;
import me.activated.core.api.player.PlayerData;
import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.EffectSerilization;
import me.activated.core.utilities.item.BukkitSerilization;
import me.activated.core.utilities.server.LocationSerilization;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class OfflineInventory {
    private final AquaCore plugin;
    private final PlayerData playerData;

    private ItemStack[] inventory = new ItemStack[36];
    private ItemStack[] armor = new ItemStack[4];
    private List<PotionEffect> potionEffects = new ArrayList<>();
    private GameMode gameMode = GameMode.SURVIVAL;
    private Location location = new Location(Bukkit.getWorlds().get(0), 0, 90, 0);

    public OfflineInventory(AquaCore plugin, PlayerData playerData) {
        this.plugin = plugin;
        this.playerData = playerData;
    }

    public void update(Player player) {
        this.inventory = player.getInventory().getContents();
        this.armor = player.getInventory().getArmorContents();
        this.potionEffects = new ArrayList<>(player.getActivePotionEffects());
        this.gameMode = player.getGameMode();
        this.location = player.getLocation();
    }

    public void loadInventory() {
        File file = new File(plugin.getEssentialsManagement().getInventoriesDirectory(), this.playerData.getUniqueId().toString() + ".yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
           YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

            try {
                this.inventory = BukkitSerilization.itemStackArrayFromBase64(configuration.getString("inventory"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                this.armor = BukkitSerilization.itemStackArrayFromBase64(configuration.getString("armor"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Collection<PotionEffect> effects = EffectSerilization.deserilizeEffects(configuration.getString("effects"));
            this.potionEffects = effects != null ? new ArrayList<>(effects) : new ArrayList<>();
            this.gameMode = GameMode.valueOf(configuration.getString("gamemode"));
            this.location = LocationSerilization.deserializeLocation(configuration.getString("location"));
        }
    }

    public void save(Player player) {
        File dataFile = new File(plugin.getEssentialsManagement().getInventoriesDirectory(), player.getUniqueId().toString() + ".yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(dataFile);

            configuration.set("uuid", player.getUniqueId().toString());
            configuration.set("name", player.getName());
            configuration.set("lowerCaseName", player.getName().toLowerCase());
            configuration.set("inventory", BukkitSerilization.itemStackArrayToBase64(this.inventory));
            configuration.set("armor", BukkitSerilization.itemStackArrayToBase64(this.armor));
            configuration.set("effects", EffectSerilization.serilizeEffects(this.potionEffects));
            configuration.set("gamemode", this.gameMode.toString());
            configuration.set("location", LocationSerilization.serializeLocation(this.location));

            try {
                configuration.save(dataFile);
            }
            catch (IOException ignored) {

            }
        } catch (Exception ignored) {

        }
    }
}
