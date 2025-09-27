package me.mapacheee.falloutcore.shared.util;

import com.google.inject.Inject;
import com.thewinterframework.configurate.Container;
import com.thewinterframework.service.annotation.Service;
import me.mapacheee.falloutcore.config.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Service
public class MessageUtil {

    private final Container<Messages> messages;

    @Inject
    public MessageUtil(Container<Messages> messages) {
        this.messages = messages;
    }

    public void sendMessage(CommandSender sender, String message) {
        String prefixed = messages.get().general().prefix() + " " + message;
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
        Messages.RadiationMessages rad = messages.get().radiation();
        return switch (key) {
            case "enterRadiation" -> rad.enterRadiation();
            case "exitRadiation" -> rad.exitRadiation();
            case "levelChanged" -> rad.levelChanged();
            case "armorProtection" -> rad.armorProtection();
            case "armorDegrading" -> rad.armorDegrading();
            case "takingDamage" -> rad.takingDamage();
            default -> key;
        };
    }

    private String getFactionMessage(String key) {
        Messages.FactionMessages fact = messages.get().faction();
        return switch (key) {
            case "factionCreated" -> fact.factionCreated();
            case "factionDeleted" -> fact.factionDeleted();
            case "playerJoined" -> fact.playerJoined();
            case "playerLeft" -> fact.playerLeft();
            case "factionNotFound" -> fact.factionNotFound();
            case "alreadyInFaction" -> fact.alreadyInFaction();
            case "notInFaction" -> fact.notInFaction();
            case "noPermission" -> fact.noPermission();
            case "factionInfo" -> fact.factionInfo();
            case "factionList" -> fact.factionList();
            case "nexusDestroyed" -> fact.nexusDestroyed();
            case "pointsAwarded" -> fact.pointsAwarded();
            case "friendlyFire" -> fact.friendlyFire();
            default -> key;
        };
    }

    public String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public Messages getMessages() {
        return messages.get();
    }

    public void reload() {
        messages.reload();
    }
}
