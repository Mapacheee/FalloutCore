package me.mapacheee.falloutcore.shared.util;

import com.google.inject.Inject;
import com.thewinterframework.service.annotation.Service;
import me.mapacheee.falloutcore.config.Messages;
import me.mapacheee.falloutcore.shared.config.ConfigService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.function.Function;

@Service
public class MessageUtil {

    private final ConfigService configService;

    @Inject
    public MessageUtil(ConfigService configService) {
        this.configService = configService;
    }

    public void sendMessage(CommandSender sender, String message) {
        String prefixed = configService.getMessages().general().prefix() + " " + message;
        sender.sendMessage(colorize(prefixed));
    }

    public void sendMessage(Player player, String message) {
        sendMessage((CommandSender) player, message);
    }

    public void sendRadiationMessage(Player player, String messageKey, String... replacements) {
        String message = getRadiationMessage(messageKey);
        if (replacements.length >= 2) {
            for (int i = 0; i < replacements.length; i += 2) {
                message = message.replace("<" + replacements[i] + ">", replacements[i + 1]);
            }
        }
        sendMessage(player, message);
    }

    public void sendFactionMessage(CommandSender sender, String messageKey, String... replacements) {
        String message = getFactionMessage(messageKey);
        if (replacements.length >= 2) {
            for (int i = 0; i < replacements.length; i += 2) {
                message = message.replace("<" + replacements[i] + ">", replacements[i + 1]);
            }
        }
        sendMessage(sender, message);
    }

    private String getRadiationMessage(String key) {
        Messages.RadiationMessages rad = configService.getMessages().radiation();

        Map<String, Function<Messages.RadiationMessages, String>> radiationMessages1 = Map.<String, Function<Messages.RadiationMessages, String>>of(
            "enterRadiation", Messages.RadiationMessages::enterRadiation,
            "exitRadiation", Messages.RadiationMessages::exitRadiation,
            "levelChanged", Messages.RadiationMessages::levelChanged,
            "armorProtection", Messages.RadiationMessages::armorProtection,
            "armorDegrading", Messages.RadiationMessages::armorDegrading,
            "takingDamage", Messages.RadiationMessages::takingDamage,
            "radiationDamageTitle", Messages.RadiationMessages::radiationDamageTitle,
            "radiationDamageSubtitle", Messages.RadiationMessages::radiationDamageSubtitle,
            "radiationArmorTitle", Messages.RadiationMessages::radiationArmorTitle,
            "radiationArmorSubtitle", Messages.RadiationMessages::radiationArmorSubtitle
        );

        Map<String, Function<Messages.RadiationMessages, String>> radiationMessages2 = Map.<String, Function<Messages.RadiationMessages, String>>of(
            "systemStatus", Messages.RadiationMessages::systemStatus,
            "currentLevel", Messages.RadiationMessages::currentLevel
        );

        Function<Messages.RadiationMessages, String> messageFunction = radiationMessages1.get(key);
        if (messageFunction == null) {
            messageFunction = radiationMessages2.get(key);
        }

        return messageFunction != null ? messageFunction.apply(rad) : key;
    }

    private String getFactionMessage(String key) {
        Messages.FactionMessages fact = configService.getMessages().faction();

        Map<String, Function<Messages.FactionMessages, String>> factionMessages = Map.<String, Function<Messages.FactionMessages, String>>of(
            "factionCreated", Messages.FactionMessages::factionCreated,
            "factionDeleted", Messages.FactionMessages::factionDeleted,
            "factionNotFound", Messages.FactionMessages::factionNotFound,
            "alreadyInFaction", Messages.FactionMessages::alreadyInFaction,
            "notInFaction", Messages.FactionMessages::notInFaction,
            "noPermission", Messages.FactionMessages::noPermission,
            "factionInfo", Messages.FactionMessages::factionInfo,
            "nameToolong", Messages.FactionMessages::nameToolong,
            "aliasToolong", Messages.FactionMessages::aliasToolong,
            "factionAlreadyExists", Messages.FactionMessages::factionAlreadyExists
        );

        Function<Messages.FactionMessages, String> messageFunction = factionMessages.get(key);
        return messageFunction != null ? messageFunction.apply(fact) : key;
    }

    public String colorize(String message) {
        if (message.contains("#")) {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("#[a-fA-F0-9]{6}");
            java.util.regex.Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                String hexColor = matcher.group();
                try {
                    message = message.replace(hexColor, net.md_5.bungee.api.ChatColor.of(hexColor).toString());
                } catch (Exception e) {}
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void sendTitle(Player player, String title, String subtitle) {
        player.sendTitle(colorize(title), colorize(subtitle), 10, 20, 10);
    }

    public void sendRadiationTitle(Player player, String titleKey, String subtitleKey, String... replacements) {
        String title = getRadiationMessage(titleKey);
        String subtitle = getRadiationMessage(subtitleKey);

        if (replacements.length >= 2) {
            for (int i = 0; i < replacements.length; i += 2) {
                title = title.replace("<" + replacements[i] + ">", replacements[i + 1]);
                subtitle = subtitle.replace("<" + replacements[i] + ">", replacements[i + 1]);
            }
        }

        sendTitle(player, title, subtitle);
    }

    public Messages getMessages() {
        return configService.getMessages();
    }
}
