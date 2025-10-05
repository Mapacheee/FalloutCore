package me.mapacheee.falloutcore.shared.command;

import com.google.inject.Inject;
import com.thewinterframework.command.CommandComponent;
import com.thewinterframework.service.ReloadServiceManager;
import me.mapacheee.falloutcore.shared.config.ConfigService;
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
    private final ConfigService configService;
    private final MessageUtil messageUtil;
    private final ReloadServiceManager reloadServiceManager;

    @Inject
    public AdminCommand(Logger logger, ConfigService configService, MessageUtil messageUtil, ReloadServiceManager reloadServiceManager) {
        this.logger = logger;
        this.configService = configService;
        this.messageUtil = messageUtil;
        this.reloadServiceManager = reloadServiceManager;
    }

    @Command("reload")
    @Permission("falloutcore.admin.reload")
    public void handleReload(Source sender) {
        long startTime = System.currentTimeMillis();

        try {
            reloadServiceManager.reload();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            if (sender.source() instanceof Player player) {
                messageUtil.sendReloadSuccessMessage(player, duration);
            } else {
                messageUtil.sendMessage(sender.source(), "&aConfig recargada en " + duration + "ms");
            }

            logger.info("Configuraciones recargadas por {} en {}ms", sender.source().getName(), duration);

        } catch (Exception e) {
            logger.error("error al recargar configs", e);

            if (sender.source() instanceof Player player) {
                messageUtil.sendReloadErrorMessage(player);
            } else {
                messageUtil.sendMessage(sender.source(), "&cerror al recargar configs.");
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
                "&6FalloutCore plugin version: &e" + configService.getPluginVersion());
        }
    }
}
