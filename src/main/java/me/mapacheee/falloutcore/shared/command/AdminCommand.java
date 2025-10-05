package me.mapacheee.falloutcore.shared.command;

import com.google.inject.Inject;
import com.thewinterframework.command.CommandComponent;
import com.thewinterframework.configurate.Container;
import me.mapacheee.falloutcore.shared.config.Config;
import me.mapacheee.falloutcore.shared.config.Messages;
import me.mapacheee.falloutcore.shared.util.MessageUtil;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.paper.util.sender.Source;
import org.slf4j.Logger;

@CommandComponent
@Command("falloutcore|fc")
@Permission("falloutcore.admin")
public final class AdminCommand {

    private final Logger logger;
    private final MessageUtil messageUtil;
    private final Container<Config> configContainer;
    private final Container<Messages> messagesContainer;

    @Inject
    public AdminCommand(Logger logger, MessageUtil messageUtil,
                       Container<Config> configContainer, Container<Messages> messagesContainer) {
        this.logger = logger;
        this.messageUtil = messageUtil;
        this.configContainer = configContainer;
        this.messagesContainer = messagesContainer;
    }

    @Command("reload")
    @Permission("falloutcore.admin.reload")
    public void handleReload(Source sender) {
        long startTime = System.currentTimeMillis();

        try {
            // Recargar configuraciones usando los containers
            configContainer.reload();
            messagesContainer.reload();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            if (sender.source() instanceof Player player) {
                messageUtil.sendReloadSuccessMessage(player, duration);
            } else {
                messageUtil.sendMessage(sender.source(), "&aConfiguración recargada correctamente en " + duration + "ms");
            }

            logger.info("Configuraciones recargadas por {} en {}ms", sender.source().getName(), duration);

        } catch (Exception e) {
            logger.error("Error al recargar configuraciones", e);

            if (sender.source() instanceof Player player) {
                messageUtil.sendReloadErrorMessage(player);
            } else {
                messageUtil.sendMessage(sender.source(), "&cError al recargar las configuraciones. Revisa la consola para más detalles.");
            }
        }
    }

    @Command("version")
    @Permission("falloutcore.admin")
    public void handleVersion(Source sender) {
        if (sender.source() instanceof Player player) {
            messageUtil.sendVersionMessage(player);
        } else {
            messageUtil.sendMessage(sender.source(),
                "&6FalloutCore plugin version: &e1.0-SNAPSHOT");
        }
    }
}
