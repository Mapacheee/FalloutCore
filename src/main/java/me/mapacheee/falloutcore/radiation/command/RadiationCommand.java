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
        if (!(sender.source() instanceof Player)) {
            messageUtil.sendMessage(sender.source(), configService.getMessages().general().playersOnly());
            return;
        }

        Player player = (Player) sender.source();
        messageUtil.sendRadiationMessage(player, "systemStatus");
        messageUtil.sendRadiationMessage(player, "currentLevel",
            "level", String.valueOf(radiationService.getCurrentRadiationLevel()),
            "maxLevel", String.valueOf(configService.getConfig().radiation().maxLevel()));
        messageUtil.sendRadiationMessage(player, "radiationHeight",
            "height", String.valueOf(radiationService.getCurrentRadiationHeight()));
        messageUtil.sendRadiationMessage(player, "playersInRadiation",
            "count", String.valueOf(getPlayersInRadiationCount()));

        String status = configService.getConfig().radiation().enabled() ?
            configService.getMessages().radiation().systemEnabled() : configService.getMessages().radiation().systemDisabled();
        messageUtil.sendRadiationMessage(player, "systemState", "status", status);
    }

    @Command("setlevel <level>")
    @Permission("falloutcore.radiation.admin")
    public void handleSetLevel(Source sender, @Argument("level") int level) {
        if (level < configService.getConfig().radiation().minLevel() || level > configService.getConfig().radiation().maxLevel()) {
            if (sender.source() instanceof Player) {
                messageUtil.sendRadiationMessage((Player) sender.source(), "levelOutOfRange",
                    "min", String.valueOf(configService.getConfig().radiation().minLevel()),
                    "max", String.valueOf(configService.getConfig().radiation().maxLevel()));
            } else {
                messageUtil.sendMessage(sender.source(), "El nivel debe estar entre " + configService.getConfig().radiation().minLevel() + " y " + configService.getConfig().radiation().maxLevel());
            }
            return;
        }

        radiationService.forceRadiationLevel(level);
        if (sender.source() instanceof Player) {
            messageUtil.sendRadiationMessage((Player) sender.source(), "levelSet",
                "level", String.valueOf(level));
        } else {
            messageUtil.sendMessage(sender.source(), "Nivel de radiación establecido a: " + level);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (radiationService.isPlayerInRadiation(player)) {
                messageUtil.sendRadiationMessage(player, "levelChanged",
                    "level", String.valueOf(level),
                    "height", String.valueOf(radiationService.getCurrentRadiationHeight()));
            }
        }

        logger.info("Radiation level set to {} by {}", level, sender.source().getName());
    }

    @Command("setheight <height>")
    @Permission("falloutcore.radiation.admin")
    public void handleSetHeight(Source sender, @Argument("height") int height) {
        if (height < -64 || height > 320) {
            if (sender.source() instanceof Player) {
                messageUtil.sendRadiationMessage((Player) sender.source(), "heightOutOfRange");
            } else {
                messageUtil.sendMessage(sender.source(), "La altura debe estar entre -64 y 320");
            }
            return;
        }

        radiationService.forceRadiationHeight(height);
        if (sender.source() instanceof Player) {
            messageUtil.sendRadiationMessage((Player) sender.source(), "heightSet",
                "height", String.valueOf(height));
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
            if (sender.source() instanceof Player) {
                messageUtil.sendRadiationMessage((Player) sender.source(), "specifyPlayerConsole");
            } else {
                messageUtil.sendMessage(sender.source(), "Debes especificar un jugador desde la consola");
            }
            return;
        }

        boolean inRadiation = radiationService.isPlayerInRadiation(target);
        boolean isImmune = radiationService.isPlayerImmune(target);
        RadiationService.ArmorProtectionLevel armor = radiationService.getPlayerArmorProtection(target);

        if (sender.source() instanceof Player) {
            Player senderPlayer = (Player) sender.source();
            messageUtil.sendRadiationMessage(senderPlayer, "playerStatusHeader",
                "player", target.getName());
            messageUtil.sendRadiationMessage(senderPlayer, "inRadiationStatus",
                "status", inRadiation ? configService.getMessages().radiation().inRadiationYes() : configService.getMessages().radiation().inRadiationNo());
            messageUtil.sendRadiationMessage(senderPlayer, "immuneStatus",
                "status", isImmune ? configService.getMessages().radiation().immuneTrue() : configService.getMessages().radiation().immuneFalse());
            messageUtil.sendRadiationMessage(senderPlayer, "armorProtectionStatus",
                "armor", armor.displayName,
                "level", String.valueOf(armor.level));
            messageUtil.sendRadiationMessage(senderPlayer, "playerHeightStatus",
                "height", String.valueOf((int) target.getLocation().getY()));
            messageUtil.sendRadiationMessage(senderPlayer, "radiationHeightStatus",
                "height", String.valueOf(radiationService.getCurrentRadiationHeight()));
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
            if (sender.source() instanceof Player) {
                messageUtil.sendRadiationMessage((Player) sender.source(), "specifyPlayerConsole");
            } else {
                messageUtil.sendMessage(sender.source(), "Debes especificar un jugador desde la consola");
            }
            return;
        }

        boolean isImmune = radiationService.isPlayerImmune(target);

        if (sender.source() instanceof Player) {
            Player senderPlayer = (Player) sender.source();
            if (isImmune) {
                messageUtil.sendRadiationMessage(senderPlayer, "playerImmune",
                    "player", target.getName());
            } else {
                messageUtil.sendRadiationMessage(senderPlayer, "playerNotImmune",
                    "player", target.getName());
                messageUtil.sendRadiationMessage(senderPlayer, "immunityInstructions",
                    "player", target.getName());
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
