package me.mapacheee.falloutcore.placeholders;

import com.google.inject.Inject;
import com.thewinterframework.service.annotation.Service;
import com.thewinterframework.service.annotation.lifecycle.OnEnable;

@Service
public class PlaceholdersService {

    @Inject
    private RadiationPlaceholderProvider radiationProvider;

    @Inject
    private FactionPlaceholderProvider factionProvider;

    @OnEnable
    void registerPlaceholders() {
        radiationProvider.register();
        factionProvider.register();
    }
}
