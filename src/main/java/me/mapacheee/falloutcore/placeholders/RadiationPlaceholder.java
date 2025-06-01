package me.mapacheee.falloutcore.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.mapacheee.falloutcore.radiation.RadiationSystem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RadiationPlaceholder extends PlaceholderExpansion {

    public String onPlaceholderRequest(Player p, @NotNull String params) {
        if ("level".equals(params))
            return String.valueOf(RadiationSystem.getInstance().getCurrentLevel());
        return null;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "";
    }

    @Override
    public @NotNull String getAuthor() {
        return "";
    }

    @Override
    public @NotNull String getVersion() {
        return "";
    }
}
