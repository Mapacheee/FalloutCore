package me.mapacheee.falloutcore;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.plugin.java.JavaPlugin;

public final class FalloutCore extends JavaPlugin {

    private static FalloutCore instance;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        instance = this;
        PacketEvents.getAPI().init();

        //RadiationSystem.getInstance().start();
        //FactionManager.getInstance().setup();
        //BombManager.getInstance().setup();

        getLogger().info("FalloutCore enabled!");
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        getLogger().info("FalloutCore disabled!");
    }

    public static FalloutCore getInstance() {
        return instance;
    }
}
