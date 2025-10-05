package me.mapacheee.falloutcore.shared.config;

import com.thewinterframework.configurate.config.Configurate;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@Configurate("messages")
public record Messages(
    General general,
    Faction faction,
    Radiation radiation,
    Bombs bombs
) {

    @ConfigSerializable
    public record General(
        String prefix,
        String noPermission,
        String playersOnly,
        String playerNotFound,
        String unknownCommand,
        String reloadComplete
    ) {}

    @ConfigSerializable
    public record Faction(
        String factionCreated,
        String factionDeleted,
        String playerJoined,
        String playerLeft,
        String factionNotFound,
        String alreadyInFaction,
        String notInFaction,
        String noPermission,
        String factionInfo,
        String factionAlreadyExists,
        String maxFactionsReached,
        String factionFull,
        String nameToolong,
        String aliasToolong,
        String playerForceJoined,
        String forceJoinedNotification,
        String playerNotInFaction,
        String playerKicked,
        String kickedNotification,
        String aliasChanged,
        String noFactionsExist,
        String factionListHeader,
        String factionListItem,
        String baseSet,
        String baseNotSet,
        String baseTeleported,
        String baseSetOther,
        String tpaRequestSent,
        String tpaRequestReceived,
        String tpaNoRequests,
        String tpaRequestAccepted,
        String tpaRequestDenied,
        String tpaRequestAcceptedSender,
        String tpaRequestDeniedSender,
        String tpaRequestExpired,
        String tpaNotSameFaction,
        String tpaSelfRequest,
        String tpaPlayerOffline,
        String tpaAlreadyHasRequest,
        String friendlyFire
    ) {}

    @ConfigSerializable
    public record Radiation(
        String enterRadiation,
        String exitRadiation,
        String levelChanged,
        String armorProtection,
        String radiationDamageTitle,
        String radiationDamageSubtitle,
        String radiationArmorTitle,
        String radiationArmorSubtitle,
        String systemStatus,
        String currentLevel,
        String radiationHeight,
        String playersInRadiation,
        String systemEnabled,
        String systemDisabled,
        String systemState,
        String levelSet,
        String levelOutOfRange,
        String heightSet,
        String heightOutOfRange,
        String specifyPlayerConsole,
        String playerStatusHeader,
        String inRadiationStatus,
        String immuneStatus,
        String armorProtectionStatus,
        String playerHeightStatus,
        String radiationHeightStatus,
        String inRadiationYes,
        String inRadiationNo,
        String immuneTrue,
        String immuneFalse,
        String playerImmune,
        String playerNotImmune,
        String immunityInstructions,
        String enterTitle,
        String enterSubtitle,
        String damageTitle,
        String damageSubtitle,
        String exitTitle,
        String exitSubtitle,
        String armorProtectionTitle,
        String armorProtectionSubtitle
    ) {}

    @ConfigSerializable
    public record Bombs(
        String moduleDisabled,
        String bombGiven,
        String bombActivated,
        String bombActivationFailed,
        String bombConfirmation,
        String bombCooldown,
        String bombProtectedArea,
        String timerTitle,
        String timerSubtitle,
        String explosionWarning,
        String radiationZoneCreated,
        String bombCancelled,
        String bombForced,
        String noBombFound,
        String invalidBombId,
        String bombListHeader,
        String bombListEmpty,
        String cooldownInfo,
        String cooldownExpired,
        String bombItemName,
        String bombGivenToPlayer,
        String bombInfo,
        String bombListItem,
        String playersOnlyCommand,
        String bombItemLore
    ) {}
}
