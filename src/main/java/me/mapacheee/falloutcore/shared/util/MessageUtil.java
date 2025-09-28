package me.mapacheee.falloutcore.shared.util;

import com.google.inject.Inject;
import com.thewinterframework.service.annotation.Service;
import me.mapacheee.falloutcore.config.Messages;
import me.mapacheee.falloutcore.shared.config.ConfigService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Service
public class MessageUtil {

    private final ConfigService configService;

    @Inject
    public MessageUtil(ConfigService configService) {
        this.configService = configService;
    }

    public void sendMessage(CommandSender sender, String message) {
        String prefixed = configService.getMessages().general().prefix() + " " + message;
        sender.sendMessage(colorize(prefixed));
    }

    public void sendMessage(Player player, String message) {
        sendMessage((CommandSender) player, message);
    }

    public void sendTitle(Player player, String title, String subtitle) {
        player.sendTitle(colorize(title), colorize(subtitle), 10, 20, 10);
    }

    public void sendRadiationEnterMessage(Player player, int level) {
        String message = configService.getMessages().radiation().enterRadiation()
                .replace("<level>", String.valueOf(level));
        sendMessage(player, message);
    }

    public void sendRadiationExitMessage(Player player) {
        String message = configService.getMessages().radiation().exitRadiation();
        sendMessage(player, message);
    }

    public void sendRadiationLevelChangedMessage(Player player, int level, int height, int oldLevel, int oldHeight) {
        String message = configService.getMessages().radiation().levelChanged()
                .replace("<level>", String.valueOf(level))
                .replace("<height>", String.valueOf(height))
                .replace("<oldLevel>", String.valueOf(oldLevel))
                .replace("<oldHeight>", String.valueOf(oldHeight));
        sendMessage(player, message);
    }

    public void sendRadiationArmorProtectionMessage(Player player) {
        String message = configService.getMessages().radiation().armorProtection();
        sendMessage(player, message);
    }

    public void sendRadiationDamageTitle(Player player, double damage, int level) {
        String title = configService.getMessages().radiation().radiationDamageTitle();
        String subtitle = configService.getMessages().radiation().radiationDamageSubtitle()
                .replace("<damage>", String.valueOf(damage))
                .replace("<level>", String.valueOf(level));
        sendTitle(player, title, subtitle);
    }

    public void sendRadiationArmorProtectionTitle(Player player, String armorType, int level) {
        String title = configService.getMessages().radiation().radiationArmorTitle();
        String subtitle = configService.getMessages().radiation().radiationArmorSubtitle()
                .replace("<armor>", armorType)
                .replace("<level>", String.valueOf(level));
        sendTitle(player, title, subtitle);
    }

    public void sendFactionCreatedMessage(CommandSender sender, String factionName) {
        String message = configService.getMessages().faction().factionCreated()
                .replace("<faction>", factionName);
        sendMessage(sender, message);
    }

    public void sendFactionDeletedMessage(CommandSender sender, String factionName) {
        String message = configService.getMessages().faction().factionDeleted()
                .replace("<faction>", factionName);
        sendMessage(sender, message);
    }

    public void sendPlayerJoinedFactionMessage(CommandSender sender, String playerName, String factionName) {
        String message = configService.getMessages().faction().playerJoined()
                .replace("<player>", playerName)
                .replace("<faction>", factionName);
        sendMessage(sender, message);
    }

    public void sendPlayerLeftFactionMessage(CommandSender sender, String playerName, String factionName) {
        String message = configService.getMessages().faction().playerLeft()
                .replace("<player>", playerName)
                .replace("<faction>", factionName);
        sendMessage(sender, message);
    }

    public void sendFactionNotFoundMessage(CommandSender sender, String factionName) {
        String message = configService.getMessages().faction().factionNotFound()
                .replace("<faction>", factionName);
        sendMessage(sender, message);
    }

    public void sendAlreadyInFactionMessage(CommandSender sender) {
        String message = configService.getMessages().faction().alreadyInFaction();
        sendMessage(sender, message);
    }

    public void sendNotInFactionMessage(CommandSender sender) {
        String message = configService.getMessages().faction().notInFaction();
        sendMessage(sender, message);
    }

    public void sendNoPermissionMessage(CommandSender sender) {
        String message = configService.getMessages().faction().noPermission();
        sendMessage(sender, message);
    }

    public void sendFactionInfoMessage(CommandSender sender, String factionName, String alias, int members, String base) {
        String message = configService.getMessages().faction().factionInfo()
                .replace("<faction>", factionName)
                .replace("<alias>", alias)
                .replace("<members>", String.valueOf(members))
                .replace("<base>", base);
        sendMessage(sender, message);
    }

    public void sendFactionAlreadyExistsMessage(CommandSender sender, String factionName) {
        String message = configService.getMessages().faction().factionAlreadyExists()
                .replace("<faction>", factionName);
        sendMessage(sender, message);
    }

    public void sendMaxFactionsReachedMessage(CommandSender sender, int maxFactions) {
        String message = configService.getMessages().faction().maxFactionsReached()
                .replace("<max>", String.valueOf(maxFactions));
        sendMessage(sender, message);
    }

    public void sendFactionFullMessage(CommandSender sender) {
        String message = configService.getMessages().faction().factionFull();
        sendMessage(sender, message);
    }

    public void sendFactionFriendlyFireMessage(CommandSender sender) {
        String message = configService.getMessages().faction().friendlyFire();
        sendMessage(sender, message);
    }

    public void sendRadiationSystemStatusMessage(Player player) {
        String message = configService.getMessages().radiation().systemStatus();
        sendMessage(player, message);
    }

    public void sendRadiationCurrentLevelMessage(Player player, int currentLevel, int maxLevel) {
        String message = configService.getMessages().radiation().currentLevel()
                .replace("<level>", String.valueOf(currentLevel))
                .replace("<maxLevel>", String.valueOf(maxLevel));
        sendMessage(player, message);
    }

    public void sendRadiationHeightMessage(Player player, int height) {
        String message = configService.getMessages().radiation().radiationHeight()
                .replace("<height>", String.valueOf(height));
        sendMessage(player, message);
    }

    public void sendRadiationPlayersCountMessage(Player player, int count) {
        String message = configService.getMessages().radiation().playersInRadiation()
                .replace("<count>", String.valueOf(count));
        sendMessage(player, message);
    }

    public void sendRadiationSystemStateMessage(Player player, String status) {
        String message = configService.getMessages().radiation().systemState()
                .replace("<status>", status);
        sendMessage(player, message);
    }

    public void sendRadiationLevelOutOfRangeMessage(Player player, int min, int max) {
        String message = configService.getMessages().radiation().levelOutOfRange()
                .replace("<min>", String.valueOf(min))
                .replace("<max>", String.valueOf(max));
        sendMessage(player, message);
    }

    public void sendRadiationLevelSetMessage(Player player, int level) {
        String message = configService.getMessages().radiation().levelSet()
                .replace("<level>", String.valueOf(level));
        sendMessage(player, message);
    }

    public void sendRadiationHeightOutOfRangeMessage(Player player) {
        String message = configService.getMessages().radiation().heightOutOfRange();
        sendMessage(player, message);
    }

    public void sendRadiationHeightSetMessage(Player player, int height) {
        String message = configService.getMessages().radiation().heightSet()
                .replace("<height>", String.valueOf(height));
        sendMessage(player, message);
    }

    public void sendRadiationSpecifyPlayerConsoleMessage(Player player) {
        String message = configService.getMessages().radiation().specifyPlayerConsole();
        sendMessage(player, message);
    }

    public void sendRadiationPlayerStatusHeaderMessage(Player player, String targetName) {
        String message = configService.getMessages().radiation().playerStatusHeader()
                .replace("<player>", targetName);
        sendMessage(player, message);
    }

    public void sendRadiationInRadiationStatusMessage(Player player, boolean inRadiation) {
        String status = inRadiation ?
            configService.getMessages().radiation().inRadiationYes() :
            configService.getMessages().radiation().inRadiationNo();
        String message = configService.getMessages().radiation().inRadiationStatus()
                .replace("<status>", status);
        sendMessage(player, message);
    }

    public void sendRadiationImmuneStatusMessage(Player player, boolean isImmune) {
        String status = isImmune ?
            configService.getMessages().radiation().immuneTrue() :
            configService.getMessages().radiation().immuneFalse();
        String message = configService.getMessages().radiation().immuneStatus()
                .replace("<status>", status);
        sendMessage(player, message);
    }

    public void sendRadiationArmorProtectionStatusMessage(Player player, String armorType, int level) {
        String message = configService.getMessages().radiation().armorProtectionStatus()
                .replace("<armor>", armorType)
                .replace("<level>", String.valueOf(level));
        sendMessage(player, message);
    }

    public void sendRadiationPlayerHeightStatusMessage(Player player, int height) {
        String message = configService.getMessages().radiation().playerHeightStatus()
                .replace("<height>", String.valueOf(height));
        sendMessage(player, message);
    }

    public void sendRadiationRadiationHeightStatusMessage(Player player, int height) {
        String message = configService.getMessages().radiation().radiationHeightStatus()
                .replace("<height>", String.valueOf(height));
        sendMessage(player, message);
    }

    public void sendRadiationPlayerImmuneMessage(Player player, String targetName) {
        String message = configService.getMessages().radiation().playerImmune()
                .replace("<player>", targetName);
        sendMessage(player, message);
    }

    public void sendRadiationPlayerNotImmuneMessage(Player player, String targetName) {
        String message = configService.getMessages().radiation().playerNotImmune()
                .replace("<player>", targetName);
        sendMessage(player, message);
    }

    public void sendRadiationImmunityInstructionsMessage(Player player, String targetName) {
        String message = configService.getMessages().radiation().immunityInstructions()
                .replace("<player>", targetName);
        sendMessage(player, message);
    }

    public void sendFactionNameTooLongMessage(CommandSender sender, int maxLength) {
        String message = configService.getMessages().faction().nameToolong()
                .replace("<max>", String.valueOf(maxLength));
        sendMessage(sender, message);
    }

    public void sendFactionAliasTooLongMessage(CommandSender sender, int maxLength) {
        String message = configService.getMessages().faction().aliasToolong()
                .replace("<max>", String.valueOf(maxLength));
        sendMessage(sender, message);
    }

    public void sendPlayerForceJoinedMessage(CommandSender sender, String playerName, String factionName) {
        String message = configService.getMessages().faction().playerForceJoined()
                .replace("<player>", playerName)
                .replace("<faction>", factionName);
        sendMessage(sender, message);
    }

    public void sendForceJoinedNotificationMessage(Player player, String factionName) {
        String message = configService.getMessages().faction().forceJoinedNotification()
                .replace("<faction>", factionName);
        sendMessage(player, message);
    }

    public void sendPlayerNotInFactionMessage(CommandSender sender, String playerName) {
        String message = configService.getMessages().faction().playerNotInFaction()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    public void sendPlayerKickedMessage(CommandSender sender, String playerName, String factionName) {
        String message = configService.getMessages().faction().playerKicked()
                .replace("<player>", playerName)
                .replace("<faction>", factionName);
        sendMessage(sender, message);
    }

    public void sendKickedNotificationMessage(Player player, String factionName) {
        String message = configService.getMessages().faction().kickedNotification()
                .replace("<faction>", factionName);
        sendMessage(player, message);
    }

    public void sendFactionAliasChangedMessage(CommandSender sender, String factionName, String newAlias) {
        String message = configService.getMessages().faction().aliasChanged()
                .replace("<faction>", factionName)
                .replace("<alias>", newAlias);
        sendMessage(sender, message);
    }

    public void sendNoFactionsExistMessage(CommandSender sender) {
        String message = configService.getMessages().faction().noFactionsExist();
        sendMessage(sender, message);
    }

    public void sendFactionListHeaderMessage(CommandSender sender) {
        String message = configService.getMessages().faction().factionListHeader();
        sendMessage(sender, message);
    }

    public void sendFactionListItemMessage(CommandSender sender, String name, String alias, int members) {
        String message = configService.getMessages().faction().factionListItem()
                .replace("<name>", name)
                .replace("<alias>", alias)
                .replace("<members>", String.valueOf(members));
        sendMessage(sender, message);
    }

    public void sendBaseSetMessage(CommandSender sender) {
        String message = configService.getMessages().faction().baseSet();
        sendMessage(sender, message);
    }

    public void sendBaseNotSetMessage(CommandSender sender) {
        String message = configService.getMessages().faction().baseNotSet();
        sendMessage(sender, message);
    }

    public void sendBaseTeleportedMessage(CommandSender sender) {
        String message = configService.getMessages().faction().baseTeleported();
        sendMessage(sender, message);
    }

    public void sendBaseSetOtherMessage(CommandSender sender, String factionName) {
        String message = configService.getMessages().faction().baseSetOther()
                .replace("<faction>", factionName);
        sendMessage(sender, message);
    }

    public void sendTpaRequestSentMessage(CommandSender sender, String playerName) {
        String message = configService.getMessages().faction().tpaRequestSent()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    public void sendTpaRequestReceivedMessage(CommandSender sender, String playerName) {
        String message = configService.getMessages().faction().tpaRequestReceived()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    public void sendTpaNoRequestsMessage(CommandSender sender) {
        String message = configService.getMessages().faction().tpaNoRequests();
        sendMessage(sender, message);
    }

    public void sendTpaRequestAcceptedMessage(CommandSender sender, String playerName) {
        String message = configService.getMessages().faction().tpaRequestAccepted()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    public void sendTpaRequestDeniedMessage(CommandSender sender, String playerName) {
        String message = configService.getMessages().faction().tpaRequestDenied()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    public void sendTpaRequestAcceptedSenderMessage(CommandSender sender, String playerName) {
        String message = configService.getMessages().faction().tpaRequestAcceptedSender()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    public void sendTpaRequestDeniedSenderMessage(CommandSender sender, String playerName) {
        String message = configService.getMessages().faction().tpaRequestDeniedSender()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    public void sendTpaRequestExpiredMessage(CommandSender sender, String playerName) {
        String message = configService.getMessages().faction().tpaRequestExpired()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    public void sendTpaNotSameFactionMessage(CommandSender sender) {
        String message = configService.getMessages().faction().tpaNotSameFaction();
        sendMessage(sender, message);
    }

    public void sendTpaSelfRequestMessage(CommandSender sender) {
        String message = configService.getMessages().faction().tpaSelfRequest();
        sendMessage(sender, message);
    }

    public void sendTpaPlayerOfflineMessage(CommandSender sender, String playerName) {
        String message = configService.getMessages().faction().tpaPlayerOffline()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    public void sendTpaAlreadyHasRequestMessage(CommandSender sender, String playerName) {
        String message = configService.getMessages().faction().tpaAlreadyHasRequest()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    private String colorize(String message) {
        return net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand()
                .serialize(
                    net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand()
                            .deserialize(message)
                );
    }

    public Messages getMessages() {
        return configService.getMessages();
    }
}
