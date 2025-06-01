package me.mapacheee.falloutcore.factions;

import me.mapacheee.falloutcore.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionCommand implements CommandExecutor {
    private final FactionManager factionManager = FactionManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create":
                return createFaction(sender, args);

            case "delete":
                return deleteFaction(sender, args);

            case "join":
                return joinFaction(sender, args);

            case "forcejoin":
                return forceJoinFaction(sender, args);

            case "kick":
                return kickPlayer(sender, args);

            case "setalias":
                return setFactionAlias(sender, args);

            default:
                sendHelp(sender);
                return true;
        }
    }

    private boolean createFaction(CommandSender sender, String[] args) {
        if (!sender.hasPermission("falloutcore.factions.admin")) {
            sender.sendMessage(MessageUtils.getMessage("factions.no-permission"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(MessageUtils.getMessage("factions.usage.create"));
            return true;
        }

        String name = args[1];
        String alias = args[2];

        if (factionManager.createFaction(name, alias)) {
            sender.sendMessage(MessageUtils.getMessage("factions.created")
                    .replace("{faction}", name)
                    .replace("{alias}", alias));
        } else {
            sender.sendMessage(MessageUtils.getMessage("factions.faction-exists"));
        }
        return true;
    }

    private boolean deleteFaction(CommandSender sender, String[] args) {
        if (!sender.hasPermission("falloutcore.factions.admin")) {
            sender.sendMessage(MessageUtils.getMessage("factions.no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(MessageUtils.getMessage("factions.usage.delete"));
            return true;
        }

        String name = args[1];
        if (factionManager.deleteFaction(name)) {
            sender.sendMessage(MessageUtils.getMessage("factions.deleted")
                    .replace("{faction}", name));
        } else {
            sender.sendMessage(MessageUtils.getMessage("factions.faction-not-found"));
        }
        return true;
    }

    private boolean setFactionAlias(CommandSender sender, String[] args) {
        if (!sender.hasPermission("falloutcore.factions.admin")) {
            sender.sendMessage(MessageUtils.getMessage("factions.no-permission"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(MessageUtils.getMessage("factions.usage.setalias"));
            return true;
        }

        String name = args[1];
        String newAlias = args[2];

        if (factionManager.setFactionAlias(name, newAlias)) {
            sender.sendMessage(MessageUtils.getMessage("factions.created")
                    .replace("{faction}", name)
                    .replace("{alias}", newAlias));
        } else {
            sender.sendMessage(MessageUtils.getMessage("factions.faction-not-found"));
        }
        return true;
    }

    private boolean joinFaction(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.getMessage("factions.player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            sender.sendMessage(MessageUtils.getMessage("factions.usage.join"));
            return true;
        }

        String factionName = args[1];
        if (factionManager.joinFaction(player, factionName)) {
            player.sendMessage(MessageUtils.getMessage("factions.joined")
                    .replace("{faction}", factionName));
        } else if (factionManager.getPlayerFaction(player) != null) {
            player.sendMessage(MessageUtils.getMessage("factions.already-in-faction"));
        } else {
            player.sendMessage(MessageUtils.getMessage("factions.faction-not-found"));
        }
        return true;
    }

    private boolean forceJoinFaction(CommandSender sender, String[] args) {
        if (!sender.hasPermission("falloutcore.factions.admin")) {
            sender.sendMessage(MessageUtils.getMessage("factions.no-permission"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(MessageUtils.getMessage("factions.usage.forcejoin"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(MessageUtils.getMessage("factions.player-not-found"));
            return true;
        }

        String factionName = args[2];
        if (factionManager.forceJoinFaction(target, factionName)) {
            sender.sendMessage(MessageUtils.getMessage("factions.forced-join")
                    .replace("{player}", target.getName())
                    .replace("{faction}", factionName));
            target.sendMessage(MessageUtils.getMessage("factions.joined")
                    .replace("{faction}", factionName));
        } else {
            sender.sendMessage(MessageUtils.getMessage("factions.faction-not-found"));
        }
        return true;
    }

    private boolean kickPlayer(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(MessageUtils.getMessage("factions.usage.kick"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(MessageUtils.getMessage("factions.player-not-found"));
            return true;
        }

        // Si es un jugador normal, solo puede kickearse a sí mismo
        if (sender instanceof Player && !sender.hasPermission("falloutcore.factions.admin")) {
            Player player = (Player) sender;
            if (!player.equals(target)) {
                player.sendMessage(MessageUtils.getMessage("factions.no-permission"));
                return true;
            }
        }

        if (factionManager.kickPlayer(target)) {
            sender.sendMessage(MessageUtils.getMessage("factions.kicked")
                    .replace("{player}", target.getName()));
            target.sendMessage(MessageUtils.getMessage("factions.player-kicked")
                    .replace("{faction}", factionManager.getPlayerFaction(target).getName()));
        } else {
            sender.sendMessage(MessageUtils.getMessage("factions.not-in-faction"));
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(MessageUtils.getMessage("factions.help"));
        sender.sendMessage("§6/faction create <nombre> <alias> - Crear nueva facción");
        sender.sendMessage("§6/faction delete <nombre> - Eliminar facción");
        sender.sendMessage("§6/faction setalias <faction> <alias> - Cambiar alias de facción");
        sender.sendMessage("§6/faction join <nombre> - Unirse a facción");
        sender.sendMessage("§6/faction forcejoin <jugador> <faction> - Forzar unión a facción");
        sender.sendMessage("§6/faction kick <jugador> - Expulsar jugador de facción");
    }
}