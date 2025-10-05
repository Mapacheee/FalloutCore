package me.mapacheee.falloutcore.bombs.entity;

import com.google.inject.Inject;
import com.thewinterframework.service.annotation.Service;
import com.thewinterframework.service.annotation.lifecycle.OnDisable;
import com.thewinterframework.service.annotation.lifecycle.OnEnable;
import me.mapacheee.falloutcore.shared.config.ConfigService;
import me.mapacheee.falloutcore.shared.util.MessageUtil;
import me.mapacheee.falloutcore.shared.effects.EffectsService;
import me.mapacheee.falloutcore.radiation.entity.RadiationService;
import me.mapacheee.falloutcore.bombs.event.BombDetonateEvent;
import me.mapacheee.falloutcore.bombs.event.BombExplodeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BombService {

    private final Logger logger;
    private final ConfigService configService;
    private final MessageUtil messageUtil;
    private final EffectsService effectsService;
    private final RadiationService radiationService;
    private final Plugin plugin;

    private final Map<UUID, NuclearBomb> activeBombs = new ConcurrentHashMap<>();
    private final Map<UUID, Long> playerCooldowns = new ConcurrentHashMap<>();
    private BukkitTask timerTask;

    @Inject
    public BombService(Logger logger, ConfigService configService, MessageUtil messageUtil,
                      EffectsService effectsService, RadiationService radiationService, Plugin plugin) {
        this.logger = logger;
        this.configService = configService;
        this.messageUtil = messageUtil;
        this.effectsService = effectsService;
        this.radiationService = radiationService;
        this.plugin = plugin;
    }

    @OnEnable
    void initialize() {
        if (!configService.getConfig().bomb().enabled()) {
            logger.info("bombas no activadas");
            return;
        }

        startTimerTask();
        logger.info("servicio de bombas iniciado");
    }

    @OnDisable
    void cleanup() {
        if (timerTask != null && !timerTask.isCancelled()) {
            timerTask.cancel();
        }

        activeBombs.clear();
        playerCooldowns.clear();

        logger.info("servicio de bombas deshabilitado");
    }

    public ItemStack createNuclearBombItem() {
        ItemStack bombItem = messageUtil.createBombItem();

        if (bombItem.getItemMeta() instanceof SkullMeta meta) {
            String headSkin = configService.getConfig().bomb().nuclear().headSkin();
            if (headSkin != null && !headSkin.isEmpty()) {
                applyCustomSkin(meta, headSkin);
                bombItem.setItemMeta(meta);
            }
        }

        return bombItem;
    }

    private void applyCustomSkin(SkullMeta meta, String headSkin) {
        try {
            com.destroystokyo.paper.profile.PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            com.destroystokyo.paper.profile.ProfileProperty property =
                    new com.destroystokyo.paper.profile.ProfileProperty("textures", headSkin);
            profile.getProperties().add(property);
            meta.setPlayerProfile(profile);
        } catch (Exception e) {
            logger.warn("no se pudo aplicar la skin personalizada a la bomba: {}", e.getMessage());
        }
    }

    public boolean canPlayerUseBomb(Player player) {
        if (!configService.getConfig().bomb().enabled()) {
            return false;
        }

        String permission = configService.getConfig().bomb().nuclear().requiredPermission();
        if (!player.hasPermission(permission)) {
            return false;
        }

        UUID playerId = player.getUniqueId();
        Long cooldownEnd = playerCooldowns.get(playerId);
        return cooldownEnd == null || System.currentTimeMillis() >= cooldownEnd;
    }

    public boolean activateNuclearBomb(Player player, Location location) {
        if (!canPlayerUseBomb(player)) {
            return false;
        }

        if (isLocationProtected(location)) {
            messageUtil.sendBombProtectedAreaMessage(player);
            return false;
        }

        int countdownSeconds = configService.getConfig().bomb().nuclear().timer().countdownSeconds();
        NuclearBomb bomb = new NuclearBomb(location, player, countdownSeconds);

        BombDetonateEvent event = new BombDetonateEvent(bomb, player);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        activeBombs.put(bomb.getId(), bomb);
        applyCooldown(player);
        startCountdownEffects(bomb);

        logger.info("bomba nuclear activada por {} en {}", player.getName(), location);
        return true;
    }

    private void applyCooldown(Player player) {
        int cooldownSeconds = configService.getConfig().bomb().nuclear().cooldownSeconds();
        long cooldownEnd = System.currentTimeMillis() + (cooldownSeconds * 1000L);
        playerCooldowns.put(player.getUniqueId(), cooldownEnd);
    }

    private boolean isLocationProtected(Location location) {
        if (!configService.getConfig().bomb().respectWorldGuard()) {
            return false;
        }

        // TODO: Implementar verificación de WorldGuard
        // Por ahora retornar false
        return false;
    }

    private void startCountdownEffects(NuclearBomb bomb) {
        new BukkitRunnable() {
            int secondsLeft = bomb.getCountdownSeconds();

            @Override
            public void run() {
                if (!bomb.isActive() || bomb.hasExploded()) {
                    cancel();
                    return;
                }

                if (secondsLeft <= 0) {
                    explodeBomb(bomb);
                    cancel();
                    return;
                }

                sendCountdownEffects(bomb, secondsLeft);
                secondsLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L); // Cada segundo
    }

    private void sendCountdownEffects(NuclearBomb bomb, int secondsLeft) {
        var timerConfig = configService.getConfig().bomb().nuclear().timer();

        if (!timerConfig.enabled()) {
            return;
        }

        String title = timerConfig.titleMessage()
            .replace("<seconds>", String.valueOf(secondsLeft))
            .replace("<location>", locationToString(bomb.getLocation()));

        String subtitle = timerConfig.subtitleMessage()
            .replace("<seconds>", String.valueOf(secondsLeft))
            .replace("<activator>", bomb.getActivator().getName());

        for (Player player : Bukkit.getOnlinePlayers()) {
            messageUtil.sendTitle(player, title, subtitle);

            try {
                String soundTypeName = timerConfig.soundType();
                Sound sound = Registry.SOUNDS.get(NamespacedKey.minecraft(soundTypeName.toLowerCase().replace("_", ".")));

                if (sound != null) {
                    player.playSound(player.getLocation(), sound,
                                   timerConfig.soundVolume(), timerConfig.soundPitch());
                } else {
                    logger.warn("sonido de timer no encontrado: {}", soundTypeName);
                }
            } catch (Exception e) {
                logger.warn("error al reproducir sonido de timer: {} - {}", timerConfig.soundType(), e.getMessage());
            }
        }
    }

    private String locationToString(Location location) {
        return String.format("(%d, %d, %d)",
                           location.getBlockX(),
                           location.getBlockY(),
                           location.getBlockZ());
    }

    private void explodeBomb(NuclearBomb bomb) {
        try {
            BombExplodeEvent event = new BombExplodeEvent(bomb);
            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                bomb.setActive(false);
                activeBombs.remove(bomb.getId());
                return;
            }

            ExplosionTask explosionTask = new ExplosionTask(
                logger, bomb, configService.getConfig().bomb().nuclear(),
                effectsService, radiationService
            );

            explosionTask.runTaskTimer(plugin, 0L, 1L); // Cada tick

            activeBombs.remove(bomb.getId());

        } catch (Exception e) {
            logger.error("error al explotar bomba nuclear", e);
            bomb.setActive(false);
            activeBombs.remove(bomb.getId());
        }
    }

    private void startTimerTask() {
        timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                playerCooldowns.entrySet().removeIf(entry -> entry.getValue() < currentTime);

                for (NuclearBomb bomb : new ArrayList<>(activeBombs.values())) {
                    if (bomb.shouldDetonate() && bomb.isActive() && !bomb.hasExploded()) {
                        explodeBomb(bomb);
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // Cada segundo
    }

    public Collection<NuclearBomb> getActiveBombs() {
        return Collections.unmodifiableCollection(activeBombs.values());
    }

    public long getPlayerCooldown(Player player) {
        Long cooldownEnd = playerCooldowns.get(player.getUniqueId());
        if (cooldownEnd == null || System.currentTimeMillis() >= cooldownEnd) {
            return 0;
        }
        return (cooldownEnd - System.currentTimeMillis()) / 1000;
    }

    public boolean forceExplodeBomb(UUID bombId) {
        NuclearBomb bomb = activeBombs.get(bombId);
        if (bomb == null || bomb.hasExploded()) {
            return false;
        }

        explodeBomb(bomb);
        return true;
    }

    public boolean cancelBomb(UUID bombId) {
        NuclearBomb bomb = activeBombs.get(bombId);
        if (bomb != null) {
            bomb.setActive(false);
            activeBombs.remove(bombId);
        }
        return false;
    }
}
