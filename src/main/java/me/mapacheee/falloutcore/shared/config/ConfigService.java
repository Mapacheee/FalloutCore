package me.mapacheee.falloutcore.shared.config;

import com.google.inject.Inject;
import com.thewinterframework.configurate.Container;
import com.thewinterframework.service.annotation.Service;
import org.slf4j.Logger;

@Service
public class ConfigService {

    private final Logger logger;
    private final Container<Config> configContainer;
    private final Container<Messages> messagesContainer;

    @Inject
    public ConfigService(Logger logger, Container<Config> configContainer, Container<Messages> messagesContainer) {
        this.logger = logger;
        this.configContainer = configContainer;
        this.messagesContainer = messagesContainer;
    }

    public Config getConfig() {
        return configContainer.get();
    }

    public Messages getMessages() {
        return messagesContainer.get();
    }

    public String getPluginVersion() {
        return "1.0";
    }
}
