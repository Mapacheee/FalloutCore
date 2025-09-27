package me.mapacheee.falloutcore.placeholders;

import com.google.inject.Inject;
import com.thewinterframework.service.annotation.Service;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.mapacheee.falloutcore.factions.entity.Faction;
import me.mapacheee.falloutcore.factions.entity.FactionService;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Service
public class FactionPlaceholderProvider extends PlaceholderExpansion {

    @Inject
    private FactionService factionService;

    private String version;

    public FactionPlaceholderProvider() {
        loadVersion();
    }

    private void loadVersion() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("version.properties")) {
            Properties properties = new Properties();
            properties.load(input);
            this.version = properties.getProperty("version", "1.0.0");
        } catch (IOException e) {
            this.version = "1.0.0";
        }
    }

    @Override
    public @NotNull String getIdentifier() {
        return "fallout";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Mapacheee";
    }

    @Override
    public @NotNull String getVersion() {
        return version;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";

        switch (params.toLowerCase()) {
            case "faction_alias":
                return factionService.getPlayerFactionAlias(player);

            case "faction_name":
                Faction faction = factionService.getPlayerFaction(player);
                return faction != null ? faction.getName() : "";

            case "faction_members":
                Faction playerFaction = factionService.getPlayerFaction(player);
                return playerFaction != null ? String.valueOf(playerFaction.getMembers().size()) : "0";

            default:
                return null;
        }
    }
}

