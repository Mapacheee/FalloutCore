package me.mapacheee.falloutcore.factions.command;

import com.google.inject.Inject;
import com.thewinterframework.command.CommandComponent;
import com.thewinterframework.configurate.Container;
import me.mapacheee.falloutcore.factions.entity.Faction;
import me.mapacheee.falloutcore.factions.entity.FactionService;
import me.mapacheee.falloutcore.shared.config.Config;
import me.mapacheee.falloutcore.shared.config.Messages;
import me.mapacheee.falloutcore.shared.util.MessageUtil;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.paper.util.sender.Source;
import org.slf4j.Logger;

@CommandComponent
@Command("faction|f")
@Permission("falloutcore.faction.use")
public final class FactionCommand {

    private final Logger logger;
    private final FactionService factionService;
    private final MessageUtil messageUtil;
    private final Container<Config> configContainer;
    private final Container<Messages> messagesContainer;

    @Inject
    public FactionCommand(Logger logger, FactionService factionService, MessageUtil messageUtil,
                         Container<Config> configContainer, Container<Messages> messagesContainer) {
        this.logger = logger;
        this.factionService = factionService;
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

    @Command("create <name> <alias>")
    public void handleCreateFaction(Source sender, @Argument("name") String name, @Argument("alias") String alias) {
        if (!(sender.source() instanceof Player player)) {
            messageUtil.sendMessage(sender.source(), messages().general().playersOnly());
            return;
        }

        if (config().faction().adminOnlyCreate() && !player.hasPermission("falloutcore.faction.admin")) {
            messageUtil.sendNoPermissionMessage(player);
            return;
        }

        if (name.length() > config().faction().maxNameLength()) {
            messageUtil.sendFactionNameTooLongMessage(player, config().faction().maxNameLength());
            return;
        }

        if (alias.length() > config().faction().maxAliasLength()) {
            messageUtil.sendFactionAliasTooLongMessage(player, config().faction().maxAliasLength());
            return;
        }

        if (factionService.createFaction(name, alias)) {
            messageUtil.sendFactionCreatedMessage(sender.source(), name);
            logger.info("Faction '{}' created by {}", name, sender.source().getName());
        } else {
            if (factionService.factionExists(name)) {
                messageUtil.sendFactionAlreadyExistsMessage(sender.source(), name);
            } else {
                messageUtil.sendMaxFactionsReachedMessage(sender.source(), config().faction().maxFactions());
            }
        }
    }

    @Command("delete <name>")
    @Permission("falloutcore.faction.admin")
    public void handleDeleteFaction(Source sender, @Argument("name") String name) {
        if (factionService.deleteFaction(name)) {
            messageUtil.sendFactionDeletedMessage(sender.source(), name);
            logger.info("Faction '{}' deleted by {}", name, sender.source().getName());
        } else {
            messageUtil.sendFactionNotFoundMessage(sender.source(), name);
        }
    }

    @Command("join <faction>")
    public void handleJoinFaction(Source sender, @Argument("faction") String factionName) {
        if (!(sender.source() instanceof Player player)) {
            messageUtil.sendMessage(sender.source(), messages().general().playersOnly());
            return;
        }

        if (factionService.getPlayerFaction(player) != null) {
            messageUtil.sendAlreadyInFactionMessage(sender.source());
            return;
        }

        if (factionService.joinFaction(player, factionName)) {
            messageUtil.sendPlayerJoinedFactionMessage(sender.source(), player.getName(), factionName);
            logger.info("Player '{}' joined faction '{}'", player.getName(), factionName);
        } else {
            if (!factionService.factionExists(factionName)) {
                messageUtil.sendFactionNotFoundMessage(sender.source(), factionName);
            } else {
                messageUtil.sendFactionFullMessage(sender.source());
            }
        }
    }

    @Command("forcejoin <player> <faction>")
    @Permission("falloutcore.faction.admin")
    public void handleForceJoinFaction(Source sender, @Argument("player") Player target, @Argument("faction") String factionName) {
        if (factionService.forceJoinFaction(target, factionName)) {
            messageUtil.sendPlayerForceJoinedMessage(sender.source(), target.getName(), factionName);
            messageUtil.sendForceJoinedNotificationMessage(target, factionName);
            logger.info("Player '{}' was force-joined to faction '{}' by {}", target.getName(), factionName, sender.source().getName());
        } else {
            messageUtil.sendFactionNotFoundMessage(sender.source(), factionName);
        }
    }

    @Command("kick <player>")
    @Permission("falloutcore.faction.admin")
    public void handleKickPlayer(Source sender, @Argument("player") Player target) {
        Faction faction = factionService.getPlayerFaction(target);
        if (faction == null) {
            messageUtil.sendPlayerNotInFactionMessage(sender.source(), target.getName());
            return;
        }

        if (factionService.kickPlayer(target)) {
            messageUtil.sendPlayerKickedMessage(sender.source(), target.getName(), faction.getName());
            messageUtil.sendKickedNotificationMessage(target, faction.getName());
            logger.info("Player '{}' was kicked from faction '{}' by {}", target.getName(), faction.getName(), sender.source().getName());
        }
    }

    @Command("base")
    public void handleFactionBase(Source sender) {
        Player player = validatePlayerSender(sender);
        if (player == null) return;

        Faction faction = validatePlayerFaction(sender, player);
        if (faction == null) return;

        if (!faction.hasBase()) {
            messageUtil.sendBaseNotSetMessage(sender.source());
            return;
        }

        player.teleport(faction.getBaseLocation());
        messageUtil.sendBaseTeleportedMessage(sender.source());
        logger.info("Player '{}' teleported to faction '{}' base", player.getName(), faction.getName());
    }

    @Command("setbase")
    @Permission("falloutcore.faction.admin")
    public void handleSetFactionBase(Source sender) {
        Player player = validatePlayerSender(sender);
        if (player == null) return;

        Faction faction = validatePlayerFaction(sender, player);
        if (faction == null) return;

        factionService.setFactionBase(player, player.getLocation());
        messageUtil.sendBaseSetMessage(sender.source());
        logger.info("Player '{}' set base for faction '{}'", player.getName(), faction.getName());
    }

    @Command("setbase <faction>")
    @Permission("falloutcore.faction.admin")
    public void handleSetFactionBaseAdmin(Source sender, @Argument("faction") String factionName) {
        Player player = validatePlayerSender(sender);
        if (player == null) return;

        if (factionService.setFactionBaseByName(factionName, player.getLocation())) {
            messageUtil.sendBaseSetOtherMessage(sender.source(), factionName);
            logger.info("Admin '{}' set base for faction '{}'", player.getName(), factionName);
        } else {
            messageUtil.sendFactionNotFoundMessage(sender.source(), factionName);
        }
    }

    @Command("info")
    public void handleFactionInfo(Source sender) {
        Player player = validatePlayerSender(sender);
        if (player == null) return;

        Faction faction = validatePlayerFaction(sender, player);
        if (faction == null) return;

        messageUtil.sendFactionInfoMessage(sender.source(),
            faction.getName(),
            faction.getAlias(),
            faction.getMembers().size(),
            "No establecida");
    }

    @Command("list")
    @Permission("falloutcore.faction.admin")
    public void handleFactionList(Source sender) {
        var factions = factionService.getFactions();
        if (factions.isEmpty()) {
            messageUtil.sendNoFactionsExistMessage(sender.source());
            return;
        }

        messageUtil.sendFactionListHeaderMessage(sender.source());
        for (Faction faction : factions.values()) {
            messageUtil.sendFactionListItemMessage(sender.source(),
                faction.getName(),
                faction.getAlias(),
                faction.getMembers().size());
        }
    }

    @Command("leave")
    public void handleLeaveFaction(Source sender) {
        Player player = validatePlayerSender(sender);
        if (player == null) return;

        Faction faction = validatePlayerFaction(sender, player);
        if (faction == null) return;

        if (factionService.kickPlayer(player)) {
            messageUtil.sendPlayerLeftFactionMessage(sender.source(), player.getName(), faction.getName());
        }
    }
    private Player validatePlayerSender(Source sender) {
        if (!(sender.source() instanceof Player player)) {
            messageUtil.sendMessage(sender.source(), messages().general().playersOnly());
            return null;
        }
        return player;
    }

    private Faction validatePlayerFaction(Source sender, Player player) {
        Faction faction = factionService.getPlayerFaction(player);
        if (faction == null) {
            messageUtil.sendNotInFactionMessage(sender.source());
            return null;
        }
        return faction;
    }
}
