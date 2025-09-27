package me.mapacheee.falloutcore.placeholders;

import com.google.inject.Inject;
import com.thewinterframework.service.annotation.Service;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.mapacheee.falloutcore.radiation.entity.RadiationService;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Service
public class RadiationPlaceholderProvider extends PlaceholderExpansion {

    @Inject
    private RadiationService radiationService;

    private String version;

    public RadiationPlaceholderProvider() {
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
        switch (params.toLowerCase()) {
            case "radiation_level":
                return String.valueOf(radiationService.getCurrentRadiationLevel());
            case "radiation_height":
                return String.valueOf(radiationService.getCurrentRadiationHeight());
            case "players_in_radiation":
                return String.valueOf(radiationService.getPlayersInRadiationCount());
            default:
                return null;
        }
    }
}

