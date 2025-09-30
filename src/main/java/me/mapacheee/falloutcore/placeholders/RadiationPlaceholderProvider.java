package me.mapacheee.falloutcore.placeholders;

import com.google.inject.Inject;
import com.thewinterframework.service.annotation.Service;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.mapacheee.falloutcore.radiation.entity.RadiationService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@Service
public class RadiationPlaceholderProvider extends PlaceholderExpansion {

    private final RadiationService radiationService;
    private final Plugin plugin;

    @Inject
    public RadiationPlaceholderProvider(RadiationService radiationService, Plugin plugin) {
        this.radiationService = radiationService;
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "radiation";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getPluginMeta().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
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

        return switch (params.toLowerCase()) {
            case "level" -> String.valueOf(radiationService.getCurrentRadiationLevel());
            case "height" -> String.valueOf(radiationService.getCurrentRadiationHeight());
            case "in_radiation" -> radiationService.isPlayerInRadiation(player) ? "true" : "false";
            case "is_immune" -> radiationService.isPlayerImmune(player) ? "true" : "false";
            case "armor_protection" -> String.valueOf(radiationService.getPlayerArmorProtection(player).level);
            case "armor_protection_name" -> radiationService.getPlayerArmorProtection(player).displayName;
            case "players_count" -> String.valueOf(radiationService.getPlayersInRadiationCount());
            default -> null;
        };
    }
}
