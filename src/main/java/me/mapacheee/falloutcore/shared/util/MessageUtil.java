package me.mapacheee.falloutcore.shared.util;

import com.google.inject.Inject;
import com.thewinterframework.configurate.Container;
import com.thewinterframework.service.annotation.Service;
import me.mapacheee.falloutcore.shared.config.Config;
import me.mapacheee.falloutcore.shared.config.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageUtil {

    private final Container<Config> configContainer;
    private final Container<Messages> messagesContainer;

    @Inject
    public MessageUtil(Container<Config> configContainer, Container<Messages> messagesContainer) {
        this.configContainer = configContainer;
        this.messagesContainer = messagesContainer;
    }

    private Config config() {
        return configContainer.get();
    }

    private Messages messages() {
        return messagesContainer.get();
    }

    public void sendMessage(CommandSender sender, String message) {
        String prefixed = messages().general().prefix() + " " + message;
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

    public void sendRadiationEnterMessage(Player player, int radiationLevel) {
        String message = messages().radiation().enterRadiation();
        sendMessage(player, message);
    }

    public void sendRadiationExitMessage(Player player) {
        String message = messages().radiation().exitRadiation();
        sendMessage(player, message);
    }

    public void sendRadiationLevelChangedMessage(Player player, int level, int height, int oldLevel, int oldHeight) {
        String message = messages().radiation().levelChanged()
                .replace("<level>", String.valueOf(level))
                .replace("<height>", String.valueOf(height))
                .replace("<oldLevel>", String.valueOf(oldLevel))
                .replace("<oldHeight>", String.valueOf(oldHeight));
        sendMessage(player, message);
    }

    public void sendRadiationArmorProtectionMessage(Player player) {
        String message = messages().radiation().armorProtection();
        sendMessage(player, message);
    }

    public void sendRadiationDamageTitle(Player player, double damage, int level) {
        String title = messages().radiation().radiationDamageTitle();
        String subtitle = messages().radiation().radiationDamageSubtitle()
                .replace("<damage>", String.valueOf(damage))
                .replace("<level>", String.valueOf(level));
        sendTitle(player, title, subtitle);
    }

    public void sendRadiationArmorProtectionTitle(Player player, String armorType, int level) {
        String title = messages().radiation().radiationArmorTitle();
        String subtitle = messages().radiation().radiationArmorSubtitle()
                .replace("<armor>", armorType)
                .replace("<level>", String.valueOf(level));
        sendTitle(player, title, subtitle);
    }

    public void sendRadiationSystemStatusMessage(Player player) {
        String message = messages().radiation().systemStatus();
        sendMessage(player, message);
    }

    public void sendRadiationCurrentLevelMessage(Player player, int currentLevel, int maxLevel) {
        String message = messages().radiation().currentLevel()
                .replace("<level>", String.valueOf(currentLevel))
                .replace("<maxLevel>", String.valueOf(maxLevel));
        sendMessage(player, message);
    }

    public void sendRadiationHeightMessage(Player player, int height) {
        String message = messages().radiation().radiationHeight()
                .replace("<height>", String.valueOf(height));
        sendMessage(player, message);
    }

    public void sendRadiationPlayersCountMessage(Player player, int count) {
        String message = messages().radiation().playersInRadiation()
                .replace("<count>", String.valueOf(count));
        sendMessage(player, message);
    }

    public void sendRadiationSystemStateMessage(Player player, String status) {
        String message = messages().radiation().systemState()
                .replace("<status>", status);
        sendMessage(player, message);
    }

    public void sendRadiationLevelOutOfRangeMessage(Player player, int min, int max) {
        String message = messages().radiation().levelOutOfRange()
                .replace("<min>", String.valueOf(min))
                .replace("<max>", String.valueOf(max));
        sendMessage(player, message);
    }

    public void sendRadiationLevelSetMessage(Player player, int level) {
        String message = messages().radiation().levelSet()
                .replace("<level>", String.valueOf(level));
        sendMessage(player, message);
    }

    public void sendRadiationHeightOutOfRangeMessage(Player player) {
        String message = messages().radiation().heightOutOfRange();
        sendMessage(player, message);
    }

    public void sendRadiationHeightSetMessage(Player player, int height) {
        String message = messages().radiation().heightSet()
                .replace("<height>", String.valueOf(height));
        sendMessage(player, message);
    }

    public void sendRadiationSpecifyPlayerConsoleMessage(Player player) {
        String message = messages().radiation().specifyPlayerConsole();
        sendMessage(player, message);
    }

    public void sendRadiationPlayerStatusHeaderMessage(Player player, String targetName) {
        String message = messages().radiation().playerStatusHeader()
                .replace("<player>", targetName);
        sendMessage(player, message);
    }

    public void sendRadiationInRadiationStatusMessage(Player player, boolean inRadiation) {
        String status = inRadiation ?
                messages().radiation().inRadiationYes() :
                messages().radiation().inRadiationNo();
        String message = messages().radiation().inRadiationStatus()
                .replace("<status>", status);
        sendMessage(player, message);
    }

    public void sendRadiationImmuneStatusMessage(Player player, boolean isImmune) {
        String status = isImmune ?
                messages().radiation().immuneTrue() :
                messages().radiation().immuneFalse();
        String message = messages().radiation().immuneStatus()
                .replace("<status>", status);
        sendMessage(player, message);
    }

    public void sendRadiationArmorProtectionStatusMessage(Player player, String armorType, int level) {
        String message = messages().radiation().armorProtectionStatus()
                .replace("<armor>", armorType)
                .replace("<level>", String.valueOf(level));
        sendMessage(player, message);
    }

    public void sendRadiationPlayerHeightStatusMessage(Player player, int height) {
        String message = messages().radiation().playerHeightStatus()
                .replace("<height>", String.valueOf(height));
        sendMessage(player, message);
    }

    public void sendRadiationRadiationHeightStatusMessage(Player player, int height) {
        String message = messages().radiation().radiationHeightStatus()
                .replace("<height>", String.valueOf(height));
        sendMessage(player, message);
    }

    public void sendRadiationPlayerImmuneMessage(Player player, String targetName) {
        String message = messages().radiation().playerImmune()
                .replace("<player>", targetName);
        sendMessage(player, message);
    }

    public void sendRadiationPlayerNotImmuneMessage(Player player, String targetName) {
        String message = messages().radiation().playerNotImmune()
                .replace("<player>", targetName);
        sendMessage(player, message);
    }

    public void sendRadiationImmunityInstructionsMessage(Player player, String targetName) {
        String message = messages().radiation().immunityInstructions()
                .replace("<player>", targetName);
        sendMessage(player, message);
    }

    public void sendFactionCreatedMessage(CommandSender sender, String factionName) {
        String message = messages().faction().factionCreated()
                .replace("<faction>", factionName);
        sendMessage(sender, message);
    }

    public void sendFactionDeletedMessage(CommandSender sender, String factionName) {
        String message = messages().faction().factionDeleted()
                .replace("<faction>", factionName);
        sendMessage(sender, message);
    }

    public void sendPlayerJoinedFactionMessage(CommandSender sender, String playerName, String factionName) {
        String message = messages().faction().playerJoined()
                .replace("<player>", playerName)
                .replace("<faction>", factionName);
        sendMessage(sender, message);
    }

    public void sendPlayerLeftFactionMessage(CommandSender sender, String playerName, String factionName) {
        String message = messages().faction().playerLeft()
                .replace("<player>", playerName)
                .replace("<faction>", factionName);
        sendMessage(sender, message);
    }

    public void sendFactionNotFoundMessage(CommandSender sender, String factionName) {
        String message = messages().faction().factionNotFound()
                .replace("<faction>", factionName);
        sendMessage(sender, message);
    }

    public void sendAlreadyInFactionMessage(CommandSender sender) {
        String message = messages().faction().alreadyInFaction();
        sendMessage(sender, message);
    }

    public void sendNotInFactionMessage(CommandSender sender) {
        String message = messages().faction().notInFaction();
        sendMessage(sender, message);
    }

    public void sendNoPermissionMessage(CommandSender sender) {
        String message = messages().faction().noPermission();
        sendMessage(sender, message);
    }

    public void sendFactionInfoMessage(CommandSender sender, String factionName, String alias, int members, String base) {
        String message = messages().faction().factionInfo()
                .replace("<faction>", factionName)
                .replace("<alias>", alias)
                .replace("<members>", String.valueOf(members))
                .replace("<base>", base);
        sendMessage(sender, message);
    }

    public void sendFactionAlreadyExistsMessage(CommandSender sender, String factionName) {
        String message = messages().faction().factionAlreadyExists()
                .replace("<faction>", factionName);
        sendMessage(sender, message);
    }

    public void sendMaxFactionsReachedMessage(CommandSender sender, int maxFactions) {
        String message = messages().faction().maxFactionsReached()
                .replace("<max>", String.valueOf(maxFactions));
        sendMessage(sender, message);
    }

    public void sendFactionFullMessage(CommandSender sender) {
        String message = messages().faction().factionFull();
        sendMessage(sender, message);
    }

    public void sendFactionFriendlyFireMessage(CommandSender sender) {
        String message = messages().faction().friendlyFire();
        sendMessage(sender, message);
    }

    public void sendFactionNameTooLongMessage(CommandSender sender, int maxLength) {
        String message = messages().faction().nameToolong()
                .replace("<max>", String.valueOf(maxLength));
        sendMessage(sender, message);
    }

    public void sendFactionAliasTooLongMessage(CommandSender sender, int maxLength) {
        String message = messages().faction().aliasToolong()
                .replace("<max>", String.valueOf(maxLength));
        sendMessage(sender, message);
    }

    public void sendPlayerForceJoinedMessage(CommandSender sender, String playerName, String factionName) {
        String message = messages().faction().playerForceJoined()
                .replace("<player>", playerName)
                .replace("<faction>", factionName);
        sendMessage(sender, message);
    }

    public void sendForceJoinedNotificationMessage(Player player, String factionName) {
        String message = messages().faction().forceJoinedNotification()
                .replace("<faction>", factionName);
        sendMessage(player, message);
    }

    public void sendPlayerNotInFactionMessage(CommandSender sender, String playerName) {
        String message = messages().faction().playerNotInFaction()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    public void sendPlayerKickedMessage(CommandSender sender, String playerName, String factionName) {
        String message = messages().faction().playerKicked()
                .replace("<player>", playerName)
                .replace("<faction>", factionName);
        sendMessage(sender, message);
    }

    public void sendKickedNotificationMessage(Player player, String factionName) {
        String message = messages().faction().kickedNotification()
                .replace("<faction>", factionName);
        sendMessage(player, message);
    }

    public void sendFactionAliasChangedMessage(CommandSender sender, String factionName, String newAlias) {
        String message = messages().faction().aliasChanged()
                .replace("<faction>", factionName)
                .replace("<alias>", newAlias);
        sendMessage(sender, message);
    }

    public void sendNoFactionsExistMessage(CommandSender sender) {
        String message = messages().faction().noFactionsExist();
        sendMessage(sender, message);
    }

    public void sendFactionListHeaderMessage(CommandSender sender) {
        String message = messages().faction().factionListHeader();
        sendMessage(sender, message);
    }

    public void sendFactionListItemMessage(CommandSender sender, String name, String alias, int members) {
        String message = messages().faction().factionListItem()
                .replace("<name>", name)
                .replace("<alias>", alias)
                .replace("<members>", String.valueOf(members));
        sendMessage(sender, message);
    }

    public void sendBaseSetMessage(CommandSender sender) {
        String message = messages().faction().baseSet();
        sendMessage(sender, message);
    }

    public void sendBaseNotSetMessage(CommandSender sender) {
        String message = messages().faction().baseNotSet();
        sendMessage(sender, message);
    }

    public void sendBaseTeleportedMessage(CommandSender sender) {
        String message = messages().faction().baseTeleported();
        sendMessage(sender, message);
    }

    public void sendBaseSetOtherMessage(CommandSender sender, String factionName) {
        String message = messages().faction().baseSetOther()
                .replace("<faction>", factionName);
        sendMessage(sender, message);
    }

    public void sendTpaRequestSentMessage(CommandSender sender, String playerName) {
        String message = messages().faction().tpaRequestSent()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    public void sendTpaRequestReceivedMessage(CommandSender sender, String playerName) {
        String message = messages().faction().tpaRequestReceived()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    public void sendTpaNoRequestsMessage(CommandSender sender) {
        String message = messages().faction().tpaNoRequests();
        sendMessage(sender, message);
    }

    public void sendTpaRequestAcceptedMessage(CommandSender sender, String playerName) {
        String message = messages().faction().tpaRequestAccepted()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    public void sendTpaRequestDeniedMessage(CommandSender sender, String playerName) {
        String message = messages().faction().tpaRequestDenied()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    public void sendTpaRequestAcceptedSenderMessage(CommandSender sender, String playerName) {
        String message = messages().faction().tpaRequestAcceptedSender()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    public void sendTpaRequestDeniedSenderMessage(CommandSender sender, String playerName) {
        String message = messages().faction().tpaRequestDeniedSender()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    public void sendTpaRequestExpiredMessage(CommandSender sender, String playerName) {
        String message = messages().faction().tpaRequestExpired()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    public void sendTpaNotSameFactionMessage(CommandSender sender) {
        String message = messages().faction().tpaNotSameFaction();
        sendMessage(sender, message);
    }

    public void sendTpaSelfRequestMessage(CommandSender sender) {
        String message = messages().faction().tpaSelfRequest();
        sendMessage(sender, message);
    }

    public void sendTpaPlayerOfflineMessage(CommandSender sender, String playerName) {
        String message = messages().faction().tpaPlayerOffline()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    public void sendTpaAlreadyHasRequestMessage(CommandSender sender, String playerName) {
        String message = messages().faction().tpaAlreadyHasRequest()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    public void sendReloadSuccessMessage(Player player, long duration) {
        String message = messages().general().reloadComplete()
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

    public void sendBombModuleDisabledMessage(CommandSender sender) {
        String message = messages().bombs().moduleDisabled();
        sendMessage(sender, message);
    }

    public void sendBombGivenMessage(Player player) {
        String message = messages().bombs().bombGiven();
        sendMessage(player, message);
    }

    public void sendBombActivatedMessage(Player player) {
        String message = messages().bombs().bombActivated();
        sendMessage(player, message);
    }

    public void sendBombActivationFailedMessage(Player player) {
        String message = messages().bombs().bombActivationFailed();
        sendMessage(player, message);
    }

    public void sendBombConfirmationMessage(Player player) {
        String message = messages().bombs().bombConfirmation();
        sendMessage(player, message);
    }

    public void sendBombCooldownMessage(Player player, long seconds) {
        String message = messages().bombs().bombCooldown()
                .replace("<seconds>", String.valueOf(seconds));
        sendMessage(player, message);
    }

    public void sendBombProtectedAreaMessage(Player player) {
        String message = messages().bombs().bombProtectedArea();
        sendMessage(player, message);
    }

    public void sendBombTimerTitle(Player player, int seconds, String location) {
        String title = messages().bombs().timerTitle()
                .replace("<seconds>", String.valueOf(seconds))
                .replace("<location>", location);
        sendTitle(player, title, "");
    }

    public void sendBombTimerSubtitle(Player player, int seconds, String activator) {
        String subtitle = messages().bombs().timerSubtitle()
                .replace("<seconds>", String.valueOf(seconds))
                .replace("<activator>", activator);
        sendTitle(player, "", subtitle);
    }

    public void sendExplosionWarningMessage(Player player) {
        String message = messages().bombs().explosionWarning();
        sendMessage(player, message);
    }

    public void sendRadiationZoneCreatedMessage(Player player, int radius, int duration) {
        String message = messages().bombs().radiationZoneCreated()
                .replace("<radius>", String.valueOf(radius))
                .replace("<duration>", String.valueOf(duration));
        sendMessage(player, message);
    }

    public void sendBombCancelledMessage(CommandSender sender) {
        String message = messages().bombs().bombCancelled();
        sendMessage(sender, message);
    }

    public void sendBombForcedMessage(CommandSender sender) {
        String message = messages().bombs().bombForced();
        sendMessage(sender, message);
    }

    public void sendNoBombFoundMessage(CommandSender sender) {
        String message = messages().bombs().noBombFound();
        sendMessage(sender, message);
    }

    public void sendInvalidBombIdMessage(CommandSender sender) {
        String message = messages().bombs().invalidBombId();
        sendMessage(sender, message);
    }

    public void sendBombListHeaderMessage(CommandSender sender) {
        String message = messages().bombs().bombListHeader();
        sendMessage(sender, message);
    }

    public void sendBombListEmptyMessage(CommandSender sender) {
        String message = messages().bombs().bombListEmpty();
        sendMessage(sender, message);
    }

    public void sendCooldownInfoMessage(CommandSender sender, String playerName) {
        String message = messages().bombs().cooldownInfo()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    public void sendCooldownExpiredMessage(CommandSender sender, String playerName, long seconds) {
        String message = messages().bombs().cooldownExpired()
                .replace("<player>", playerName)
                .replace("<seconds>", String.valueOf(seconds));
        sendMessage(sender, message);
    }

    public void sendBombActivatedAtLocationMessage(CommandSender sender, int x, int y, int z) {
        String message = messages().bombs().bombActivated()
                .replace("<x>", String.valueOf(x))
                .replace("<y>", String.valueOf(y))
                .replace("<z>", String.valueOf(z));
        sendMessage(sender, message);
    }

    public void sendBombGivenToPlayerMessage(CommandSender sender, String playerName) {
        String message = messages().bombs().bombGivenToPlayer()
                .replace("<player>", playerName);
        sendMessage(sender, message);
    }

    public void sendBombInfoMessage(CommandSender sender, int radius, int depth, int duration, int cooldown, String radiationStatus, String mushroomStatus) {
        String message = messages().bombs().bombInfo()
                .replace("<radius>", String.valueOf(radius))
                .replace("<depth>", String.valueOf(depth))
                .replace("<duration>", String.valueOf(duration))
                .replace("<cooldown>", String.valueOf(cooldown))
                .replace("<radiationStatus>", radiationStatus)
                .replace("<mushroomStatus>", mushroomStatus);
        sendMessage(sender, message);
    }

    public void sendBombListItemMessage(CommandSender sender, String id, String activator, int x, int y, int z) {
        String message = messages().bombs().bombListItem()
                .replace("<id>", id)
                .replace("<activator>", activator)
                .replace("<x>", String.valueOf(x))
                .replace("<y>", String.valueOf(y))
                .replace("<z>", String.valueOf(z));
        sendMessage(sender, message);
    }

    public void sendPlayersOnlyCommandMessage(CommandSender sender) {
        String message = messages().bombs().playersOnlyCommand();
        sendMessage(sender, message);
    }

    public ItemStack createBombItem() {
        ItemStack bombItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) bombItem.getItemMeta();

        if (meta != null) {
            Component displayName = colorizeToComponent(messages().bombs().bombItemName());
            meta.displayName(displayName);

            String loreText = messages().bombs().bombItemLore();
            List<Component> loreComponents = Arrays.stream(loreText.split("\\n"))
                    .map(this::colorizeToComponent)
                    .collect(Collectors.toList());
            meta.lore(loreComponents);

            bombItem.setItemMeta(meta);
        }

        return bombItem;
    }

    private String getVersion() {
        try {
            return getClass().getPackage().getImplementationVersion() != null ?
                    getClass().getPackage().getImplementationVersion() : "1.0-SNAPSHOT";
        } catch (Exception e) {
            return "1.0";
        }
    }

    private Component colorizeToComponent(String message) {
        if (message == null) return Component.empty();

        message = message.replaceAll("&#([A-Fa-f0-9]{6})", "<#$1>");
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }
}
