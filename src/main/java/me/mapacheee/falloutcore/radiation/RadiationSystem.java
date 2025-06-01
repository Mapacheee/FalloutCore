package me.mapacheee.falloutcore.radiation;

import me.mapacheee.falloutcore.FalloutCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.*;

import me.mapacheee.falloutcore.utils.ArmorUtils;

public class RadiationSystem {
    private static RadiationSystem instance;
    private int currentLevel = 1;
    private int radiationY = 64;
    private BukkitTask radiationTask;
    private final Map<UUID, Integer> exposureTime = new HashMap<>();
    private final Map<UUID, Long> lastWarning = new HashMap<>();

    private static final Map<Integer, RadiationLevel> RADIATION_LEVELS = new HashMap<>();

    static {
        RADIATION_LEVELS.put(1, new RadiationLevel(
                Material.LEATHER,
                Arrays.asList(
                        new PotionEffect(PotionEffectType.WITHER, 200, 0)
                )
        ));

        RADIATION_LEVELS.put(2, new RadiationLevel(
                Material.CHAINMAIL_HELMET,
                Arrays.asList(
                        new PotionEffect(PotionEffectType.WITHER, 200, 1),
                        new PotionEffect(PotionEffectType.BLINDNESS, 200, 0)
                )
        ));

        RADIATION_LEVELS.put(3, new RadiationLevel(
                Material.IRON_HELMET,
                Arrays.asList(
                        new PotionEffect(PotionEffectType.WITHER, 200, 1),
                        new PotionEffect(PotionEffectType.BLINDNESS, 200, 0),
                        new PotionEffect(PotionEffectType.SLOWNESS, 200, 0)
                )
        ));

        RADIATION_LEVELS.put(4, new RadiationLevel(
                Material.DIAMOND_HELMET,
                Arrays.asList(
                        new PotionEffect(PotionEffectType.WITHER, 200, 2),
                        new PotionEffect(PotionEffectType.BLINDNESS, 200, 0),
                        new PotionEffect(PotionEffectType.SLOWNESS, 200, 1),
                        new PotionEffect(PotionEffectType.POISON, 200, 0)
                ),
                true
        ));

        RADIATION_LEVELS.put(5, new RadiationLevel(
                Material.NETHERITE_HELMET,
                Collections.emptyList(),
                false,
                30
        ));
    }

    private RadiationSystem() {}

    public static RadiationSystem getInstance() {
        if (instance == null) {
            instance = new RadiationSystem();
        }
        return instance;
    }

    public void start() {
        stop();

        new BukkitRunnable() {
            @Override
            public void run() {
                updateRadiationLevel();
            }
        }.runTaskTimer(FalloutCore.getInstance(), 0, 20 * 60 * 5);

        radiationTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    checkPlayer(player);
                }
            }
        }.runTaskTimer(FalloutCore.getInstance(), 0, 20);
    }

    public void stop() {
        if (radiationTask != null) {
            radiationTask.cancel();
            radiationTask = null;
        }
        exposureTime.clear();
        lastWarning.clear();
    }

    public void updateRadiationLevel() {
        Random random = new Random();
        currentLevel = random.nextInt(5) + 1;

        radiationY = switch(currentLevel) {
            case 1 -> 60 + random.nextInt(20);
            case 2 -> 40 + random.nextInt(30);
            case 3 -> 30 + random.nextInt(30);
            case 4 -> 20 + random.nextInt(20);
            case 5 -> 0 + random.nextInt(30);
            default -> 64;
        };

        String message = "§c¡nivel de radiación cambiado a §4" + currentLevel + "§c! Capa Y: §4" + radiationY;
        Bukkit.broadcastMessage(message);

        exposureTime.clear();
    }

    private void checkPlayer(Player player) {
        if (!isInRadiationZone(player)) {
            exposureTime.remove(player.getUniqueId());
            return;
        }

        if (player.hasPermission("fallout.radiacoinmune") || isProtected(player)) {
            return;
        }

        applyRadiationEffects(player);
        showRadiationWarning(player);
    }

    private boolean isInRadiationZone(Player player) {
        World world = player.getWorld();
        Location loc = player.getLocation();
        return world.getEnvironment() == World.Environment.NORMAL &&
                loc.getBlockY() >= radiationY;
    }

    private boolean isProtected(Player player) {
        RadiationLevel levelConfig = RADIATION_LEVELS.get(currentLevel);
        if (levelConfig == null) return false;

        return ArmorUtils.hasRequiredArmor(player, levelConfig);
    }

    private void applyRadiationEffects(Player player) {
        UUID playerId = player.getUniqueId();
        RadiationLevel levelConfig = RADIATION_LEVELS.get(currentLevel);

        if (currentLevel == 5) {
            int secondsExposed = exposureTime.getOrDefault(playerId, 0) + 1;
            exposureTime.put(playerId, secondsExposed);

            if (secondsExposed >= levelConfig.getMaxExposureTime()) {
                player.damage(5.0);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 0.5f);
                return;
            }
        }

        for (PotionEffect effect : levelConfig.getEffects()) {
            player.removePotionEffect(effect.getType());
        }

        for (PotionEffect effect : levelConfig.getEffects()) {
            player.addPotionEffect(effect, true);
        }
    }

    private void showRadiationWarning(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (lastWarning.getOrDefault(playerId, 0L) + 5000 > currentTime) {
            return;
        }

        lastWarning.put(playerId, currentTime);

        RadiationLevel levelConfig = RADIATION_LEVELS.get(currentLevel);
        String subtitle = "";

        if (currentLevel == 5) {
            int timeLeft = levelConfig.getMaxExposureTime() - exposureTime.getOrDefault(playerId, 0);
            subtitle = "§cProtección crítica! " + timeLeft + "s restantes";
        }

        player.sendTitle(
                "§4¡PELIGRO DE RADIACIÓN!",
                subtitle,
                10, 70, 20
        );

        player.playSound(player.getLocation(), Sound.AMBIENT_BASALT_DELTAS_LOOP, 1.0f, 0.8f);
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getRadiationY() {
        return radiationY;
    }

    public String getRadiationLevelName() {
        return switch(currentLevel) {
            case 1 -> "Bajo";
            case 2 -> "Moderado";
            case 3 -> "Alto";
            case 4 -> "Extremo";
            case 5 -> "CRÍTICO";
            default -> "Desconocido";
        };
    }

    public static class RadiationLevel {
        private final Material minArmorType;
        private final List<PotionEffect> effects;
        private final boolean requiresEnchantment;
        private final int maxExposureTime;

        public RadiationLevel(Material minArmorType, List<PotionEffect> effects) {
            this(minArmorType, effects, false, 0);
        }

        public RadiationLevel(Material minArmorType, List<PotionEffect> effects, boolean requiresEnchantment) {
            this(minArmorType, effects, requiresEnchantment, 0);
        }

        public RadiationLevel(Material minArmorType,
                              List<PotionEffect> effects,
                              boolean requiresEnchantment,
                              int maxExposureTime) {
            this.minArmorType = minArmorType;
            this.effects = effects;
            this.requiresEnchantment = requiresEnchantment;
            this.maxExposureTime = maxExposureTime;
        }

        public Material getMinArmorType() {
            return minArmorType;
        }

        public List<PotionEffect> getEffects() {
            return effects;
        }

        public boolean requiresEnchantment() {
            return requiresEnchantment;
        }

        public int getMaxExposureTime() {
            return maxExposureTime;
        }
    }
}