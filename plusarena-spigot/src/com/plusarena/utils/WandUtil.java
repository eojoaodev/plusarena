package com.plusarena.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;

import java.util.List;

public class WandUtil {

    private static final String WAND_TAG = "§6[PlusArena] §eWand";

    private WandUtil() {}

    public static ItemStack createWand() {
        ItemStack wand = new ItemStack(Material.GOLDEN_AXE);
        ItemMeta meta = wand.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(WAND_TAG);
            meta.setLore(List.of(
                    "§7Clique Esquerdo: §aDefinir POS1",
                    "§7Clique Direito: §cDefinir POS2"
            ));
            wand.setItemMeta(meta);
        }
        return wand;
    }

    public static boolean isWand(ItemStack item) {
        if (item == null || item.getType() != Material.GOLDEN_AXE) return false;
        if (!item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && WAND_TAG.equals(meta.getDisplayName());
    }
}
