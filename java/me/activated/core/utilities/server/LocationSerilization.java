package me.activated.core.utilities.server;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationSerilization {

    public static Location deserializeLocation(String input) {
        String[] attributes = input.split(":");

        World world = null;
        Double x = null;
        Double y = null;
        Double z = null;
        Float pitch = null;
        Float yaw = null;

        for (String attribute : attributes) {
            String[] split = attribute.split(";");

            if (split[0].equalsIgnoreCase("#w")) {
                world = Bukkit.getWorld(split[1]);
                continue;
            }

            if (split[0].equalsIgnoreCase("#x")) {
                x = Double.parseDouble(split[1]);
                continue;
            }

            if (split[0].equalsIgnoreCase("#y")) {
                y = Double.parseDouble(split[1]);
                continue;
            }

            if (split[0].equalsIgnoreCase("#z")) {
                z = Double.parseDouble(split[1]);
                continue;
            }

            if (split[0].equalsIgnoreCase("#p")) {
                pitch = Float.parseFloat(split[1]);
                continue;
            }

            if (split[0].equalsIgnoreCase("#yaw")) {
                yaw = Float.parseFloat(split[1]);
            }
        }

        if (world == null || x == null || y == null || z == null || pitch == null || yaw == null) {
            return null;
        }

        return new Location(world, x, y, z, yaw, pitch);
    }

    public static String serializeLocation(Location location) {
        return "#w;" + location.getWorld().getName() +
                ":#x;" + location.getX() +
                ":#y;" + location.getY() +
                ":#z;" + location.getZ() +
                ":#p;" + location.getPitch() +
                ":#yaw;" + location.getYaw();
    }
}
