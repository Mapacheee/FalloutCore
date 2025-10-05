package me.mapacheee.falloutcore.bombs.command;

import com.google.inject.Inject;
import com.thewinterframework.command.CommandComponent;
import me.mapacheee.falloutcore.bombs.entity.BombService;
import me.mapacheee.falloutcore.bombs.entity.NuclearBomb;
import me.mapacheee.falloutcore.shared.config.ConfigService;
import me.mapacheee.falloutcore.shared.util.MessageUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.paper.util.sender.Source;
import org.slf4j.Logger;

import java.util.UUID;

@CommandComponent
@Command("bomb")
@Permission("falloutcore.bomb.use")
public final class BombCommand {

    private final Logger logger;
    private final BombService bombService;
    private final MessageUtil messageUtil;
    private final ConfigService configService;

    @Inject
    public BombCommand(Logger logger, BombService bombService, MessageUtil messageUtil, ConfigService configService) {
        this.logger = logger;
        this.bombService = bombService;
        this.messageUtil = messageUtil;
        this.configService = configService;
    }

    @Command("give [player]")
    @Permission("falloutcore.bomb.admin")
    public void handleGiveBomb(Source sender, @Argument("player") Player target) {
        var bombConfig = configService.getConfig().bomb();

        if (!bombConfig.enabled()) {
            messageUtil.sendBombModuleDisabledMessage(sender.source());
            return;
        }

        Player targetPlayer = target != null ? target : (Player) sender.source();

        ItemStack bombItem = bombService.createNuclearBombItem();
        targetPlayer.getInventory().addItem(bombItem);

        messageUtil.sendBombGivenMessage(targetPlayer);

        if (!targetPlayer.equals(sender.source())) {
            messageUtil.sendBombGivenToPlayerMessage(sender.source(), targetPlayer.getName());
        }

        logger.info("Bomba nuclear entregada a {} por {}",
                   targetPlayer.getName(), sender.source().getName());
    }

    @Command("detonate <x> <y> <z>")
    @Permission("falloutcore.bomb.admin.detonate")
    public void handleDetonateBomb(Source sender,
                                  @Argument("x") int x,
                                  @Argument("y") int y,
                                  @Argument("z") int z) {
        var bombConfig = configService.getConfig().bomb();

        if (!bombConfig.enabled()) {
            messageUtil.sendBombModuleDisabledMessage(sender.source());
            return;
        }

        if (!(sender.source() instanceof Player player)) {
            messageUtil.sendPlayersOnlyCommandMessage(sender.source());
            return;
        }

        Location location = new Location(player.getWorld(), x, y, z);

        if (bombService.activateNuclearBomb(player, location)) {
            messageUtil.sendBombActivatedAtLocationMessage(sender.source(), x, y, z);
        } else {
            messageUtil.sendBombActivationFailedMessage(player);
        }
    }

    @Command("list")
    @Permission("falloutcore.bomb.admin")
    public void handleListBombs(Source sender) {
        var activeBombs = bombService.getActiveBombs();

        if (activeBombs.isEmpty()) {
            messageUtil.sendBombListEmptyMessage(sender.source());
            return;
        }

        messageUtil.sendBombListHeaderMessage(sender.source());

        for (NuclearBomb bomb : activeBombs) {
            String shortId = bomb.getId().toString().substring(0, 8);
            messageUtil.sendBombListItemMessage(sender.source(),
                shortId,
                bomb.getActivator().getName(),
                bomb.getLocation().getBlockX(),
                bomb.getLocation().getBlockY(),
                bomb.getLocation().getBlockZ());
        }
    }

    @Command("cancel <bombId>")
    @Permission("falloutcore.bomb.admin.cancel")
    public void handleCancelBomb(Source sender, @Argument("bombId") String bombIdStr) {
        try {
            UUID bombId = UUID.fromString(bombIdStr);

            if (bombService.cancelBomb(bombId)) {
                messageUtil.sendBombCancelledMessage(sender.source());
            } else {
                messageUtil.sendNoBombFoundMessage(sender.source());
            }

        } catch (IllegalArgumentException e) {
            messageUtil.sendInvalidBombIdMessage(sender.source());
        }
    }

    @Command("force <bombId>")
    @Permission("falloutcore.bomb.admin.force")
    public void handleForceBomb(Source sender, @Argument("bombId") String bombIdStr) {
        try {
            UUID bombId = UUID.fromString(bombIdStr);

            if (bombService.forceExplodeBomb(bombId)) {
                messageUtil.sendBombForcedMessage(sender.source());
            } else {
                messageUtil.sendNoBombFoundMessage(sender.source());
            }

        } catch (IllegalArgumentException e) {
            messageUtil.sendInvalidBombIdMessage(sender.source());
        }
    }

    @Command("cooldown [player]")
    @Permission("falloutcore.bomb.admin")
    public void handleCheckCooldown(Source sender, @Argument("player") Player target) {
        Player targetPlayer = target != null ? target : (Player) sender.source();

        long cooldownSeconds = bombService.getPlayerCooldown(targetPlayer);

        if (cooldownSeconds <= 0) {
            messageUtil.sendCooldownInfoMessage(sender.source(), targetPlayer.getName());
        } else {
            messageUtil.sendCooldownExpiredMessage(sender.source(), targetPlayer.getName(), cooldownSeconds);
        }
    }

    @Command("info")
    @Permission("falloutcore.bomb.info")
    public void handleBombInfo(Source sender) {
        var bombConfig = configService.getConfig().bomb();
        var nuclearConfig = bombConfig.nuclear();

        String radiationStatus = nuclearConfig.postRadiation().enabled() ?
            "Sí (" + nuclearConfig.postRadiation().durationMinutes() + " minutos)" : "No";

        String mushroomStatus = nuclearConfig.mushroom().enabled() ? "Sí" : "No";

        messageUtil.sendBombInfoMessage(sender.source(),
            nuclearConfig.maxRadius(),
            nuclearConfig.craterDepth(),
            nuclearConfig.explosionDurationSeconds(),
            nuclearConfig.cooldownSeconds(),
            radiationStatus,
            mushroomStatus);
    }
}
