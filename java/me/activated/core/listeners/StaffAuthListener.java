package me.activated.core.listeners;

import me.activated.core.plugin.AquaCore;
import me.activated.core.api.player.PlayerData;
import me.activated.core.enums.Language;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class StaffAuthListener implements Listener {
    private final AquaCore plugin = AquaCore.INSTANCE;

    private boolean isAuth(Player player) {
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        return playerData != null && playerData.isStaffAuth() && plugin.getCoreConfig().getBoolean("staff-auth.enabled")
                && player.hasPermission(plugin.getCoreConfig().getString("staff-auth.permission", "Aqua.staff.auth"));
    }

    private boolean isCommandAllowed(String command) {
        for (String allowed : plugin.getCoreConfig().getStringList("auth-allowed-commands")) {
            if (command.toLowerCase().startsWith(allowed.toLowerCase())) {
                return true;
            }
        }
        return false;
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void handlePlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Location from = event.getFrom();
        Location to = event.getTo();

        if (this.isAuth(player)) {
            if ((from.getX() != to.getX()) || (from.getZ() != to.getZ())) {
                player.teleport(from);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleBlockBreak(BlockBreakEvent event) {
        event.setCancelled(this.isAuth(event.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(this.isAuth(event.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleInventoryClick(InventoryClickEvent event) {
        event.setCancelled(this.isAuth((Player) event.getWhoClicked()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleInventoryClick(InventoryCreativeEvent event) {
        event.setCancelled(this.isAuth((Player) event.getWhoClicked()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) return;
        event.setCancelled(this.isAuth((Player) event.getEntered()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleEnter(PlayerCommandPreprocessEvent event) {
        if (this.isAuth(event.getPlayer()) && !this.isCommandAllowed(event.getMessage())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Language.PREFIX.toString() + ChatColor.RED + "Please auth yourself before executing other commands!");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        event.setCancelled(this.isAuth(player));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        event.setCancelled(this.isAuth(player));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void hanldeChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!this.isAuth(player)) return;

        event.setCancelled(true);

        event.getPlayer().sendMessage(Language.PREFIX.toString() + ChatColor.RED + "Please auth yourself before using chat!");
    }
}
