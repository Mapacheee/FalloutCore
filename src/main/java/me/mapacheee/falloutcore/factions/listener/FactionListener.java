package me.mapacheee.falloutcore.factions.listener;

import com.google.inject.Inject;
import com.thewinterframework.configurate.Container;
import com.thewinterframework.paper.listener.ListenerComponent;
import me.mapacheee.falloutcore.factions.entity.FactionService;
import me.mapacheee.falloutcore.shared.config.Config;
import me.mapacheee.falloutcore.shared.config.Messages;
import me.mapacheee.falloutcore.shared.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@ListenerComponent
public class FactionListener implements Listener {

    private final FactionService factionService;
    private final MessageUtil messageUtil;
    private final Container<Config> config;
    private final Container<Messages> messages;

    @Inject
    public FactionListener(FactionService factionService, MessageUtil messageUtil,
                          Container<Config> config, Container<Messages> messages) {
        this.factionService = factionService;
        this.messageUtil = messageUtil;
        this.config = config;
        this.messages = messages;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker) || !(event.getEntity() instanceof Player victim)) {
            return;
        }

        if (config.get().faction().enableFriendlyFire()) {
            return;
        }

        if (factionService.isSameFaction(attacker, victim)) {
            event.setCancelled(true);
            messageUtil.sendMessage(attacker, messages.get().factions().friendlyFireBlocked());
        }
    }
}
