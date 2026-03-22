package com.plusarena.managers;

import com.plusarena.PlusArena;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class KitManager {

    private final PlusArena plugin;

    public KitManager(PlusArena plugin) {
        this.plugin = plugin;
    }

    public void giveKit(Player player) {
        // Inventory must be clear before giving kit
        plugin.getArenaManager().clearInventory(player);

        ConfigurationSection kit = plugin.getConfig().getConfigurationSection("kit");
        if (kit == null) {
            plugin.getLogger().warning("Seção 'kit' não encontrada no config.yml!");
            return;
        }

        // Armor
        ItemStack helmet = buildItem(kit.getConfigurationSection("helmet"));
        ItemStack chestplate = buildItem(kit.getConfigurationSection("chestplate"));
        ItemStack leggings = buildItem(kit.getConfigurationSection("leggings"));
        ItemStack boots = buildItem(kit.getConfigurationSection("boots"));

        if (helmet != null) player.getInventory().setHelmet(helmet);
        if (chestplate != null) player.getInventory().setChestplate(chestplate);
        if (leggings != null) player.getInventory().setLeggings(leggings);
        if (boots != null) player.getInventory().setBoots(boots);

        // Items by slot
        List<Map<?, ?>> items = kit.getMapList("items");
        for (Map<?, ?> itemMap : items) {
            try {
                int slot = (int) itemMap.get("slot");
                String matName = (String) itemMap.get("material");
                Material mat = Material.matchMaterial(matName);
                if (mat == null) {
                    plugin.getLogger().warning("Material inválido no kit: " + matName);
                    continue;
                }

                int amount = itemMap.containsKey("amount") ? (int) itemMap.get("amount") : 1;
                ItemStack item = new ItemStack(mat, amount);

                // Enchantments
                if (itemMap.containsKey("enchantments")) {
                    Map<?, ?> enchMap = (Map<?, ?>) itemMap.get("enchantments");
                    applyEnchantments(item, enchMap);
                }

                player.getInventory().setItem(slot, item);
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Erro ao carregar item do kit: " + e.getMessage(), e);
            }
        }

        player.updateInventory();
    }

    private ItemStack buildItem(ConfigurationSection section) {
        if (section == null) return null;

        String matName = section.getString("material");
        if (matName == null) return null;

        Material mat = Material.matchMaterial(matName);
        if (mat == null) {
            plugin.getLogger().warning("Material inválido no kit: " + matName);
            return null;
        }

        ItemStack item = new ItemStack(mat, 1);

        ConfigurationSection enchSection = section.getConfigurationSection("enchantments");
        if (enchSection != null) {
            for (String key : enchSection.getKeys(false)) {
                int level = enchSection.getInt(key);
                applyEnchantment(item, key, level);
            }
        }

        return item;
    }

    private void applyEnchantments(ItemStack item, Map<?, ?> enchMap) {
        for (Map.Entry<?, ?> entry : enchMap.entrySet()) {
            String key = (String) entry.getKey();
            int level = (int) entry.getValue();
            applyEnchantment(item, key, level);
        }
    }

    private void applyEnchantment(ItemStack item, String enchName, int level) {
        try {
            // Try direct key first
            NamespacedKey key = NamespacedKey.minecraft(enchName.toLowerCase());
            Enchantment enchant = Registry.ENCHANTMENT.get(key);

            if (enchant == null) {
                // Try legacy name lookup
                enchant = legacyLookup(enchName);
            }

            if (enchant != null) {
                item.addUnsafeEnchantment(enchant, level);
            } else {
                plugin.getLogger().warning("Encantamento não encontrado: " + enchName);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Erro ao aplicar encantamento " + enchName + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    private Enchantment legacyLookup(String name) {
        // Map common legacy names to their Minecraft keys
        Map<String, String> legacyMap = Map.ofEntries(
                Map.entry("PROTECTION", "protection"),
                Map.entry("PROTECTION_ENVIRONMENTAL", "protection"),
                Map.entry("FIRE_PROTECTION", "fire_protection"),
                Map.entry("FEATHER_FALLING", "feather_falling"),
                Map.entry("BLAST_PROTECTION", "blast_protection"),
                Map.entry("PROJECTILE_PROTECTION", "projectile_protection"),
                Map.entry("RESPIRATION", "respiration"),
                Map.entry("AQUA_AFFINITY", "aqua_affinity"),
                Map.entry("THORNS", "thorns"),
                Map.entry("DEPTH_STRIDER", "depth_strider"),
                Map.entry("FROST_WALKER", "frost_walker"),
                Map.entry("SHARPNESS", "sharpness"),
                Map.entry("DAMAGE_ALL", "sharpness"),
                Map.entry("SMITE", "smite"),
                Map.entry("BANE_OF_ARTHROPODS", "bane_of_arthropods"),
                Map.entry("KNOCKBACK", "knockback"),
                Map.entry("FIRE_ASPECT", "fire_aspect"),
                Map.entry("LOOTING", "looting"),
                Map.entry("SWEEPING_EDGE", "sweeping_edge"),
                Map.entry("EFFICIENCY", "efficiency"),
                Map.entry("SILK_TOUCH", "silk_touch"),
                Map.entry("UNBREAKING", "unbreaking"),
                Map.entry("DURABILITY", "unbreaking"),
                Map.entry("FORTUNE", "fortune"),
                Map.entry("POWER", "power"),
                Map.entry("ARROW_DAMAGE", "power"),
                Map.entry("PUNCH", "punch"),
                Map.entry("FLAME", "flame"),
                Map.entry("INFINITY", "infinity"),
                Map.entry("ARROW_INFINITE", "infinity"),
                Map.entry("LUCK_OF_THE_SEA", "luck_of_the_sea"),
                Map.entry("LURE", "lure"),
                Map.entry("MENDING", "mending"),
                Map.entry("VANISHING_CURSE", "vanishing_curse"),
                Map.entry("BINDING_CURSE", "binding_curse"),
                Map.entry("SOUL_SPEED", "soul_speed"),
                Map.entry("SWIFT_SNEAK", "swift_sneak"),
                Map.entry("MULTISHOT", "multishot"),
                Map.entry("QUICK_CHARGE", "quick_charge"),
                Map.entry("PIERCING", "piercing"),
                Map.entry("LOYALTY", "loyalty"),
                Map.entry("IMPALING", "impaling"),
                Map.entry("RIPTIDE", "riptide"),
                Map.entry("CHANNELING", "channeling")
        );

        String mapped = legacyMap.get(name.toUpperCase());
        if (mapped != null) {
            NamespacedKey key = NamespacedKey.minecraft(mapped);
            return Registry.ENCHANTMENT.get(key);
        }
        return null;
    }
}
