package me.mapacheee.falloutcore.shared.config;

import com.google.inject.Inject;
import com.thewinterframework.service.annotation.Service;
import me.mapacheee.falloutcore.config.Messages;
import org.slf4j.Logger;

@Service
public class ConfigService {

    private final Logger logger;
    private final Config config;
    private final Messages messages;

    @Inject
    public ConfigService(Logger logger, Config config, Messages messages) {
        this.logger = logger;
        this.config = config;
        this.messages = messages;
    }

    public Config getConfig() {
        return config;
    }

    public Messages getMessages() {
        return messages;
    }

    public String getPluginVersion() {
        return "1.0";
    }
}
