package me.mapacheee.falloutcore.placeholders;

import com.google.inject.Inject;
import com.thewinterframework.service.annotation.Service;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.mapacheee.falloutcore.factions.entity.Faction;
import me.mapacheee.falloutcore.factions.entity.FactionService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@Service
public class FactionPlaceholderProvider extends PlaceholderExpansion {

    private final FactionService factionService;
    private final Plugin plugin;

    @Inject
    public FactionPlaceholderProvider(FactionService factionService, Plugin plugin) {
        this.factionService = factionService;
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "fallout";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        Faction faction = factionService.getPlayerFaction(player);

        return switch (params.toLowerCase()) {
            case "faction_name" -> faction != null ? faction.getName() : "Sin facción";
            case "faction_alias" -> faction != null ? faction.getAlias() : "-";
            case "faction_members" -> faction != null ? String.valueOf(faction.getMembers().size()) : "0";
            case "faction_points" -> faction != null ? String.valueOf(faction.getNexusPoints()) : "0";
            case "has_faction" -> faction != null ? "true" : "false";
            default -> null;
        };
    }
}
