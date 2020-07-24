package me.activated.core.utilities.server;

import me.activated.core.utilities.chat.Color;
import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

public class TPSUtils {

    private static Object minecraftServer;
    private static Field recentTps;
    private static final DecimalFormat SECONDS_FORMAT = new DecimalFormat("#0.0");

    private static double[] getTps() {
        try {
            if (minecraftServer == null) {
                Server server = Bukkit.getServer();
                Field consoleField = server.getClass().getDeclaredField("console");
                consoleField.setAccessible(true);
                minecraftServer = consoleField.get(server);
            }
            if (recentTps == null) {
                recentTps = minecraftServer.getClass().getSuperclass().getDeclaredField("recentTps");
                recentTps.setAccessible(true);
            }
            return (double[]) recentTps.get(minecraftServer);
        } catch (IllegalAccessException | NoSuchFieldException ignored) {
        }
        return new double[]{-1, -1, -1};
    }

    private static String formatSeconds(long time) {
        return SECONDS_FORMAT.format(time / 1000.0F);
    }


    public static double[] getRecentTps() {
        double[] tps = getTps();
        if (tps[0] >= 20.0) tps[0] = 20.0;
        if (tps[1] >= 20.0) tps[1] = 20.0;
        if (tps[2] >= 20.0) tps[2] = 20.0;

        return new double[] {Math.round(tps[0]), Math.round(tps[1]), Math.round(tps[2])};
    }


    public static String getNiceTPS(double tps) {
        if (tps >= 20.0) return Color.translate("&a*" + tps);
        if (tps < 20.0 && tps > 18.0) return Color.translate("&a" + tps);
        if (tps < 18.0 && tps > 10.0) return Color.translate("&e" + tps);
        if (tps < 10.0 && tps > 5.0) return Color.translate("&c" + tps);
        return Color.translate("&4" + tps);
    }

    public static String getTPSStatus(double tps) {
        if (tps >= 20.0) return Color.translate("&aExcellent");
        if (tps < 20.0 && tps > 18.0) return Color.translate("&aHigh");
        if (tps < 18.0 && tps > 10.0) return Color.translate("&eMedium");
        if (tps < 10.0 && tps > 5.0) return Color.translate("&cLow");
        return Color.translate("&4Risky, lower than 5!!");
    }
}
