package me.activated.core.utilities;


import com.google.common.collect.ImmutableSet;
import me.activated.core.plugin.AquaCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Utilities {

    public static boolean isNumberInteger(String index) {
        try {
            Integer.parseInt(index);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static List<Player> getOnlinePlayers() {
        List<Player> players = new ArrayList<>();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            players.add(player);
        }
        return players;
    }

    public static void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, 2F, 2F);
    }

    public static void playSound(Player player, Sound sound, float v, float v2) {
        player.playSound(player.getLocation(), sound, v, v2);
    }

    public static void playSound(Player player, String sound) {
        try {
            player.playSound(player.getLocation(), Sound.valueOf(sound), 2F, 2F);
        } catch (Exception ignored) { }
    }

    public static int getPing(Player who) {
        try {
            String bukkitversion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".entity.CraftPlayer");
            Object handle = craftPlayer.getMethod("getHandle").invoke(who);
            return (Integer) handle.getClass().getDeclaredField("ping").get(handle);
        } catch (Exception ignored) {
            return -1;
        }
    }

    public static void sendPlayer(Player player, String server, JavaPlugin plugin) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
    }

    public static void loadListenersFromPackage(Plugin plugin, String packageName) {
        for (Class<?> clazz : getClassesInPackage(plugin, packageName)) {
            if (isListener(clazz)) {
                try {
                    plugin.getServer().getPluginManager().registerEvents((Listener) clazz.newInstance(), plugin);
                } catch (Exception e) {
                    //
                }
            } else {
                try {
                    clazz.newInstance();
                } catch (Exception e) {
                    //
                }
            }
        }
    }

    public static void loadCommandsFromPackage(Plugin plugin, String packageName) {
        for (Class<?> clazz : getClassesInPackage(plugin, packageName)) {
            try {
                clazz.newInstance();
            } catch (Exception e) {
                //
            }
        }
    }

    public static boolean isListener(Class<?> clazz) {
        for (Class<?> interfaze : clazz.getInterfaces()) {
            if (interfaze == Listener.class) {
                return true;
            }
        }

        return false;
    }


    public static Collection<Class<?>> getClassesInPackage(Plugin plugin, String packageName) {
        Collection<Class<?>> classes = new ArrayList<>();

        CodeSource codeSource = plugin.getClass().getProtectionDomain().getCodeSource();
        URL resource = codeSource.getLocation();
        String relPath = packageName.replace('.', '/');
        String resPath = resource.getPath().replace("%20", " ");
        String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        JarFile jarFile;

        try {
            jarFile = new JarFile(jarPath);
        } catch (IOException e) {
            throw (new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e));
        }

        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            String className = null;

            if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length())) {
                className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
            }

            if (className != null) {
                Class<?> clazz = null;

                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if (clazz != null) {
                    classes.add(clazz);
                }
            }
        }

        try {
            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (ImmutableSet.copyOf(classes));
    }

    public static String formatTime(int timer) {
        int hours = timer / 3600;
        int secondsLeft = timer - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;

        String formattedTime = "";

        if (hours > 0) {
            if (hours < 10)
                formattedTime += "0";
            formattedTime += hours + ":";
        }

        if (minutes < 10)
            formattedTime += "0";
        formattedTime += minutes + ":";

        if (seconds < 10)
            formattedTime += "0";
        formattedTime += seconds;

        return formattedTime;
    }

    public  static boolean isNameMCVerified(UUID uuid) {
        try {
            String url = "https://api.namemc.com/server/" + AquaCore.INSTANCE.getCoreConfig().getString("name-mc.server-name") + "/likes?profile=" + uuid.toString();
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection)obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");

            InputStream is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("true")) {
                    return true;
                }
            }
            br.close();
            return false;
        }
        catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Failed to h if user is nameMC verified. Are you online?");
            return false;
        }
    }
}
