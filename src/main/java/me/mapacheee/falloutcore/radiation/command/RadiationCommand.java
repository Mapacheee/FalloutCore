package me.mapacheee.falloutcore.radiation.command;

import com.google.inject.Inject;
import com.thewinterframework.command.CommandComponent;
import me.mapacheee.falloutcore.radiation.entity.RadiationService;
import me.mapacheee.falloutcore.shared.config.ConfigService;
import me.mapacheee.falloutcore.shared.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.paper.util.sender.Source;
import org.slf4j.Logger;

@CommandComponent
@Command("radiation|rad")
@Permission("falloutcore.radiation.use")
public final class RadiationCommand {

    private final Logger logger;
    private final RadiationService radiationService;
    private final MessageUtil messageUtil;
    private final ConfigService configService;

    @Inject
    public RadiationCommand(Logger logger, RadiationService radiationService, MessageUtil messageUtil,
                           ConfigService configService) {
        this.logger = logger;
        this.radiationService = radiationService;
        this.messageUtil = messageUtil;
        this.configService = configService;
    }

    @Command("info")
    public void handleRadiationInfo(Source sender) {
        if (!(sender.source() instanceof Player player)) {
            messageUtil.sendMessage(sender.source(), configService.getMessages().general().playersOnly());
            return;
        }

        messageUtil.sendRadiationSystemStatusMessage(player);
        messageUtil.sendRadiationCurrentLevelMessage(player,
            radiationService.getCurrentRadiationLevel(),
            configService.getConfig().radiation().maxLevel());
        messageUtil.sendRadiationHeightMessage(player, radiationService.getCurrentRadiationHeight());
        messageUtil.sendRadiationPlayersCountMessage(player, getPlayersInRadiationCount());

        String status = configService.getConfig().radiation().enabled() ?
            configService.getMessages().radiation().systemEnabled() :
            configService.getMessages().radiation().systemDisabled();
        messageUtil.sendRadiationSystemStateMessage(player, status);
    }

    @Command("setlevel <level>")
    @Permission("falloutcore.radiation.admin")
    public void handleSetLevel(Source sender, @Argument("level") int level) {
        int minLevel = configService.getConfig().radiation().minLevel();
        int maxLevel = configService.getConfig().radiation().maxLevel();

        if (level < minLevel || level > maxLevel) {
            if (sender.source() instanceof Player player) {
                messageUtil.sendRadiationLevelOutOfRangeMessage(player, minLevel, maxLevel);
            } else {
                messageUtil.sendMessage(sender.source(), "El nivel debe estar entre " + minLevel + " y " + maxLevel);
            }
            return;
        }

        radiationService.forceRadiationLevel(level);

        if (sender.source() instanceof Player player) {
            messageUtil.sendRadiationLevelSetMessage(player, level);
        } else {
            messageUtil.sendMessage(sender.source(), "Nivel de radiación establecido a: " + level);
        }

        // Notificar a jugadores en radiación del cambio de nivel
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (radiationService.isPlayerInRadiation(player)) {
                messageUtil.sendRadiationLevelChangedMessage(player, level,
                    radiationService.getCurrentRadiationHeight(), 0, 0);
            }
        }

        logger.info("Radiation level set to {} by {}", level, sender.source().getName());
    }

    @Command("setheight <height>")
    @Permission("falloutcore.radiation.admin")
    public void handleSetHeight(Source sender, @Argument("height") int height) {
        if (height < -64 || height > 320) {
            if (sender.source() instanceof Player player) {
                messageUtil.sendRadiationHeightOutOfRangeMessage(player);
            } else {
                messageUtil.sendMessage(sender.source(), "La altura debe estar entre -64 y 320");
            }
            return;
        }

        radiationService.forceRadiationHeight(height);

        if (sender.source() instanceof Player player) {
            messageUtil.sendRadiationHeightSetMessage(player, height);
        } else {
            messageUtil.sendMessage(sender.source(), "Altura de radiación establecida a: Y " + height);
        }

        logger.info("Radiation height set to {} by {}", height, sender.source().getName());
    }

    @Command("check [player]")
    @Permission("falloutcore.radiation.admin")
    public void handleCheckPlayer(Source sender, @Argument("player") Player target) {
        if (target == null && sender.source() instanceof Player) {
            target = (Player) sender.source();
        }

        if (target == null) {
            if (sender.source() instanceof Player player) {
                messageUtil.sendRadiationSpecifyPlayerConsoleMessage(player);
            } else {
                messageUtil.sendMessage(sender.source(), "Debes especificar un jugador desde la consola");
            }
            return;
        }

        boolean inRadiation = radiationService.isPlayerInRadiation(target);
        boolean isImmune = radiationService.isPlayerImmune(target);
        RadiationService.ArmorProtectionLevel armor = radiationService.getPlayerArmorProtection(target);

        if (sender.source() instanceof Player senderPlayer) {
            messageUtil.sendRadiationPlayerStatusHeaderMessage(senderPlayer, target.getName());
            messageUtil.sendRadiationInRadiationStatusMessage(senderPlayer, inRadiation);
            messageUtil.sendRadiationImmuneStatusMessage(senderPlayer, isImmune);
            messageUtil.sendRadiationArmorProtectionStatusMessage(senderPlayer, armor.displayName, armor.level);
            messageUtil.sendRadiationPlayerHeightStatusMessage(senderPlayer, (int) target.getLocation().getY());
            messageUtil.sendRadiationRadiationHeightStatusMessage(senderPlayer, radiationService.getCurrentRadiationHeight());
        } else {
            messageUtil.sendMessage(sender.source(), "=== Estado de Radiación: " + target.getName() + " ===");
            messageUtil.sendMessage(sender.source(), "En zona de radiación: " + (inRadiation ? "Sí" : "No"));
            messageUtil.sendMessage(sender.source(), "Inmune: " + (isImmune ? "Verdadero" : "Falso"));
            messageUtil.sendMessage(sender.source(), "Protección de armadura: " + armor.displayName + " (Nivel " + armor.level + ")");
            messageUtil.sendMessage(sender.source(), "Altura actual: Y " + (int) target.getLocation().getY());
            messageUtil.sendMessage(sender.source(), "Altura de radiación: Y " + radiationService.getCurrentRadiationHeight());
        }
    }

    @Command("immunity [player]")
    public void handleImmunityInfo(Source sender, @Argument("player") Player target) {
        if (target != null && !target.equals(sender.source()) &&
            !sender.source().hasPermission("falloutcore.radiation.admin")) {
            messageUtil.sendMessage(sender.source(), configService.getMessages().general().noPermission());
            return;
        }

        if (target == null && sender.source() instanceof Player) {
            target = (Player) sender.source();
        }

        if (target == null) {
            if (sender.source() instanceof Player player) {
                messageUtil.sendRadiationSpecifyPlayerConsoleMessage(player);
            } else {
                messageUtil.sendMessage(sender.source(), "Debes especificar un jugador desde la consola");
            }
            return;
        }

        boolean isImmune = radiationService.isPlayerImmune(target);

        if (sender.source() instanceof Player senderPlayer) {
            if (isImmune) {
                messageUtil.sendRadiationPlayerImmuneMessage(senderPlayer, target.getName());
            } else {
                messageUtil.sendRadiationPlayerNotImmuneMessage(senderPlayer, target.getName());
                messageUtil.sendRadiationImmunityInstructionsMessage(senderPlayer, target.getName());
            }
        } else {
            if (isImmune) {
                messageUtil.sendMessage(sender.source(), "El jugador " + target.getName() + " es inmune a la radiación");
            } else {
                messageUtil.sendMessage(sender.source(), "El jugador " + target.getName() + " NO es inmune a la radiación");
                messageUtil.sendMessage(sender.source(), "Para otorgar inmunidad usa: /lp user " + target.getName() + " permission set falloutcore.radiation.immune true");
            }
        }
    }

    private int getPlayersInRadiationCount() {
        int count = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (radiationService.isPlayerInRadiation(player)) {
                count++;
            }
        }
        return count;
    }
}
