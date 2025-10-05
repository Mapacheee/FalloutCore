package me.mapacheee.falloutcore.shared.util;

import com.google.inject.Inject;
import com.thewinterframework.service.annotation.Service;
import me.mapacheee.falloutcore.shared.config.ConfigService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.List;


@Service
public class MessageUtil {

    private final ConfigService configService;

    @Inject
    public MessageUtil(ConfigService configService) {
        this.configService = configService;
    }

    public void sendMessage(CommandSender sender, String message) {
        String prefixed = configService.getMessages().general().prefix() + " " + message;
        Component component = colorizeToComponent(prefixed);
        sender.sendMessage(component);
    }

    public void sendMessage(Player player, String message) {
        sendMessage((CommandSender) player, message);
    }

    public void sendTitle(Player player, String title, String subtitle) {
        Component titleComponent = colorizeToComponent(title);
        Component subtitleComponent = colorizeToComponent(subtitle);

        Title adventureTitle = Title.title(
                titleComponent,
                subtitleComponent,
                Title.Times.times(
                        Duration.ofMillis(500),
                        Duration.ofSeconds(1),
                        Duration.ofMillis(500)
                )
        );

        player.showTitle(adventureTitle);
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

    public void sendReloadSuccessMessage(Player player, long duration) {
        String message = configService.getMessages().general().reloadComplete()
                .replace("<duration>", String.valueOf(duration));
        sendMessage(player, message);
    }

    public void sendReloadErrorMessage(Player player) {
        String message = "&cError al recargar las configuraciones. Revisa la consola para más detalles.";
        sendMessage(player, message);
    }

    public void sendVersionMessage(Player player) {
        String message = "&6FalloutCore &ev" + getVersion() + " &7- Plugin de facciones y radiación";
        sendMessage(player, message);
    }

    private String getVersion() {
        try {
            return getClass().getPackage().getImplementationVersion() != null ?
                    getClass().getPackage().getImplementationVersion() : "1.0-SNAPSHOT";
        } catch (Exception e) {
            return "1.0";
        }
    }

    public void sendBombModuleDisabledMessage(CommandSender sender) {
        String message = configService.getMessages().bombs().moduleDisabled();
        sendMessage(sender, message);
    }

    public void sendBombGivenMessage(Player player) {
        String message = configService.getMessages().bombs().bombGiven();
        sendMessage(player, message);
    }

    public void sendBombActivatedMessage(Player player) {
        String message = configService.getMessages().bombs().bombActivated();
        sendMessage(player, message);
    }

    public void sendBombActivationFailedMessage(Player player) {
        String message = configService.getMessages().bombs().bombActivationFailed();
        sendMessage(player, message);
    }

    public void sendBombConfirmationMessage(Player player) {
        String message = configService.getMessages().bombs().bombConfirmation();
        sendMessage(player, message);
    }

    public void sendBombCooldownMessage(Player player, long seconds) {
        String message = configService.getMessages().bombs().bombCooldown()
                .replace("<seconds>", String.valueOf(seconds));
        sendMessage(player, message);
    }

    public void sendBombProtectedAreaMessage(Player player) {
        String message = configService.getMessages().bombs().bombProtectedArea();
        sendMessage(player, message);
    }

    public void sendBombTimerTitle(Player player, int seconds, String location) {
        String title = configService.getMessages().bombs().timerTitle()
                .replace("<seconds>", String.valueOf(seconds))
                .replace("<location>", location);
        sendTitle(player, title, "");
    }

    public void sendBombTimerSubtitle(Player player, int seconds, String activator) {
        String subtitle = configService.getMessages().bombs().timerSubtitle()
                .replace("<seconds>", String.valueOf(seconds))
                .replace("<activator>", activator);
        sendTitle(player, "", subtitle);
    }

    public void sendExplosionWarningMessage(Player player) {
        String message = configService.getMessages().bombs().explosionWarning();
        sendMessage(player, message);
    }

    public void sendRadiationZoneCreatedMessage(Player player, int radius, int duration) {
        String message = configService.getMessages().bombs().radiationZoneCreated()
                .replace("<radius>", String.valueOf(radius))
                .replace("<duration>", String.valueOf(duration));
        sendMessage(player, message);
    }

    public ItemStack createBombItem() {
        ItemStack bombItem = new ItemStack(org.bukkit.Material.PLAYER_HEAD);
        org.bukkit.inventory.meta.SkullMeta meta = (org.bukkit.inventory.meta.SkullMeta) bombItem.getItemMeta();

        if (meta != null) {
            Component displayName = colorizeToComponent(configService.getMessages().bombs().bombItemName());
            meta.displayName(displayName);

            String loreText = configService.getMessages().bombs().bombItemLore();
            List<Component> loreComponents = java.util.Arrays.stream(loreText.split("\\n"))
                    .map(this::colorizeToComponent)
                    .collect(java.util.stream.Collectors.toList());
            meta.lore(loreComponents);

            bombItem.setItemMeta(meta);
        }

        return bombItem;
    }

    private String colorize(String message) {
        if (message == null) return "";

        message = message.replaceAll("&#([A-Fa-f0-9]{6})", "<#$1>");
        return net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()
                .serialize(
                        net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand()
                                .deserialize(message)
                );
    }

    private Component colorizeToComponent(String message) {
        if (message == null) return Component.empty();

        message = message.replaceAll("&#([A-Fa-f0-9]{6})", "<#$1>");
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    public void sendBombCancelledMessage(CommandSender sender) {
        String message = configService.getMessages().bombs().bombCancelled();
        sendMessage(sender, message);
    }

    public void sendBombForcedMessage(CommandSender sender) {
        String message = configService.getMessages().bombs().bombForced();
        sendMessage(sender, message);
    }

    public void sendNoBombFoundMessage(CommandSender sender) {
        String message = configService.getMessages().bombs().noBombFound();
        sendMessage(sender, message);
    }

    public void sendInvalidBombIdMessage(CommandSender sender) {
        String message = configService.getMessages().bombs().invalidBombId();
        sendMessage(sender, message);
    }

    public void sendBombListHeaderMessage(CommandSender sender) {
        String message = configService.getMessages().bombs().bombListHeader();
        sendMessage(sender, message);
    }

    public void sendBombListEmptyMessage(CommandSender sender) {
        String message = configService.getMessages().bombs().bombListEmpty();
        sendMessage(sender, message);
    }

    public void sendCooldownInfoMessage(CommandSender sender, String playerName) {
        String message = configService.getMessages().bombs().cooldownInfo()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    public void sendCooldownExpiredMessage(CommandSender sender, String playerName, long seconds) {
        String message = configService.getMessages().bombs().cooldownExpired()
                .replace("<player>", playerName)
                .replace("<seconds>", String.valueOf(seconds));
        sendMessage(sender, message);
    }

    public void sendBombActivatedAtLocationMessage(CommandSender sender, int x, int y, int z) {
        String message = configService.getMessages().bombs().bombActivated()
                .replace("<x>", String.valueOf(x))
                .replace("<y>", String.valueOf(y))
                .replace("<z>", String.valueOf(z));
        sendMessage(sender, message);
    }

    public void sendBombGivenToPlayerMessage(CommandSender sender, String playerName) {
        String message = configService.getMessages().bombs().bombGivenToPlayer()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }


    public void sendBombInfoMessage(CommandSender sender, int radius, int depth, int duration, int cooldown, String radiationStatus, String mushroomStatus) {
        String message = configService.getMessages().bombs().bombInfo()
                .replace("<radius>", String.valueOf(radius))
                .replace("<depth>", String.valueOf(depth))
                .replace("<duration>", String.valueOf(duration))
                .replace("<cooldown>", String.valueOf(cooldown))
                .replace("<radiationStatus>", radiationStatus)
                .replace("<mushroomStatus>", mushroomStatus);
        sendMessage(sender, message);
    }

    public void sendBombListItemMessage(CommandSender sender, String id, String activator, int x, int y, int z) {
        String message = configService.getMessages().bombs().bombListItem()
                .replace("<id>", id)
                .replace("<activator>", activator)
                .replace("<x>", String.valueOf(x))
                .replace("<y>", String.valueOf(y))
                .replace("<z>", String.valueOf(z));
        sendMessage(sender, message);
    }

    public void sendPlayersOnlyCommandMessage(CommandSender sender) {
        String message = configService.getMessages().bombs().playersOnlyCommand();
        sendMessage(sender, message);
    }
}
