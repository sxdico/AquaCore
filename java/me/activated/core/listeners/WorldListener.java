package me.activated.core.listeners;

import me.activated.core.plugin.AquaCore;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.stream.Collectors;

public class WorldListener implements Listener {
    private final AquaCore plugin = AquaCore.INSTANCE;

    public WorldListener() {
        this.clearEntities();
    }

    private boolean isWorldSkip(World world) {
        return plugin.getCoreConfig().getStringList("worlds.not-affected-worlds").stream().map(String::toLowerCase).collect(Collectors.toList())
                .contains(world.getName().toLowerCase());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void handleMobSpawn(CreatureSpawnEvent event) {
        if (isWorldSkip(event.getEntity().getWorld())) return;
        if (plugin.getCoreConfig().getBoolean("worlds.do-mob-spawn")) return;

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void handleFood(FoodLevelChangeEvent event) {
        if (isWorldSkip(event.getEntity().getWorld())) return;
        if (plugin.getCoreConfig().getBoolean("worlds.do-food-level-change")) return;

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void handleMobSpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof Animals)) return;
        if (isWorldSkip(event.getEntity().getWorld())) return;
        if (plugin.getCoreConfig().getBoolean("worlds.do-animal-spawn")) return;

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void handleLeavesDecay(LeavesDecayEvent event) {
        if (isWorldSkip(event.getBlock().getWorld())) return;
        if (plugin.getCoreConfig().getBoolean("world.do-leaves-decay")) return;

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void handleWeatherChange(WeatherChangeEvent event) {
        if (isWorldSkip(event.getWorld())) return;
        if (plugin.getCoreConfig().getBoolean("world.do-weather-change")) return;

        event.setCancelled(event.toWeatherState());
    }

    private void clearEntities() {
        Bukkit.getWorlds().stream().filter(world -> !this.isWorldSkip(world)).forEach(world -> {
            if (!plugin.getCoreConfig().getBoolean("worlds.do-mob-spawn")) {
                world.getEntities().stream().filter(entity -> entity instanceof Monster).forEach(Entity::remove);
            }
            if (!plugin.getCoreConfig().getBoolean("worlds.do-animal-spawn")) {
                world.getEntities().stream().filter(entity -> entity instanceof Animals).forEach(Entity::remove);
            }
            world.setTime(0L);
        });
    }
}
