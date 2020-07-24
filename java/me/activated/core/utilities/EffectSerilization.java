package me.activated.core.utilities;


import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;

public class EffectSerilization {

    public static String serilizeEffects(Collection<PotionEffect> effects) {
        StringBuilder builder = new StringBuilder();
        for (PotionEffect potionEffect : effects) {
            builder.append(serilizePotionEffect(potionEffect));
            builder.append(";");
        }
        return builder.toString();
    }

    public static Collection<PotionEffect> deserilizeEffects(String source) {
        if (!source.contains(":")) {
            return null;
        }
        Collection<PotionEffect> effects = new ArrayList<>();
        String[] split = source.split(";");

        for (String piece : split) {
            effects.add(deserilizePotionEffect(piece));
        }

        return effects;
    }

    public static String serilizePotionEffect(PotionEffect potionEffect) {
        StringBuilder builder = new StringBuilder();

        if (potionEffect == null) {
            return "null";
        }
        String name = potionEffect.getType().getName();
        builder.append("n@").append(name);

        String duration = String.valueOf(potionEffect.getDuration());
        builder.append(":d@").append(duration);

        String amplifier = String.valueOf(potionEffect.getAmplifier());
        builder.append(":a@").append(amplifier);

        return builder.toString();
    }

    public static PotionEffect deserilizePotionEffect(String source) {
        String name = "";
        String duration = "";
        String amplifier = "";

        if (source.equals("null")) {
            return null;
        }
        String[] split = source.split(":");

        for (String effectInfo : split) {
            String[] itemAttribute = effectInfo.split("@");
            String s2 = itemAttribute[0];

            if (s2.equalsIgnoreCase("n")) {
                name = itemAttribute[1];
            }
            if (s2.equalsIgnoreCase("d")) {
                duration = itemAttribute[1];
            }
            if (s2.equalsIgnoreCase("a")) {
                amplifier = itemAttribute[1];
            }
        }
        return new PotionEffect(PotionEffectType.getByName(name), Integer.parseInt(duration), Integer.parseInt(amplifier));
    }
}
