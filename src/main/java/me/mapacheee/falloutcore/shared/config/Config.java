package me.mapacheee.falloutcore.shared.config;

import com.thewinterframework.configurate.config.Configurate;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import java.util.List;

@ConfigSerializable
@Configurate("config")
public record Config(
    FactionConfig faction,
    RadiationConfig radiation,
    BombConfig bomb
) {

    @ConfigSerializable
    public record FactionConfig(
        boolean enabled,
        boolean adminOnlyCreate,
        boolean enableFriendlyFire,
        int maxFactions,
        int maxMembersPerFaction,
        int maxNameLength,
        int maxAliasLength,
        int nexusPointsPerDestroy,
        boolean enableNexusSystem
    ) {}

    @ConfigSerializable
    public record RadiationConfig(
        boolean enabled,
        boolean enableSound,
        String soundType,
        float soundVolume,
        float soundPitch,
        int minLevel,
        int maxLevel,
        int startingHeight,
        int changeIntervalMinutes,
        double damagePerLevel,
        int effectDurationSeconds,
        boolean enableParticles,
        int armorDamageMin,
        int armorDamageMax
    ) {}

    @ConfigSerializable
    public record BombConfig(
        boolean enabled,
        NuclearConfig nuclear,
        boolean respectWorldGuard,
        boolean onlyGlobalRegion
    ) {}

    @ConfigSerializable
    public record NuclearConfig(
        int maxRadius,
        int craterDepth,
        int explosionDurationSeconds,
        String headSkin,
        TimerConfig timer,
        ExplosionPhasesConfig phases,
        MushroomConfig mushroom,
        ScreenEffectsConfig screenEffects,
        PostRadiationConfig postRadiation,
        String requiredPermission,
        int cooldownSeconds
    ) {}

    @ConfigSerializable
    public record TimerConfig(
        boolean enabled,
        int countdownSeconds,
        String titleMessage,
        String subtitleMessage,
        String soundType,
        float soundVolume,
        float soundPitch
    ) {}

    @ConfigSerializable
    public record ExplosionPhasesConfig(
        DetonationPhase detonation,
        FireballPhase fireball,
        ShockwavePhase shockwave
    ) {}

    @ConfigSerializable
    public record DetonationPhase(
        int durationSeconds,
        boolean flashEffect,
        float soundVolume,
        String soundType
    ) {}

    @ConfigSerializable
    public record FireballPhase(
        int durationSeconds,
        int expansionSpeed,
        boolean fireParticles,
        String particleType
    ) {}

    @ConfigSerializable
    public record ShockwavePhase(
        int durationSeconds,
        int expansionSpeed,
        List<DestructionLayer> destructionLayers
    ) {}

    @ConfigSerializable
    public record DestructionLayer(
        int radius,
        String material,
        double destructionChance
    ) {}

    @ConfigSerializable
    public record MushroomConfig(
        boolean enabled,
        int height,
        int durationSeconds,
        int columnRadius,
        int headRadius,
        int particleCount,
        String particleType
    ) {}

    @ConfigSerializable
    public record ScreenEffectsConfig(
        boolean flash,
        boolean shake,
        boolean distortion,
        boolean radioactiveOverlay,
        int durationSeconds
    ) {}

    @ConfigSerializable
    public record PostRadiationConfig(
        boolean enabled,
        int radiationLevel,
        int durationMinutes,
        int affectedRadius,
        boolean gradualDecrease
    ) {}
}
