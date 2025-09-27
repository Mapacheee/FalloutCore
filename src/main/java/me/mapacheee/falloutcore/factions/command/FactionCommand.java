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
    private final Container<Config> config;
    private final Container<Messages> messages;

    @Inject
    public FactionCommand(Logger logger, FactionService factionService, MessageUtil messageUtil,
                         Container<Config> config, Container<Messages> messages) {
        this.logger = logger;
        this.factionService = factionService;
        this.messageUtil = messageUtil;
        this.config = config;
        this.messages = messages;
    }

    @Command("create <name> <alias>")
    @Permission("falloutcore.faction.admin")
    public void handleCreateFaction(Source sender, @Argument("name") String name, @Argument("alias") String alias) {
        if (config.get().faction().adminOnlyCreate() && !sender.source().hasPermission("falloutcore.faction.admin")) {
            messageUtil.sendMessage(sender.source(), messages.get().factions().noPermission());
            return;
        }

        if (name.length() > config.get().faction().maxNameLength()) {
            messageUtil.sendMessage(sender.source(), messages.get().factions().nameToolong()
                .replace("{max}", String.valueOf(config.get().faction().maxNameLength())));
            return;
        }

        if (alias.length() > config.get().faction().maxAliasLength()) {
            messageUtil.sendMessage(sender.source(), messages.get().factions().aliasToolong()
                .replace("{max}", String.valueOf(config.get().faction().maxAliasLength())));
            return;
        }

        if (factionService.createFaction(name, alias)) {
            messageUtil.sendMessage(sender.source(), messages.get().factions().factionCreated()
                .replace("{name}", name)
                .replace("{alias}", alias));
            logger.info("Faction '{}' created by {}", name, sender.source().getName());
        } else {
            if (factionService.factionExists(name)) {
                messageUtil.sendMessage(sender.source(), messages.get().factions().factionAlreadyExists()
                    .replace("{name}", name));
            } else {
                messageUtil.sendMessage(sender.source(), messages.get().factions().maxFactionsReached()
                    .replace("{max}", String.valueOf(config.get().faction().maxFactions())));
            }
        }
    }

    @Command("delete <name>")
    @Permission("falloutcore.faction.admin")
    public void handleDeleteFaction(Source sender, @Argument("name") String name) {
        if (factionService.deleteFaction(name)) {
            messageUtil.sendMessage(sender.source(), messages.get().factions().factionDeleted()
                .replace("{name}", name));
            logger.info("Faction '{}' deleted by {}", name, sender.source().getName());
        } else {
            messageUtil.sendMessage(sender.source(), messages.get().factions().factionNotFound()
                .replace("{name}", name));
        }
    }

    @Command("join <faction>")
    public void handleJoinFaction(Source sender, @Argument("faction") String factionName) {
        if (!(sender.source() instanceof Player player)) {
            messageUtil.sendMessage(sender.source(), messages.get().factions().playersOnly());
            return;
        }

        if (factionService.getPlayerFaction(player) != null) {
            messageUtil.sendMessage(sender.source(), messages.get().factions().alreadyInFaction());
            return;
        }

        if (factionService.joinFaction(player, factionName)) {
            messageUtil.sendMessage(sender.source(), messages.get().factions().joinedFaction()
                .replace("{faction}", factionName));
            logger.info("Player '{}' joined faction '{}'", player.getName(), factionName);
        } else {
            if (!factionService.factionExists(factionName)) {
                messageUtil.sendMessage(sender.source(), messages.get().factions().factionNotFound()
                    .replace("{name}", factionName));
            } else {
                messageUtil.sendMessage(sender.source(), messages.get().factions().factionFull()
                    .replace("{faction}", factionName));
            }
        }
    }

    @Command("forcejoin <player> <faction>")
    @Permission("falloutcore.faction.admin")
    public void handleForceJoinFaction(Source sender, @Argument("player") Player target, @Argument("faction") String factionName) {
        if (factionService.forceJoinFaction(target, factionName)) {
            messageUtil.sendMessage(sender.source(), messages.get().factions().playerForceJoined()
                .replace("{player}", target.getName())
                .replace("{faction}", factionName));
            messageUtil.sendMessage(target, messages.get().factions().forceJoinedNotification()
                .replace("{faction}", factionName));
            logger.info("Player '{}' was force-joined to faction '{}' by {}", target.getName(), factionName, sender.source().getName());
        } else {
            messageUtil.sendMessage(sender.source(), messages.get().factions().factionNotFound()
                .replace("{name}", factionName));
        }
    }

    @Command("kick <player>")
    @Permission("falloutcore.faction.admin")
    public void handleKickPlayer(Source sender, @Argument("player") Player target) {
        Faction faction = factionService.getPlayerFaction(target);
        if (faction == null) {
            messageUtil.sendMessage(sender.source(), messages.get().factions().playerNotInFaction()
                .replace("{player}", target.getName()));
            return;
        }

        if (factionService.kickPlayer(target)) {
            messageUtil.sendMessage(sender.source(), messages.get().factions().playerKicked()
                .replace("{player}", target.getName())
                .replace("{faction}", faction.getName()));
            messageUtil.sendMessage(target, messages.get().factions().kickedNotification()
                .replace("{faction}", faction.getName()));
            logger.info("Player '{}' was kicked from faction '{}' by {}", target.getName(), faction.getName(), sender.source().getName());
        }
    }

    @Command("setalias <faction> <alias>")
    @Permission("falloutcore.faction.admin")
    public void handleSetFactionAlias(Source sender, @Argument("faction") String factionName, @Argument("alias") String newAlias) {
        if (newAlias.length() > config.get().faction().maxAliasLength()) {
            messageUtil.sendMessage(sender.source(), messages.get().factions().aliasToolong()
                .replace("{max}", String.valueOf(config.get().faction().maxAliasLength())));
            return;
        }

        if (factionService.setFactionAlias(factionName, newAlias)) {
            messageUtil.sendMessage(sender.source(), messages.get().factions().aliasChanged()
                .replace("{faction}", factionName)
                .replace("{alias}", newAlias));
            logger.info("Faction '{}' alias changed to '{}' by {}", factionName, newAlias, sender.source().getName());
        } else {
            messageUtil.sendMessage(sender.source(), messages.get().factions().factionNotFound()
                .replace("{name}", factionName));
        }
    }

    @Command("info")
    public void handleFactionInfo(Source sender) {
        if (!(sender.source() instanceof Player player)) {
            messageUtil.sendMessage(sender.source(), messages.get().factions().playersOnly());
            return;
        }

        Faction faction = factionService.getPlayerFaction(player);
        if (faction == null) {
            messageUtil.sendMessage(sender.source(), messages.get().factions().notInFaction());
            return;
        }

        // Enviar información de la facción usando mensajes del config
        String[] infoLines = messages.get().factions().factionInfo().split("\n");
        for (String line : infoLines) {
            messageUtil.sendMessage(sender.source(), line
                .replace("{name}", faction.getName())
                .replace("{alias}", faction.getAlias())
                .replace("{members}", String.valueOf(faction.getMembers().size()))
                .replace("{maxMembers}", String.valueOf(config.get().faction().maxMembersPerFaction()))
                .replace("{points}", String.valueOf(faction.getNexusPoints())));
        }
    }

    @Command("list")
    @Permission("falloutcore.faction.admin")
    public void handleFactionList(Source sender) {
        var factions = factionService.getFactions();
        if (factions.isEmpty()) {
            messageUtil.sendMessage(sender.source(), messages.get().factions().noFactionsExist());
            return;
        }

        messageUtil.sendMessage(sender.source(), messages.get().factions().factionListHeader());
        for (Faction faction : factions.values()) {
            messageUtil.sendMessage(sender.source(), messages.get().factions().factionListItem()
                .replace("{name}", faction.getName())
                .replace("{alias}", faction.getAlias())
                .replace("{members}", String.valueOf(faction.getMembers().size())));
        }
    }

    @Command("leave")
    public void handleLeaveFaction(Source sender) {
        if (!(sender.source() instanceof Player player)) {
            messageUtil.sendMessage(sender.source(), messages.get().factions().playersOnly());
            return;
        }

        Faction faction = factionService.getPlayerFaction(player);
        if (faction == null) {
            messageUtil.sendMessage(sender.source(), messages.get().factions().notInFaction());
            return;
        }

        if (factionService.kickPlayer(player)) {
            messageUtil.sendMessage(sender.source(), messages.get().factions().leftFaction()
                .replace("{faction}", faction.getName()));
            logger.info("Player '{}' left faction '{}'", player.getName(), faction.getName());
        }
    }
}
