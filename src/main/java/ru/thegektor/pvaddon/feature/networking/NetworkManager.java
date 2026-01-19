package ru.thegektor.pvaddon.feature.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import ru.thegektor.pvaddon.PVAddonClient;

public class NetworkManager {
    public static final Identifier VOICE_PACKET_ID = Identifier.of(PVAddonClient.MOD_ID, "voice_message");

    public record VoicePayload(byte[] data) implements CustomPayload {
        public static final CustomPayload.Id<VoicePayload> ID = new CustomPayload.Id<>(VOICE_PACKET_ID);
        
        // Manual codec implementation to avoid lambda ambiguity
        public static final PacketCodec<PacketByteBuf, VoicePayload> CODEC = new PacketCodec<>() {
            public void encode(PacketByteBuf buf, VoicePayload payload) {
                buf.writeByteArray(payload.data);
            }

            public VoicePayload decode(PacketByteBuf buf) {
                return new VoicePayload(buf.readByteArray());
            }
        };

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public static void register() {
         PayloadTypeRegistry.playC2S().register(VoicePayload.ID, VoicePayload.CODEC);
    }

    public static void sendVoiceMessage(byte[] audioData) {
        if (audioData == null || audioData.length == 0) return;
        try {
            ClientPlayNetworking.send(new VoicePayload(audioData));
            PVAddonClient.LOGGER.info("Sent voice packet. Size: " + audioData.length + " bytes.");
        } catch (Exception e) {
            PVAddonClient.LOGGER.error("Failed to send voice packet", e);
        }
    }
}
