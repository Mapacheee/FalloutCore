package me.mapacheee.falloutcore;

import com.thewinterframework.paper.PaperWinterPlugin;
import com.thewinterframework.plugin.WinterBootPlugin;

@WinterBootPlugin
public final class FalloutWinterPlugin extends PaperWinterPlugin {

    public static <T> T getService(Class<T> type) {
        return FalloutWinterPlugin.getPlugin(FalloutWinterPlugin.class)
                .injector
                .getInstance(type);
    }
}

