package me.activated.core.utilities.reflection.sit;


import me.activated.core.utilities.reflection.NMSClass;
import me.activated.core.utilities.reflection.Reflection;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SitUtil {

    public static Map<String, Object> datas = new HashMap<>();

    public static void sitPlayer(Player player) {
        if (datas.containsKey(player.getName())) return;

        player.setAllowFlight(true);
        Location playerLocation = player.getLocation();
        try {
            Object world = Reflection.getHandle(playerLocation.getWorld());
            Object entityCreeper = SitUtil.buildEntityCreeper(world, playerLocation);
            Object playerHandle = player.getClass().getMethod("getHandle").invoke(player);
            Object id = NMSClass.Entity.getDeclaredMethod("getId").invoke(entityCreeper);

            Constructor<?> firstConstructor = Objects.requireNonNull(Reflection.getNMSClass("PacketPlayOutSpawnEntityLiving")).getConstructor(NMSClass.EntityLiving);
            Object firstPacket = firstConstructor.newInstance(entityCreeper);
            Reflection.sendPacket(player, firstPacket);

            datas.put(player.getName(), id);

            Constructor<?> secondConstructor = Objects.requireNonNull(Reflection.getNMSClass("PacketPlayOutAttachEntity")).getConstructor(int.class, NMSClass.Entity, NMSClass.Entity);
            Object secondPacket = secondConstructor.newInstance(0, playerHandle, entityCreeper);
            Reflection.sendPacket(player, secondPacket);
        } catch (Exception e) {  }
    }

    public static void unsitPlayer(Player player) {
        if (!datas.containsKey(player.getName())) return;

        player.setAllowFlight(false);
        try {
            Object packet = NMSClass.PacketPlayOutEntityDestroy.getDeclaredConstructor(int[].class).newInstance((Object) new int[] {Integer.parseInt(datas.get(player.getName()).toString())});
            Reflection.sendPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        datas.remove(player.getName());
    }

    private static Object buildEntityCreeper(Object world, Location loc) throws Exception {
        Object creeper = NMSClass.EntityCreeper.getConstructor(NMSClass.World).newInstance(world);
        updateEntityLocation(creeper, loc);
        if (Reflection.getVersion().contains("1_8")) {
            NMSClass.Entity.getDeclaredMethod("setInvisible", boolean.class).invoke(creeper, true);
            NMSClass.Entity.getDeclaredMethod("setCustomNameVisible", boolean.class).invoke(creeper, false);
        } else {
            NMSClass.Entity.getDeclaredMethod("setInvisible", boolean.class).invoke(creeper, true);
            NMSClass.EntityInsentient.getDeclaredMethod("setCustomNameVisible", boolean.class).invoke(creeper, false);
        }
        return creeper;
    }

    private static void updateEntityLocation(Object entity, Location loc) throws Exception {
        NMSClass.Entity.getDeclaredField("locX").set(entity, loc.getX());
        NMSClass.Entity.getDeclaredField("locY").set(entity, loc.getY());
        NMSClass.Entity.getDeclaredField("locZ").set(entity, loc.getZ());
    }
}
