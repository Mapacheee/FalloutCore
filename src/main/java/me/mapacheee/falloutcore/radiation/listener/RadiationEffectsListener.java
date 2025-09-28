package me.mapacheee.falloutcore.radiation.listener;

import com.google.inject.Inject;
import com.thewinterframework.paper.listener.ListenerComponent;
import me.mapacheee.falloutcore.radiation.entity.RadiationService;
import me.mapacheee.falloutcore.radiation.event.RadiationLevelChangeEvent;
import me.mapacheee.falloutcore.shared.effects.EffectsService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.slf4j.Logger;

@ListenerComponent
public class RadiationEffectsListener implements Listener {

    private final Logger logger;
    private final RadiationService radiationService;
    private final EffectsService effectsService;
    private final Plugin plugin;

    @Inject
    public RadiationEffectsListener(Logger logger, RadiationService radiationService,
                                   EffectsService effectsService, Plugin plugin) {
        this.logger = logger;
        this.radiationService = radiationService;
        this.effectsService = effectsService;
        this.plugin = plugin;

        startZoneIndicatorTask();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            showRadiationZoneIndicators(event.getPlayer().getLocation());
        }, 20L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        effectsService.cleanupPlayerEffects(event.getPlayer());
    }

    @EventHandler
    public void onRadiationLevelChange(RadiationLevelChangeEvent event) {
        for (var player : Bukkit.getOnlinePlayers()) {
            createLevelChangeEffect(player, event.getOldLevel(), event.getNewLevel());
        }

    }

    private void startZoneIndicatorTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (var player : Bukkit.getOnlinePlayers()) {
                    if (player.getLocation().getY() >= radiationService.getCurrentRadiationHeight() - 10) {
                        showRadiationZoneIndicators(player.getLocation());
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L * 30L);
    }

    private void showRadiationZoneIndicators(Location playerLocation) {
        Location zoneCenter = new Location(
            playerLocation.getWorld(),
            playerLocation.getX(),
            radiationService.getCurrentRadiationHeight(),
            playerLocation.getZ()
        );

        int visualRadius = 50 + (radiationService.getCurrentRadiationLevel() * 10);

        effectsService.createRadiationZoneIndicator(
            zoneCenter,
            radiationService.getCurrentRadiationLevel(),
            visualRadius
        );
    }

    private void createLevelChangeEffect(org.bukkit.entity.Player player, int oldLevel, int newLevel) {
        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 40;

            @Override
            public void run() {
                if (!player.isOnline() || ticks >= maxTicks) {
                    cancel();
                    return;
                }

                Location loc = player.getLocation().add(0, 2, 0);

                boolean levelIncreased = newLevel > oldLevel;

                for (int i = 0; i < 5; i++) {
                    double offsetX = (Math.random() - 0.5) * 10;
                    double offsetZ = (Math.random() - 0.5) * 10;
                    effectsService.createRadiationZoneIndicator(
                        loc.clone().add(offsetX, 0, offsetZ),
                        newLevel,
                        3
                    );
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
}
