package me.mapacheee.falloutcore.shared.config;

import com.google.inject.Inject;
import com.thewinterframework.service.annotation.Service;
import me.mapacheee.falloutcore.config.Messages;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
public class ConfigService {

    private final Logger logger;
    private final Plugin plugin;
    private Config config;
    private Messages messages;

    @Inject
    public ConfigService(Logger logger, Plugin plugin) {
        this.logger = logger;
        this.plugin = plugin;
        loadConfigurations();
    }

    private void loadConfigurations() {
        try {
            plugin.reloadConfig();
            FileConfiguration configFile = plugin.getConfig();

            File configFileObj = new File(plugin.getDataFolder(), "config.yml");
            if (!configFileObj.exists()) {
                plugin.saveDefaultConfig();
            }

            configFile = YamlConfiguration.loadConfiguration(configFileObj);

            this.config = new Config(
                new Config.FactionConfig(
                    configFile.getBoolean("faction.enabled", true),
                    configFile.getBoolean("faction.admin-only-create", false),
                    configFile.getBoolean("faction.enable-friendly-fire", false),
                    configFile.getInt("faction.max-factions", 10),
                    configFile.getInt("faction.max-members-per-faction", 20),
                    configFile.getInt("faction.max-name-length", 16),
                    configFile.getInt("faction.max-alias-length", 4),
                    configFile.getInt("faction.nexus-points-per-destroy", 1),
                    configFile.getBoolean("faction.enable-nexus-system", true)
                ),
                new Config.RadiationConfig(
                    configFile.getBoolean("radiation.enabled", true),
                    configFile.getBoolean("radiation.enable-sound", true),
                    configFile.getString("radiation.sound-type", "ENTITY_WITHER_AMBIENT"),
                    (float) configFile.getDouble("radiation.sound-volume", 0.3),
                    (float) configFile.getDouble("radiation.sound-pitch", 1.0),
                    configFile.getInt("radiation.min-level", 1),
                    configFile.getInt("radiation.max-level", 5),
                    configFile.getInt("radiation.starting-height", 80),
                    configFile.getInt("radiation.change-interval-minutes", 10),
                    configFile.getDouble("radiation.damage-per-level", 2.0),
                    configFile.getInt("radiation.effect-duration-seconds", 30),
                    configFile.getBoolean("radiation.enable-particles", true),
                    configFile.getInt("radiation.armor-damage-min", 1),
                    configFile.getInt("radiation.armor-damage-max", 3)
                ),
                new Config.DatabaseConfig(
                    configFile.getString("database.type", "sqlite"),
                    configFile.getString("database.host", "localhost"),
                    configFile.getInt("database.port", 3306),
                    configFile.getString("database.database", "falloutcore"),
                    configFile.getString("database.username", "root"),
                    configFile.getString("database.password", ""),
                    configFile.getString("database.table-prefix", "fc_")
                )
            );

            logger.info("Config cargado - Sonido: {}, Volumen: {}, Tono: {}",
                config.radiation().soundType(),
                config.radiation().soundVolume(),
                config.radiation().soundPitch());

            File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
            if (!messagesFile.exists()) {
                plugin.saveResource("messages.yml", false);
            }

            FileConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
            InputStreamReader reader = new InputStreamReader(
                Objects.requireNonNull(plugin.getResource("messages.yml")), StandardCharsets.UTF_8);
                YamlConfiguration defaultMessages = YamlConfiguration.loadConfiguration(reader);
                messagesConfig.setDefaults(defaultMessages);

            this.messages = new Messages(
                new Messages.General(
                    messagesConfig.getString("general.prefix", "&8[&6FalloutCore&8]"),
                    messagesConfig.getString("general.noPermission", "&cNo tienes permisos para ejecutar este comando."),
                    messagesConfig.getString("general.playersOnly", "&cSolo los jugadores pueden usar este comando."),
                    messagesConfig.getString("general.noPlayer", "&cJugador no encontrado."),
                    messagesConfig.getString("general.commandUsage", "&eUso: <usage>"),
                    messagesConfig.getString("general.reloaded", "&aPlugin recargado exitosamente.")
                ),
                new Messages.Faction(
                    messagesConfig.getString("faction.factionCreated", "&aFacción '<faction>' creada exitosamente."),
                    messagesConfig.getString("faction.factionDeleted", "&cFacción '<faction>' eliminada."),
                    messagesConfig.getString("faction.playerJoined", "&a<player> se ha unido a la facción '<faction>'."),
                    messagesConfig.getString("faction.playerLeft", "&e<player> ha abandonado la facción '<faction>'."),
                    messagesConfig.getString("faction.factionNotFound", "&cLa facción '<faction>' no existe."),
                    messagesConfig.getString("faction.alreadyInFaction", "&cYa perteneces a una facción."),
                    messagesConfig.getString("faction.notInFaction", "&cNo perteneces a ninguna facción."),
                    messagesConfig.getString("faction.noPermission", "&cNo tienes permisos para ejecutar este comando."),
                    messagesConfig.getString("faction.factionInfo", "&6Facción: <faction> | Alias: <alias> | Miembros: <members> | Base: <base>"),
                    messagesConfig.getString("faction.factionList", "&9Facciones disponibles: <factions>"),
                    messagesConfig.getString("faction.nexusDestroyed", "&c¡El nexo de la facción '<faction>' ha sido destruido!"),
                    messagesConfig.getString("faction.pointsAwarded", "&a¡Has ganado <points> puntos por destruir el nexus!"),
                    messagesConfig.getString("faction.friendlyFire", "&cNo puedes atacar a miembros de tu propia facción."),
                    messagesConfig.getString("faction.nameToolong", "&cEl nombre es muy largo (máximo <max> caracteres)"),
                    messagesConfig.getString("faction.aliasToolong", "&cEl alias es muy largo (máximo <max> caracteres)"),
                    messagesConfig.getString("faction.factionAlreadyExists", "&cLa facción '<faction>' ya existe."),
                    messagesConfig.getString("faction.maxFactionsReached", "&cSe ha alcanzado el límite máximo de facciones (<max>)"),
                    messagesConfig.getString("faction.factionFull", "&cLa facción está llena."),
                    messagesConfig.getString("faction.playerForceJoined", "&aEl jugador <player> ha sido forzado a unirse a <faction>"),
                    messagesConfig.getString("faction.forceJoinedNotification", "&aHas sido añadido a la facción <faction> por un administrador."),
                    messagesConfig.getString("faction.playerNotInFaction", "&cEl jugador <player> no está en ninguna facción."),
                    messagesConfig.getString("faction.playerKicked", "&aEl jugador <player> ha sido expulsado de <faction>"),
                    messagesConfig.getString("faction.kickedNotification", "&cHas sido expulsado de la facción <faction>"),
                    messagesConfig.getString("faction.aliasChanged", "&aAlias de la facción <faction> cambiado a <alias>"),
                    messagesConfig.getString("faction.noFactionsExist", "&7No existen facciones actualmente."),
                    messagesConfig.getString("faction.factionListHeader", "&6=== Lista de Facciones ==="),
                    messagesConfig.getString("faction.factionListItem", "&e<name> &7(&f<alias>&7) &7- &f<members> miembros"),
                    messagesConfig.getString("faction.baseSet", "&aBase de la facción establecida en tu ubicación actual."),
                    messagesConfig.getString("faction.baseNotSet", "&cTu facción no tiene una base establecida."),
                    messagesConfig.getString("faction.baseTeleported", "&aTeletransportado a la base de tu facción."),
                    messagesConfig.getString("faction.baseSetOther", "&aBase de la facción &e<faction> &aestablecida en tu ubicación actual."),
                    messagesConfig.getString("faction.tpaRequestSent", "&aSolicitud de teletransporte enviada a &e<player>&a."),
                    messagesConfig.getString("faction.tpaRequestReceived", "&e<player> &aha enviado una solicitud de teletransporte. Usa &e/tpaccept &apara aceptar o &e/tpdeny &apara rechazar."),
                    messagesConfig.getString("faction.tpaNoRequests", "&cNo tienes solicitudes de teletransporte pendientes."),
                    messagesConfig.getString("faction.tpaRequestAccepted", "&aHas aceptado la solicitud de teletransporte de &e<player>&a."),
                    messagesConfig.getString("faction.tpaRequestDenied", "&cHas rechazado la solicitud de teletransporte de &e<player>&c."),
                    messagesConfig.getString("faction.tpaRequestAcceptedSender", "&a<player> &aha aceptado tu solicitud de teletransporte."),
                    messagesConfig.getString("faction.tpaRequestDeniedSender", "&c<player> &cha rechazado tu solicitud de teletransporte."),
                    messagesConfig.getString("faction.tpaRequestExpired", "&cTu solicitud de teletransporte a &e<player> &cha expirado."),
                    messagesConfig.getString("faction.tpaNotSameFaction", "&cSolo puedes enviar solicitudes de teletransporte a miembros de tu facción."),
                    messagesConfig.getString("faction.tpaSelfRequest", "&cNo puedes enviarte una solicitud de teletransporte a ti mismo."),
                    messagesConfig.getString("faction.tpaPlayerOffline", "&cEl jugador &e<player> &cno está en línea."),
                    messagesConfig.getString("faction.tpaAlreadyHasRequest", "&cYa tienes una solicitud de teletransporte pendiente con &e<player>&c.")
                ),
                new Messages.Radiation(
                    messagesConfig.getString("radiation.enterRadiation", "&c¡Has entrado en una zona de radiación nivel <level>!"),
                    messagesConfig.getString("radiation.exitRadiation", "&aHas salido de la zona de radiación."),
                    messagesConfig.getString("radiation.levelChanged", "&eEl nivel de radiación ha cambiado a <level>. Altura afectada: Y<height>+"),
                    messagesConfig.getString("radiation.armorProtection", "&9Tu armadura te está protegiendo de la radiación."),
                    messagesConfig.getString("radiation.radiationDamageTitle", "&4⚠ RADIACIÓN ⚠"),
                    messagesConfig.getString("radiation.radiationDamageSubtitle", "&c¡Recibiendo daño por radiación! &7(Nivel <level>)"),
                    messagesConfig.getString("radiation.radiationArmorTitle", "&9⚠ PROTEGIDO ⚠"),
                    messagesConfig.getString("radiation.radiationArmorSubtitle", "&bTu armadura <armor> te protege &7(Nivel <level>)"),
                    messagesConfig.getString("radiation.systemStatus", "&6=== Estado del Sistema de Radiación ==="),
                    messagesConfig.getString("radiation.currentLevel", "&eNivel actual: &f<level>&7/&f<maxLevel>"),
                    messagesConfig.getString("radiation.radiationHeight", "&eAltura de radiación: &fY <height>"),
                    messagesConfig.getString("radiation.playersInRadiation", "&eJugadores en radiación: &f<count>"),
                    messagesConfig.getString("radiation.systemEnabled", "&aActivo"),
                    messagesConfig.getString("radiation.systemDisabled", "&cDesactivado"),
                    messagesConfig.getString("radiation.systemState", "&eEstado: &f<status>"),
                    messagesConfig.getString("radiation.levelSet", "&aNivel de radiación establecido a: &e<level>"),
                    messagesConfig.getString("radiation.levelOutOfRange", "&cEl nivel debe estar entre <min> y <max>"),
                    messagesConfig.getString("radiation.heightSet", "&aAltura de radiación establecida a: &eY <height>"),
                    messagesConfig.getString("radiation.heightOutOfRange", "&cLa altura debe estar entre -64 y 320"),
                    messagesConfig.getString("radiation.specifyPlayerConsole", "&cDebes especificar un jugador desde la consola"),
                    messagesConfig.getString("radiation.playerStatusHeader", "&6=== Estado de Radiación: &e<player> &6==="),
                    messagesConfig.getString("radiation.inRadiationStatus", "&eEn zona de radiación: <status>"),
                    messagesConfig.getString("radiation.immuneStatus", "&eInmune: <status>"),
                    messagesConfig.getString("radiation.armorProtectionStatus", "&eProtección de armadura: &f<armor> &7(Nivel <level>)"),
                    messagesConfig.getString("radiation.playerHeightStatus", "&eAltura actual: &fY <height>"),
                    messagesConfig.getString("radiation.radiationHeightStatus", "&eAltura de radiación: &fY <height>"),
                    messagesConfig.getString("radiation.inRadiationYes", "&cSí"),
                    messagesConfig.getString("radiation.inRadiationNo", "&aNo"),
                    messagesConfig.getString("radiation.immuneTrue", "&aVerdadero"),
                    messagesConfig.getString("radiation.immuneFalse", "&cFalso"),
                    messagesConfig.getString("radiation.playerImmune", "&aEl jugador &e<player> &aes inmune a la radiación"),
                    messagesConfig.getString("radiation.playerNotImmune", "&cEl jugador &e<player> &cNO es inmune a la radiación"),
                    messagesConfig.getString("radiation.immunityInstructions", "&7Para otorgar inmunidad usa: &e/lp user <player> permission set falloutcore.radiation.immune true"),
                    messagesConfig.getString("radiation.enterTitle", "&c&lRADIACIÓN DETECTADA"),
                    messagesConfig.getString("radiation.enterSubtitle", "&7Salga de esta área inmediatamente"),
                    messagesConfig.getString("radiation.damageTitle", "&c&lRADIACIÓN"),
                    messagesConfig.getString("radiation.damageSubtitle", "&7Recibiendo daño por radiación"),
                    messagesConfig.getString("radiation.exitTitle", "&a&lZONA SEGURA"),
                    messagesConfig.getString("radiation.exitSubtitle", "&7Ya no está en una zona de radiación"),
                    messagesConfig.getString("radiation.armorProtectionTitle", "&a&lPROTEGIDO"),
                    messagesConfig.getString("radiation.armorProtectionSubtitle", "&7Tu armadura te está protegiendo")
                )
            );

            logger.info("Configuraciones cargadas exitosamente");
        } catch (Exception e) {
            logger.error("Error cargando configuraciones", e);
        }
    }

    public Config getConfig() {
        return config;
    }

    public Messages getMessages() {
        return messages;
    }

    public boolean reloadConfigurations() {
        try {
            logger.info("Recargando configuraciones...");
            loadConfigurations();
            logger.info("Configuraciones recargadas");
            return true;
        } catch (Exception e) {
            logger.error("Error al recargar configuraciones", e);
            return false;
        }
    }

    public String getPluginVersion() {
        return plugin.getPluginMeta().getVersion();
    }
}
