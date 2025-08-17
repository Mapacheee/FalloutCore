package me.mapacheee.falloutcore.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.mapacheee.falloutcore.radiation.RadiationSystem.RadiationLevel;

public class ArmorUtils {

    private static final Material[] ARMOR_STRENGTH = {
            Material.LEATHER_HELMET,
            Material.CHAINMAIL_HELMET,
            Material.IRON_HELMET,
            Material.GOLDEN_HELMET,
            Material.DIAMOND_HELMET,
            Material.NETHERITE_HELMET
    };

    public static boolean hasRequiredArmor(Player player, RadiationLevel level) {
        PlayerInventory inv = player.getInventory();
        ItemStack[] armor = inv.getArmorContents();

        for (ItemStack piece : armor) {
            if (piece == null || piece.getType() == Material.AIR) {
                return false;
            }
        }

        Material helmetType = armor[3].getType();
        int requiredIndex = getMaterialIndex(level.minArmorType());
        int playerIndex = getMaterialIndex(helmetType);

        if (playerIndex < requiredIndex) {
            return false;
        }

        if (level.requiresEnchantment()) {
            boolean hasEnchantment = false;
            for (ItemStack piece : armor) {
                if (!piece.getEnchantments().isEmpty()) {
                    hasEnchantment = true;
                    break;
                }
            }
            if (!hasEnchantment) return false;
        }

        return true;
    }

    private static int getMaterialIndex(Material material) {
        for (int i = 0; i < ARMOR_STRENGTH.length; i++) {
            if (ARMOR_STRENGTH[i] == material) {
                return i;
            }
        }
        return -1;
    }
}
