package me.mapacheee.falloutcore.radiation.command;

import com.google.inject.Inject;
import com.thewinterframework.command.CommandComponent;
import com.thewinterframework.configurate.Container;
import me.mapacheee.falloutcore.radiation.entity.RadiationService;
import me.mapacheee.falloutcore.shared.config.Config;
import me.mapacheee.falloutcore.shared.config.Messages;
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
    private final Container<Config> configContainer;
    private final Container<Messages> messagesContainer;

    @Inject
    public RadiationCommand(Logger logger, RadiationService radiationService, MessageUtil messageUtil,
                           Container<Config> configContainer, Container<Messages> messagesContainer) {
        this.logger = logger;
        this.radiationService = radiationService;
        this.messageUtil = messageUtil;
        this.configContainer = configContainer;
        this.messagesContainer = messagesContainer;
    }

    private Config config() {
        return configContainer.get();
    }

    private Messages messages() {
        return messagesContainer.get();
    }

    @Command("info")
    public void handleRadiationInfo(Source sender) {
        if (!(sender.source() instanceof Player player)) {
            messageUtil.sendMessage(sender.source(), messages().general().playersOnly());
            return;
        }

        messageUtil.sendRadiationSystemStatusMessage(player);
        messageUtil.sendRadiationCurrentLevelMessage(player,
            radiationService.getCurrentRadiationLevel(),
            config().radiation().maxLevel());
        messageUtil.sendRadiationHeightMessage(player, radiationService.getCurrentRadiationHeight());
        messageUtil.sendRadiationPlayersCountMessage(player, getPlayersInRadiationCount());

        String status = config().radiation().enabled() ? "Habilitado" : "Deshabilitado";
        messageUtil.sendRadiationSystemStateMessage(player, status);
    }

    @Command("setlevel <level>")
    @Permission("falloutcore.radiation.admin")
    public void handleSetLevel(Source sender, @Argument("level") int level) {
        int minLevel = config().radiation().minLevel();
        int maxLevel = config().radiation().maxLevel();

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
        target = validateTargetPlayer(sender, target);
        if (target == null) return;

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
            messageUtil.sendMessage(sender.source(), messages().general().noPermission());
            return;
        }

        target = validateTargetPlayer(sender, target);
        if (target == null) return;

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

    private Player validateTargetPlayer(Source sender, Player target) {
        if (target == null && sender.source() instanceof Player) {
            target = (Player) sender.source();
        }

        if (target == null) {
            if (sender.source() instanceof Player player) {
                messageUtil.sendRadiationSpecifyPlayerConsoleMessage(player);
            } else {
                messageUtil.sendMessage(sender.source(), "Debes especificar un jugador desde la consola");
            }
            return null;
        }

        return target;
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
