package me.activated.core.managers;

import me.activated.core.plugin.AquaCore;
import me.activated.core.api.player.PlayerData;
import me.activated.core.data.staffmode.LastInventory;
import me.activated.core.data.staffmode.StaffModeItem;
import me.activated.core.utilities.Manager;
import me.activated.core.utilities.file.ConfigFile;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class StaffModeManagement extends Manager {
    private final ConfigFile configFile = plugin.getStaffModeFile();
    private final List<StaffModeItem> items = new ArrayList<>();
    private final Map<UUID, LastInventory> inventories = new HashMap<>();

    public StaffModeManagement(AquaCore plugin) {
        super(plugin);

        this.setupItems();
    }

    public StaffModeItem getStaffModeItem(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return this.items.stream().filter(staffModeItem -> ChatColor.stripColor(staffModeItem.getName()).equals(ChatColor.stripColor(item.getItemMeta().getDisplayName())))
                    .filter(staffModeItem -> staffModeItem.getItem() == item.getType()).findFirst().orElse(null);
        }
        return null;
    }

    public StaffModeItem getVanishOffItem() {
        return this.items.stream().filter(staffModeItem -> staffModeItem.getAction().isEnabled())
                .filter(staffModeItem -> staffModeItem.getAction().getAction().equalsIgnoreCase("vanish-off"))
                .findFirst().orElse(null);
    }

    public StaffModeItem getVanishOnItem() {
        return this.items.stream().filter(staffModeItem -> staffModeItem.getAction().isEnabled())
                .filter(staffModeItem -> staffModeItem.getAction().getAction().equalsIgnoreCase("vanish-on"))
                .findFirst().orElse(null);
    }

    public void setupItems() {
        this.items.clear();
        configFile.getConfigurationSection("items").getKeys(false).forEach(key -> {
            this.items.add(new StaffModeItem(key, configFile).setup());
        });
    }

    public String getStaffPermission() {
        return configFile.getString("online-staff-gui.permission");
    }

    public void enableStaffMode(Player player) {
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        this.inventories.remove(player.getUniqueId());

        LastInventory inventory = new LastInventory(player.getInventory().getContents(),
                player.getInventory().getArmorContents(), player.getActivePotionEffects(),
                player.getExp(), player.getGameMode());
        this.inventories.put(player.getUniqueId(), inventory);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.CREATIVE);

        this.items.forEach(staffModeItem -> {
            player.getInventory().setItem(staffModeItem.getSlot(), staffModeItem.build());
        });

        StaffModeItem vanishOn = this.getVanishOnItem();
        StaffModeItem vanishOff = this.getVanishOffItem();

        if (playerData.isVanished() && vanishOff != null) {
            player.getInventory().setItem(vanishOff.getSlot(), vanishOff.build());
        } else if (!playerData.isVanished() && vanishOn != null) {
            player.getInventory().setItem(vanishOn.getSlot(), vanishOn.build());
        }

        player.updateInventory();
        playerData.setInStaffMode(true);
    }

    public void disableStaffMode(Player player) {
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        LastInventory inventory = this.inventories.get(player.getUniqueId());
        player.getInventory().setContents(inventory.getContents());
        player.getInventory().setArmorContents(inventory.getArmorContents());
        player.setExp(inventory.getExp());
        player.setGameMode(inventory.getGameMode());

        player.updateInventory();
        playerData.setInStaffMode(false);
    }
}
