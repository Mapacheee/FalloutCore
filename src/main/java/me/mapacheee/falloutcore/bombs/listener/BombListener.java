package me.mapacheee.falloutcore.bombs.listener;

import com.google.inject.Inject;
import com.thewinterframework.configurate.Container;
import com.thewinterframework.paper.listener.ListenerComponent;
import me.mapacheee.falloutcore.bombs.entity.BombService;
import me.mapacheee.falloutcore.shared.config.Config;
import me.mapacheee.falloutcore.shared.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.slf4j.Logger;

@ListenerComponent
public class BombListener implements Listener {

    private final Logger logger;
    private final BombService bombService;
    private final MessageUtil messageUtil;
    private final Container<Config> configContainer;

    @Inject
    public BombListener(Logger logger, BombService bombService, MessageUtil messageUtil, Container<Config> configContainer) {
        this.logger = logger;
        this.bombService = bombService;
        this.messageUtil = messageUtil;
        this.configContainer = configContainer;
    }

    private Config config() {
        return configContainer.get();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!config().bomb().enabled()) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (item == null || item.getType() != Material.PLAYER_HEAD) {
            return;
        }

        if (!isBombItem(item)) {
            return;
        }

        event.setCancelled(true);

        if (!bombService.canPlayerUseBomb(player)) {
            long cooldown = bombService.getPlayerCooldown(player);
            if (cooldown > 0) {
                messageUtil.sendBombCooldownMessage(player, cooldown);
            } else {
                messageUtil.sendNoPermissionMessage(player);
            }
            return;
        }

        if (confirmBombActivation(player)) {
            if (bombService.activateNuclearBomb(player, player.getLocation())) {
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    player.getInventory().setItemInMainHand(null);
                }

                messageUtil.sendBombActivatedMessage(player);
            } else {
                messageUtil.sendBombActivationFailedMessage(player);
            }
        }
    }

    private boolean isBombItem(ItemStack item) {
        if (!(item.getItemMeta() instanceof SkullMeta meta)) {
            return false;
        }

        net.kyori.adventure.text.Component displayName = meta.displayName();
        if (displayName == null) {
            return false;
        }

        String displayNameText = net.kyori.adventure.text.serializer.
                                 plain.PlainTextComponentSerializer.plainText().serialize(displayName);
        return displayNameText.contains("BOMBA NUCLEAR");
    }

    private boolean confirmBombActivation(Player player) {
        messageUtil.sendBombConfirmationMessage(player);
        return true;
    }
}
