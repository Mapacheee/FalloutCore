package me.mapacheee.falloutcore.config;

import com.thewinterframework.configurate.config.Configurate;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@Configurate("config")
public record Config(
    RadiationConfig radiation,
    FactionConfig faction
) {

    @ConfigSerializable
    public record RadiationConfig(
        int minLevel,
        int maxLevel,
        int changeIntervalMinutes,
        int startingHeight,
        boolean enableSound,
        double damagePerLevel,
        int effectDurationSeconds
    ) {
        public RadiationConfig() {
            this(1, 5, 10, 80, true, 1.0, 30);
        }
    }

    @ConfigSerializable 
    public record FactionConfig(
        boolean enableFriendlyFire,
        int maxFactions,
        int maxMembersPerFaction,
        boolean adminOnlyCreate,
        int nexusBreakPoints
    ) {
        public FactionConfig() {
            this(false, 10, 20, true, 10);
        }
    }

    public Config() {
        this(new RadiationConfig(), new FactionConfig());
    }
}
