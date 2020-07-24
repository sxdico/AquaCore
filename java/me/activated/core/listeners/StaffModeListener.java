package me.activated.core.listeners;

import me.activated.core.events.VanishUpdateEvent;
import me.activated.core.plugin.AquaCore;
import me.activated.core.api.player.PlayerData;
import me.activated.core.data.staffmode.StaffModeItem;
import me.activated.core.menus.staffmode.OnlineStaffMenu;
import me.activated.core.enums.Language;
import me.activated.core.utilities.Utilities;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class StaffModeListener implements Listener {
    private final AquaCore plugin = AquaCore.INSTANCE;

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleRightClickItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        ItemStack item = event.getItem();

        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        if (!playerData.isInStaffMode()) return;
        if (item == null) return;

        StaffModeItem staffModeItem = plugin.getStaffModeManagement().getStaffModeItem(item);

        if (staffModeItem == null) return;

        if (staffModeItem.getCommand().isEnabled()) {
            player.performCommand(staffModeItem.getCommand().getCommand());
        }

        if (!staffModeItem.getAction().isEnabled()) return;

        switch (staffModeItem.getAction().getAction()) {
            case "compass": {
                Vector vector = player.getLocation().getDirection().multiply(2.5);
                player.setVelocity(vector);
                break;
            }
            case "random-teleport" : {
                List<Player> players = Utilities.getOnlinePlayers().stream().filter(online -> !online.hasPermission(plugin.getStaffModeManagement().getStaffPermission())).collect(Collectors.toList());

                if (players.size() == 0) {
                    player.sendMessage(Language.RANDOM_TELEPORT_FAIL.toString());
                } else {
                    Random random = new Random();
                    Player target = players.get(random.nextInt(players.size()));

                    if (target == null) {
                        player.sendMessage(Language.RANDOM_TELEPORT_FAIL.toString());
                        return;
                    }

                    player.teleport(target);
                    player.sendMessage(Language.RANDOM_TELEPORT_SUCCESS.toString().replace("<target>", target.getName()));
                }
                break;
            }
            case "online-staff" : {
                List<Player> players = Utilities.getOnlinePlayers().stream().filter(online -> online.hasPermission(plugin.getStaffModeManagement().getStaffPermission())).collect(Collectors.toList());
                if (players.size() == 0) {
                    player.sendMessage(Language.NO_ONLINE_PLAYERS.toString());
                    return;
                }
                new OnlineStaffMenu().open(player);
                break;
            }
            case "vanish-on" : {
                plugin.getVanishManagement().vanishPlayer(player);
                player.sendMessage(Language.VANISH_VANISHED.toString());
                StaffModeItem vanishItem = plugin.getStaffModeManagement().getVanishOffItem();

                if (vanishItem != null) {
                    player.setItemInHand(vanishItem.build());
                    player.updateInventory();
                }
                break;
            }
            case "vanish-off" : {
                plugin.getVanishManagement().unvanishPlayer(player);
                player.sendMessage(Language.VANISH_UN_VANISHED.toString());
                StaffModeItem vanishItem = plugin.getStaffModeManagement().getVanishOnItem();

                if (vanishItem != null) {
                    player.setItemInHand(vanishItem.build());
                    player.updateInventory();
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleRightClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        ItemStack item = player.getItemInHand();

        if (!playerData.isInStaffMode()) return;
        if (!(event.getRightClicked() instanceof Player)) return;

        StaffModeItem staffModeItem = plugin.getStaffModeManagement().getStaffModeItem(item);
        if (staffModeItem == null) return;
        if (!staffModeItem.getAction().isEnabled()) return;

        Player rightClicked = (Player) event.getRightClicked();

        switch (staffModeItem.getAction().getAction()) {
            case "freeze-player" : {
                player.performCommand("freeze " + rightClicked.getName());
                break;
            }
            case "inspect-player" : {
                player.performCommand("invsee " + rightClicked.getName());
                break;
            }
        }
    }

    @EventHandler
    public void handleVanishUpdate(VanishUpdateEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (!playerData.isInStaffMode()) return;

        StaffModeItem vanishOn = plugin.getStaffModeManagement().getVanishOnItem();
        StaffModeItem vanishOff = plugin.getStaffModeManagement().getVanishOffItem();

        if (playerData.isVanished() && vanishOff != null) {
            player.getInventory().setItem(vanishOff.getSlot(), vanishOff.build());
        } else if (!playerData.isVanished() && vanishOn != null) {
            player.getInventory().setItem(vanishOn.getSlot(), vanishOn.build());
        }
        player.updateInventory();
    }

    @EventHandler(ignoreCancelled = true)
    public void handleBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        ItemStack item = player.getItemInHand();

        if (!playerData.isInStaffMode()) return;

        StaffModeItem staffModeItem = plugin.getStaffModeManagement().getStaffModeItem(item);
        if (staffModeItem != null) {
            event.setCancelled(true);
        }

        if (playerData.isInStaffMode() && !player.hasPermission("Aqua.staff.mode.break.blocks")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void handleBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        ItemStack item = player.getItemInHand();

        if (!playerData.isInStaffMode()) return;

        StaffModeItem staffModeItem = plugin.getStaffModeManagement().getStaffModeItem(item);
        if (staffModeItem != null) {
            event.setCancelled(true);
        }

        if (playerData.isInStaffMode() && !player.hasPermission("Aqua.staff.mode.place.blocks")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void handleEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player damager = (Player) event.getDamager();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(damager.getUniqueId());

        if (playerData.isInStaffMode()) {
            event.setCancelled(true);
        }
        if (playerData.isVanished()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void handleItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (playerData.isInStaffMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void handlePlayerDaamge(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player damager = (Player) event.getEntity();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(damager.getUniqueId());

        if (playerData == null) return;

        if (playerData.isInStaffMode()) {
            event.setCancelled(true);
        }
        if (playerData.isVanished()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void handleVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) return;
        Player damager = (Player) event.getEntered();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(damager.getUniqueId());

        if (playerData.isInStaffMode()) {
            event.setCancelled(true);
        }

        if (playerData.isFrozen()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleStaffMode(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (!playerData.isInStaffMode()) return;

        ItemStack item = event.getCurrentItem();

        if (item == null) return;

        StaffModeItem staffModeItem = plugin.getStaffModeManagement().getStaffModeItem(item);
        if (staffModeItem == null) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleStaffMode(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (playerData.isInStaffMode()) {
            event.setCancelled(true);
            player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleStaffMode(InventoryCreativeEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (playerData.isInStaffMode()) {
            event.setCancelled(true);
            player.updateInventory();
        }
    }
}
