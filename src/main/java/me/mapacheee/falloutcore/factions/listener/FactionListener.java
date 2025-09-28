package me.mapacheee.falloutcore.factions.listener;

import com.google.inject.Inject;
import com.thewinterframework.paper.listener.ListenerComponent;
import me.mapacheee.falloutcore.factions.entity.FactionService;
import me.mapacheee.falloutcore.shared.config.ConfigService;
import me.mapacheee.falloutcore.shared.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@ListenerComponent
public class FactionListener implements Listener {

    private final FactionService factionService;
    private final MessageUtil messageUtil;
    private final ConfigService configService;

    @Inject
    public FactionListener(FactionService factionService, MessageUtil messageUtil, ConfigService configService) {
        this.factionService = factionService;
        this.messageUtil = messageUtil;
        this.configService = configService;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker) || !(event.getEntity() instanceof Player victim)) {
            return;
        }

        if (configService.getConfig().faction().enableFriendlyFire()) {
            return;
        }

        if (factionService.isSameFaction(attacker, victim)) {
            event.setCancelled(true);
            messageUtil.sendFactionFriendlyFireMessage(attacker);
        }
    }
}
