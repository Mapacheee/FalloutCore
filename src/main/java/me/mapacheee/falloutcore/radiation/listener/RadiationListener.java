package me.mapacheee.falloutcore.radiation.listener;

import com.google.inject.Inject;
import com.thewinterframework.paper.listener.ListenerComponent;
import me.mapacheee.falloutcore.radiation.event.RadiationEnterEvent;
import me.mapacheee.falloutcore.radiation.event.RadiationExitEvent;
import me.mapacheee.falloutcore.shared.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


@ListenerComponent
public class RadiationListener implements Listener {

    private final MessageUtil messageUtil;

    @Inject
    public RadiationListener(MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
    }


    @EventHandler
    public void onRadiationEnter(RadiationEnterEvent event) {
        Player player = event.getPlayer();
        int radiationLevel = event.getRadiationLevel();

        messageUtil.sendRadiationEnterMessage(player, radiationLevel);
    }

    @EventHandler
    public void onRadiationExit(RadiationExitEvent event) {
        Player player = event.getPlayer();
        messageUtil.sendRadiationExitMessage(player);

    }
}
