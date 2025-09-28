package me.mapacheee.falloutcore;

import com.github.retrooper.packetevents.PacketEvents;
import com.thewinterframework.paper.PaperWinterPlugin;
import com.thewinterframework.plugin.WinterBootPlugin;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;

@WinterBootPlugin
public final class FalloutWinterPlugin extends PaperWinterPlugin {

    public static <T> T getService(Class<T> type) {
        return FalloutWinterPlugin.getPlugin(FalloutWinterPlugin.class)
                .injector
                .getInstance(type);
    }

    @Override
    public void onPluginLoad() {
        super.onPluginLoad();
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onPluginDisable() {
        super.onPluginDisable();
        PacketEvents.getAPI().terminate();
    }
}

