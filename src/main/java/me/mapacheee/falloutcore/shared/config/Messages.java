package me.mapacheee.falloutcore.shared.config;

import com.thewinterframework.configurate.config.Configurate;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@Configurate("messages")
public record Messages(
    General general,
    Factions factions,
    Radiation radiation
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
    public record Factions(
        String helpMessage,
        String adminHelpMessage,
        String createUsage,
        String factionCreated,
        String factionAlreadyExists,
        String maxFactionsReached,
        String nameToolong,
        String aliasToolong,
        String deleteUsage,
        String factionDeleted,
        String factionNotFound,
        String joinUsage,
        String joinedFaction,
        String alreadyInFaction,
        String factionFull,
        String notInFaction,
        String forceJoinUsage,
        String playerForceJoined,
        String forceJoinedNotification,
        String kickUsage,
        String playerKicked,
        String kickedNotification,
        String playerNotInFaction,
        String setAliasUsage,
        String aliasChanged,
        String factionInfo,
        String factionListHeader,
        String factionListItem,
        String noFactionsExist,
        String leftFaction,
        String friendlyFireBlocked,
        String noPermission,
        String playersOnly,
        String playerNotFound
    ) {}

    @ConfigSerializable
    public record Radiation(
        String enterRadiation,
        String exitRadiation,
        String levelChanged,
        String armorProtection,
        String armorDegrading,
        String takingDamage,
        String radiationDamageTitle,
        String radiationDamageSubtitle,
        String immuneMessage,
        String unknownSubcommand,
        String systemStatus,
        String currentLevel,
        String radiationHeight,
        String playersInRadiation,
        String systemEnabled,
        String systemDisabled,
        String systemState,
        String levelSetUsage,
        String levelSet,
        String levelOutOfRange,
        String heightSetUsage,
        String heightSet,
        String heightOutOfRange,
        String invalidNumber,
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
        String currentHeight,
        String playerImmune,
        String playerNotImmune,
        String immunityInstructions,
        String helpHeader,
        String helpInfo,
        String helpCheck,
        String helpImmunity,
        String adminHelpHeader,
        String adminHelpSetLevel,
        String adminHelpSetHeight
    ) {}
}
