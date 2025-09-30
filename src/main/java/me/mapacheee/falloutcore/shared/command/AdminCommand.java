package me.mapacheee.falloutcore.shared.command;

import com.google.inject.Inject;
import com.thewinterframework.command.CommandComponent;
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

    @Inject
    public AdminCommand(Logger logger, ConfigService configService, MessageUtil messageUtil) {
        this.logger = logger;
        this.configService = configService;
        this.messageUtil = messageUtil;
    }

    @Command("reload")
    @Permission("falloutcore.admin.reload")
    public void handleReload(Source sender) {
        long startTime = System.currentTimeMillis();

        if (configService.reloadConfigurations()) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            if (sender.source() instanceof Player player) {
                messageUtil.sendReloadSuccessMessage(player, duration);
            }
        } else {
            if (sender.source() instanceof Player player) {
                messageUtil.sendReloadErrorMessage(player);
            } else {
                messageUtil.sendMessage(sender.source(),
                    "&cerror reloading configurations. Check console for details.");
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
