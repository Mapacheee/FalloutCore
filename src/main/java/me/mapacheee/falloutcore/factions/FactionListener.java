package me.mapacheee.falloutcore.factions;

import me.mapacheee.falloutcore.FalloutCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class FactionListener implements Listener {
    private final FactionManager factionManager = FactionManager.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!factionManager.getPlayerFaction(player)) {
            FalloutCore.getInstance().getServer().getScheduler().runTaskLater(
                    FalloutCore.getInstance(),
                    () -> showFactionSelector(player),
                    20L
            );
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();

            if (factionManager.isSameFaction(attacker, victim)) {
                event.setCancelled(true);
                attacker.sendMessage("§cNo puedes dañar a miembros de tu facción!");
            }
        }
    }

    private void showFactionSelector(Player player) {
        player.sendMessage("§6§l¡BIENVENIDO AL APOCALIPSIS!");
        player.sendMessage("§eUsa §b/faccion unirse <nombre> §epara unirte a una facción");
        player.sendMessage("§eFacciones disponibles: §aBrotherhood, §cEnclave, §eNCR");
    }
}