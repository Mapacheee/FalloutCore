package me.mapacheee.falloutcore.factions.entity;

import com.google.inject.Inject;
import com.thewinterframework.service.annotation.Service;
import me.mapacheee.falloutcore.shared.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TpaService {
    private final Logger logger;
    private final FactionService factionService;
    private final MessageUtil messageUtil;

    private final Map<UUID, TpaRequest> pendingRequests = new ConcurrentHashMap<>();
    private static final long DEFAULT_EXPIRATION_MINUTES = 2; // 2 minutos para expirar

    @Inject
    public TpaService(Logger logger, FactionService factionService, MessageUtil messageUtil) {
        this.logger = logger;
        this.factionService = factionService;
        this.messageUtil = messageUtil;

        Bukkit.getScheduler().runTaskTimer(
            Bukkit.getPluginManager().getPlugin("FalloutCore"),
            this::cleanupExpiredRequests,
            20L * 30L,
            20L * 30L
        );
    }

    public boolean sendTpaRequest(Player sender, Player target) {
        if (sender.getUniqueId().equals(target.getUniqueId())) {
            messageUtil.sendTpaSelfRequestMessage(sender);
            return false;
        }

        if (!factionService.isSameFaction(sender, target)) {
            messageUtil.sendTpaNotSameFactionMessage(sender);
            return false;
        }

        if (!target.isOnline()) {
            messageUtil.sendTpaPlayerOfflineMessage(sender, target.getName());
            return false;
        }

        TpaRequest existingRequest = pendingRequests.get(target.getUniqueId());
        if (existingRequest != null && existingRequest.getSenderId().equals(sender.getUniqueId())) {
            messageUtil.sendTpaAlreadyHasRequestMessage(sender, target.getName());
            return false;
        }

        TpaRequest request = new TpaRequest(sender, target, DEFAULT_EXPIRATION_MINUTES);
        pendingRequests.put(target.getUniqueId(), request);

        messageUtil.sendTpaRequestSentMessage(sender, target.getName());
        messageUtil.sendTpaRequestReceivedMessage(target, sender.getName());

        logger.info("Player '{}' sent TPA request to '{}'", sender.getName(), target.getName());

        Bukkit.getScheduler().runTaskLater(
            Bukkit.getPluginManager().getPlugin("FalloutCore"),
            () -> expireRequest(target.getUniqueId()),
            20L * 60L * DEFAULT_EXPIRATION_MINUTES // 2 minutos
        );

        return true;
    }

    public boolean acceptTpaRequest(Player target) {
        TpaRequest request = pendingRequests.remove(target.getUniqueId());
        if (request == null) {
            messageUtil.sendTpaNoRequestsMessage(target);
            return false;
        }

        if (request.isExpired()) {
            messageUtil.sendTpaNoRequestsMessage(target);
            return false;
        }

        Player sender = Bukkit.getPlayer(request.getSenderId());
        if (sender == null || !sender.isOnline()) {
            messageUtil.sendTpaPlayerOfflineMessage(target, "el remitente");
            return false;
        }

        if (!factionService.isSameFaction(sender, target)) {
            messageUtil.sendTpaNotSameFactionMessage(target);
            return false;
        }

        sender.teleport(target.getLocation());

        messageUtil.sendTpaRequestAcceptedMessage(target, sender.getName());
        messageUtil.sendTpaRequestAcceptedSenderMessage(sender, target.getName());

        logger.info("Player '{}' accepted TPA request from '{}'", target.getName(), sender.getName());
        return true;
    }

    public boolean denyTpaRequest(Player target) {
        TpaRequest request = pendingRequests.remove(target.getUniqueId());
        if (request == null) {
            messageUtil.sendTpaNoRequestsMessage(target);
            return false;
        }

        if (request.isExpired()) {
            messageUtil.sendTpaNoRequestsMessage(target);
            return false;
        }

        Player sender = Bukkit.getPlayer(request.getSenderId());

        messageUtil.sendTpaRequestDeniedMessage(target, sender != null ? sender.getName() : "Jugador desconocido");
        if (sender != null && sender.isOnline()) {
            messageUtil.sendTpaRequestDeniedSenderMessage(sender, target.getName());
        }

        logger.info("Player '{}' denied TPA request from '{}'", target.getName(),
                   sender != null ? sender.getName() : request.getSenderId());
        return true;
    }

    public boolean hasPendingRequest(Player player) {
        TpaRequest request = pendingRequests.get(player.getUniqueId());
        return request != null && !request.isExpired();
    }

    public void cancelRequestsForPlayer(Player player) {
        pendingRequests.remove(player.getUniqueId());

        pendingRequests.entrySet().removeIf(entry ->
            entry.getValue().getSenderId().equals(player.getUniqueId()));
    }

    private void expireRequest(UUID targetId) {
        TpaRequest request = pendingRequests.remove(targetId);
        if (request != null) {
            Player sender = Bukkit.getPlayer(request.getSenderId());
            if (sender != null && sender.isOnline()) {
                Player target = Bukkit.getPlayer(targetId);
                String targetName = target != null ? target.getName() : "Jugador desconocido";
                messageUtil.sendTpaRequestExpiredMessage(sender, targetName);
            }
        }
    }

    private void cleanupExpiredRequests() {
        pendingRequests.entrySet().removeIf(entry -> {
            boolean expired = entry.getValue().isExpired();
            if (expired) {
                logger.debug("Cleaned up expired TPA request from {} to {}",
                           entry.getValue().getSenderId(), entry.getKey());
            }
            return expired;
        });
    }

    public int getPendingRequestsCount() {
        return pendingRequests.size();
    }
}
