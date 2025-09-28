package me.mapacheee.falloutcore.shared.util;

import com.google.inject.Inject;
import com.thewinterframework.service.annotation.Service;
import me.mapacheee.falloutcore.config.Messages;
import me.mapacheee.falloutcore.shared.config.ConfigService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        return switch (key) {
            case "enterRadiation" -> rad.enterRadiation();
            case "exitRadiation" -> rad.exitRadiation();
            case "levelChanged" -> rad.levelChanged();
            case "armorProtection" -> rad.armorProtection();
            case "armorDegrading" -> rad.armorDegrading();
            case "takingDamage" -> rad.takingDamage();
            case "systemStatus" -> rad.systemStatus();
            case "currentLevel" -> rad.currentLevel();
            case "radiationHeight" -> rad.radiationHeight();
            case "playersInRadiation" -> rad.playersInRadiation();
            case "systemEnabled" -> rad.systemEnabled();
            case "systemDisabled" -> rad.systemDisabled();
            case "systemState" -> rad.systemState();
            case "levelSet" -> rad.levelSet();
            case "levelOutOfRange" -> rad.levelOutOfRange();
            case "heightSet" -> rad.heightSet();
            case "heightOutOfRange" -> rad.heightOutOfRange();
            case "specifyPlayerConsole" -> rad.specifyPlayerConsole();
            case "playerStatusHeader" -> rad.playerStatusHeader();
            case "inRadiationStatus" -> rad.inRadiationStatus();
            case "immuneStatus" -> rad.immuneStatus();
            case "armorProtectionStatus" -> rad.armorProtectionStatus();
            case "playerHeightStatus" -> rad.playerHeightStatus();
            case "radiationHeightStatus" -> rad.radiationHeightStatus();
            case "inRadiationYes" -> rad.inRadiationYes();
            case "inRadiationNo" -> rad.inRadiationNo();
            case "immuneTrue" -> rad.immuneTrue();
            case "immuneFalse" -> rad.immuneFalse();
            case "playerImmune" -> rad.playerImmune();
            case "playerNotImmune" -> rad.playerNotImmune();
            case "immunityInstructions" -> rad.immunityInstructions();
            default -> key;
        };
    }

    private String getFactionMessage(String key) {
        Messages.FactionMessages fact = configService.getMessages().faction();
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
            case "nameToolong" -> fact.nameToolong();
            case "aliasToolong" -> fact.aliasToolong();
            case "factionAlreadyExists" -> fact.factionAlreadyExists();
            case "maxFactionsReached" -> fact.maxFactionsReached();
            case "factionFull" -> fact.factionFull();
            case "playerForceJoined" -> fact.playerForceJoined();
            case "forceJoinedNotification" -> fact.forceJoinedNotification();
            case "playerNotInFaction" -> fact.playerNotInFaction();
            case "playerKicked" -> fact.playerKicked();
            case "kickedNotification" -> fact.kickedNotification();
            case "aliasChanged" -> fact.aliasChanged();
            case "noFactionsExist" -> fact.noFactionsExist();
            case "factionListHeader" -> fact.factionListHeader();
            case "factionListItem" -> fact.factionListItem();
            default -> key;
        };
    }

    public String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public Messages getMessages() {
        return configService.getMessages();
    }
}
