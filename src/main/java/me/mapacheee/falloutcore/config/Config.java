package me.mapacheee.falloutcore.config;

import com.thewinterframework.configurate.config.Configurate;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@Configurate("config")
public record Config(
    RadiationConfig radiation,
    FactionConfig faction,
    BombConfig bomb
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

    @ConfigSerializable
    public record BombConfig(
        boolean enabled,
        boolean respectWorldGuard,
        boolean onlyGlobalRegion,
        NuclearConfig nuclear
    ) {
        public BombConfig() {
            this(true, true, true, new NuclearConfig());
        }
    }

    @ConfigSerializable
    public record NuclearConfig(
        int maxRadius,
        int craterDepth,
        int explosionDurationSeconds,
        String headSkin,
        int cooldownSeconds,
        TimerConfig timer,
        ExplosionPhasesConfig phases,
        MushroomConfig mushroom,
        ScreenEffectsConfig screenEffects,
        PostRadiationConfig postRadiation
    ) {
        public NuclearConfig() {
            this(100, 25, 30, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWU2YjIwZjY4MTQyNDUyYzRkN2JkNzNiNTgwNjQ4NzQwYzI3ZGIyOTMxODVhM2IyOGM0YzJhNmE2MmY4YmEwIn19fQ==", 300, new TimerConfig(), new ExplosionPhasesConfig(), new MushroomConfig(), new ScreenEffectsConfig(), new PostRadiationConfig());
        }
    }

    @ConfigSerializable
    public record TimerConfig(
        boolean enabled,
        int countdownSeconds,
        String titleMessage,
        String subtitleMessage,
        String soundType,
        float soundVolume,
        float soundPitch
    ) {
        public TimerConfig() {
            this(true, 10, "&c&l¡BOMBA NUCLEAR ACTIVADA!", "&7Detonación en <seconds> segundos", "ENTITY_WITHER_AMBIENT", 1.0f, 0.5f);
        }
    }

    @ConfigSerializable
    public record ExplosionPhasesConfig(
        DetonationPhase detonation,
        FireballPhase fireball,
        ShockwavePhase shockwave
    ) {
        public ExplosionPhasesConfig() {
            this(new DetonationPhase(), new FireballPhase(), new ShockwavePhase());
        }
    }

    @ConfigSerializable
    public record DetonationPhase(
        int durationSeconds,
        boolean flashEffect,
        float soundVolume,
        String soundType
    ) {
        public DetonationPhase() {
            this(2, true, 2.0f, "ENTITY_LIGHTNING_BOLT_THUNDER");
        }
    }

    @ConfigSerializable
    public record FireballPhase(
        int durationSeconds,
        int expansionSpeed,
        boolean fireParticles,
        String particleType
    ) {
        public FireballPhase() {
            this(5, 3, true, "EXPLOSION_LARGE");
        }
    }

    @ConfigSerializable
    public record ShockwavePhase(
        int durationSeconds,
        int expansionSpeed,
        java.util.List<DestructionLayer> destructionLayers
    ) {
        public ShockwavePhase() {
            this(10, 5, java.util.Arrays.asList(
                new DestructionLayer(30, "AIR", 1.0),
                new DestructionLayer(60, "GLASS", 0.8),
                new DestructionLayer(100, "COBBLESTONE", 0.5)
            ));
        }
    }

    @ConfigSerializable
    public record DestructionLayer(
        int radius,
        String material,
        double destructionChance
    ) {
        public DestructionLayer() {
            this(50, "AIR", 1.0);
        }
    }

    @ConfigSerializable
    public record MushroomConfig(
        boolean enabled,
        int height,
        int durationSeconds,
        int columnRadius,
        int headRadius,
        int particleCount,
        String particleType
    ) {
        public MushroomConfig() {
            this(true, 50, 15, 8, 15, 100, "EXPLOSION_LARGE");
        }
    }

    @ConfigSerializable
    public record ScreenEffectsConfig(
        boolean flash,
        boolean shake,
        boolean distortion,
        boolean radioactiveOverlay,
        int durationSeconds
    ) {
        public ScreenEffectsConfig() {
            this(true, true, true, true, 10);
        }
    }

    @ConfigSerializable
    public record PostRadiationConfig(
        boolean enabled,
        int radiationLevel,
        int durationMinutes,
        int affectedRadius,
        boolean gradualDecrease
    ) {
        public PostRadiationConfig() {
            this(true, 3, 10, 150, true);
        }
    }

    public Config() {
        this(new RadiationConfig(), new FactionConfig(), new BombConfig());
    }
}
