package me.mapacheee.falloutcore.radiation.entity;

import com.google.inject.Inject;
import com.thewinterframework.service.annotation.Service;
import com.thewinterframework.service.annotation.lifecycle.OnDisable;
import com.thewinterframework.service.annotation.lifecycle.OnEnable;
import me.mapacheee.falloutcore.shared.config.Config;
import me.mapacheee.falloutcore.shared.config.ConfigService;
import me.mapacheee.falloutcore.shared.util.MessageUtil;
import me.mapacheee.falloutcore.radiation.event.RadiationLevelChangeEvent;
import me.mapacheee.falloutcore.radiation.event.RadiationEnterEvent;
import me.mapacheee.falloutcore.radiation.event.RadiationExitEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RadiationService {

    private final Logger logger;
    private final ConfigService configService;
    private final MessageUtil messageUtil;
    private final Plugin plugin;

    private int currentRadiationLevel = 1;
    private int currentRadiationHeight = 80;
    private final Map<UUID, Boolean> playersInRadiation = new HashMap<>();
    private final Map<UUID, Long> lastDamageTime = new HashMap<>();

    private BukkitTask radiationTask;
    private BukkitTask levelChangeTask;

    @Inject
    public RadiationService(Logger logger, ConfigService configService, MessageUtil messageUtil, Plugin plugin) {
        this.logger = logger;
        this.configService = configService;
        this.messageUtil = messageUtil;
        this.plugin = plugin;
    }

    @OnEnable
    void startRadiationSystem() {
        if (!configService.getConfig().radiation().enabled()) {
            logger.info("radiacion apagada");
            return;
        }

        startRadiationMonitoring();
        startLevelChangeTask();
        logger.info("radiación iniciado - Nivel: {} | Altura: {}", currentRadiationLevel, currentRadiationHeight);
    }

    @OnDisable
    void stopRadiationSystem() {
        if (radiationTask != null && !radiationTask.isCancelled()) {
            radiationTask.cancel();
        }
        if (levelChangeTask != null && !levelChangeTask.isCancelled()) {
            levelChangeTask.cancel();
        }
        playersInRadiation.clear();
        lastDamageTime.clear();
        logger.info("Sistema de radiación detenido");
    }

    private void startRadiationMonitoring() {
        radiationTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("falloutcore.radiation.immune")) {
                        continue;
                    }

                    processPlayerRadiation(player);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void startLevelChangeTask() {
        long intervalTicks = configService.getConfig().radiation().changeIntervalMinutes() * 60L * 20L;

        levelChangeTask = new BukkitRunnable() {
            @Override
            public void run() {
                changeRadiationLevel();
            }
        }.runTaskTimer(plugin, intervalTicks, intervalTicks);
    }

    private void processPlayerRadiation(Player player) {
        boolean inRadiation = player.getLocation().getY() >= currentRadiationHeight;
        boolean wasInRadiation = playersInRadiation.getOrDefault(player.getUniqueId(), false);

        if (inRadiation && !wasInRadiation) {
            RadiationEnterEvent enterEvent = new RadiationEnterEvent(player, currentRadiationLevel);
            Bukkit.getPluginManager().callEvent(enterEvent);

            if (!enterEvent.isCancelled()) {
                playersInRadiation.put(player.getUniqueId(), true);
                messageUtil.sendRadiationMessage(player, "enterRadiation",
                        "level", String.valueOf(currentRadiationLevel),
                        "height", String.valueOf(currentRadiationHeight));
            }
        } else if (!inRadiation && wasInRadiation) {
            RadiationExitEvent exitEvent = new RadiationExitEvent(player, currentRadiationLevel);
            Bukkit.getPluginManager().callEvent(exitEvent);

            if (!exitEvent.isCancelled()) {
                playersInRadiation.put(player.getUniqueId(), false);
                messageUtil.sendRadiationMessage(player, "exitRadiation");
            }
        }

        if (inRadiation && playersInRadiation.getOrDefault(player.getUniqueId(), false)) {
            handleRadiationEffects(player);
        }
    }

    private void handleRadiationEffects(Player player) {
        if (configService.getConfig().radiation().enableSound()) {
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 0.3f, 1.0f);
        }

        ArmorProtectionLevel protection = getArmorProtection(player);

        if (protection.level >= currentRadiationLevel) {
            messageUtil.sendRadiationMessage(player, "armorProtection");
            degradeArmor(player);
            return;
        }

        long currentTime = System.currentTimeMillis();
        long lastDamage = lastDamageTime.getOrDefault(player.getUniqueId(), 0L);

        if (currentTime - lastDamage > 3000) { // Daño cada 3 segundos
            double damage = configService.getConfig().radiation().damagePerLevel() * currentRadiationLevel;
            player.damage(damage);
            applyRadiationEffects(player);

            messageUtil.sendRadiationMessage(player, "takingDamage",
                "damage", String.valueOf(damage),
                "level", String.valueOf(currentRadiationLevel));
            lastDamageTime.put(player.getUniqueId(), currentTime);
        }
    }

    private ArmorProtectionLevel getArmorProtection(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        ItemStack chestplate = player.getInventory().getChestplate();
        ItemStack leggings = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();

        if (isFullArmorSet(helmet, chestplate, leggings, boots, Material.NETHERITE_HELMET)) {
            return ArmorProtectionLevel.NETHERITE;
        } else if (isFullArmorSet(helmet, chestplate, leggings, boots, Material.DIAMOND_HELMET)) {
            return ArmorProtectionLevel.DIAMOND;
        } else if (isFullArmorSet(helmet, chestplate, leggings, boots, Material.IRON_HELMET)) {
            return ArmorProtectionLevel.IRON;
        } else if (isFullArmorSet(helmet, chestplate, leggings, boots, Material.GOLDEN_HELMET)) {
            return ArmorProtectionLevel.GOLD;
        } else if (isFullArmorSet(helmet, chestplate, leggings, boots, Material.LEATHER_HELMET)) {
            return ArmorProtectionLevel.LEATHER;
        }

        return ArmorProtectionLevel.NONE;
    }

    private boolean isFullArmorSet(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, Material helmetType) {
        if (helmet == null || chestplate == null || leggings == null || boots == null) {
            return false;
        }

        String materialPrefix = helmetType.name().split("_")[0];

        return helmet.getType().name().startsWith(materialPrefix) &&
               chestplate.getType().name().startsWith(materialPrefix) &&
               leggings.getType().name().startsWith(materialPrefix) &&
               boots.getType().name().startsWith(materialPrefix);
    }

    private void degradeArmor(Player player) {
        ItemStack[] armor = {
            player.getInventory().getHelmet(),
            player.getInventory().getChestplate(),
            player.getInventory().getLeggings(),
            player.getInventory().getBoots()
        };

        for (int i = 0; i < armor.length; i++) {
            ItemStack piece = armor[i];
            if (piece != null && piece.getType().getMaxDurability() > 0) {
                short currentDamage = piece.getDurability();
                short newDamage = (short) (currentDamage + ThreadLocalRandom.current().nextInt(1, 4));

                if (newDamage >= piece.getType().getMaxDurability()) {
                    piece.setType(Material.AIR);
                } else {
                    piece.setDurability(newDamage);
                }

                switch (i) {
                    case 0 -> player.getInventory().setHelmet(piece);
                    case 1 -> player.getInventory().setChestplate(piece);
                    case 2 -> player.getInventory().setLeggings(piece);
                    case 3 -> player.getInventory().setBoots(piece);
                }
            }
        }

        messageUtil.sendRadiationMessage(player, "armorDegrading");
    }

    private void applyRadiationEffects(Player player) {
        int duration = configService.getConfig().radiation().effectDurationSeconds() * 20;

        switch (currentRadiationLevel) {
            case 1 -> player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, duration, 0));
            case 2 -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, duration, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, duration, 0));
            }
            case 3 -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, duration, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, duration, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, duration, 0));
            }
            case 4 -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, duration, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, duration, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, duration, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 0));
            }
            case 5 -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, duration, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, duration, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, duration, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, duration, 0));
            }
        }
    }

    private void changeRadiationLevel() {
        int oldLevel = currentRadiationLevel;
        int oldHeight = currentRadiationHeight;

        Config.RadiationConfig radConfig = configService.getConfig().radiation();

        currentRadiationLevel = ThreadLocalRandom.current().nextInt(
            radConfig.minLevel(), radConfig.maxLevel() + 1
        );

        currentRadiationHeight = radConfig.startingHeight() +
            ThreadLocalRandom.current().nextInt(-20, 41);

        RadiationLevelChangeEvent changeEvent = new RadiationLevelChangeEvent(
            oldLevel, currentRadiationLevel, oldHeight, currentRadiationHeight);
        Bukkit.getPluginManager().callEvent(changeEvent);

        if (changeEvent.isCancelled()) {
            currentRadiationLevel = oldLevel;
            currentRadiationHeight = oldHeight;
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            messageUtil.sendRadiationMessage(player, "levelChanged",
                "level", String.valueOf(currentRadiationLevel),
                "height", String.valueOf(currentRadiationHeight),
                "oldLevel", String.valueOf(oldLevel),
                "oldHeight", String.valueOf(oldHeight)
            );
        }

        logger.info("Nivel de radiación cambiado: {} -> {} | Altura: {} -> {}",
            oldLevel, currentRadiationLevel, oldHeight, currentRadiationHeight);
    }

    public int getPlayersInRadiationCount() {
        return playersInRadiation.size();
    }
    public int getCurrentRadiationLevel() {
        return currentRadiationLevel;
    }

    public int getCurrentRadiationHeight() {
        return currentRadiationHeight;
    }

    public boolean isPlayerInRadiation(Player player) {
        return playersInRadiation.getOrDefault(player.getUniqueId(), false);
    }

    public boolean isPlayerImmune(Player player) {
        return player.hasPermission("falloutcore.radiation.immune");
    }

    public ArmorProtectionLevel getPlayerArmorProtection(Player player) {
        return getArmorProtection(player);
    }

    public void forceRadiationLevel(int level) {
        if (level >= configService.getConfig().radiation().minLevel() && level <= configService.getConfig().radiation().maxLevel()) {
            currentRadiationLevel = level;
            logger.info("Nivel de radiación forzado a: {}", level);
        }
    }

    public void forceRadiationHeight(int height) {
        currentRadiationHeight = height;
        logger.info("Altura de radiación forzada a: {}", height);
    }

    public enum ArmorProtectionLevel {
        NONE(0, "Sin protección"),
        LEATHER(1, "Cuero"),
        GOLD(2, "Oro"),
        IRON(3, "Hierro"),
        DIAMOND(4, "Diamante"),
        NETHERITE(5, "Netherita");

        public final int level;
        public final String displayName;

        ArmorProtectionLevel(int level, String displayName) {
            this.level = level;
            this.displayName = displayName;
        }
    }

}
