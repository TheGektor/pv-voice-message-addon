package ru.thegektor.pvaddon.server;

import org.bukkit.plugin.java.JavaPlugin;
import ru.thegektor.pvaddon.server.feature.networking.VoicePacketListener;
import ru.thegektor.pvaddon.server.feature.storage.AudioStorage;

import java.io.File;

public class PVAddonPlugin extends JavaPlugin {

    public static final String VOICE_MESSAGE_CHANNEL = "pv-addon:voice_message";
    private AudioStorage audioStorage;

    @Override
    public void onEnable() {
        getLogger().info("PV Voice Message Addon (Server) initializing...");

        // Initialize Storage
        File storageDir = new File(getDataFolder(), "messages");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        this.audioStorage = new AudioStorage(storageDir, getLogger());

        // Register Networking
        getServer().getMessenger().registerIncomingPluginChannel(this, VOICE_MESSAGE_CHANNEL, new VoicePacketListener(this, audioStorage));
        getServer().getMessenger().registerOutgoingPluginChannel(this, VOICE_MESSAGE_CHANNEL);

        getLogger().info("PV Voice Message Addon initialized. Listening on channel: " + VOICE_MESSAGE_CHANNEL);
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        getLogger().info("PV Voice Message Addon disabled.");
    }
}
