package me.mapacheee.falloutcore.factions.command;

import com.google.inject.Inject;
import com.thewinterframework.command.CommandComponent;
import com.thewinterframework.configurate.Container;
import me.mapacheee.falloutcore.factions.entity.TpaService;
import me.mapacheee.falloutcore.shared.config.Messages;
import me.mapacheee.falloutcore.shared.util.MessageUtil;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.paper.util.sender.Source;
import org.slf4j.Logger;

@CommandComponent
@Command("tpa")
@Permission("falloutcore.faction.tpa")
public final class TpaCommand {

    private final Logger logger;
    private final TpaService tpaService;
    private final MessageUtil messageUtil;
    private final Container<Messages> messagesContainer;

    @Inject
    public TpaCommand(Logger logger, TpaService tpaService, MessageUtil messageUtil, Container<Messages> messagesContainer) {
        this.logger = logger;
        this.tpaService = tpaService;
        this.messageUtil = messageUtil;
        this.messagesContainer = messagesContainer;
    }

    private Messages messages() {
        return messagesContainer.get();
    }

    @Command("tpa <player>")
    @Permission("falloutcore.faction.tpa")
    public void handleTpaRequest(Source sender, @Argument("player") Player target) {
        if (!(sender.source() instanceof Player player)) {
            messageUtil.sendMessage(sender.source(), messages().general().playersOnly());
            return;
        }

        tpaService.sendTpaRequest(player, target);
    }

    @Command("tpaccept")
    @Permission("falloutcore.faction.tpa")
    public void handleTpaAccept(Source sender) {
        if (!(sender.source() instanceof Player player)) {
            messageUtil.sendMessage(sender.source(), messages().general().playersOnly());
            return;
        }

        tpaService.acceptTpaRequest(player);
    }

    @Command("tpdeny")
    @Permission("falloutcore.faction.tpa")
    public void handleTpaDeny(Source sender) {
        if (!(sender.source() instanceof Player player)) {
            messageUtil.sendMessage(sender.source(), messages().general().playersOnly());
            return;
        }

        tpaService.denyTpaRequest(player);
    }
}
