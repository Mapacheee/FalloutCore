package me.mapacheee.falloutcore.config;

import com.thewinterframework.configurate.config.Configurate;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@Configurate("messages")
public record Messages(
    RadiationMessages radiation,
    FactionMessages faction,
    GeneralMessages general
) {

    @ConfigSerializable
    public record RadiationMessages(
        String enterRadiation,
        String exitRadiation,
        String levelChanged,
        String armorProtection,
        String armorDegrading,
        String takingDamage,
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
        String immunityInstructions
    ) {}

    @ConfigSerializable
    public record FactionMessages(
        String factionCreated,
        String factionDeleted,
        String playerJoined,
        String playerLeft,
        String factionNotFound,
        String alreadyInFaction,
        String notInFaction,
        String noPermission,
        String factionInfo,
        String factionList,
        String nexusDestroyed,
        String pointsAwarded,
        String friendlyFire,
        String nameToolong,
        String aliasToolong,
        String factionAlreadyExists,
        String maxFactionsReached,
        String factionFull,
        String playerForceJoined,
        String forceJoinedNotification,
        String playerNotInFaction,
        String playerKicked,
        String kickedNotification,
        String aliasChanged,
        String noFactionsExist,
        String factionListHeader,
        String factionListItem
    ) {}

    @ConfigSerializable
    public record GeneralMessages(
        String prefix,
        String noPlayer,
        String invalidNumber,
        String commandUsage,
        String reloaded,
        String playersOnly,
        String noPermission
    ) {}
}
