package me.mapacheee.falloutcore.factions.entity;

import com.google.inject.Inject;
import com.thewinterframework.service.annotation.Service;
import me.mapacheee.falloutcore.shared.util.MessageUtil;
import me.mapacheee.falloutcore.shared.effects.EffectsService;
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
    private final EffectsService effectsService;

    private final Map<UUID, TpaRequest> pendingRequests = new ConcurrentHashMap<>();
    private static final long DEFAULT_EXPIRATION_MINUTES = 2;

    @Inject
    public TpaService(Logger logger, FactionService factionService, MessageUtil messageUtil,
                      EffectsService effectsService) {
        this.logger = logger;
        this.factionService = factionService;
        this.messageUtil = messageUtil;
        this.effectsService = effectsService;

        Bukkit.getScheduler().runTaskTimer(
            Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("FalloutCore")),
            this::cleanupExpiredRequests,
            20L * 30L,
            20L * 30L
        );
    }

    public void sendTpaRequest(Player sender, Player target) {
        if (sender.getUniqueId().equals(target.getUniqueId())) {
            messageUtil.sendTpaSelfRequestMessage(sender);
            return;
        }

        if (!factionService.isSameFaction(sender, target)) {
            messageUtil.sendTpaNotSameFactionMessage(sender);
            return;
        }

        if (!target.isOnline()) {
            messageUtil.sendTpaPlayerOfflineMessage(sender, target.getName());
            return;
        }

        TpaRequest existingRequest = pendingRequests.get(target.getUniqueId());
        if (existingRequest != null && existingRequest.getSenderId().equals(sender.getUniqueId())) {
            messageUtil.sendTpaAlreadyHasRequestMessage(sender, target.getName());
            return;
        }

        TpaRequest request = new TpaRequest(sender, target, DEFAULT_EXPIRATION_MINUTES);
        pendingRequests.put(target.getUniqueId(), request);

        messageUtil.sendTpaRequestSentMessage(sender, target.getName());
        messageUtil.sendTpaRequestReceivedMessage(target, sender.getName());

        Bukkit.getScheduler().runTaskLater(
            Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("FalloutCore")),
            () -> expireRequest(target.getUniqueId()),
            20L * 60L * DEFAULT_EXPIRATION_MINUTES
        );

    }

    public void acceptTpaRequest(Player target) {
        Player sender = validateAndRemoveTpaRequest(target);

        if (sender == null || !sender.isOnline()) {
            messageUtil.sendTpaPlayerOfflineMessage(target, "el remitente");
            return;
        }

        if (!factionService.isSameFaction(sender, target)) {
            messageUtil.sendTpaNotSameFactionMessage(target);
            return;
        }

        effectsService.startTpaAnimation(sender);

        Bukkit.getScheduler().runTaskLater(
            Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("FalloutCore")),
            () -> {
                if (sender.isOnline()) {
                    sender.teleport(target.getLocation());
                    effectsService.playTeleportEffect(sender);
                    effectsService.playTeleportEffect(target);
                }
            },
            60L
        );

        messageUtil.sendTpaRequestAcceptedMessage(target, sender.getName());
        messageUtil.sendTpaRequestAcceptedSenderMessage(sender, target.getName());
    }

    public void denyTpaRequest(Player target) {

        Player sender = validateAndRemoveTpaRequest(target);
        if (sender == null) return;

        messageUtil.sendTpaRequestDeniedMessage(target, sender.getName());
        if (sender.isOnline()) {
            messageUtil.sendTpaRequestDeniedSenderMessage(sender, target.getName());
        }

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
            notifySenderRequestExpired(request, targetId);
        }
    }

    private void cleanupExpiredRequests() {
        pendingRequests.entrySet().removeIf(entry -> {
            boolean expired = entry.getValue().isExpired();
            if (expired) {
                notifySenderRequestExpired(entry.getValue(), entry.getKey());
            }
            return expired;
        });
    }

    private void notifySenderRequestExpired(TpaRequest request, UUID targetId) {
        Player sender = Bukkit.getPlayer(request.getSenderId());
        if (sender != null && sender.isOnline()) {
            Player target = Bukkit.getPlayer(targetId);
            String targetName = target != null ? target.getName() : "Jugador desconocido";
            messageUtil.sendTpaRequestExpiredMessage(sender, targetName);
        }
    }

    private Player validateAndRemoveTpaRequest(Player target) {
        TpaRequest request = pendingRequests.remove(target.getUniqueId());
        if (request == null || request.isExpired()) {
            messageUtil.sendTpaNoRequestsMessage(target);
            return null;
        }
        return Bukkit.getPlayer(request.getSenderId());
    }

    public int getPendingRequestsCount() {
        return pendingRequests.size();
    }
}
