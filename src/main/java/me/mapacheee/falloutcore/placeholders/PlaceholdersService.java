package me.mapacheee.falloutcore.placeholders;

import com.thewinterframework.service.annotation.Service;
import com.thewinterframework.service.annotation.lifecycle.OnDisable;
import com.thewinterframework.service.annotation.lifecycle.OnEnable;

@Service
public class PlaceholdersService {

    private FactionPlaceholder factionExpansion;
    private RadiationPlaceholder radiationExpansion;

    @OnEnable
    void registerExpansions() {
        factionExpansion = new FactionPlaceholder();
        factionExpansion.register();

        radiationExpansion = new RadiationPlaceholder();
        radiationExpansion.register();
    }

    @OnDisable
    void unregisterExpansions() {
    }
}
