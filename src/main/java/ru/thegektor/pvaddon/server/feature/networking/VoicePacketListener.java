package ru.thegektor.pvaddon.server.feature.networking;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import ru.thegektor.pvaddon.server.PVAddonPlugin;
import ru.thegektor.pvaddon.server.feature.storage.AudioStorage;

import java.util.logging.Logger;

public class VoicePacketListener implements PluginMessageListener {
    private final PVAddonPlugin plugin;
    private final AudioStorage storage;
    private final Logger logger;

    public VoicePacketListener(PVAddonPlugin plugin, AudioStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
        this.logger = plugin.getLogger();
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(PVAddonPlugin.VOICE_MESSAGE_CHANNEL)) {
            return;
        }

        logger.info("Received voice message from " + player.getName() + ". Size: " + message.length + " bytes.");
        
        // Save the audio data
        storage.saveVoiceMessage(player.getUniqueId(), message);
        
        // Notify player (temporary feedback)
        player.sendMessage("§a[PV Addon] §fГолосовое сообщение получено и сохранено! (" + message.length + " байт)");
    }
}
