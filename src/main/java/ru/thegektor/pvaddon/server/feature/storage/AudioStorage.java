package ru.thegektor.pvaddon.server.feature.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

public class AudioStorage {
    private final File dataFolder;
    private final Logger logger;

    public AudioStorage(File dataFolder, Logger logger) {
        this.dataFolder = dataFolder;
        this.logger = logger;
    }

    public void saveVoiceMessage(UUID sender, byte[] data) {
        String fileName = sender.toString() + "_" + System.currentTimeMillis() + ".pcm";
        File file = new File(dataFolder, fileName);
        
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
            logger.info("Saved voice message from " + sender + " to " + file.getPath() + " (" + data.length + " bytes)");
        } catch (IOException e) {
            logger.severe("Failed to save voice message from " + sender + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
