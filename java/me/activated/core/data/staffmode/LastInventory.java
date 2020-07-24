package me.activated.core.data.staffmode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;

@AllArgsConstructor
@Getter
@Setter
public class LastInventory {
    private ItemStack[] contents = new ItemStack[36];
    private ItemStack[] armorContents = new ItemStack[4];
    private Collection<PotionEffect> effects = new ArrayList<>();
    private float exp;
    private GameMode gameMode;
}
