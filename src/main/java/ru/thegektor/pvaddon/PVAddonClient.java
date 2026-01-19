package ru.thegektor.pvaddon;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.thegektor.pvaddon.feature.audio.AudioRecorder;
import ru.thegektor.pvaddon.feature.networking.NetworkManager;

public class PVAddonClient implements ClientModInitializer {
    public static final String MOD_ID = "pv-addon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static AudioRecorder audioRecorder;

    @Override
    public void onInitializeClient() {
        LOGGER.info("PV Voice Message Addon initializing...");

        audioRecorder = new AudioRecorder();
        
        // Initialize Networking
        NetworkManager.register();
    }

    public static AudioRecorder getAudioRecorder() {
        return audioRecorder;
    }
}
