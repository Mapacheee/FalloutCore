package me.mapacheee.falloutcore.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.mapacheee.falloutcore.factions.Faction;
import me.mapacheee.falloutcore.factions.FactionManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FactionPlaceholder extends PlaceholderExpansion {
    private final FactionManager factionManager = FactionManager.getInstance();

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
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";

        if (params.equalsIgnoreCase("faction_alias")) {
            return factionManager.getPlayerFactionAlias(player);
        }

        if (params.equalsIgnoreCase("faction_name")) {
            Faction faction = factionManager.getPlayerFaction(player);
            return faction != null ? faction.getName() : "";
        }

        return null;
    }
}
