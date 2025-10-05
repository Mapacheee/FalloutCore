package me.mapacheee.falloutcore.shared.effects;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import com.google.inject.Inject;
import com.thewinterframework.service.annotation.Service;
import com.thewinterframework.service.annotation.lifecycle.OnEnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EffectsService {

    private final Logger logger;
    private final Plugin plugin;

    private final Set<UUID> playersInRadiation = ConcurrentHashMap.newKeySet();
    private final Set<UUID> playersWithTpaEffects = ConcurrentHashMap.newKeySet();

    private final Map<UUID, Integer> radiationEffectTasks = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> tpaEffectTasks = new ConcurrentHashMap<>();


    @Inject
    public EffectsService(Logger logger, Plugin plugin) {
        this.logger = logger;
        this.plugin = plugin;
    }

    @OnEnable
    public void initialize() {
        Bukkit.getScheduler().runTaskLater(plugin, ParticleTypes.HAPPY_VILLAGER::getName, 20L);
        logger.info("EffectsService inicializado");
    }

    public void startRadiationEffects(Player player, int radiationLevel) {

        UUID playerId = player.getUniqueId();

        if (playersInRadiation.contains(playerId)) {
            return;
        }

        playersInRadiation.add(playerId);

        int taskId = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !playersInRadiation.contains(playerId)) {
                    cancel();
                    return;
                }

                spawnRadiationParticles(player, radiationLevel);

                if (Math.random() < 0.33) {
                    createRadiationScreenEffect(player, radiationLevel);
                }
            }
        }.runTaskTimer(plugin, 0L, 10L).getTaskId();

        radiationEffectTasks.put(playerId, taskId);
    }

    public void stopRadiationEffects(Player player) {
        UUID playerId = player.getUniqueId();
        playersInRadiation.remove(playerId);

        Integer taskId = radiationEffectTasks.remove(playerId);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        spawnCleansingParticles(player);

    }

    public void playRadiationEffect(Player player, int radiationLevel) {

        if (!playersInRadiation.contains(player.getUniqueId())) {
            startRadiationEffects(player, radiationLevel);
        }
    }

    public void playTeleportEffect(Player player) {
        spawnTeleportArrivedParticles(player);
    }

    private void spawnTeleportArrivedParticles(Player player) {
        Location loc = player.getLocation().add(0, 0.1, 0);

        for (int i = 0; i < 20; i++) {
            double offsetX = (Math.random() - 0.5) * 3;
            double offsetY = Math.random() * 2;
            double offsetZ = (Math.random() - 0.5) * 3;

            WrapperPlayServerParticle arrivalParticle = new WrapperPlayServerParticle(
                new Particle<>(ParticleTypes.FIREWORK),
                true,
                new Vector3d(loc.getX() + offsetX, loc.getY() + offsetY, loc.getZ() + offsetZ),
                new Vector3f((float) offsetX * 0.1f, (float) offsetY * 0.1f, (float) offsetZ * 0.1f),
                1.0f,
                5
            );

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, arrivalParticle);
        }
    }

    private void spawnRadiationParticles(Player player, int radiationLevel) {

        Location loc = player.getLocation().add(0, 1, 0);

        for (int i = 0; i < 5 + radiationLevel * 2; i++) {
            double offsetX = (Math.random() - 0.5) * 2;
            double offsetY = Math.random() * 2;
            double offsetZ = (Math.random() - 0.5) * 2;

            WrapperPlayServerParticle particle = new WrapperPlayServerParticle(
                new Particle<>(ParticleTypes.HAPPY_VILLAGER),
                false,
                new Vector3d(loc.getX() + offsetX, loc.getY() + offsetY, loc.getZ() + offsetZ),
                new Vector3f(0.1f, 0.1f, 0.1f),
                0.1f,
                1
            );

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, particle);
        }

        if (radiationLevel >= 3) {
            for (int i = 0; i < radiationLevel; i++) {
                double offsetX = (Math.random() - 0.5) * 1.5;
                double offsetY = Math.random() * 1.5 + 0.5;
                double offsetZ = (Math.random() - 0.5) * 1.5;

                WrapperPlayServerParticle dangerParticle = new WrapperPlayServerParticle(
                    new Particle<>(ParticleTypes.DAMAGE_INDICATOR),
                    false,
                    new Vector3d(loc.getX() + offsetX, loc.getY() + offsetY, loc.getZ() + offsetZ),
                    new Vector3f(0.2f, 0.2f, 0.2f),
                    0.1f,
                    1
                );

                PacketEvents.getAPI().getPlayerManager().sendPacket(player, dangerParticle);
            }
        }
    }

    private void createRadiationScreenEffect(Player player, int radiationLevel) {
        Location eyeLevel = player.getEyeLocation();

        for (int i = 0; i < radiationLevel * 3; i++) {
            double offsetX = (Math.random() - 0.5) * 0.5;
            double offsetY = (Math.random() - 0.5) * 0.3;
            double offsetZ = (Math.random() - 0.5) * 0.5;

            WrapperPlayServerParticle screenEffect = new WrapperPlayServerParticle(
                new Particle<>(ParticleTypes.PORTAL),
                false,
                new Vector3d(eyeLevel.getX() + offsetX, eyeLevel.getY() + offsetY, eyeLevel.getZ() + offsetZ),
                new Vector3f(0.05f, 0.05f, 0.05f),
                0.02f,
                3
            );

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, screenEffect);
        }
    }

    private void spawnCleansingParticles(Player player) {
        Location loc = player.getLocation().add(0, 1, 0);

        for (int i = 0; i < 15; i++) {
            double offsetX = (Math.random() - 0.5) * 2;
            double offsetY = Math.random() * 2;
            double offsetZ = (Math.random() - 0.5) * 2;

            WrapperPlayServerParticle cleanParticle = new WrapperPlayServerParticle(
                new Particle<>(ParticleTypes.SPLASH),
                false,
                new Vector3d(loc.getX() + offsetX, loc.getY() + offsetY, loc.getZ() + offsetZ),
                new Vector3f(0.2f, 0.2f, 0.2f),
                0.15f,
                1
            );

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, cleanParticle);
        }
    }

    public void startTpaAnimation(Player player) {
        UUID playerId = player.getUniqueId();

        if (playersWithTpaEffects.contains(playerId)) {
            return;
        }

        playersWithTpaEffects.add(playerId);

        int taskId = new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 60;

            @Override
            public void run() {
                if (!player.isOnline() || !playersWithTpaEffects.contains(playerId) || ticks >= maxTicks) {
                    cancel();
                    playersWithTpaEffects.remove(playerId);
                    tpaEffectTasks.remove(playerId);
                    return;
                }

                spawnTpaParticles(player, ticks);
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L).getTaskId();

        tpaEffectTasks.put(playerId, taskId);
    }

    public void stopTpaAnimation(Player player) {
        UUID playerId = player.getUniqueId();
        playersWithTpaEffects.remove(playerId);

        Integer taskId = tpaEffectTasks.remove(playerId);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    private void spawnTpaParticles(Player player, int tick) {
        Location loc = player.getLocation().add(0, 0.5, 0);
        double radius = 1.0 + (tick * 0.02);

        for (int i = 0; i < 8; i++) {
            double angle = (i / 8.0) * 2 * Math.PI + (tick * 0.1);
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            double y = Math.sin(tick * 0.2) * 0.5;

            WrapperPlayServerParticle tpaParticle = new WrapperPlayServerParticle(
                new Particle<>(ParticleTypes.ENCHANT),
                false,
                new Vector3d(loc.getX() + x, loc.getY() + y, loc.getZ() + z),
                new Vector3f(0.0f, 0.1f, 0.0f),
                0.1f,
                1
            );

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, tpaParticle);
        }

        if (tick % 10 == 0) {
            for (int i = 0; i < 3; i++) {
                double offsetX = (Math.random() - 0.5) * 0.6;
                double offsetY = Math.random() * 1;
                double offsetZ = (Math.random() - 0.5) * 0.6;

                WrapperPlayServerParticle centerParticle = new WrapperPlayServerParticle(
                    new Particle<>(ParticleTypes.END_ROD),
                    false,
                    new Vector3d(loc.getX() + offsetX, loc.getY() + offsetY, loc.getZ() + offsetZ),
                    new Vector3f(0.0f, 0.05f, 0.0f),
                    0.05f,
                    1
                );

                PacketEvents.getAPI().getPlayerManager().sendPacket(player, centerParticle);
            }
        }
    }

    public void cleanupPlayerEffects(Player player) {
        stopRadiationEffects(player);
        stopTpaAnimation(player);
    }

    public void createRadiationZoneIndicator(Location center, int radiationLevel, int radius) {
        for (int i = 0; i < 20; i++) {
            double angle = (i / 20.0) * 2 * Math.PI;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;

            Location particleLocation = center.clone().add(x, 0, z);
            WrapperPlayServerParticle borderParticle = new WrapperPlayServerParticle(
                new Particle<>(getRadiationZoneParticleType(radiationLevel)),
                false,
                new Vector3d(particleLocation.getX(), particleLocation.getY(), particleLocation.getZ()),
                new Vector3f(0.0f, 0.1f, 0.0f),
                0.1f,
                1
            );

            for (Player player : center.getWorld().getPlayers()) {
                if (player.getLocation().distance(center) <= radius + 50) {
                    PacketEvents.getAPI().getPlayerManager().sendPacket(player, borderParticle);
                }
            }
        }

        for (int i = 0; i < radiationLevel * 5; i++) {
            double offsetX = (Math.random() - 0.5) * 10;
            double offsetY = Math.random() * 5;
            double offsetZ = (Math.random() - 0.5) * 10;

            WrapperPlayServerParticle centerParticle = new WrapperPlayServerParticle(
                new Particle<>(ParticleTypes.SMOKE),
                false,
                new Vector3d(center.getX() + offsetX, center.getY() + offsetY, center.getZ() + offsetZ),
                new Vector3f(0.1f, 0.2f, 0.1f),
                0.05f,
                1
            );

            for (Player player : center.getWorld().getPlayers()) {
                if (player.getLocation().distance(center) <= radius + 50) {
                    PacketEvents.getAPI().getPlayerManager().sendPacket(player, centerParticle);
                }
            }
        }
    }

    private com.github.retrooper.packetevents.protocol.particle.type.ParticleType<?> getRadiationZoneParticleType(int radiationLevel) {
        return switch (radiationLevel) {
            case 1, 2 -> ParticleTypes.HAPPY_VILLAGER;
            case 3, 4 -> ParticleTypes.ANGRY_VILLAGER;
            default -> ParticleTypes.DAMAGE_INDICATOR;
        };
    }

    // Nuclear Bomb Effects
    public void triggerFlashEffect(Player player) {
        try {
            // Efecto de flash cegador blanco
            WrapperPlayServerParticle flashParticle = new WrapperPlayServerParticle(
                new Particle<>(ParticleTypes.FLASH),
                true,
                new Vector3d(player.getLocation().getX(), player.getLocation().getY() + 1, player.getLocation().getZ()),
                new Vector3f(0, 0, 0),
                10.0f,
                100
            );

            PacketEvents.getAPI().getPlayerManager().sendPacket(player, flashParticle);
            logger.debug("Flash effect enviado a {}", player.getName());
        } catch (Exception e) {
            logger.warn("Error al enviar efecto de flash a {}: {}", player.getName(), e.getMessage());
        }
    }

    public void triggerCameraShake(Player player) {
        // Simular sacudida de cámara moviendo al jugador ligeramente
        Bukkit.getScheduler().runTask(plugin, () -> {
            Location loc = player.getLocation();
            for (int i = 0; i < 10; i++) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    double offsetX = (Math.random() - 0.5) * 0.1;
                    double offsetZ = (Math.random() - 0.5) * 0.1;
                    Location newLoc = player.getLocation().add(offsetX, 0, offsetZ);
                    newLoc.setYaw(loc.getYaw());
                    newLoc.setPitch(loc.getPitch());
                    player.teleport(newLoc);
                }, i * 2L);
            }
        });
    }

    public void triggerHeatDistortion(Player player) {
        try {
            // Efectos de distorsión con partículas de calor
            Location loc = player.getLocation();

            for (int i = 0; i < 50; i++) {
                double offsetX = (Math.random() - 0.5) * 20;
                double offsetY = Math.random() * 10;
                double offsetZ = (Math.random() - 0.5) * 20;

                WrapperPlayServerParticle heatParticle = new WrapperPlayServerParticle(
                    new Particle<>(ParticleTypes.FLAME),
                    false,
                    new Vector3d(loc.getX() + offsetX, loc.getY() + offsetY, loc.getZ() + offsetZ),
                    new Vector3f((float) offsetX * 0.02f, 0.5f, (float) offsetZ * 0.02f),
                    0.3f,
                    3
                );

                PacketEvents.getAPI().getPlayerManager().sendPacket(player, heatParticle);
            }
        } catch (Exception e) {
            logger.warn("Error al enviar efecto de distorsión a {}: {}", player.getName(), e.getMessage());
        }
    }

    public void triggerRadioactiveOverlay(Player player, int durationSeconds) {
        try {
            // Crear overlay radioactivo con partículas verdes
            UUID playerId = player.getUniqueId();

            BukkitRunnable overlayTask = new BukkitRunnable() {
                int ticks = 0;
                final int maxTicks = durationSeconds * 20;

                @Override
                public void run() {
                    if (ticks >= maxTicks || !player.isOnline()) {
                        cancel();
                        return;
                    }

                    Location loc = player.getEyeLocation();

                    // Partículas verdes alrededor de la pantalla
                    for (int i = 0; i < 15; i++) {
                        double angle = (Math.PI * 2 * i) / 15;
                        double x = Math.cos(angle) * 1.5;
                        double z = Math.sin(angle) * 1.5;

                        WrapperPlayServerParticle radioactiveParticle = new WrapperPlayServerParticle(
                            new Particle<>(ParticleTypes.HAPPY_VILLAGER),
                            false,
                            new Vector3d(loc.getX() + x, loc.getY(), loc.getZ() + z),
                            new Vector3f(0, 0, 0),
                            0.1f,
                            1
                        );

                        PacketEvents.getAPI().getPlayerManager().sendPacket(player, radioactiveParticle);
                    }

                    ticks++;
                }
            };

            overlayTask.runTaskTimer(plugin, 0L, 2L); // Cada 2 ticks

        } catch (Exception e) {
            logger.warn("Error al crear overlay radioactivo para {}: {}", player.getName(), e.getMessage());
        }
    }

    public void spawnFireballParticles(Location center, int radius) {
        try {
            // Efectos de bola de fuego expandiéndose
            for (Player player : center.getWorld().getPlayers()) {
                if (player.getLocation().distance(center) <= radius + 100) {

                    // Partículas de fuego en la esfera
                    for (int i = 0; i < 30; i++) {
                        double phi = Math.random() * Math.PI * 2;
                        double theta = Math.random() * Math.PI;

                        double x = radius * Math.sin(theta) * Math.cos(phi);
                        double y = radius * Math.sin(theta) * Math.sin(phi);
                        double z = radius * Math.cos(theta);

                        WrapperPlayServerParticle fireParticle = new WrapperPlayServerParticle(
                            new Particle<>(ParticleTypes.FLAME),
                            false,
                            new Vector3d(center.getX() + x, center.getY() + y, center.getZ() + z),
                            new Vector3f((float) x * 0.1f, (float) y * 0.1f, (float) z * 0.1f),
                            0.5f,
                            5
                        );

                        PacketEvents.getAPI().getPlayerManager().sendPacket(player, fireParticle);
                    }

                    // Partículas de explosión
                    for (int i = 0; i < 20; i++) {
                        double offsetX = (Math.random() - 0.5) * radius * 2;
                        double offsetY = (Math.random() - 0.5) * radius * 2;
                        double offsetZ = (Math.random() - 0.5) * radius * 2;

                        WrapperPlayServerParticle explosionParticle = new WrapperPlayServerParticle(
                            new Particle<>(ParticleTypes.EXPLOSION),
                            false,
                            new Vector3d(center.getX() + offsetX, center.getY() + offsetY, center.getZ() + offsetZ),
                            new Vector3f(0, 0, 0),
                            1.0f,
                            1
                        );

                        PacketEvents.getAPI().getPlayerManager().sendPacket(player, explosionParticle);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Error al generar partículas de bola de fuego: {}", e.getMessage());
        }
    }

    public void spawnMushroomCloud(Location center, me.mapacheee.falloutcore.shared.config.Config.MushroomConfig mushroomConfig) {
        try {
            // Crear hongo nuclear con partículas
            int height = mushroomConfig.height();
            int columnRadius = mushroomConfig.columnRadius();
            int headRadius = mushroomConfig.headRadius();
            int particleCount = mushroomConfig.particleCount();

            for (Player player : center.getWorld().getPlayers()) {
                if (player.getLocation().distance(center) <= height + 50) {

                    // Columna del hongo
                    for (int y = 0; y < height; y += 3) {
                        for (int i = 0; i < particleCount / 4; i++) {
                            double angle = (Math.PI * 2 * i) / ((double) particleCount / 4);
                            double radius = columnRadius * (1.0 - (double) y / height * 0.7);

                            double x = Math.cos(angle) * radius + (Math.random() - 0.5) * 2;
                            double z = Math.sin(angle) * radius + (Math.random() - 0.5) * 2;

                            WrapperPlayServerParticle smokeParticle = new WrapperPlayServerParticle(
                                new Particle<>(ParticleTypes.LARGE_SMOKE),
                                false,
                                new Vector3d(center.getX() + x, center.getY() + y, center.getZ() + z),
                                new Vector3f(0, 0.1f, 0),
                                0.1f,
                                1
                            );

                            PacketEvents.getAPI().getPlayerManager().sendPacket(player, smokeParticle);
                        }
                    }

                    // Cabeza del hongo
                    int headY = height - 20;
                    for (int i = 0; i < particleCount; i++) {
                        double phi = Math.random() * Math.PI * 2;
                        double theta = Math.random() * Math.PI;

                        double x = headRadius * Math.sin(theta) * Math.cos(phi);
                        double y = headRadius * Math.sin(theta) * Math.sin(phi) * 0.3; // Más plano
                        double z = headRadius * Math.cos(theta);

                        WrapperPlayServerParticle cloudParticle = new WrapperPlayServerParticle(
                            new Particle<>(ParticleTypes.CLOUD),
                            false,
                            new Vector3d(center.getX() + x, center.getY() + headY + y, center.getZ() + z),
                            new Vector3f(0, 0.05f, 0),
                            0.2f,
                            2
                        );

                        PacketEvents.getAPI().getPlayerManager().sendPacket(player, cloudParticle);

                        // Partículas de ceniza cayendo
                        if (i % 3 == 0) {
                            WrapperPlayServerParticle ashParticle = new WrapperPlayServerParticle(
                                new Particle<>(ParticleTypes.ASH),
                                false,
                                new Vector3d(center.getX() + x, center.getY() + headY + y + 10, center.getZ() + z),
                                new Vector3f((float) (Math.random() - 0.5) * 0.1f, -0.2f, (float) (Math.random() - 0.5) * 0.1f),
                                0.1f,
                                1
                            );

                            PacketEvents.getAPI().getPlayerManager().sendPacket(player, ashParticle);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Error al generar hongo nuclear: {}", e.getMessage());
        }
    }
}
