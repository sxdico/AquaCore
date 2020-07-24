package me.activated.core.listeners;

import me.activated.core.database.redis.other.bson.JsonChain;
import me.activated.core.database.redis.payload.action.JedisAction;
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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class FreezeListener implements Listener {
    private final AquaCore plugin = AquaCore.INSTANCE;

    private boolean isFrozen(Player player) {
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        return playerData != null && (playerData.isFrozen() || playerData.isGuiFrozen());
    }

    private boolean isCommandAllowed(String command) {
        for (String allowed : plugin.getCoreConfig().getStringList("freeze-allowed-commands")) {
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

        if (this.isFrozen(player)) {
            if ((from.getX() != to.getX()) || (from.getZ() != to.getZ())) {
                player.teleport(from);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleBlockBreak(BlockBreakEvent event) {
        event.setCancelled(this.isFrozen(event.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(this.isFrozen(event.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleInventoryClick(InventoryClickEvent event) {
        event.setCancelled(this.isFrozen((Player) event.getWhoClicked()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleInventoryClick(InventoryCreativeEvent event) {
        event.setCancelled(this.isFrozen((Player) event.getWhoClicked()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) return;
        event.setCancelled(this.isFrozen((Player) event.getEntered()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleEnter(PlayerCommandPreprocessEvent event) {
        event.setCancelled(this.isFrozen(event.getPlayer()) && !isCommandAllowed(event.getMessage()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        event.setCancelled(this.isFrozen(player));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void handleEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        event.setCancelled(this.isFrozen(player));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void handleQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!this.isFrozen(player)) return;

        JsonChain jsonChain = new JsonChain();
        jsonChain.addProperty("name", player.getName());
        jsonChain.addProperty("displayName", player.getDisplayName());
        jsonChain.addProperty("server", plugin.getEssentialsManagement().getServerName());

        plugin.getRedisData().write(JedisAction.LEFT_FROZEN, jsonChain.get());
    }
}
