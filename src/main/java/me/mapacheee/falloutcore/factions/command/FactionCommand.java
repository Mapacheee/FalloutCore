package me.mapacheee.falloutcore.factions.command;

import com.google.inject.Inject;
import com.thewinterframework.command.CommandComponent;
import me.mapacheee.falloutcore.factions.entity.Faction;
import me.mapacheee.falloutcore.factions.entity.FactionService;
import me.mapacheee.falloutcore.shared.config.ConfigService;
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
    private final ConfigService configService;

    @Inject
    public FactionCommand(Logger logger, FactionService factionService, MessageUtil messageUtil,
                         ConfigService configService) {
        this.logger = logger;
        this.factionService = factionService;
        this.messageUtil = messageUtil;
        this.configService = configService;
    }

    @Command("create <name> <alias>")
    @Permission("falloutcore.faction.admin")
    public void handleCreateFaction(Source sender, @Argument("name") String name, @Argument("alias") String alias) {
        if (configService.getConfig().faction().adminOnlyCreate() && !sender.source().hasPermission("falloutcore.faction.admin")) {
            messageUtil.sendFactionMessage(sender.source(), "noPermission");
            return;
        }

        if (name.length() > configService.getConfig().faction().maxNameLength()) {
            messageUtil.sendFactionMessage(sender.source(), "nameToolong", "max", String.valueOf(configService.getConfig().faction().maxNameLength()));
            return;
        }

        if (alias.length() > configService.getConfig().faction().maxAliasLength()) {
            messageUtil.sendFactionMessage(sender.source(), "aliasToolong", "max", String.valueOf(configService.getConfig().faction().maxAliasLength()));
            return;
        }

        if (factionService.createFaction(name, alias)) {
            messageUtil.sendFactionMessage(sender.source(), "factionCreated", "faction", name);
            logger.info("Faction '{}' created by {}", name, sender.source().getName());
        } else {
            if (factionService.factionExists(name)) {
                messageUtil.sendFactionMessage(sender.source(), "factionAlreadyExists", "faction", name);
            } else {
                messageUtil.sendFactionMessage(sender.source(), "maxFactionsReached", "max", String.valueOf(configService.getConfig().faction().maxFactions()));
            }
        }
    }

    @Command("delete <name>")
    @Permission("falloutcore.faction.admin")
    public void handleDeleteFaction(Source sender, @Argument("name") String name) {
        if (factionService.deleteFaction(name)) {
            messageUtil.sendFactionMessage(sender.source(), "factionDeleted", "faction", name);
            logger.info("Faction '{}' deleted by {}", name, sender.source().getName());
        } else {
            messageUtil.sendFactionMessage(sender.source(), "factionNotFound", "faction", name);
        }
    }

    @Command("join <faction>")
    public void handleJoinFaction(Source sender, @Argument("faction") String factionName) {
        if (!(sender.source() instanceof Player player)) {
            messageUtil.sendMessage(sender.source(), configService.getMessages().general().playersOnly());
            return;
        }

        if (factionService.getPlayerFaction(player) != null) {
            messageUtil.sendFactionMessage(sender.source(), "alreadyInFaction");
            return;
        }

        if (factionService.joinFaction(player, factionName)) {
            messageUtil.sendFactionMessage(sender.source(), "playerJoined", "player", player.getName(), "faction", factionName);
            logger.info("Player '{}' joined faction '{}'", player.getName(), factionName);
        } else {
            if (!factionService.factionExists(factionName)) {
                messageUtil.sendFactionMessage(sender.source(), "factionNotFound", "faction", factionName);
            } else {
                messageUtil.sendFactionMessage(sender.source(), "factionFull");
            }
        }
    }

    @Command("forcejoin <player> <faction>")
    @Permission("falloutcore.faction.admin")
    public void handleForceJoinFaction(Source sender, @Argument("player") Player target, @Argument("faction") String factionName) {
        if (factionService.forceJoinFaction(target, factionName)) {
            messageUtil.sendFactionMessage(sender.source(), "playerForceJoined", "player", target.getName(), "faction", factionName);
            messageUtil.sendFactionMessage(target, "forceJoinedNotification", "faction", factionName);
            logger.info("Player '{}' was force-joined to faction '{}' by {}", target.getName(), factionName, sender.source().getName());
        } else {
            messageUtil.sendFactionMessage(sender.source(), "factionNotFound", "faction", factionName);
        }
    }

    @Command("kick <player>")
    @Permission("falloutcore.faction.admin")
    public void handleKickPlayer(Source sender, @Argument("player") Player target) {
        Faction faction = factionService.getPlayerFaction(target);
        if (faction == null) {
            messageUtil.sendFactionMessage(sender.source(), "playerNotInFaction", "player", target.getName());
            return;
        }

        if (factionService.kickPlayer(target)) {
            messageUtil.sendFactionMessage(sender.source(), "playerKicked", "player", target.getName(), "faction", faction.getName());
            messageUtil.sendFactionMessage(target, "kickedNotification", "faction", faction.getName());
            logger.info("Player '{}' was kicked from faction '{}' by {}", target.getName(), faction.getName(), sender.source().getName());
        }
    }

    @Command("setalias <faction> <alias>")
    @Permission("falloutcore.faction.admin")
    public void handleSetFactionAlias(Source sender, @Argument("faction") String factionName, @Argument("alias") String newAlias) {
        if (newAlias.length() > configService.getConfig().faction().maxAliasLength()) {
            messageUtil.sendFactionMessage(sender.source(), "aliasToolong", "max", String.valueOf(configService.getConfig().faction().maxAliasLength()));
            return;
        }

        if (factionService.setFactionAlias(factionName, newAlias)) {
            messageUtil.sendFactionMessage(sender.source(), "aliasChanged", "faction", factionName, "alias", newAlias);
            logger.info("Faction '{}' alias changed to '{}' by {}", factionName, newAlias, sender.source().getName());
        } else {
            messageUtil.sendFactionMessage(sender.source(), "factionNotFound", "faction", factionName);
        }
    }

    @Command("info")
    public void handleFactionInfo(Source sender) {
        if (!(sender.source() instanceof Player player)) {
            messageUtil.sendMessage(sender.source(), configService.getMessages().general().playersOnly());
            return;
        }

        Faction faction = factionService.getPlayerFaction(player);
        if (faction == null) {
            messageUtil.sendFactionMessage(sender.source(), "notInFaction");
            return;
        }

        messageUtil.sendFactionMessage(sender.source(), "factionInfo",
            "faction", faction.getName(),
            "alias", faction.getAlias(),
            "members", String.valueOf(faction.getMembers().size()),
            "base", "No establecida");
    }

    @Command("list")
    @Permission("falloutcore.faction.admin")
    public void handleFactionList(Source sender) {
        var factions = factionService.getFactions();
        if (factions.isEmpty()) {
            messageUtil.sendFactionMessage(sender.source(), "noFactionsExist");
            return;
        }

        messageUtil.sendFactionMessage(sender.source(), "factionListHeader");
        for (Faction faction : factions.values()) {
            messageUtil.sendFactionMessage(sender.source(), "factionListItem",
                "name", faction.getName(),
                "alias", faction.getAlias(),
                "members", String.valueOf(faction.getMembers().size()));
        }
    }

    @Command("leave")
    public void handleLeaveFaction(Source sender) {
        if (!(sender.source() instanceof Player player)) {
            messageUtil.sendMessage(sender.source(), configService.getMessages().general().playersOnly());
            return;
        }

        Faction faction = factionService.getPlayerFaction(player);
        if (faction == null) {
            messageUtil.sendFactionMessage(sender.source(), "notInFaction");
            return;
        }

        if (factionService.kickPlayer(player)) {
            messageUtil.sendFactionMessage(sender.source(), "playerLeft", "player", player.getName(), "faction", faction.getName());
            logger.info("Player '{}' left faction '{}'", player.getName(), faction.getName());
        }
    }
}
