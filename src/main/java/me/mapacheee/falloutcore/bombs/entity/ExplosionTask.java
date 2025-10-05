package me.mapacheee.falloutcore.bombs.entity;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockTypes;
import me.mapacheee.falloutcore.shared.config.Config;
import me.mapacheee.falloutcore.shared.effects.EffectsService;
import me.mapacheee.falloutcore.radiation.entity.RadiationService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ExplosionTask extends BukkitRunnable {

    private final Logger logger;
    private final NuclearBomb bomb;
    private final Config.NuclearConfig config;
    private final EffectsService effectsService;
    private final RadiationService radiationService;

    private int currentPhase = 0; // 0: detonation, 1: fireball, 2: shockwave, 3: mushroom
    private int ticksElapsed = 0;
    private int currentRadius = 0;
    private final Location centerLocation;
    private final World weWorld;
    private final BlockVector3 centerVector;

    public ExplosionTask(Logger logger, NuclearBomb bomb, Config.NuclearConfig config,
                        EffectsService effectsService, RadiationService radiationService) {
        this.logger = logger;
        this.bomb = bomb;
        this.config = config;
        this.effectsService = effectsService;
        this.radiationService = radiationService;
        this.centerLocation = bomb.getLocation();
        this.weWorld = BukkitAdapter.adapt(centerLocation.getWorld());
        this.centerVector = BlockVector3.at(centerLocation.getX(), centerLocation.getY(), centerLocation.getZ());
    }

    @Override
    public void run() {
        try {
            switch (currentPhase) {
                case 0 -> handleDetonationPhase();
                case 1 -> handleFireballPhase();
                case 2 -> handleShockwavePhase();
                case 3 -> handleMushroomPhase();
                default -> {
                    finishExplosion();
                    cancel();
                }
            }
            ticksElapsed++;
        } catch (Exception e) {
            logger.error("Error durante la explosión nuclear", e);
            cancel();
        }
    }

    private void handleDetonationPhase() {
        Config.DetonationPhase detonation = config.phases().detonation();

        if (ticksElapsed == 0) {
            triggerFlashEffect();
            triggerDetonationSound();

            centerLocation.getWorld().createExplosion(centerLocation, 8.0f, false, false);

            logger.info("Bomba nuclear detonada en {} por {}",
                       centerLocation, bomb.getActivator().getName());
        }

        if (ticksElapsed >= detonation.durationSeconds() * 20) {
            currentPhase = 1;
            ticksElapsed = 0;
            currentRadius = 5;
        }
    }

    private void handleFireballPhase() {
        Config.FireballPhase fireball = config.phases().fireball();

        if (ticksElapsed % 2 == 0) {
            currentRadius += fireball.expansionSpeed();

            if (currentRadius <= config.maxRadius() / 3) {
                createFireballSphere(currentRadius);

                if (fireball.fireParticles()) {
                    effectsService.spawnFireballParticles(centerLocation, currentRadius);
                }
            }
        }

        if (ticksElapsed >= fireball.durationSeconds() * 20) {
            currentPhase = 2;
            ticksElapsed = 0;
            currentRadius = config.maxRadius() / 3;
        }
    }

    private void handleShockwavePhase() {
        Config.ShockwavePhase shockwave = config.phases().shockwave();

        currentRadius += shockwave.expansionSpeed();

        if (currentRadius <= config.maxRadius()) {
            createDestructionLayer(currentRadius);

            damagePlayersInRadius(currentRadius);
            triggerScreenEffects(currentRadius);
        }

        if (currentRadius >= config.maxRadius() || ticksElapsed >= shockwave.durationSeconds() * 20) {
            currentPhase = 3;
            ticksElapsed = 0;

            createPostExplosionRadiation();
        }
    }

    private void handleMushroomPhase() {
        Config.MushroomConfig mushroom = config.mushroom();

        if (!mushroom.enabled()) {
            finishExplosion();
            cancel();
            return;
        }

        if (ticksElapsed % 10 == 0) { // Cada 10 ticks
            effectsService.spawnMushroomCloud(centerLocation, mushroom);
        }

        if (ticksElapsed >= mushroom.durationSeconds() * 20) {
            finishExplosion();
            cancel();
        }
    }

    private void createFireballSphere(int radius) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("FalloutCore");
        if (plugin != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
                    Vector3 radiusVector = Vector3.at(radius, radius, radius);
                    EllipsoidRegion region = new EllipsoidRegion(centerVector, radiusVector);
                    if (BlockTypes.AIR != null) {
                        editSession.setBlocks((Region) region, BlockTypes.AIR.getDefaultState().toBaseBlock());
                    }
                } catch (Exception e) {
                    logger.error("error al crear bola de fuego", e);
                }
            });
        }
    }

    private void createDestructionLayer(int radius) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("FalloutCore");
        if (plugin != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
                    List<Config.DestructionLayer> layers = config.phases().shockwave().destructionLayers();

                    for (Config.DestructionLayer layer : layers) {
                        if (radius <= layer.radius()) {
                            applyDestructionLayer(editSession, radius, layer);
                            break;
                        }
                    }
                } catch (Exception e) {
                    logger.error("error al crear capa de destrucción", e);
                }
            });
        }
    }

    private void applyDestructionLayer(EditSession editSession, int radius, Config.DestructionLayer layer) {
        try {
            Vector3 radiusVector = Vector3.at(radius, radius, radius);
            EllipsoidRegion region = new EllipsoidRegion(centerVector, radiusVector);

            switch (layer.material().toUpperCase()) {
                case "AIR" -> {
                    if (BlockTypes.AIR != null) {
                        editSession.setBlocks((Region) region, BlockTypes.AIR.getDefaultState());
                    }
                }
                case "GLASS" -> {
                    if (BlockTypes.GLASS != null) {
                        editSession.setBlocks((Region) region, BlockTypes.GLASS.getDefaultState());
                    }
                }
                case "COBBLESTONE" -> {
                    if (BlockTypes.COBBLESTONE != null) {
                        editSession.setBlocks((Region) region, BlockTypes.COBBLESTONE.getDefaultState());
                    }
                }
                case "RANDOM_DAMAGE" -> applyRandomDamage(editSession, region, layer.destructionChance());
            }

            if (radius <= 20) {
                createCrater(editSession);
            }

        } catch (Exception e) {
            logger.error("error al aplicar capa de destrucción", e);
        }
    }

    private void applyRandomDamage(EditSession editSession, EllipsoidRegion region, double chance) {
        region.forEach(blockVector -> {
            if (ThreadLocalRandom.current().nextDouble() < chance) {
                try {
                    if (BlockTypes.AIR != null) {
                        editSession.setBlock(blockVector, BlockTypes.AIR.getDefaultState());
                    }
                } catch (Exception ignored) {}
            }
        });
    }

    private void createCrater(EditSession editSession) {
        try {
            int craterDepth = config.craterDepth();
            Vector3 craterRadius = Vector3.at(20, craterDepth, 20);
            EllipsoidRegion craterRegion = new EllipsoidRegion(centerVector, craterRadius);
            if (BlockTypes.AIR != null) {
                editSession.setBlocks((Region) craterRegion, BlockTypes.AIR.getDefaultState());
            }
        } catch (Exception e) {
            logger.error("error al crear cráter", e);
        }
    }

    private void damagePlayersInRadius(int radius) {
        for (Player player : centerLocation.getWorld().getPlayers()) {
            double distance = player.getLocation().distance(centerLocation);

            if (distance <= radius) {
                double damage = calculateDamage(distance, radius);

                if (!isPlayerProtected(player)) {
                    player.damage(damage);

                    applyRadiationEffects(player, distance);
                }
            }
        }
    }

    private double calculateDamage(double distance, int maxRadius) {
        double percentage = 1.0 - (distance / maxRadius);
        return Math.max(1.0, percentage * 40.0); // Máximo 40 de daño, mínimo 1
    }

    private boolean isPlayerProtected(Player player) {
        // TODO: Implementar verificación de WorldGuard
        // Por ahora retornar false
        return false;
    }

    private void applyRadiationEffects(Player player, double distance) {

        int radiationLevel = config.postRadiation().radiationLevel();
        int duration = 600;

        switch (radiationLevel) {
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

    private void triggerFlashEffect() {
        if (config.phases().detonation().flashEffect()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                double distance = player.getLocation().distance(centerLocation);
                if (distance <= config.maxRadius() * 3) {
                    effectsService.triggerFlashEffect(player);
                }
            }
        }
    }

    private void triggerDetonationSound() {
        Config.DetonationPhase detonation = config.phases().detonation();
        for (Player player : Bukkit.getOnlinePlayers()) {
            double distance = player.getLocation().distance(centerLocation);
            if (distance <= config.maxRadius() * 2) {
                try {
                    String soundTypeName = detonation.soundType();
                    Sound sound = Registry.SOUNDS.get(NamespacedKey.minecraft(soundTypeName.toLowerCase().replace("_", ".")));

                    if (sound != null) {
                        player.playSound(centerLocation, sound, detonation.soundVolume(), 1.0f);
                    } else {
                        logger.warn("sonido no encontrado: {}", soundTypeName);
                    }
                } catch (Exception e) {
                    logger.warn("error al reproducir sonido: {}", e.getMessage());
                }
            }
        }
    }

    private void triggerScreenEffects(int radius) {
        Config.ScreenEffectsConfig screenEffects = config.screenEffects();

        for (Player player : centerLocation.getWorld().getPlayers()) {
            double distance = player.getLocation().distance(centerLocation);

            if (distance <= radius + 50) {
                if (screenEffects.shake()) {
                    effectsService.triggerCameraShake(player);
                }

                if (screenEffects.distortion()) {
                    effectsService.triggerHeatDistortion(player);
                }

                if (distance <= radius && screenEffects.radioactiveOverlay()) {
                    effectsService.triggerRadioactiveOverlay(player, screenEffects.durationSeconds());
                }
            }
        }
    }

    private void createPostExplosionRadiation() {
        Config.PostRadiationConfig radConfig = config.postRadiation();

        if (radConfig.enabled()) {

            radiationService.forceRadiationLevel(radConfig.radiationLevel());

            Plugin plugin = Bukkit.getPluginManager().getPlugin("FalloutCore");
            if (plugin != null) {
                int originalLevel = radiationService.getCurrentRadiationLevel();
                long delayTicks = radConfig.durationMinutes() * 60L * 20L;

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    radiationService.forceRadiationLevel(originalLevel);
                }, delayTicks);
            }

        }
    }

    private void finishExplosion() {
        bomb.setExploded(true);
        bomb.setActive(false);
    }
}
