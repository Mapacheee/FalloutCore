package me.mapacheee.falloutcore.utils;

import me.mapacheee.falloutcore.FalloutCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

public class MessageUtils {

    private static FileConfiguration messagesConfig;
    private static File messagesFile;

    public static void loadMessages() {
        messagesFile = new File(FalloutCore.getInstance().getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            try {
                messagesFile.getParentFile().mkdirs();
                messagesFile.createNewFile();

                FalloutCore.getInstance().saveResource("messages.yml", false);
            } catch (IOException e) {
                FalloutCore.getInstance().getLogger().log(Level.SEVERE, "No se pudo crear messages.yml", e);
            }
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public static String getMessage(String path) {
        return getMessage(path, null);
    }

    public static String getMessage(String path, Player player) {
        return getMessage(path, player, null);
    }

    public static String getMessage(String path, Player player, Map<String, String> replacements) {
        if (messagesConfig == null) {
            reloadMessages();
        }

        String message = messagesConfig.getString(path);

        if (message == null) {
            FalloutCore.getInstance().getLogger().warning("Mensaje no encontrado: " + path);
            return "&c[MENSAJE NO ENCONTRADO: " + path + "]";
        }

        if (replacements != null) {
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                message = message.replace(entry.getKey(), entry.getValue());
            }
        }

        if (player != null) {
            message = message
                    .replace("{player}", player.getName())
                    .replace("{displayname}", player.getDisplayName());
        }

        return message.replace('&', '§');
    }

    public static void reloadMessages() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public static void saveMessages() {
        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            FalloutCore.getInstance().getLogger().log(Level.SEVERE, "No se pudo guardar messages.yml", e);
        }
    }
}