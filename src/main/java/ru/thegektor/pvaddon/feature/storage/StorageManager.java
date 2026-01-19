package ru.thegektor.pvaddon.feature.storage;

import java.io.IOException;
import java.util.UUID;
import java.io.InputStream;

public interface StorageManager {
    void saveVoiceMessage(UUID messageId, UUID senderUuid, byte[] data, long duration) throws IOException;
    byte[] getVoiceMessage(UUID messageId) throws IOException;
    void logMessageMetadata(UUID messageId, UUID senderUuid, long duration);
}
