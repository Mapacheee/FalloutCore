package me.mapacheee.falloutcore.shared.config;

import com.thewinterframework.configurate.config.Configurate;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@Configurate("config")
public record Config(
    FactionConfig faction,
    RadiationConfig radiation,
    DatabaseConfig database
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
        int minLevel,
        int maxLevel,
        int startingHeight,
        int changeIntervalMinutes,
        double damagePerLevel,
        int effectDurationSeconds,
        boolean enableParticles
    ) {}

    @ConfigSerializable
    public record DatabaseConfig(
        String type,
        String host,
        int port,
        String database,
        String username,
        String password,
        String tablePrefix
    ) {}
}
