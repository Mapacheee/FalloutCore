package me.mapacheee.falloutcore.radiation.listener;

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

    public RadiationListener(MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
    }


    @EventHandler
    public void onRadiationEnter(RadiationEnterEvent event) {
        Player player = event.getPlayer();
        int radiationLevel = event.getRadiationLevel();

        messageUtil.sendRadiationMessage(player, "radiationWarning",
                "level", String.valueOf(radiationLevel));

        player.getServer().getLogger().info(
                String.format("Player %s entered radiation zone (Level %d)",
                        player.getName(), radiationLevel));
    }


    @EventHandler
    public void onRadiationExit(RadiationExitEvent event) {
        Player player = event.getPlayer();
        messageUtil.sendRadiationMessage(player, "radiationSafe");

    }
}
