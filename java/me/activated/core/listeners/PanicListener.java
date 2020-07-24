package me.activated.core.listeners;

import me.activated.core.plugin.AquaCore;
import me.activated.core.api.player.PlayerData;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class PanicListener implements Listener {
    private final AquaCore plugin = AquaCore.INSTANCE;

    private boolean isInPanic(Player player) {
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        return playerData != null && playerData.getPanicSystem().isInPanic();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handlePlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        Location from = event.getFrom();
        Location to = event.getTo();

        if (playerData != null && playerData.getPanicSystem().isInPanic()) {
            if ((from.getX() != to.getX()) || (from.getZ() != to.getZ())) {
                player.teleport(from);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleBlockBreak(BlockBreakEvent event) {
        event.setCancelled(this.isInPanic(event.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(this.isInPanic(event.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleInventoryClick(InventoryClickEvent event) {
        event.setCancelled(this.isInPanic((Player) event.getWhoClicked()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleInventoryClick(InventoryCreativeEvent event) {
        event.setCancelled(this.isInPanic((Player) event.getWhoClicked()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) return;
        event.setCancelled(this.isInPanic((Player) event.getEntered()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        event.setCancelled(this.isInPanic(player));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        event.setCancelled(this.isInPanic(player));
    }

    @EventHandler
    public void handleQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!this.isInPanic(player)) return;

        if (plugin.getCoreConfig().getBoolean("panic.remove-panic-on-quit")) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            playerData.getPanicSystem().unPanicPlayer();
        }
    }
}
