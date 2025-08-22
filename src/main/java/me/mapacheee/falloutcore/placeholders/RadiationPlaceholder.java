package me.mapacheee.falloutcore.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.mapacheee.falloutcore.radiation.RadiationSystem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RadiationPlaceholder extends PlaceholderExpansion {

    @Override
    public String onPlaceholderRequest(Player p, @NotNull String params) {
        if (p == null) return "";

        //%fallout_radiation_level%
        if ("level".equalsIgnoreCase(params)) {
            return String.valueOf(RadiationSystem.getInstance().getCurrentLevel());
        }
        return null;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "fallout_radiation";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Mapacheee";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }
}
