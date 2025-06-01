package me.mapacheee.falloutcore.factions;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;


public class FactionListener implements Listener {
    private final FactionManager factionManager = FactionManager.getInstance();

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player victim) {

            if (factionManager.isSameFaction(attacker, victim)) {
                event.setCancelled(true);
                attacker.sendMessage("§cNo puedes dañar a miembros de tu facción!");
            }
        }
    }

}