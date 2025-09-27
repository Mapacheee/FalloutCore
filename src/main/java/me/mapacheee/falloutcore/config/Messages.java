package me.mapacheee.falloutcore.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Messages {

    private RadiationMessages radiation;
    private FactionMessages faction;
    private GeneralMessages general;
    private FileConfiguration config;
    private File configFile;
    private Plugin plugin;

    public Messages(Plugin plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    private void loadMessages() {
        configFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!configFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        InputStreamReader reader = new InputStreamReader(
            plugin.getResource("messages.yml"), StandardCharsets.UTF_8);
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(reader);
        config.setDefaults(defaultConfig);

        loadFromConfig();
    }

    private void loadFromConfig() {
        this.radiation = new RadiationMessages();
        this.faction = new FactionMessages();
        this.general = new GeneralMessages();
    }

    public void reload() {
        loadMessages();
    }

    public RadiationMessages radiation() { return radiation; }
    public FactionMessages faction() { return faction; }
    public GeneralMessages general() { return general; }

    public class RadiationMessages {
        public String enterRadiation() { 
            return config.getString("radiation.enterRadiation", "&c¡Has entrado en una zona de radiación nivel <level>!");
        }
        public String exitRadiation() { 
            return config.getString("radiation.exitRadiation", "&aHas salido de la zona de radiación.");
        }
        public String levelChanged() { 
            return config.getString("radiation.levelChanged", "&eEl nivel de radiación ha cambiado a <level>. Altura afectada: Y<height>+");
        }
        public String armorProtection() { 
            return config.getString("radiation.armorProtection", "&9Tu armadura te está protegiendo de la radiación.");
        }
        public String armorDegrading() { 
            return config.getString("radiation.armorDegrading", "&6Tu armadura se está desgastando por la radiación.");
        }
        public String takingDamage() { 
            return config.getString("radiation.takingDamage", "&4La radiación te está causando daño!");
        }
    }

    public class FactionMessages {
        public String factionCreated() { 
            return config.getString("faction.factionCreated", "&aFacción '<faction>' creada exitosamente.");
        }
        public String factionDeleted() { 
            return config.getString("faction.factionDeleted", "&cFacción '<faction>' eliminada.");
        }
        public String playerJoined() { 
            return config.getString("faction.playerJoined", "&a<player> se ha unido a la facción '<faction>'.");
        }
        public String playerLeft() { 
            return config.getString("faction.playerLeft", "&e<player> ha abandonado la facción '<faction>'.");
        }
        public String factionNotFound() { 
            return config.getString("faction.factionNotFound", "&cLa facción '<faction>' no existe.");
        }
        public String alreadyInFaction() { 
            return config.getString("faction.alreadyInFaction", "&cYa perteneces a una facción.");
        }
        public String notInFaction() { 
            return config.getString("faction.notInFaction", "&cNo perteneces a ninguna facción.");
        }
        public String noPermission() { 
            return config.getString("faction.noPermission", "&cNo tienes permisos para ejecutar este comando.");
        }
        public String factionInfo() { 
            return config.getString("faction.factionInfo", "&6Facción: <faction> | Alias: <alias> | Miembros: <members> | Base: <base>");
        }
        public String factionList() { 
            return config.getString("faction.factionList", "&9Facciones disponibles: <factions>");
        }
        public String nexusDestroyed() { 
            return config.getString("faction.nexusDestroyed", "&c¡El nexo de la facción '<faction>' ha sido destruido!");
        }
        public String pointsAwarded() { 
            return config.getString("faction.pointsAwarded", "&a¡Has ganado <points> puntos por destruir el nexus!");
        }
        public String friendlyFire() { 
            return config.getString("faction.friendlyFire", "&cNo puedes atacar a miembros de tu propia facción.");
        }
    }

    public class GeneralMessages {
        public String prefix() {
            return config.getString("general.prefix", "&8[&6FalloutCore&8]");
        }
        public String noPlayer() { 
            return config.getString("general.noPlayer", "&cJugador no encontrado.");
        }
        public String invalidNumber() { 
            return config.getString("general.invalidNumber", "&cNúmero inválido.");
        }
        public String commandUsage() { 
            return config.getString("general.commandUsage", "&eUso: <usage>");
        }
        public String reloaded() { 
            return config.getString("general.reloaded", "&aPlugin recargado exitosamente.");
        }
    }
}
