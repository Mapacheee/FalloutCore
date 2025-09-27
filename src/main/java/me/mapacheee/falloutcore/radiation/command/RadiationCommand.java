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
    private final Container<Config> config;
    private final Container<Messages> messages;

    @Inject
    public RadiationCommand(Logger logger, RadiationService radiationService, MessageUtil messageUtil,
                           Container<Config> config, Container<Messages> messages) {
        this.logger = logger;
        this.radiationService = radiationService;
        this.messageUtil = messageUtil;
        this.config = config;
        this.messages = messages;
    }

    @Command("info")
    public void handleRadiationInfo(Source sender) {
        Messages.Radiation rad = messages.get().radiation();

        messageUtil.sendMessage(sender.source(), rad.systemStatus());
        messageUtil.sendMessage(sender.source(), rad.currentLevel()
            .replace("{level}", String.valueOf(radiationService.getCurrentRadiationLevel()))
            .replace("{maxLevel}", String.valueOf(config.get().radiation().maxLevel())));
        messageUtil.sendMessage(sender.source(), rad.radiationHeight()
            .replace("{height}", String.valueOf(radiationService.getCurrentRadiationHeight())));
        messageUtil.sendMessage(sender.source(), rad.playersInRadiation()
            .replace("{count}", String.valueOf(getPlayersInRadiationCount())));

        String status = config.get().radiation().enabled() ? rad.systemEnabled() : rad.systemDisabled();
        messageUtil.sendMessage(sender.source(), rad.systemState()
            .replace("{status}", status));
    }

    @Command("setlevel <level>")
    @Permission("falloutcore.radiation.admin")
    public void handleSetLevel(Source sender, @Argument("level") int level) {
        Messages.Radiation rad = messages.get().radiation();

        if (level < config.get().radiation().minLevel() || level > config.get().radiation().maxLevel()) {
            messageUtil.sendMessage(sender.source(), rad.levelOutOfRange()
                .replace("{min}", String.valueOf(config.get().radiation().minLevel()))
                .replace("{max}", String.valueOf(config.get().radiation().maxLevel())));
            return;
        }

        radiationService.forceRadiationLevel(level);
        messageUtil.sendMessage(sender.source(), rad.levelSet()
            .replace("{level}", String.valueOf(level)));

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
        Messages.Radiation rad = messages.get().radiation();

        if (height < -64 || height > 320) {
            messageUtil.sendMessage(sender.source(), rad.heightOutOfRange());
            return;
        }

        radiationService.forceRadiationHeight(height);
        messageUtil.sendMessage(sender.source(), rad.heightSet()
            .replace("{height}", String.valueOf(height)));

        logger.info("Radiation height set to {} by {}", height, sender.source().getName());
    }

    @Command("check [player]")
    @Permission("falloutcore.radiation.admin")
    public void handleCheckPlayer(Source sender, @Argument(value = "player", suggestions = "players") Player target) {
        Messages.Radiation rad = messages.get().radiation();

        if (target == null && sender.source() instanceof Player) {
            target = (Player) sender.source();
        }

        if (target == null) {
            messageUtil.sendMessage(sender.source(), rad.specifyPlayerConsole());
            return;
        }

        boolean inRadiation = radiationService.isPlayerInRadiation(target);
        boolean isImmune = radiationService.isPlayerImmune(target);
        RadiationService.ArmorProtectionLevel armor = radiationService.getPlayerArmorProtection(target);

        messageUtil.sendMessage(sender.source(), rad.playerStatusHeader()
            .replace("{player}", target.getName()));
        messageUtil.sendMessage(sender.source(), rad.inRadiationStatus()
            .replace("{status}", inRadiation ? rad.inRadiationYes() : rad.inRadiationNo()));
        messageUtil.sendMessage(sender.source(), rad.immuneStatus()
            .replace("{status}", isImmune ? rad.immuneTrue() : rad.immuneFalse()));
        messageUtil.sendMessage(sender.source(), rad.armorProtectionStatus()
            .replace("{armor}", armor.displayName)
            .replace("{level}", String.valueOf(armor.level)));
        messageUtil.sendMessage(sender.source(), rad.playerHeightStatus()
            .replace("{height}", String.valueOf((int) target.getLocation().getY())));
        messageUtil.sendMessage(sender.source(), rad.radiationHeightStatus()
            .replace("{height}", String.valueOf(radiationService.getCurrentRadiationHeight())));
    }

    @Command("immunity [player]")
    public void handleImmunityInfo(Source sender, @Argument(value = "player", suggestions = "players") Player target) {
        Messages.Radiation rad = messages.get().radiation();

        if (target != null && !target.equals(sender.source()) &&
            !sender.source().hasPermission("falloutcore.radiation.admin")) {
            messageUtil.sendMessage(sender.source(), messages.get().general().noPermission());
            return;
        }

        if (target == null && sender.source() instanceof Player) {
            target = (Player) sender.source();
        }

        if (target == null) {
            messageUtil.sendMessage(sender.source(), rad.specifyPlayerConsole());
            return;
        }

        boolean isImmune = radiationService.isPlayerImmune(target);

        if (isImmune) {
            messageUtil.sendMessage(sender.source(), rad.playerImmune()
                .replace("{player}", target.getName()));
        } else {
            messageUtil.sendMessage(sender.source(), rad.playerNotImmune()
                .replace("{player}", target.getName()));
            messageUtil.sendMessage(sender.source(), rad.immunityInstructions()
                .replace("{player}", target.getName()));
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
